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
import static android.os.Looper.getMainLooper;
import static com.mesibo.api.Mesibo.getProfile;
import static com.mesibo.api.MesiboPresence.PRESENCE_TYPING;
import static com.mesibo.messaging.MesiboConfiguration.ATTACHMENT_ICON;
import static com.mesibo.messaging.MesiboConfiguration.DELETED_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.IMAGE_ICON;
import static com.mesibo.messaging.MesiboConfiguration.LOCATION_ICON;
import static com.mesibo.messaging.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;
import static com.mesibo.messaging.MesiboConfiguration.MESIBO_SEARCH_READ_USERLIST;
import static com.mesibo.messaging.MesiboConfiguration.MESSAGE_STRING_USERLIST_SEARCH;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VIDEOCALL_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VOICECALL_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.USERS_STRING_USERLIST_SEARCH;
import static com.mesibo.messaging.MesiboConfiguration.VIDEO_ICON;
import static com.mesibo.messaging.MesiboImages.getMissedCallDrawable;
import static com.mesibo.messaging.MesiboUserListActivity.TAG;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_CONTACTS;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_EDITGROUP;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_FORWARD;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_GROUPS;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_MESSAGES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboPresence;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.messaging.AllUtils.LetterTileProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class UserListFragment extends BaseFragment implements Mesibo.MessageListener, Mesibo.PresenceListener, Mesibo.ConnectionListener,
        Mesibo.ProfileListener, Mesibo.SyncListener, Mesibo.GroupListener {

    RecyclerView mRecyclerView=null;
    MessageContactAdapter mAdapter=null;
    public static ArrayList<MesiboProfile> mMemberProfiles = new ArrayList<>();
    public static MesiboGroupProfile.Member[] mExistingMembers = null;

    public UserListFragment() {
        // Required empty public constructor
    }

    public TextView mEmptyView ;
    public boolean mContactView =false;
    private Boolean mIsMessageSearching=false;
    private LinearLayout mforwardLayout;
    private ArrayList<MesiboProfile> mUserProfiles = null;
    private ArrayList<MesiboProfile> mSearchResultList = null;
    private ArrayList<MesiboProfile> mAdhocUserList = null;
    public String mSearchQuery = null;
    public int mMode = 0;


    private long mRefreshTs = 0;
    private boolean mFirstOnline = false;

    private MesiboUI.MesiboUserListScreen mScreen = new MesiboUI.MesiboUserListScreen();
    private MesiboUI.MesiboUserListRow mMsgRow = new MesiboUI.MesiboUserListRow();
    public MesiboUI.MesiboUserListScreenOptions mOpts = new MesiboUI.MesiboUserListScreenOptions(); // default

    MesiboProfile mGroupProfile = null;
    Set<String> mGroupMembers = null;
    ArrayList<MesiboProfile> memberProfiles = new ArrayList<MesiboProfile>();
    Boolean mReadingMembers = false;
    MesiboUiDefaults mMesiboUIOptions = null;
    LetterTileProvider mLetterTileProvider = null;

    private boolean mSelectionMode = false;

    private boolean mSyncDone = false;
    private long mUiUpdateTimestamp = 0;
    private TimerTask mUiUpdateTimerTask = null;
    private Timer mUiUpdateTimer = null;
    private Handler mUiUpdateHandler = new Handler(getMainLooper());
    private Runnable mUiUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if(null != mAdapter) {
                mAdapter.notifyChangeInData();
            }
        }
    };

    private MesiboReadSession mDbSession = null;

    public void updateTitle(String title) {
        if(null == mScreen.title) return;
        mScreen.title.setText(title);
    }

    public void updateSubTitle(String title) {
        if(null == mScreen.subtitle) return;
        if (title== null) {
            mScreen.subtitle.setVisibility(View.GONE);
        } else {
            mScreen.subtitle.setVisibility(View.VISIBLE);
            mScreen.subtitle.setText(title);
        }
    }

    public void updateMesiboRow(MesiboProfile user, MessageContactAdapter.SectionCellsViewHolder holder) {
        mMsgRow.reset();
        mMsgRow.screen = mScreen;
        mMsgRow.profile = user;

        if(null == holder)
            return;

        mMsgRow.row = holder.mView;
        //mMsgRow.row = holder.mRowBackground;
        mMsgRow.name = holder.mContactsName;
        mMsgRow.subtitle = holder.mContactsMessage;
        mMsgRow.timestamp = holder.mContactsTime;
        mMsgRow.image = holder.mContactsProfile;
    }


    public boolean onClickUser(MesiboProfile user, MessageContactAdapter.SectionCellsViewHolder holder) {
        if(null == getListener()) return false;

        updateMesiboRow(user, holder);
        return getListener().MesiboUI_onClickedRow(mScreen, mMsgRow);
    }

    public MesiboUI.MesiboUserListScreen getScreen() {
        return mScreen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MesiboImages.init(getActivity());
        mMesiboUIOptions = MesiboUI.getUiDefaults();
        mScreen.parent = getActivity();
        mScreen.options = mOpts;
        mMsgRow.screen = mScreen;

        if(mMode == MODE_MESSAGES) {
            String title = mMesiboUIOptions.messageListTitle;
            if(TextUtils.isEmpty(title)) title = Mesibo.getAppName();
            updateTitle(title);
        }
        else if(mMode == MODE_CONTACTS)
            updateTitle(mMesiboUIOptions.selectContactTitle);
        else if(mMode == MODE_FORWARD)
            updateTitle(mMesiboUIOptions.forwardTitle);
        else if(mMode == MODE_GROUPS)
            updateTitle(mMesiboUIOptions.selectGroupContactsTitle);
        else if(mMode == MODE_EDITGROUP){
            updateTitle(mMesiboUIOptions.selectGroupContactsTitle);
            if(mOpts.groupid > 0) {
                mGroupProfile = Mesibo.getProfile(mOpts.groupid);
            }
        }

        if(mMesiboUIOptions.useLetterTitleImage)
            mLetterTileProvider =  new LetterTileProvider(getActivity(), 60, mMesiboUIOptions.mLetterTitleColors);

        mSearchResultList = new ArrayList<>();
        mUserProfiles = new ArrayList<>();

        int layout = R.layout.fragment_message_contact;

        if(MesiboUI.getUiDefaults().mUserListFragmentLayout != 0)
            layout = MesiboUI.getUiDefaults().mUserListFragmentLayout;

        // Inflate the layout for this fragment
        View view =  inflater.inflate(layout, container, false);
        setHasOptionsMenu(true);

        //getFillData();
        mforwardLayout = (LinearLayout) view.findViewById(R.id.bottom_forward_btn);
        mforwardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMode == MODE_GROUPS) {
                    mAdapter.createNewGroup();
                }else if(mMode == MODE_EDITGROUP){
                    mAdapter.modifyGroupDetail();
                }else if(mMode == MODE_FORWARD) {
                    mAdapter.forwardMessageToContacts();
                }
            }
        });

        mforwardLayout.setBackgroundColor(mMesiboUIOptions.mUserListBottomButtonColor);

        mEmptyView = (TextView) view.findViewById(R.id.emptyview_text);
        setEmptyViewText();

        mUserProfiles = new ArrayList<MesiboProfile>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id. message_contact_frag_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter =new MessageContactAdapter(getActivity(), this, mUserProfiles , mSearchResultList);
        mRecyclerView.setAdapter(mAdapter);
        mScreen.table = mRecyclerView;

        if(MesiboUI.getUiDefaults().mUserListBackgroundColor != 0)
            mRecyclerView.setBackgroundColor(MesiboUI.getUiDefaults().mUserListBackgroundColor);



        return view;
    }


    public void setEmptyViewText() {
        if(mMode == MODE_MESSAGES)
            mEmptyView.setText(MesiboUI.getUiDefaults().emptyMessageListMessage);
        else
            mEmptyView.setText(MesiboUI.getUiDefaults().emptyUserListMessage);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        mScreen.menu = menu;

        if(getListener() == null)
            return ;

        SearchView searchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
        searchView.setIconifiedByDefault(true);
        mScreen.search = searchView; // TBD, this is initialized after onInitScreen
        getListener().MesiboUI_onInitScreen(mScreen);

        MenuItem searchViewItem = menu.findItem(R.id.mesibo_search);

        if(null != searchViewItem && mMesiboUIOptions.enableSearch) {
            MenuItemCompat.setShowAsAction(searchViewItem, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            MenuItemCompat.setActionView(searchViewItem, searchView);
            searchView.setIconifiedByDefault(true);

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Log.d("search View closed", "SEARCHVIEW");
                    mSearchQuery = null;
                    return false;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mAdapter.filter(newText);
                    mAdapter.notifyDataSetChanged();

                    return true;
                }
            });

            searchView.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                              }
                                          }
            );
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Context context = getActivity();
        if(null == context) return false;

        if(item.getItemId() == R.id.mesibo_contacts) {
            MesiboUIManager.launchContactActivity(context, 0, MODE_CONTACTS, 0, false, false, null);
        }else if (item.getItemId() == R.id.mesibo_search) {
            return false;
        }else {
            if(true) return false; // let user define their handler
            if(getListener() == null)
                return false;

            //getListener().MesiboUI_onMenuItemSelected(context, 0, null, item.getItemId());
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mesibo_contacts);
        if(null != item && mMode != MODE_MESSAGES) {
            item.setVisible(false);
        }
    }

    public void filterUsersByName(String newText) {
        if(null == mAdapter) return;
        mAdapter.filter(newText);
        mAdapter.notifyDataSetChanged();
    }

    public void showForwardLayout() {
        mforwardLayout.setVisibility(View.VISIBLE);
        return;
    }

    public void handleEmptyUserList (int userListsize){
        if(userListsize == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

    }

    public void hideForwardLayout() {
        mforwardLayout.setVisibility(View.GONE);
        return;
    }

    private void updateNotificationBadge() {
        if(!mMesiboUIOptions.mEnableNotificationBadge)
            return;
        try {


        } catch (Exception e) {
            mMesiboUIOptions.mEnableNotificationBadge = false;
        }
    }

    public synchronized void addNewMessage(MesiboMessage params) {

        //should only happen for db messages and deleted on unsynced group
        if(params.groupid > 0 && null == params.groupProfile)
            return;


        UserData data = UserData.getUserData(params);

        // This is to inflate other title message like 2users or 6 messages match
        if(mIsMessageSearching && params.isDbMessage()) {
            mAdhocUserList = mAdapter.getActiveUserlist();
            MesiboProfile mup = null;

            if (mAdhocUserList.size() > mAdapter.mCountProfileMatched + 1) {
                int i = mAdapter.mCountProfileMatched ==0?mAdapter.mCountProfileMatched:mAdapter.mCountProfileMatched+1;
                mup = mAdhocUserList.get(i);
                mup.setName(String.valueOf(mAdhocUserList.size() - (i)) + " " + MESSAGE_STRING_USERLIST_SEARCH);
            } else {
                mup = new MesiboProfile();
                mup.setName("1"+ " " + MESSAGE_STRING_USERLIST_SEARCH);
                mAdhocUserList.add(mAdhocUserList.size(), mup);
            }
        }

        MesiboProfile user = params.profile;
        if(params.groupProfile != null)
            user = params.groupProfile;

        //This MUST not happen
        if(null == user) {
            Log.d(TAG, "Should not happen");
        }

        //depending on whether we want to show user in search or group in search
        //TBD, this need to be fixed, implementation
        if(mIsMessageSearching) {
            if(params.groupProfile != null)
                user = params.groupProfile.cloneProfile();
            else
                user = params.profile.cloneProfile();
        }

        if(null == user.other) {
            user.other = new UserData(user);
        }

        data = (UserData) user.other;

        /*if(null == data.getImagePath()){
            data.setImagePath(MesiboImages.getDafaultUserPath());
        }*/

        data.setMessage(params);

        if(params.isRealtimeMessage()) {
            updateNotificationBadge();
        }

        // remove message from existing position so that it can go to top
        //Note that we must do this always as DB messages may be received while someone else is reading it
        if(true || (!params.isDbSummaryMessage() && !params.isDbMessage())) {
            for(int i=0; i< mAdhocUserList.size(); i++) {
                // we are comparing peer and not object as it might have been changed by setUserProfile
                UserData mcd = ((UserData)mAdhocUserList.get(i).other);

                if(null != mcd && params.compare(mcd.getPeer(), mcd.getGroupId())) {
                    mAdhocUserList.remove(i);
                    break;
                }
            }
        }

        if(!params.isDbSummaryMessage() && !params.isDbMessage())
            mAdhocUserList.add(0, user);
        else
            mAdhocUserList.add(user);

        if(null != mUiUpdateTimer) {
            mUiUpdateTimer.cancel();
            mUiUpdateTimer = null;
        }

        if(params.isRealtimeMessage()) {

            long ts = Mesibo.getTimestamp();

            if((ts-mUiUpdateTimestamp) > 2000) {
                mAdapter.notifyChangeInData();
                return;
            }

            long timeout = 2000;
            if((ts - params.ts) < 5000) {
                timeout = 500;
            }

            mUiUpdateTimestamp = ts; // so that it doesn't update even though messages are keep coming

            mUiUpdateTimer = new Timer();
            mUiUpdateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mUiUpdateHandler.post(mUiUpdateRunnable);
                }
            };

            mUiUpdateTimer.schedule(mUiUpdateTimerTask, timeout);
        }
    }

    @Override
    public void Mesibo_onPresence(MesiboPresence msg) {
        if (PRESENCE_TYPING != msg.presence && MesiboPresence.PRESENCE_TYPINGCLEARED != msg.presence && MesiboPresence.PRESENCE_LEFT != msg.presence)
            return;

        if(null == msg || null == msg.profile)
            return;

        if(msg.groupid > 0 && null == msg.groupProfile)
            return;

        MesiboProfile profile = msg.profile;

        UserData data;
        //TBD, why not using params.groupProfile
        if(msg.isGroupMessage()) {
            profile = Mesibo.getProfile(msg.groupid);
            if(null == profile)
                return; //MUST not happen
        }

        data = UserData.getUserData(profile);

        int position = data.getUserListPosition();
        if(position < 0)
            return;

        if(mAdhocUserList.size() <= position)
            return;

        if(PRESENCE_TYPING == msg.presence && msg.isGroupMessage())
            data.setTypingUser(msg.profile);

        try {
            if (profile != mAdhocUserList.get(position))
                return;
        }catch (Exception e) {
            return;
        }

        mAdapter.notifyItemChanged(position);


    }

    @Override
    public void Mesibo_onPresenceRequest(MesiboPresence mesiboPresence) {

    }

    private void updateUiIfLastMessage(MesiboMessage params) {
        if(!params.isLastMessage()) return;

        // TBD, this logic is complicated, need to fix.
        // in any case we are doing non-search read because we need mUsers
        if(!mIsMessageSearching && !TextUtils.isEmpty(mSearchQuery)) {
            mAdapter.filter(mSearchQuery);
        }

        mAdapter.notifyChangeInData();
        updateNotificationBadge();

    }
    @Override
    public void Mesibo_onMessage(MesiboMessage msg) {
        if(msg.isIncomingCall() || msg.isOutgoingCall() || msg.isEndToEndEncryptionStatus()) {
            updateUiIfLastMessage(msg); //?? required
            return;
        }

        if(msg.groupid > 0 && null == msg.groupProfile) {
            updateUiIfLastMessage(msg);
            return;
        }

        addNewMessage(msg);
        updateUiIfLastMessage(msg);
        return;
    }

    @Override
    public void Mesibo_onMessageStatus(MesiboMessage msg) {

        for(int i=0; i< mUserProfiles.size(); i++) {
            UserData mcd = ((UserData)mUserProfiles.get(i).other);

            if(null == mcd) continue;

            if (mcd.getmid() != 0 && mcd.getmid() == msg.mid) {
                mcd.setMessage(msg);
                if(msg.isDeleted()) {
                    mcd.setMessage(MesiboUI.getUiDefaults().deletedMessageTitle);
                    mcd.setDeletedMessage(true);
                }
                mAdapter.notifyItemChanged(i);
            }
        }

    }

    @Override
    public void Mesibo_onMessageUpdate(MesiboMessage msg) {
        Mesibo_onMessageStatus(msg); // To be tested
    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        status = Mesibo.getConnectionStatus();

        if(status == Mesibo.STATUS_ONLINE) {

            // we are doing this in case contacts were not loaded or user started UI before start()
            if(!mFirstOnline && Mesibo.getLastProfileUpdateTimestamp() > mRefreshTs) {
                showUserList(MESIBO_INTITIAL_READ_USERLIST);
            }

            mFirstOnline = true;

            // updated App Name
            if(mMode == MODE_MESSAGES) {
                String title = mMesiboUIOptions.messageListTitle;
                if(TextUtils.isEmpty(title)) title = Mesibo.getAppName();
                updateTitle(title);
            }
            updateSubTitle(mMesiboUIOptions.onlineIndicationTitle);
        }
        else if(status == Mesibo.STATUS_CONNECTING)
            updateSubTitle(mMesiboUIOptions.connectingIndicationTitle);
        else if(status == Mesibo.STATUS_SUSPENDED) {
            updateSubTitle(mMesiboUIOptions.suspendIndicationTitle);
            Utils.showServicesSuspendedAlert(getActivity());
        }
        else if(status == Mesibo.STATUS_NONETWORK)
            updateSubTitle(mMesiboUIOptions.noNetworkIndicationTitle);
        else if(status == Mesibo.STATUS_SHUTDOWN) {
            getActivity().finish();
        }
        else
            updateSubTitle(mMesiboUIOptions.offlineIndicationTitle);
    }

    @Override
    public void onResume(){
        super.onResume();

        showUserList(MESIBO_INTITIAL_READ_USERLIST);
        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());

        Utils.showServicesSuspendedAlert(getActivity());
    }

    @Override
    public void onPause(){
        super.onPause();

        Mesibo.removeListener(this);
    }


    @Override public void onStop(){
        super.onStop();
        if(mMode == MODE_FORWARD || mMode == MODE_GROUPS || mMode == MODE_EDITGROUP) {
            for (MesiboProfile d : mUserProfiles) {
                d.uiFlags &= ~MesiboProfile.FLAG_MARKED;
            }
        }
    }

    public void onLongClick() {

    }

    @Override
    public void Mesibo_onSync(MesiboReadSession session, int count) {
        final int c = count;
        if(count > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showUserList(MESIBO_INTITIAL_READ_USERLIST);
                }
            });

        }
    }

    public void showUserList(int readCount){
        Log.d("showUserList", "showUserList");

        setEmptyViewText();

        if(mMode == MODE_MESSAGES) {
            mRefreshTs = Mesibo.getTimestamp();

            Mesibo.addListener(this);
            mAdhocUserList = mUserProfiles;
            mUserProfiles.clear();
            mAdapter.onResumeAdapter();

            MesiboReadSession.endAllSessions();

            mDbSession = MesiboReadSession.createReadSummarySession(this);
            if(null != mOpts)
                mDbSession.setQuery(mOpts.readQuery);

            int rcount = mDbSession.read(readCount);

            if(false && rcount < readCount && !mSyncDone) {
                mSyncDone = true;
                mDbSession.sync(readCount, this);
            }

        } else {
            mUserProfiles.clear();

            ArrayList profiles = Mesibo.getSortedUserProfiles();

            if(null != profiles && profiles.size() > 0 && !TextUtils.isEmpty(mMesiboUIOptions.createGroupTitle) && mMode == MODE_CONTACTS) {
                MesiboProfile user = new MesiboProfile();
                user.address = mMesiboUIOptions.createGroupTitle;
                user.setName(mMesiboUIOptions.createGroupTitle);
                user.setString("status", MesiboConfiguration.CREATE_NEW_GROUP_MESSAGE_STRING);
                user.setPrivate(true);

                UserData ud = new UserData(user);
                Bitmap b = MesiboImages.getDefaultGroupBitmap();
                ud.setImageThumbnail(b);
                ud.setImage(b);
                ud.setFixedImage(true); // so that it does not reset image on setUser
                user.other = ud;
                ud.setMessage(user.getString("status", ""));
                mUserProfiles.add(user);
            }

            if(mMode == MODE_FORWARD && mMesiboUIOptions.showRecentInForward) {
                mUserProfiles.addAll(Mesibo.getRecentUserProfiles());
                if(mUserProfiles.size() > 0) {
                    MesiboProfile tempUserProfile = new MesiboProfile();
                    tempUserProfile.setName(String.valueOf(mMesiboUIOptions.recentUsersTitle));
                    mUserProfiles.add(0,tempUserProfile);
                }
                MesiboProfile tempUserProfile1 = new MesiboProfile();
                tempUserProfile1.setName(String.valueOf(mMesiboUIOptions.allUsersTitle));
                mUserProfiles.add(tempUserProfile1);

            }

            if(mMode == MODE_EDITGROUP) {
                if(0 == memberProfiles.size() && !mReadingMembers) {
                    mReadingMembers = true;
                    Mesibo.addListener(this);
                    MesiboProfile profile = getProfile(mOpts.groupid);
                    profile.getGroupProfile().getMembers(100, true, this);
                    return;
                }

                mUserProfiles.addAll(memberProfiles);

                // add separator
                MesiboProfile tempUserProfile = new MesiboProfile();
                tempUserProfile.setName(String.valueOf(mMesiboUIOptions.groupMembersTitle));
                mUserProfiles.add(0,tempUserProfile);


                // add separator for all profiles
                MesiboProfile tempUserProfile1 = new MesiboProfile();
                tempUserProfile1.setName(String.valueOf(mMesiboUIOptions.allUsersTitle));
                mUserProfiles.add(tempUserProfile1);

            }

            mUserProfiles.addAll(Mesibo.getSortedUserProfiles());

            for (int i = mUserProfiles.size() - 1; i >= 0; i--) {
                MesiboProfile user = mUserProfiles.get(i);
                if (TextUtils.isEmpty(user.address) && user.groupid==0) {
                    if (!user.getName().equalsIgnoreCase(mMesiboUIOptions.allUsersTitle) && !user.getName().equalsIgnoreCase(mMesiboUIOptions.recentUsersTitle)
                            && !user.getName().equalsIgnoreCase(mMesiboUIOptions.groupMembersTitle))
                        mUserProfiles.remove(i);
                }else if (TextUtils.isEmpty(user.getName()) && user.groupid > 0) {
                    mUserProfiles.remove(i);

                } else if(mMode == MODE_EDITGROUP || mMode == MODE_GROUPS) {
                    if (user.groupid > 0) {
                        mUserProfiles.remove(i);
                    } else {
                        if(!TextUtils.isEmpty(user.address) && null != mGroupMembers && mGroupMembers.contains(user.address)) {
                            user.uiFlags = user.uiFlags | MesiboProfile.FLAG_MARKED;
                            showForwardLayout();
                        }
                    }
                }
            }

        }
        mAdapter.notifyChangeInData();
    }

    private void updateContacts(MesiboProfile userProfile) {
        if(null == userProfile) {
            showUserList(MESIBO_INTITIAL_READ_USERLIST);
            return;
        }

        if(null == userProfile.other)
            return;

        UserData data = UserData.getUserData(userProfile);
        int position = data.getUserListPosition();
        if(position < 0) return;

        if(userProfile.isDeleted() || (mMode == MODE_MESSAGES  && userProfile.getTotalMessageCount() == 0)) {
            mUserProfiles.remove(position);
            mAdapter.notifyDataSetChanged();
            return;
        }

        if(position >= 0)
            mAdapter.notifyItemChanged(position);

        return;

    }


    @Override
    public boolean Mesibo_onGetProfile(MesiboProfile userProfile) {
        return false;
    }

    @Override
    public void Mesibo_onProfileUpdated(MesiboProfile userProfile) {

        if(null != userProfile && null == userProfile.other)
            return;

        if(Mesibo.isUiThread()) {
            updateContacts(userProfile);
            return;
        }

        final MesiboProfile profile = userProfile;
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                updateContacts(profile);
            }
        });

    }

    @Override
    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
        mExistingMembers = members;
        for(MesiboGroupProfile.Member m : members) {
            memberProfiles.add(m.getProfile());
        }
        if(memberProfiles.size() > 0)
            showUserList(MESIBO_INTITIAL_READ_USERLIST);
    }

    @Override
    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {

    }

    @Override
    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long error) {

    }

    public class MessageContactAdapter extends SelectableAdapter <RecyclerView.ViewHolder> {
        private int mBackground=0;
        private Context mContext=null;
        private ArrayList<MesiboProfile> mDataList=null;
        private ArrayList<MesiboProfile> mUsers=null;
        private ArrayList<MesiboProfile> mSearchResults=null;
        private UserListFragment mHost;
        private MesiboUI.MesiboUserListScreenOptions mOpts;
        private SparseBooleanArray mSelectionItems;
        public int mCountProfileMatched = 0;

        public final static int SECTION_HEADER = 100;
        public final static int SECTION_CELLS = 300;

        public   class SectionHeaderViewHolder extends MesiboRecycleViewHolder {
            public  TextView mSectionTitle=null;


            public SectionHeaderViewHolder(View itemView) {
                super(itemView);
                mSectionTitle = (TextView) itemView.findViewById(R.id.section_header);
            }
        }

        public   class SectionCellsViewHolder extends MesiboRecycleViewHolder  {
            public String mBoundString=null;
            public  View mView=null;
            public  ImageView mContactsProfile=null;
            public  TextView mContactsName=null;
            public  TextView mContactsTime=null;
            public  EmojiconTextView mContactsMessage=null;
            public  ImageView mContactsDeliveryStatus=null;
            public  TextView mNewMesAlert=null;
            public RelativeLayout mHighlightView = null;
            public RelativeLayout mRowBackground = null;
            public int position = 0;

            public SectionCellsViewHolder(View view) {
                super(view);
                mView = view;
                mRowBackground = (RelativeLayout) view.findViewById(R.id.top_layout);
                mContactsProfile = (ImageView) view.findViewById(R.id.mes_rv_profile);
                mContactsName = (TextView) view.findViewById(R.id.mes_rv_name);
                mContactsTime = (TextView) view.findViewById(R.id.mes_rv_date);
                mContactsMessage = (EmojiconTextView) view.findViewById(R.id.mes_cont_post_or_details);
                mContactsDeliveryStatus = (ImageView)view.findViewById(R.id.mes_cont_status);
                mNewMesAlert = (TextView) view.findViewById(R.id.mes_alert);
                mHighlightView = (RelativeLayout) view.findViewById(R.id.highlighted_view);
            }

        }

        public MessageContactAdapter(Context context, UserListFragment host, ArrayList<MesiboProfile> list,ArrayList<MesiboProfile> searchResults) {

            this.mContext = context;
            mHost = host;
            mOpts = mHost.mOpts;
            mUsers = list;
            mSearchResults = searchResults;
            mDataList = list;
            mSelectionItems = new SparseBooleanArray();

        }


        public ArrayList<MesiboProfile> getActiveUserlist() {
            if(mIsMessageSearching)
                return mSearchResults;
            else
                return mUsers;
        }
        @Override
        public int getItemViewType(int position) {
            String address = mDataList.get(position).address;

            if (address == null && mDataList.get(position).groupid <= 0)
                return SECTION_HEADER;
            else
                return SECTION_CELLS;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == SECTION_HEADER) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_header_title, parent, false);
                return new SectionHeaderViewHolder(v);

            }

            View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list, parent, false);
            return new SectionCellsViewHolder(view);

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, @SuppressLint("RecyclerView") final int position) {

            if(vh.getItemViewType()== SECTION_HEADER) {

                ((SectionHeaderViewHolder)vh).mSectionTitle.setText( mDataList.get(position).getName());
                return;
            }

            final int pos = position;
            final MesiboProfile user = mDataList.get(position);

            final SectionCellsViewHolder holder = (SectionCellsViewHolder) vh;

            holder.position = position;

            UserData userdata = UserData.getUserData(user);
            userdata.setUser(user); // in case user is changed dynamically
            userdata.setUserListPosition(position);

            final UserData data = userdata;
            MesiboUiDefaults opts = MesiboUI.getUiDefaults();
            if(mSelectionMode && isSelected(position)) {
                if (MesiboUI.getUiDefaults().mUserListSelectedBackgroundColor != 0)
                    holder.mView.setBackgroundColor(MesiboUI.getUiDefaults().mUserListSelectedBackgroundColor);
            } else {
                if (MesiboUI.getUiDefaults().mUserListBackgroundColor != 0)
                    holder.mView.setBackgroundColor(MesiboUI.getUiDefaults().mUserListBackgroundColor);
            }

            holder.mContactsName.setText(data.getUserName());

            if (mHost.mMode == MODE_MESSAGES && (opts.clearedMessageContactTimestamp || data.getStatus() != Mesibo.MSGSTATUS_EMPTY)) {
                holder.mContactsTime.setVisibility(View.VISIBLE);
                holder.mContactsTime.setText(data.getTime());
            } else {
                holder.mContactsTime.setVisibility(View.GONE);
            }

            int imageDrawableId = 0;
            Drawable imageDrawable = null;
            int padding = 5;
            MesiboMessage msg = data.getMessage();
            if (null != msg && msg.isDeleted()) {
                imageDrawableId = DELETED_DRAWABLE;
                imageDrawable = MesiboImages.getDeletedMessageDrawable();
            } else if (null != msg && msg.hasImage()) {
                imageDrawableId = IMAGE_ICON;
            } else if (null != msg && msg.hasVideo()) {
                imageDrawableId = VIDEO_ICON;
            } else if (null != msg && msg.hasFile()) {
                imageDrawableId = ATTACHMENT_ICON;
            } else if (null != msg && msg.isMissedCall() && msg.isVideoCall()) {
                imageDrawableId = MISSED_VIDEOCALL_DRAWABLE;
                imageDrawable = getMissedCallDrawable(true);
            } else if (null != msg && msg.isMissedCall() && msg.isVoiceCall()) {
                imageDrawableId = MISSED_VOICECALL_DRAWABLE;
                imageDrawable = getMissedCallDrawable(false);
            } else if (null != msg && msg.hasLocation()) {
                imageDrawableId = LOCATION_ICON;
            } else {
                imageDrawableId = 0;
                padding = 0;
            }

            boolean typing = data.isTyping();
            if(typing) {
                imageDrawableId = 0;
                padding = 0;
            }

            if (mHost.mMode == MODE_MESSAGES) {
                if(null != imageDrawable) {
                    holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawable, null, null, null);
                } else {
                    holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawableId, 0, 0, 0);
                }

                holder.mContactsMessage.setCompoundDrawablePadding(padding);

                if(false) {
                    // just for testing
                    holder.mContactsMessage.setText(userdata.getLastMessage());
                    return;
                }

                if(!typing) {
                    holder.mContactsMessage.setText(userdata.getLastMessage());
                    holder.mContactsMessage.setTextColor(mMesiboUIOptions.mUserListStatusColor);
                }
                else {
                    MesiboProfile typingProfile = data.getTypingProfile();

                    String typingText = mMesiboUIOptions.typingIndicationTitle;
                    if(null != typingProfile) {
                        typingText = typingProfile.getName() + " is " + mMesiboUIOptions.typingIndicationTitle;
                    }

                    holder.mContactsMessage.setText(typingText);
                    holder.mContactsMessage.setTextColor(mMesiboUIOptions.mUserListTypingIndicationColor);
                }
            }
            else
                holder.mContactsMessage.setText(user.getString("status", ""));

            Bitmap b = data.getThumbnail(mLetterTileProvider);

            Drawable d = new RoundImageDrawable(b);

            holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));

            if (mHost.mMode == MODE_MESSAGES && data.getUnreadCount() > 0) {
                holder.mNewMesAlert.setVisibility(View.VISIBLE);
                holder.mNewMesAlert.setText(String.valueOf(data.getUnreadCount()));

            } else {
                holder.mNewMesAlert.setVisibility(View.INVISIBLE);
            }

            holder.mContactsDeliveryStatus.setVisibility(View.GONE);
            if (!typing && mHost.mMode == MODE_MESSAGES) {
                int sts = data.getStatus();

                if(sts != Mesibo.MSGSTATUS_EMPTY)
                	holder.mContactsDeliveryStatus.setVisibility(View.VISIBLE);

                if (sts == Mesibo.MSGSTATUS_RECEIVEDREAD || sts == Mesibo.MSGSTATUS_RECEIVEDNEW || sts == Mesibo.MSGSTATUS_CALLMISSED || sts == Mesibo.MSGSTATUS_CUSTOM || data.isDeletedMessage()) {
                    holder.mContactsDeliveryStatus.setVisibility(View.GONE);
                } else {
                    holder.mContactsDeliveryStatus.setImageBitmap(MesiboImages.getStatusImage(sts));
                }
            }

            if(mMode == MODE_FORWARD || mMode == MODE_GROUPS || mMode == MODE_EDITGROUP) {
                if((mDataList.get(position).uiFlags & MesiboProfile.FLAG_MARKED ) == MesiboProfile.FLAG_MARKED) {
                    holder.mHighlightView.setVisibility(View.VISIBLE);
                    mHost.showForwardLayout();
                }else {
                    holder.mHighlightView.setVisibility(View.GONE);
                }
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mSelectionMode) {
                        if (mHost.mMode == MODE_MESSAGES) {
                            toggleSelectedUser(position);
                        }
                        return;
                    }

                    if (mMode == MODE_FORWARD || mMode == MODE_GROUPS || mMode == MODE_EDITGROUP) {
                        if ((user.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                            user.uiFlags = user.uiFlags & ~MesiboProfile.FLAG_MARKED;
                        } else {
                            user.uiFlags = user.uiFlags | MesiboProfile.FLAG_MARKED;
                        }

                        //notifyItemChanged(pos);
                        notifyDataSetChanged();

                        if (isForwardContactsSelected()) {
                            mHost.showForwardLayout();
                        } else {
                            mHost.hideForwardLayout();
                        }

                    } else {

                        //TBD, it's checking user name, instead we should set flag
                        if(null != user.getName() && null != mMesiboUIOptions.createGroupTitle && user.getName().equals(mMesiboUIOptions.createGroupTitle) && mMode == MODE_CONTACTS){
                            MesiboUIManager.launchContactActivity(getActivity(), 0, MODE_GROUPS, 0, false, false, null);
                            getActivity().finish();
                            return ;
                        }

                        Context context = v.getContext();

                        boolean handledByApp = onClickUser(user, holder);

                        if(!handledByApp) {
                            MesiboUI.MesiboMessageScreenOptions opts = new MesiboUI.MesiboMessageScreenOptions();
                            opts.forwardId = mOpts.forwardId;
                            opts.profile = user;
                            MesiboUIManager.launchMessagingActivity(getActivity(), opts);
                            mOpts.forwardId = 0;
                            if (mMode != MODE_MESSAGES)
                                getActivity().finish();
                        } else {
                            mOpts.forwardId = 0;
                        }
                    }
                }
            });

            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public boolean onLongClick(View v) {
                    if(mMode == MODE_FORWARD || mMode == MODE_GROUPS || mMode == MODE_EDITGROUP)
                        return true;

                    if(mMode != MODE_MESSAGES)
                        return  true;

                    if (!mSelectionMode && !mIsMessageSearching ) {
                        mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
                        Utils.setActionModeBackgroundColor(mActionMode, MesiboUI.getUiDefaults().mActionbarColor);
                        mSelectionMode = true;
                        toggleSelectedUser(position);
                    }
                    return true;
                }
            });

            if(getListener() == null) return;

            updateMesiboRow(user, holder);
            getListener().MesiboUI_onUpdateRow(mScreen, mMsgRow, true);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if(holder instanceof MesiboRecycleViewHolder) {
                ((MesiboRecycleViewHolder) holder).cleanup();
                return;
            }
        }

        @Override
        public int getItemCount() {
            handleEmptyUserList(mDataList.size());
            return mDataList.size();
        }

        public void notifyChangeInData(){

            mUiUpdateTimestamp = Mesibo.getTimestamp();
            mDataList = getActiveUserlist();
            notifyDataSetChanged();
        }

        public void onResumeAdapter() {
            mSearchResults.clear();
            mIsMessageSearching = false;
            mUsers.clear();
            mDataList = mUsers;
        }

        public Boolean isForwardContactsSelected() {
            Boolean retValue = false;
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED )==MesiboProfile.FLAG_MARKED) {
                    retValue = true;
                }
            }
            return  retValue;
        }

        public void createNewGroup (){
            mMemberProfiles.clear();
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED )==MesiboProfile.FLAG_MARKED) {
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(getActivity(), 0);
            getActivity().finish();
            return ;
        }

        public void modifyGroupDetail() {
            mMemberProfiles.clear();
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED )==MesiboProfile.FLAG_MARKED) {
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }

            MesiboUIManager.launchGroupActivity(getActivity(), mOpts.groupid);
            getActivity().finish();
            return ;
        }

        public void forwardMessageToContacts () {
            mMemberProfiles.clear();
            int i = 0;
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                    d.uiFlags &= ~MesiboProfile.FLAG_MARKED;
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }

            if(mMemberProfiles.size() == 0) {
                return;
            }

            for(i=0; i< mMemberProfiles.size(); i++) {
                MesiboProfile user = mMemberProfiles.get(i);
                UserData data = (UserData)(user).other;
                MesiboMessage msg = user.newMessage();

                if(null != mOpts.forwardIds) {
                    msg.setForwarded(mOpts.forwardIds);
                    msg.send();
                }
                else if(!TextUtils.isEmpty(mOpts.forwardedMessage)) {
                    msg.message = mOpts.forwardedMessage;
                    msg.send();
                }
            }

            if(!mOpts.forwardAndClose && mMemberProfiles.size() ==1 ) {
                MesiboProfile user = mMemberProfiles.get(0);
                UserData data = (UserData)(user).other;
                boolean handledByApp = onClickUser(user, null);
                if(!handledByApp) {
                    MesiboUI.MesiboMessageScreenOptions opts = new MesiboUI.MesiboMessageScreenOptions();
                    opts.profile = user;
                    MesiboUIManager.launchMessagingActivity(getActivity(), opts);
                }

            }

            getActivity().finish();
            mOpts.forwardId = 0;
            return ;
        }

        public void filter(String text) {
            mSearchQuery = text;
            mCountProfileMatched = 0;
            mSearchResults.clear();
            mIsMessageSearching = false;
            if(TextUtils.isEmpty(text)){
                mDataList = mUsers;
            } else {
                //mSearchResults = new ArrayList<MesiboProfile>();
                mDataList = mSearchResults;
                text = text.toLowerCase();

                for(MesiboProfile item: mUsers){
                    if(item.getName().toLowerCase().contains(text) || item.getName().equals(mMesiboUIOptions.allUsersTitle) || item.getName().equals(mMesiboUIOptions.recentUsersTitle) || item.getName().equals(mMesiboUIOptions.groupMembersTitle)) {
                        mSearchResults.add(item);
                    }
                }
                if(mSearchResults.size() > 0 && mMode == MODE_MESSAGES) {

                    MesiboProfile tempUserProfile = new MesiboProfile();
                    mCountProfileMatched = mSearchResults.size();
                    tempUserProfile.setName(String.valueOf(mSearchResults.size())+" "+ USERS_STRING_USERLIST_SEARCH);
                    mSearchResults.add(0, tempUserProfile);
                }

                mDataList = mSearchResults;
                setEmptyViewText();

                if(mMode == MODE_MESSAGES) {
                    mEmptyView.setText(MesiboUI.getUiDefaults().emptySearchListMessage);
                    if (!TextUtils.isEmpty(text)) {
                        mIsMessageSearching = true;
                        //Do nott pass SUMMARY_FLAG when searching
                        MesiboReadSession rbd = new MesiboReadSession(UserListFragment.this);
                        rbd.setQuery(text);
                        rbd.read(MESIBO_SEARCH_READ_USERLIST);

                    }
                }
            }

            //notifyDataSetChanged();
        }
    }

    private void toggleSelectedUser(int position) {
        mAdapter.toggleSelection(position);
        mAdapter.notifyItemChanged(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            hideInContextUserInterface();
        } else {
            updateContextUserInterfaceCount(count);
        }
    }

    public void clearSelections() {
        List<Integer> selection = mAdapter.getSelectedItems();
        mAdapter.clearSelectedItems();
        for (Integer i : selection) {
            mAdapter.notifyItemChanged(i);
        }
    }

    private boolean onActionSelectedItems(int item, List<Integer> selection) {
        ArrayList<MesiboProfile> users = mAdapter.getActiveUserlist();
        for (Integer i : selection) {
            MesiboProfile profile = users.get(i);
            if(item == R.id.menu_unread) {
                profile.markUnread();
            } else if(item == R.id.menu_read) {
                profile.markRead(true);
            } else if(R.id.menu_remove == item) {
                profile.deleteMessages();
            } else if(R.id.menu_clear == item) {
                profile.clearMessages(MesiboUI.getUiDefaults().clearMessageMode);
            }
        }

        showUserList(MESIBO_INTITIAL_READ_USERLIST);
        return true;
    }

    private boolean onContextMenuClicked(int item) {
        List<Integer> selection = mAdapter.getSelectedItems();
        clearSelections();

        if(item == R.id.menu_unread) {
            try {
                return onActionSelectedItems(item, selection);
            } catch (Exception e) {
                return true;
            }
        }

        MesiboUiDefaults opts = MesiboUI.getUiDefaults();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        if(R.id.menu_remove == item)
            builder.setTitle(opts.deleteMessagesTitle);
        else if(R.id.menu_clear == item)
            builder.setTitle(opts.clearMessagesTitle);

        String[] items = {opts.yes, opts.cancel};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(1 == which) {
                    return;
                }
                try {
                    onActionSelectedItems(item, selection);
                } catch (Exception e) {
                }
            }
        });

        builder.show();
        return true;
    }

    private void closeContextUserInterfaceClosed() {
        clearSelections();
        mSelectionMode = false;
    }

    private void hideInContextUserInterface() {
        if(null == mActionMode)
            return;
        mActionMode.finish();
    }


    private void updateContextUserInterfaceCount(int count) {
        if(null == mActionMode)
            return;

        mActionMode.setTitle(String.valueOf(count));
        mActionMode.invalidate();
    }

    private ActionMode mActionMode = null;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = "ActionModeCallback";

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            mode.getMenuInflater().inflate(R.menu.selected_contact, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            if(onContextMenuClicked(item.getItemId())) {
                mode.finish();
                closeContextUserInterfaceClosed();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            closeContextUserInterfaceClosed();
            mActionMode = null;
        }
    }

}
