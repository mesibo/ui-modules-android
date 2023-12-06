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

package com.mesibo.calls.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.api.MesiboCallActivity;
import com.mesibo.calls.api.MesiboVideoView;

import static com.mesibo.calls.api.MesiboCall.MESIBOCALL_SOUND_RINGING;
import static com.mesibo.calls.api.MesiboCall.MESIBOCALL_UI_STATE_SHOWCONTROLS;
import static com.mesibo.calls.api.MesiboCall.MESIBOCALL_UI_STATE_SHOWINCOMING;


public class CallFragment extends Fragment implements MesiboCall.InProgressListener, View.OnClickListener {

    public static final String TAG = "CallFragment";
    protected MesiboCall.Call mCall = null;
    protected MesiboCall.CallProperties mCp = null;
    protected MesiboCallActivity mActivity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        TextView serviceName = (TextView)view.findViewById(R.id.title);
        serviceName.setText(mCp.ui.title);

        // Create UI controls.
        ui.controlLayout = view.findViewById(R.id.control_container);

        ui.pipVideo = view.findViewById(R.id.pip_video_view);
        ui.fullscreenVideo = view.findViewById(R.id.fullscreen_video_view);
        ui.contactView = view.findViewById(R.id.call_name);
        ui.status = view.findViewById(R.id.call_status);
        ui.disconnectButton = view.findViewById(R.id.button_call_disconnect);
        ui.cameraSwitchButton = view.findViewById(R.id.button_call_switch_camera);
        ui.sourceSwitchButton = view.findViewById(R.id.button_call_switch_source);
        ui.toggleSpeakerButton = view.findViewById(R.id.button_call_toggle_speaker);
        ui.toggleCameraButton = view.findViewById(R.id.button_call_toggle_camera);
        ui.toggleMuteButton = view.findViewById(R.id.button_call_toggle_mic);
        ui.acceptButton = view.findViewById(R.id.incoming_call_connect);
        ui.acceptAudioButton = view.findViewById(R.id.incoming_audio_call_connect);
        ui.declineButton = view.findViewById(R.id.incoming_call_disconnect);

        ui.cameraToggleLayout = view.findViewById(R.id.layout_toggle_camera);
        ui.sourceSwitchLayout = view.findViewById(R.id.layout_switch_source);
        ui.cameraSwitchLayout = view.findViewById(R.id.layout_switch_camera);

        ui.incomingView = view.findViewById(R.id.incoming_call_container);
        ui.inprogressView = view.findViewById(R.id.outgoing_call_container);
        ui.incomingAudioAcceptLayout = view.findViewById(R.id.incoming_audio_accept_container);
        ui.incomingVideoAcceptLayout = view.findViewById(R.id.incoming_video_accept_container);


        ui.remoteMute = view.findViewById(R.id.remote_mute);
        ui.remoteMute.setColorFilter(Color.argb(200, 200, 0, 0));

        ui.disconnectButton.setOnClickListener(this);
        ui.cameraSwitchButton.setOnClickListener(this);
        ui.sourceSwitchButton.setOnClickListener(this);
        ui.toggleSpeakerButton.setOnClickListener(this);
        ui.toggleCameraButton.setOnClickListener(this);
        ui.toggleMuteButton.setOnClickListener(this);
        ui.acceptButton.setOnClickListener(this);
        ui.acceptAudioButton.setOnClickListener(this);
        ui.declineButton.setOnClickListener(this);

        // Swap feeds on pip view click.
        if(null != ui.pipVideo) {
            ui.pipVideo.setOnClickListener(this);
            ui.pipVideo.enablePip(true);
        }

        if(null != ui.fullscreenVideo) {
            ui.fullscreenVideo.setOnClickListener(this);
            ui.fullscreenVideo.scaleToFill(true);
            ui.fullscreenVideo.enableHardwareScaler(false);
        }

        ui.background = view.findViewById(R.id.userImage);

        if(!mCp.ui.showScreenSharing) {
            ui.sourceSwitchLayout.setVisibility(View.GONE);
        }


        ui.thumbnailLayout = view.findViewById(R.id.photo_layout);
        TextView nameView = (TextView)view.findViewById(R.id.call_name);
        //TextView addrView = (TextView)view.findViewById(R.id.call_address);
        ImageView imageView = view.findViewById(R.id.photo_image);
        setUserDetails(nameView, imageView);

        setStatusView(Mesibo.CALLSTATUS_NONE);

