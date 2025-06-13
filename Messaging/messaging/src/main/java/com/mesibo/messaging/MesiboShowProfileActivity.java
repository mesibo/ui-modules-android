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

import static com.mesibo.messaging.Utils.setActivityStyle;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboDateTime;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboProfileImage;
import com.mesibo.messaging.AllUtils.OnSwipeTouchListener;

public class MesiboShowProfileActivity extends AppCompatActivity implements ShowProfileFragment.OnFragmentInteractionListener, MesiboProfile.Listener {

    MesiboMessagingSquareImageView mUsermageView;
    MesiboProfile mUserProfile;
    Toolbar mToolbar;
    AppBarLayout mAppBarLayout;
    CoordinatorLayout mCoordinatorLayout;

    long mGroupId = 0;
    String mPeer = null;
    MesiboProfileImage mProfileImage = null;
    TextView mUserName = null;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        mToolbar = (Toolbar) findViewById(R.id.up_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle args = getIntent().getExtras();
        if (null == args) {
            return;
        }

        mPeer = args.getString(MesiboUI.PEER);
        mGroupId = args.getLong(MesiboUI.GROUP_ID);

        mUserProfile = null;

        if (mGroupId > 0) {
            mUserProfile = Mesibo.getProfile(mGroupId);
        } else {
            mUserProfile = Mesibo.getProfile(mPeer);
        }

        mUserProfile.addListener(this);

        mUsermageView = (MesiboMessagingSquareImageView) findViewById(R.id.up_image_profile);

        Mesibo.addListener(this);

        mProfileImage = mUserProfile.getImage();

        mUsermageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mProfileImage.getImageOrThumbnailPath();
                if(TextUtils.isEmpty(path))
                    return;
                MesiboUIManager.launchPictureActivity(MesiboShowProfileActivity.this, mUserProfile.getNameOrAddress(), path);
            }

        });

        mUsermageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });

        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(this) {
            public boolean onSwipeRight() {
                if(!hasWindowFocus()) return true; // this is to ensure activity is live
                MesiboProfileImage image = mProfileImage.getPrevious();
                if(image != null) {
                    mProfileImage = image;
                    showPicture();
                }
                return true;
            }
            public boolean onSwipeLeft() {
                if(!hasWindowFocus()) return true; // this is to ensure activity is live
                MesiboProfileImage image = mProfileImage.getNext();
                if(image != null) {
                    mProfileImage = image;
                    showPicture();
                }
                return true;
            }
        };

        mUsermageView.setOnTouchListener(touchListener);

        mUserName = (TextView) findViewById(R.id.up_user_name);
        TextView userstatus = (TextView) findViewById(R.id.up_current_status);

        mUserName.setText(mUserProfile.getName());
        MesiboDateTime lastSeen = mUserProfile.getLastSeen();
        userstatus.setVisibility(View.VISIBLE);
        if(mUserProfile.isOnline()) {
            userstatus.setText("Online");
        }
        else if(null == lastSeen || !MesiboUI.getUiDefaults().showLastSeenInProfileView) {
            // never seen or group
            userstatus.setVisibility(View.GONE);
        }
        else {
            String seenStatus = lastSeen.getDateInNaturalLanguage();
            userstatus.setText("Last seen, " + seenStatus);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.up_collapsing_toolbar);
        collapsingToolbar.setTitle("  ");
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.up_profile_root);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.up_appbar);
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {

                setAppBarOffset(-250);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //measuring for alpha
                float Alpha = ((float) (appBarLayout.getTotalScrollRange() - Math.abs(verticalOffset)) / appBarLayout.getTotalScrollRange());
                if (Alpha > 0.4)
                    Alpha = 1;
                else {
                    Alpha = Alpha + (float) 0.6;
                }

                mUsermageView.setAlpha(Alpha);

            }
        });

        mAppBarLayout.setBackgroundColor(MesiboUI.getUiDefaults().mToolbarColor);

        //collapsingToolbar.setOnTouchListener(touchListener);

        setActivityStyle(this, null);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ShowProfileFragment showUserProfileDetailsFragment = ShowProfileFragment.newInstance(mUserProfile);
        fragmentTransaction.add(R.id.up_fragment, showUserProfileDetailsFragment, "up");
        fragmentTransaction.commit();

    }

    private void setAppBarOffset(int offsetPx) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.setTopAndBottomOffset(-60 - mAppBarLayout.getTotalScrollRange() / 2);
        //behavior.onNestedPreScroll(mCoordinatorLayout, mAppBarLayout, null, 0, offsetPx, new int[]{0, 0});
    }

    private void showPicture() {
        if(null == mProfileImage) return;
        Bitmap b = mProfileImage.getImageOrThumbnail();
        if(null != b) {
            mUsermageView.setImageBitmap(b);
        }



    }
    private void setUserPicture() {
        mProfileImage = mUserProfile.getImage();
        showPicture();
    }

    private void setUserName() {
        if(null != mUserName) {
            mUserName.setText(mUserProfile.getNameOrAddress());
        }
    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile userProfile) {

        if(Mesibo.isUiThread()) {
            setUserPicture();
            setUserName();
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setUserPicture();
                setUserName();
            }
        });
    }

    @Override
    public void MesiboProfile_onPublish(MesiboProfile mesiboProfile, boolean b) {

    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onResume() {
        super.onResume();

        Mesibo.setForegroundContext(this, 0x102, true);

        if(mUserProfile.groupid > 0) {
            TextView userName = (TextView) findViewById(R.id.up_user_name);
            if(null != mUserProfile.getName())
                userName.setText(mUserProfile.getName());
        }

        setUserPicture();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.setForegroundContext(this, 0x102, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mesibo.setForegroundContext(this, 0x102, false);
    }


    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

}
