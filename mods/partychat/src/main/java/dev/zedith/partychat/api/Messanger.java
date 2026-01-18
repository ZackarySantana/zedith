package dev.zedith.partychat.api;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public interface Messanger {
    void send(PlayerRef to, Message message);
}
