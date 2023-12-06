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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


public class GrantPermissionActivty  extends AppCompatActivity {

    public static class GrantPermissionConfig {
        String title;
        String message;
        int backgroundColor;
    }

    GrantPermissionConfig mConfig = new GrantPermissionConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.activity_grant_permission);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GrantPermissionFragment grantPermissionFragment = GrantPermissionFragment.newInstance(
                this,
                R.color.white,//text color
                mConfig.backgroundColor, //background color
                R.drawable.ic_message, // icon 1 drawable
                R.drawable.ic_add_contact,// icon 2 drawable
                R.drawable.ic_add_contact,// icon 3 drawable
                mConfig.title, // Heading Text
                mConfig.message, // Sub Heading Content
                true); // false if have to ask for permission ; true if "never ask" is clicked

        fragmentTransaction.add(R.id.up_fragment, grantPermissionFragment, "up");
        fragmentTransaction.commit();
        */
    }
}
