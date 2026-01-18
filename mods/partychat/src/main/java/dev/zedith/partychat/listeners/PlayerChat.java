package dev.zedith.partychat.listeners;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import dev.zedith.partychat.managers.PartyDataManager;

public record PlayerChat(PartyDataManager manager) {

    public void onPlayerChat(PlayerChatEvent event) {
        if (manager.partyModeEnabled(event.getSender())) {
            event.setCancelled(true);
            manager.sendPlayerMessage(event.getSender(), Message.raw(event.getContent()));
        }
    }
}
