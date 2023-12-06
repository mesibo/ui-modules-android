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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.uihelper.Utils.OverLayFrameLayout;
import com.mesibo.uihelper.Utils.TouchInterceptLayout;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment implements View.OnTouchListener {

    ViewPager mPictureViewPager;
    ViewPager mBannerViewPager;
    TouchInterceptLayout mTouchLayout;
    OverLayFrameLayout mMobilePngView;
    List<WelcomeScreen> mDataList;
    private LinearLayout mPageIndicator;
    private int mPageDotCount;
    private ImageView[] mPageDotView;
    private float imagePagerWidth = 1;
    private float mBannerViewPagerWidth = 0;
    private  int mScaleX = 1;
    private int mReverseScale = 1;
    TextView    mInfoText;
    TextView mBottomTextLong;
    ImageButton mClosePaneBtn;
    Button mSignInBtn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    MesiboUiHelperConfig mConfig = MesiboUiHelper.getConfig();

    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.welcome_launch_fragment, container, false);
        MesiboUiHelperConfig config = MesiboUiHelper.getConfig();
        if(null == config) {
            return v;
        }

        this.mDataList = config.mScreens;
        mPictureViewPager = (ViewPager) v.findViewById(R.id.viewpager_picture);

        PagerAdapter pagerAdapter = new WelcomePictureViewPagerAdapter(getActivity(), mDataList);
        //mPictureViewPager.addOnPageChangeListener(new PageChangeListener());
        mPictureViewPager.setAdapter(pagerAdapter);
        mPictureViewPager.setPageMargin(0);

        ViewTreeObserver vto = mPictureViewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPictureViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                imagePagerWidth  = mPictureViewPager.getMeasuredWidth();

            }
        });
        mMobilePngView = (OverLayFrameLayout) v.findViewById(R.id.overlay_frame);
        mMobilePngView.requestLayout();
        ViewTreeObserver vto2 = mMobilePngView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMobilePngView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //mPictureViewPager.requestLayout();
                 ViewGroup.LayoutParams layoutParams = mMobilePngView.getLayoutParams();
                 layoutParams.width = (int)(0.66 *mMobilePngView.getMeasuredHeight());
                 layoutParams.height = mMobilePngView.getMeasuredHeight();
                //mMobilePngView.setMinimumWidth((int)(0.66 *mMobilePngView.getMeasuredHeight()));
                 mMobilePngView.setLayoutParams(layoutParams);
            }
        });
        mTouchLayout = (TouchInterceptLayout) v.findViewById(R.id.main_relative_layout);

        mSignInBtn = (Button) v.findViewById(R.id.signin);
        mSignInBtn.setBackgroundColor(mConfig.mWelcomeBackgroundColor);
        mSignInBtn.setTextColor(mConfig.mWelcomeTextColor);
        mSignInBtn.setText(mConfig.mWelcomeButtonName);
        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction(null);
            }
        });


        mTouchLayout.setOnTouchListener(this);
        v.setBackgroundColor(mConfig.mWelcomeBackgroundColor);

        mBottomTextLong = (TextView)  v.findViewById(R.id.bottom_textview);
        mBottomTextLong.setText(mConfig.mWelcomeBottomTextLong);


        mClosePaneBtn = (ImageButton) v.findViewById(R.id.close_pane);

        TextView termsView = (TextView) v.findViewById(R.id.terms);

        termsView.setText(Html.fromHtml(mConfig.mWelcomeTermsText));
        termsView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse(mConfig.mTermsUrl));
                startActivity(in);
            }

        });

        termsView.setMovementMethod(LinkMovementMethod.getInstance());

        // ((TextView) v.findViewById(R.id.textView)).setMovementMethod(LinkMovementMethod.getInstance());
        // ((TextView) v.findViewById(R.id.textView)).setText(Html.fromHtml(getResources().getString(R.string.tnc)));
        mInfoText = (TextView)  v.findViewById(R.id.info_text);
        mInfoText.setText(mConfig.mWelcomeBottomText);
        mInfoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomTextLong.setVisibility(View.VISIBLE);
                mClosePaneBtn.setVisibility(View.VISIBLE);

            }
        });

        mClosePaneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomTextLong.setVisibility(View.GONE);
                mClosePaneBtn.setVisibility(View.GONE);

            }
        });


        RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.main_relative_layout) ;
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomTextLong.setVisibility(View.GONE);
                mClosePaneBtn.setVisibility(View.GONE);
            }
        });





        mBannerViewPager = (ViewPager) v.findViewById(R.id.viewpager_text);

        PagerAdapter pagerAdapterT = new WelcomeBannerViewPagerAdapter(getActivity(), mDataList, mConfig.mWelcomeTextColor);
        //mBannerViewPager.addOnPageChangeListener(new PageChangeListener());
        mBannerViewPager.setAdapter(pagerAdapterT);
        mBannerViewPager.setPageMargin(0);

        ViewTreeObserver vto1 = mBannerViewPager.getViewTreeObserver();
        vto1.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBannerViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mBannerViewPagerWidth = mBannerViewPager.getMeasuredWidth();

            }
        });
        mPageDotCount = pagerAdapter.getCount();
        mPageDotView = new ImageView[mPageDotCount];
        mPageIndicator = (LinearLayout) v.findViewById(R.id.viewPagerIndicator);
        for (int i = 0; i < mPageDotCount; i++) {
            mPageDotView[i] = new ImageView(getActivity());
            mPageDotView[i].setImageDrawable(getResources().getDrawable(R.drawable.unselected_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            mPageIndicator.addView(mPageDotView[i], params);
        }

        mPageDotView[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));

        mBannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


        private int mScrollState = ViewPager.SCROLL_STATE_IDLE;


// float mScaleX = textPagerWodth/imagePagerWidth;

            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                mBottomTextLong.setVisibility(View.GONE);
                mClosePaneBtn.setVisibility(View.GONE);

                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }

                float scaleXX = imagePagerWidth / mBannerViewPagerWidth;
                double dummy = scaleXX * mBannerViewPager.getScrollX();
                int scrolled  = (int)Math.round((double)dummy);
                mPictureViewPager.scrollTo(scrolled, mPictureViewPager.getScrollY());
            }

            @Override
            public void onPageSelected(final int position) {
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mPictureViewPager.setCurrentItem(mBannerViewPager.getCurrentItem(), false);
                }
            }
        });
        mPictureViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int mScrollState = ViewPager.SCROLL_STATE_IDLE;


// float mScaleX = textPagerWodth/imagePagerWidth;

            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {



                if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                    return;
                }

                float scaleXX = mBannerViewPagerWidth /imagePagerWidth;
                double dummy = scaleXX * mPictureViewPager.getScrollX();
                int scrolled  = (int)Math.round((double)dummy);
                mBannerViewPager.scrollTo(scrolled, mBannerViewPager.getScrollY());


            }

            @Override
            public void onPageSelected(final int position) {
                // Page change Operation!
                for (int i = 0; i < mPageDotCount; i++) {
                    mPageDotView[i].setImageDrawable(getResources().getDrawable(R.drawable.unselected_dot));
                }

                mPageDotView[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));


            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                mScrollState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mBannerViewPager.setCurrentItem(mPictureViewPager.getCurrentItem(), false);

                }
            }
        });
        ///mScaleX = mBannerViewPagerWidth/imagePagerWidth;
        //mReverseScale = imagePagerWidth / mBannerViewPagerWidth;


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginInteactionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public boolean onTouch(final View view, MotionEvent event) {

        mBannerViewPager.onTouchEvent(event);

        return false;
    }


}