        setSwappedFeeds(mCall.isVideoViewsSwapped());
        ui.pipVideo.setVisibility(mCall.isAnswered()?View.VISIBLE:View.GONE);

        mCall.start((MesiboCallActivity) getActivity(), this);
        return view;
    }

    void answer(boolean video) {
        if(mCall.isAnswered() || !mCall.isIncoming()) return;

        mCall.answer(video);
        setSwappedFeeds(false);
        //setCallView();
        if(mCall.isVideoCall() && video)
            ui.pipVideo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        // we can't use switch and hence using if-else
        if(id == R.id.pip_video_view)
            setSwappedFeeds(!mCall.isVideoViewsSwapped());
        else if(id == R.id.fullscreen_video_view)
            toggleCallControlsVisibility();
        else if(id == R.id.incoming_call_disconnect || id == R.id.button_call_disconnect) {
            mCall.hangup();
            setStatusView(Mesibo.CALLSTATUS_COMPLETE);
            //setCallControlsVisibility(true, true);
            mActivity.delayedFinish(500); // if user clicked end, close asap
        }
        else if(id == R.id.incoming_call_connect) {
            answer(true);
        }  else if(id == R.id.incoming_audio_call_connect) {
            answer(false);
        } else if(id == R.id.button_call_toggle_speaker) {
            mCall.toggleAudioDevice(MesiboCall.AudioDevice.SPEAKER);
        } else if(id == R.id.button_call_toggle_mic) {
            boolean enabled = mCall.toggleAudioMute();
            setButtonAlpha(ui.toggleMuteButton, enabled);
        } else if(id == R.id.button_call_switch_camera) {
            mCall.switchCamera();
        } else if(id == R.id.button_call_switch_source) {
            mCall.switchSource();
        } else if(id == R.id.button_call_toggle_camera) {
            boolean enabled = mCall.toggleVideoMute();
            setButtonAlpha(ui.toggleCameraButton, enabled);
        }
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        if(!mCall.isVideoCall()) return;

        mCall.setVideoViewsSwapped(isSwappedFeeds);
        mCall.setVideoView(ui.fullscreenVideo, !isSwappedFeeds);
        mCall.setVideoView(ui.pipVideo, isSwappedFeeds);

    }

    public void setUserDetails(TextView nameView, ImageView image) {

        if(!TextUtils.isEmpty(mCp.user.name)) {
            nameView.setText(mCp.user.name);
        } else
            nameView.setText(mCp.user.address);

        if(null != image)
            image.setImageDrawable(MesiboUtils.getRoundImageDrawable(mCp.ui.userImageSmall));
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mCp.autoAnswer) {
            answer(mCall.isVideoCall());
        }
    }


    public void setStatusView(int status) {
        if(mCall.isAnswered() && mCall.isCallInProgress() && mCall.isCallConnected()) {
            ui.status.setFormat(null);
            ui.status.setText("");
            ui.status.setBase(mCall.getAnswerTime());
            ui.status.start();
            return;
        }

        ui.status.stop();
        ui.mStatusText = statusToString(status, ui.mStatusText);
        ui.status.setText(ui.mStatusText);
    }

    public void updateRemoteMuteButtons() {
        boolean amute = mCall.getMuteStatus(true, false, true);
        boolean vmute = mCall.getMuteStatus(false, true, true);
        if(!amute && !vmute) {
            ui.remoteMute.setVisibility(View.GONE);
            return;
        }
        int id = R.drawable.ic_mesibo_mic_off;
        if(vmute)
            id = R.drawable.ic_mesibo_videocam_off;
        if(vmute && amute)
            id = R.drawable.ic_mesibo_tv_off;

        ui.remoteMute.setImageResource(id);
        ui.remoteMute.setVisibility(View.VISIBLE);
    }


    // Should be called from UI thread
    private boolean mConnected = false;
    private void callConnected() {
        if(mConnected) return;
        mConnected = true;
        if(mCall.isVideoCall()) {
            ui.pipVideo.setVisibility(View.VISIBLE);
            setSwappedFeeds(false);
        }
    }

    @Override
    public void MesiboCall_OnVideoSourceChanged(int source, int index) {
        setButtonAlpha(ui.sourceSwitchButton, source == MesiboCall.MESIBOCALL_VIDEOSOURCE_SCREEN);
    }

    @Override
    public void MesiboCall_OnVideo(MesiboCall.CallProperties p, MesiboCall.VideoProperties video, boolean remote) {

    }

    /* recommended view settings - you can override it */
    @Override
    public void MesiboCall_OnUpdateUserInterface(MesiboCall.CallProperties p, int state, boolean video, boolean enable) {

        if(state == MESIBOCALL_UI_STATE_SHOWCONTROLS) {
            if(!enable && !mCp.ui.autoHideControls)
                return;

            setCallControlsVisibility(enable, false);
            return;
        }

        boolean showIncoming = (state == MESIBOCALL_UI_STATE_SHOWINCOMING);

        ui.incomingView.setVisibility(showIncoming?View.VISIBLE:View.GONE);
        ui.inprogressView.setVisibility(showIncoming?View.GONE:View.VISIBLE);

        // audio call controls visibility
        int acVisibility = video?View.GONE:View.VISIBLE;
        // video call controls visibility
        int vcVisibility = video?View.VISIBLE:View.GONE;
        int vciVisibility = (showIncoming && video)?View.VISIBLE:View.GONE;

        //audio controls
        ui.background.setVisibility(acVisibility);
        if(!video)
            ui.background.setImageBitmap(mCp.ui.userImage);

            //video controls
        ui.pipVideo.setVisibility(vcVisibility);
        ui.fullscreenVideo.setVisibility(vcVisibility);
        ui.cameraToggleLayout.setVisibility(vcVisibility);
        ui.cameraSwitchLayout.setVisibility(vcVisibility);
        if(mCp.ui.showScreenSharing)
            ui.sourceSwitchLayout.setVisibility(vcVisibility);
        ui.thumbnailLayout.setVisibility(vcVisibility);
        ui.incomingVideoAcceptLayout.setVisibility(vciVisibility);
    }

    @Override
    public void MesiboCall_OnStatus(MesiboCall.CallProperties p, int status, boolean video) {

        if(null != mCp.ui.inProgressListener)
            mCp.ui.inProgressListener.MesiboCall_OnStatus(p, status, video);

        setStatusView(status);

        if((status& Mesibo.CALLSTATUS_COMPLETE) > 0) {
            mActivity.delayedFinish(3000);
            return;
        }

        int micStatus = 0;

        switch (status) {

            case Mesibo.CALLSTATUS_CONNECTED:
                callConnected();
                break;
        }

        return;
    }



    public static class CallUserInterface {
        public Chronometer status = null;

        public MesiboVideoView pipVideo;
        public MesiboVideoView fullscreenVideo;

        public TextView contactView;
        public ImageButton cameraSwitchButton;
        public ImageButton sourceSwitchButton;
        public ImageButton toggleCameraButton;
        public ImageButton toggleMuteButton;
        public ImageButton toggleSpeakerButton;
        public ImageView remoteMute;
        public ImageButton acceptButton, acceptAudioButton;
        public ImageButton declineButton;
        public ImageButton disconnectButton;
        public ImageView background;

        public View incomingView, inprogressView, controlLayout;
        public View cameraToggleLayout, cameraSwitchLayout, thumbnailLayout, sourceSwitchLayout;
        public View incomingVideoAcceptLayout, incomingAudioAcceptLayout;

        public String mStatusText = "";

        public int buttonAlphaOff = 127;
        public int buttonAlphaMid = 200;
        public int buttonAlphaOn = 255;

    }

    protected CallUserInterface ui = new CallUserInterface();

    protected void setButtonAlpha(ImageButton v, boolean enable) {
        v.setAlpha((float)(enable?ui.buttonAlphaOn:ui.buttonAlphaOff)/255.0f);
        return;
    }

    @Override
    public void MesiboCall_OnSetCall(MesiboCallActivity activity, MesiboCall.Call call) {
        mActivity = activity;
        mCall = call;
        mCp = mCall.getCallProperties();
        mCp.activity = activity;
        if(null != mCp.ui.inProgressListener)
            mCp.ui.inProgressListener.MesiboCall_OnSetCall(activity, mCall);
    }

    @Override
    public void MesiboCall_OnMute(MesiboCall.CallProperties p, boolean audioMuted, boolean videoMuted, boolean remote) {
        if(null != mCp.ui.inProgressListener)
            mCp.ui.inProgressListener.MesiboCall_OnMute(p, audioMuted, videoMuted, remote);

        if(remote)
            updateRemoteMuteButtons();
        else {
            setButtonAlpha(ui.toggleMuteButton, mCall.getMuteStatus(true, false, false));
            setButtonAlpha(ui.toggleCameraButton, mCall.getMuteStatus(false, true, false));
        }
    }

    @Override
    public boolean MesiboCall_OnPlayInCallSound(MesiboCall.CallProperties p, int type, boolean play) {
        if(!play) {
            mCall.stopInCallSound();
            return true;
        }

        mCall.playInCallSound(mActivity.getApplicationContext(), (MESIBOCALL_SOUND_RINGING == type)?R.raw.mesibo_ring :R.raw.mesibo_busy, true);
        return true;
    }

    @Override
    public void MesiboCall_OnHangup(MesiboCall.CallProperties p, int reason) {
        if(null != mCp.ui.inProgressListener)
            mCp.ui.inProgressListener.MesiboCall_OnHangup(p, reason);
    }

    @Override
    public void MesiboCall_OnAudioDeviceChanged(MesiboCall.CallProperties p, MesiboCall.AudioDevice active, MesiboCall.AudioDevice inactive) {
        setButtonAlpha(ui.toggleSpeakerButton, active == MesiboCall.AudioDevice.SPEAKER);
    }

    @Override
    public void MesiboCall_OnOrientationChanged(boolean landscape, boolean remote) {

    }

    @Override
    public void MesiboCall_OnBatteryStatus(boolean low, boolean remote) {

    }

    @Override
    public void MesiboCall_OnDTMF(MesiboCall.CallProperties p, int digit) {

    }

    public String statusToString(int status, String statusText) {

        switch (status) {
            case Mesibo.CALLSTATUS_NONE:
                statusText = "Initiating Call";
                if(mCall.isIncoming())
                    statusText = "Incoming Call";
                break;

            case Mesibo.CALLSTATUS_INPROGRESS:
                statusText = "Calling";
                break;

            case Mesibo.CALLSTATUS_RINGING:
                statusText = "Ringing";
                break;

            case Mesibo.CALLSTATUS_BUSY:
                statusText = "Busy";
                break;

            case Mesibo.CALLSTATUS_NOANSWER:
                statusText = "No Answer";
                break;

            case Mesibo.CALLSTATUS_NOCALLS:
                statusText = "Calls Not Supported";
                break;

            case Mesibo.CALLSTATUS_NETWORKERROR:
                statusText = "Network Error";
                break;

            case Mesibo.CALLSTATUS_UNREACHABLE:
                statusText = "Not Reachable";
                break;

            case Mesibo.CALLSTATUS_INVALIDDEST:
                statusText = "Invalid Destination";
                break;

            case Mesibo.CALLSTATUS_COMPLETE:
                statusText = "Call Completed";
                break;

            case Mesibo.CALLSTATUS_NOTALLOWED:
                statusText = "Not Allowed";
                break;

            case Mesibo.CALLSTATUS_RECONNECTING:
                statusText = "Reconnecting";
                break;

            case Mesibo.CALLSTATUS_HOLD:
                if(mCall.isAnswered()) {
                    statusText = "On Hold";
                }
                break;


            case Mesibo.CALLSTATUS_ANSWER:
                if(!mCall.isLinkUp()) {
                    statusText = "Connecting";
                }
                break;
        }

        return statusText;
    }

    private boolean callControlFragmentVisible = true;
    protected long autoHideVideoControlsTimeout = 7000;
    // Helper functions.
    protected void toggleCallControlsVisibility() {
        if (!mCall.isAnswered()) {
            return;
        }

        setCallControlsVisibility(!callControlFragmentVisible, true);
    }


    protected void setCallControlsVisibility(boolean visibility, boolean autoHide) {
        if(!mCall.isVideoCall()) return;

        try {
            // Show/hide call control fragment
            callControlFragmentVisible = visibility;
            ui.controlLayout.setVisibility(visibility?View.VISIBLE:View.GONE);

            if (autoHide && visibility && autoHideVideoControlsTimeout > 0)
                triggerDelayedAutoHideControls();
        } catch (Exception e) {

        }
    }

    private Thread mControlHidingThread = null;
    protected void triggerDelayedAutoHideControls() {
        if(null != mControlHidingThread) {
            mControlHidingThread.interrupt();
        }

        mControlHidingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(autoHideVideoControlsTimeout);

                // we set interrupted status from onDestroy
                if(Thread.currentThread().isInterrupted())
                    return;

                // TBD. crashing here sometime after cellular call received while other call in prrogress
                // and we come back here
                setCallControlsVisibility(false, false);
            }
        });

        mControlHidingThread.start();
    }
}
