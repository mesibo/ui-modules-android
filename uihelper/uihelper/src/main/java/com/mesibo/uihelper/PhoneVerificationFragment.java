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

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.uihelper.Utils.Alert;
import com.mesibo.uihelper.Utils.BaseFragment;
import com.mesibo.uihelper.Utils.Log;

//import com.afollestad.materialdialogs.MaterialDialog;


public class PhoneVerificationFragment extends BaseFragment implements Alert.DialogListener, MesiboLoginUiHelperResultCallback, OtpView.OtpViewListener {

    private boolean mForced = true;
    private static Boolean mMode = false;
    private final int PHONEVIEW=1, CODEVIEW=2, PROGRESSVIEW=3;

    private int mCurrentView = PHONEVIEW;
    private ProgressDialog mProgressDialog = null;

    TextView mTitle;
    TextView mDescription;
    TextView mBottomNote1;
    TextView mChangeMode;

    TextView mPhoneNumberText;
    TextView mCountryText;
    TextView mEnterCodeText;

    EditText mCountryCode;
    EditText mPhoneNumber;
    EditText mVerificationCode;
    Button mOk;
    TextView mError;
    View mPhoneFields;
    View mCodeFields;
    View mBottomFields;
    LinearLayout mProgressBar;
    MesiboUiHelperConfig mConfig = MesiboUiHelper.getConfig();
    Activity mActivity = null;

    View mView = null;

    public static class Contact {
        public String name = null;
        public String country = null;
        public String phoneNumber = null;
        public String nationalNumber = null;
        public int countryCode = 0;
        public boolean valid = false;
    }

    Contact mContact = new Contact();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle b = this.getArguments();
        if(null != b) {
            mForced = b.getBoolean("forced", true);
        }

        MesiboLoginUiHelperListener i = MesiboUiHelper.getLoginInterface();

        mContact.countryCode = i.MesiboLoginUiHelper_getCountryCode(getActivity());
        if(0 == mContact.countryCode) mContact.countryCode = mConfig.mDefaultCountry;

        View v = inflater.inflate(R.layout.fragment_phone_verification_simple, container, false);
        mView = v;
        mTitle = (TextView) v.findViewById(R.id.title);
        mDescription = (TextView) v.findViewById(R.id.description);
        mBottomNote1 = (TextView) v.findViewById(R.id.bottomNote1);
        mChangeMode = (TextView) v.findViewById(R.id.changemode);
        mCountryCode = (EditText) v.findViewById(R.id.country_code);
        mPhoneNumber = (EditText) v.findViewById(R.id.phone);
        mVerificationCode = (EditText) v.findViewById(R.id.code);

