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

import static com.mesibo.messaging.MesiboConfiguration.YOU_STRING_IN_REPLYVIEW;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboDateTime;
import com.mesibo.api.MesiboMessage;

public class MessageData {

    private boolean mFavourite = false;

    private boolean mShowName = true;

    private MesiboRecycleViewHolder mViewHolder = null;
    private int mNameColor = 0xff777777;
    private MesiboMessage msg = null;
    private MesiboMessage repliedTo = null;
    private boolean mSelected = false;
    private boolean mDirty = false;
    private boolean mDeleted = false; // temporary
    private boolean mVisible = true;
    private Context mContext = null;
    private Bitmap image = null;
    private boolean imageDecoded = false;


    MessageData(Context context, MesiboMessage msg) {
        this.msg = msg;
        this.mContext = context;
        if(msg.isDate()) {
            MesiboUiDefaults opts = MesiboUI.getUiDefaults();
            msg.message = msg.getTimestamp().getDate(opts.showMonthFirst, opts.today, opts.yesterday);
        }
    }

    private Mesibo.MessageListener mMessageListener = null;
    void setMessageListener(Mesibo.MessageListener listener) {
        mMessageListener = listener;

    }


    void setViewHolder(MesiboRecycleViewHolder vh) {
        MesiboRecycleViewHolder pv = mViewHolder; //to avoid race condition in reset
        mViewHolder = null;
        if(null != pv) {
            pv.reset();
        }

        mViewHolder = vh;

    }

    public MesiboMessage getMesiboMessage() {
        return msg;
    }

    public int getPosition() {
        if(null != mViewHolder)
            return mViewHolder.getItemPosition();
        return -1;
    }

    MesiboRecycleViewHolder getViewHolder() {
        return mViewHolder;
    }


    public boolean hasThumbnail() {
        if(null == msg || isDeleted()) return false;

        return msg.hasThumbnail();
    }

    public boolean isImageVideo() {
        if(null == msg) return false;
        return (msg.hasImage() || msg.hasVideo());
    }


    public boolean isForwarded() {
        return msg.isForwarded();
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        setDirty(true);
    }

    public void toggleSelected() {
        mSelected = !mSelected;
        setDirty(true);
    }

    public boolean isDirty() {
        return mDirty;
    }

    public void setDirty(boolean dirty) {
        mDirty = dirty;
    }


    public long getGroupId() {
        return msg.groupid;
    }

    public String getPeer() {
        return msg.peer;
    }

    public String getUsername() {
        return msg.profile.getNameOrAddress();
    }

    public long getMid() {
        if(null == msg)
            return -1;
        return msg.mid;
    }

    public int getMessageType() {
        if(null == msg)
            return -1;
        return msg.type;
    }

    public String getTitle() {
        if(null == msg)
            return null;
        if(!TextUtils.isEmpty(msg.title)) return msg.title;
        if(msg.isDocument() || msg.isAudio()) {
            return msg.getFileName();
        }

        return null;
    }

    public String getSubTitle() {
        if(null == msg)
            return null;
        if(!TextUtils.isEmpty(msg.subtitle)) return msg.subtitle;
        if(msg.isDocument() || msg.isAudio()) {
            return Utils.getFileSizeString(msg.getFileSize());
            //return msg.getMimeType();
        }

        return null;
    }

    public String getMessage() {
        if(null == msg)
            return null;

        if(isDeleted())
            return MesiboUI.getUiDefaults().deletedMessageTitle;

        return msg.message;
    }

    public String getDisplayMessage() {
        String message = getMessage();
        if(TextUtils.isEmpty(message))
            message = msg.title;
        if(TextUtils.isEmpty(message))
            message = msg.subtitle;
        if(TextUtils.isEmpty(message))
            message = "";
        return message;
    }

    public String getTimestamp() {
        MesiboDateTime m = msg.getTimestamp();
        return m.getTime(false);
    }

    public int getStatus() {
        if(null == msg)
            return -1;
        return  msg.getStatus();
    }

    public boolean isDeleted() {
        return (mDeleted || msg.isDeleted());
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

    public boolean isVisible() {
        return mVisible;
    }

    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    public boolean isEncrypted() {
        return msg.isEndToEndEncrypted();
    }


    public Bitmap getImage() {
        if(null == msg || isDeleted()) return null;

        // this thumbnail can update so don't cache
        if(msg.hasThumbnail()) {
            //Mesibo.log("Message Data get thumbnail");
            return msg.getThumbnail();
        }

        if(null != image) return image;

        if(msg.openExternally) return null;

        if((msg.isFilePending() || msg.isFileReady()) && !imageDecoded) {
            imageDecoded = true;

            int drawableid = MesiboImages.getFileDrawable(msg.getFilePath());
            if(drawableid > 0)
                image = BitmapFactory.decodeResource(mContext.getApplicationContext().getResources(), drawableid);
        }

        return image;

    }

    public String getDateStamp() {
        MesiboUiDefaults opts = MesiboUI.getUiDefaults();
        MesiboDateTime d = msg.getTimestamp();
        return d.getDate(true, opts.today, opts.yesterday);
    }

    public void setStaus(int status) {
        if(null != msg)
            msg.setStatus(status);
        setDirty(true);
    }

    public void setFavourite(Boolean favourite) {
        mFavourite = favourite;
        setDirty(true);

    }
    public Boolean getFavourite() {
        return mFavourite;

    }

    public boolean isReply() {
        boolean reply = msg.isReply();
        if(!reply) return false;
        repliedTo = msg.getRepliedToMessage();
        if(null == repliedTo) return false;
        return true;
    }


    public String getReplyString() {
        if(!isReply()) return "";
        return repliedTo.message;
    }



    public Bitmap getReplyBitmap() {
        if(!isReply()) return null;
        return repliedTo.getThumbnail();
    }

    public String getReplyName() {
        if(!isReply()) return null;
        if(repliedTo.isIncoming())
            return repliedTo.profile.getNameOrAddress();
        return YOU_STRING_IN_REPLYVIEW;
    }

    public void setNameColor(int color) {
        mNameColor = color;
        setDirty(true);
    }

    public int getNameColor() {
        return mNameColor;
    }

    public void checkPreviousData(MessageData pd) {
        if(msg.isOutgoing() || !msg.isGroupMessage()) {
            mShowName = false;
            return;
        }

        if(!pd.getMesiboMessage().isIncoming() || pd.hasThumbnail()) {
            mShowName = true;
            return;
        }

        String prevPeer = pd.getPeer();
        if(null != prevPeer && null != msg.peer && prevPeer.equalsIgnoreCase(msg.peer)) {
            mShowName = false;
            return;
        }

        mShowName = true;
    }

    public boolean isShowName() {
        return mShowName;
    }
}
