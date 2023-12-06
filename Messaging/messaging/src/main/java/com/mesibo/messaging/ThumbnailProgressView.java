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

package com.mesibo.messaging;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mesibo.api.MesiboMessage;
import com.mesibo.messaging.AllUtils.MyTrace;

import static com.mesibo.messaging.MesiboConfiguration.PROGRESSVIEW_DOWNLOAD_SYMBOL;
import static com.mesibo.messaging.MesiboConfiguration.PROGRESSVIEW_UPLOAD_SYMBOL;

public class ThumbnailProgressView extends FrameLayout  {
    private int mCurrentState=-1;
    LayoutInflater mInflater=null;
    FrameLayout mFrameLayout=null;
    ImageView mPictureView=null;
    ProgressBar mProgressBar=null;
    Button mTransferButton = null;
    private MessageData mData = null;
    private String mFileSize = null;

    // Default, In this state, we just need to display thumbnail, button and progress gone
    public final static int STATE_DISPLAY   = 0;
    // In this state, we teed to display thumbnail and progress, button gone
    public final static int STATE_INPROGRESS   = 1;
    // In this state, we teed to display thumbnail and progress and button
    public final static int STATE_PROMPT   = 2;

    public ThumbnailProgressView(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();

    }
    public ThumbnailProgressView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public ThumbnailProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {

        mFrameLayout = (FrameLayout) mInflater.inflate(R.layout.thumbnail_progress_view, this, true);
        mPictureView = (ImageView) mFrameLayout.findViewById(R.id.imageView);
        mProgressBar = (ProgressBar) mFrameLayout.findViewById(R.id.progressBar);

        if(Build.VERSION.SDK_INT > 21) {
            mProgressBar.setProgressTintList(ColorStateList.valueOf(MesiboUI.getUiDefaults().progressbarColor));
        }


        mProgressBar.getIndeterminateDrawable().setColorFilter(MesiboUI.getUiDefaults().mToolbarColor, android.graphics.PorterDuff.Mode.MULTIPLY);


        mTransferButton = (Button) mFrameLayout.findViewById(R.id.transferButton);
        setState(STATE_DISPLAY);

    }


    public void setData(MessageData data) {
        boolean changed = (mData != data);
        mData = data;

        Bitmap image =  mData.getImage();
        if(null == image) {
            return;
        }

        MesiboMessage m = mData.getMesiboMessage();

        if(!m.isFileTransferInProgress() || changed) {
            mPictureView.setImageBitmap(image);
        }

        if(m.isFileReady() || m.openExternally) {
            setState(STATE_DISPLAY);  //Location or sticker
            return;
        }

        if(m.isFileTransferInProgress()) {
            setState(STATE_INPROGRESS);
            mProgressBar.setMax(100);

            int progress = m.getProgress();

            if(progress <= 0)
                mProgressBar.setIndeterminate(true); //so that we can show spinning at start
            else {
                mProgressBar.setIndeterminate(false);
                mProgressBar.setProgress(progress);
            }
            return;
        }

        if(!m.isFileTransferRequired()) {
            return;
        }

        mFileSize = Utils.getFileSizeString(m.getFileSize());

        if(m.isFileTransferFailed()) {
            mFileSize = mFileSize + " (Retry)";
        }

        Drawable img = null;
        //TBD, need to cache
        if(m.isDownloadRequired())
            img = getContext().getResources().getDrawable(PROGRESSVIEW_DOWNLOAD_SYMBOL);
        else if(m.isUploadRequired())
            img = getContext().getResources().getDrawable(PROGRESSVIEW_UPLOAD_SYMBOL);

        mTransferButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        mTransferButton.setText(mFileSize);
        setState(STATE_PROMPT); // for idle or retry later stage

    }

    public void setState(int state) {
        if(state == mCurrentState)
            return;

        mCurrentState = state;
        if(STATE_DISPLAY == state) {
            mTransferButton.setVisibility(GONE);
            mProgressBar.setVisibility(GONE);
        }
        else  if(STATE_INPROGRESS == state) {
            mProgressBar.setVisibility(VISIBLE);
            mTransferButton.setVisibility(GONE);
        } else {
            mTransferButton.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }

    }

    public void setImage(Bitmap bmp) {
        mPictureView.setImageBitmap(bmp);
    }
}
