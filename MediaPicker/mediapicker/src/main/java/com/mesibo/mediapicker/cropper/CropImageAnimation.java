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

// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.mesibo.mediapicker.cropper;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Animation to handle smooth cropping image matrix transformation change, specifically for
 * zoom-in/out.
 */
final class CropImageAnimation extends Animation implements Animation.AnimationListener {

  // region: Fields and Consts

  private final ImageView mImageView;

  private final CropOverlayView mCropOverlayView;

  private final float[] mStartBoundPoints = new float[8];

  private final float[] mEndBoundPoints = new float[8];

  private final RectF mStartCropWindowRect = new RectF();

  private final RectF mEndCropWindowRect = new RectF();

  private final float[] mStartImageMatrix = new float[9];

  private final float[] mEndImageMatrix = new float[9];

  private final RectF mAnimRect = new RectF();

  private final float[] mAnimPoints = new float[8];

  private final float[] mAnimMatrix = new float[9];
  // endregion

  public CropImageAnimation(ImageView cropImageView, CropOverlayView cropOverlayView) {
    mImageView = cropImageView;
    mCropOverlayView = cropOverlayView;

    setDuration(300);
    setFillAfter(true);
    setInterpolator(new AccelerateDecelerateInterpolator());
    setAnimationListener(this);
  }

  public void setStartState(float[] boundPoints, Matrix imageMatrix) {
    reset();
    System.arraycopy(boundPoints, 0, mStartBoundPoints, 0, 8);
    mStartCropWindowRect.set(mCropOverlayView.getCropWindowRect());
    imageMatrix.getValues(mStartImageMatrix);
  }

  public void setEndState(float[] boundPoints, Matrix imageMatrix) {
    System.arraycopy(boundPoints, 0, mEndBoundPoints, 0, 8);
    mEndCropWindowRect.set(mCropOverlayView.getCropWindowRect());
    imageMatrix.getValues(mEndImageMatrix);
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {

    mAnimRect.left =
        mStartCropWindowRect.left
            + (mEndCropWindowRect.left - mStartCropWindowRect.left) * interpolatedTime;
    mAnimRect.top =
        mStartCropWindowRect.top
            + (mEndCropWindowRect.top - mStartCropWindowRect.top) * interpolatedTime;
    mAnimRect.right =
        mStartCropWindowRect.right
            + (mEndCropWindowRect.right - mStartCropWindowRect.right) * interpolatedTime;
    mAnimRect.bottom =
        mStartCropWindowRect.bottom
            + (mEndCropWindowRect.bottom - mStartCropWindowRect.bottom) * interpolatedTime;
    mCropOverlayView.setCropWindowRect(mAnimRect);

    for (int i = 0; i < mAnimPoints.length; i++) {
      mAnimPoints[i] =
          mStartBoundPoints[i] + (mEndBoundPoints[i] - mStartBoundPoints[i]) * interpolatedTime;
    }
    mCropOverlayView.setBounds(mAnimPoints, mImageView.getWidth(), mImageView.getHeight());

    for (int i = 0; i < mAnimMatrix.length; i++) {
      mAnimMatrix[i] =
          mStartImageMatrix[i] + (mEndImageMatrix[i] - mStartImageMatrix[i]) * interpolatedTime;
    }
    Matrix m = mImageView.getImageMatrix();
    m.setValues(mAnimMatrix);
    mImageView.setImageMatrix(m);

    mImageView.invalidate();
    mCropOverlayView.invalidate();
  }

  @Override
  public void onAnimationStart(Animation animation) {}

  @Override
  public void onAnimationEnd(Animation animation) {
    mImageView.clearAnimation();
  }

  @Override
  public void onAnimationRepeat(Animation animation) {}
}
