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

import java.util.List;

/**
 * Created by root on 1/14/17.
 */

public class MesiboUiHelperConfig {
    public static List<WelcomeScreen> mScreens = null;
    public static boolean mScreenAnimation = false;

    public static boolean mVerifyPhone = false;

    public static String mWelcomeTermsText = "By registering, you agree to our <b>Terms of services</b> and <b>privacy policy</b>";
    public static String mWelcomeBottomText = "We will never share your information";
    public static String mWelcomeBottomTextLong = "Mesibo never publishes anything on your facebook wall\n\n";
    public static String mTermsUrl = "https://mesibo.com";
    public static String mWebsite = "https://mesibo.com";
    public static String mWelcomeButtonName = "Sign Up";
    public static String mName = "Mesibo";
    public static int mDefaultCountry = 1;

    public static String mPhoneVerificationTitle = "welcome to mesibo";
    public static String mPhoneVerificationText = "Please enter a valid phone number";
    public static String mPhoneVerificationBottomText = "Note, Mesibo may call instead of sending an SMS if you enter a landline number.";
    public static String mInvalidPhoneTitle = "Invalid Phone Number";
    public static String mPhoneVerificationSkipText = "Already have the OTP?";
    public static String mPhoneSMSVerificatinDiscriptionText = "We have sent a SMS with one-time password (OTP) to %PHONENUMBER%. It may take a few minutes to receive it.";
    public static String mPhoneCALLVerificatinDiscriptionTextRecent = "You will soon receive a call from us on %PHONENUMBER% with one-time password (OTP). Note it down and then enter it here";
    public static String mPhoneCALLVerificatinDiscriptionTextOld = "You might have received a call from us with a verification code on %PHONENUMBER%. Enter that code here";


    public static String mCodeVerificationBottomTextRecent = "You may restart the verification if you don't receive your one-time password (OTP) within 15 minutes";
    public static String mCodeVerificationBottomTextold = "You may restart the verification if you haven't received your one-time password (OTP) so far";


    public static String mCodeVerificationTitle = "Enter one-time password (OTP)";
    public static String mPhoneVerificationRestartText = "Start Again";

    public static String mCountrySubString = "Country";
    public static String mPhoneNumberSubString = "Phone Number";
    public static String mEnterCodeSubString = "Enter one-time password (OTP)";

    public static String mInvalidPhoneMessage = "Invalid phone number: %PHONENUMBER% \n\nPlease check number and try again.";

    public static String mInvalidOTPMessage ="Invalid OTP. Please enter the exact code.";
    public static String mInvalidOTPTitle = "Invalid One-time password (OTP)";

    public static String mMobileConfirmationPrompt = "We are about to verify your phone number:\n\n+%CCODE%-%PHONENUMBER%\n\nIs this number correct?";
    public static String mMobileConfirmationTitle = "Confirm Phone Number";

    public static List<String> mPermissions = null;
    public static String mPermissionsRequestMessage = "Please grant permissions to continue";
    public static String mPermissionsDeniedMessage = "App will close now since the required permissions were not granted";

    public static int mAppIconResourceId = 0;

    //public int mBackgroundColor = 0Xff2196f3;
    public static int mButttonColor = 0Xff1565c0;
    public static int mButttonTextColor = 0Xffffffff;

    public static int mWelcomeBackgroundColor = 0xff2196f3;
    public static int mWelcomeTextColor = 0xffffffff;
    public static int mBackgroundColor = 0xff2196f3;

    public static int mLoginTitleColor = 0xff00868b;
    public static int mLoginDescColor = 0xff555555;
    public static int mLoginBottomDescColor = 0xff555555;

    public static int mPrimaryTextColor = 0xffffffff;
    public static int mErrorTextColor = 0xFFFF2222;
    public static int mSecondaryTextColor = 0xff000000 ;

    public static OtpView.OtpViewConfig otpConfig = new OtpView.OtpViewConfig();

    //public int mTextEditorUnderlineColor = ~(mBackgroundColor);

    public MesiboUiHelperConfig() {

    }
}
