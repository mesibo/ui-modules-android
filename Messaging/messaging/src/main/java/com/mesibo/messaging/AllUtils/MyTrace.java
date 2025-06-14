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

package com.mesibo.messaging.AllUtils;

import android.os.Trace;

public class MyTrace {

    public static boolean enabled = false;

    public static void start(String name) {
        if(!enabled) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.beginSection(name);
        }
    }

    public static void stop() {
        if(!enabled) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }
    }

    public static void setEnable(boolean enable) {
        enabled = enable;
    }
}
