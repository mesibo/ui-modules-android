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

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;

import com.mesibo.api.MesiboFile;
import com.mesibo.emojiview.EmojiconTextView;

import static com.mesibo.api.MesiboMessage.TYPE_AUDIO;
import static com.mesibo.api.MesiboMessage.TYPE_VIDEO;
import static com.mesibo.messaging.MesiboConfiguration.DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.FAVORITED_INCOMING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.FAVORITED_OUTGOING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.NORMAL_INCOMING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.NORMAL_OUTGOING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.TOPIC_TEXT_COLOR_WITHOUT_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.TOPIC_TEXT_COLOR_WITH_PICTURE;

public class MessageView extends RelativeLayout {

    LayoutInflater mInflater = null;
    TextView mTitleView = null;
    TextView mSubTitleView = null;
    TextView mHeadingView = null;
    EmojiconTextView mMessageTextView = null;
    ThumbnailProgressView mPictureThumbnail = null;
    FrameLayout mPicLayout = null;
    FrameLayout mReplayContainer = null;
    RelativeLayout mPTTlayout = null;

    RelativeLayout mReplyLayout = null;
    TextView mReplyUserName;
    TextView mReplyMessage;
    ImageView mReplyImage;
    ImageView mAudioVideoLayer;
    View mMessageView = null;

    LinearLayout mTitleLayout = null;

    RelativeLayout.LayoutParams mPicLayoutParam;
    FrameLayout.LayoutParams mThumbnailParams;
    RelativeLayout.LayoutParams mTitleParams;
    RelativeLayout.LayoutParams mMsgParams;

    private static int mThumbailWidth = 0;
    private MessageData mData = null;
    private boolean hasImage = false;

    public MessageView(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();

    }

    public MessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {

        View v = mInflater.inflate(R.layout.message_view, this, true);
        mMessageView = v;
        mPicLayout = (FrameLayout)  v.findViewById(R.id.m_piclayout);
        mTitleLayout = (LinearLayout) v.findViewById(R.id.m_titlelayout);
        //mPictureThumbnail = (ThumbnailProgressView) v.findViewById(R.id.m_picture);
        mTitleView = (TextView) v.findViewById(R.id.m_ptitle);
        mSubTitleView = (TextView) v.findViewById(R.id.m_psubtitle);
        mHeadingView = (TextView) v.findViewById(R.id.m_pheading);
        mMessageTextView = (EmojiconTextView) v.findViewById(R.id.m_pmessage);
        mPTTlayout = (RelativeLayout) v.findViewById(R.id.message_layout);
        mReplayContainer = (FrameLayout)  v.findViewById(R.id.reply_container);

        if(false) {
            mReplyLayout = (RelativeLayout) v.findViewById(R.id.reply_layout);
            mReplyImage = (ImageView) v.findViewById(R.id.reply_image);
            mReplyUserName = (TextView) v.findViewById(R.id.reply_name);
            mReplyMessage = (TextView) v.findViewById(R.id.reply_text);
        }

        mPicLayoutParam = (RelativeLayout.LayoutParams)mPicLayout.getLayoutParams();

        mTitleParams = (RelativeLayout.LayoutParams)mTitleLayout.getLayoutParams();
        mMsgParams = (RelativeLayout.LayoutParams) mMessageTextView.getLayoutParams();

    }

    public void loadImageView() {
        if(null != mPictureThumbnail)
            return;

        View v = mInflater.inflate(R.layout.thumbnail_progress_view_layout, mPicLayout, true);
        mPictureThumbnail = (ThumbnailProgressView) v.findViewById(R.id.m_picture);
        mAudioVideoLayer = (ImageView) mPictureThumbnail.findViewById(R.id.audio_video_layer);
        mAudioVideoLayer.setVisibility(GONE);

        if(null == mThumbnailParams)
            mThumbnailParams = (FrameLayout.LayoutParams)mPictureThumbnail.getLayoutParams();
    }

