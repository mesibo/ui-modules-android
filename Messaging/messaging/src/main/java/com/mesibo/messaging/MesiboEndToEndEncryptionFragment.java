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

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.mediapicker.MediaPicker;

import java.io.File;


public class MesiboEndToEndEncryptionFragment extends BaseFragment implements View.OnClickListener, MessagingActivityListener {
    private MesiboProfile profile = null;
    private View mView = null;
    private boolean mOptions = false;
    private boolean mLocalPrivate = false;

    public void setProfile(MesiboProfile p) {
        profile = p;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void replaceFirstName(int id) {
        TextView t = (TextView) mView.findViewById(id);
        String text = t.getText().toString();
        text = text.replaceAll("FirstName", profile.getFirstNameOrAddress());
        t.setText(text);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_e2e_info, container, false);

        if(null == mView)
            return mView;

        MesiboImages.init(getActivity());

        if(profile.isSelfProfile()) {
            mView.findViewById(R.id.e2einfo).setVisibility(View.GONE);
            mView.findViewById(R.id.e2eself).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.e2eselfexportbutton).setOnClickListener(this);
            mView.findViewById(R.id.e2eselfloadbutton).setOnClickListener(this);
            return mView;
        }

        mView.findViewById(R.id.e2eopt).setVisibility(View.GONE);

        boolean active = profile.e2ee().isActive();
        if(!active) {
            replaceFirstName(R.id.e2einactivetext);
            mView.findViewById(R.id.e2einfo).setVisibility(View.GONE);
            mView.findViewById(R.id.e2einactive).setVisibility(View.VISIBLE);
            return mView;
        }

        replaceFirstName(R.id.e2etexttop);
        replaceFirstName(R.id.e2etextbottom);
        replaceFirstName(R.id.e2etextlast);
        replaceFirstName(R.id.e2etextlast);
        replaceFirstName(R.id.e2eopttext);
        replaceFirstName(R.id.e2epasstext);
        replaceFirstName(R.id.e2ecerttext);

        mView.findViewById(R.id.e2eoptbutton).setOnClickListener(this);
        mView.findViewById(R.id.e2epassbutton).setOnClickListener(this);
        mView.findViewById(R.id.e2eresetbutton).setOnClickListener(this);
        mView.findViewById(R.id.e2ecertbutton).setOnClickListener(this);

        String fp = profile.e2ee().getFingerprint();
        int len = fp.length();
        String fp_top = Mesibo.e2ee().getFingerprintPart(fp, 0);
        String fp_bottom = Mesibo.e2ee().getFingerprintPart(fp, 1);

        ((TextView) mView.findViewById(R.id.e2e_fp_top)).setText(fp_top);
        ((TextView) mView.findViewById(R.id.e2e_fp_bottom)).setText(fp_bottom);

        return mView;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.e2eoptbutton) {
            mView.findViewById(R.id.e2einfo).setVisibility(View.GONE);
            mView.findViewById(R.id.e2eopt).setVisibility(View.VISIBLE);
            mOptions = true;
        } else if(id == R.id.e2epassbutton) {
            setPassword();
        } else if(id == R.id.e2eresetbutton) {
            reset();

        } else if(id == R.id.e2ecertbutton) {
            mLocalPrivate  = false;
            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILE);
        } else if(id == R.id.e2eselfexportbutton) {
            exportCertificate(getActivity());
        } else if(id == R.id.e2eselfloadbutton) {
            mLocalPrivate  = true;
            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILE);
        }
    }

    private void reset() {
        DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_NEGATIVE:
                        // int which = -2
                        break;
                    case BUTTON_NEUTRAL:
                        // int which = -3
                        break;
                    case BUTTON_POSITIVE:
                        // int which = -1
                        profile.e2ee().reset();
                        break;
                }

                dialog.dismiss();
            }
        };

        Utils.showAlert(getActivity(), "Reset E2EE",
                "Do you want to reset E2EE options set for " +profile.getFirstNameOrAddress() + "?", onclick);
    }

    private void setPassword() {
        final TextView t = (TextView) mView.findViewById(R.id.e2epasstext);
        final String pass = t.getText().toString().trim();
        if(pass.length() < 6) {
            Utils.showAlert(getActivity(), "Password is too short", "Enter minimum 6-chars password");
            return;
        }

        DialogInterface.OnClickListener onclick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        // int which = -1
                        if(profile.e2ee().setPassword(pass)) {
                            Utils.showAlert(getActivity(), "Set Password",
                                    "Password set successfully");
                            t.setText("");
                        }
                        break;
                }

                dialog.dismiss();
            }
        };

        Utils.showAlert(getActivity(), "Set Password",
                "You need to ensure that " +profile.getFirstNameOrAddress() + " also uses the same password.", onclick);

    }


    void exportCertificate(Activity context) {
        // https://pki-tutorial.readthedocs.io/en/latest/mime.html

        String filePath = Mesibo.e2ee().getPublicCertificate();
        if(TextUtils.isEmpty(filePath)) {
            Utils.showAlert(getActivity(), "Error", "Unable to export public certificate");
            return;
        }

        Uri uri = FileProvider.getUriForFile(context, MediaPicker.getAuthority(context), new File(filePath));

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("application/x-pem-file");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Public Certificate");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Public Certificate");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Public Certificate"));

    }

    private void loadPublic(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());

        if(ext.equalsIgnoreCase("pub") || ext.equalsIgnoreCase("cer") ||
                ext.equalsIgnoreCase("crt") || ext.equalsIgnoreCase("pem")) {

            if(profile.e2ee().setPeerCertificate(filePath)) {
                Utils.showAlert(getActivity(), "Successful", "Certificate is set and active");
            } else {
                Utils.showAlert(getActivity(), "Invalid Certificate File", "Unable to set certificate");
            }
        } else {
            Utils.showAlert(getActivity(), "Incorrect File", "Select a valid certificate file (.pub)");
        }
    }

    private void loadPrivate(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());

        if(ext.equalsIgnoreCase("p12") || ext.equalsIgnoreCase("pfx")) {

            if(Mesibo.e2ee().setPrivateCertificate(filePath)) {
                Utils.showAlert(getActivity(), "Successful", "Certificate is set and active");
            } else {
                Utils.showAlert(getActivity(), "Invalid Certificate File", "Unable to private set certificate");
            }
        } else {
            Utils.showAlert(getActivity(), "Incorrect File", "Select a valid private certificate file (.p12/.pfx)");
        }
    }

    @Override
    public boolean Mesibo_onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Activity.RESULT_OK != resultCode)
            return true;


        String filePath = MediaPicker.processOnActivityResult(myActivity(), requestCode, resultCode, data);

        if(null == filePath) {
            return true;
        }

        if(MediaPicker.TYPE_FILE  == requestCode) {
            if(mLocalPrivate) loadPrivate(filePath);
            else loadPublic(filePath);
        }

        return true;
    }


    @Override
    public void Mesibo_onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public boolean Mesibo_onBackPressed() {
        if(mOptions) {
            mView.findViewById(R.id.e2einfo).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.e2eopt).setVisibility(View.GONE);
            mOptions = false;
            return true;
        }
        return false;
    }
}
