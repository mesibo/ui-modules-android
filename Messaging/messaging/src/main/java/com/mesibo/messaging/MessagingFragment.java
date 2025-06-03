/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2024 mesibo                                              *
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
import static com.mesibo.api.Mesibo.MSGSTATUS_HEADER;
import static com.mesibo.api.MesiboMessage.TYPE_IMAGE;
import static com.mesibo.api.MesiboMessage.TYPE_LOCATION;
import static com.mesibo.messaging.MesiboConfiguration.COPY_STRING;
import static com.mesibo.messaging.MesiboConfiguration.EMOJI_ICON;
import static com.mesibo.messaging.MesiboConfiguration.KEYBOARD_ICON;
import static com.mesibo.messaging.MesiboConfiguration.MESIBO_INTITIAL_READ_MESSAGEVIEW;
import static com.mesibo.messaging.MesiboConfiguration.MSG_INVALID_GROUP;
import static com.mesibo.messaging.MesiboConfiguration.MSG_PERMISON_FAIL;
import static com.mesibo.messaging.MesiboConfiguration.TITLE_INVALID_GROUP;
import static com.mesibo.messaging.MesiboConfiguration.TITLE_PERMISON_FAIL;
import static com.mesibo.messaging.MesiboConfiguration.YOU_STRING_IN_REPLYVIEW;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_FORWARD;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboPresence;
import com.mesibo.api.MesiboFile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboMessageProperties;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.emojiview.EmojiconGridView.OnEmojiconClickedListener;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.emojiview.EmojiconsPopup;
import com.mesibo.emojiview.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import com.mesibo.emojiview.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import com.mesibo.emojiview.emoji.Emojicon;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.messaging.AllUtils.LetterTileProvider;
import com.mesibo.messaging.AllUtils.TextToEmoji;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import com.mesibo.mediapicker.FacebookAlbumData;


