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

import android.graphics.Bitmap;

import androidx.appcompat.view.ActionMode;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;

public class MesiboMessagingFragment extends MessagingFragment {

    public static final int ERROR_PERMISSION=1;
    public static final int ERROR_INVALIDGROUP=2;
    public static final int ERROR_FILE=3;

    public static final String MESSAGE_ID = MesiboUI.MESSAGE_ID;
    public static final String READONLY = "readonly";

    public void sendTextMessage(String newText) {
        super.sendTextMessage(newText);
    }
}

