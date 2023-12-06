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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mesibo.uihelper.Utils.Alert;

import java.lang.ref.WeakReference;

public class MesiboUiHelper {
    public static MesiboUiHelperConfig mMesiboUiHelperConfig = new MesiboUiHelperConfig();
    public static WeakReference<MesiboLoginUiHelperListener> mLoginInterface = null;
    public static IProductTourListener mProductTourListener = null;


    public static void launchTour(Context context, boolean newTask, IProductTourListener listener) {

        mProductTourListener = listener;
        Intent intent = new Intent(context, ProductTourActivity.class);
        if(newTask)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    public static void launchWelcome(Context context, boolean newTask, MesiboLoginUiHelperListener iLogin) {

        mLoginInterface = new WeakReference<MesiboLoginUiHelperListener>(iLogin);
        launch(context, newTask, 0);
    }

    public static void launchLogin(Context context, boolean newTask, int type, MesiboLoginUiHelperListener iLogin) {
        mLoginInterface = new WeakReference<MesiboLoginUiHelperListener>(iLogin);

        launch(context, newTask, type);
    }

    //type 0 - welcome and login
    // type 1 - old login
    // type 2 - new login
    private static void launch(Context context, boolean newTask, int type) {
        Intent intent = new Intent(context, LoginActivity.class);
        if(newTask)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    public static void setConfig(MesiboUiHelperConfig mesiboUiHelperConfig) {
        mMesiboUiHelperConfig = mesiboUiHelperConfig;
    }

    public static MesiboUiHelperConfig getConfig() {
        return mMesiboUiHelperConfig;
    }

    public static MesiboLoginUiHelperListener getLoginInterface() {
        if(null == mLoginInterface)
            return null;

        return mLoginInterface.get();
    }

    public static void showChoicesDialog(final Activity context, String title, String[] items) {
        Alert.showChoicesDialog(context, title, items);
    }
}
