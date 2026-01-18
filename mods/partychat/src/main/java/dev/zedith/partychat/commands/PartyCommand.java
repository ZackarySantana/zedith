package dev.zedith.partychat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.glowtext.Glow;
import dev.zedith.glowtext.Option;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PartyCommand extends AbstractPlayerCommand {

    public PartyCommand(PartyDataManager partyDataManager) {
        super("party", "Party command");
        setPermissionGroup(GameMode.Adventure);
        addAliases("p");

        addSubCommand(new InviteSubCommand(partyDataManager));
        addSubCommand(new JoinSubCommand(partyDataManager));
        addSubCommand(new LeaveSubCommand(partyDataManager));
        addSubCommand(new ListSubCommand(partyDataManager));
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

        try {

            playerRef.sendMessage(
                    Glow.parse(
                            "{red}This is red{bold} and bold{/red} and only bold{/bold} and nothing!",
                            Option.withOpeningStyleChar('{')
                    )
            );
        } catch (Exception e) {
            playerRef.sendMessage(Message.raw("failed: " + e.getMessage()));
        }


        playerRef.sendMessage(this.getUsageShort(player, false));
    }
}
