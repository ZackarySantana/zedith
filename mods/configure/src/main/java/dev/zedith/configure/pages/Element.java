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
                    ConfigEvent.pageEvent(ConfigEventType.RESET_VALUE, Integer.toString(index))
            );
        }

        public void setValue(BsonValue value) {
            ConfigEventType eventType;
            Field field;
            switch (value.getBsonType()) {
                case STRING:
                    eventType = ConfigEventType.STR_CONFIG_VALUE_CHANGE_EVENT;
                    field = new Field(cmds, events, "%s #ConfigStringValue".formatted(selector()));
                    field.setValue(value.asString().getValue());
                    field.setVisible(true);
                    break;
                case INT32:
                    eventType = ConfigEventType.INT_CONFIG_VALUE_CHANGE_EVENT;
                    field = new Field(cmds, events, "%s #ConfigIntValue".formatted(selector()));
                    field.setValue(value.asInt32().getValue());
                    field.setVisible(true);
                    break;
                case BOOLEAN:
                    eventType = ConfigEventType.BOOL_CONFIG_VALUE_CHANGE_EVENT;
                    field = new Field(cmds, events, "%s #ConfigCheckbox".formatted(selector()));
                    field.setValue(value.asBoolean().getValue());
                    field.setVisible(true, "%s #ConfigCheckboxContainer".formatted(selector()));
                    break;
                default:
                    // TODO: Replace this with the hytale logger.
                    System.out.println("Unhandled BSON type: " + value.getBsonType().name());
                    return;
            }

            field.registerEvent(
                    CustomUIEventBindingType.ValueChanged,
                    ConfigEvent.valueEvent(eventType, index, "%s.Value".formatted(field.selector()))
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

    private record Field(UICommandBuilder cmds, UIEventBuilder events, String selector) {

        public void setValue(String value) {
            cmds.set("%s.Value".formatted(selector()), value);
        }

        public void setValue(int value) {
            cmds.set("%s.Value".formatted(selector()), Integer.toString(value));
        }

        public void setValue(boolean value) {
            cmds.set("%s.Value".formatted(selector()), value);
        }

        public void setVisible(boolean value) {
            cmds.set("%s.Visible".formatted(selector()), value);
        }

        public void setVisible(boolean value, String overrideSelector) {
            cmds.set("%s.Visible".formatted(overrideSelector), value);
        }

        public void registerEvent(CustomUIEventBindingType eventType, EventData eventData) {
            events.addEventBinding(eventType, selector, eventData);
        }
    }
}
