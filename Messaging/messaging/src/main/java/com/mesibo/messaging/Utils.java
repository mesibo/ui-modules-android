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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mesibo.api.Mesibo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.targetSdkVersion;
import static com.mesibo.messaging.MesiboConfiguration.GOOGLE_PLAYSERVICE_STRING;

public final class Utils {

    public static void setActivityStyle(AppCompatActivity context, Toolbar toolbar) {
        if(null != toolbar) {
            if(MesiboUI.getUiDefaults().mToolbarColor != 0)
                toolbar.setBackgroundColor(MesiboUI.getUiDefaults().mToolbarColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(MesiboUI.getUiDefaults().mStatusbarColor != 0) {
                Window window = context.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(MesiboUI.getUiDefaults().mStatusbarColor);
            }
        }
    }


    public static void createRoundDrawable(Context context, View view, int color, float radiusInDp) {
        GradientDrawable drawable = new GradientDrawable();
        //ColorDrawable drawable = new ColorDrawable();

        drawable.setColor(color);
        float radiusInPx = radiusInDp*8; //some random approx if context is null

        if(null != context)
            radiusInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radiusInDp, context.getResources().getDisplayMetrics());

        drawable.setCornerRadius(radiusInPx);

        view.setBackground(drawable);
    }

    public static void setTitleAndColor(ActionBar actionBar, String title) {
        SpannableString s = new SpannableString(title);
        if(MesiboUI.getUiDefaults().mToolbarTextColor != 0) {
            s.setSpan(new ForegroundColorSpan(MesiboUI.getUiDefaults().mToolbarTextColor), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (null != title)
            actionBar.setTitle(s);

    }

    public static void setTextViewColor(TextView textView, int color) {
        if(color != 0) {
            textView.setTextColor(color);
        }
    }

    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener onclick) {
        //android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        if(null == context) {
            return; //
        }
        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogStyle);
        dialog.setTitle(title);
        dialog.setMessage(message);
        // dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(true);

        dialog.setPositiveButton(android.R.string.yes, onclick);
        dialog.setNegativeButton(android.R.string.cancel, onclick);

        try {
            dialog.show();
        } catch (Exception e) {
            //Log.d(TAG, "Exception showing alert: " + e);
        }
    }

    public static void showAlert(Context context, String title, String message) {
        if(null == context) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showServicesSuspendedAlert(Context context) {
        if(null == context) return;
        if(Mesibo.isAccountSuspended()) {
            Utils.showAlert(context, "Mesibo Service Suspended", "Mesibo Services for this App are suspended. Upgrade to continue");
        }
    }


    public static String getFileSizeString(long fileSize) {
        if(fileSize <= 0) return "";

        String unit = "KB";
        if(fileSize > 1024*1024) {
            unit = "MB";
            fileSize /= 1024*1024;
        } else {
            fileSize /= 1024;
        }

        if(fileSize < 1)
            fileSize = 1;

        return String.valueOf(fileSize) + unit;
    }

    public static  boolean saveBitmpToFilePath(Bitmap bmp, String filePath) {
        File file = new File(filePath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if(null != bmp) {

            bmp.compress(Bitmap.CompressFormat.PNG, 70, fOut);
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static boolean aquireUserPermission(Context context, final String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity)context,
                    permission)) {

            } else {
                ActivityCompat.requestPermissions((AppCompatActivity)context,
                        new String[]{permission},
                        REQUEST_CODE);
            }

            return false;
        }

        return true;

    }

    public static boolean aquireUserPermissions(Context context, List<String> permissions, int REQUEST_CODE) {
        List<String> permissionsNeeded = new ArrayList<>();

        for(String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                permissionsNeeded.add(permission);
            }
        }

        if(permissionsNeeded.isEmpty())
            return true;


        ActivityCompat.requestPermissions((AppCompatActivity) context,
                permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    REQUEST_CODE);

        return false;
    }

    public static boolean checkPermissionGranted(Context context, String permission) {
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }


    /**
     * Method to verify google play services on the device
     * */

    public static boolean checkPlayServices(Activity activity, int requestCode) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        requestCode).show();
            } else {
                Toast.makeText(activity.getApplicationContext(),
                        GOOGLE_PLAYSERVICE_STRING, Toast.LENGTH_LONG)
                        .show();
                activity.finish();
            }
            return false;
        }
        return true;
    }


}