    public void loadReplyView() {
        if(null != mReplyLayout)
            return;

        View v = mInflater.inflate(R.layout.reply_layout, mReplayContainer, true);
        mReplyLayout = (RelativeLayout) v.findViewById(R.id.reply_layout);
        mReplyImage = (ImageView) v.findViewById(R.id.reply_image);
        mReplyUserName = (TextView) v.findViewById(R.id.reply_name);
        mReplyMessage = (TextView) v.findViewById(R.id.reply_text);
    }

    public void setData(MessageData data) {
        mData = data;

        ViewGroup.LayoutParams PTTParams = getLayoutParams();

        String title = mData.getTitle();
        String subtitle = mData.getSubTitle();
        String message = mData.getMessage();
        Bitmap thumbnail = mData.getImage();

        if(null != thumbnail) {
            loadImageView();
        } else {
            if(null != mPictureThumbnail)
                mPictureThumbnail.setVisibility(GONE);
            if(null != mAudioVideoLayer)
                mAudioVideoLayer.setVisibility(GONE);
            mPicLayout.setVisibility(GONE);
        }

        if(null != mAudioVideoLayer)
            mAudioVideoLayer.setVisibility(GONE);

        MesiboFile file = mData.getMesiboMessage().getFile();
        if(null != file && mAudioVideoLayer != null && (TYPE_AUDIO == file.type || TYPE_VIDEO == file.type)) {
            mAudioVideoLayer.setVisibility(VISIBLE);
        }

        if(mData.isReply()) {
            loadReplyView();
            mReplayContainer.setVisibility(VISIBLE);
            mReplyLayout.setVisibility(VISIBLE);
            if(null != mData.getReplyString()) {
                mReplyMessage.setText(mData.getReplyString());
            } else {
                mReplyMessage.setText("");
            }
            mReplyUserName.setText(mData.getReplyName());
            if(null != mData.getReplyBitmap()) {
                mReplyImage.setVisibility(VISIBLE);
                mReplyImage.setImageBitmap(mData.getReplyBitmap());
            }else {
                mReplyImage.setVisibility(GONE);
            }

        }else {
            if(null != mReplyLayout) {
                mReplyLayout.setVisibility(GONE);
                mReplayContainer.setVisibility(GONE);
            }


        }

        if(null != thumbnail) {
            int width = MesiboUI.getUiDefaults().mHorizontalImageWidth;
            if(thumbnail.getHeight() > thumbnail.getWidth())
                width = MesiboUI.getUiDefaults().mVerticalImageWidth;
            mThumbailWidth = (Mesibo.getDisplayWidthInPixel()*width)/100;

            PTTParams.width = mThumbailWidth;
            RelativeLayout.LayoutParams picLayoutParam = (RelativeLayout.LayoutParams)mPicLayout.getLayoutParams();
            FrameLayout.LayoutParams thumbnailParams = (FrameLayout.LayoutParams)mPictureThumbnail.getLayoutParams();
            mTitleLayout.setLayoutParams(mTitleParams);
            mMessageTextView.setLayoutParams(mMsgParams);

            thumbnailParams.width = mThumbailWidth;


            thumbnailParams.height = (mThumbailWidth*thumbnail.getHeight())/thumbnail.getWidth();

            if(!mData.hasThumbnail() || (!mData.isImageVideo() && thumbnail.getWidth() < 200 && thumbnail.getHeight() < 200)) {
                thumbnailParams.height = mThumbailWidth/4;
                thumbnailParams.width = mThumbailWidth/4;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(mTitleParams);
                    RelativeLayout.LayoutParams msgParams = new RelativeLayout.LayoutParams(mMsgParams);

                    if(null == message || message.length() < 32) {
                        titleParams.addRule(RelativeLayout.RIGHT_OF, R.id.m_piclayout);
                        titleParams.addRule(RelativeLayout.ALIGN_TOP, R.id.m_piclayout);
                        msgParams.addRule(RelativeLayout.BELOW, R.id.m_piclayout);
                    } else {
                        titleParams.addRule(RelativeLayout.RIGHT_OF, R.id.m_piclayout);
                        titleParams.addRule(RelativeLayout.ALIGN_TOP, R.id.m_piclayout);
                        titleParams.topMargin = thumbnailParams.topMargin + thumbnailParams.height/4;
                        msgParams.addRule(RelativeLayout.BELOW, R.id.m_titlelayout);
                    }
                    mTitleLayout.setLayoutParams(titleParams);
                    mMessageTextView.setLayoutParams(msgParams);
                }
            }


            mTitleView.requestLayout();
            mMessageTextView.requestLayout();

            picLayoutParam.height = thumbnailParams.height;
            picLayoutParam.width = thumbnailParams.width;
            mPicLayout.setLayoutParams(picLayoutParam);
            mPicLayout.requestLayout();
            mPictureThumbnail.setLayoutParams(thumbnailParams);
            mPictureThumbnail.requestLayout();

            mPictureThumbnail.setData(mData);
            mPicLayout.setVisibility(VISIBLE);
            mPictureThumbnail.setVisibility(VISIBLE);
            mMessageTextView.setTextColor(TOPIC_TEXT_COLOR_WITH_PICTURE);

            hasImage = true;
        }else {
            if(mData.isDeleted())
                mMessageTextView.setTextColor(DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE);
            else
                mMessageTextView.setTextColor(TOPIC_TEXT_COLOR_WITHOUT_PICTURE);

            hasImage = false;

        }

