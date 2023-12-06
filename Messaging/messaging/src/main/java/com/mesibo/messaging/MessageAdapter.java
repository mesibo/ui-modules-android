/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2023 mesibo                                              *
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
* Documentation: https://mesibo.com/documentation/                            *
*                                                                             *
* Source Code Repository: https://github.com/mesibo/                          *
*******************************************************************************/

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import android.widget.ProgressBar;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboEndToEndEncryption;
import com.mesibo.api.MesiboMessage;
import com.mesibo.messaging.AllUtils.MyTrace;

import static com.mesibo.api.Mesibo.MSGSTATUS_CALLMISSED;
import static com.mesibo.api.Mesibo.MSGSTATUS_CUSTOM;
import static com.mesibo.api.Mesibo.MSGSTATUS_E2E;
import static com.mesibo.api.Mesibo.MSGSTATUS_INVISIBLE;
import static com.mesibo.api.Mesibo.MSGSTATUS_RECEIVEDREAD;
import static com.mesibo.api.Mesibo.MSGSTATUS_SENT;
import static com.mesibo.messaging.MesiboConfiguration.headerCellBackgroundColor;
import static com.mesibo.messaging.MesiboConfiguration.otherCellsBackgroundColor;
import static com.mesibo.messaging.Utils.createRoundDrawable;


public class MessageAdapter extends SelectableAdapter <RecyclerView.ViewHolder> {
    private List<MessageData> mChatList = null;
    private MessagingAdapterListener mListener = null;
    private int mDisplayMsgCnt = 0;
    private int mTotalMessages = 0;
    private int mcellHeight = 0;
    private ProgressBar mProgress = null;
    private ImageView mImageVu = null;
    private String mDateCoin = null;
    int mOriginalId = 0;
    private Context mContext = null;

    private MessageViewHolder.ClickListener clickListener = null;
    private MesiboUIListener mUiListener = null;
    private MesiboUI.MesiboMessageScreen mScreen = null;
    private MesiboUI.MesiboMessageRow mMsgRow = null;
    private MesiboMessage mMsg = null;


    public interface MessagingAdapterListener {
        boolean isMoreMessage();
        void loadMoreMessages();
        //void showMessgeVisible();
        void showMessageInvisible();
    }

    public MessageAdapter(MesiboUI.MesiboMessageScreen screen, MessagingAdapterListener listener, List<MessageData> ChatList, MessageViewHolder.ClickListener cl1, MesiboUIListener uiListner) {
        mScreen = screen;
        mMsgRow = new MesiboUI.MesiboMessageRow();
        mMsgRow.screen = mScreen;

        mMsg = new MesiboMessage();

        mContext = screen.parent;
        this.mChatList = ChatList;
        this.mListener = listener;
        this.clickListener = cl1;
        mUiListener = uiListner;
        mDisplayMsgCnt = 30;
        mDateCoin = "";
        mTotalMessages = mChatList.size();
        mDisplayMsgCnt = mTotalMessages;
        mcellHeight = 0;
    }

