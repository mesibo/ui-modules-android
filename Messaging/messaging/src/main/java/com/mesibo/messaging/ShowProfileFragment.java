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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_EDITGROUP;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboPhoneContact;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.mediapicker.AlbumListData;
import com.mesibo.mediapicker.AlbumPhotosData;
import com.mesibo.mediapicker.MediaPicker;

import java.util.ArrayList;


public class ShowProfileFragment extends Fragment implements Mesibo.MessageListener, MesiboProfile.Listener, Mesibo.GroupListener {
    private static final int MAX_THUMBNAIL_GALERY_SIZE = 35;

    private MesiboProfile mUser;

    private OnFragmentInteractionListener mListener;
    private ArrayList<String> mThumbnailMediaFiles;
    private LinearLayout mGallery;
    private int mMediaFilesCounter=0;
    private TextView mMediaCounterView;
    private ArrayList<AlbumListData>  mGalleryData;
    private ImageView mMessageBtn;
    private CardView mMediaCardView;
    private CardView mStatusPhoneCard;
    private CardView mGroupMembersCard;
    private CardView mExitGroupCard;
    private TextView mExitGroupText;
    private static int VIDEO_FILE = 2;
    private static int IMAGE_FILE = 1;
    private static int OTHER_FILE = 2;



    RecyclerView mRecyclerView ;
    RecyclerView.Adapter mAdapter;
    LinearLayout mAddMemebers, mEditGroup;
    ArrayList<MesiboGroupProfile.Member> mGroupMemberList = new ArrayList<>();
    MesiboGroupProfile.Member mSelfMember;

    LinearLayout mll ;
    TextView mStatus ;
    TextView mStatusTime ;
    TextView mMobileNumber ;
    TextView mPhoneType ;

    private static Bitmap mDefaultProfileBmp;
    private MesiboReadSession mReadSession = null;

    public ShowProfileFragment() {
    }

    public static ShowProfileFragment newInstance(MesiboProfile userdata) {
        ShowProfileFragment fragment = new ShowProfileFragment();
        fragment.mUser = userdata;
        fragment.mUser.addListener(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View v =  inflater.inflate(R.layout.fragment_profile_view, container, false);

        mDefaultProfileBmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_user_image);
        mThumbnailMediaFiles = new ArrayList<>();
        mGalleryData = new ArrayList<>();

        AlbumListData Images = new AlbumListData();
        Images.setmAlbumName("Images");
        AlbumListData Video = new AlbumListData();
        Video.setmAlbumName("Videos");
        AlbumListData Documents = new AlbumListData();
        Documents.setmAlbumName("Documents");
        mGalleryData.add(Images);
        mGalleryData.add(Video);
        mGalleryData.add(Documents);

        mMediaCardView = (CardView) v.findViewById(R.id.up_media_layout);
        mMediaCardView.setVisibility(GONE);
        Mesibo.addListener(this);

        mReadSession = mUser.createReadSession(this);
        mReadSession.enableFiles(true);
        mReadSession.enableReadReceipt(true);
        mReadSession.read(100);

        mMessageBtn = (ImageView) v.findViewById (R.id.up_message_btn);
        mMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.showprofile_memebers_rview);

        // change in file
        mAddMemebers = (LinearLayout) v.findViewById(R.id.showprofile_add_member);
        mAddMemebers.setVisibility(GONE);

        mEditGroup = (LinearLayout) v.findViewById(R.id.showprofile_editgroup);
        mEditGroup.setVisibility(GONE);


        mll = (LinearLayout) v.findViewById(R.id.up_status_card);
        mStatus = (TextView)v.findViewById(R.id.up_status_text);
        mStatusTime =(TextView) v.findViewById(R.id.up_status_update_time);
        mMobileNumber =(TextView) v.findViewById(R.id.up_number);
        mPhoneType =(TextView) v.findViewById(R.id.up_phone_type);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new GroupMemeberAdapter(getActivity(), mGroupMemberList);
        mRecyclerView.setAdapter(mAdapter);
        ///
        mGallery = (LinearLayout) v.findViewById(R.id.up_gallery);
        mMediaCounterView = (TextView) v.findViewById(R.id.up_media_counter);
        mMediaCounterView.setText(String.valueOf(mMediaFilesCounter)+"\u3009 ");

