package dev.zedith.partychat;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;
import dev.zedith.partychat.commands.PartyChatCommand;
import dev.zedith.partychat.commands.PartyCommand;
import dev.zedith.partychat.config.PartyConfig;
import dev.zedith.partychat.events.PartyChatSendEvent;
import dev.zedith.partychat.listeners.PartyChatSend;
import dev.zedith.partychat.listeners.PlayerChat;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class Main extends JavaPlugin {

    private final Config<PartyConfig> config;

    public Main(@NonNullDecl JavaPluginInit init) {
        super(init);
        config = withConfig("PartyConfig", PartyConfig.CODEC);
    }

    @Override
    protected void setup() {
        config.save();
        PartyDataManager partyDataManager = new PartyDataManager(config);
        getCommandRegistry().registerCommand(new PartyCommand(partyDataManager));
        getCommandRegistry().registerCommand(new PartyChatCommand(partyDataManager));
        getEventRegistry().registerGlobal(
                PlayerChatEvent.class,
                new PlayerChat(partyDataManager)::onPlayerChat
        );
        getEventRegistry().registerGlobal(
                PartyChatSendEvent.class,
                new PartyChatSend(partyDataManager)::onPartyChatSend
        );
    }
}
