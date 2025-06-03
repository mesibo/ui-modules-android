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
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowManager;

import com.mesibo.api.Mesibo;
import com.mesibo.mediapicker.MediaPicker;

import java.lang.ref.WeakReference;

import static com.mesibo.messaging.MesiboUserListFragment.MODE_FORWARD;

public class MesiboUIManager {

    private static  boolean mTestMode = false;

    public static void setTestMode(boolean testMode) {
        mTestMode = testMode;
    }

    public static void launchContactActivity(Context context, MesiboUI.MesiboUserListScreenOptions opts) {
        Intent intent = new Intent(context, MesiboUserListActivity.class);

        if(opts.activityFlags > 0)
            intent.setFlags(opts.activityFlags);

        Mesibo.setIntentObject(intent, opts);

        context.startActivity(intent);
    }

    public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, String forwardMessage) {
        MesiboUI.MesiboUserListScreenOptions opts = new MesiboUI.MesiboUserListScreenOptions();
        opts.sid = 1; // just for debug
        opts.activityFlags = flag;
        opts.forwardId = forwardid;
        opts.mode = selectionMode;
        opts.startInBackground = startInBackground;
        opts.keepRunning = keepRunning;
        opts.forwardedMessage = forwardMessage;
        launchContactActivity(context, opts);
    }

    public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning) {
        launchContactActivity(context, forwardid, selectionMode, flag, startInBackground, keepRunning, null);
    }

    public static void launchContactActivity(Context context, int selectionMode, long[] mids) {
        Intent intent = new Intent(context, MesiboUserListActivity.class);

        MesiboUI.MesiboUserListScreenOptions opts = new MesiboUI.MesiboUserListScreenOptions();
        opts.sid = 2; // just for debug
        opts.mode = selectionMode;
        opts.forwardIds = mids;
        Mesibo.setIntentObject(intent, opts);

        context.startActivity(intent);
    }

    public static void launchForwardActivity(Context context, String forwardMessage, boolean forwardAndClose) {
        Intent intent = new Intent(context, MesiboUserListActivity.class);

        MesiboUI.MesiboUserListScreenOptions opts = new MesiboUI.MesiboUserListScreenOptions();
        opts.sid = 3; // just for debug
        opts.mode = MODE_FORWARD;
        opts.forwardedMessage = forwardMessage;
        opts.forwardAndClose = forwardAndClose;
        Mesibo.setIntentObject(intent, opts);

        context.startActivity(intent);
    }

    public static void showEndToEndEncryptionInfo(Context context, String peer, long groupid) {
        Intent intent = new Intent(context, MesiboEndToEndEncryptionActivity.class);
        intent.putExtra(MesiboUI.PEER, peer);
        intent.putExtra(MesiboUI.GROUP_ID, groupid);
        context.startActivity(intent);
    }


    public static void launchGroupActivity(Context context, long groupid) {
        Intent intent = new Intent(context, CreateNewGroupActivity.class);
        if (groupid > 0)
            intent.putExtra(MesiboUI.GROUP_ID, groupid);
        context.startActivity(intent);
    }

    public static void launchPictureActivity(Context context, String title, String filePath) {
        if(null == context) return;

        MediaPicker.launchImageViewer((AppCompatActivity) context, filePath);
        //MediaPicker.launchImageViewer((AppCompatActivity)context, filePath);

    }

    public static void launchMessagingActivity(Context context, MesiboUI.MesiboMessageScreenOptions opts) {
        Intent intent = new Intent(context, MesiboMessagingActivity.class);
        Mesibo.setIntentObject(intent, opts);
        context.startActivity(intent);

        if (null != mMessagingActivityNew) {
            MesiboMessagingActivity oldActivity = mMessagingActivityNew.get();
            if (null != oldActivity)
                oldActivity.finish();
        }
    }

    public static void launchPlacePicker(Context context, Intent intent, int REQUEST_CODE) {
        ((AppCompatActivity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    public static void launchImageEditor(Context context, int type, int drawableid, String title, String filePath, boolean showEditControls, boolean showTitle, boolean showCropOverlay, boolean squareCrop, int maxDimension, MediaPicker.ImageEditorListener listener) {
        MediaPicker.launchEditor((AppCompatActivity) context, type, drawableid, title, filePath, showEditControls, showTitle, showCropOverlay, squareCrop, maxDimension, listener);
    }

    private static WeakReference<MesiboMessagingActivity> mMessagingActivityNew = null;

    public static void setMessagingActivity(MesiboMessagingActivity activity) {
        mMessagingActivityNew = new WeakReference<MesiboMessagingActivity>(activity);
    }

    protected static boolean enableSecureScreen(AppCompatActivity activity) {
        if(!Mesibo.isSetSecureScreen()) return false;

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        return true;
    }

}
