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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboDateTime;
import com.mesibo.api.MesiboProfile;
import com.mesibo.calls.api.MesiboCall;



public class MainActivity extends AppCompatActivity implements Mesibo.ConnectionListener, MesiboCall.IncomingListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token = "your app token";

        /* initialize mesibo */
        Mesibo mesibo = Mesibo.getInstance();
        mesibo.init(getApplicationContext());
        mesibo.setAccessToken(token);
        boolean res = mesibo.setDatabase("callapp.db");
        mesibo.addListener(this);
        Mesibo.start();

        /* initialize call */
        MesiboCall.getInstance().init(this);

        String destination =  "destination";

        /* set profile so that it is visible in call screen */
        MesiboProfile u = Mesibo.getProfile(destination);

        FloatingActionButton fab_v = (FloatingActionButton) findViewById(R.id.fab_videocall);
        fab_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCustomCallActivity(destination, true, false);
            }
        });

        FloatingActionButton fab_a = (FloatingActionButton) findViewById(R.id.fab_audiocall);
        fab_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCustomCallActivity(destination, false, false);
            }
        });

        FloatingActionButton fab_m = (FloatingActionButton) findViewById(R.id.fab_message);
        fab_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    protected void launchCustomCallActivity(String destination, boolean video, boolean incoming) {
        Intent intent = new Intent(this, CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("video", video);
        intent.putExtra("address", destination);
        intent.putExtra("incoming", incoming);
        startActivity(intent);
    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        Log.d("Mesibo", "Connection status: " + status);
    }

    @Override
    public MesiboCall.CallProperties MesiboCall_OnIncoming(@NonNull  MesiboProfile profile, boolean video, boolean waiting) {
        MesiboCall.CallProperties cc = MesiboCall.getInstance().createCallProperties(video);
        cc.parent = getApplicationContext();
        cc.user = profile;
        cc.className = CallActivity.class;
        return cc;
    }

    @Override
    public boolean MesiboCall_OnShowUserInterface(MesiboCall.Call call, MesiboCall.CallProperties cp) {
        launchCustomCallActivity(cp.user.address, cp.video.enabled, true);
        return true;
    }

    @Override
    public void MesiboCall_OnError(MesiboCall.CallProperties properties, int error) {

    }

    @Override
    public boolean MesiboCall_onNotify(int type, @NonNull MesiboProfile mesiboProfile, boolean b, @NonNull MesiboDateTime mesiboDateTime) {
        return false;
    }
}
