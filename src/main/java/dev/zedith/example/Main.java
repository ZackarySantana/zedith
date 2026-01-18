package dev.zedith.example;

import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.CompletableFuture;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JavaPlugin {
    public Main(@NonNullDecl JavaPluginInit init) {
        super(init);
    }

    @NullableDecl @Override public CompletableFuture<Void> preLoad() {
        return super.preLoad();
    }

    @Override
    protected void setup() {
        CommandRegistry reg = this.getCommandRegistry();
        reg.registerCommand(new Party());
        reg.registerCommand(new Party2());

        getEntityStoreRegistry().registerSystem(new BlockBreakEventSystem());

        getEventRegistry().registerGlobal(PlayerChatEvent.class, this::onPlayerChat);
    }

    private void onPlayerChat(PlayerChatEvent playerChatEvent) {

        // this is a chat event
    }
}
