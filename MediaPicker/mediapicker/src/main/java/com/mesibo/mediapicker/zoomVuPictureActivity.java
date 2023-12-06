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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.view.View.GONE;

public class zoomVuPictureActivity extends AppCompatActivity {


    private static final String TAG = "zoomVuPictureActivity";

    ImageFragmentPagerAdapter mImageFragmentPagerAdapter = null;
    public ArrayList<String>  mImageArraylist = null;
    int mStartPosition = 0;
    ViewPager mViewPager = null;


    // These matrices will be used to move and zoom image

    public static String filePath;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_pager);

        Intent intent = getIntent();

        filePath = intent.getStringExtra("filePath");
        mImageArraylist = intent.getStringArrayListExtra("stringImageArray");
        mStartPosition = intent.getIntExtra("position", 0);

        if(mImageArraylist == null & filePath != null) {
            mImageArraylist = new ArrayList<>();
            mImageArraylist.add(filePath);
            mStartPosition = 0 ;
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.zoom_toolbar);
        setSupportActionBar(toolbar);*/

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            String title = getIntent().getStringExtra("title");
            actionBar.setTitle(title);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20000000")));
            actionBar.setBackgroundDrawable(new ColorDrawable(MediaPicker.getToolbarColor()));
        }
        //actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));



        mImageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager(),mImageArraylist);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mImageFragmentPagerAdapter);
        mViewPager.setCurrentItem(mStartPosition);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String>  mImageArray;

        public ImageFragmentPagerAdapter(FragmentManager fm , ArrayList<String> list) {
            super(fm);
            this.mImageArray = list;
        }

        @Override
        public int getCount() {
            if(null == mImageArray)
                return 0;

            return mImageArray.size();
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(this.mImageArray,position);
        }
    }

    public static class SwipeFragment extends Fragment {

        RelativeLayout mTouchLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.zoomvu_layout, container, false);
            ImageViewTouch imageView = (ImageViewTouch) swipeView.findViewById(R.id.imageViewz);
            Bundle bundle = getArguments();
            ArrayList<String> imageArray = bundle.getStringArrayList("images");
            int position = bundle.getInt("position");
            final String imageFileName = imageArray.get(position);

            if (SocialUtilities.isImageFile(imageFileName)) {
                LinearLayout v = (LinearLayout) swipeView.findViewById(R.id.video_layer);
                v.setVisibility(GONE);
                ImageViewTouch touchLayout = (ImageViewTouch) swipeView.findViewById(R.id.imageViewz);

                if (null != ((AppCompatActivity) getActivity()).getSupportActionBar())
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);

                touchLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AppCompatActivity activity = (AppCompatActivity)  getActivity();
                        if(null == activity)
                            return;

                        ActionBar actionBar = activity.getSupportActionBar();
                        if(null == actionBar)
                            return;

                        if (actionBar.isShowing()) {
                            actionBar.hide();
                        } else {
                            actionBar.show();
                        }

                    }
                });
                Bitmap b = BitmapFactory.decodeFile(imageFileName);
                imageView.setImageBitmap(b);

            } else if (SocialUtilities.isVideoFile(imageFileName)) {

                LinearLayout v = (LinearLayout) swipeView.findViewById(R.id.video_layer);
                v.setVisibility(View.VISIBLE);
                //ImageViewTouch touchLayout = (ImageViewTouch) swipeView.findViewById(R.id.imageViewz);
                //ImagePicker imh = ImagePicker.getInstance(getActivity());
                Bitmap b = SocialUtilities.createThumbnailAtTime(imageFileName, 0);
                imageView.setImageBitmap(b);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File playFile = new File(imageFileName);
                        if (playFile.exists()) {
                            intent.setDataAndType(Uri.fromFile(playFile), "video/mp4");
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "The file doesn´t exist!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            return swipeView;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (null != ((AppCompatActivity) getActivity()).getSupportActionBar()) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }


        }

        static SwipeFragment newInstance(ArrayList<String> ImageArray, int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("images", ImageArray);
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }


    }

}
