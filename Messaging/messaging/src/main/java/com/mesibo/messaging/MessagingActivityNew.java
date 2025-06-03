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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;

import static com.mesibo.messaging.MesiboConfiguration.TOOLBAR_TEXT_COLOR;


public class MessagingActivityNew extends AppCompatActivity implements MesiboMessagingFragment.FragmentListener {


    private Toolbar mToolbar = null;


    MessagingFragment mFragment = null;

    private Mesibo.UIHelperListner mMesiboUIHelperlistener = null;
    private MesiboUI.Config mMesiboUIOptions = null ;

    private MesiboProfile mUser=null;
    private ImageView mProfileImage = null;
    private TextView mUserStatus = null;
    private TextView mTitle = null;
    private String mProfileImagePath = null;
    private Bitmap mProfileThumbnail = null;

    private ActionMode mActionMode = null;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();

    private Mesibo.MessageParams mParameter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if(null == args) {
            return;
        }

        if(!Mesibo.isReady()) {
            finish();
            return;
        }

        mMesiboUIHelperlistener = Mesibo.getUIHelperListner();
        mMesiboUIOptions = MesiboUI.getConfig();

        String peer = args.getString(MesiboUI.PEER);
        long groupId = args.getLong(MesiboUI.GROUP_ID);

        if(groupId > 0) {
            mUser = Mesibo.getProfile(groupId);
        } else
            mUser = Mesibo.getProfile(peer);

        if(null == mUser) {
            finish();
            return;
        }

        MesiboUIManager.enableSecureScreen(this);

        mParameter = new Mesibo.MessageParams(peer, groupId, Mesibo.FLAG_DEFAULT, 0);

        setContentView(R.layout.activity_messaging_new);

        mToolbar = findViewById(R.id.toolbar);
        Utils.setActivityStyle(this, mToolbar);

        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mUserStatus = (TextView) findViewById(R.id.chat_profile_subtitle);
        Utils.setTextViewColor(mUserStatus, TOOLBAR_TEXT_COLOR);

        mProfileImage = (ImageView) findViewById((R.id.chat_profile_pic));
        String name = mUser.getName();
        if(TextUtils.isEmpty(name))
            name = mUser.address;

        if (mProfileImage != null) {

            final String name_final = name;
            mProfileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null == mProfileImagePath) {
                        return;
                    }

                    MesiboUIManager.launchPictureActivity(MessagingActivityNew.this, name_final, mProfileImagePath);
                }
            });
        }

        RelativeLayout nameLayout = (RelativeLayout) findViewById(R.id.name_tite_layout);
        mTitle = (TextView) findViewById(R.id.chat_profile_title);
        mTitle.setText(name);
        Utils.setTextViewColor(mTitle, TOOLBAR_TEXT_COLOR);

        if (mTitle != null) {
            nameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null != mMesiboUIHelperlistener)
                        mMesiboUIHelperlistener.Mesibo_onShowProfile(MessagingActivityNew.this, mUser);
                }
            });
        }

        startFragment(savedInstanceState);

    }

    private void setProfilePicture() {

    }


    private void startFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container) == null || savedInstanceState != null) {
            return;
        }

        mFragment = new MessagingFragment();

        mFragment.setArguments(getIntent().getExtras());

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
        if(null == mMesiboUIHelperlistener)
            return true;

        mMesiboUIHelperlistener.Mesibo_onGetMenuResourceId(this, FROM_MESSAGING_ACTIVITY, mParameter, menu);

        return true;
    }

    static int FROM_MESSAGING_ACTIVITY = 1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }else {
            mMesiboUIHelperlistener.Mesibo_onMenuItemSelected(this, FROM_MESSAGING_ACTIVITY, mParameter, id);
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
    public void Mesibo_onUpdateUserPicture(MesiboProfile profile, Bitmap thumbnail, String picturePath) {
        mProfileThumbnail = thumbnail;
        mProfileImagePath = picturePath;
        mProfileImage.setImageDrawable(new RoundImageDrawable(mProfileThumbnail));
        String name = mUser.getName();
        if(TextUtils.isEmpty(name))
            name = mUser.address;
        if(name.length() > 16)
            name = name.substring(0, 14) + "...";
        mTitle.setText(name);
    }

    @Override
    public void Mesibo_onUpdateUserOnlineStatus(MesiboProfile profile, String status) {
        if(null == status) {
            mUserStatus.setVisibility(View.GONE);
            return;
        }

        mUserStatus.setVisibility(View.VISIBLE);
        mUserStatus.setText(status);
        return;
    }

    @Override
    public void Mesibo_onShowInContextUserInterface() {
        mActionMode = startSupportActionMode(mActionModeCallback);
    }

    @Override
    public void Mesibo_onHideInContextUserInterface() {
        mActionMode.finish();
    }

    @Override
    public void Mesibo_onContextUserInterfaceCount(int count) {
        if(null == mActionMode)
            return;

        mActionMode.setTitle(String.valueOf(count));
        mActionMode.invalidate();
    }

    @Override
    public void Mesibo_onError(int type, String title, String message) {
        Utils.showAlert(this, title, message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mFragment.Mesibo_onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mFragment.Mesibo_onActivityResult(requestCode, resultCode, data);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);

            menu.findItem(R.id.menu_reply).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_star).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_resend).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_forward).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_forward).setVisible(mMesiboUIOptions.enableForward);
            menu.findItem(R.id.menu_forward).setEnabled(mMesiboUIOptions.enableForward);
            menu.findItem(R.id.menu_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            int enabled = mFragment.Mesibo_onGetEnabledActionItems();


            menu.findItem(R.id.menu_resend).setVisible((enabled&MessagingFragment.MESIBO_MESSAGECONTEXTACTION_RESEND) > 0);

            menu.findItem(R.id.menu_copy).setVisible((enabled&MessagingFragment.MESIBO_MESSAGECONTEXTACTION_COPY) > 0);
            menu.findItem(R.id.menu_copy).setVisible((enabled&MessagingFragment.MESIBO_MESSAGECONTEXTACTION_REPLY) > 0);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int mesiboItemId = 0;

            if (item.getItemId() == R.id.menu_remove) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_DELETE;
            } else if (item.getItemId() == R.id.menu_copy) {

                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_COPY;

            } else if (item.getItemId() == R.id.menu_resend) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_RESEND;
            } else if (item.getItemId() == R.id.menu_forward) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_FORWARD;
            } else if (item.getItemId() == R.id.menu_star) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_FAVORITE;
            } else if (item.getItemId() == R.id.menu_reply) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_REPLY;
            }

            if(mesiboItemId > 0) {
                mFragment.Mesibo_onActionItemClicked(mesiboItemId);
                mode.finish();
                mFragment.Mesibo_onInContextUserInterfaceClosed();
                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFragment.Mesibo_onInContextUserInterfaceClosed();
            mActionMode = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MesiboUIManager.setMessagingActivity(this);
        setProfilePicture();
    }
}

