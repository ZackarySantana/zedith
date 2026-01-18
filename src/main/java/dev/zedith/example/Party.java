package dev.zedith.example;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class Party extends CommandBase {

    protected Party() {
        super("party", "Create a party for yourself and friends!");
//        super("add", "server.commands.op.add.desc");
//        this.playerArg = this.withRequiredArg("player", "server.commands.op.add.player.desc", ArgTypes.PLAYER_UUID);
//        this.requirePermission(HytalePermissions.fromCommand("op.add"));
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        commandContext.sender().sendMessage(Message.raw("Hello world"));
        commandContext.sendMessage(Message.raw("second message a new world!!!"));
    }
}
