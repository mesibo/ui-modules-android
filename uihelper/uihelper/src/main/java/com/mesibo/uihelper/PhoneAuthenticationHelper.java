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
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

public class PhoneAuthenticationHelper {

    public interface Listener {
        boolean Mesibo_onPhoneAuthenticationNumber(PhoneNumber phone);
        void Mesibo_onPhoneAuthenticationComplete();
    }

    PhoneAuthentication mAuth;
    public PhoneAuthenticationHelper(AppCompatActivity activity, PhoneAuthenticationHelper.PhoneNumber phone, PhoneAuthenticationHelper.Listener listner, int reqCode) {
        mAuth = new PhoneAuthentication(activity, phone, listner, reqCode);
    }

    public static class PhoneNumber {
        public String mCountryCode = null;
        public String mNationalNumber = null;
        public String mCountryName = null;
        public String mSmartLockUrl = null;
        boolean mValid = false;

        public boolean isValid() {
            return mValid;
        }

        public String getNumber() {


            if(TextUtils.isEmpty(mCountryCode))
                return "";

            String phoneNumber = mCountryCode;

            if(!TextUtils.isEmpty(mNationalNumber))
                phoneNumber += mNationalNumber;

            return phoneNumber;
        }
    }

    public void stop(boolean success) {
        mAuth.stop(success);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAuth.onActivityResult(requestCode, resultCode, data);
    }

    public PhoneNumber update(PhoneNumber phoneNumber) {
        return mAuth.update(phoneNumber);
    }

    public void selectCountry() {
        mAuth.selectCountry();
    }

}
