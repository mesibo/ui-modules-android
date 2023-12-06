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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.mesibo.contactutils.ContactUtils;

import java.lang.ref.WeakReference;

public class PhoneAuthentication implements LoginCredentials.Listener {

    private WeakReference<PhoneAuthenticationHelper.Listener> mListener = null;
    private PhoneAuthenticationHelper.PhoneNumber mPhone = null;
    private AppCompatActivity mActivity = null;
    private LoginCredentials mLoginCredentials = null;
    private ContactUtils.PhoneNumber mParsedNumber = null;
    private int mReqCode = -1;

    PhoneAuthentication(AppCompatActivity activity, PhoneAuthenticationHelper.PhoneNumber phone, PhoneAuthenticationHelper.Listener listner, int reqCode) {
        mActivity = activity;
        mReqCode = reqCode;

        if(null != listner) {
            mListener = new WeakReference<PhoneAuthenticationHelper.Listener>(listner);
        }

        update(phone);

        invokePhoneListener();

        mLoginCredentials = new LoginCredentials(getActivity(), mPhone.mSmartLockUrl, this);
        mLoginCredentials.requestHint(mReqCode, mPhone.mSmartLockUrl != null);

    }

    private AppCompatActivity getActivity() {
        return mActivity;
    }

    private void invokePhoneListener() {
        if(null == mListener) return;

        PhoneAuthenticationHelper.Listener l = mListener.get();
        if(null == l)
            return;

        l.Mesibo_onPhoneAuthenticationNumber(mPhone);
    }

    @Override
    public void onSavedCredentials(ContactUtils.PhoneNumber phone) {

        if(null != phone) {
            mPhone.mCountryCode = phone.mCountryCode;
            mPhone.mCountryName = phone.mCountry;
            mPhone.mNationalNumber = phone.mNationalNumber;
            invokePhoneListener();
            return;
        }

        //phone is null - no phone or other saved credentials found
        if(TextUtils.isEmpty(mPhone.mCountryCode))
            selectCountry();
    }

    @Override
    public void onSaveCompleted() {
        if(null == mListener) return;

        PhoneAuthenticationHelper.Listener l = mListener.get();
        if(null == l)
            return;

        l.Mesibo_onPhoneAuthenticationComplete();
    }

    void stop(boolean success) {
        if(success) {
            ContactUtils.PhoneNumber p = new ContactUtils.PhoneNumber();
            p.mCountryCode = mPhone.mCountryCode;
            p.mNationalNumber = mPhone.mNationalNumber;
            mLoginCredentials.save(p, mReqCode+1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLoginCredentials.onActivityResult(requestCode, resultCode, data);
    }

    public PhoneAuthenticationHelper.PhoneNumber update(PhoneAuthenticationHelper.PhoneNumber phone) {
        mPhone = phone;
        if(null == mPhone)
            mPhone = new PhoneAuthenticationHelper.PhoneNumber();

        if(null == mPhone.mCountryCode) {
            ContactUtils.init(mActivity.getApplicationContext());
            mPhone.mCountryCode = ContactUtils.getCountryCode();
            mPhone.mCountryName = ContactUtils.getCountryName();
        }

        String phoneNumber = mPhone.getNumber();

        if(!TextUtils.isEmpty(phoneNumber))
            mParsedNumber = ContactUtils.getPhoneNumberInfo(phoneNumber);

        if(null != mParsedNumber) {
            mPhone.mValid = mParsedNumber.mValid;
            if(!TextUtils.isEmpty(mParsedNumber.mCountry))
                mPhone.mCountryName = mParsedNumber.mCountry;
            if(!TextUtils.isEmpty(mParsedNumber.mCountryCode))
                mPhone.mCountryCode = mParsedNumber.mCountryCode;
            if(!TextUtils.isEmpty(mParsedNumber.mNationalNumber))
                mPhone.mNationalNumber = mParsedNumber.mNationalNumber;
        }


        return mPhone;
    }

    public void selectCountry() {

        CountryListFragment countryListFragment = new CountryListFragment();
        //countryListFragment.setCountry(code);

        countryListFragment.setOnCountrySelected(new CountryListFragment.CountryListerer() {
            @Override
            public void onCountrySelected(String name, String code) {
                mPhone.mCountryCode = code;
                update(mPhone);
                invokePhoneListener();
            }

            @Override
            public void onCountryCanceled() {

            }
        });

        countryListFragment.show(mActivity.getSupportFragmentManager(), null);
    }

}
