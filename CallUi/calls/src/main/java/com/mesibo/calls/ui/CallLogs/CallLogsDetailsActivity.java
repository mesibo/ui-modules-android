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

package com.mesibo.calls.ui.CallLogs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.ui.R;

import java.util.ArrayList;


public class CallLogsDetailsActivity extends AppCompatActivity implements Mesibo.MessageListener {

    Toolbar mToolbar;
    String mPID;
    TextView mProfileName;
    ImageView mVideoCall, mAudioCall, mProfilePicture;
    RecyclerView recyclerView;
    private MesiboProfile mUser = null;
    ArrayList<CallLogsItem> CallLogsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_call_logs_details);

        mToolbar = findViewById(R.id.toolbar_map);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            mPID = intent.getStringExtra("pid");

        }

        mProfileName = findViewById(R.id.profileName);
        mProfilePicture = findViewById(R.id.profile_pic);
        mAudioCall = findViewById(R.id.audioCall);
        mVideoCall = findViewById(R.id.videoCam);


        //getUserProfile
        mUser = Mesibo.getProfile(mPID);

        mProfileName.setText(mUser.getNameOrAddress("+"));


        ///Set Profile Pic
        Bitmap b = mUser.getThumbnail();


        if (null != b) {
            mProfilePicture.setImageDrawable(MesiboUtils.getRoundImageDrawable(b));
        } else {
            //TBD, getActivity.getresource crashes sometime if activity is closing
            mProfilePicture.setImageDrawable(MesiboUtils.getRoundImageDrawable(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_user_image)));
        }


        mVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mesibo.MessageParams mParameter = new Mesibo.MessageParams(mPID, 0, Mesibo.FLAG_DEFAULT, 0);
                MesiboCall.getInstance().callUi(CallLogsDetailsActivity.this, mParameter.profile.address, true);
            }
        });

        mAudioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mesibo.MessageParams mParameter = new Mesibo.MessageParams(mPID, 0, Mesibo.FLAG_DEFAULT, 0);
                MesiboCall.getInstance().callUi(CallLogsDetailsActivity.this, mParameter.profile.address, false);
            }
        });

        mProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent i = new Intent(CallLogsDetailsActivity.this, OthersProfileActivity.class);
                i.putExtra("pid",mPID);
                startActivity(i);
                */
            }
        });

        recyclerView = findViewById(R.id.call_logs_details_RV);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);


    }


    @Override
    protected void onResume() {
        super.onResume();

        Mesibo.addListener(this);

        CallLogsArrayList.clear();
        Mesibo.ReadDbSession rdb = new Mesibo.ReadDbSession(mPID, this);
        rdb.enableMessages(false);
        rdb.enableCalls(true);
        rdb.read(100);
    }


    public void setCallList(ArrayList<CallLogsItem> arrayList) {
        CallLogsAdapter mAdapter = new CallLogsAdapter(this, arrayList, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 70));
        recyclerView.setAdapter(mAdapter);

    }


    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams messageParams, byte[] bytes) {

        CallLogsItem callLogsItem = new CallLogsItem();
        callLogsItem.setName(messageParams.profile.getNameOrAddress("+"));
        callLogsItem.setStatus(messageParams.getStatus());
        callLogsItem.setTs(messageParams.ts);
        callLogsItem.setPeer(messageParams.peer);
        callLogsItem.setDuration(messageParams.duration);
        callLogsItem.setType(messageParams.type);
        callLogsItem.setMid(messageParams.mid);

        CallLogsArrayList.add(callLogsItem);

        if(messageParams.isLastMessage())
            setCallList(CallLogsArrayList);


        return false;
    }

    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams messageParams) {

    }

    @Override
    public void Mesibo_onActivity(Mesibo.MessageParams messageParams, int i) {

    }

    @Override
    public void Mesibo_onLocation(Mesibo.MessageParams messageParams, Mesibo.Location location) {

    }

    @Override
    public void Mesibo_onFile(Mesibo.MessageParams messageParams, Mesibo.FileInfo fileInfo) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_action_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.deleteItem);
        menuItem.setVisible(false);
        MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_NEVER);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if(R.id.removeFromCallLog == item.getItemId()) {
                for (int i = 0; i < CallLogsArrayList.size(); i++) {
                    long mid = CallLogsArrayList.get(i).getMid();
                    Mesibo.deleteMessage(mid);
                }

                finish();
            }

            return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.removeListener(this);

        CallLogsArrayList.clear();
    }


}