        mStatusPhoneCard = (CardView) v.findViewById(R.id.status_phone_card) ;
        mGroupMembersCard = (CardView) v.findViewById(R.id.showprofile_members_card) ;
        mExitGroupCard = (CardView) v.findViewById(R.id.group_exit_card);
        mExitGroupText = (TextView) v.findViewById(R.id.group_exit_text);
        mExitGroupCard.setVisibility(GONE);
        mExitGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelfMember.isOwner())
                    mUser.getGroupProfile().deleteGroup();
                else
                    mUser.getGroupProfile().leave();

                getActivity().finish();
            }
        });

        CardView e2ecard = (CardView) v.findViewById(R.id.e2ee_card);
        e2ecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MesiboUI.showEndToEndEncryptionInfo(getActivity(), mUser.getAddress(), mUser.getGroupId());
            }
        });

        SwitchCompat switchCompat = (SwitchCompat)v.findViewById(R.id.block_m_switch);
        switchCompat.setChecked(mUser.isMessageBlocked());
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUser.blockMessages(isChecked);
                mUser.save();
            }
        });

        SwitchCompat blockGroupSwitch = (SwitchCompat)v.findViewById(R.id.block_g_switch);
        blockGroupSwitch.setChecked(mUser.isGroupMessageBlocked());
        blockGroupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUser.blockGroupMessages(isChecked);
                mUser.save();
            }
        });

        SwitchCompat blockVideoCallSwitch = (SwitchCompat)v.findViewById(R.id.block_v_switch);
        blockVideoCallSwitch.setChecked(mUser.isVideoCallBlocked());
        blockVideoCallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUser.blockVideoCalls(isChecked);
                mUser.save();
            }
        });

        SwitchCompat blockCallSwitch = (SwitchCompat)v.findViewById(R.id.block_c_switch);
        blockCallSwitch.setChecked(mUser.isCallBlocked());
        blockCallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // if calls are blocked, video calls are blcoked too amd hence switch has no meaning
                v.findViewById(R.id.block_v_layout).setVisibility(isChecked?GONE:VISIBLE);
                mUser.blockCalls(isChecked);
                mUser.save();
            }
        });

        SwitchCompat blockProfileSwitch = (SwitchCompat)v.findViewById(R.id.block_p_switch);
        blockProfileSwitch.setChecked(mUser.isProfileSubscriptionBlocked());
        blockProfileSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) mUser.subscribe(true);
                mUser.blockProfileSubscription(isChecked);
                mUser.save();
            }
        });


        if(mUser.isGroup()) {
            v.findViewById(R.id.block_layout).setVisibility(GONE);
        }

        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.up_open_media);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mGalleryData.size() > 0) {
                    for( int i=mGalleryData.size()-1;i>=0;i--){
                        AlbumListData tempdata = mGalleryData.get(i);
                        if(tempdata.getmPhotoCount()==0)
                            mGalleryData.remove(tempdata);
                    }

                    MediaPicker.launchAlbum(getActivity(), mGalleryData);
                }
            }
        });

        return v;
    }

    private void addThumbnailToGallery(MesiboMessage msg) {
        View thumbnailView = null;
        String path = msg.getFilePath();
        mThumbnailMediaFiles.add(path);
        if (mThumbnailMediaFiles.size() < MAX_THUMBNAIL_GALERY_SIZE) {
            if (null != path) {
                thumbnailView = getThumbnailView(msg.getThumbnail(), msg.hasVideo());
                if(null != thumbnailView) {
                    thumbnailView.setClickable(true);
                    thumbnailView.setTag(mMediaFilesCounter - 1);
                    thumbnailView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int index = (int) v.getTag();
                            //String path = (String) mThumbnailMediaFiles.get(index);
                            MediaPicker.launchImageViewer(getActivity(), mThumbnailMediaFiles, index);
                        }
                    });
                    mGallery.addView(thumbnailView);
                }
            }
        }
    }


    View       getThumbnailView (Bitmap bm, Boolean isVideo) {
        Context activity = getActivity();
        if(null == activity) return null;
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view  = layoutInflater.inflate(R.layout.profile_view_horizontal_gallery, null, false);
        ImageView thumbpic = (ImageView) view.findViewById(R.id.mp_thumbnail);
        thumbpic.setImageBitmap(bm);
        ImageView layer = (ImageView) view.findViewById(R.id.video_layer);
        layer.setVisibility(isVideo?VISIBLE:GONE);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = (int)((metrics.widthPixels-50)/(5)); //number of pics in media view
        view.setLayoutParams(new ViewGroup.LayoutParams(width,width));
        return  view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TBD, we need to provide option to user
        //inflater.inflate(R.menu.user_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void Mesibo_onMessage(MesiboMessage msg) {
        if(!msg.hasFile()) return;
        mMediaCardView.setVisibility(VISIBLE);
        mMediaFilesCounter++;
        mMediaCounterView.setText(String.valueOf(mMediaFilesCounter)+"\u3009 ");
        AlbumPhotosData newPhoto = new AlbumPhotosData();
        newPhoto.setmPictueUrl(msg.getFilePath());
        newPhoto.setmSourceUrl(msg.getFilePath());
        AlbumListData tempAlbum;
        int index=0;
        if(msg.hasVideo())
            index = 1;
        else if (!msg.hasImage())
            index = 2;
        tempAlbum = mGalleryData.get(index);

        if(tempAlbum.getmPhotosList()==null) {
            ArrayList<AlbumPhotosData> newPhotoList = new ArrayList<>();
            tempAlbum.setmPhotosList(newPhotoList);
        }
        if(tempAlbum.getmPhotosList().size()==0) {
            tempAlbum.setmAlbumPictureUrl(msg.getFilePath());
        }
        tempAlbum.getmPhotosList().add(newPhoto);
        tempAlbum.setmPhotoCount(tempAlbum.getmPhotosList().size());
        addThumbnailToGallery(msg);
        return;
    }

    @Override
    public void Mesibo_onMessageStatus(MesiboMessage msg) {

    }

    @Override
    public void Mesibo_onMessageUpdate(MesiboMessage mesiboMessage) {

    }

    public boolean parseGroupMembers(MesiboGroupProfile.Member[] users) {
        if(null == users) return false;

        String phone = Mesibo.getAddress();
        //MUST not happen
        if(TextUtils.isEmpty(phone))
            return false;

        mGroupMemberList.clear();

        for(int i=0; i < users.length; i++) {
            String peer = users[i].getAddress();
            if(phone.equalsIgnoreCase(peer)) {
                mSelfMember = users[i];
            }

            mGroupMemberList.add(users[i]);
        }

        if(null == mSelfMember) {
            mExitGroupText.setVisibility(GONE);
            mAddMemebers.setVisibility(GONE);
            mEditGroup.setVisibility(GONE);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        //only owner can delete group
        mExitGroupText.setText(mSelfMember.isOwner() ? "Delete Group" : "Exit Group");

        if(mUser.groupid > 0) {
            mAddMemebers.setVisibility(mSelfMember.isAdmin() && mUser.isActive() ? VISIBLE : GONE);
            mEditGroup.setVisibility(mUser.getGroupProfile().canModify() ? VISIBLE : GONE);
        }

        mEditGroup.setVisibility(GONE);

        mAdapter.notifyDataSetChanged();
        return true;
    }

    public void updateMember(MesiboGroupProfile.Member m) {
        for(int i=0; i < mGroupMemberList.size(); i++) {
            MesiboGroupProfile.Member em = mGroupMemberList.get(i);
            if(em.getAddress().equalsIgnoreCase(m.getAddress())) {
                mGroupMemberList.remove(em);
                mGroupMemberList.add(i, m);
                break;
            }
        }
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
        parseGroupMembers(members);
    }

    @Override
    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
        if(null == members) return;

        for(MesiboGroupProfile.Member m : members) {
            updateMember(m);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {

    }

    @Override
    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long l) {

    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile userProfile) {
        if(null != mAdapter)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void MesiboProfile_onPublish(MesiboProfile mesiboProfile, boolean b) {

    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    @Override
    public void onResume  () {
        super.onResume();
        MesiboUiDefaults opts = MesiboUI.getUiDefaults();

        if(mUser.isGroup()){
            boolean isActive = mUser.isActive();
            mExitGroupCard.setVisibility(isActive?VISIBLE:GONE);
            mAddMemebers.setVisibility(isActive?VISIBLE:GONE);
            mGroupMembersCard.setVisibility(VISIBLE);
            mStatusPhoneCard.setVisibility(GONE);
            mAddMemebers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MesiboUI.MesiboUserListScreenOptions opts = new MesiboUI.MesiboUserListScreenOptions();
                    opts.mode = MODE_EDITGROUP;
                    opts.groupid = mUser.groupid;
                    MesiboUI.launchUserList(getActivity(), opts);
                    getActivity().finish();
                }
            });

            mEditGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            if(opts.showGroupMembersInProfileView)
                mUser.getGroupProfile().getMembers(25, true, this);
            else
                mGroupMembersCard.setVisibility(GONE);

        } else {
            mExitGroupCard.setVisibility(GONE);
            mGroupMembersCard.setVisibility(GONE);
            mStatusPhoneCard.setVisibility(VISIBLE);

            String userStatus = mUser.getString("status", "");
            if(TextUtils.isEmpty(userStatus)) {
                mll.setVisibility(GONE);
            } else {
                mll.setVisibility(VISIBLE);
                mStatus.setText(userStatus);
            }
            mStatusTime.setText((""));

            String userName = mUser.getName();
            if(opts.showAddressInProfileView) userName = mUser.getAddress();
            if(opts.showAddressInProfileView && opts.showAddressAsPhoneInProfileView) {

                MesiboPhoneContact c = Mesibo.getPhoneContactsManager().getPhoneNumberInfo(mUser.address, true);
                userName = c.formattedPhoneNumber;

                String type = "Phone";
                if (c.type == MesiboPhoneContact.PHONETYPE_MOBILE)
                    type = "Mobile";
                else if (c.type == MesiboPhoneContact.PHONETYPE_FIXED)
                    type = "Fixed";
                else if (c.type == MesiboPhoneContact.PHONETYPE_TOLLFREE)
                    type = "Toll Free";

                mPhoneType.setText(type);
            } else {
                mPhoneType.setVisibility(GONE);
            }

            mMobileNumber.setText(userName);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class GroupMemeberAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private Context mContext=null;
        private ArrayList<MesiboGroupProfile.Member> mDataList=null;

        public GroupMemeberAdapter(Context context,ArrayList<MesiboGroupProfile.Member> list) {
            this.mContext = context;
            mDataList = list;
        }

        public   class GroupMembersCellsViewHolder extends RecyclerView.ViewHolder  {
            public String mBoundString=null;
            public View mView=null;
            public ImageView mContactsProfile=null;
            public TextView mContactsName=null;
            public TextView mAdminTextView=null;
            public EmojiconTextView mContactsStatus=null;

            public GroupMembersCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = (ImageView) view.findViewById(R.id.sp_rv_profile);
                mContactsName = (TextView) view.findViewById(R.id.sp_rv_name);
                mContactsStatus = (EmojiconTextView) view.findViewById(R.id.sp_memeber_status);
                mAdminTextView = (TextView)  view.findViewById(R.id.admin_info);
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_view_group_member_rv_item, parent, false);
            return new GroupMembersCellsViewHolder(view);

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holderr, final int position) {
            final int pos = position;
            final MesiboGroupProfile.Member member = mDataList.get(position);
            final MesiboProfile user = member.getProfile();
            final GroupMembersCellsViewHolder holder = (GroupMembersCellsViewHolder) holderr;



            holder.mContactsName.setText(user.getNameOrAddress());

            Bitmap memberImage = user.getImage().getImage();
            if(null != memberImage)
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(memberImage));
            else
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(mDefaultProfileBmp));

            if (member.isAdmin()) {
                holder.mAdminTextView.setVisibility(VISIBLE);
            }else {
                holder.mAdminTextView.setVisibility(GONE);
            }

            String status =  user.getString("status", "");
            if(TextUtils.isEmpty(status)) {
                status = "";
            }

            holder.mContactsStatus.setText(status);

            // only admin can have menu, also owner can't be deleted

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final MesiboGroupProfile.Member member = mDataList.get(position);
                        final MesiboProfile profile = member.getProfile();

                        if(!mSelfMember.isAdmin()) {
                            if(profile.isSelfProfile()) {
                                return;
                            }

                            MesiboUI.MesiboMessageScreenOptions opts = new MesiboUI.MesiboMessageScreenOptions();
                            opts.profile = profile;

                            MesiboUI.launchMessaging(getActivity(), opts);
                            getActivity().finish();
                            return;
                        }

                        ArrayList<String> items = new ArrayList<String>();

                        if(!member.isAdmin()) {
                            items.add("Make Admin");

                        } else {
                            items.add("Remove Admin");
                        }

                        // don't allow self messaging or self delete member
                        if(!profile.isSelfProfile()) {
                            items.add("Delete member");
                            items.add("Message");
                        }

                        CharSequence[] cs = items.toArray(new CharSequence[items.size()]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        //builder.setTitle("Select The Action");
                        builder.setItems(cs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                //Delete member
                                if (item == 1) {
                                    String[] members = new String[1];
                                    members[0] = mDataList.get(position).getAddress();
                                    mUser.getGroupProfile().removeMembers(members);
                                    mDataList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                } else if(item == 0 ) {
                                    String[] members = new String[1];
                                    members[0] = mDataList.get(position).getAddress();
                                    mUser.getGroupProfile().addMembers(members , MesiboGroupProfile.MEMBERFLAG_ALL, member.isAdmin()?0:MesiboGroupProfile.ADMINFLAG_ALL);
                                } else  if( 2 == item) {
                                    MesiboUI.MesiboMessageScreenOptions opts = new MesiboUI.MesiboMessageScreenOptions();
                                    opts.profile = profile;
                                    MesiboUI.launchMessaging(getActivity(), opts);

                                    getActivity().finish();
                                    return;
                                }
                            }
                        });
                        builder.show();
                    }
                });

        }



        @Override
        public int getItemCount() {
            return mDataList.size();
        }

    }
}
