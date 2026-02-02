package dev.zedith.configure.pages;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.exception.CodecValidationException;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.data.ConfigEvent;
import dev.zedith.configure.data.ConfigEventType;
import org.bson.*;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;
import java.util.function.Consumer;

public class ConfigPage<T> extends InteractiveCustomUIPage<ConfigEvent> {

    private final WrappedConfig<T> config;
    private final List<String> codecKeys;
    private final BsonDocument currentDocument, defaultDocument;

    private String filterString = "";
    private List<String> filteredKeys;

    private int pageSize = 5;
    private int currentPage = 0;

    public ConfigPage(@NonNullDecl PlayerRef playerRef, WrappedConfig<T> config) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, ConfigEvent.CODEC);
        this.config = config;
        this.codecKeys = this.filteredKeys = config.codec().getEntries().keySet().stream().sorted().toList();

        ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
        this.currentDocument = config.read((c) -> config.codec().encode(c, extraInfo));
        this.defaultDocument = config.codec().encode(config.codec().getDefaultValue(extraInfo), extraInfo);
    }

    protected void openPage(
            Ref<EntityStore> ref,
            Store<EntityStore> store,
            CustomUIPage page
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.getPageManager().openCustomPage(ref, store, page);
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

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#PreviousPageButton",
                ConfigEvent.pageEvent(ConfigEventType.SET_PAGE2, "-1")
        );

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#NextPageButton",
                ConfigEvent.pageEvent(ConfigEventType.SET_PAGE2, "1")
        );

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

        // Ensure that the current page is within 0 - finalPage.
        this.currentPage = Math.max(Math.min(finalPage, currentPage), 0);

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

    @Override
    public void handleDataEvent(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl ConfigEvent data
    ) {
        int idx = data.getIndexOnPage() + this.currentPage * this.pageSize;
        String key = idx < filteredKeys.size() ? filteredKeys.get(idx) : null;
        UICommandBuilder cmds = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();

        Consumer<BsonValue> setValue = (value) -> {
            String selector;
            Consumer<BsonValue> setToOld;
            if (value.isString()) {
                selector = "#ListItems[%d] #ConfigStringValue.Value".formatted(data.getIndexOnPage());
                setToOld = (toValue) -> cmds.set(selector, toValue.asString().getValue());
            } else if (value.isInt32()) {
                selector = "#ListItems[%d] #ConfigIntValue.Value".formatted(data.getIndexOnPage());
                setToOld = (toValue) -> cmds.set(selector, Integer.toString(toValue.asInt32().getValue()));
            } else if (value.isBoolean()) {
                selector = "#ListItems[%d] #ConfigCheckbox.Value".formatted(data.getIndexOnPage());
                setToOld = (toValue) -> cmds.set(selector, toValue.asBoolean().getValue());
            } else {
                // TODO: Error case.
                this.sendUpdate(cmds);
                return;
            }

            BsonValue currentValue = currentDocument.get(key);
            if (data.hasDataParseError()) {
                if (!data.isDataEmpty()) {
                    setToOld.accept(currentValue);
                }

                this.sendUpdate(cmds);
                return;
            }

            currentDocument.put(key, value);
            try {
                config.codec().decode(currentDocument, ExtraInfo.THREAD_LOCAL.get());
            } catch (CodecValidationException e) {
                currentDocument.replace(key, currentValue);
                if (!data.isDataEmpty()) {
                    setToOld.accept(currentValue);
                }

                this.sendUpdate(cmds);
                return;
            }

            setToOld.accept(value);
            this.sendUpdate(cmds);
        };

        switch (data.getAction()) {
            case STR_CONFIG_VALUE_CHANGE_EVENT -> setValue.accept(new BsonString(data.getStrVal()));
            case INT_CONFIG_VALUE_CHANGE_EVENT -> setValue.accept(new BsonInt32(data.getIntVal()));
            case BOOL_CONFIG_VALUE_CHANGE_EVENT -> setValue.accept(new BsonBoolean(data.getBoolVal()));
            case RESET_VALUE -> setValue.accept(defaultDocument.get(key));
            case FILTER_KEYS -> {
                this.filterString = data.getStrVal();
                this.filteredKeys = codecKeys.stream().filter(
                        (codecKey) -> codecKey.toLowerCase().contains(this.filterString)
                ).toList();
                this.buildConfigList(cmds, events);
                this.sendUpdate(cmds, events, false);
            }
            case SET_PAGE2 -> {
                this.currentPage += data.getIntVal();
                this.rebuild();
            }
            case CHANGE_PAGE_SIZE -> {
                if (data.hasDataParseError()) {
                    if (!data.isDataEmpty()) {
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
            }
            case SAVE -> {
                try {
                    config.saveFrom(config.codec().decode(currentDocument, ExtraInfo.THREAD_LOCAL.get()));
                    openPage(ref, store, new ConfigSelectorPage(playerRef));
                } catch (CodecValidationException e) {
                    // TODO: This should be a notification.
                    playerRef.sendMessage(Message.raw("There was an error saving."));
                    this.sendUpdate();
                }
            }
            case OPEN_MODS_PAGE -> openPage(ref, store, new ConfigSelectorPage(playerRef));
            case CLOSE -> this.close();
            default -> {
                playerRef.sendMessage(Message.raw("Invalid action, please contact the developer."));
                this.sendUpdate();
            }
        }
    }
}
