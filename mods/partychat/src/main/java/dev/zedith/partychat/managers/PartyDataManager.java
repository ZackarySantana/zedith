package dev.zedith.partychat.managers;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zedith.configure.config.WrappedConfig;
import dev.zedith.partychat.config.PartyConfig;
import dev.zedith.partychat.data.PartyData;
import dev.zedith.partychat.events.PartyChatSendEvent;

import java.util.*;
import java.util.function.Function;

public class PartyDataManager {

    private final WrappedConfig<PartyConfig> config;

    private final Map<PlayerRef, List<Invite>> playerToInvites;
    private final Map<PlayerRef, UUID> playerToParty;
    private final Map<UUID, PartyData> parties;
    private final Set<PlayerRef> partyModeEnabledPlayers;

    public PartyDataManager(WrappedConfig<PartyConfig> config) {
        this.config = config;
        playerToInvites = new HashMap<>();
        playerToParty = new HashMap<>();
        parties = new HashMap<>();
        partyModeEnabledPlayers = new HashSet<>();
    }

    public <R> R cfg(Function<PartyConfig, ? extends R> fn) {
        return config.read(fn);
    }

    private UUID getOrCreateParty(PlayerRef leader) {
        UUID partyId = playerToParty.get(leader);
        if (partyId == null) {
            partyId = UUID.randomUUID();
            playerToParty.put(leader, partyId);
            parties.put(partyId, new PartyData(leader, new HashSet<>(), new HashSet<>()));
            sendSystemMessage(leader, cfg(PartyConfig::getCreatePartyMessage));
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
            sendSystemMessage(inviter, cfg(PartyConfig::getNotLeaderInviteMessage));
            return;
        }

        for (PlayerRef invitee : invitees) {
            if (invitee == null) {
                continue;
            }
            if (!invitee.isValid()) {
                sendSystemMessage(
                        inviter,
                        cfg(PartyConfig::getInviteeNotOnlineMessage).formatted(invitee.getUsername())
                );
                continue;
            }
            if (partyData.inParty(invitee)) {
                sendSystemMessage(inviter, cfg(PartyConfig::getAlreadyInPartyMessage));
                continue;
            }

            sendSystemMessage(
                    partyData,
                    cfg(PartyConfig::getInviteAnnounceToPartyMessage).formatted(invitee.getUsername())
            );

            List<Invite> invites = playerToInvites.computeIfAbsent(invitee, k -> new ArrayList<>());
            if (invites.stream().anyMatch((invite) ->
                    invite.partyId.equals(partyId) &&
                            invite.inviter.getUuid().equals(inviter.getUuid())
            )) {
                sendSystemMessage(inviter, cfg(PartyConfig::getAlreadyInvitedMessage));
                return;
            }

            invites.add(new Invite(partyId, invitee));
            sendSystemMessage(
                    invitee, cfg(PartyConfig::getInviteSentToInviteeMessage).formatted(inviter.getUsername()));
        }
    }

    public void acceptInvite(PlayerRef inviter, PlayerRef invitee) {
        List<Invite> invites = playerToInvites.get(invitee);
        if (invites == null || invites.isEmpty()) {
            sendSystemMessage(invitee, cfg(PartyConfig::getNoInviteToAcceptMessage));
            return;
        }

        Invite invite;
        if (inviter != null && inviter.isValid()) {
            invite = invites.stream()
                    .filter(i -> i.inviter.getUuid().equals(inviter.getUuid()))
                    .findFirst()
                    .orElse(null);

            if (invite == null) {
                sendSystemMessage(
                        invitee,
                        cfg(PartyConfig::getNoInviteFromInviterMessage).formatted(inviter.getUsername())
                );
                return;
            }
        } else {
            invite = invites.getLast();
        }

        PartyData partyData = parties.get(invite.partyId);
        sendSystemMessage(partyData, cfg(PartyConfig::getJoinPartyAnnounceMessage).formatted(invitee.getUsername()));

        partyData.members().add(invitee);
        playerToParty.put(invitee, invite.partyId);

        sendSystemMessage(invitee, cfg(PartyConfig::getJoinedPartyLedByMessage).formatted(partyData.partyLeaderName()));
    }

    public void leaveParty(PlayerRef player) {
        UUID partyId = playerToParty.get(player);
        if (partyId == null) {
            sendSystemMessage(player, cfg(PartyConfig::getNoPartyToLeaveMessage));
            return;
        }

        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            sendSystemMessage(player, cfg(PartyConfig::getNoPartyToLeaveMessage));
            return;
        }

        boolean isLeader = partyData.isLeader(player);

        sendSystemMessage(partyData, cfg(PartyConfig::getLeavePartyAnnounceMessage).formatted(player.getUsername()));
        partyData.members().remove(player);
        playerToParty.remove(player);

        if (isLeader) {
            sendSystemMessage(partyData, cfg(PartyConfig::getDisbandPartyLeaderLeftMessage));
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
            sendSystemMessage(player, cfg(PartyConfig::getNoPartyToListMessage));
            return;
        }

        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            sendSystemMessage(player, cfg(PartyConfig::getNoPartyToListMessage));
            return;
        }

        player.sendMessage(Message.raw(cfg(PartyConfig::getListHeaderMessage)));

        player.sendMessage(Message.raw(
                cfg(PartyConfig::getListLeaderFormat).replace("[LEADER]", partyData.leader().getUsername())
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
                cfg(PartyConfig::getListMembersFormat).replace("[MEMBERS]", otherMembers.toString())
        ));
    }

    public void togglePartyMode(PlayerRef player) {
        if (partyModeEnabledPlayers.contains(player)) {
            partyModeEnabledPlayers.remove(player);
            sendSystemMessage(player, cfg(PartyConfig::getPartyModeDisabledMessage));
        } else {
            partyModeEnabledPlayers.add(player);
            sendSystemMessage(player, cfg(PartyConfig::getPartyModeEnabledMessage));
        }
    }

    public boolean partyModeEnabled(PlayerRef player) {
        return partyModeEnabledPlayers.contains(player);
    }

    public void sendPlayerMessage(PlayerRef from, Message message) {
        UUID partyId = playerToParty.get(from);
        if (partyId == null) {
            sendSystemMessage(from, cfg(PartyConfig::getNotInPartyMessage));
            return;
        }

        PartyData partyData = parties.get(partyId);
        if (partyData == null) {
            sendSystemMessage(from, cfg(PartyConfig::getNotInPartyMessage));
            return;
        }

        String playerSendChatFormat = cfg(PartyConfig::getPlayerSendChatFormat);

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
        to.sendMessage(Message.raw("%s%s".formatted(cfg(PartyConfig::getSystemPrefix), message)));
    }

    private void sendSystemMessage(PartyData partyData, String message) {
        for (PlayerRef currentMember : partyData.members()) {
            sendSystemMessage(currentMember, message);
        }
    }

    private record Invite(UUID partyId, PlayerRef inviter) {
    }
}
