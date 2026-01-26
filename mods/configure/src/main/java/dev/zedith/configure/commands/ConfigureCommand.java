package dev.zedith.configure.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.configure.Configure;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.configure.pages.ConfigPage;
import dev.zedith.configure.pages.ConfigSelectorPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ConfigureCommand extends AbstractPlayerCommand {

    private final OptionalArg<String> modNameArgument;

    public ConfigureCommand() {
        super("Configure", "Allows you to edit the config of a mod.");
        addAliases("config", "c");

        this.modNameArgument = withOptionalArg("Mod Name", "The mod to configure", ArgTypes.STRING);
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }
        String modName = this.modNameArgument.get(commandContext);
        if (modName != null) {
            openSpecificMod(ref, store, player, playerRef, modName);
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new ConfigSelectorPage(playerRef));
    }

    private void openSpecificMod(
            Ref<EntityStore> ref, Store<EntityStore> store, Player player, PlayerRef playerRef, String modName) {
        WrappedConfig<?> config = Configure.getConfigs().get(modName.toLowerCase());
        if (config == null) {
            playerRef.sendMessage(Message.raw("That mod cannot be configured."));
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new ConfigPage<>(playerRef, config));
    }
}
