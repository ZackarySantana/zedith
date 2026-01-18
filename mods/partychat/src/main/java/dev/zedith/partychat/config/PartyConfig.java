package dev.zedith.partychat.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class PartyConfig {

    public static final BuilderCodec<PartyConfig> CODEC =
            BuilderCodec.builder(PartyConfig.class, PartyConfig::new)
                        .append(
                                new KeyedCodec<>("PlayerSendChatFormat", BuilderCodec.STRING),
                                PartyConfig::setPlayerSendChatFormat,
                                PartyConfig::getPlayerSendChatFormat
                        ).add()
                        .append(
                                new KeyedCodec<>("SystemPrefix", BuilderCodec.STRING),
                                PartyConfig::setSystemPrefix,
                                PartyConfig::getSystemPrefix
                        ).add()
                        .append(
                                new KeyedCodec<>("CreatePartyMessage", BuilderCodec.STRING),
                                PartyConfig::setCreatePartyMessage,
                                PartyConfig::getCreatePartyMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("NotLeaderInviteMessage", BuilderCodec.STRING),
                                PartyConfig::setNotLeaderInviteMessage,
                                PartyConfig::getNotLeaderInviteMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("InviteeNotOnlineMessage", BuilderCodec.STRING),
                                PartyConfig::setInviteeNotOnlineMessage,
                                PartyConfig::getInviteeNotOnlineMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("AlreadyInPartyMessage", BuilderCodec.STRING),
                                PartyConfig::setAlreadyInPartyMessage,
                                PartyConfig::getAlreadyInPartyMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("InviteAnnounceToPartyMessage", BuilderCodec.STRING),
                                PartyConfig::setInviteAnnounceToPartyMessage,
                                PartyConfig::getInviteAnnounceToPartyMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("AlreadyInvitedMessage", BuilderCodec.STRING),
                                PartyConfig::setAlreadyInvitedMessage,
                                PartyConfig::getAlreadyInvitedMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("InviteSentToInviteeMessage", BuilderCodec.STRING),
                                PartyConfig::setInviteSentToInviteeMessage,
                                PartyConfig::getInviteSentToInviteeMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("NoInviteToAcceptMessage", BuilderCodec.STRING),
                                PartyConfig::setNoInviteToAcceptMessage,
                                PartyConfig::getNoInviteToAcceptMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("NoInviteFromInviterMessage", BuilderCodec.STRING),
                                PartyConfig::setNoInviteFromInviterMessage,
                                PartyConfig::getNoInviteFromInviterMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("JoinPartyAnnounceMessage", BuilderCodec.STRING),
                                PartyConfig::setJoinPartyAnnounceMessage,
                                PartyConfig::getJoinPartyAnnounceMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("JoinedPartyLedByMessage", BuilderCodec.STRING),
                                PartyConfig::setJoinedPartyLedByMessage,
                                PartyConfig::getJoinedPartyLedByMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("NoPartyToLeaveMessage", BuilderCodec.STRING),
                                PartyConfig::setNoPartyToLeaveMessage,
                                PartyConfig::getNoPartyToLeaveMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("LeavePartyAnnounceMessage", BuilderCodec.STRING),
                                PartyConfig::setLeavePartyAnnounceMessage,
                                PartyConfig::getLeavePartyAnnounceMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("DisbandPartyLeaderLeftMessage", BuilderCodec.STRING),
                                PartyConfig::setDisbandPartyLeaderLeftMessage,
                                PartyConfig::getDisbandPartyLeaderLeftMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("NoPartyToListMessage", BuilderCodec.STRING),
                                PartyConfig::setNoPartyToListMessage,
                                PartyConfig::getNoPartyToListMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("ListHeaderMessage", BuilderCodec.STRING),
                                PartyConfig::setListHeaderMessage,
                                PartyConfig::getListHeaderMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("ListLeaderFormat", BuilderCodec.STRING),
                                PartyConfig::setListLeaderFormat,
                                PartyConfig::getListLeaderFormat
                        ).add()
                        .append(
                                new KeyedCodec<>("ListMembersFormat", BuilderCodec.STRING),
                                PartyConfig::setListMembersFormat,
                                PartyConfig::getListMembersFormat
                        ).add()
                        .append(
                                new KeyedCodec<>("PartyModeEnabledMessage", BuilderCodec.STRING),
                                PartyConfig::setPartyModeEnabledMessage,
                                PartyConfig::getPartyModeEnabledMessage
                        ).add()
                        .append(
                                new KeyedCodec<>("NotInPartyMessage", BuilderCodec.STRING),
                                PartyConfig::setNotInPartyMessage,
                                PartyConfig::getNotInPartyMessage
                        ).add()
                        .build()
            ;

    // Chat prefix format
    private String playerSendChatFormat = "[[SENDER] -> [RECEIVER]] [MESSAGE]";
    private String systemPrefix = "[Party] ";

    // Party creation / invite flow
    private String createPartyMessage = "Creating a party...";
    private String notLeaderInviteMessage = "You cannot invite anyone because you are not the leader";
    private String inviteeNotOnlineMessage = "%s is not online right now";
    private String alreadyInPartyMessage = "That player is already in your party";
    private String inviteAnnounceToPartyMessage = "%s has been invited";
    private String alreadyInvitedMessage = "That person is already invited to your party";
    private String inviteSentToInviteeMessage = "You have been invited to a party by %s";

    // Accept flow
    private String noInviteToAcceptMessage = "You do not have an invite to accept.";
    private String noInviteFromInviterMessage = "You do not have an invite from %s to accept";
    private String joinPartyAnnounceMessage = "%s has joined the party!";
    private String joinedPartyLedByMessage = "You have joined the party led by %s";

    // Leave flow
    private String noPartyToLeaveMessage = "You do not have a party to leave.";
    private String leavePartyAnnounceMessage = "%s has left the party.";
    private String disbandPartyLeaderLeftMessage = "Disbanding party because leader left.";

    // List flow
    private String noPartyToListMessage = "You do not have a party to list.";
    private String listHeaderMessage = "===== Party =====";
    private String listLeaderFormat = "Leader: [LEADER]";
    private String listMembersFormat = "Member: [MEMBERS]";

    // Party mode
    private String partyModeDisabledMessage = "Party mode disabled.";
    private String partyModeEnabledMessage = "Party mode enabled.";

    // No party
    private String notInPartyMessage = "You are not in a party";

    public String getPlayerSendChatFormat() {
        return playerSendChatFormat;
    }

    public void setPlayerSendChatFormat(String playerSendChatFormat) {
        this.playerSendChatFormat = playerSendChatFormat;
    }

    public String getSystemPrefix() {
        return systemPrefix;
    }

    public void setSystemPrefix(String systemPrefix) {
        this.systemPrefix = systemPrefix;
    }

    public String getCreatePartyMessage() {
        return createPartyMessage;
    }

    public void setCreatePartyMessage(String createPartyMessage) {
        this.createPartyMessage = createPartyMessage;
    }

    public String getNotLeaderInviteMessage() {
        return notLeaderInviteMessage;
    }

    public void setNotLeaderInviteMessage(String notLeaderInviteMessage) {
        this.notLeaderInviteMessage = notLeaderInviteMessage;
    }

    public String getInviteeNotOnlineMessage() {
        return inviteeNotOnlineMessage;
    }

    public void setInviteeNotOnlineMessage(String inviteeNotOnlineMessage) {
        this.inviteeNotOnlineMessage = inviteeNotOnlineMessage;
    }

    public String getAlreadyInPartyMessage() {
        return alreadyInPartyMessage;
    }

    public void setAlreadyInPartyMessage(String alreadyInPartyMessage) {
        this.alreadyInPartyMessage = alreadyInPartyMessage;
    }

    public String getInviteAnnounceToPartyMessage() {
        return inviteAnnounceToPartyMessage;
    }

    public void setInviteAnnounceToPartyMessage(String inviteAnnounceToPartyMessage) {
        this.inviteAnnounceToPartyMessage = inviteAnnounceToPartyMessage;
    }

    public String getAlreadyInvitedMessage() {
        return alreadyInvitedMessage;
    }

    public void setAlreadyInvitedMessage(String alreadyInvitedMessage) {
        this.alreadyInvitedMessage = alreadyInvitedMessage;
    }

    public String getInviteSentToInviteeMessage() {
        return inviteSentToInviteeMessage;
    }

    public void setInviteSentToInviteeMessage(String inviteSentToInviteeMessage) {
        this.inviteSentToInviteeMessage = inviteSentToInviteeMessage;
    }

    public String getNoInviteToAcceptMessage() {
        return noInviteToAcceptMessage;
    }

    public void setNoInviteToAcceptMessage(String noInviteToAcceptMessage) {
        this.noInviteToAcceptMessage = noInviteToAcceptMessage;
    }

    public String getNoInviteFromInviterMessage() {
        return noInviteFromInviterMessage;
    }

    public void setNoInviteFromInviterMessage(String noInviteFromInviterMessage) {
        this.noInviteFromInviterMessage = noInviteFromInviterMessage;
    }

    public String getJoinPartyAnnounceMessage() {
        return joinPartyAnnounceMessage;
    }

    public void setJoinPartyAnnounceMessage(String joinPartyAnnounceMessage) {
        this.joinPartyAnnounceMessage = joinPartyAnnounceMessage;
    }

    public String getJoinedPartyLedByMessage() {
        return joinedPartyLedByMessage;
    }

    public void setJoinedPartyLedByMessage(String joinedPartyLedByMessage) {
        this.joinedPartyLedByMessage = joinedPartyLedByMessage;
    }

    public String getNoPartyToLeaveMessage() {
        return noPartyToLeaveMessage;
    }

    public void setNoPartyToLeaveMessage(String noPartyToLeaveMessage) {
        this.noPartyToLeaveMessage = noPartyToLeaveMessage;
    }

    public String getListHeaderMessage() {
        return listHeaderMessage;
    }

    public void setListHeaderMessage(String listHeaderMessage) {
        this.listHeaderMessage = listHeaderMessage;
    }

    public String getListLeaderFormat() {
        return listLeaderFormat;
    }

    public void setListLeaderFormat(String listLeaderFormat) {
        this.listLeaderFormat = listLeaderFormat;
    }

    public String getListMembersFormat() {
        return listMembersFormat;
    }

    public void setListMembersFormat(String listMembersFormat) {
        this.listMembersFormat = listMembersFormat;
    }

    public String getLeavePartyAnnounceMessage() {
        return leavePartyAnnounceMessage;
    }

    public void setLeavePartyAnnounceMessage(String leavePartyAnnounceMessage) {
        this.leavePartyAnnounceMessage = leavePartyAnnounceMessage;
    }

    public String getDisbandPartyLeaderLeftMessage() {
        return disbandPartyLeaderLeftMessage;
    }

    public void setDisbandPartyLeaderLeftMessage(String disbandPartyLeaderLeftMessage) {
        this.disbandPartyLeaderLeftMessage = disbandPartyLeaderLeftMessage;
    }

    public String getNoPartyToListMessage() {
        return noPartyToListMessage;
    }

    public void setNoPartyToListMessage(String noPartyToListMessage) {
        this.noPartyToListMessage = noPartyToListMessage;
    }

    public String getPartyModeDisabledMessage() {
        return partyModeDisabledMessage;
    }

    public void setPartyModeDisabledMessage(String partyModeDisabledMessage) {
        this.partyModeDisabledMessage = partyModeDisabledMessage;
    }

    public String getPartyModeEnabledMessage() {
        return partyModeEnabledMessage;
    }

    public void setPartyModeEnabledMessage(String partyModeEnabledMessage) {
        this.partyModeEnabledMessage = partyModeEnabledMessage;
    }

    public String getNotInPartyMessage() {
        return notInPartyMessage;
    }

    public void setNotInPartyMessage(String notInPartyMessage) {
        this.notInPartyMessage = notInPartyMessage;
    }
}
