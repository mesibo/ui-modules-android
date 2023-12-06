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

/*
 * Copyright (c) 2010, Sony Ericsson Mobile Communication AB. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *    * Neither the name of the Sony Ericsson Mobile Communication AB nor the names
 *      of its contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.mesibo.mediapicker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class containing static utility methods for bitmap decoding and scaling
 *
 * @author Andreas Agvard (andreas.agvard@sonyericsson.com)
 */
public class ScalingUtilities {

    public static class Result {
        public int width = 0, height = 0;
        public int origWidth = 0, origHeight = 0;
        public String mimeType = null;
        public float scale = 1;
        public boolean scaled = false;

    }

    public static Bitmap scale(String pathname, Rect srcRegion, int dstWidth, int dstHeight,
                               ScalingLogic scalingLogic, Result result) {
        return decodeFile(pathname, srcRegion, dstWidth, dstHeight, 0, scalingLogic, result);
    }

    public static Bitmap scale(String pathname, Rect srcRegion, int maxSide,
                               ScalingLogic scalingLogic, Result result) {
        return decodeFile(pathname, srcRegion, 0, 0, maxSide, scalingLogic, result);
    }

    public static Bitmap scale(Bitmap bmp, int maxSide,
                               ScalingLogic scalingLogic, Result result) {

        int dstHeight = bmp.getHeight();
        int dstWidth = bmp.getWidth();

        int max = dstWidth;
        if (dstHeight > max) {
            max = dstHeight;
        }

        if(null != result) {
            result.height = dstHeight;
            result.width = dstWidth;
            result.mimeType = null;
            result.scaled = false;
        }

        float multiplier = ((float) maxSide) / ((float) max);

        if (multiplier < 0.999) {
            dstHeight = (int) (dstHeight * multiplier);
            dstWidth = (int) (dstWidth * multiplier);
        } else {
            // no need to scale
            if(scalingLogic == ScalingLogic.FIT)
                return bmp;
        }

        return createScaledBitmap(bmp, dstWidth, dstHeight, scalingLogic, result);

    }

    /**
     * Utility function for decoding an image resource. The decoded bitmap will
     * be optimized for further scaling to the requested destination dimensions
     * and scaling logic.
     *
     * @param res The resources object containing the image data
     * @param resId The resource id of the image data
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Decoded bitmap
     */
    public static Bitmap decodeResource(Resources res, int resId, int dstWidth, int dstHeight,
            ScalingLogic scalingLogic) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inDither = false;  
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; 
        BitmapFactory.decodeResource(res, resId, options);

        options.inJustDecodeBounds = false;
        options.inDither = false;   
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; 
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeResource(res, resId, options);

        return unscaledBitmap;
    }

    public static Bitmap decodeFile(String filename, Rect srcRegion, int dstWidth, int dstHeight, int maxDstSide,
                                    ScalingLogic scalingLogic, Result result) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        options.inDither = false;   
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        BitmapRegionDecoder regionDecoder = null;
        if(null != srcRegion && !srcRegion.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            try {
                regionDecoder = BitmapRegionDecoder.newInstance(filename, false);
            } catch (Exception e) {

            }
        }

        if(null != regionDecoder) {
            regionDecoder.decodeRegion(srcRegion, options);
        }
        else
            BitmapFactory.decodeFile(filename, options);

        if(options.outWidth <= 0 || options.outHeight <= 0) {
            return null;
        }

        if(null != result) {
            result.height = options.outHeight;
            result.width = options.outWidth;
            result.origHeight = options.outHeight;
            result.origWidth = options.outWidth;
            result.mimeType = options.outMimeType;
            result.scaled = false;
        }

        options.inJustDecodeBounds = false;
        options.inDither = false;   
        options.inPreferQualityOverSpeed = true; 
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; 


        boolean returnUnscaled = false;
        float multiplier = 1;

        if(maxDstSide > 0) {
            int max = options.outWidth;
            if (options.outHeight > max) {
                max = options.outHeight;
            }

            multiplier = ((float) maxDstSide) / ((float) max);

            dstHeight = options.outHeight;
            dstWidth = options.outWidth;

            if (multiplier < 0.999) {
                dstHeight = (int) (options.outHeight * multiplier);
                dstWidth = (int) (options.outWidth * multiplier);
            } else {
                if(scalingLogic == ScalingLogic.FIT)
                    returnUnscaled = true;
            }
        }

        //inSampleSize is scaling factor if > 1
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);

        Bitmap unscaledBitmap = null;

        if(null != regionDecoder) {
            unscaledBitmap = regionDecoder.decodeRegion(srcRegion, options);
        }
        else
            unscaledBitmap = BitmapFactory.decodeFile(filename, options);

        if(returnUnscaled)
            return unscaledBitmap;

        if(null != result)
            result.scale = 1/multiplier;

        return createScaledBitmap(unscaledBitmap, dstWidth, dstHeight, scalingLogic, result);
        //return unscaledBitmap;
    }

    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap Bitmap to scale
     * @param dstWidth Wanted width of destination bitmap
     * @param dstHeight Wanted height of destination bitmap
     * @param scalingLogic Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
            ScalingLogic scalingLogic, Result result) {
        if(null == unscaledBitmap)
            return null;

        if(null != result) {
            result.height = dstHeight;
            result.width = dstWidth;
            result.scaled = true;
        }

        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    /**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     *
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     *
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    public static enum ScalingLogic {
        CROP, FIT
    }

    /**
     * Calculate optimal down-sampling factor given the dimensions of a source
     * image, the dimensions of a destination area and a scaling logic.
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal down scaling sample size for decoding
     */
    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
            ScalingLogic scalingLogic) {

        final float srcAspect = (float)srcWidth / (float)srcHeight;
        final float dstAspect = (float)dstWidth / (float)dstHeight;

        if (scalingLogic == ScalingLogic.FIT) {
           if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }

    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
            ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int)(srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int)(srcWidth / dstAspect);
                final int scrRectTop = (int)(srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth Width of source image
     * @param srcHeight Height of source image
     * @param dstWidth Width of destination area
     * @param dstHeight Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
            ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float)srcWidth / (float)srcHeight;
            final float dstAspect = (float)dstWidth / (float)dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

}
