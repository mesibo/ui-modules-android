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

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ProductTourFragment extends Fragment{
 
    final static String SCREEN_INDEX = "layoutid";
 
    public static ProductTourFragment newInstance(int screenIndex) {
        ProductTourFragment pane = new ProductTourFragment();
        Bundle args = new Bundle();
        args.putInt(SCREEN_INDEX, screenIndex);
        pane.setArguments(args);
        return pane;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MesiboUiHelperConfig config = MesiboUiHelper.getConfig();
        if(null == config) {
            return null;
        }

        int index = getArguments().getInt(SCREEN_INDEX, -1);
        if(index < 0)
            return null;

        WelcomeScreen screen = config.mScreens.get(index);
        int layoutId = screen.getLayoutId();
        if(layoutId <= 0) {
            layoutId = R.layout.tour_fragment;
        }

        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);

        if(layoutId == R.layout.tour_fragment) {
            View v = rootView.findViewById(R.id.welcome_fragment);
            if(null != v && screen.getBackgroundColor() != 0) {
                v.setBackgroundColor(screen.getBackgroundColor());
            }

            ImageView image = (ImageView) rootView.findViewById(R.id.welcomeImage);
            if(null != image)
                image.setImageResource(screen.getResourceId());
            AutoResizeTextView heading = (AutoResizeTextView) rootView.findViewById(R.id.heading);
            if(null != heading) {
                heading.setMaxLines(1);
                heading.setText(screen.getTitle());
            }
            AutoResizeTextView content = (AutoResizeTextView) rootView.findViewById(R.id.content);
            if(null != content) {
                content.setMaxLines(3);
                content.setText(screen.getDescription());
            }
        }

        if(MesiboUiHelper.mProductTourListener != null) {
            MesiboUiHelper.mProductTourListener.onProductTourViewLoaded(rootView, index, screen);
        }

        return rootView;
    }

}
