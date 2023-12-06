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

package com.mesibo.uihelper.Utils;



import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

// We created this class to ensure that all fragments are derived from android.support.v4.app.Fragment
public class BaseFragment extends Fragment implements ActivityListener, View.OnClickListener {
    protected FragmentListener mFragmentListener;
    protected Activity mActivity;
    protected String TABNAME = "";

    // if getActivity is called in another thread after fragment was removed say after API response,
    // getActivity may return NULL and hence we are storing the reference
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try {
            mFragmentListener = (FragmentListener) activity;
        } catch (ClassCastException e) {
            mFragmentListener = null;
            //throw new ClassCastException(activity.toString() + " must implement FragmentListener");
        }
    }

    /*
    //This only works from  viewpager
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Log.d(TAG, "Class " + this.getClass() + " visible " + isVisibleToUser);
        if(null != mFragmentListener)
            mFragmentListener.onFragmentLoaded(this, this.getClass(), isVisibleToUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mFragmentListener)
            mFragmentListener.onFragmentLoaded(this, this.getClass(), true);
    }

    */

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View view) {
        onClick(view.getId());
    }

    public void onClick(int id) {

    }

    public String getName() {
        return TABNAME;
    }

    @Override
    public void onActivityResultPrivate(int requestCode, int resultCode, Intent data) {

    }
}
