package dev.zedith.partychat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class JoinSubCommand extends AbstractPlayerCommand {

    private final PartyDataManager partyDataManager;

    private final OptionalArg<PlayerRef> inviteeArgument;

    public JoinSubCommand(PartyDataManager partyDataManager) {
        super("join", "Join a party. You can provide a name if you have multiple invites.");
        this.partyDataManager = partyDataManager;
        this.inviteeArgument = withOptionalArg("inviter", "The person who invited you.", ArgTypes.PLAYER_REF);
        addAliases("j");
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        PlayerRef inviter = this.inviteeArgument.get(commandContext);
        partyDataManager.acceptInvite(inviter, playerRef);
    }
}