        mPhoneNumber.getBackground().setColorFilter(mConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        mCountryCode.getBackground().setColorFilter(mConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);
        mVerificationCode.getBackground().setColorFilter(mConfig.mSecondaryTextColor, PorterDuff.Mode.SRC_IN);


        mPhoneNumber.setTextColor(mConfig.mSecondaryTextColor);
        mCountryCode.setTextColor(mConfig.mSecondaryTextColor);
        mVerificationCode.setTextColor(mConfig.mSecondaryTextColor);

        v.setBackgroundColor(mConfig.mBackgroundColor);
        mOk = (Button) v.findViewById(R.id.button_next);
        mOk.setBackgroundColor(mConfig.mButttonColor);
        mOk.setTextColor(mConfig.mButttonTextColor);

        mPhoneNumberText = (TextView) v.findViewById(R.id.phone_number_text);
        mPhoneNumberText.setText(mConfig.mPhoneNumberSubString);
        mPhoneNumberText.setTextColor(mConfig.mSecondaryTextColor);

        mCountryText = (TextView) v.findViewById(R.id.country_text);
        mCountryText.setText(mConfig.mCountrySubString);
        mCountryText.setTextColor(mConfig.mSecondaryTextColor);

        mEnterCodeText = (TextView) v.findViewById(R.id.enter_code_text);
        mEnterCodeText.setText(mConfig.mEnterCodeSubString);
        mEnterCodeText.setTextColor(mConfig.mSecondaryTextColor);


        mTitle.setTextColor(mConfig.mLoginTitleColor);
        mDescription.setTextColor(mConfig.mLoginDescColor);
        mBottomNote1.setTextColor(mConfig.mLoginBottomDescColor);
        mChangeMode.setTextColor(mConfig.mPrimaryTextColor);


        mError = (TextView) v.findViewById(R.id.error);
        mError.setTextColor(mConfig.mErrorTextColor);
        mPhoneFields = (View) v.findViewById(R.id.verify_phone_fields);
        mCodeFields = (View) v.findViewById(R.id.verify_code_fields);
        mBottomFields = (View) v.findViewById(R.id.bottomInfoFields);

        mChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleView();
            }
        });

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPhoneVerification();
            }
        });

        setCountryCode();
        mCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAuth.selectCountry();
                //showCountryList();
                selectCountry();
            }
        });

        //mCode = mConfig.mDefaultCountry;
        mProgressDialog = Alert.getProgressDialog(getActivity(), "Please wait...");


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mFragmentListener)
            mFragmentListener.onFragmentLoaded(this, this.getClass(), true);
        showView(PHONEVIEW);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDialog(int id, int state) {

        if(0 == id) {
            if (state == Alert.DIALOG_POSITIVE) {
                mMode = false;
                mProgressDialog.show();
                MesiboLoginUiHelperListener i = MesiboUiHelper.getLoginInterface();
                i.MesiboLoginUiHelper_onLogin(getActivity(), mContact.phoneNumber, null, this);
            }

            return;
        }

        if(1 == id) {
            if (state == Alert.DIALOG_POSITIVE) {
                showOTPDialog();
                return;
            }
        }
    }

    public void refresh() {

        setCountryCode();

        if(!TextUtils.isEmpty(mContact.nationalNumber))
            mPhoneNumber.setText(mContact.nationalNumber);

        mPhoneNumber.setFocusableInTouchMode(true);
        mPhoneNumber.setFocusable(true);
    }

    public void startPhoneVerification() {

        mContact.nationalNumber = mPhoneNumber.getText().toString().trim();

        mContact.phoneNumber = mContact.countryCode + mContact.nationalNumber;

        MesiboLoginUiHelperListener i = MesiboUiHelper.getLoginInterface();

        boolean valid = i.MesiboLoginUiHelper_isPhoneValid(getActivity(), mContact.phoneNumber);

        if(!valid && MesiboUiHelperConfig.mVerifyPhone) {
            showError("Invalid Phone Number");
            Alert.showInvalidPhoneNumber(getActivity());
            return;
        }

        showError(null);

        //String prompt = "We are about to verify your phone number:\n\n+" + mCode+ "-" + number + "\n\nIs this number correct?";
        String conformationPrompt = mConfig.mMobileConfirmationPrompt;
        conformationPrompt = conformationPrompt.replace("%PHONENUMBER%", mContact.nationalNumber);
        conformationPrompt = conformationPrompt.replace("%CCODE%", String.valueOf(mContact.countryCode));

        //Alert.showAlertDialog(getActivity(), mConfig.mMobileConfirmationTitle, conformationPrompt, "Yes", "No", 0, this, true);
        i.MesiboLoginUiHelper_onLogin(getActivity(), mContact.phoneNumber, null, this);

    }

    public void onCancel() {
        getActivity().finish();
    }


    public void startCodeVerification(String code) {
        if(null == code)
            return;
        
	    int codeint = 0;
        try {
            codeint = Integer.parseInt(code);
        } catch (Exception e) {
            showError(mConfig.mInvalidPhoneTitle);
            return;
        }

        showError(null);
        mMode = true;
        mProgressDialog.show();
        MesiboLoginUiHelperListener i = MesiboUiHelper.getLoginInterface();
        i.MesiboLoginUiHelper_onLogin(getActivity(), mContact.phoneNumber, code, this);
    }

    private void setCountryCode() {
        mCountryCode.setText("+" + mContact.countryCode);
    }

    private void showError(String error) {
        if(TextUtils.isEmpty(error)) {
            mError.setVisibility(View.GONE);
            return;
        }

        mError.setText(error);
        mError.setVisibility(View.VISIBLE);
    }

    public void toggleView() {
        showError(null);
        if(PHONEVIEW == mCurrentView)
            showView(CODEVIEW);
        else
            showView(PHONEVIEW);
    }

    public void onBackKeyPressed () {
        getActivity().finish();
    }


    private void showView(int vid) {

        // HIDE keyboard --
        if(PROGRESSVIEW == vid) {
            //mProgressDialog.show();
            return;
        }

        mCurrentView = vid;

        if (PHONEVIEW == vid) {
            mChangeMode.setText(mConfig.mPhoneVerificationSkipText);
            mTitle.setText(mConfig.mPhoneVerificationTitle);
            //mDescription.setText("Mesibo will send you a one-time SMS with the verification code before you can start using Mesibo");
            mDescription.setText(mConfig.mPhoneVerificationText);
            mBottomNote1.setText(mConfig.mPhoneVerificationBottomText);
            mPhoneFields.setVisibility(View.VISIBLE);
            mCodeFields.setVisibility(View.GONE);
        } else {
            showOTPDialog();
        }
    }

    @Override
    public void OtpView_onOtp(String enteredOtp) {
        if(null != enteredOtp)
            startCodeVerification(enteredOtp);
    }

    @Override
    public void OtpView_onResend() {

    }

    public void showOTPDialog() {
        String phone = "your phone";
        if(mContact.phoneNumber != null) {
            phone = "+" + mContact.phoneNumber;
        }

        OtpView.OtpViewConfig config = mConfig.otpConfig;
        config.mPhone = phone;

        OtpView otpView = new OtpView(getActivity(), config,this);
        otpView.showPopup(mView);
    }

    @Override
    public void onLoginResult(boolean result, int delay) {
        //mProgressBar.setVisibility(View.GONE);
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();;
        }

        if(result) {
            if (mMode) {
            } else {
                // phone registration success full
                showView(CODEVIEW);
            }
        } else {
            if (mMode) {
                // launch dialogue saying verification failed
                //String prompt = "Invalid code please enter the exact code:\n\n+" +  mVerificationCode.getText().toString() +  "\n\nIs this code correct?";
                String prompt = mConfig.mInvalidOTPMessage;
                Alert.showAlertDialog(getActivity(), mConfig.mInvalidOTPTitle, prompt, "OK", "Cancel", 1, this, true);

            } else {
                // launche dialogue saying invalid number
                //String prompt = "Please enter different phone:\n\n+" + mPhone +  "\n\n above number may be already registered";
                String prompt = mConfig.mInvalidPhoneMessage;
                prompt = prompt.replace("%MOBILENUMBER%", mContact.phoneNumber);
                Alert.showAlertDialog(getActivity(), mConfig.mInvalidPhoneTitle, prompt, "OK", null, 2, this, true);

            }
        }
        Log.d("REG-Ver","on MesiboUiHelper results");
    }

    public void selectCountry() {

        CountryListFragment countryListFragment = new CountryListFragment();
        //countryListFragment.setCountry(code);

        countryListFragment.setOnCountrySelected(new CountryListFragment.CountryListerer() {
            @Override
            public void onCountrySelected(String name, String code) {
                mContact.countryCode = Integer.valueOf(code);
                setCountryCode();
            }

            @Override
            public void onCountryCanceled() {

            }
        });

        countryListFragment.show(getActivity().getSupportFragmentManager(), null);
    }

}
