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

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mesibo.api.MesiboProfile;

public class BaseFragment extends Fragment {
    public Activity mActivity = null;
    public MesiboUIListener mListener = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity) {
            mActivity = (Activity)context;
        }
    }

    @Override
    public final void onDetach() {
        // Handle as usual.
        super.onDetach();
        mActivity = null;
    }

    public Activity myActivity() {
        if(null != mActivity)
            return mActivity;

        return getActivity();
    }

    public void setListener(MesiboUIListener listener) {
        mListener = listener;
    }

    public MesiboUIListener getListener() {
        if(null != mListener) return mListener;


        MesiboUIListener listener = MesiboUI.getListener();
        if(null != listener) return listener;

        return mDummayListener;
    }


    public void waitForContext(boolean finishIfFailed) {

    }

    private MesiboUIListener mDummayListener = new MesiboUIListener() {
        @Override
        public boolean MesiboUI_onInitScreen(MesiboUI.MesiboScreen screen) {
            return false;
        }

        @Nullable
        @Override
        public MesiboRecycleViewHolder MesiboUI_onGetCustomRow(MesiboUI.MesiboScreen screen, MesiboUI.MesiboRow row) {
            return null;
        }

        @Override
        public boolean MesiboUI_onUpdateRow(MesiboUI.MesiboScreen screen, MesiboUI.MesiboRow row, boolean last) {
            return false;
        }

        @Override
        public boolean MesiboUI_onClickedRow(MesiboUI.MesiboScreen screen, MesiboUI.MesiboRow row) {
            return false;
        }

        @Override
        public boolean MesiboUI_onShowLocation(Context context, MesiboProfile profile) {
            return false;
        }
    };
}
