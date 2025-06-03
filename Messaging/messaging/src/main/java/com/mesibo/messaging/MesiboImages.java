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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.mesibo.api.Mesibo;

import static com.mesibo.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE;
import static com.mesibo.messaging.MesiboConfiguration.DEFAULT_GROUP_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.DEFAULT_LOCATION_IMAGE;
import static com.mesibo.messaging.MesiboConfiguration.DEFAULT_PROFILE_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.DELETED_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.DELETED_TINT_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.ERROR_TINT_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_CALL__TINT_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VIDEOCALL_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.MISSED_VOICECALL_DRAWABLE;
import static com.mesibo.messaging.MesiboConfiguration.NORMAL_TINT_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.READ_TINT_COLOR;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_ERROR;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_NOTIFIED;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_READ;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_SEND;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_TIMER;


public class MesiboImages {
    private static Bitmap[] mStatusBitmaps = {null, null, null, null, null, null};
    private static Bitmap mMissedVideoCallImage = null;
    private static Bitmap mMissedVoiceCallImage = null;
    private static Bitmap mDeletedMessageImage = null;
    private static Drawable mMissedVideoCallDrawable = null;
    private static Drawable mMissedVoiceCallDrawable = null;
    private static Drawable mDeletedMessageDrawable = null;
    private static Bitmap mE2EEImage = null;
    private static Bitmap mHeaderImage = null;

    public static int[] deliveryStatus = new int[] {
    STATUS_TIMER,
    STATUS_SEND,
    STATUS_NOTIFIED,
    STATUS_READ,
    STATUS_ERROR
    };

    private static Context mContext = null;

    //private static String  mDefaultUserPath = null;
    //private static String  mDefaultGroupPath = null;

    public static void init(Context context) {

        if(null != mContext)
            return;

        mContext = context;

        /*
        if(null == mDefaultUserPath) {
            mDefaultUserPath = getPicturePath(context, DEFAULT_PROFILE_PICTURE, "defaultUser");
        }

        if( null == mDefaultGroupPath) {
            mDefaultGroupPath = getPicturePath(context, DEFAULT_GROUP_PICTURE, "defaultGroup");
        }
        */

        getDummyFunction(true);

    }



    /*public static String getDefaultUserPath() {
        return mDefaultUserPath;
    }

    public static String getDefaultGroupPath() {
        return mDefaultGroupPath;
    }
    */


