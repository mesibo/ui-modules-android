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

package com.mesibo.uihelper.Utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by root on 7/26/15.
 */
public class AnimationUtils {
    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    /*
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = (toVisibility == View.VISIBLE);
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }
    */

    public static void animateView(final View view, final boolean show, int duration) {
        view.clearAnimation();
        float maxAlpha = 0.6f;
        float startalpha = maxAlpha;
        float endAlpha = 0.0f;
        if(show) {
            startalpha = 0.0f;
            endAlpha = maxAlpha;
        }
        AlphaAnimation animation = new AlphaAnimation(startalpha, endAlpha);
        animation.setDuration(duration);
        //animation.setStartOffset(5000);
        animation.setFillAfter(true);

        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                //view.clearAnimation();
                // start animation1 when animation2 ends (repeat)
                view.setVisibility(show?View.VISIBLE:View.GONE);

                if(!show) {
                    //http://stackoverflow.com/questions/8690029/why-doesnt-setvisibility-work-after-a-view-is-animated
                    view.clearAnimation();
                    view.setVisibility(View.GONE);
                    //view.setClickable(false);
                }


            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
        });
    }



	private void animateImage(ImageView iv) {
		int vy = iv.getLayoutParams().height;

		int x = iv.getDrawable().getBounds().width()/2;
		int y = iv.getDrawable().getBounds().height()/2;
		float animation_range = -100f;
		int duration = 8000; // time to cover the range, more the value, slower the animation
        TranslateAnimation _translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, animation_range, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f);
		_translateAnimation.setDuration(duration);
		_translateAnimation.setRepeatCount(Animation.INFINITE); // -1
		_translateAnimation.setRepeatMode(Animation.REVERSE); // REVERSE
		_translateAnimation.setInterpolator(new LinearInterpolator());
        iv.startAnimation(_translateAnimation);
	}

}
