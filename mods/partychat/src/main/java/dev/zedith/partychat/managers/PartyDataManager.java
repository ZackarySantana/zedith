package dev.zedith.partychat.managers;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import dev.zedith.partychat.config.PartyConfig;
import dev.zedith.partychat.data.PartyData;
import dev.zedith.partychat.events.PartyChatSendEvent;

import java.util.*;

public class PartyDataManager {

    private final Config<PartyConfig> config;

    private final Map<PlayerRef, List<Invite>> playerToInvites;
    private final Map<PlayerRef, UUID> playerToParty;
    private final Map<UUID, PartyData> parties;
    private final Set<PlayerRef> partyModeEnabledPlayers;

    public PartyDataManager(Config<PartyConfig> config) {
        this.config = config;
        playerToInvites = new HashMap<>();
        playerToParty = new HashMap<>();
        parties = new HashMap<>();
        partyModeEnabledPlayers = new HashSet<>();
    }

    private PartyConfig cfg() {
        return config.get();
    }

    private UUID getOrCreateParty(PlayerRef leader) {
        UUID partyId = playerToParty.get(leader);
        if (partyId == null) {
            partyId = UUID.randomUUID();
            playerToParty.put(leader, partyId);
            parties.put(partyId, new PartyData(leader, new HashSet<>(), new HashSet<>()));
            sendSystemMessage(leader, cfg().getCreatePartyMessage());
        }
        return partyId;
    }

    public void inviteMembers(PlayerRef inviter, List<PlayerRef> invitees) {
        UUID partyId = getOrCreateParty(inviter);
        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            return;
        }
        if (!partyData.isLeader(inviter)) {
            sendSystemMessage(inviter, cfg().getNotLeaderInviteMessage());
            return;
        }

        for (PlayerRef invitee : invitees) {
            if (invitee == null) {
                continue;
            }
            if (!invitee.isValid()) {
                sendSystemMessage(inviter, cfg().getInviteeNotOnlineMessage().formatted(invitee.getUsername()));
                continue;
            }
            if (partyData.inParty(invitee)) {
                sendSystemMessage(inviter, cfg().getAlreadyInPartyMessage());
                continue;
            }

            sendSystemMessage(partyData, cfg().getInviteAnnounceToPartyMessage().formatted(invitee.getUsername()));

            List<Invite> invites = playerToInvites.computeIfAbsent(invitee, k -> new ArrayList<>());
            if (invites.stream().anyMatch((invite) ->
                                                  invite.partyId.equals(partyId) &&
                                                  invite.inviter.getUuid().equals(inviter.getUuid())
            )) {
                sendSystemMessage(inviter, cfg().getAlreadyInvitedMessage());
                return;
            }

            invites.add(new Invite(partyId, invitee));
            sendSystemMessage(invitee, cfg().getInviteSentToInviteeMessage().formatted(inviter.getUsername()));
        }
    }

    public void acceptInvite(PlayerRef inviter, PlayerRef invitee) {
        List<Invite> invites = playerToInvites.get(invitee);
        if (invites == null || invites.isEmpty()) {
            sendSystemMessage(invitee, cfg().getNoInviteToAcceptMessage());
            return;
        }

        Invite invite;
        if (inviter != null && inviter.isValid()) {
            invite = invites.stream()
                            .filter(i -> i.inviter.getUuid().equals(inviter.getUuid()))
                            .findFirst()
                            .orElse(null);

            if (invite == null) {
                sendSystemMessage(invitee, cfg().getNoInviteFromInviterMessage().formatted(inviter.getUsername()));
                return;
            }
        } else {
            invite = invites.getLast();
        }

        PartyData partyData = parties.get(invite.partyId);
        sendSystemMessage(partyData, cfg().getJoinPartyAnnounceMessage().formatted(invitee.getUsername()));

        partyData.members().add(invitee);
        playerToParty.put(invitee, invite.partyId);

        sendSystemMessage(invitee, cfg().getJoinedPartyLedByMessage().formatted(partyData.partyLeaderName()));
    }

    public void leaveParty(PlayerRef player) {
        UUID partyId = playerToParty.get(player);
        if (partyId == null) {
            sendSystemMessage(player, cfg().getNoPartyToLeaveMessage());
            return;
        }

        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            sendSystemMessage(player, cfg().getNoPartyToLeaveMessage());
            return;
        }

        boolean isLeader = partyData.isLeader(player);

        sendSystemMessage(partyData, cfg().getLeavePartyAnnounceMessage().formatted(player.getUsername()));
        partyData.members().remove(player);
        playerToParty.remove(player);

        if (isLeader) {
            sendSystemMessage(partyData, cfg().getDisbandPartyLeaderLeftMessage());
            for (PlayerRef member : partyData.members()) {
                playerToParty.remove(member);
            }
            partyData.members().clear();
            parties.remove(partyId);
        }
    }

    public void listParty(PlayerRef player) {
        UUID partyId = playerToParty.get(player);
        if (partyId == null) {
            sendSystemMessage(player, cfg().getNoPartyToListMessage());
            return;
        }

        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            sendSystemMessage(player, cfg().getNoPartyToListMessage());
            return;
        }

        player.sendMessage(Message.raw(cfg().getListHeaderMessage()));

        player.sendMessage(Message.raw(
                cfg().getListLeaderFormat().replace("[LEADER]", partyData.leader().getUsername())
        ));

        StringBuilder otherMembers = new StringBuilder();
        boolean first = true;

        for (PlayerRef member : partyData.members()) {
            if (!first) {
                otherMembers.append(", ");
            }
            otherMembers.append(member.getUsername());
            first = false;
        }

        player.sendMessage(Message.raw(
                cfg().getListMembersFormat().replace("[MEMBERS]", otherMembers.toString())
        ));
    }

    public void togglePartyMode(PlayerRef player) {
        if (partyModeEnabledPlayers.contains(player)) {
            partyModeEnabledPlayers.remove(player);
            sendSystemMessage(player, cfg().getPartyModeDisabledMessage());
        } else {
            partyModeEnabledPlayers.add(player);
            sendSystemMessage(player, cfg().getPartyModeEnabledMessage());
        }
    }

    public boolean partyModeEnabled(PlayerRef player) {
        return partyModeEnabledPlayers.contains(player);
    }

    public void sendPlayerMessage(PlayerRef from, Message message) {
        UUID partyId = playerToParty.get(from);
        if (partyId == null) {
            sendSystemMessage(from, cfg().getNotInPartyMessage());
            return;
        }

        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            sendSystemMessage(from, cfg().getNotInPartyMessage());
            return;
        }

        String playerSendChatFormat = cfg().getPlayerSendChatFormat();

        for (PlayerRef player : partyData.members()) {
            PartyChatSendEvent event = new PartyChatSendEvent(
                    from,
                    player,
                    playerSendChatFormat,
                    message.getRawText()
            );

            HytaleServer.get().getEventBus()
                        .dispatchFor(PartyChatSendEvent.class)
                        .dispatch(event)
            ;

            player.sendMessage(Message.raw(event.getMessage()));
        }
    }

    private void sendSystemMessage(PlayerRef to, String message) {
        to.sendMessage(Message.raw("%s%s".formatted(cfg().getSystemPrefix(), message)));
    }

    private void sendSystemMessage(PartyData partyData, String message) {
        for (PlayerRef currentMember : partyData.members()) {
            sendSystemMessage(currentMember, message);
        }
    }

    private record Invite(UUID partyId, PlayerRef inviter) {
    }
}
