package dev.zedith.partychat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class InviteSubCommand extends AbstractPlayerCommand {

    private final PartyDataManager partyDataManager;

    private final RequiredArg<List<PlayerRef>> inviteeArgument;

    public InviteSubCommand(PartyDataManager partyDataManager) {
        super("invite", "Invite a player to your party. Creates a party if you are not the leader of one.");
        this.partyDataManager = partyDataManager;
        this.inviteeArgument = withListRequiredArg("invitee", "The person to invite", ArgTypes.PLAYER_REF);
        addAliases("i");
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        List<PlayerRef> targetPlayerRefs = this.inviteeArgument.get(commandContext);
        partyDataManager.inviteMembers(playerRef, targetPlayerRefs);
    }
}
