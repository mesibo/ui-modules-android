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


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.Profile;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.emojiview.EmojiconGridView;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.emojiview.EmojiconsPopup;
import com.mesibo.emojiview.emoji.Emojicon;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.mesibo.api.MesiboGroupProfile.MEMBERFLAG_ALL;

import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_NOMEMEBER_MESSAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_NOMEMEBER_TITLE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.MAX_GROUP_SUBJECT_LENGTH;
import static com.mesibo.messaging.MesiboConfiguration.MIN_GROUP_SUBJECT_LENGTH;
import static com.mesibo.messaging.MesiboConfiguration.MSG_PERMISON_CAMERA_FAIL;
import static com.mesibo.messaging.MesiboConfiguration.TITLE_PERMISON_CAMERA_FAIL;
import static com.mesibo.messaging.Utils.showAlert;

import com.mesibo.mediapicker.MediaPicker;

public class CreateNewGroupFragment extends Fragment implements MediaPicker.ImageEditorListener, Mesibo.GroupListener, MesiboProfile.Listener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    ImageView mGroupPicture;
    EmojiconEditText mGroupSubjectEditor;
    ImageView mEmojiButton;
    TextView mCharCounter;
    LinearLayout mCreateGroupBtn;
    RecyclerView mRecyclerView ;
    RecyclerView.Adapter mAdapter;
    public long mGroupId = 0;
    int  mGroupMode;
    Bitmap mGroupImage = null;
    MesiboProfile mProfile = null;
    boolean mDone = false;

    @Override
    public void onImageEdit(int i, String s, String s1, Bitmap bitmap, int i1) {
        mGroupImage = bitmap;
        setGroupImage(bitmap);
        if(null != mProfile) {
            mProfile.setImage(mGroupImage);
            mProfile.save();
        }
    }

    public CreateNewGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_CAMERAIMAGE);

            } else {
                //TBD, show alert that you can't continue
                Utils.showAlert(getActivity(),TITLE_PERMISON_CAMERA_FAIL, MSG_PERMISON_CAMERA_FAIL);

            }
            return;

        }

        // other 'case' lines to check for other
        // permissions this app might request

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_create_new_group, container, false);
        mGroupSubjectEditor = (EmojiconEditText) v.findViewById(R.id.nugroup_editor);
        Mesibo.addListener(this);
        MesiboImages.init(getActivity());

            if(mGroupId > 0) {
                mProfile = Mesibo.getProfile(mGroupId);
                mGroupSubjectEditor.setText(mProfile.getName());

                MesiboGroupProfile gp = mProfile.getGroupProfile();
                gp.getMembers(256, true, this);
            }

        mCreateGroupBtn = (LinearLayout) v.findViewById(R.id.nugroup_create_btn);

        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserListFragment.mMemberProfiles.size() == 0 && (mGroupMode==0)) {
                    showAlert(getActivity(), CREATE_GROUP_NOMEMEBER_TITLE_STRING, CREATE_GROUP_NOMEMEBER_MESSAGE_STRING);
                    return;
                }
                if(mGroupSubjectEditor.getText().toString().length() < MIN_GROUP_SUBJECT_LENGTH) {
                    showAlert(getActivity(),null,CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING);
                    return;
                }

                mCreateGroupBtn.setEnabled(false);

                if(0 == mGroupId) {
                    MesiboGroupProfile.GroupSettings gs = new MesiboGroupProfile.GroupSettings();
                    gs.name = mGroupSubjectEditor.getText().toString();
                    gs.flags = 0;
                    Mesibo.createGroup(gs, CreateNewGroupFragment.this);
                }
                else {
                    MesiboProfile profile = Mesibo.getProfile(mGroupId);
                    profile.setName(mGroupSubjectEditor.getText().toString());
                    setGroupInfo();
                    Activity a = getActivity();
                    if(null != a)
                        a.finish();
                }
            }
        });

        mGroupPicture = (ImageView) v.findViewById(R.id.nugroup_picture) ;
        setGroupImageFile();

        mGroupPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuBuilder menuBuilder = new MenuBuilder(getActivity());
                MenuInflater inflater = new MenuInflater(getActivity());
                inflater.inflate(R.menu.image_source_menu, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(getActivity(), menuBuilder, v);
                optionsMenu.setForceShowIcon(true);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        if (item.getItemId() == R.id.popup_camera) {
                            if(Utils.aquireUserPermission(getActivity(), Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE))
                                MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_CAMERAIMAGE);
                            return true;
                        } else if (item.getItemId() == R.id.popup_gallery) {
                            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILEIMAGE);
                            return true;
                        } else if(item.getItemId() == R.id.popup_remove) {
                            if(null != mProfile) {
                                mProfile.setImage(null);
                                mProfile.save();
                            }

                            mGroupImage = null;
                            setGroupImage(null);
                        }
                        return false;

                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {

                    }
                });
                optionsMenu.show();
            }
        });


        mRecyclerView = (RecyclerView) v.findViewById(R.id.nugroup_members);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new GroupMemeberAdapter(getActivity(), UserListFragment.mMemberProfiles);
        mRecyclerView.setAdapter(mAdapter);


        mCharCounter = (TextView) v.findViewById(R.id.nugroup_counter);
        mCharCounter.setText(String.valueOf(MAX_GROUP_SUBJECT_LENGTH));

        mGroupSubjectEditor.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_GROUP_SUBJECT_LENGTH)});
        mGroupSubjectEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mCharCounter.setText(String.valueOf(MAX_GROUP_SUBJECT_LENGTH-(mGroupSubjectEditor.getText().length())));

            }
        });


        FrameLayout rootView = (FrameLayout) v.findViewById(R.id.nugroup_root_layout);
        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        mEmojiButton = (ImageView) v.findViewById(R.id.nugroup_smile_btn);
        mEmojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {
                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_keyboard);
                    }
                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        mGroupSubjectEditor.setFocusableInTouchMode(true);
                        mGroupSubjectEditor.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mGroupSubjectEditor, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_keyboard);
                    }
                }
                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_sentiment_satisfied_black_24dp);
                //sendActivity(ACTIVITY_ONLINE);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mGroupSubjectEditor == null || emojicon == null) {
                    return;
                }

                // don't need to send here, it will be anyway sent from onTextChanged
                //sendActivity(ACTIVITY_TYPING);
                int start = mGroupSubjectEditor.getSelectionStart();
                int end = mGroupSubjectEditor.getSelectionEnd();
                if (start < 0) {
                    mGroupSubjectEditor.append(emojicon.getEmoji());
                } else {
                    mGroupSubjectEditor.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mGroupSubjectEditor.dispatchKeyEvent(event);
            }
        });

        return v;
    }

    boolean isExistingMember(Profile p) {
        if(null == UserListFragment.mExistingMembers) return false;
        for(MesiboGroupProfile.Member m : UserListFragment.mExistingMembers) {
            if(p == m.getProfile())
                return true;
        }

        return false;
    }

    void addGroupMembers() {
        ArrayList<String> members = new ArrayList<String>();

        for (int i = 0; i < UserListFragment.mMemberProfiles.size(); i++) {
            MesiboProfile mp = UserListFragment.mMemberProfiles.get(i);
            if(isExistingMember(mp)) continue;
            members.add(mp.getAddress());
        }

        if(members.size() > 0) {
            String [] m = new String[members.size()];
            members.toArray(m);
            MesiboGroupProfile.MemberPermissions permissions = new MesiboGroupProfile.MemberPermissions();
            permissions.flags = MEMBERFLAG_ALL;
            permissions.adminFlags = 0;
            mProfile.getGroupProfile().addMembers(m, permissions);
            return;
        }
    }

    void setGroupInfo() {

        if(null != mGroupImage) {
            mProfile.addListener(this);
            mProfile.setImage(mGroupImage);
            mProfile.save();
        } else {
            addGroupMembers();
            launchMessaging();
        }
    }

    void launchMessaging() {
        MesiboUI.MesiboMessageScreenOptions opts = new MesiboUI.MesiboMessageScreenOptions();
        opts.profile = mProfile;
        MesiboUIManager.launchMessagingActivity(getActivity(), opts);

        Activity a = getActivity();
        if(null != a)
            a.finish();
    }

    @Override
    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {
        mProfile = mesiboProfile;
        mGroupId = mesiboProfile.getGroupId();
        setGroupInfo();
        // now wait for profile to updated
    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile profile) {
        if(mDone) return;
        mDone = true;
        mProfile.removeListener(this);
        addGroupMembers();
        launchMessaging();
    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    @Override
    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // In fragment class callback
        if(RESULT_OK != resultCode)
            return;

        String fileName = MediaPicker.processOnActivityResult(getActivity(), requestCode, resultCode, data);
        if(null == fileName)
            return;

        MesiboUIManager.launchImageEditor((AppCompatActivity)getActivity(), MediaPicker.TYPE_CAMERAIMAGE, 0, null, fileName, false, false, true, true, 600, this);
    }

    public void setGroupImage(Bitmap bmp) {
        if(null == bmp)
            bmp = MesiboImages.getDefaultGroupBitmap();

        if(null == bmp) return;

        mGroupPicture.setImageDrawable(new RoundImageDrawable(bmp));
    }

    public void setGroupImageFile() {
        if(null == mProfile) {
            setGroupImage(null);
            return;
        }

        setGroupImage(mProfile.getThumbnail());
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    public class GroupMemeberAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private int mBackground=0;
        private Context mContext=null;
        private ArrayList<MesiboProfile> mDataList=null;

        private UserListFragment mHost;




        public GroupMemeberAdapter(Context context,ArrayList<MesiboProfile> list) {
            this.mContext = context;
            mDataList = list;

        }


        public   class GroupMembersCellsViewHolder extends RecyclerView.ViewHolder  {
            public  View mView=null;
            public  ImageView mContactsProfile=null;
            public  TextView mContactsName=null;
            public  EmojiconTextView mContactsStatus=null;
            public  ImageView mDeleteContact;
            public GroupMembersCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = (ImageView) view.findViewById(R.id.nu_rv_profile);
                mContactsName = (TextView) view.findViewById(R.id.nu_rv_name);
                mContactsStatus = (EmojiconTextView) view.findViewById(R.id.nu_memeber_status);
                mDeleteContact = (ImageView) view.findViewById(R.id.nu_delete_btn);
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_memeber_rv_item, parent, false);
            return new GroupMembersCellsViewHolder(view);

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holderr, final int position) {
            final int pos = position;
            final MesiboProfile user = mDataList.get(position);
            final GroupMembersCellsViewHolder holder = (GroupMembersCellsViewHolder) holderr;

            if (null == user.other) {
                user.other = new UserData(user);
            }
            final UserData data = (UserData) user.other;
            holder.mContactsName.setText(data.getUserName());
            final Bitmap b = data.getImage();
            String filePath = data.getImagePath();

            if (b != null)
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));
            else {
                if (filePath != null) {
                    holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeFile(filePath)));
                } else
                    holder.mContactsProfile.setImageDrawable(MesiboImages.getDefaultRoundedDrawable());
            }
            if(null != user) {
                if(!TextUtils.isEmpty(user.getStatus()))
                    holder.mContactsStatus.setText(user.getStatus());
                else
                    holder.mContactsStatus.setText("");
            }

            holder.mDeleteContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        public void removeItem(int position) {
            mDataList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

    }

    static final int CAMERA_PERMISSION_CODE = 102;

}
