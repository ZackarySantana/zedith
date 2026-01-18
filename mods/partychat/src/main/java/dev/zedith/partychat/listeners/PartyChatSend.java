package dev.zedith.partychat.listeners;

import dev.zedith.partychat.events.PartyChatSendEvent;
import dev.zedith.partychat.managers.PartyDataManager;

public record PartyChatSend(PartyDataManager manager) {

    public void onPartyChatSend(PartyChatSendEvent event) {
        String message = event.getFormat();

        message = message.replace("[SENDER]", event.getSender().getUsername());
        message = message.replace("[RECEIVER]", event.getReceiver().getUsername());
        message = message.replace("[MESSAGE]", event.getUserMessage());

        event.setMessage(message);
    }
}
