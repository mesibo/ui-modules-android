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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.mesibo.messaging.AllUtils.MyTrace;

public class MesiboUI {

    public static abstract class MenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        public MesiboScreen screen;
        public MenuItemClickListener(MesiboScreen scr) {
            this.screen = scr;
        }
    }

    public static abstract class ViewOnClickListener implements View.OnClickListener {
        public MesiboScreen screen;
        public ViewOnClickListener(MesiboScreen scr) {
            this.screen = scr;
        }
    }

    public static class MesiboScreenOptions {
        public long sid = 0;
        public Object userObject = 0;
    }

    public static class MesiboScreen {
        @NonNull public Context parent;
        public Toolbar toolbar = null;
        public RecyclerView table = null;
        public boolean userList = false;
        public Menu menu = null;
        public TextView title = null;
        public TextView subtitle = null;
        public View titleArea = null;
        public MesiboScreenOptions options = null;

    }

    public static class MesiboUserListScreen extends MesiboScreen {
        public int mode = 0;
        public SearchView search = null;

        MesiboUserListScreen() {
            userList = true;
        }
    }

    public static class MesiboMessageScreen extends MesiboScreen {
        public MesiboProfile profile  = null;
        public ImageView profileImage = null;
        public EditText editText = null;
    }

    public static class MesiboRow {
        public View row;
        public MesiboMessage message;
        public MesiboScreen screen;
        public ViewGroup viewGroup;
        public MesiboRecycleViewHolder viewHolder;

        protected void reset() {
            // screen = null - do not reset
            viewGroup = null;
            message = null;
            row = null;
        }
    }

    public static class MesiboUserListRow extends MesiboRow{
        public MesiboProfile profile  = null;
        public TextView name = null;
        public TextView subtitle = null;
        public TextView timestamp = null;
        public ImageView image = null;

        protected void reset() {
            super.reset();
            profile = null;
            name = null;
            subtitle = null;
            timestamp = null;
            image = null;
        }
    }

    public static class MesiboMessageRow extends MesiboRow {
        public TextView title = null;
        public TextView subtitle = null;
        public TextView messageText = null;
        public TextView filename = null;
        public TextView filesize = null;
        public TextView name = null;
        public TextView heading = null;
        public TextView footer = null;
        public TextView timestamp = null;
        public ImageView image = null;
        public ImageView status = null;
        public View replyView = null;
        public View titleView = null;
        public boolean selected = false;

        protected void reset() {
            super.reset();
            title = null;
            subtitle = null;
            messageText = null;
            filename = null;
            filesize = null;
            name = null;
            heading = null;
            footer = null;
            timestamp = null;
            image = null;
            status = null;
            replyView = null;
            titleView = null;
            selected = false;
        }
    }

    //public static final String BUNDLE = "bundle";
    public static final String MESSAGE_ID = "mid";
    public static final String GROUP_ID = "groupid";
    public static final String GROUP_NAME = "groupname";
    public static final String PEER = "peer";

    private static MesiboUiDefaults mConfig = new MesiboUiDefaults();
    public static MesiboUiDefaults getUiDefaults() {
        return mConfig;
    }

    public static MesiboUIListener mListener = null;
    public static void setListener(MesiboUIListener listner) {
        mListener = listner;
    }
    public static MesiboUIListener getListener() {
        return mListener;
    }


    public static class MesiboUserListScreenOptions extends MesiboScreenOptions {
        public int mode = MesiboUserListFragment.MODE_MESSAGES;
        public boolean startInBackground = false;
        public boolean keepRunning = false;
        public int activityFlags = 0;
        public long forwardId = 0;
        public long forwardIds[] = null;
        public boolean forwardAndClose = false;
        public String forwardedMessage = null;
        public long groupid = 0;
        public String readQuery = null;
        public String searchQuery = null;
    }

    public static class MesiboMessageScreenOptions extends MesiboScreenOptions {
        public MesiboProfile profile = null;
        public long forwardId = 0;
        public boolean readOnly = false;
    }

    public static void launchUserList(Context context, MesiboUserListScreenOptions opts) {
        MesiboUIManager.launchContactActivity(context, opts);
    }

    public static void launchMessaging(Context context, MesiboMessageScreenOptions opts) {
        MesiboUIManager.launchMessagingActivity(context, opts);
    }

    public static void launchForwardActivity(Context context, String forwardMessage, boolean forwardAndClose) {
        MesiboUIManager.launchForwardActivity(context, forwardMessage, forwardAndClose);
    }

    public static void showEndToEndEncryptionInfo(Context context, String address, long groupid) {
        MesiboUIManager.showEndToEndEncryptionInfo(context, address, groupid);
    }

    public static void showEndToEndEncryptionInfoForSelf(Context context) {
        MesiboUIManager.showEndToEndEncryptionInfo(context, null, 0);
    }

    public static String version() {
        return BuildConfig.BUILD_VERSION;
    }

    public static void setTestMode(boolean testMode) {
        MesiboUIManager.setTestMode(testMode);
    }

    public static void setEnableProfiling(boolean enabled) {
        MyTrace.setEnable(enabled);
    }
}
