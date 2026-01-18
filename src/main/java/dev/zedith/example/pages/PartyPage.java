package dev.zedith.example.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;


public class PartyPage extends InteractiveCustomUIPage<GreetEventData> {

    public PartyPage(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, GreetEventData.CODEC);
    }

    @Override
    public void build(
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder,
            @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store
    ) {
        uiCommandBuilder.append("Pages/Party.ui");

//        uiEventBuilder.addEventBinding(
//                CustomUIEventBindingType.Activating,
//                "#GreetButton",
//                new EventData().append("@PlayerName", "#NameInput.Value")
//        );
    }

//    @Override
//    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store,
//    @NonNullDecl GreetEventData data) {
//        Player player = store.getComponent(ref, Player.getComponentType());
//        if (player == null) {
//            return;
//        }
//
//        String playerName = data.playerName;
//        if (playerName == null || playerName.isEmpty()) {
//            playerName = "Stranger";
//        }
//
//        playerRef.sendMessage(Message.raw("Hello, " + playerName + "!"));
//        player.getPageManager().setPage(ref, store, Page.None);
//    }
}
