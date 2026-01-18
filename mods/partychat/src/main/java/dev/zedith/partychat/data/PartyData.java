package dev.zedith.partychat.data;

import com.hypixel.hytale.server.core.universe.PlayerRef;

import java.util.Set;

public record PartyData(
        PlayerRef leader,
        Set<PlayerRef> members,
        Set<PlayerRef> invites
) {
    public PartyData {
        members.add(leader);
    }

    public String partyLeaderName() {
        return leader.getUsername();
    }

    public boolean inParty(PlayerRef player) {
        return members.contains(player);
    }

    public boolean isLeader(PlayerRef leader) {
        return leader.getUuid().equals(this.leader.getUuid());
    }
}
