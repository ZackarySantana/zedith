package dev.zedith.partychat.events;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class PartyChatSendEvent implements IEvent<Void> {

    private final PlayerRef receiver;
    private final PlayerRef sender;
    private String format;
    private String userMessage;

    private String message;

    public PartyChatSendEvent(PlayerRef receiver, PlayerRef sender, String format, String userMessage) {
        this.receiver = receiver;
        this.sender = sender;
        this.format = format;
        this.userMessage = userMessage;
    }

    public PlayerRef getReceiver() {
        return receiver;
    }

    public PlayerRef getSender() {
        return sender;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUserMessage() {
        return this.userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
