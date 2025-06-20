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
import android.graphics.Color;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.mesibo.api.Mesibo.MSGSTATUS_RECEIVEDREAD;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_COLOR_OVER_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_COLOR_WITHOUT_PICTURE;


public class MessageViewHolder extends MesiboRecycleViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    protected TextView otherUserName;
    protected TextView mTime;
    protected View mSelectedOverlay;
    protected ImageView mStatus;
    protected ImageView mFavourite;

    protected MessageView mMview;
    //protected int mType;
    protected MessageData mData = null;
    //private int mPosition = -1;
    //protected com.mesibo.messaging.ThumbnailProgressImageView mPicture;


    protected FrameLayout mBubble;

    private ClickListener listener;
    private LinearLayout m_titleLayout;
    private LinearLayout m_statusLayout;
    private boolean mSelected = false;
    private View mView = null;
    private View mMessageGap = null;

    private static float mDefaultRadius = -1;
    private static int mDefMsgViewMarginBottom = -1;
    private static int mDefStatusMarginBottom = -1;
    private static int mDefUserNameMarginBottom = -1;

    public MessageViewHolder(int type, View v, ClickListener listener) {

        super(v);
        mView = v;
        mData = null;
        //mMessage = (EmojiconTextView ) v.findViewById(R.id.m_message);
        m_statusLayout = (LinearLayout) v.findViewById(R.id.m_status_layout);
        mTime = (TextView) v.findViewById(R.id.m_time);
        mStatus = (ImageView) v.findViewById(R.id.m_status);
        mFavourite = (ImageView) v.findViewById(R.id.m_star);
        m_titleLayout = (LinearLayout) v.findViewById(R.id.m_titlelayout);

        mMview = (MessageView) v.findViewById(R.id.mesibo_message_view);

        mMessageGap = v.findViewById(R.id.message_gap);

        int bubbleid = R.id.outgoing_layout_bubble;
        if(MSGSTATUS_RECEIVEDREAD == type) {
            otherUserName = (TextView) v.findViewById(R.id.m_user_name);
            bubbleid = R.id.incoming_layout_bubble;
        }

        mBubble = (FrameLayout) v.findViewById(bubbleid);
        mSelectedOverlay = (View) v.findViewById(R.id.selected_overlay);

        this.listener = listener;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        //mMview.setOnClickListener(this);
    }

    public void setData(MessageData m, int position, boolean selected) {
        reset();

        mSelected = selected;
        setItemPosition(position);
        mData = m;
        mData.setViewHolder(this);

        mMessageGap.setVisibility(mData.hasExtraSpace()?View.VISIBLE:View.GONE);

        // need to check footer also when we use in UI
        boolean imageOnly = (TextUtils.isEmpty(m.getTitle()) && TextUtils.isEmpty(m.getSubTitle()) && TextUtils.isEmpty(m.getMessage()) && m.hasThumbnail());
        boolean messageOnly = (TextUtils.isEmpty(m.getTitle()) && TextUtils.isEmpty(m.getSubTitle()) && !TextUtils.isEmpty(m.getMessage()) && !m.hasThumbnail());

        if(MSGSTATUS_RECEIVEDREAD == getType()) {
            if (m.getGroupId() != 0 && m.isShowName()) {
                otherUserName.setVisibility(View.VISIBLE);
                otherUserName.setTextColor(m.getNameColor());
                otherUserName.setText(m.getUsername());

                ViewGroup.LayoutParams vlp = otherUserName.getLayoutParams();
                if (vlp instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vlp;

                    if(mDefUserNameMarginBottom < 0)
                        mDefUserNameMarginBottom = lp.bottomMargin;

                    if(m.hasThumbnail())
                        lp.bottomMargin = 12;
                    else
                        lp.bottomMargin = mDefUserNameMarginBottom; // -8 is default because of layout, we are setting again in case viewholder is reused
                    otherUserName.setLayoutParams(lp);
                }

            } else {
                otherUserName.setVisibility(View.GONE);
            }

            setupBackgroundBubble(mBubble, -1, MesiboUI.getUiDefaults().messageBackgroundColorForPeer);
            m_titleLayout.setBackgroundColor(MesiboUI.getUiDefaults().titleBackgroundColorForPeer);

            if(MesiboUI.getUiDefaults().e2eeIndicator && mData.isEncrypted()) {
                mStatus.setVisibility(View.VISIBLE);
            } else {
                mStatus.setVisibility(View.GONE);
            }

        } else {
            setupBackgroundBubble(mBubble,-1, MesiboUI.getUiDefaults().messageBackgroundColorForMe);
            m_titleLayout.setBackgroundColor(MesiboUI.getUiDefaults().titleBackgroundColorForMe);
            setupMessageStatus(mStatus, m.getStatus());
        }

        mTime.setText(m.getTimestamp());
        if(!TextUtils.isEmpty(m.getTitle()) || !TextUtils.isEmpty(m.getSubTitle())) {
            m_titleLayout.setVisibility(View.VISIBLE);
        } else {
            m_titleLayout.setVisibility(View.GONE);
        }

        if(null != m_statusLayout) {
            ViewGroup.LayoutParams vlp = m_statusLayout.getLayoutParams();

            if (vlp instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) vlp;

                if(mDefStatusMarginBottom < 0)
                    mDefStatusMarginBottom = lp.bottomMargin;

                if(imageOnly)
                    lp.bottomMargin = 16;
                else
                    lp.bottomMargin = mDefStatusMarginBottom;
                m_statusLayout.setLayoutParams(lp);
            }
        }

        mFavourite.setVisibility(m.getFavourite()?View.VISIBLE:View.GONE);
        if((null == mData.getTitle() || mData.getTitle().isEmpty()) && (null == mData.getMessage()
                || mData.getMessage().isEmpty())) {
            mTime.setTextColor(Color.parseColor(STATUS_COLOR_OVER_PICTURE));
            mFavourite.setColorFilter(Color.parseColor(STATUS_COLOR_OVER_PICTURE));

        } else {
            mFavourite.setColorFilter(Color.parseColor(STATUS_COLOR_WITHOUT_PICTURE));
            mTime.setTextColor(Color.parseColor(STATUS_COLOR_WITHOUT_PICTURE));
        }
        mMview.setData(mData);

        mSelectedOverlay.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        if(MesiboUI.getUiDefaults().autoAdjustCardRadius && mBubble instanceof CardView) {
            CardView cv = (CardView) mBubble;

            if(mDefaultRadius < 0)
                mDefaultRadius = cv.getRadius();

            if(messageOnly && m.getMessage().length() < 32)
                cv.setRadius(mDefaultRadius*0.8f);
            else
                cv.setRadius(mDefaultRadius);
        }

        if(null != mMview) {
            ViewGroup.LayoutParams vlp = mMview.getLayoutParams();

            if (vlp instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vlp;

                if(mDefMsgViewMarginBottom < 0)
                    mDefMsgViewMarginBottom = lp.bottomMargin;

                if(imageOnly)
                    lp.bottomMargin = 0;
                else
                    lp.bottomMargin = mDefMsgViewMarginBottom; // -8 is default because of layout, we are setting again in case viewholder is reused
                mMview.setLayoutParams(lp);
            }
        }
    }

    public void removeData() {
        if(null == mData)
            return;

        setItemPosition(-1);
        mData = null;
    }

    public void setRow(MesiboUI.MesiboRow row) {
        MesiboUI.MesiboMessageRow r = (MesiboUI.MesiboMessageRow) row;

        r.row = mView;
        r.title = mMview.mTitleView;
        r.subtitle = mMview.mSubTitleView;
        r.messageText = mMview.mMessageTextView;
        r.name = otherUserName;
        r.heading = mMview.mHeadingView;
        r.timestamp = mTime;
        if(null != mMview.mPictureThumbnail) {
            r.image = mMview.mPictureThumbnail.mPictureView;
        }
        r.status = mStatus;
        r.replyView = mMview.mReplyLayout;
        r.titleView = mMview.mTitleLayout;
        r.selected = mSelected;

    }

    protected void updateThumbnail(MessageData data) {
        if(null != mMview.mPictureThumbnail)
            mMview.mPictureThumbnail.setData(data);
    }

    @Override
    public void reset() {
        if(null == mData)
            return;

        MessageData d = mData;
        mData = null; //so that no loop condition
        setItemPosition(-1);
        d.setViewHolder(null);
    }


    public void setImage(Bitmap image) {
        mMview.setImage(image);
    }


    public void setupBackgroundBubble (FrameLayout fm, int resource, int color)
    {
        int pL1 = fm.getPaddingLeft();
        int pT1 = fm.getPaddingTop();
        int pR1 = fm.getPaddingRight();
        int pB1 = fm.getPaddingBottom();

        fm.setPadding(pL1, pT1, pR1, pB1);

        CardView v = (CardView) fm;
        v.setCardBackgroundColor(color);

    }

    // im will be null on receiver side as we do not have message status
    public void setupMessageStatus(ImageView im, int status) {
        im.setVisibility(mData.isDeleted()?View.GONE:View.VISIBLE);

        if(mData.isDeleted()) {
            //TBD, use a smaller image
            //im.setImageBitmap(MesiboImages.getDeletedMessageImage());
            return;
        }

        im.setImageBitmap(MesiboImages.getStatusImage(status));
    }


    @Override
    public void onClick(View v) {

        if (listener != null) {
            listener.onItemClicked(getAdapterPosition(), this);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            return listener.onItemLongClicked(getAdapterPosition(), this);
        }

        return false;
    }

    public interface ClickListener {
        public void onItemClicked(int position, MessageViewHolder holder);

        public boolean onItemLongClicked(int position, MessageViewHolder holder);
    }
}
