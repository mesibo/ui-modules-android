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

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mesibo.api.Mesibo;


public class MesiboUserListActivity extends AppCompatActivity  {
    public static final String TAG="MesiboMainActivity";
    TextView contactsTitle = null;
    TextView contactsSubTitle = null;
    int mMode = 0;
    boolean mKeepRunning = false;


    MesiboUiDefaults mMesiboUIOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TBD, this must be fixed
        if(!Mesibo.isReady()) {
            finish();
            return;
        }

        MesiboImages.init(this);

        MesiboUI.MesiboUserListScreenOptions opts = (MesiboUI.MesiboUserListScreenOptions) Mesibo.getIntentObject(getIntent(), MesiboUI.MesiboUserListScreenOptions.class);

        if(null == opts) opts = new MesiboUI.MesiboUserListScreenOptions();

        mMode = opts.mode;
        mKeepRunning = opts.keepRunning;
        if(opts.startInBackground) {
            moveTaskToBack(true);
        }

        MesiboUIManager.enableSecureScreen(this);

        setContentView(R.layout.activity_messages);
        mMesiboUIOptions = MesiboUI.getUiDefaults();

        Toolbar toolbar = findViewById(R.id.message_toolbar);
        contactsSubTitle = findViewById(R.id.contacts_subtitle);
        contactsTitle =  findViewById(R.id.contacts_title);
        Utils.setTextViewColor(contactsTitle, MesiboUI.getUiDefaults().mToolbarTextColor);
        Utils.setTextViewColor(contactsSubTitle, MesiboUI.getUiDefaults().mToolbarTextColor);
        Utils.setActivityStyle(this, toolbar);

        setSupportActionBar(toolbar);
        Utils.setActivityStyle(this, toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setTitle("Contacts");
        String title = mMesiboUIOptions.messageListTitle;
        if(TextUtils.isEmpty(title)) title = Mesibo.getAppName();
        contactsTitle.setText(title);

        if(mMode == MesiboUserListFragment.MODE_MESSAGES) {
            contactsSubTitle.setText(mMesiboUIOptions.offlineIndicationTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(mMesiboUIOptions.enableBackButton);
        }

        if(savedInstanceState == null) {

            UserListFragment userListFragment = new UserListFragment();
            //userListFragment.setListener(this);
            MesiboUI.MesiboUserListScreen screen = userListFragment.getScreen();
            screen.title = contactsTitle;
            screen.subtitle = contactsSubTitle;
            userListFragment.mMode = mMode;
            userListFragment.mOpts = opts;

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.userlist_fragment, userListFragment,"null");
            ft.addToBackStack("userListFragment");
            ft.commit();

        }
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
    }
}
