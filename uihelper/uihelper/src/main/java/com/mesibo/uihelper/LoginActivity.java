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

package com.mesibo.uihelper;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;


import com.mesibo.uihelper.Utils.ActivityListener;

public class LoginActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_welcome_login);

        Bundle bundle = getIntent().getExtras();

        int type = 0;

        if(null != bundle)
            type = bundle.getInt("type", 0);

        if(savedInstanceState == null) {
            if(0 == type) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_place, new WelcomeFragment(), "welcome")
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_place,new PhoneVerificationFragment(), "verification")
                        .addToBackStack(null)
                        .commit();
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_place, new PhoneVerificationFragment(), "verification")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if(fragment instanceof PhoneVerificationFragment && fragment.isVisible()) {
                    ((PhoneVerificationFragment) fragment).onBackKeyPressed();
                    return;
                }
            }

            getSupportFragmentManager().popBackStack();
            //finish();
        }
    }



    //https://developers.google.com/identity/smartlock-passwords/android/overview
    //https://github.com/googlesamples/android-credentials/tree/master/sms-verification/android
    private final int RC_HINT = 111;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(fragment instanceof ActivityListener && fragment.isVisible()) {
                ((ActivityListener) fragment).onActivityResultPrivate(requestCode, resultCode, data);
                return;
            }
        }
    }
}
