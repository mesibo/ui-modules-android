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

/**
 */
public class WelcomeScreen {
    private String mTitle = null;
    private String mDescription = null;

    private int mLayoutId = 0, mResourceId = 0, mColor = 0;

    public WelcomeScreen(String title, String description, int layoutId, int imageResourceId, int backgroundColor) {
        mTitle = title;
        mDescription = description;
        mLayoutId = layoutId;
        mResourceId = imageResourceId;
        mColor = backgroundColor;
    }

    public String getDescription() {
        return mDescription;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public int getResourceId() {
        return mResourceId;
    }

    public void setResourceId(int resourceId) {
        mResourceId = resourceId;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public int getBackgroundColor() { return mColor; }


}