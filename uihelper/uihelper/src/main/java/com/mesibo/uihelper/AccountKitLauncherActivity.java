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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.AccountKitLoginResult;
import com.mesibo.uihelper.Utils.Alert;
import com.mesibo.uihelper.Utils.Log;

public class AccountKitLauncherActivity extends AppCompatActivity implements ILoginResultsInterface {
    private ProgressDialog mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // progress is crashing possible because we don't have view
        //mProgressDialog = Alert.getProgressDialog(this, "Please wait...");
        setContentView(R.layout.activity_accountkitlauncher);

        phoneLogin();
    }

    public static int APP_REQUEST_CODE = 99;

    public void phoneLogin() {

        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }




    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = null;
            if(null != data)
                loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (null == loginResult || loginResult.getError() != null || loginResult.wasCancelled()) {
                sendResult(null);
            } else {
                if (loginResult.getAccessToken() != null) {
                    sendResult(loginResult.getAccessToken().getToken());
                } else {
                          //  loginResult.getAuthorizationCode();
                    sendResult(null);
                }
            }

            // Surface the result to your user in an appropriate way.
            //Toast.makeText(this,                   toastMessage,                    Toast.LENGTH_LONG).show();
        }
    }

    private void sendResult(String token) {
        boolean relaunch = false;
        ILoginInterface i = MesiboUiHelper.getLoginInterface();
        if(null != i) {
            if(null != mProgressDialog)
                mProgressDialog.show();

            relaunch = i.onAccountKitLogin(this, token, this);
        }

        if(null == token && relaunch)
            phoneLogin();
        else {
            //finish();
        }
    }

    @Override
    public void onLoginResult(boolean result, int delay) {
        //mProgressBar.setVisibility(View.GONE);
        if(null != mProgressDialog && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        if(result) {
            // if delay < 0, do nothing
            if(0 == delay) {
                finish();
            }
            else if(delay > 0) {
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        finish();
                    }
                }, delay);
            }
        } else {
            phoneLogin();
        }

    }

}
