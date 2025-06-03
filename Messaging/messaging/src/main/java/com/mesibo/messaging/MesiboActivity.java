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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mesibo.api.Mesibo;

import static com.mesibo.messaging.MesiboConfiguration.TOOLBAR_TEXT_COLOR;


public class MesiboActivity extends AppCompatActivity implements MesiboUserListFragment.FragmentListener {
    public static final String TAG="MesiboMainActivity";
    TextView contactsTitle = null;
    TextView contactsSubTitle = null;
    int mMode = 0;
    long mForwardId = 0;
    long[] mForwardIds;
    Bundle mEditGroupBundle = null;
    boolean mHideHomeBtn = false;
    boolean mKeepRunning = false;


    MesiboUI.Config mMesiboUIOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TBD, this must be fixed
        if(!Mesibo.isReady()) {
            finish();
            return;
        }

        MesiboImages.init(this);

        mMode = getIntent().getIntExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_MESSAGELIST);
        mForwardId = getIntent().getLongExtra(MesiboUI.MESSAGE_ID, 0);
        mForwardIds = getIntent().getLongArrayExtra(MesiboUI.MESSAGE_IDS);
        String forwardMessage = getIntent().getStringExtra(MesiboUI.MESSAGE_CONTENT);
        boolean forwardAndClose = getIntent().getBooleanExtra(MesiboUI.FORWARD_AND_CLOSE, false);
        mKeepRunning = getIntent().getBooleanExtra(MesiboUI.KEEP_RUNNING, false);
        if(getIntent().getBooleanExtra(MesiboUI.START_IN_BACKGROUND, false)) {
            moveTaskToBack(true);
        }

        if(mMode == MesiboUserListFragment.MODE_EDITGROUP)
            mEditGroupBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);

        MesiboUIManager.enableSecureScreen(this);

        setContentView(R.layout.activity_messages);
        mMesiboUIOptions = MesiboUI.getConfig();

        Toolbar toolbar = findViewById(R.id.message_toolbar);
        contactsSubTitle = findViewById(R.id.contacts_subtitle);
        contactsTitle =  findViewById(R.id.contacts_title);
        Utils.setTextViewColor(contactsTitle, TOOLBAR_TEXT_COLOR);
        Utils.setTextViewColor(contactsSubTitle, TOOLBAR_TEXT_COLOR);
        Utils.setActivityStyle(this, toolbar);

        setSupportActionBar(toolbar);
        Utils.setActivityStyle(this, toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setTitle("Contacts");
        String title = mMesiboUIOptions.messageListTitle;
        if(TextUtils.isEmpty(title)) title = Mesibo.getAppName();
        contactsTitle.setText(title);

        if(mMode == MesiboUserListFragment.MODE_MESSAGELIST) {
            contactsSubTitle.setText(mMesiboUIOptions.offlineIndicationTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(mMesiboUIOptions.enableBackButton);
        }

        if(savedInstanceState == null) {

            UserListFragment userListFragment = new UserListFragment();
            userListFragment.setListener(this);
            Bundle bl = new Bundle();
            bl.putInt(MesiboUserListFragment.MESSAGE_LIST_MODE, mMode);
            bl.putLong(MesiboUI.MESSAGE_ID, mForwardId);

            if(!TextUtils.isEmpty(forwardMessage))
                bl.putString(MesiboUI.MESSAGE_CONTENT, forwardMessage);

            bl.putLongArray(MesiboUI.MESSAGE_IDS, mForwardIds);
            if(mMode == MesiboUserListFragment.MODE_EDITGROUP)
                bl.putBundle(MesiboUI.BUNDLE, mEditGroupBundle);

            bl.putBoolean(MesiboUI.FORWARD_AND_CLOSE, forwardAndClose);

            userListFragment.setArguments(bl);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.userlist_fragment, userListFragment,"null");
            ft.addToBackStack("userListFragment");
            ft.commit();

        }
    }

    @Override
    public void Mesibo_onUpdateTitle(String title) {
        contactsTitle.setText(title);
    }

    @Override
    public void Mesibo_onUpdateSubTitle(String title) {
        if (title== null) {
            contactsSubTitle.setVisibility(View.GONE);
        } else {
            contactsSubTitle.setVisibility(View.VISIBLE);
            contactsSubTitle.setText(title);
        }
    }

    @Override
    public boolean Mesibo_onClickUser(String address, long groupid, long forwardid) {
        return false;
    }

    @Override
    public boolean Mesibo_onUserListFilter(Mesibo.MessageParams params) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mKeepRunning)
            moveTaskToBack(true);
        else
            finish();

    }

    @Override
    public  void onResume() {
        super.onResume();

        Mesibo.setForegroundContext(this, 0, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Now Mesibo API does it
        //Mesibo.setAppInForeground(this, 0, false);
    }
}
