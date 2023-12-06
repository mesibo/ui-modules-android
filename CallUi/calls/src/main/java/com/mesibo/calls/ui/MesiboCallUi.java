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

package com.mesibo.calls.ui;

import android.content.Context;
import android.content.Intent;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.ui.CallLogs.CallLogsActivity;

import java.lang.ref.WeakReference;

public class MesiboCallUi {

    private static MesiboCallUi _instance = null;
    public static MesiboCallUi getInstance() {
        if(null==_instance)
            synchronized(MesiboCallUi.class) {
                if(null == _instance) {
                    _instance = new MesiboCallUi();
                }
            }

        return _instance;
    }

    public void launchCallLogs(Context context, int flag) {

        Intent intent = new Intent(context, CallLogsActivity.class);

        if(flag > 0)
            intent.setFlags(flag);
        context.startActivity(intent);

    }
}
