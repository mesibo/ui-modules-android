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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Date;
import java.util.UUID;

public final class SocialUtilities {

    public static  String createImageFromBitmap(Context ctx, Bitmap bitmap) {
         String fileName = "myImage";//no .png or .jpg needed
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "temp"+new Date().getTime()+".jpg");
        //String fileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/view.png";
        try {

            FileOutputStream fo = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fo);

            fo.flush();
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return f.getAbsolutePath();
    }

    public static Bitmap getMyProfilePictureBitmap(Context ctx ) {
        Bitmap bitmap =null;
        String filename = "myImage";
        //String filename = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/view.png";

        try {

            bitmap = BitmapFactory.decodeStream(ctx.openFileInput(filename));
        }catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }

        return(bitmap);
    }
    public static  String saveBitmpToFile(Context ctx, Bitmap bitmap, String fileName) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public static Bitmap getBitmapFromfile(Context ctx,String filename ) {
        Bitmap bitmap =null;
        try {

            bitmap = BitmapFactory.decodeStream(ctx.openFileInput(filename));
        }catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }

        return(bitmap);
    }

    public static Bitmap getBitmapFromImagefile(String filename ) {
        Bitmap bitmap =null;
        try {

            bitmap = BitmapFactory.decodeFile(filename);
        }catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }

        return(bitmap);
    }

    public static  Bitmap createSqrCropppedProfile (Bitmap bitmap) {
        Bitmap dstBmp = null;
        if (bitmap.getWidth() >= bitmap.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }


        return dstBmp;
    }

    public  static String bitmapToFilepath(Context mContext, Bitmap icon)

    {


        String file_path = MediaPicker.getPath() +
                "/profiles";
        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();

        String uuid = UUID.randomUUID().toString();
        File file = new File(dir, uuid + ".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        icon.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    public  static Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static int getExifRotation(String filePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = 0;

        if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationInDegrees =  90;
        }
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationInDegrees =  180;
        }
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationInDegrees =  270;
        }

        return rotationInDegrees;
    }

    public static ExifInterface getExif(String filePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exif;
    }

    public static int getDeviceRotation(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int deviceRotation = display.getRotation();
        if(Surface.ROTATION_0 == deviceRotation)
            return 0;
        if(Surface.ROTATION_90 == deviceRotation)
            return 90;
        if(Surface.ROTATION_180 == deviceRotation)
            return 180;
        if(Surface.ROTATION_270 == deviceRotation)
            return 270;
        return 0;
    }

}
