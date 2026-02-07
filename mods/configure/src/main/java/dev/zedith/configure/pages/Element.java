package dev.zedith.configure.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import dev.zedith.configure.data.ConfigEvent;
import dev.zedith.configure.data.ConfigEventType;
import org.bson.BsonValue;

class Element {

    public static class TotalItems {
        private final static String selector = "#TotalItems";

        public static void setText(
                UICommandBuilder cmds, int startOfPage, int endOfPage, int totalItems, int currentPage, int finalPage
        ) {
            cmds.set(
                    "%s.Text".formatted(selector),
                    "Showing %d-%d of %d (Page %d of %d)".formatted(
                            startOfPage, endOfPage, totalItems, currentPage, finalPage
                    )
            );
        }
    }

    public static class PreviousPageButton {
        private final static String selector = "#PreviousPageButton";

        public static void setDisabled(UICommandBuilder cmds, boolean disabled) {
            cmds.set("%s.Disabled".formatted(selector), disabled);
        }
    }

    public static class NextPageButton {
        private final static String selector = "#NextPageButton";

        public static void setDisabled(UICommandBuilder cmds, boolean disabled) {
            cmds.set("%s.Disabled".formatted(selector), disabled);
        }
    }

    public static class ListItems {
        private final static String selector = "#ListItems";

        private final static String configItemPath = "Components/ConfigItem.ui";
        private final static String configItemWithoutDocsPath = "Components/ConfigItemWithoutDocs.ui";
        private final static String modItemPath = "Components/ModItem.ui";

        public static void clear(UICommandBuilder cmds) {
            cmds.clear(selector);
        }

        public static ConfigItem newConfigItem(UICommandBuilder cmds, UIEventBuilder events, int i, String docs) {
            ConfigItem item = new ConfigItem(cmds, events, i);
            if (docs != null && !docs.isEmpty()) {
                cmds.append(selector, configItemPath);
                item.setTooltip(docs);
            } else {
                cmds.append(selector, configItemWithoutDocsPath);
            }
            return item;
        }

        public static ConfigItem getConfigItem(UICommandBuilder cmds, UIEventBuilder events, int i) {
            return new ConfigItem(cmds, events, i);
        }

        public static ModItem newModItem(UICommandBuilder cmds, UIEventBuilder events, int i) {
            cmds.append(selector, modItemPath);
            return new ModItem(cmds, events, i);
        }
    }


    public record ConfigItem(UICommandBuilder cmds, UIEventBuilder events, int index) {
        private String selector() {
            return "#ListItems[%d]".formatted(index);
        }

        public void setTooltip(String text) {
            cmds.set("%s.TooltipText".formatted(selector()), text);
        }

        public void setConfigKey(String text) {
            cmds.set("%s #ConfigKey.Text".formatted(selector()), text + ":");
        }

        public void setResetButton() {
            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "%s #Reset".formatted(selector()),
                    ConfigEvent.valueEvent(ConfigEventType.RESET_VALUE, index, Integer.toString(index))
            );
        }

        private Field getField(BsonValue value) {
            return switch (value.getBsonType()) {
                case STRING -> new StringField(cmds, events, selector());
                case INT32 -> new IntField(cmds, events, selector());
                case BOOLEAN -> new BooleanField(cmds, events, selector());
                default -> {
                    // TODO: Replace this with the hytale logger.
                    System.out.println("Unhandled BSON type: " + value.getBsonType().name());
                    yield null;
                }
            };
        }

        public void setValue(BsonValue value) {
            Field field = getField(value);
            if (field == null) {
                // TODO: Maybe return error?
                return;
            }
            field.setValue(value);
            field.setVisible(true);
        }

        public void registerValueChangedEvent(BsonValue value) {
            Field field = getField(value);
            if (field == null) {
                // TODO: Maybe return error?
                return;
            }

            field.registerEvent(
                    CustomUIEventBindingType.ValueChanged,
                    ConfigEvent.valueEvent(field.eventType, index, "%s.Value".formatted(field.selector()))
            );
        }
    }

    public record ModItem(UICommandBuilder cmds, UIEventBuilder events, int index) {
        private String selector() {
            return "#ListItems[%d]".formatted(index);
        }

        public void setName(String text) {
            cmds.set("%s #Name.Text".formatted(selector()), text);
        }

        public void setOpenButton() {
            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "%s #Open".formatted(selector()),
                    ConfigEvent.pageEvent(ConfigEventType.OPEN_MOD_PAGE, Integer.toString(index))
            );
        }
    }

    private static final class StringField extends Field {

        private StringField(UICommandBuilder cmds, UIEventBuilder events, String parentSelector) {
            super(cmds, events, parentSelector, ConfigEventType.STR_CONFIG_VALUE_CHANGE_EVENT);
        }

        @Override
        public void setValue(BsonValue value) {
            cmds.set("%s.Value".formatted(selector()), value.asString().getValue());
        }

        @Override
        public String selector() {
            return "%s #ConfigStringValue".formatted(parentSelector);
        }
    }

    private static final class IntField extends Field {

        private IntField(UICommandBuilder cmds, UIEventBuilder events, String parentSelector) {
            super(cmds, events, parentSelector, ConfigEventType.INT_CONFIG_VALUE_CHANGE_EVENT);
        }

        @Override
        public void setValue(BsonValue value) {
            cmds.set("%s.Value".formatted(selector()), Integer.toString(value.asInt32().getValue()));
        }

        @Override
        public String selector() {
            return "%s #ConfigIntValue".formatted(parentSelector);
        }
    }

    private static final class BooleanField extends Field {

        private BooleanField(UICommandBuilder cmds, UIEventBuilder events, String parentSelector) {
            super(cmds, events, parentSelector, ConfigEventType.BOOL_CONFIG_VALUE_CHANGE_EVENT);
        }

        @Override
        public void setValue(BsonValue value) {
            cmds.set("%s.Value".formatted(selector()), value.asBoolean().getValue());
        }

        @Override
        public String selector() {
            return "%s #ConfigCheckbox".formatted(parentSelector);
        }

        @Override
        public void setVisible(boolean value) {
            cmds.set("%s #ConfigCheckboxContainer.Visible".formatted(parentSelector), value);
        }
    }

    private abstract static class Field {
        final UICommandBuilder cmds;
        final UIEventBuilder events;
        final ConfigEventType eventType;
        final String parentSelector;

        private Field(UICommandBuilder cmds, UIEventBuilder events, String parentSelector, ConfigEventType eventType) {
            this.cmds = cmds;
            this.events = events;
            this.parentSelector = parentSelector;
            this.eventType = eventType;
        }

        public void setValue(BsonValue value) {
            throw new RuntimeException("setValue should not be called directly.");
        }

        public void setVisible(boolean value) {
            cmds.set("%s.Visible".formatted(selector()), value);
        }

        public void registerEvent(CustomUIEventBindingType eventType, EventData eventData) {
            events.addEventBinding(eventType, selector(), eventData);
        }

        public abstract String selector();
    }
}
