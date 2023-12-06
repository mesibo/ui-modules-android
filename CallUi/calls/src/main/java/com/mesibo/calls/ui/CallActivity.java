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

package com.mesibo.calls.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

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

        int res = checkPermissions(mVideo);

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

            Mesibo.UserProfile profile = Mesibo.getUserProfile(mAddress);
            if(null == profile) {
                profile = new Mesibo.UserProfile();
                profile.address = mAddress;
                profile.name = mAddress;
            }

            MesiboCall.CallProperties cc = MesiboCall.getInstance().new CallProperties(mVideo);
            cc.parent = this;
            cc.activity = this;
            cc.user = profile;
            cc.video.source = mVideoSource;

            MesiboCallUi.Listener cul = MesiboCallUi.getInstance().getListener();
            if(null != cul)
                cul.MesiboCallUi_OnConfig(cc);

            mCall = MesiboCall.getInstance().call(cc);

            if(null == mCall || !mCall.isCallInProgress()) {
                finish();
                return;
            }
        }

        super.initCall(mCall);

        CallFragment fragment = null;
        fragment = new CallFragment();

        /* OPTIONAL - you can use different fragments for different type of calls */
        if(mVideo) {
            // show video fragment
        } else if(mIncoming && mCall.isCallInProgress() && !mCall.isAnswered()) {
            //show incoming audio fragment
        } else {
            //show outgoing audio fragemnt
        }

        fragment.MesiboCall_OnSetCall(this, mCall);

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
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