    public static Bitmap getDeletedMessageImage() {
        if(null == mContext) return null;

        if(null == mDeletedMessageImage) {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), DELETED_DRAWABLE);
            mDeletedMessageImage = tint(bmp, DELETED_TINT_COLOR);
            mDeletedMessageDrawable = new BitmapDrawable(mContext.getResources(), mDeletedMessageImage);
        }

        return mDeletedMessageImage;
    }

    public static Drawable getDeletedMessageDrawable() {
            if(null == mDeletedMessageDrawable)
                getDeletedMessageImage();
            return mDeletedMessageDrawable;
    }

    public static Bitmap getStatusImage(int status) {
        if(null == mContext) return null;

        int tintColor = NORMAL_TINT_COLOR;

        if(Mesibo.MSGSTATUS_DELIVERED == status) {
            tintColor = DELETED_TINT_COLOR;
        }
        else if(Mesibo.MSGSTATUS_READ == status) {
            tintColor = READ_TINT_COLOR;
        }
        else if(status > Mesibo.MSGSTATUS_READ) {
            status = Mesibo.MSGSTATUS_READ + 1;
            tintColor = ERROR_TINT_COLOR;
        }

        if(null != mStatusBitmaps[status])
            return mStatusBitmaps[status];

        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), deliveryStatus[status]);
        mStatusBitmaps[status] = tint(bmp, tintColor);
        return mStatusBitmaps[status];
    }

    public static Bitmap getE2EEImage() {
        if(null == mContext) return null;

        if(null == mE2EEImage) {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), MesiboUI.getUiDefaults().e2eeIcon);
            mE2EEImage = tint(bmp, MesiboUI.getUiDefaults().e2eeIconColor);
        }

        return mE2EEImage;
    }

    public static Bitmap getHeaderImage() {
        if(null == mHeaderImage) {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), MesiboUI.getUiDefaults().headerIcon);
            mHeaderImage = tint(bmp, MesiboUI.getUiDefaults().headerIconColor);
        }

        return mHeaderImage;
    }

    public static Bitmap getMissedVideoCallImage() {
        if(null == mContext) return null;

        if(null == mMissedVideoCallImage) {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), MISSED_VIDEOCALL_DRAWABLE);
            mMissedVideoCallImage = tint(bmp, MISSED_CALL__TINT_COLOR);
            mMissedVideoCallDrawable = new BitmapDrawable(mContext.getResources(), mMissedVideoCallImage);
        }

        return mMissedVideoCallImage;
    }

    public static Bitmap getMissedVoiceCallImage() {
        if(null == mContext) return null;

        if(null == mMissedVoiceCallImage) {
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), MISSED_VOICECALL_DRAWABLE);
            mMissedVoiceCallImage = tint(bmp, MISSED_CALL__TINT_COLOR);
            mMissedVoiceCallDrawable = new BitmapDrawable(mContext.getResources(), mMissedVoiceCallImage);
        }

        return mMissedVoiceCallImage;
    }

    public static Bitmap getMissedCallImage(boolean video) {
        if(video) return getMissedVideoCallImage();
        return getMissedVoiceCallImage();
    }

    public static Drawable getMissedCallDrawable(boolean video) {
        if(null == mContext) return null;

        if(video) {
            if(null == mMissedVideoCallDrawable)
                getMissedVideoCallImage();
            return mMissedVideoCallDrawable;
        }

        if(null == mMissedVoiceCallDrawable)
            getMissedVoiceCallImage();
        return mMissedVoiceCallDrawable;
    }



    // TBD, use weak reference
    private static Bitmap mDefaultUserBmp = null;
    private static Bitmap mDefaultGroupBmp = null;
    private static Drawable mDefaultUserRoundedDrawable = null;
    public static Bitmap getDefaultUserBitmap() {
        if(null != mDefaultUserBmp)
            return mDefaultUserBmp;

        mDefaultUserBmp = BitmapFactory.decodeResource(mContext.getResources(), DEFAULT_PROFILE_PICTURE);
        return mDefaultUserBmp;
    }

    public static Bitmap getDefaultGroupBitmap() {
        if(null == mContext) return null;

        if(null != mDefaultGroupBmp)
            return mDefaultGroupBmp;

        mDefaultGroupBmp = BitmapFactory.decodeResource(mContext.getResources(), DEFAULT_GROUP_PICTURE);
        return mDefaultGroupBmp;
    }
    public static Drawable getDefaultRoundedDrawable() {
        if(null == mContext) return null;

        if(null != mDefaultUserRoundedDrawable)
            return mDefaultUserRoundedDrawable;

        mDefaultUserRoundedDrawable = new RoundImageDrawable(getDefaultUserBitmap());
        return mDefaultUserRoundedDrawable;
    }

    // TBD, use weak reference
    private static Bitmap mDefaultLocationBmp = null;
    public static Bitmap getDefaultLocationBitmap() {
        if(null == mContext) return null;

        if(null == mDefaultLocationBmp)
            mDefaultLocationBmp = BitmapFactory.decodeResource(mContext.getResources(), DEFAULT_LOCATION_IMAGE);

        return mDefaultLocationBmp;
    }

    //This is a dummy function so that when we optimize unused resources, these resources wont be deleted
    public static int getDummyFunction(boolean test) {
        if(test)
            return 0;

        int checkExistence = mContext.getResources().getIdentifier("file_audio" , "drawable", mContext.getPackageName());
        checkExistence += mContext.getResources().getIdentifier("file_doc" , "drawable", mContext.getPackageName());
        checkExistence += mContext.getResources().getIdentifier("file_file" , "drawable", mContext.getPackageName());
        checkExistence += mContext.getResources().getIdentifier("file_pdf" , "drawable", mContext.getPackageName());
        checkExistence += mContext.getResources().getIdentifier("file_txt" , "drawable", mContext.getPackageName());
        checkExistence += mContext.getResources().getIdentifier("file_xls" , "drawable", mContext.getPackageName());
        checkExistence += mContext.getResources().getIdentifier("file_xml" , "drawable", mContext.getPackageName());
        return checkExistence;
    }

    public static int getFileDrawable(String fileName) {
        if(TextUtils.isEmpty(fileName)) return DEFAULT_FILE_IMAGE;

        int dotindex = fileName.lastIndexOf(".");
        if(dotindex < 0) return DEFAULT_FILE_IMAGE;

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

        if(TextUtils.isEmpty(ext)) return DEFAULT_FILE_IMAGE;

        if(ext.length() > 3)
            ext = ext.substring(0, 3);

        int checkExistence = mContext.getResources().getIdentifier("file_" + ext, "drawable", mContext.getPackageName());

        if ( checkExistence != 0 ) {  // the resouce exists...
            return checkExistence;
        }

        if(ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("wav") || ext.equalsIgnoreCase("amr") || ext.equalsIgnoreCase("aif") || ext.equalsIgnoreCase("wma")) {
            checkExistence = mContext.getResources().getIdentifier("file_audio", "drawable", mContext.getPackageName());

            if ( checkExistence != 0 ) {  // the resouce exists...
                return checkExistence;
            }
        }

        return DEFAULT_FILE_IMAGE;
    }

    public static Bitmap tint(Bitmap bmp, int color) {
        //int color = Color.parseColor(color);
        if(null == bmp)
            return null;

        int red   = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue  = color
                & 0xFF;

        float[] matrix = { 0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0 };

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);

        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        //ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        //paint.setColorFilter(filter);


        //Bitmap resultBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight());
        if(bmp.isMutable()) {

        }

        Bitmap resultBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);

        //image.setImageBitmap(resultBitmap);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }
}
