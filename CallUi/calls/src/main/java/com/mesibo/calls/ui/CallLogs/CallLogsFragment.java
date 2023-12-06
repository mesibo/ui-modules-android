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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.calls.ui.R;

import java.util.ArrayList;


public class CallLogsFragment extends Fragment implements Mesibo.MessageListener {

    private RecyclerView recyclerView;
    private CallLogsAdapter mAdapter;
    private MenuItem myActionMenuItem;
    private ArrayList<CallLogsItem> CallLogsArrayList = new ArrayList<>();
    private int mMsg_count = 0;
    private Mesibo.ReadDbSession mRDB = null;
    private int mItemToLoadCount = 256;
    private TextView mNoLogsView = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_logs, container, false);

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        mNoLogsView = view.findViewById(R.id.no_call_logs_text);
        return view;
    }

    public void setCallList(ArrayList<CallLogsItem> arrayList) {
        mNoLogsView.setVisibility((arrayList.size()>0)?View.GONE:View.VISIBLE);
        mAdapter = new CallLogsAdapter(getActivity(), arrayList, true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    if(!startReadingLogs()) {
                        recyclerView.removeOnScrollListener(this);
                    }
                }
            }
        });

        //recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 70));
        recyclerView.setAdapter(mAdapter);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_calllogsearch, menu);
        super.onCreateOptionsMenu(menu, inflater);


        myActionMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if(null == mAdapter) return false;
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if(null == mAdapter) return false;

                mAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();

        Mesibo.addListener(this);
        CallLogsArrayList.clear();
        mRDB = new Mesibo.ReadDbSession(this);
        mRDB.enableMessages(false);
        mRDB.enableCalls(true);
        startReadingLogs();
    }

    private boolean mReadComplete = false;
    public boolean startReadingLogs() {
        if(mReadComplete) return false;
        int count = mRDB.read(mItemToLoadCount);
        if(count < mItemToLoadCount) {
            mReadComplete = true;
        }
        return true;
    }


    @Override
    public void onPause() {
        super.onPause();
        Mesibo.removeListener(this);

        mRDB.stop();
        CallLogsArrayList.clear();
        mMsg_count = 0;
        setHasOptionsMenu(false);

    }


    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams messageParams, byte[] bytes) {

        CallLogsItem mCallLogsItem = new CallLogsItem();


        mCallLogsItem.setName(messageParams.profile.getNameOrAddress("+"));
        mCallLogsItem.setStatus(messageParams.getStatus());
        mCallLogsItem.setTs(messageParams.ts);
        mCallLogsItem.setPeer(messageParams.peer);
        mCallLogsItem.setType(messageParams.type);
        mCallLogsItem.setMid(messageParams.mid);

        mCallLogsItem.setCount(1);

        MesiboProfile mUser = Mesibo.getProfile(messageParams.peer);

        mCallLogsItem.setImg(mUser.getImageOrThumbnailPath());

        if (CallLogsArrayList.size() == 0) {
            CallLogsArrayList.add(mCallLogsItem);
        } else {//second onwards

            CallLogsItem previousItem = CallLogsArrayList.get(CallLogsArrayList.size() - 1);

            String currentPeer = mCallLogsItem.getPeer();
            String prevPeer = previousItem.getPeer();

            int currentStatus = mCallLogsItem.getStatus();
            int prevStatus = previousItem.getStatus();

            int currentType = mCallLogsItem.getType();
            int prevType = previousItem.getType();

            if (currentPeer.equalsIgnoreCase(prevPeer) && currentStatus == prevStatus && currentType == prevType) {

                mMsg_count = previousItem.getCount() + 1;
                previousItem.setCount(mMsg_count);

                int pos = CallLogsArrayList.size() - 1;

                if (pos >= 0) {
                    CallLogsArrayList.set(pos, previousItem);
                } else if (pos == -1) {
                    CallLogsArrayList.set(0, previousItem);
                }


            } else {
                CallLogsArrayList.add(mCallLogsItem);
            }
        }


        if(messageParams.isLastMessage()) {
            setCallList(CallLogsArrayList);
            mAdapter.notifyDataSetChanged();
        }

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

}