public class MessagingFragment extends BaseFragment implements Mesibo.MessageListener,
        Mesibo.PresenceListener,
        Mesibo.ConnectionListener, Mesibo.SyncListener, OnClickListener,
        MessageViewHolder.ClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, MediaPicker.ImageEditorListener, MesiboProfile.Listener,
        MessageAdapter.MessagingAdapterListener,
        MessagingActivityListener, MesiboMapScreenshot.Listener
{

    private static final String TAG = "MessagingFragment";

    private ArrayList<MessageData> mMessageList = new ArrayList<>();
    private RecyclerView mRecyclerView=null;
    private MessageAdapter mAdapter=null;
    private LinearLayoutManager mLayoutManager=null;
    private LinearLayout showMessage=null;
    private HashMap<Long, MessageData> mMessageMap = new HashMap<Long, MessageData>();
    boolean hidden = true;


    ImageButton ib_gallery=null, ib_contacts=null, ib_location=null;
    ImageButton ib_video=null, ib_audio=null, ib_upload=null, ib_send = null;
    ImageButton ib_cam = null, ib_showattach = null, ib_closeattach = null;
    boolean mPressed = false;


    private String mName = null;

    private long mForwardId = 0;
    private MesiboProfile mUser=null;
    private String mGroupStatus = null;


    private LetterTileProvider mLetterTitler = null;

    //in seconds
    private static int ONLINE_TIME=60000;

    private boolean read_flag = false;

    private MesiboPresence mPresence =null;
    private boolean showLoadMore = true;
    private int mLastReadCount = 0;
    private int mLastMessageCount = 0;
    private int mLastMessageStatus = -1;


    private ImageView mEmojiButton =null;

    private Map<String, String> mEmojiMap;
    private Toolbar toolbar=null;

    private Mesibo.MessageFilter mMessageFilter = Mesibo.getMessageFilter();
    private boolean mMediaHandled = true;

    private UserData mUserData =null;
    private LocationRequest mLocationRequest=null;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;


    private View mEditLayout, mAttachLayout, mBottomLayout;


    private TextView mReplyName;
    private EmojiconTextView mReplyText;
    private ImageView mReplyImage;
    private ImageView mReplyCancel;
    private RelativeLayout mReplyLayout;
    private EmojiconEditText mEmojiEditText;

    private MessageData mReplyMessage = null;
    private Boolean mReplyEnabled = false;

    private MesiboUiDefaults mMesiboUIOptions = null ;

    private RelativeLayout mMessageViewBackgroundImage ;
    private boolean mPlayServiceAvailable = false;

    private int mNonDeliveredCount = 0;

    private boolean mSelectionMode = false;


    public static final int MESIBO_MESSAGECONTEXTACTION_FORWARD = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_REPLY = 2;
    public static final int MESIBO_MESSAGECONTEXTACTION_RESEND = 4;
    public static final int MESIBO_MESSAGECONTEXTACTION_DELETE = 8;
    public static final int MESIBO_MESSAGECONTEXTACTION_COPY = 0x10;
    public static final int MESIBO_MESSAGECONTEXTACTION_FAVORITE = 0x20;

    private MesiboRecycleViewHolder mHeaderView = null;

    //TBD, make it local variable so it will be recycled
    private MesiboReadSession mReadSession = null;
    private FrameLayout mCustomLayout = null;
    private View mParentView = null;

    private Handler mScrollHandler = new Handler();
    private long uptime = Mesibo.getTimestamp();
    private boolean mFirstLayout = true;

    private MesiboMapScreenshot mMapScreenshot = new MesiboMapScreenshot();
    private MesiboUI.MesiboMessageScreen mScreen = new MesiboUI.MesiboMessageScreen();
    public MesiboUI.MesiboMessageScreenOptions mOpts = null;

    public int getLayout() {
        if(MesiboUI.getUiDefaults().mMessagingFragmentLayout != 0)
            return MesiboUI.getUiDefaults().mMessagingFragmentLayout;

        return R.layout.fragment_messaging;
    }

    protected MesiboUI.MesiboMessageScreen getScreen() {
        return mScreen;
    }

    public void onCreateCustomView(View parent, FrameLayout frame, String message) {
        if(parent != null) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = super.onCreateView(inflater, container, savedInstanceState);
        if(null == container)
            return null;

        if(null == getActivity()) {
            finish();
            return null;
        }

        mScreen.parent = getActivity();
        mScreen.options = mOpts;

        if(null == mMessageList || mOpts.profile != mUser) {
            read_flag = false;

            // if null or having existing data - reset it
            if(null == mMessageList || mMessageList.size() > 0)
                mMessageList = new ArrayList<>();
        }

        mUser = mOpts.profile;

        if(null != mUser && mUser.isGroup()) {
            mLetterTitler = new LetterTileProvider(getActivity(), 60, null);
            if(null != mUser) {
                mGroupStatus = mUser.getStatus();
            }
        }

        View view = inflater.inflate(getLayout(), container,false);
        mParentView = view;

        if(!Mesibo.isReady() || null == mUser) {
            finish();
            return view;
        }

        mUser.addListener(this);
        setHasOptionsMenu(true);

        mUser.getImagePath(); // just to get image in advance

        mPresence = mUser.newPresence();


        mRecyclerView = (RecyclerView) view.findViewById(R.id.chat_list_view);
        mRecyclerView.setBackgroundColor(MesiboUI.getUiDefaults().messagingBackgroundColor);
        mScreen.table = mRecyclerView;

        mCustomLayout = view.findViewById(R.id.customLayout);
        onCreateCustomView(view, mCustomLayout, null);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new MessageAdapter(mScreen, this, mMessageList, this, getListener());
        mRecyclerView.setAdapter(mAdapter);

        ib_cam = (ImageButton) view.findViewById(R.id.cameraButton);

        ib_cam.setOnClickListener(this);

        showMessage = (LinearLayout) view.findViewById(R.id.messageLayout);
        if (mAdapter.getItemCount() != 0) {
            if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1)

                showMessgeVisible();
        }

        showMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage.setVisibility(View.INVISIBLE);
                loadFromDB(MESIBO_INTITIAL_READ_MESSAGEVIEW);

            }
        });

        if(!MesiboUI.getUiDefaults().showReplyLayout) {
            View replyLayout = view.findViewById(R.id.reply_layout_id);
            if(null != replyLayout)
                replyLayout.setVisibility(View.GONE);
        }

        mEmojiEditText = (EmojiconEditText) view.findViewById(R.id.chat_edit_text1);
        setupTextWatcher(mEmojiEditText);
        mEmojiEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });


        ib_send = (ImageButton) view.findViewById(R.id.sendmessage);

        ib_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSend();
            }
        });

        ib_audio = (ImageButton) view.findViewById(R.id.audio);
        ib_upload = (ImageButton) view.findViewById(R.id.document_btn);
        ib_gallery = (ImageButton) view.findViewById(R.id.gallery);
        ib_location = (ImageButton) view.findViewById(R.id.location);
        ib_video = (ImageButton) view.findViewById(R.id.video);
        ib_audio.setOnClickListener(this);
        ib_upload.setOnClickListener(this);
        ib_gallery.setOnClickListener(this);
        ib_location.setOnClickListener(this);
        ib_video.setOnClickListener(this);

        ib_showattach = (ImageButton) view.findViewById(R.id.showAttachment);

        ib_showattach.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditLayout.setVisibility(View.GONE);
                mAttachLayout.setVisibility(View.VISIBLE);
            }
        });

        ib_showattach.setVisibility(mMediaHandled?View.VISIBLE:View.GONE);

        showLoadMore = false;

        if (null == mUser.other) {
            mUser.other = new UserData(mUser);
        }

        mUserData = (UserData) mUser.other;

        mMesiboUIOptions = MesiboUI.getUiDefaults();
        mMediaHandled = Mesibo.isFileTransferEnabled();

        if(!mMediaHandled || !mMesiboUIOptions.showRichMessageButtons){
            ib_cam.setClickable(false);
            ib_cam.setVisibility(View.GONE);
            ib_send.setClickable(true);
            ib_send.setVisibility(View.VISIBLE);
            ib_showattach.setClickable(false);
            ib_showattach.setVisibility(View.GONE);
        }

        MesiboImages.init(getActivity());

        mEmojiMap = TextToEmoji.getEmojimap();

        mName = mUserData.getUserName();

        Mesibo.addListener(this);
        mMesiboUIOptions = MesiboUI.getUiDefaults();

        mBottomLayout = view.findViewById(R.id.bottomlayout);

        mMessageViewBackgroundImage = (RelativeLayout) view.findViewById(R.id.chat_layout);
        if(null != mMesiboUIOptions.messagingBackground) {
            Drawable drawable = new BitmapDrawable(getResources(),mMesiboUIOptions.messagingBackground);
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mMessageViewBackgroundImage.setBackground(drawable);
            } else {
                mMessageViewBackgroundImage.setBackgroundDrawable(drawable);
            }
        }

        mReplyLayout = (RelativeLayout) view.findViewById(R.id.reply_layout);

        mReplyCancel = (ImageView) view.findViewById(R.id.reply_cancel);
        mReplyCancel.setVisibility(View.VISIBLE);
        mReplyCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplyLayout.setVisibility(View.GONE);
                mReplyEnabled = false;
            }
        });


        mReplyLayout.setVisibility(View.GONE);

        mReplyImage = (ImageView) view.findViewById(R.id.reply_image);

        mReplyName = (TextView) view.findViewById(R.id.reply_name);
        mReplyText = (EmojiconTextView) view.findViewById(R.id.reply_text);


        final View activityRootView = view.findViewById(R.id.chat_root_layout);


        // forward before read so that we get it back on read()
        if(mOpts.forwardId > 0) {
            MesiboMessage msg = mUser.newMessage();

            msg.setForwarded(mOpts.forwardId);
            msg.setUiContext(getActivity());
            msg.send();
            mOpts.forwardId = 0;
        }


        RelativeLayout rootView = (RelativeLayout) view.findViewById(R.id.chat_layout);

        mEditLayout = view.findViewById(R.id.edit_layout);
        mAttachLayout = view.findViewById(R.id.attachLayout);


        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());


        popup.setSizeForSoftKeyboard();

        mEmojiButton = (ImageView) view.findViewById(R.id.mojiButton);

        mEmojiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!popup.isShowing()) {


                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(mEmojiButton, KEYBOARD_ICON);
                    }
                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        mEmojiEditText.setFocusableInTouchMode(true);
                        mEmojiEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) myActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mEmojiEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(mEmojiButton, KEYBOARD_ICON);
                    }
                }
                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(mEmojiButton, EMOJI_ICON);
                //sendActivity(ACTIVITY_ONLINE);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
                //sendActivity(Mesibo.ACTIVITY_TYPING);
            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mEmojiEditText == null || emojicon == null) {
                    return;
                }

                // don't need to send here, it will be anyway sent from onTextChanged
                //sendActivity(ACTIVITY_TYPING);
                int start = mEmojiEditText.getSelectionStart();
                int end = mEmojiEditText.getSelectionEnd();
                if (start < 0) {
                    mEmojiEditText.append(emojicon.getEmoji());
                } else {
                    mEmojiEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mEmojiEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)


        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());

        String packageName = myActivity().getPackageName();


        // check null - not empty
        if(null == mMesiboUIOptions.mGoogleApiKey) {
            mMesiboUIOptions.mGoogleApiKey = ""; // mark empty
            try {
                ApplicationInfo app = myActivity().getPackageManager().getApplicationInfo(myActivity().getPackageName(), PackageManager.GET_META_DATA);
                Bundle bundle = app.metaData;
                if(null != bundle)
                    mMesiboUIOptions.mGoogleApiKey = bundle.getString("com.google.android.geo.API_KEY");
            } catch (Exception e) {
                mMesiboUIOptions.mGoogleApiKey  = "";
            }
        }

        enableLocationServices();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mScreen.menu = menu;
        if(getListener() == null)
            return ;
        getListener().MesiboUI_onInitScreen(mScreen);
    }

    @Override
    public boolean onMapScreenshot(MesiboMessage msg, Bitmap bmp) {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {

        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        if(null != mUserData && null != mReadSession) {

            mReadSession.enableReadReceipt(false);
        }


        super.onStop();

    }

    @Override
    public void onDestroy() {
        if(null != mReadSession)
            mReadSession.stop();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = myActivity();
        if(null == activity || null == mOpts) {
            return;
        }

        Utils.showServicesSuspendedAlert(activity);

        mNonDeliveredCount = 0;

        blockedUserView();

        Mesibo.setForegroundContext(activity, 1, true);

        setProfilePicture();

        Mesibo.addListener(this);

        // a crash was reported here
        if(null == mUser) {
            finish();
            return;
        }

        if(!TextUtils.isEmpty(mUser.draft)){
            mEmojiEditText.setText(mUser.draft);
        }

        if(null == mUserData)
            return;

        if(null == mReadSession) {
            mReadSession = mUser.createReadSession(this);
            mReadSession.enableReadReceipt(true);
            mReadSession.enableMissedCalls(MesiboUI.getUiDefaults().showMissedCalls);
            mReadSession.start();
        } else {
            mReadSession.enableReadReceipt(true);
        }

        addHeaderMessage();

        if(!read_flag) {

            read_flag = true;
            loadFromDB(MESIBO_INTITIAL_READ_MESSAGEVIEW);

            if(false) {
                int sizeNext = mMessageList.size();
                mAdapter.notifyItemRangeInserted(0, sizeNext);

                // add header if size 0
                if (0 == mLastReadCount) {
                    addHeaderMessage();
                }
            }
        } else {
            mAdapter.notifyDataSetChanged();
        }

        if(!mUser.isGroup())
            mPresence.sendJoined();
        else
            mGroupStatus = mUser.getStatus();


        if(!TextUtils.isEmpty(mGroupStatus))
            updateUserStatus(mGroupStatus, 0);
        else
            updateUserActivity(null, MesiboPresence.PRESENCE_ONLINE);

    }

    @Override
    public void onPause() {
        super.onPause();

        if(null != mEmojiEditText)
            mUser.draft = mEmojiEditText.getText().toString();

        if(null != mUser && null != mPresence && !mUser.isGroup())  mPresence.sendLeft();

    }

    private void finish() {
        FragmentActivity a = getActivity();
        if(null != a)
            a.finish();
    }

    private void blockedUserView() {
        if(mOpts.readOnly || (mUser.isGroup() && !mUser.isActive() && null != mBottomLayout) || mUser.isMessageBlocked()) {
            mBottomLayout.setVisibility(View.GONE);
        } else {
            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onMediaButtonClicked(int buttonId) {

        if (buttonId == R.id.cameraButton) {
            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_CAMERAIMAGE, Mesibo.getTempFilesPath());
        } else if (buttonId == R.id.audio) {
            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_AUDIO);
        } else if (buttonId == R.id.document_btn) {
            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_FILE);
        } else if (buttonId == R.id.location) {
            try {
                displayPlacePicker();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

        }/*else if (v.getId() == R.id.contacts) {

            //imagePicker.initilizeAlbumList();

        } */ else if (buttonId == R.id.video) {


            CharSequence Options[] = new CharSequence[] {MesiboUI.getUiDefaults().videoFromRecorderTitle, MesiboUI.getUiDefaults().videoFromGalleryTitle};

            AlertDialog.Builder builder = new AlertDialog.Builder(myActivity());
            builder.setTitle(MesiboUI.getUiDefaults().videoSelectTitle);
            builder.setItems(Options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    if(which==0)
                    {
                        MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_CAMERAVIDEO, Mesibo.getTempFilesPath());;
                    }else if (which==1) {
                        MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_FILEVIDEO);;
                    }
                }
            });
            builder.show();

        } else if (buttonId == R.id.gallery) {

            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_FILEIMAGE);

        }

    }

    private int mMediaButtonClicked = -1;
    @Override
    public void onClick(View v) {
        showAttachments(false);
        mPressed = false;
        hidden = true;

        mMediaButtonClicked = v.getId();

        List<String> permissions = new ArrayList<>();
        if (mMediaButtonClicked != R.id.location) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (mMediaButtonClicked == R.id.cameraButton || mMediaButtonClicked == R.id.video) {
            permissions.add(Manifest.permission.CAMERA);
        } if (mMediaButtonClicked == R.id.location) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(Utils.aquireUserPermissions(getActivity(), permissions, MESSAGING_PERMISSION_CODE)) {
            onMediaButtonClicked(mMediaButtonClicked);
            mMediaButtonClicked = -1;
        }
    }

    @Override
    public boolean Mesibo_onBackPressed() {

        if (mAttachLayout.getVisibility() == View.VISIBLE) {
            showAttachments(false);
            mPressed = false;
            hidden = true;
            return true;
        } else {
            return false;
        }

    }

    public void updateUserPicture(MesiboProfile profile, Bitmap thumbnail, String picturePath) {
        if(null == mScreen.profileImage) return;
        mScreen.profileImage.setImageDrawable(new RoundImageDrawable(thumbnail));
        String name = mUser.getName();
        if(TextUtils.isEmpty(name))
            name = mUser.address;
        if(name.length() > 16)
            name = name.substring(0, 14) + "...";
        if(null != mScreen.title)
            mScreen.title.setText(name);
    }

    private void setProfilePicture() {
        LetterTileProvider tileProvider = new LetterTileProvider(myActivity(), 60, mMesiboUIOptions.mLetterTitleColors);

        Bitmap thumbnail = mUserData.getThumbnail(tileProvider);

        updateUserPicture(mUser, thumbnail, mUserData.getImagePath());
    }
    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }



    private GoogleApiClient mGoogleApiClient = null;
    private boolean mPlaceInitialized = false;
    private static int  PLACE_PICKER_REQUEST=199;

    private void displayPlacePicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {

        if(getListener().MesiboUI_onShowLocation(getActivity(), mUser)) {
            return;
        }

        enableLocationServices();

        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() || !mPlaceInitialized)
            return;

        try {

            Intent mapIntent = new Intent(myActivity(), MesiboMapActivity.class);
            myActivity().startActivityForResult(mapIntent, PLACE_PICKER_REQUEST);

            if(true) return;

            List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ICON_URL, Place.Field.LAT_LNG, Place.Field.VIEWPORT);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                    .build(myActivity());

            myActivity().startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Toast.makeText(myActivity(), "Google Play Services exception.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUserOnlineStatus(MesiboProfile profile, String status) {
        if(null == mScreen.subtitle) return;

        if(null == status) {
            mScreen.subtitle.setVisibility(View.GONE);
            return;
        }

        mScreen.subtitle.setVisibility(View.VISIBLE);
        mScreen.subtitle.setText(status);
        return;
    }

    private int updateUserStatus(String status, long duration) {

        if(null == status) {

            if(TextUtils.isEmpty(mGroupStatus)) {
                updateUserOnlineStatus(mUser, null);
            }
            else {
                updateUserOnlineStatus(mUser, mGroupStatus);
            }

            return 0;
        }

        updateUserOnlineStatus(mUser, status);

        return 0;
    }

    private int updateUserActivity(MesiboMessageProperties params, int activity) {

        int connectionStatus = Mesibo.getConnectionStatus();
        if(Mesibo.STATUS_CONNECTING == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.connectingIndicationTitle, 0);
        }

        if(Mesibo.STATUS_SUSPENDED == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.suspendIndicationTitle, 0);
        }

        if(Mesibo.STATUS_NONETWORK == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.noNetworkIndicationTitle, 0);
        }

        if(Mesibo.STATUS_CONNECTFAILURE == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.offlineIndicationTitle, 0);
        }

        if(Mesibo.STATUS_ONLINE != connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.offlineIndicationTitle, 0);
        }

        if(MesiboPresence.PRESENCE_NONE == activity) return 0;

        MesiboProfile profile = mUser;
        long groupid = 0;
        if(null != params) {
            groupid = params.groupid;
            if(null != params.profile)
                profile = params.profile;
        }

        String status = null;
        if (profile.isTyping(groupid)) {
            status = "";
            if(groupid > 0) {
                status = profile.getName() + " is ";
            }
            status += mMesiboUIOptions.typingIndicationTitle;
        } else if(profile.isChatting() && 0 == groupid) {
            status = mMesiboUIOptions.joinedIndicationTitle;
        } else if(profile.isOnline() && 0 == groupid) {
            status = mMesiboUIOptions.userOnlineIndicationTitle;
        }

        return updateUserStatus(status, 0);
    }

    public boolean isMoreMessage() {
        return showLoadMore;
    }

    private boolean isForMe(MesiboMessageProperties msg) {
        if(msg.isRealtimeMessage())
            updateUserActivity(msg, MesiboPresence.PRESENCE_NONE); //TBD, this will fire twice in case of activity

        return msg.isDestinedFor(mUser);
    }

    @Override
    public void Mesibo_onSync(int count) {
        final int c = count;
        if(count > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    loadFromDB(c);
                }
            });

        }
    }

    private void loadFromDB(int count) {
        mLastMessageCount = mMessageList.size();

        showLoadMore = false;
        mLastReadCount = mReadSession.read(count);
        if(mLastReadCount == count) {
            showLoadMore = true;
        } else {

            if(0 == mLastReadCount && mMessageList.size() == 0) {
                updateUiIfLastMessage(null);
            }

            mReadSession.sync(count, this);

        }
    }


    private boolean deleteTimestamp(MesiboMessage m) {
        if(mMessageList.size() <= mMessageOffset) {
            return true; // return true as it can be considered deleted (not exists)
        }

        // used when we deleted messages and the last message is date
        // TBD, need to do for top messages too
        if(null == m) {
            int n = mMessageList.size()-1;
            MesiboMessage p = mMessageList.get(n).getMesiboMessage();
            if(null != p && p.isDate()) {
                mMessageList.remove(n);
                // mAdapter.notifyItemRemoved(n);
                return true;
            }
            return false;
        }

        MesiboMessage p = mMessageList.get(mMessageOffset).getMesiboMessage();
        if(p.isDate() && p.date.daysElapsed == m.date.daysElapsed) {
            mMessageList.remove(mMessageOffset);
            return true;
        }

        return false;
    }

    private void addTimestampToList(MesiboMessage m) {
        MesiboMessage d = new MesiboMessage(m.date);
        MessageData data = new MessageData(myActivity(), d);
        if(m.isRealtimeMessage()) mMessageList.add(data);
        else mMessageList.add(mMessageOffset, data);
    }
    private boolean addTimestamp(MesiboMessage m, boolean forced) {
        if(null == m) return false;

        if(forced) {
            addTimestampToList(m);
            return true;
        }

        if(mMessageList.size() > mMessageOffset && !m.isRealtimeMessage()) {
            MesiboMessage p = mMessageList.get(mMessageOffset).getMesiboMessage();

            addTimestampToList(p);
            return true;
        }

        MesiboMessage p = null;
        if(mMessageList.size() > mMessageOffset) {
            int n = mMessageList.size() - 1;
            p = mMessageList.get(n).getMesiboMessage();
        }

        if(null != p && p.date.daysElapsed == m.date.daysElapsed)
            return false;

        addTimestampToList(m);
        return true;
    }

    private boolean addTimestamp(MesiboMessage m) {
        return addTimestamp(m, false);
    }


    private void addMessage(MesiboMessage m) {
        MessageData data = new MessageData(myActivity(), m);

        mMessageMap.put(Long.valueOf(m.mid), data);

        // this could be database or pending
        if (!m.isRealtimeMessage()) {

            //This will add previous TS if this one is older
            boolean deleted = deleteTimestamp(m);
            mMessageList.add(mMessageOffset, data);
            addTimestamp(m);

        } else {
            boolean dateAdded = addTimestamp(m);
            mMessageList.add(data);

            mAdapter.addRow();

            if(dateAdded) {
                mAdapter.notifyItemRangeInserted(mMessageList.size()-2, 2);
            } else {
                mAdapter.notifyItemInserted(mMessageList.size() - 1);
            }


            if(m.isEndToEndEncryptionStatus())
                return;

            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }

    }

    MessageData findMessage(long id) {
        return mMessageMap.get(Long.valueOf(id));
    }

    @Override
    public void Mesibo_onPresence(MesiboPresence params) {
        if(!isForMe(params)) return;

        updateUserActivity(params, (int) params.presence);
    }

    @Override
    public void Mesibo_onPresenceRequest(MesiboPresence mesiboPresence) {

    }

    private int mMessageOffset = 0;
    public void addHeaderMessage() {
        if(TextUtils.isEmpty(MesiboUI.getUiDefaults().headerTitle))
            return;

        if(mMessageOffset > 0) return;
        mMessageOffset = 1;
        MesiboMessage msg = new MesiboMessage();
        msg.setStatus(MSGSTATUS_HEADER);
        msg.message = MesiboUI.getUiDefaults().headerTitle;
        mMessageList.add(0, new MessageData(myActivity(), msg));
    }

    public void updateUiIfLastMessage(MesiboMessage msg) {
        if(null != msg) {
            if (msg.isRealtimeMessage()) return;
            if (!msg.isLastMessage()) return;
        }

        mAdapter.notifyItemRangeInserted(mLastMessageCount, mMessageList.size()-mLastMessageCount);

        mLoading = false;

        return;

    }

    @Override
    public void Mesibo_onMessage(MesiboMessage msg) {
        //Log.d(TAG, "Mesibo_onMessage");
        //this may come in real-time
        //TBD. maybe custom UI needs it so we should not restrict
        if(msg.isIncomingCall() || msg.isOutgoingCall()) {
            updateUiIfLastMessage(msg); //needed ???
            return;
        }

        if(!isForMe(msg)) return;

        if(msg.isEndToEndEncryptionStatus()) {
            if(!Mesibo.e2ee().isEnabled())
                return;

            if(TextUtils.isEmpty(MesiboUI.getUiDefaults().e2eeActive))
                return;
        }


        MessageData m = findMessage(msg.mid);
        if(null != m) {
            return;
        }
        addMessage(msg);

        updateUiIfLastMessage(msg);

        MesiboFile f = msg.getFile();
        if((null == f || f.type == TYPE_LOCATION) && msg.hasLocation()) {
            if(null == msg.getThumbnail() && !msg.isFileTransferFailed()) {
                msg.replaceThumbnailWithLocation(16, mMesiboUIOptions.mGoogleApiKey);
                if(false && !TextUtils.isEmpty(mMesiboUIOptions.mGoogleApiKey)) {
                    String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?zoom=16&size=800x600&maptype=roadmap&markers=color:red%7Clabel:C%7C" + msg.latitude + "," + msg.longitude + "&key=" + mMesiboUIOptions.mGoogleApiKey;
                }

            }
        }
        return;
    }

    @Override
    public void Mesibo_onMessageUpdate(MesiboMessage msg) {
        if(!isForMe(msg)) return;
        MessageData m = findMessage(msg.mid);

        // Mesibo.log("Mesibo_onMessageUpdate: fragment");
        // TBD fixed
        if(null == m) return;
        if(msg.isFileTransferInProgress()) {
            MessageViewHolder vh = (MessageViewHolder) m.getViewHolder();
            if(null != vh) {
                vh.updateThumbnail(m);
                return;
            }
        }
        int position = m.getPosition();
        if(position >= 0)
            mAdapter.updateStatus(position);

    }

    @Override
    public void Mesibo_onMessageStatus(MesiboMessage params) {
        if(null == params || 0 == params.mid /*|| params.isMessageStatusInProgress() */) {
            return;
        }

        if(!isForMe(params)) return;

        if(params.isDeleted()) {
            MessageData m = findMessage(params.mid);
            if(null != m) {
                m.setDeleted(true);
                int position = m.getPosition();
                if(position >= 0)
                    mAdapter.updateStatus(position);
            }
            return;
        }

        mLastMessageStatus = params.getStatus();

        mNonDeliveredCount++;
        if(params.isReadByPeer() || params.isDelivered()) {
            updateUserActivity(null, MesiboPresence.PRESENCE_NONE);
            mNonDeliveredCount = 0;
        }

        if(0 == mMessageList.size())
            return;

        if (params.isFailed()) {

        }

        if(mUser.groupid > 0 && Mesibo.MSGSTATUS_INVALIDDEST == params.getStatus()) {
            Utils.showAlert(getActivity(), TITLE_INVALID_GROUP, MSG_INVALID_GROUP);
        }

        if(params.isReadByPeer()) {

            int i = mMessageList.size();
            boolean found = false;
            //TBD, we need to lock else it may crash if message was deleted etc
            while(i > 0) {
                MessageData cm = mMessageList.get(i-1);

                // start marking as read from this message onward
                if(cm.getMesiboMessage() == params || cm.getMesiboMessage().mid == params.mid)
                    found = true;

                if(!found) {
                    i--;
                    continue;
                }

                if(null == cm) return;

                if(cm.getStatus() == Mesibo.MSGSTATUS_READ) {

                    if(cm.getMesiboMessage() != params)
                        break; 
                }

                if (cm.getStatus() == Mesibo.MSGSTATUS_DELIVERED || cm.getStatus() == Mesibo.MSGSTATUS_SENT) {
                    cm.setStaus(params.getStatus());
                }
                i--;
            }

            mAdapter.notifyDataSetChanged();
            return;
        }

        MessageData m = findMessage(params.mid);
        if(null != m) {
            m.setStaus(params.getStatus());
            int position = m.getPosition();
            if(position >= 0) {
                mAdapter.updateStatus(position);
            }
        }

        if(true)
            return;

        int i = mMessageList.size();
        while(i > 0) {
            MessageData cm = mMessageList.get(i-1);
            if(cm.getMid() != 0 && cm.getMid() == params.mid) {
                cm.setStaus(params.getStatus());
                mAdapter.updateStatus(i-1);
                break; //all previous are already marked read, break
            }

            i--;
        }

    }



    @Override
    public void Mesibo_onConnectionStatus(int status) {
        if(status == Mesibo.STATUS_SHUTDOWN) {
            finish();
            return;
        }

        if(status == Mesibo.STATUS_SUSPENDED) {
            Utils.showServicesSuspendedAlert(getActivity());
        }

        if(Mesibo.STATUS_ONLINE == status) {
            updateUserActivity(null, MesiboPresence.PRESENCE_ONLINE);
        }

        updateUserActivity(null, MesiboPresence.PRESENCE_NONE);
    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile userProfile) {

        if(Mesibo.isUiThread()) {
            if(userProfile.isDeleted()) {
                finish();
                return;
            }

            setProfilePicture();
            blockedUserView();
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(userProfile.isDeleted()) {
                    finish();
                    return;
                }

                setProfilePicture();
                blockedUserView();
            }
        });
    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int status) {
    }

    public void showMessgeVisible() {
        showMessage.setVisibility(View.VISIBLE);
    }

    private boolean mLoading = false;
    private long mLoadTs = 0;
    public synchronized  void loadMoreMessages() {
        if(mLoading) return;
        long ts = Mesibo.getTimestamp();
        if((ts - mLoadTs) < 2000) return;

        mLoadTs = ts;
        mLoading = true;
        loadFromDB(MESIBO_INTITIAL_READ_MESSAGEVIEW);
    }

    public void showMessageInvisible() {
        if (showMessage.getVisibility() == View.VISIBLE) {
            showMessage.setVisibility(View.GONE);
        }
    }

    private void showAttachments(boolean show) {
        boolean isVisible = (mAttachLayout.getVisibility() == View.VISIBLE);

        if(show == isVisible ) {
            return;
        }

        mEditLayout.setVisibility(show?View.GONE:View.VISIBLE);
        mAttachLayout.setVisibility(show?View.VISIBLE:View.GONE);


    }

    @Override
    public void onImageEdit(int type, String caption, String filePath, Bitmap bmp, int result) {
        if(0 != result)
            return;

        sendFile(type, caption, filePath, bmp, result);
    }

    private void sendFile(int type, String caption, String filePath, Bitmap bmp, int result) {
        if(0 != result)
            return;

        int temp = mMessageList.size();
        MesiboMessage m = mUser.newMessage();
        if(null != bmp && (MediaPicker.TYPE_FILEIMAGE == type || MediaPicker.TYPE_CAMERAIMAGE == type))
            m.setContent(bmp);
        else m.setContent(filePath);
        m.message = caption;
        if(mReplyEnabled && null != mReplyMessage)
            m.refid = mReplyMessage.getMid();

        mReplyEnabled = false;
        mReplyLayout.setVisibility(View.GONE);
        m.setUiContext(getActivity());
        m.send();
    }


    @Override
    public void onItemClicked(int position, MessageViewHolder holder) {
        if (mSelectionMode) {
            toggleSelection(position);
            return;
        }

        MessageData d = mMessageList.get(position);
        MesiboMessage m = d.getMesiboMessage();

        if(getListener() != null) {
            MesiboUI.MesiboMessageRow row = new MesiboUI.MesiboMessageRow();
            row.reset();
            row.screen = mScreen;
            row.message = m;
            row.viewGroup = null;
            holder.setRow(row);
            if(getListener().MesiboUI_onClickedRow(mScreen, row))
                return;
        }

        if(!m.isRichMessage())
            return;

        if(m.isFileTransferRequired() || m.isFileTransferInProgress()) {
            m.toggleFileTransfer(1);
            return;
        }

        MesiboFile f = m.getFile();

        if(null == f || f.type == TYPE_LOCATION) {
            if(m.hasLocation()) Mesibo.launchLocation(myActivity(), m);
            return;
        }

        if(TextUtils.isEmpty(f.path) || m.openExternally) {
            Mesibo.launchUrl(myActivity(), f.url);
            return;
        }

        if(TYPE_IMAGE == f.type) {
            MesiboUIManager.launchPictureActivity(myActivity(), mUser.getName(), f.path);
            return;
        }

        Mesibo.launchFile(myActivity(), f.path);
        return;
    }

    @Override
    public boolean onItemLongClicked(int position, MessageViewHolder holder) {
        if (!mSelectionMode) {
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(mActionModeCallback);
            mSelectionMode = true;
        }

        toggleSelection(position);
        return true;
    }

    /**
     * Toggle the selection state of an item.
     * <p/>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {

        int gPosition = mAdapter.globalPosition(position);
        mAdapter.toggleSelection(gPosition);
        mAdapter.notifyItemChanged(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            hideInContextUserInterface();
        } else {
            updateContextUserInterfaceCount(count);
            //getListener().Mesibo_onContextUserInterfaceCount(count);
        }
    }

    public void sendTextMessage(String newText) {
        if(0 == newText.length())
            return;

        MesiboMessage msg = mUser.newMessage();
        msg.message = newText;
        if(mReplyEnabled && null != mReplyMessage)
            msg.refid = mReplyMessage.getMid();
        mReplyEnabled = false;
        mReplyLayout.setVisibility(View.GONE);
        msg.setUiContext(getActivity());
        msg.send();

        mEmojiEditText.getText().clear();
        //et.clearFocus();

    }

    private void onSend() {

        String newStr = mEmojiEditText.getText().toString();
        String newText = newStr.trim();

        mEmojiEditText.getText().clear();
        mEmojiEditText.setText("");

        //=newText = new String(Character.toChars(0x1F60A));
        if (MesiboUI.getUiDefaults().mConvertSmilyToEmoji) {
            Iterator<Map.Entry<String, String>> iitr = mEmojiMap.entrySet().iterator();
            while (iitr.hasNext()) {
                Map.Entry<String, String> entry = iitr.next();
                newText = newText.replace(entry.getKey(), entry.getValue());
            }
        }

        sendTextMessage(newText);
        //MesiboHelper.sendMessage(0, SEND_TO_USER, "Got your message - my dummy replay");
    }

    private void setupTextWatcher(EmojiconEditText et) {
        //same as emojiconEditText
        et.setRawInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String newStr = mEmojiEditText.getText().toString().trim();

                if(newStr.length() > 0)
                    mPresence.sendTyping();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mMediaHandled) {
                    if (s.length() == 0) {
                        //cam.setVisibility(View.VISIBLE);
                        if (ib_cam.getVisibility() == View.GONE) {
                            //slideToReveal(cam);
                            TranslateAnimation animate = new TranslateAnimation(15, 0, 0, 0);
                            animate.setDuration(100);
                            ib_cam.startAnimation(animate);
                            ib_send.startAnimation(animate);
                            if(mMediaHandled) {
                                ib_cam.setVisibility(View.VISIBLE);
                                ib_send.setVisibility(View.GONE);
                            }

                        }

                    } else
                        //cam.setVisibility(View.GONE);
                        if (ib_cam.getVisibility() == View.VISIBLE) {
                            //slideToConceal(cam);/*
                            TranslateAnimation animate = new TranslateAnimation(0, 15, 0, 0);
                            animate.setDuration(100);
                            ib_cam.startAnimation(animate);
                            ib_send.startAnimation(animate);
                            ib_cam.setVisibility(View.GONE);
                            ib_send.setVisibility(View.VISIBLE);
                        }

                }}
        });
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }


    private boolean mGoogleApiClientChecked = false;
    private void enableLocationServices() {
        if(null != mGoogleApiClient && mGoogleApiClientChecked && mPlaceInitialized)
            return;

        Context context = myActivity();
        if(null == context) return;

        if(!mGoogleApiClientChecked) {
            mGoogleApiClientChecked = true;
            // First we need to check availability of play services
            if (Utils.checkPlayServices(myActivity(), PLAY_SERVICES_RESOLUTION_REQUEST)) {
                buildGoogleApiClient();
                createLocationRequest();
                mPlayServiceAvailable = true;
            }
        }


        if(TextUtils.isEmpty(mMesiboUIOptions.mGoogleApiKey)) {
            Log.e("mesibo", "Google map support API key is not defined in AndroidManifest.xml");
            return;
        }

        mPlaceInitialized = true;
        if (!Places.isInitialized()) {
            Places.initialize(myActivity(), mMesiboUIOptions.mGoogleApiKey, Locale.US);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    static final int MESSAGING_PERMISSION_CODE = 101;

    @Override
    public void Mesibo_onRequestPermissionsResult(int requestCode,
                                                  String permissions[], int[] grantResults) {

        if(MESSAGING_PERMISSION_CODE != requestCode)
            return;

        for(int i=0; i < grantResults.length; i++) {

            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                Utils.showAlert(getActivity(), TITLE_PERMISON_FAIL, MSG_PERMISON_FAIL);
                return;
            }
        }

        onMediaButtonClicked(mMediaButtonClicked);
        mMediaButtonClicked = -1;
        return;

    }

    @Override
    public boolean Mesibo_onActivityResult(int requestCode, int resultCode, Intent data) {

        if( !(requestCode == PLACE_PICKER_REQUEST ||
                (requestCode >= MediaPicker.getBaseTypeValue() || requestCode <= MediaPicker.getMaxTypeValue()))) {
            return false;

        }

        if(Activity.RESULT_OK != resultCode)
            return true;

        if( requestCode == PLACE_PICKER_REQUEST) {
            MesiboMapActivity.LocationInfo li = (MesiboMapActivity.LocationInfo) Mesibo.getIntentObject(data, MesiboMapActivity.LocationInfo.class);
            if(null == li) return true;

            MesiboMessage msg = mUser.newMessage();
            msg.title = li.name;
            msg.message = li.address;
            msg.latitude = li.lat;
            msg.longitude = li.lon;
            msg.setContentType(TYPE_LOCATION); // not required, for old versions
            msg.setThumbnail(li.image);
            msg.setUiContext(getActivity());

            if(mReplyEnabled && null != mReplyMessage)
                msg.refid = mReplyMessage.getMid();

            mReplyEnabled = false;
            mReplyLayout.setVisibility(View.GONE);
            msg.send();
            return true;
        }

        String filePath = MediaPicker.processOnActivityResult(myActivity(), requestCode, resultCode, data);

        if(null == filePath) {
            return true;
        }

        if(MediaPicker.TYPE_FILE  == requestCode) {
            String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());

            if (ext.length() > 3)
                ext = ext.substring(0, 3);

            if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpe") || ext.equalsIgnoreCase("png")
                    || ext.equalsIgnoreCase("gif")) {
                requestCode = MediaPicker.TYPE_FILEIMAGE;
            }

            else if (ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("avi") || ext.equalsIgnoreCase("3g")
                    || ext.equalsIgnoreCase("3gp")) {
                requestCode = MediaPicker.TYPE_FILEVIDEO;
            }
        }


        int drawableid = -1;
        if(MediaPicker.TYPE_AUDIO == requestCode) {
            drawableid = R.drawable.file_audio;
        } else if(MediaPicker.TYPE_FILE == requestCode) {
            drawableid = MesiboImages.getFileDrawable(filePath);
        }

        MesiboUIManager.launchImageEditor(myActivity(), requestCode, drawableid, null, filePath, true, true, false, false, 1280, this);
        return true;
    }

    public int onGetEnabledActionItems() {

        int enabled = MESIBO_MESSAGECONTEXTACTION_FORWARD | MESIBO_MESSAGECONTEXTACTION_DELETE | MESIBO_MESSAGECONTEXTACTION_FAVORITE;

        List<Integer> selection = mAdapter.getSelectedItems();

        boolean hideResend = true;

        for (Integer i : selection) {
            MessageData cm = mMessageList.get(i);
            if (cm.getMesiboMessage().isFailed()) {
                hideResend = false;
            }
        }

        if (!hideResend) {
            enabled |= MESIBO_MESSAGECONTEXTACTION_RESEND;
        }

        if(selection.size() == 1) {
            enabled |= MESIBO_MESSAGECONTEXTACTION_COPY | MESIBO_MESSAGECONTEXTACTION_REPLY;
        }

        return enabled;

    }

    private boolean onContextMenuClicked(int item) {
        if (MESIBO_MESSAGECONTEXTACTION_DELETE == item) {
            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();
            Collections.reverse(selection);
            int maxDeleteInterval = Mesibo.getMessageRetractionInterval();

            boolean deleteRemote = true;

            for (Integer i : selection) {
                MessageData m = mMessageList.get(i);
                if(m.getStatus() > Mesibo.MSGSTATUS_READ || m.isDeleted() || ((Mesibo.getTimestamp() - m.getMesiboMessage().ts)/1000) > maxDeleteInterval) {
                    deleteRemote = false;
                    break;
                }
            }

            if(deleteRemote)
                promptAndDeleteMessage(selection);
            else
                deleteSelectedMessages(selection, false);

            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_COPY == item) {

            String st = mAdapter.copyData();
            if(TextUtils.isEmpty(st))
                return true;

            st = st.trim();

            ClipboardManager clipboard = (ClipboardManager) myActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(COPY_STRING, st);
            clipboard.setPrimaryClip(clip);
            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_RESEND == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();

            for (Integer i : selection) {
                int index = i;
                MessageData cm = mMessageList.get(i);
                if(cm.getMesiboMessage().isFailed()) {   //
                    Mesibo.resend(cm.getMesiboMessage().mid);
                }
            }

            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_FORWARD == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();
            int j = 0;
            long[] mids = new long[selection.size()];
            for (Integer i : selection) {
                int index = i;
                MessageData cm = mMessageList.get(i);
                MesiboMessage m = cm.getMesiboMessage();
                // don't forward if upload is in progress
                if(!m.isRichMessage() || !m.isFileTransferRequired())
                    mids[j++] = cm.getMid();
            }

            MesiboUIManager.launchContactActivity(myActivity(), MODE_FORWARD, mids);
            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_FAVORITE == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();

            Boolean setFlag = false;

            for (Integer i : selection) {
                MessageData cm = mMessageList.get(i);
                setFlag = cm.getFavourite();
                if(setFlag)
                    break;
            }

            setFlag = !setFlag;

            for (Integer i : selection) {
                MessageData cm = mMessageList.get(i);
                if(cm.getFavourite() != setFlag) {
                    cm.setFavourite(setFlag);
                    mAdapter.updateStatus(i);
                }
            }

            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_REPLY == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();
            mReplyEnabled = true;

            for (Integer i : selection) {
                int index = i;
                mReplyMessage = mMessageList.get(i);
                String username = YOU_STRING_IN_REPLYVIEW;
                if(mReplyMessage.getMesiboMessage().isIncoming())
                    username = mReplyMessage.getUsername();

                mReplyName.setTextColor(mReplyMessage.getNameColor());
                mReplyName.setText(username);
                mReplyText.setText(mReplyMessage.getDisplayMessage());
                mReplyImage.setVisibility(View.GONE);
                Bitmap image = mReplyMessage.getImage();

                if(image != null) {
                    mReplyImage.setVisibility(View.VISIBLE);
                    mReplyImage.setImageBitmap(image);
                }

                mReplyLayout.setVisibility(View.VISIBLE);
                break;
            }

            return true;
        }

        return false;
    }

    public void deleteSelectedMessages(List<Integer> selection, boolean remote) {
        List<Long> mids = new ArrayList<Long>();
        List<Integer> deleted = new ArrayList<Integer>();
        Integer maxDeleted = -1, minDeleted=100000000;

        for (Integer i : selection) {
            int index = i;

            MessageData md = mMessageList.get(i);
            if(md != null)
                md.setDeleted(true);

            mids.add(mMessageList.get(i).getMid());

            if(!remote) {
                if(i > 0) {
                    MessageData p = mMessageList.get(i-1); // previous message
                    MessageData n = null;
                    if(i < mMessageList.size()-1)
                        n = mMessageList.get(i+1); //next message

                    if(p.getMesiboMessage().isDate() && (n == null || n.getMesiboMessage().isDate())) {
                        p.setVisible(false);
                        deleted.add(i-1);
                        if(minDeleted > (i-1))
                            minDeleted = (i-1);
                    }
                }
                deleted.add(i);
                if(minDeleted > i)
                    minDeleted = i;
                if(maxDeleted < i)
                    maxDeleted = i;
                md.setVisible(false);
            }
            else {
                mAdapter.notifyItemChanged(i);
            }
        }
        //   Log.d(TAG, "menu_remove");
        long[] m = new long[mids.size()];
        for(int i=0; i < mids.size(); i++) {
            m[i] = mids.get(i);
        }

        if(!remote) Mesibo.deleteMessages(m);
        else Mesibo.wipeAndRecallMessages(m);

        if(deleted.size() == 1) {
            int n = deleted.get(0);
            mMessageList.remove(n);
            //mAdapter.notifyItemRemoved(deleted.get(0));
            mAdapter.notifyDataSetChanged();
        } else if(deleted.size() > 1) {
            // remove from back so that array list positions are not disturbed
            for(int i = maxDeleted; i >= minDeleted; i--) {
                if(!mMessageList.get(i).isVisible())
                    mMessageList.remove(i);
            }
            // date sandwich condition may fail in multiple message condition
            deleteTimestamp(null);
            mAdapter.notifyDataSetChanged();
        }


    }

    public void promptAndDeleteMessage(final List<Integer> selection) {
        MesiboUiDefaults opts = MesiboUI.getUiDefaults();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(opts.deleteMessagesTitle);

        String[] items = {opts.deleteForEveryoneTitle, opts.deleteForMeTitle, opts.cancelTitle};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(2 == which) {

                    return;
                }

                boolean remote = false;
                if(0 == which)
                    remote = true;

                deleteSelectedMessages(selection, remote);
            }
        });

        builder.show();
    }

    private void closeContextUserInterfaceClosed() {
        mAdapter.clearSelections();
        mSelectionMode = false;
    }

    private ActionMode mActionMode = null;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();

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

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = "ActionModeCallback";


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);

            menu.findItem(R.id.menu_reply).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_star).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_resend).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_copy).setVisible(true);
            menu.findItem(R.id.menu_copy).setEnabled(true);
            menu.findItem(R.id.menu_forward).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_forward).setVisible(mMesiboUIOptions.enableForward);
            menu.findItem(R.id.menu_forward).setEnabled(mMesiboUIOptions.enableForward);
            menu.findItem(R.id.menu_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            menu.findItem(R.id.menu_reply).setVisible(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            int enabled = onGetEnabledActionItems();
            menu.findItem(R.id.menu_copy).setVisible(false);

            menu.findItem(R.id.menu_resend).setVisible((enabled&MessagingFragment.MESIBO_MESSAGECONTEXTACTION_RESEND) > 0);
            menu.findItem(R.id.menu_copy).setVisible((enabled&MessagingFragment.MESIBO_MESSAGECONTEXTACTION_COPY) > 0);
            menu.findItem(R.id.menu_reply).setVisible((enabled&MessagingFragment.MESIBO_MESSAGECONTEXTACTION_REPLY) > 0);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int mesiboItemId = 0;

            if (item.getItemId() == R.id.menu_remove) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_DELETE;
            } else if (item.getItemId() == R.id.menu_copy) {

                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_COPY;

            } else if (item.getItemId() == R.id.menu_resend) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_RESEND;
            } else if (item.getItemId() == R.id.menu_forward) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_FORWARD;
            } else if (item.getItemId() == R.id.menu_star) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_FAVORITE;
            } else if (item.getItemId() == R.id.menu_reply) {
                mesiboItemId = MessagingFragment.MESIBO_MESSAGECONTEXTACTION_REPLY;
            }

            if(mesiboItemId > 0) {
                onContextMenuClicked(mesiboItemId);
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
