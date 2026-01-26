package dev.zedith.configure.data;


import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class ExampleConfig {

    public static final BuilderCodec<ExampleConfig> CODEC =
            BuilderCodec.builder(ExampleConfig.class, ExampleConfig::new)
                    .append(
                            new KeyedCodec<>("PlayerSendChatFormat", BuilderCodec.STRING),
                            ExampleConfig::setPlayerSendChatFormat,
                            ExampleConfig::getPlayerSendChatFormat
                    ).documentation("This format can be found to be bad.").add()
                    .append(
                            new KeyedCodec<>("SystemPrefix", BuilderCodec.STRING),
                            ExampleConfig::setSystemPrefix,
                            ExampleConfig::getSystemPrefix
                    ).add()
                    .append(
                            new KeyedCodec<>("CreatePartyMessage", BuilderCodec.STRING),
                            ExampleConfig::setCreatePartyMessage,
                            ExampleConfig::getCreatePartyMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("NotLeaderInviteMessage", BuilderCodec.STRING),
                            ExampleConfig::setNotLeaderInviteMessage,
                            ExampleConfig::getNotLeaderInviteMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("InviteeNotOnlineMessage", BuilderCodec.STRING),
                            ExampleConfig::setInviteeNotOnlineMessage,
                            ExampleConfig::getInviteeNotOnlineMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("AlreadyInPartyMessage", BuilderCodec.STRING),
                            ExampleConfig::setAlreadyInPartyMessage,
                            ExampleConfig::getAlreadyInPartyMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("InviteAnnounceToPartyMessage", BuilderCodec.STRING),
                            ExampleConfig::setInviteAnnounceToPartyMessage,
                            ExampleConfig::getInviteAnnounceToPartyMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("AlreadyInvitedMessage", BuilderCodec.STRING),
                            ExampleConfig::setAlreadyInvitedMessage,
                            ExampleConfig::getAlreadyInvitedMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("InviteSentToInviteeMessage", BuilderCodec.STRING),
                            ExampleConfig::setInviteSentToInviteeMessage,
                            ExampleConfig::getInviteSentToInviteeMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("NoInviteToAcceptMessage", BuilderCodec.STRING),
                            ExampleConfig::setNoInviteToAcceptMessage,
                            ExampleConfig::getNoInviteToAcceptMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("NoInviteFromInviterMessage", BuilderCodec.STRING),
                            ExampleConfig::setNoInviteFromInviterMessage,
                            ExampleConfig::getNoInviteFromInviterMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("JoinPartyAnnounceMessage", BuilderCodec.STRING),
                            ExampleConfig::setJoinPartyAnnounceMessage,
                            ExampleConfig::getJoinPartyAnnounceMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("JoinedPartyLedByMessage", BuilderCodec.STRING),
                            ExampleConfig::setJoinedPartyLedByMessage,
                            ExampleConfig::getJoinedPartyLedByMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("NoPartyToLeaveMessage", BuilderCodec.STRING),
                            ExampleConfig::setNoPartyToLeaveMessage,
                            ExampleConfig::getNoPartyToLeaveMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("LeavePartyAnnounceMessage", BuilderCodec.STRING),
                            ExampleConfig::setLeavePartyAnnounceMessage,
                            ExampleConfig::getLeavePartyAnnounceMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("DisbandPartyLeaderLeftMessage", BuilderCodec.STRING),
                            ExampleConfig::setDisbandPartyLeaderLeftMessage,
                            ExampleConfig::getDisbandPartyLeaderLeftMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("NoPartyToListMessage", BuilderCodec.STRING),
                            ExampleConfig::setNoPartyToListMessage,
                            ExampleConfig::getNoPartyToListMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("ListHeaderMessage", BuilderCodec.STRING),
                            ExampleConfig::setListHeaderMessage,
                            ExampleConfig::getListHeaderMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("ListLeaderFormat", BuilderCodec.STRING),
                            ExampleConfig::setListLeaderFormat,
                            ExampleConfig::getListLeaderFormat
                    ).add()
                    .append(
                            new KeyedCodec<>("ListMembersFormat", BuilderCodec.STRING),
                            ExampleConfig::setListMembersFormat,
                            ExampleConfig::getListMembersFormat
                    ).add()
                    .append(
                            new KeyedCodec<>("PartyModeEnabledMessage", BuilderCodec.STRING),
                            ExampleConfig::setPartyModeEnabledMessage,
                            ExampleConfig::getPartyModeEnabledMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("NotInPartyMessage", BuilderCodec.STRING),
                            ExampleConfig::setNotInPartyMessage,
                            ExampleConfig::getNotInPartyMessage
                    ).add()
                    .append(
                            new KeyedCodec<>("AAnIntegerTest", BuilderCodec.INTEGER),
                            ExampleConfig::setIntegerCheck,
                            ExampleConfig::getIntegerCheck
                    ).documentation("This is a int. You can't type letters or other things.").add()
                    .append(
                            new KeyedCodec<>("AAABoolTrue", BuilderCodec.BOOLEAN),
                            ExampleConfig::setBooleanCheckTrue,
                            ExampleConfig::isBooleanCheckTrue
                    ).add()
                    .append(
                            new KeyedCodec<>("AAABoolFalse", BuilderCodec.BOOLEAN),
                            ExampleConfig::setBooleanCheckFalse,
                            ExampleConfig::isBooleanCheckFalse
                    ).documentation("This is a default false bool.").add()
                    .build();

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

    private int integerCheck = 42;
    private boolean booleanCheckTrue = true;
    private boolean booleanCheckFalse = false;

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

    public int getIntegerCheck() {
        return integerCheck;
    }

    public void setIntegerCheck(int integerCheck) {
        this.integerCheck = integerCheck;
    }

    public boolean isBooleanCheckFalse() {
        return booleanCheckFalse;
    }

    public void setBooleanCheckFalse(boolean booleanCheckFalse) {
        this.booleanCheckFalse = booleanCheckFalse;
    }

    public boolean isBooleanCheckTrue() {
        return booleanCheckTrue;
    }

    public void setBooleanCheckTrue(boolean booleanCheckTrue) {
        this.booleanCheckTrue = booleanCheckTrue;
    }
}

