/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2025 mesibo                                              *
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

package com.mesibo.calls.app;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.api.MesiboCallActivity;

public class CallActivity extends MesiboCallActivity {
    private boolean mInit = false;
    MesiboCall.Call mCall = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        int res = checkPermissions(mCp.video.enabled);

        /* permissions were declined */
        if(res < 0) {
            finish();
            return;
        }

        /* all permissions were already granted */
        if(0 == res) {
            initCall();
        } else {
            /* permission requested - wait for results */
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initCall();
        }
        else
            finish();
    }

    private void initCall() {
        if(mInit) return;
        mInit = true;

        mCall = MesiboCall.getInstance().getActiveCall();

        if(null == mCall) {

            if(null == mCp.parent)
                mCp.parent = this;
            mCp.activity = this;
            mCall = MesiboCall.getInstance().call(mCp);

            if(null == mCall || !mCall.isCallInProgress()) {
                finish();
                return;
            }
        }

        super.initCall(mCall);

        CallFragment fragment = null;
        fragment = new CallFragment();

        fragment.MesiboCall_OnSetCall(this, mCall);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.top_fragment_container, fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mInit)
            return;

        mCall = MesiboCall.getInstance().getActiveCall();
        if(null == mCall) {
            finish();
            return;
        }
    }


}
