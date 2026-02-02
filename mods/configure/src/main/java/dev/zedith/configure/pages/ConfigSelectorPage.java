package dev.zedith.configure.pages;

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
import dev.zedith.configure.Configure;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.data.ConfigEvent;
import dev.zedith.configure.data.ConfigEventType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class ConfigSelectorPage extends InteractiveCustomUIPage<ConfigEvent> {

    private String filterString = "";
    private List<String> filteredMods;

    private int pageSize = 5;
    private int currentPage = 0;

    public ConfigSelectorPage(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, ConfigEvent.CODEC);
        this.filteredMods = Configure.getConfigs().keySet().stream().toList();
    }

    @Override
    public void build(
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder cmds,
            @NonNullDecl UIEventBuilder events, @NonNullDecl Store<EntityStore> store
    ) {
        cmds.append("Pages/ConfigSelector.ui");

        cmds.set("#Filter.Value", filterString);

        buildModList(cmds, events);
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
                "#CloseButton",
                ConfigEvent.pageEvent(ConfigEventType.CLOSE)
        );
    }

    private void buildModList(
            @NonNullDecl UICommandBuilder cmds,
            @NonNullDecl UIEventBuilder events
    ) {
        int startOfPage = currentPage * pageSize + 1;
        int endOfPage = Math.min(startOfPage + pageSize - 1, filteredMods.size());
        int totalItems = filteredMods.size();
        int finalPage = (int) Math.ceil((double) totalItems / pageSize) - 1;

        Element.TotalItems.setText(
                cmds, startOfPage, endOfPage, totalItems, currentPage + 1, finalPage + 1
        );
        Element.PreviousPageButton.setDisabled(cmds, this.currentPage == 0);
        Element.NextPageButton.setDisabled(cmds, this.currentPage >= finalPage);
        Element.ListItems.clear(cmds);

        int startIndex = currentPage * pageSize;
        int endIndex = Math.min((currentPage + 1) * pageSize, filteredMods.size());
        List<String> page = filteredMods.subList(startIndex, endIndex);
        for (int i = 0; i < page.size(); ++i) {
            var modName = page.get(i);

            Element.ModItem item = Element.ListItems.newModItem(cmds, events, i);
            item.setName(modName);
            item.setOpenButton();
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
        UICommandBuilder cmds = new UICommandBuilder();
        UIEventBuilder events = new UIEventBuilder();

        switch (data.getAction()) {
            case ConfigEventType.FILTER_KEYS:
                filterString = data.getStrVal();

                // If the filter string does not have a capital, treat the search as case-insensitive.
                if (!hasCapital(filterString)) {
                    filteredMods = Configure.getConfigs().keySet().stream()
                            .filter((codecKey) -> codecKey.toLowerCase().contains(filterString)).toList();
                } else {
                    filteredMods = Configure.getConfigs().keySet().stream()
                            .filter((codecKey) -> codecKey.contains(filterString)).toList();
                }

                int finalPage = (int) Math.ceil((double) filteredMods.size() / pageSize) - 1;
                this.currentPage = Math.max(Math.min(finalPage, currentPage), 0);
                addPageButtonBindings(events);

                this.buildModList(cmds, events);
                this.sendUpdate(cmds, events, false);
                break;
            case ConfigEventType.OPEN_MOD_PAGE:
                var modName = filteredMods.get(data.getIntVal() + this.currentPage * this.pageSize);

                WrappedConfig<?> config = Configure.getConfigs().get(modName);
                if (config == null) {
                    playerRef.sendMessage(Message.raw("That mod isn't configurable. Please consult the mod author."));
                    break;
                }

                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) {
                    return;
                }

                player.getPageManager().openCustomPage(ref, store, new ConfigPage<>(playerRef, config));
                break;
            case ConfigEventType.SET_PAGE:
                this.currentPage = data.getIntVal();
                this.rebuild();
                break;
            case ConfigEventType.CHANGE_PAGE_SIZE:
                if (data.hasDataParseError()) {
                    if (!data.isDataEmpty()) {
                        cmds.set(
                                "#PageSize.Value",
                                Integer.toString(this.pageSize)
                        );
                    } else {
                        this.pageSize = 5;
                    }
                    this.buildModList(cmds, events);
                    this.sendUpdate(cmds, events, false);
                    break;
                }
                this.pageSize = data.getIntVal();
                this.buildModList(cmds, events);
                this.sendUpdate(cmds, events, false);
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