    @Override
    public int getItemViewType(int position) {
        MessageData data = mChatList.get(position);
        MesiboMessage m = data.getMesiboMessage();

        if(false && !data.isVisible())
            return MSGSTATUS_INVISIBLE;

        if(m.isDate())
            return m.getStatus();

        if(m.isHeader())
            return m.getStatus();

        if(m.isCustom())
            return MSGSTATUS_CUSTOM;

        if(m.isMissedCall())
            return MSGSTATUS_CALLMISSED;

        if(m.isEndToEndEncryptionStatus())
            return MSGSTATUS_E2E;

        if (m.isIncoming())
            return MSGSTATUS_RECEIVEDREAD;
        else
            return MSGSTATUS_SENT;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        MyTrace.start("Messaging-CVH");

        MesiboRecycleViewHolder holder = null;

        mMsg.setStatus(viewType); 

        if(null != mUiListener) {
            mMsgRow.reset();
            mMsgRow.message = mMsg;
            mMsgRow.viewGroup = viewGroup;
            holder = mUiListener.MesiboUI_onGetCustomRow(mScreen, mMsgRow);

            if(null != holder)
                holder.setCustom(true);
        }

        if (null == holder) {
            if (mMsg.isInvisible()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_empty_view, viewGroup, false);
                holder = new EmptyViewHolder(v);

            } else if (mMsg.isDate()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_date_view, viewGroup, false);
                holder = new DateViewHolder(v);
            } else if (mMsg.isHeader()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, headerCellBackgroundColor, false);
            } else if (mMsg.isMissedCall()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, otherCellsBackgroundColor, true);
            } else if (mMsg.isEndToEndEncryptionStatus()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, headerCellBackgroundColor, true);
            } else if (mMsg.isOutgoing()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.me_user, viewGroup, false);
                holder = new MessageViewHolder(viewType, v, clickListener);
            } else if (mMsg.isIncoming()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_user, viewGroup, false);
                holder = new MessageViewHolder(viewType, v, clickListener);
            } else if(mMsg.isCustom()) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, otherCellsBackgroundColor, false);
            }
        }

        if(null != holder)
            holder.setType(viewType);

        MyTrace.stop();
        if(null != holder)
            holder.setAdapter(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        MesiboUiDefaults opts = MesiboUI.getUiDefaults();

        MyTrace.start("Messaging-BVH");
        if (i < 20) {
            // if(mDisplayMsgCnt != mTotalMessages)
            if (mListener.isMoreMessage()) {

                // we now don't show button instead we continuously load
                mListener.loadMoreMessages();
            }
        } else {
            mListener.showMessageInvisible();
        }

        MesiboRecycleViewHolder h = (MesiboRecycleViewHolder) holder;
        int type = h.getType();
        h.setItemPosition(i);

        MessageData cm = mChatList.get(i);
        cm.setDirty(false);

        MesiboMessage msg = cm.getMesiboMessage();

        // it was created by user so let user handle
        if(h.getCustom()) {
            //Mesibo.MesiboMessage
            cm.setViewHolder(h);
            mMsgRow.reset();
            mMsgRow.message = msg;
            mMsgRow.viewHolder = h;

            if(null != mUiListener) {
                mUiListener.MesiboUI_onUpdateRow(mScreen, mMsgRow, true);
            }
            MyTrace.stop();
            return;
        }

        if(i > 0 && cm.getGroupId() > 0) {
            MessageData prevcm = mChatList.get(i-1);
            cm.checkPreviousData(prevcm);
        }

        if (msg.isInvisible()) {

        } else if (msg.isDate()) {
            DateViewHolder dvh = (DateViewHolder) holder;
            dvh.mDate.setText(cm.getDateStamp());

        } else if (msg.isHeader()) {
            SystemMessageViewHolder smvh = (SystemMessageViewHolder) holder;
            smvh.setText(opts.headerTitle, MesiboImages.getHeaderImage());

        } else if (msg.isEndToEndEncryptionStatus()) {
            SystemMessageViewHolder smvh = (SystemMessageViewHolder) holder;
            int messageType = cm.getMessageType();
            String msgtext = "";
            if(messageType == MesiboEndToEndEncryption.STATUS_ACTIVE) {
                msgtext = opts.e2eeActive;
            }
            else if(messageType == MesiboEndToEndEncryption.STATUS_IDENTITYCHANGED) {
                msgtext = opts.e2eeIdentityChanged;
            }
            else  {
                msgtext = opts.e2eeInactive;
            }

            msgtext = msgtext.replaceAll("AppName", Mesibo.getAppName());

            smvh.setText(msgtext, MesiboImages.getE2EEImage());
        } else if (msg.isMissedCall()) {
            SystemMessageViewHolder smvh = (SystemMessageViewHolder) holder;
            int messageType = cm.getMessageType();
            if((messageType&1) > 0) {
                smvh.setText(opts.missedVideoCallTitle + " " + opts.at + " " + cm.getTimestamp(), MesiboImages.getMissedVideoCallImage());
            } else {
                smvh.setText(opts.missedVoiceCallTitle + " " + opts.at + " " + cm.getTimestamp(), MesiboImages.getMissedVoiceCallImage());
            }
        } else if (msg.isIncoming() || msg.isOutgoing()) {

            MessageViewHolder mvh = (MessageViewHolder) holder;
            mvh.setData(cm, i, isSelected(i));
        }

        if(null != mUiListener) {
            mMsgRow.reset();
            mMsgRow.screen = mScreen;
            mMsgRow.message = cm.getMesiboMessage();
            mMsgRow.viewGroup = null;
            h.setRow(mMsgRow);
            mUiListener.MesiboUI_onUpdateRow(mScreen, mMsgRow, true);
        }

        MyTrace.stop();

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        MyTrace.start("Messaging-RVH");
        if(null == holder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        if(holder instanceof DateViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        if(holder instanceof SystemMessageViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        if(holder instanceof EmptyViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        if(!(holder instanceof MesiboRecycleViewHolder)) {
            super.onViewRecycled(holder);
            return;
        }

        MesiboRecycleViewHolder h = (MesiboRecycleViewHolder) holder;
        
        if(h.getCustom()) {
            super.onViewRecycled(holder);
            h.cleanup();
            MyTrace.stop();
            return;
        }

        MessageViewHolder mvh = (MessageViewHolder) holder;
        if(null != mvh)
            mvh.reset();

        super.onViewRecycled(holder);
        MyTrace.stop();
    }

    public static class DateViewHolder extends MesiboRecycleViewHolder {

        protected TextView mDate;
        private View mView = null;
        public DateViewHolder(View v) {
            super(v);
            mView = v;
            mDate = (TextView) v.findViewById(R.id.chat_date);
        }

        public void setRow(MesiboUI.MesiboRow row) {
            MesiboUI.MesiboMessageRow r = (MesiboUI.MesiboMessageRow) row;
            r.row = mView;
            r.messageText = mDate;
        }
    }

    // https://stackoverflow.com/questions/15352496/how-to-add-image-in-a-textview-text#65714637
    // https://stackoverflow.com/questions/25628258/align-text-around-imagespan-center-vertical/38788432#38788432

    public class VerticalImageSpan extends ImageSpan {

        public VerticalImageSpan(Drawable drawable) {
            super(drawable);
        }

        public VerticalImageSpan(Context context, Bitmap image) {
            super(context, image);
        }

        /**
         * update the text line height
         */
        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.descent - fmPaint.ascent;
                drawable.setBounds(0, 0, fontHeight, fontHeight);
                rect = drawable.getBounds();
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + fontHeight / 2;

                fontMetricsInt.ascent = centerY - drHeight / 2;
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = centerY + drHeight / 2;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        /**
         * see detail message in android.text.TextLine
         *
         * @param canvas the canvas, can be null if not rendering
         * @param text the text to be draw
         * @param start the text start position
         * @param end the text end position
         * @param x the edge of the replacement closest to the leading margin
         * @param top the top of the line
         * @param y the baseline
         * @param bottom the bottom of the line
         * @param paint the work paint
         */
        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {

            Drawable drawable = getDrawable();

            canvas.save();
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            drawable.setBounds(0, 0, fontHeight, fontHeight);
            int centerY = y + fmPaint.descent - fontHeight / 2;
            int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }

    }

    public class SystemMessageViewHolder extends MesiboRecycleViewHolder {

        protected TextView mTextView = null;
        protected ImageView mImageView = null;
        private Context mContext;
        private String mText = null;
        private Bitmap mImage = null;
        private View mView = null;

        public SystemMessageViewHolder(View v, Context context, int color, boolean showImage) {
            super(v);
            mView = v;
            mContext = null;
            mTextView = v.findViewById(R.id.system_msg_text);
            mImageView = v.findViewById(R.id.system_msg_icon);
            View layoutView = v.findViewById(R.id.system_msg_layout);
            createRoundDrawable(context, layoutView, color, 9);
        }


        void setSpannableText() {
            mTextView.setVisibility(View.VISIBLE);
            if(null == mImage) {
                mTextView.setText(mText);
                return;
            }

            //VerticalImageSpan v = new VerticalImageSpan(mContext, mImage);

            SpannableStringBuilder ssb = new SpannableStringBuilder(mText);
            ssb.setSpan(new VerticalImageSpan(mContext, mImage), 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mTextView.setText(ssb, TextView.BufferType.SPANNABLE);
        }

        public void setText(String text, Bitmap image) {
            mText = "  " + text;
            mImage = image;
            setSpannableText();
        }

        public void setText(String text, int drawable) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawable);
            setText(text, bitmap);
        }

        public void setRow(MesiboUI.MesiboRow row) {
            MesiboUI.MesiboMessageRow r = (MesiboUI.MesiboMessageRow) row;
            r.row = mView;
            r.messageText = mTextView;
            r.image = mImageView;
        }
    }

    public class EmptyViewHolder extends MesiboRecycleViewHolder {

        public EmptyViewHolder(View v) {
            super(v);
        }
    }
    public void addRow() {
        mDisplayMsgCnt++;
        mTotalMessages = mChatList.size();
    }

    public float getItemHeight() {
        return  mcellHeight;
    }

    public void clearSelections() {
        List<Integer> selection = getSelectedItems();
        clearSelectedItems();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }
    public String copyData() {
        String copiedData="";

        List<Integer> selection = getSelectedItems();
        for (Integer i : selection) {
            int index = i;
            MessageData cm = mChatList.get(index);
            copiedData += cm.getDisplayMessage();
            copiedData += "\n";
        }

        return copiedData;
    }

    public int globalPosition(int position) {

        return (  position);
    }

    public void updateStatus(int index){

        int position = index;
        notifyItemChanged(position);

    }

    @Override
    public int getItemCount() {
        int count = (null != mChatList ? mChatList.size() : 0);
        return count;
    }
}


