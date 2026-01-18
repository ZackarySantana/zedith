package dev.zedith.partychat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ListSubCommand extends AbstractPlayerCommand {

    private final PartyDataManager partyDataManager;

    public ListSubCommand(PartyDataManager partyDataManager) {
        super("list", "List members in your party.");
        this.partyDataManager = partyDataManager;
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        partyDataManager.listParty(playerRef);
    }
}
