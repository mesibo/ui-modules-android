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

package com.mesibo.mediapicker;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class AlbumStartActivity extends AppCompatActivity implements GalleryPhotoGridFragment.facebookPicturecallback {

    FragmentTransaction mFragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fb_activity_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra("title");
        ab.setTitle(title);
        ab.setBackgroundDrawable(new ColorDrawable(MediaPicker.getToolbarColor()));

        // Begin the transaction
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Replace the contents of the container with the new fragment
        mFragmentTransaction.replace(R.id.fb_fragment, new AlbumPhotoListFragment());

        // or mFragmentTransaction.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        mFragmentTransaction.commit();

    }

    @Override
    public  void facebookPictureSelected(String filePath) {

        Intent reverseIntent = new Intent();
        reverseIntent.putExtra("PATH",filePath);
        setResult(2,reverseIntent);
        finish();//finishing activity

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==android.R.id.home) {
            onBackPressed();
            return true;


        } else if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
