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

package com.mesibo.messaging.AllUtils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import androidx.core.view.VelocityTrackerCompat;

import com.mesibo.api.Mesibo;

import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private VelocityTracker mVelocityTracker = null;
    float avgXVelocity=0, avgYVelocity=0;
    int eventCount = 0;
    private android.os.Handler handler = new android.os.Handler();

    public OnSwipeTouchListener(Context context) {
    }

    public boolean onTouch(final View v, final MotionEvent event) {
        return computeVelocity(event);
    }

    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            //Log.d("mesibocore", "timer fired");
            swipeComplete();
        }
    };


    protected boolean swipeComplete() {
        handler.removeCallbacks(timerRunnable);
        if(eventCount == 0)
            return false;
        avgXVelocity /= eventCount;
        avgYVelocity /= eventCount;
        //Log.d("mesibocore", "Average X velocity: "   + avgXVelocity + ", Y velocity: " + avgYVelocity);
        if (avgXVelocity > 400) {
            return onSwipeRight();
        } else if (avgXVelocity < -400) {
            return onSwipeLeft();
        }
        return false;
    }

    public boolean computeVelocity(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                avgXVelocity = avgYVelocity = 0;
                eventCount = 0;
                if (mVelocityTracker == null) {

                    // Retrieve a new VelocityTracker object to watch the velocity
                    // of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_UP:
                return swipeComplete();

            case MotionEvent.ACTION_MOVE:

                handler.removeCallbacks(timerRunnable);

                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);

                float x_velocity = mVelocityTracker.getXVelocity(pointerId);
                float y_velocity = mVelocityTracker.getYVelocity(pointerId);
                //float x_velocity = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId);
                //float y_velocity = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);
                //Log.d("mesibocore", "X velocity: "   + x_velocity + ", Y velocity: " + y_velocity);

                avgXVelocity += x_velocity;
                avgYVelocity += y_velocity;
                eventCount++;

                handler.postDelayed(timerRunnable, 400);
                break;

              case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                try {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                } catch (Exception e) {

                }
                //if(true) return false;
                return swipeComplete();
        }
        return false;
    }

    public boolean onSwipeRight() {
        return false;
    }

    public boolean onSwipeLeft() {
        return false;
    }

    public boolean onSwipeTop() {
        return false;
    }

    public boolean onSwipeBottom() {
        return false;
    }
}