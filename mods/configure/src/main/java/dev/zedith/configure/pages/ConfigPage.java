package dev.zedith.configure.pages;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.data.ConfigEvent;
import dev.zedith.configure.data.ConfigEventType;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class ConfigPage<T> extends InteractiveCustomUIPage<ConfigEvent> {

    private final WrappedConfig<T> config;
    private final List<String> codecKeys;
    private final BsonDocument currentDocument;

    private String filterString = "";
    private List<String> filteredKeys;

    private int pageSize = 5;
    private int currentPage = 0;

    public ConfigPage(@NonNullDecl PlayerRef playerRef, WrappedConfig<T> config) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, ConfigEvent.CODEC);
        this.config = config;
        this.codecKeys = this.filteredKeys = config.codec().getEntries().keySet().stream().sorted().toList();

        this.currentDocument = config.read((c) -> config.codec().encode(c, ExtraInfo.THREAD_LOCAL.get()));
    }

    @Override
    public void build(
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder cmds,
            @NonNullDecl UIEventBuilder events, @NonNullDecl Store<EntityStore> store
    ) {
        cmds.append("Pages/ConfigEditorPanel.ui");

        cmds.set("#PageTitle.Text", "Configuring Mod: " + config.metadata().name());
        cmds.set("#Filter.Value", filterString);

        buildConfigList(cmds, events);
        addPageButtonBindings(events);

        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#Filter",
                ConfigEvent.pageEvent(ConfigEventType.FILTER_KEYS, "#Filter.Value")
        );

        events.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#PageSize",
                ConfigEvent.pageEvent(ConfigEventType.CHANGE_PAGE_SIZE, "#PageSize.Value")
        );

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#Save",
                ConfigEvent.pageEvent(ConfigEventType.SAVE)
        );

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#GoBackButton",
                ConfigEvent.pageEvent(ConfigEventType.OPEN_MODS_PAGE)
        );
    }

    private void buildConfigList(
            @NonNullDecl UICommandBuilder cmds,
            @NonNullDecl UIEventBuilder events
    ) {
        int startOfPage = currentPage * pageSize + 1;
        int endOfPage = Math.min(startOfPage + pageSize - 1, filteredKeys.size());
        int totalItems = filteredKeys.size();
        int finalPage = (int) Math.ceil((double) totalItems / pageSize) - 1;

        Element.TotalItems.setText(
                cmds, startOfPage, endOfPage, totalItems, currentPage + 1, finalPage + 1
        );
        Element.PreviousPageButton.setDisabled(cmds, this.currentPage == 0);
        Element.NextPageButton.setDisabled(cmds, this.currentPage >= finalPage);
        Element.ListItems.clear(cmds);

        int startIndex = currentPage * pageSize;
        int endIndex = Math.min((currentPage + 1) * pageSize, filteredKeys.size());
        List<String> page = filteredKeys.subList(startIndex, endIndex);
        for (int i = 0; i < page.size(); ++i) {
            var key = page.get(i);

            var docs = config.codec().getEntries().get(key).getFirst().getDocumentation();
            Element.ConfigItem item = Element.ListItems.newConfigItem(cmds, events, i, docs);

            var docValue = currentDocument.get(key);
            if (docValue == null) {
                continue;
            }
            item.setConfigKey(key);
            item.setValue(docValue);
            item.setResetButton();
        }
    }

    private void addPageButtonBindings(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#PreviousPageButton",
                ConfigEvent.pageEvent(ConfigEventType.SET_PAGE, String.valueOf(this.currentPage - 1))
        );

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#NextPageButton",
                ConfigEvent.pageEvent(ConfigEventType.SET_PAGE, String.valueOf(this.currentPage + 1))
        );
    }

    @Override
    public void handleDataEvent(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl ConfigEvent data
    ) {
        int idx = data.getIndexOnPage() + this.currentPage * this.pageSize;

        String key = "";
        if (idx < filteredKeys.size()) {
            key = filteredKeys.get(idx);
        }
        UICommandBuilder cmds = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();

        switch (data.getAction()) {
            case ConfigEventType.STR_CONFIG_VALUE_CHANGE_EVENT:
                currentDocument.replace(key, new BsonString(data.getStrVal()));
                this.sendUpdate(cmds);
                break;
            case ConfigEventType.INT_CONFIG_VALUE_CHANGE_EVENT:
                // If there was an error parsing, set it back to the current value.
                if (data.hasDataParseError()) {
                    if (!data.dataIsEmpty()) {
                        cmds.set(
                                "#ListItems[%d] #ConfigIntValue.Value".formatted(data.getIndexOnPage()),
                                Integer.toString(currentDocument.get(key).asInt32().getValue())
                        );
                    }
                    this.sendUpdate(cmds);
                    break;
                }
                currentDocument.replace(key, new BsonInt32(data.getIntVal()));
                this.sendUpdate(cmds);
                break;
            case ConfigEventType.BOOL_CONFIG_VALUE_CHANGE_EVENT:
                currentDocument.replace(key, new BsonBoolean(data.getBoolVal()));
                this.sendUpdate(cmds);
                break;
            case ConfigEventType.RESET_VALUE:
                ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
                var defaultDoc = config.codec().encode(config.codec().getDefaultValue(extraInfo), extraInfo);
                var fKey = filteredKeys.get(data.getIntVal() + this.currentPage * this.pageSize);
                var defaultValue = defaultDoc.get(fKey);

                currentDocument.replace(fKey, defaultValue);

                switch (defaultValue.getBsonType()) {
                    case STRING:
                        cmds.set(
                                "#ListItems[%d] #ConfigStringValue.Value".formatted(data.getIntVal()),
                                defaultValue.asString().getValue()
                        );
                        break;
                    case INT32:
                        cmds.set(
                                "#ListItems[%d] #ConfigIntValue.Value".formatted(data.getIntVal()),
                                Integer.toString(defaultValue.asInt32().getValue())
                        );
                        break;
                    case BOOLEAN:
                        cmds.set(
                                "#ListItems[%d] #ConfigCheckbox.Value".formatted(data.getIntVal()),
                                defaultValue.asBoolean().getValue()
                        );
                        break;
                }

                this.sendUpdate(cmds);
                break;
            case ConfigEventType.FILTER_KEYS:
                filterString = data.getStrVal();

                // If the filter string does not have a capital, treat the search as case-insensitive.
                if (!hasCapital(filterString)) {
                    filteredKeys = codecKeys.stream().filter(
                                    (codecKey) -> codecKey.toLowerCase().contains(filterString))
                            .toList();
                } else {
                    filteredKeys = codecKeys.stream().filter((codecKey) -> codecKey.contains(filterString)).toList();
                }

                int finalPage = (int) Math.ceil((double) filteredKeys.size() / pageSize) - 1;
                this.currentPage = Math.max(Math.min(finalPage, currentPage), 0);
                addPageButtonBindings(events);

                this.buildConfigList(cmds, events);
                this.sendUpdate(cmds, events, false);
                break;
            case ConfigEventType.SET_PAGE:
                this.currentPage = data.getIntVal();
                this.rebuild();
                break;
            case ConfigEventType.CHANGE_PAGE_SIZE:
                if (data.hasDataParseError()) {
                    if (!data.dataIsEmpty()) {
                        cmds.set(
                                "#PageSize.Value",
                                Integer.toString(this.pageSize)
                        );
                    } else {
                        this.pageSize = 5;
                    }
                    this.buildConfigList(cmds, events);
                    this.sendUpdate(cmds, events, false);
                    break;
                }
                this.pageSize = data.getIntVal();
                this.buildConfigList(cmds, events);
                this.sendUpdate(cmds, events, false);
                break;
            case ConfigEventType.SAVE:
                config.saveFrom(config.codec().decode(currentDocument, ExtraInfo.THREAD_LOCAL.get()));
            case ConfigEventType.OPEN_MODS_PAGE:
                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) {
                    return;
                }

                player.getPageManager().openCustomPage(ref, store, new ConfigSelectorPage(playerRef));
                break;
            case ConfigEventType.CLOSE:
                this.close();
                break;
            default:
                playerRef.sendMessage(Message.raw("invalid action"));
                this.sendUpdate();
        }
    }

    public boolean hasCapital(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }
}
