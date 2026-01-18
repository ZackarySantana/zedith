package dev.zedith.partychat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zedith.partychat.managers.PartyDataManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class PartyChatCommand extends AbstractPlayerCommand {

    PartyDataManager partyDataManager;

    public PartyChatCommand(PartyDataManager partyDataManager) {
        super("partychat", "Talk in party chat.");
        this.partyDataManager = partyDataManager;
        setPermissionGroup(GameMode.Adventure);
        addAliases("pchat", "pc");

        setAllowsExtraArguments(true);
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        String input = commandContext.getInputString().trim();

        if (input.contains(" ")) {
            partyDataManager.sendPlayerMessage(playerRef, Message.raw(input.substring(input.indexOf(" ") + 1)));
        } else {
            partyDataManager.togglePartyMode(playerRef);
        }
    }
}
