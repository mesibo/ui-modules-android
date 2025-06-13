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

package com.mesibo.messaging;

import android.content.Context;

import androidx.annotation.Nullable;

import com.mesibo.api.MesiboProfile;

public interface MesiboUIListener {
    boolean MesiboUI_onInitScreen(MesiboUI.MesiboScreen screen);

    @Nullable
    MesiboRecycleViewHolder MesiboUI_onGetCustomRow(MesiboUI.MesiboScreen screen, MesiboUI.MesiboRow row);

    boolean MesiboUI_onUpdateRow(MesiboUI.MesiboScreen screen, MesiboUI.MesiboRow row, boolean last);

    boolean MesiboUI_onClickedRow(MesiboUI.MesiboScreen screen, MesiboUI.MesiboRow row); // not needed - user can add handler for row.row

    // TBD
    //boolean MesiboUI_onFilterRow(MesiboScreen screen, MesiboRow row);


    //boolean MesiboUI_onShowImage(Context context, MesiboProfile user);
    //void MesiboUI_onShowProfile(Context context, MesiboProfile user);
    //int MesiboUI_onGetMenuResourceId(Context context, int type, MesiboProfile profile, Menu menu);
    //boolean MesiboUI_onMenuItemSelected(Context context, int type, MesiboProfile profile, int item);
    boolean MesiboUI_onShowLocation(Context context, MesiboProfile profile);
}
