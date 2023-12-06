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


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;

public class MesiboEndToEndEncryptionActivity extends AppCompatActivity  {
    MesiboEndToEndEncryptionFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e2e_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setActivityStyle(this, toolbar);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle args = getIntent().getExtras();
        if(null == args) {
            return;
        }

        String peer = args.getString(MesiboUI.PEER);
        long groupid = args.getLong(MesiboUI.GROUP_ID);

        MesiboProfile profile = null;
        TextView st = (TextView) findViewById(R.id.e2e_subtitle);

        if(!TextUtils.isEmpty(peer) || groupid > 0) {
            profile = Mesibo.getProfile(peer, groupid);
            String firstName = profile.getFirstNameOrAddress();
            String subtitle = "You and " + firstName;
            st.setText(subtitle);
        } else {
            st.setVisibility(View.GONE);
            profile = Mesibo.getSelfProfile();
        }

        if(null == profile) {
            finish();
            return;
        }


        Utils.setTextViewColor(st, MesiboUI.getUiDefaults().mToolbarTextColor);

        String title = MesiboUI.getUiDefaults().e2eeTitle;

        if(TextUtils.isEmpty(title))
            title = "";

        Utils.setTitleAndColor(ab, title);

        mFragment = new MesiboEndToEndEncryptionFragment();
        mFragment.setProfile(profile);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_place, mFragment,"null");
        ft.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(mFragment.Mesibo_onBackPressed()) {
            return super.onOptionsItemSelected(item);
        }

        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        mFragment.Mesibo_onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(mFragment.Mesibo_onBackPressed()) {
            return;
        }

        super.onBackPressed(); // allows standard use of backbutton for page 1

    }
}
