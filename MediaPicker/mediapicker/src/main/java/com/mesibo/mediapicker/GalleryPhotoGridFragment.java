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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class GalleryPhotoGridFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView mRecyclerView;

    private GalleryPhotoGridAdapter mAdapter;

    private static List<AlbumPhotosData> mPhotoList;

    private OnFragmentInteractionListener mListener = null;


    public GalleryPhotoGridFragment() {
        // Required empty public constructor

    }
    public interface facebookPicturecallback {
        void facebookPictureSelected(String imagePath);

    }

    public void passPhotoListData(List<AlbumPhotosData> photoList) {
        this.mPhotoList = photoList;

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.content_facebook_photogrid, container, false);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(MediaPicker.getToolbarColor()));
        ab.setTitle("Select picture");


        mRecyclerView = (RecyclerView) view.findViewById(R.id.photogrid_rv);
        //LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));


        mAdapter = new GalleryPhotoGridAdapter(getActivity(), mPhotoList);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.SimpleOnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        String checkUrl = mPhotoList.get(position).getmSourceUrl();
                        if(checkUrl.startsWith("http://") || checkUrl.startsWith("https://")) {
                            String fPath = SocialUtilities.createImageFromBitmap(getActivity().getApplicationContext(),mPhotoList.get(position).getmGridPicture());
                            facebookPicturecallback callback = (facebookPicturecallback)getActivity();
                            callback.facebookPictureSelected(fPath);
                        } else {

                            ArrayList<String> stringImageArray = new ArrayList<String>();
                            for(int i=0; i<mPhotoList.size(); i++) {
                                stringImageArray.add(mPhotoList.get(i).getmSourceUrl());
                            }
                            Intent intent = new Intent(getActivity(), zoomVuPictureActivity.class);
                            intent.putExtra("position",position);
                            intent.putStringArrayListExtra("stringImageArray", stringImageArray);
                            startActivity(intent);

                        }
/*
                        FragmentManager fm = getActivity()
                                .getSupportFragmentManager();
                        fm.popBackStack ("UserProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);*/
                    }
                })
        );


        return view;

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

}
