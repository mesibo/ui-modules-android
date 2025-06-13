/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-present mesibo                                              *
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;

// https://developers.google.com/maps/documentation/android-sdk/renderer
/*
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
*/
import java.util.LinkedList;
import java.util.Queue;

public class MesiboMapScreenshot implements OnMapReadyCallback {
    GoogleMap mMap = null;
    Context mContext = null;

    public interface Listener {
        boolean onMapScreenshot(MesiboMessage msg, Bitmap bmp);
    }

    public class Job {
        MesiboMessage msg = null;
        Listener listener = null;
    }

    Queue<Job> mQueue = new LinkedList<Job>();

    public void start(Context context, Bundle savedInstanceState) {
        synchronized (this) {
            if(null != mContext)
                return;
        }
        mContext = context;
        //MapsInitializer.initialize(mContext, Renderer.LATEST, this);
        GoogleMapOptions options = new GoogleMapOptions()
                .liteMode(true);
        MapView mp = new MapView(mContext);
        mp.onCreate(null);
        mp.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(!mQueue.isEmpty()) {
            take_screenshot(mQueue.peek());
        }
    }


    private synchronized void take_screenshot(Job job) {
        if(null == job) return;

        LatLng ll = new LatLng(job.msg.latitude, job.msg.longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 16);
        if(null == cameraUpdate)
            return;
        mMap.moveCamera(cameraUpdate);

        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                byte[] byteArray = null;
                try {
                    bitmap = SettingsScalingUtilities.createScaledBitmap(bitmap, 400, 300, SettingsScalingUtilities.ScalingLogic.CROP);
                    byteArray = Mesibo.bitmapToBuffer(bitmap, false, 80);
                    bitmap.recycle();
                    job.listener.onMapScreenshot(job.msg, bitmap);

                    if(!mQueue.isEmpty()) {
                        take_screenshot(mQueue.peek());
                    }

                } catch (Exception e) {

                }

            }
        });
    }

    // maybe we can queue
    public boolean screenshot(MesiboMessage msg, Listener listener) {
        if(false && null == mMap)
            return false;

        Job job = new Job();
        job.msg = msg;
        job.listener = listener;

        synchronized (this) {
            if(null == mMap || !mQueue.isEmpty()) {
                mQueue.add(job);
                return true;
            }
        }

        take_screenshot(job);
        return true;
    }
}
