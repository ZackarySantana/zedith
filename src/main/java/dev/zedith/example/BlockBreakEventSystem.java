package dev.zedith.example;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BlockBreakEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {
    protected BlockBreakEventSystem() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void handle(
            int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl BreakBlockEvent breakBlockEvent
    ) {
        Ref<EntityStore> es = archetypeChunk.getReferenceTo(index);

        Player player = store.getComponent(es, Player.getComponentType());
        if (player == null) {
            return;
        }

        player.sendMessage(Message.raw("You mined a %s".formatted(breakBlockEvent.getBlockType().getId())));

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
