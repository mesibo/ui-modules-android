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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;



public class MesiboMessagingActivity extends AppCompatActivity  {
    private Toolbar mToolbar = null;
    MessagingFragment mFragment = null;

    private MesiboUiDefaults mMesiboUIOptions = null ;

    private MesiboProfile mUser=null;
    private ImageView mProfileImage = null;
    private TextView mUserStatus = null;
    private TextView mTitle = null;
    private String mProfileImagePath = null;
    private Bitmap mProfileThumbnail = null;
    private RelativeLayout mTitleArea = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

        super.onCreate(savedInstanceState);
        MesiboUI.MesiboMessageScreenOptions opts = (MesiboUI.MesiboMessageScreenOptions) Mesibo.getIntentObject(getIntent(), MesiboUI.MesiboMessageScreenOptions.class);

        if(null == opts) {
            return;
        }

        if(!Mesibo.isReady()) {
            finish();
            return;
        }

        mMesiboUIOptions = MesiboUI.getUiDefaults();
        mUser = opts.profile;

        MesiboUIManager.enableSecureScreen(this);
        setContentView(R.layout.activity_messaging_new);

        mToolbar = findViewById(R.id.toolbar);
        Utils.setActivityStyle(this, mToolbar);


        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(false);
        ab.setDisplayShowHomeEnabled(false);

        //mToolbar.setContentInsetStartWithNavigation(0);

        findViewById(R.id.chat_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mUserStatus = (TextView) findViewById(R.id.chat_profile_subtitle);
        Utils.setTextViewColor(mUserStatus, MesiboUI.getUiDefaults().mToolbarTextColor);

        mProfileImage = (ImageView) findViewById((R.id.chat_profile_pic));

        if (mProfileImage != null) {

            mProfileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null == mUser) {
                        return;
                    }

                    mProfileImagePath = mUser.getImage().getImageOrThumbnailPath();
                    if(TextUtils.isEmpty(mProfileImagePath))
                        return;

                    MesiboUIManager.launchPictureActivity(MesiboMessagingActivity.this, mUser.getNameOrAddress(), mProfileImagePath);
                }
            });
        }

        mTitleArea = (RelativeLayout) findViewById(R.id.name_tite_layout);
        mTitle = (TextView) findViewById(R.id.chat_profile_title);
        mTitle.setText(mUser.getNameOrAddress());
        Utils.setTextViewColor(mTitle, MesiboUI.getUiDefaults().mToolbarTextColor);

        startFragment(savedInstanceState, opts);
    }

    private void startFragment(Bundle savedInstanceState, MesiboUI.MesiboMessageScreenOptions opts) {
        if (findViewById(R.id.fragment_container) == null || savedInstanceState != null) {
            return;
        }

        mFragment = new MessagingFragment();
        MesiboUI.MesiboMessageScreen screen = mFragment.getScreen();
        screen.parent = this;
        screen.title = mTitle;
        screen.subtitle = mUserStatus;
        screen.titleArea = mTitleArea;
        screen.profileImage = mProfileImage;
        screen.profile = mUser;
        mFragment.mOpts = opts;

        // In case this activity was started with additional instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    static int FROM_MESSAGING_ACTIVITY = 1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }else {
            //mMesiboUIHelperlistener.MesiboUI_onMenuItemSelected(this, FROM_MESSAGING_ACTIVITY, mUser, id);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        MessagingFragment f = mFragment;
        if(null != f && f.Mesibo_onBackPressed()) {
            return;
        }

        super.onBackPressed(); // allows standard use of backbutton for page 1

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mFragment.Mesibo_onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (null != mFragment)
            mFragment.Mesibo_onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MesiboUIManager.setMessagingActivity(this);
    }
}

