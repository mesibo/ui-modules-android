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

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

public class WelcomePictureViewPagerAdapter extends PagerAdapter
{
    LayoutInflater _inflater = null;

    List<WelcomeScreen> list;




    public WelcomePictureViewPagerAdapter(Context context, List<WelcomeScreen> list )
    {
        super();
        _inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        RelativeLayout layout = (RelativeLayout)_inflater.inflate(R.layout.welcome_launch_picture_pager_layout, null);

        //nameView = (TextView)layout.findViewById(R.id.word);
        //nameView.setText(mDataList.get(position).getName());
         ImageView pictureView;


        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        pictureView = (ImageView)layout.findViewById(R.id.img);

        ViewGroup.LayoutParams params= pictureView.getLayoutParams();
        params.width=RelativeLayout.LayoutParams.MATCH_PARENT;
        params.height=RelativeLayout.LayoutParams.MATCH_PARENT;

        pictureView.setLayoutParams(params);

        pictureView.setImageResource(list.get(position).getResourceId());

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView((View) object);
    }


    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object obj)
    {
        return view.equals(obj);
    }
}
