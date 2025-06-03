/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2024 mesibo                                              *
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
import static com.mesibo.messaging.MesiboConfiguration.ATTACHMENT_STRING;
import static com.mesibo.messaging.MesiboConfiguration.AUDIO_STRING;
import static com.mesibo.messaging.MesiboConfiguration.IMAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.LOCATION_STRING;
import static com.mesibo.messaging.MesiboConfiguration.VIDEO_STRING;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboDateTime;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.mesibo.messaging.AllUtils.LetterTileProvider;

public class UserData {

    private Bitmap mUserImage = null;
    private Bitmap mUserImageThumbnail = null;

    private String mLastMessage;

    private MesiboProfile mUser;
    private boolean mFixedImage = false;
    private int mUserListPosition = -1;
    private MesiboProfile mTypingProfile = null;
    private boolean mDeleted = false;
    private Drawable icon = null;
    private MesiboMessage msg = null;


    public UserData(MesiboProfile user){
        mUser = user;
        mUserImage = null;
        mUserImageThumbnail = null;

        mLastMessage ="";
    }

    private String appendNameToMessage(MesiboMessage params, String message) {
        String name = params.peer;
        if(null != params.profile && null != params.profile.getFirstName())
            name = params.profile.getFirstName();

        if(TextUtils.isEmpty(name)) return message;

        if(name.length() > 12)
            name = name.substring(0, 12);

        return name + ": " + message;
    }

    public void setMessage(MesiboMessage message){
        this.msg = message;

        String str = msg.message;
        if(msg.isDeleted()) {
            str = MesiboUI.getUiDefaults().deletedMessageTitle;
            setMessage(str);
	    return;
        }

        if(TextUtils.isEmpty(str)) str = msg.title;
        if(TextUtils.isEmpty(str)) {
            if (msg.hasImage())
                str = IMAGE_STRING;
            else if (msg.hasVideo())
                str = VIDEO_STRING;
            else if (msg.hasAudio())
                str = AUDIO_STRING;
            else if (msg.hasFile())
                str = ATTACHMENT_STRING;
            else if (msg.hasLocation())
                str = LOCATION_STRING;
        }


        if(msg.isGroupMessage() && msg.isIncoming()) {
            str = appendNameToMessage(msg, str);
        }

        if(msg.isMissedCall()) {
            str = MesiboUI.getUiDefaults().missedVideoCallTitle;
            if(msg.isVoiceCall())
                str = MesiboUI.getUiDefaults().missedVoiceCallTitle;
        }

        setMessage(str);
    }

    public void setMessage(String message){
        this.mLastMessage = message;
    }

    public String getLastMessage() {
        if(TextUtils.isEmpty(mLastMessage)) return "";
        if(false && mLastMessage.length() >= 36)
            return mLastMessage.substring(0, 33) + "..."; //TBD, this is not needed, let layout handle this
        return mLastMessage;
    }

    public MesiboMessage getMessage() {
        return msg;
    }

    public String getPeer() {
        return mUser.address;
    }
    public long getGroupId() { return mUser.groupid; }
    public long getmid() {
        if(null == msg) return 0;
        return msg.mid;
    }

    public boolean isDeletedMessage() {
        if(null == msg) return mDeleted;
        return (mDeleted || msg.isDeleted());
    }

    public void setDeletedMessage(boolean deleted) {
        mDeleted = deleted;
    }

    public void setTypingUser(MesiboProfile user) {
        mTypingProfile = user;
    }

    public void setUser(MesiboProfile user) {
        mUser = user;
    }

    public void setFixedImage(boolean fixed) {
        mFixedImage = fixed;
    }

    public Integer getUnreadCount() {
        return mUser.getUnreadMessageCount();
    }

    public void setImageThumbnail(Bitmap b) {
        mUserImageThumbnail = b;
    }

    public Bitmap getImage() {
        return mUserImage;
    }

    public void setImage(Bitmap b) {
        this.mUserImage = b;
    }

    public Bitmap getThumbnail(LetterTileProvider tileProvider) {
        Bitmap tn = mUser.getThumbnail();
        if(null != tn) return tn;

        if(null != mUserImageThumbnail)
            return mUserImageThumbnail;

        if(MesiboUI.getUiDefaults().useLetterTitleImage && null != tileProvider) {

            mUserImageThumbnail = tileProvider.getLetterTile(getUserName(), true);
        }
        else if(mUser.groupid > 0)
            mUserImageThumbnail = MesiboImages.getDefaultGroupBitmap();
        else
            mUserImageThumbnail = MesiboImages.getDefaultUserBitmap();

        return mUserImageThumbnail;
    }

    public Bitmap getThumbnail() {
        return getThumbnail(null);
    }

    public String getImagePath() {
        return mUser.getImageOrThumbnailPath();
    }

    public Integer getStatus() {
        if(null != msg) return msg.getStatus();
        return Mesibo.MSGSTATUS_RECEIVEDREAD;
    }

    public String getTime() {
        if(null == msg) return "";
        MesiboDateTime ts = msg.getTimestamp();
        if(ts.isToday()) return ts.getTime(false);

        MesiboUiDefaults opts = MesiboUI.getUiDefaults();
        return ts.getDate(opts.showMonthFirst, opts.today, opts.yesterday, true);
    }

    public String getUserName() {
        String name = mUser.getName();
        if(TextUtils.isEmpty(name))
            name = mUser.address;

        if(name.length() > 24)
            name = name.substring(0, 20) + "...";
        return name;
    }


    public boolean isTyping() {
        if(null != mTypingProfile) return mTypingProfile.isTyping(mUser.getGroupId());
        return mUser.isTyping(mUser.getGroupId());
    }

    public MesiboProfile getTypingProfile() {
        return mTypingProfile;
    }

    public void setUserListPosition(int position) {
        mUserListPosition = position;
    }

    public int getUserListPosition() {
        return mUserListPosition;
    }

    public static UserData getUserData(MesiboMessage params) {
        if(null == params || null == params.profile)
            return null;


        return getUserData(params.profile);
    }

    public static UserData getUserData(MesiboProfile profile) {
        if(null == profile)
            return null;

        UserData d = (UserData) profile.other;
        if(null == d) {
            d = new UserData(profile);
            profile.other = d;
        }

        return d;
    }

}