        if(mData.isForwarded() && !TextUtils.isEmpty(MesiboUI.getUiDefaults().forwardedTitle)) {
            mHeadingView.setVisibility(VISIBLE);
            mHeadingView.setText(MesiboUI.getUiDefaults().forwardedTitle);
        } else {
            mHeadingView.setVisibility(GONE);
        }

        if(!mData.isDeleted() && !TextUtils.isEmpty(title)) {
            mTitleView.setVisibility(VISIBLE);
            mTitleView.setText(title);
        }else {
            mTitleView.setVisibility(GONE);
        }

        if(!mData.isDeleted() && !TextUtils.isEmpty(subtitle)) {
            mSubTitleView.setVisibility(VISIBLE);
            mSubTitleView.setText(subtitle);
        }else {
            mSubTitleView.setVisibility(GONE);
        }

        if(!TextUtils.isEmpty(message)) {
            mMessageTextView.setVisibility(VISIBLE);
            boolean incoming = (data.getStatus() == Mesibo.MSGSTATUS_RECEIVEDREAD || data.getStatus() == Mesibo.MSGSTATUS_RECEIVEDNEW);
            if(data.getFavourite()) {
                if(incoming)
                    mMessageTextView.setText(message + " " + FAVORITED_INCOMING_MESSAGE_DATE_SPACE);
                else
                    mMessageTextView.setText(message + " " + FAVORITED_OUTGOING_MESSAGE_DATE_SPACE);

            } else {
                if (incoming)
                    mMessageTextView.setText(message + " " + NORMAL_INCOMING_MESSAGE_DATE_SPACE);
                else
                    mMessageTextView.setText(message + " " + NORMAL_OUTGOING_MESSAGE_DATE_SPACE);

            }

        }else {
            mMessageTextView.setVisibility(GONE);

        }

        if(false && null != thumbnail) {
            mTitleView.setVisibility(GONE);
            mMessageTextView.setVisibility(GONE);
        }

        if(null == thumbnail) {
            PTTParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        setLayoutParams(PTTParams);

        PTTParams = mMessageTextView.getLayoutParams();
        if(null != thumbnail) {
            PTTParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            PTTParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mMessageTextView.setLayoutParams(PTTParams);

        if(false && null != thumbnail) {
            mTitleView.setVisibility(VISIBLE);
            mMessageTextView.setVisibility(VISIBLE);

        }

    }

    public void setImage(Bitmap image) {
        loadImageView();
        mPictureThumbnail.setImage(image);
    }

}
