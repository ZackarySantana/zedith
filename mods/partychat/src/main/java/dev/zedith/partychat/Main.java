package dev.zedith.partychat;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zedith.configure.Configure;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.data.ConfigMetadata;
import dev.zedith.partychat.commands.PartyChatCommand;
import dev.zedith.partychat.commands.PartyCommand;
import dev.zedith.partychat.config.PartyConfig;
import dev.zedith.partychat.events.PartyChatSendEvent;
import dev.zedith.partychat.listeners.PartyChatSend;
import dev.zedith.partychat.listeners.PlayerChat;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class Main extends JavaPlugin {

    private final WrappedConfig<PartyConfig> wrappedConfig;

    public Main(@NonNullDecl JavaPluginInit init) {
        super(init);
        wrappedConfig = new WrappedConfig<>(
                PartyConfig.CODEC,
                withConfig(PartyConfig.CODEC),
                new ConfigMetadata("partychat")
        );
        Configure.registerConfig(wrappedConfig);
    }

    @Override
    protected void setup() {
        wrappedConfig.save();
        PartyDataManager partyDataManager = new PartyDataManager(wrappedConfig);
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
