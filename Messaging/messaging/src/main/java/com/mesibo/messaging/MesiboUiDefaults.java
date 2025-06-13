/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-present mesibo                                              *
* https://mesibo.com                                                          *
* All rights reserved.                                                        *
*                                                                             *
* Redistribution is not permitted. Use of this software is subject to the     *
* conditions specified at https://mesibo.com . When using the source code,    *
* maintain the copyright notice, conditions, disclaimer, and  links to mesibo * 
* website, documentation and the source code repository.                      *
*                                                                             *
* Do not use the name of mesibo or its contributors to endorse products from  *
* this software without prior written permission.                             *
*                                                                             *
* This software is provided "as is" without warranties. mesibo and its        *
* contributors are not liable for any damages arising from its use.           *
*                                                                             *
* Documentation: https://docs.mesibo.com/                                     *
*                                                                             *
* Source Code Repository: https://github.com/mesibo/                          *
*******************************************************************************/

package com.mesibo.messaging;

import android.graphics.Bitmap;
import android.util.Log;

import com.mesibo.api.MesiboDateTime;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;

public class MesiboUiDefaults {
    public Bitmap contactPlaceHolder = null;
    public Bitmap messagingBackground = null;

    public boolean useLetterTitleImage = true;

    //public boolean enableVoiceCall = false;
    //public boolean enableVideoCall = false;
    public boolean enableForward = true;
    public boolean enableEdit = true;
    public boolean enableSearch = true;
    public boolean enableBackButton = false;

    public boolean showRichMessageButtons = true;

    public String messageListTitle = ""; // use getAppName if null
    public String userListTitle = "Contacts";
    public String createGroupTitle = "Create a New Group";
    public String modifyGroupTitle = "Modify group details";
    public String selectContactTitle = "Select a contact";
    public String selectGroupContactsTitle = "Select group members";
    public String forwardTitle = "Forward To";
    public String editTitle = "Edit Message";
    public String headerTitle = "";
    public int headerIcon = R.drawable.ic_info_black;
    public int headerIconColor = 0xFF00868b;
    //public String messagingTitle = "";

    public String e2eeTitle = "End-To-End Encryption";
    public String e2eeActive = "Messages and Calls are End-To-End Encrypted. No one including AppName can read or listen to your communication";
    public String e2eeIdentityChanged = "End-To-End Encryption is Active, but the Identity has changed. It is recommended to match the fingerprint";
    public String e2eeInactive = "End-To-End Encryption Not Active. TLS encryption is active.";
    public static final int e2eeIcon = R.drawable.ic_lock_black_18dp;
    public int e2eeIconColor = 0xFFCC0000;
    public boolean e2eeIndicator = true;

    public boolean showMissedCalls = true;
    public boolean showReplyLayout = true;

    //This is for remote user
    public String userOnlineIndicationTitle = "online";

    // This is for connection online
    public String onlineIndicationTitle = null;
    public String typingIndicationTitle = "typing...";
    public String joinedIndicationTitle = "joined";
    public String offlineIndicationTitle = "Not connected";
    public String connectingIndicationTitle = "Connecting...";
    public String noNetworkIndicationTitle = "No Network";
    public String suspendIndicationTitle = "Service Suspended";

    public String recentUsersTitle = "Recent Users";
    public String allUsersTitle = "All Users";
    public String groupMembersTitle = "Group Members";

    public String cancelTitle = "Cancel";
    public String unknownTitle = "Unknown";
    public String forwardedTitle = "Forwarded";
    public String editedTitle = "Edited";

    public String missedVoiceCallTitle = "Missed voice call";
    public String missedVideoCallTitle = "Missed video call";
    public String deletedMessageTitle = "This message was deleted";

    public String clearMessagesTitle = "Clear Messages?";
    public String deleteMessagesTitle = "Delete Messages?";
    public String deleteForEveryoneTitle = "Delete For Everyone";
    public String deleteForMeTitle = "Delete For Me";
    public String deleteTitle = "Delete";
    //public String deleteAlertTitle = "Delete chat with %@";

    public String emptyUserListMessage = "No Messages";
    public String emptyMessageListMessage = "You do not have any messages. Click on the message button above to start a conversation";
    public String emptySearchListMessage = "Your search returned no results";

    public boolean forceScrollToLatest = false;
    public String messageAlertOnScrolledView = "New message received";

    public String videoFromRecorderTitle = "Video Recorder";
    public String videoFromGalleryTitle = "Gallery";
    public String videoSelectTitle = "Send your video from?";

    public String sendCurrentLocation = "Send Current Location";
    public String sendAnotherLocation = "To send other location, long press on the map and then click on the address";

    public String sendLimitReachedTitle = "You've Hit the Rate Limit";
    public String sendLimitReached = "You are sending messages too quickly. Please wait a moment before sending more to avoid rate limits";

    public boolean showMonthFirst = MesiboDateTime.isMonthFirstDateFormat();
    public String today = "Today";
    public String yesterday = "Yesterday";
    public String at = "at";

    public String yes = "Yes";
    public String no = "No";
    public String cancel = "Cancel";

    public boolean showRecentInForward = true;
    protected boolean hideEmptyProfilesInUserlist = false;
    public boolean mConvertSmilyToEmoji = true;

    public int[] mLetterTitleColors = null;

    public int mToolbarColor = 0xFF00868b;
    public int mActionbarColor = 0xFF00868b;
    public int mStatusbarColor = mToolbarColor;
    public int mToolbarTextColor = 0;
    public int mUserListTypingIndicationColor = 0xFF499944;
    public int mUserListStatusColor = 0xFF868686;
    public int mUserListBackgroundColor = 0xFFFFFFFF;
    public int mUserListSelectedBackgroundColor = 0xFFDDDDDD;
    public int mUserListBottomButtonColor = mToolbarColor;

    public int messageBackgroundColorForMe = 0xffd5f5ec;
    //public int messageBackgroundColorForMe = 0xf00b9c0;
    public int titleBackgroundColorForMe = 0xffbff0e2;
    //public int messageBackgroundColorForPeer = 0xfff3f3f3;
    public int messageBackgroundColorForPeer = 0xfffafafa;
    public int titleBackgroundColorForPeer = 0xffeeeeee;
    public int messagingBackgroundColor = 0xffe9e9e9;
    public int progressbarColor = 0xff00868b;

    public long mMaxImageFileSize = 300 * 1024;
    public long mMaxVideoFileSize = 20 * 1024 * 1024;

    public int mMessagingFragmentLayout = 0;
    public int mUserListFragmentLayout = 0;

    public boolean mEnableNotificationBadge = false;

    public boolean autoAdjustCardRadius = true;

    public int mVerticalImageWidth = 65;
    public int mHorizontalImageWidth = 75;

    public int clearMessageMode = 0;
    public boolean clearedMessageContactTimestamp = true;

    public boolean showAddressInProfileView = false;
    public boolean showAddressAsPhoneInProfileView = true;
    public boolean showLastSeenInProfileView = true;
    public boolean showGroupMembersInProfileView = true;

    // this can be null or "", null by default, "" if not found
    protected String mGoogleApiKey = null;

    public MesiboUiDefaults() {
    }
}
