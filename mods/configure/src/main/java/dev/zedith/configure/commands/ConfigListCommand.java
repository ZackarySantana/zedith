package dev.zedith.configure.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.configure.Configure;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConfigListCommand extends AbstractPlayerCommand {

    public ConfigListCommand() {
        super("configlist", "List all mods that can be configured.");
        addAliases("clist");
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        playerRef.sendMessage(Message.raw("Plugins that can be configured:"));
        for (String name : Configure.getConfigs().keySet()) {
            playerRef.sendMessage(Message.raw("- " + name));
        }
    }
}
