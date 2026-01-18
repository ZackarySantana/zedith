package dev.zedith.example;

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
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import dev.zedith.example.pages.PartyPage;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class Party2 extends AbstractPlayerCommand {

    protected Party2() {
        super("party2", "Create a party for yourself and friends!");
//        super("add", "server.commands.op.add.desc");
//        this.playerArg = this.withRequiredArg("player", "server.commands.op.add.player.desc", ArgTypes.PLAYER_UUID);
//        this.requirePermission(HytalePermissions.fromCommand("op.add"));
        // This is still required in an abstract player command.
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(
            @NonNullDecl CommandContext commandContext, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl Ref<EntityStore> ref, @NonNullDecl PlayerRef playerRef, @NonNullDecl World world
    ) {
        EventTitleUtil.showEventTitleToPlayer(
                playerRef,
                Message.raw("Title"),
                Message.raw("Sub title"),
                true
        );

        Player player = store.getComponent(ref, Player.getComponentType());

        PartyPage page = new PartyPage(playerRef);

        if (player == null) {
            return;
        }
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
