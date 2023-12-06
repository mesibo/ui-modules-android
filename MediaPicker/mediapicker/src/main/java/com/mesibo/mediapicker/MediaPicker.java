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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaPicker {

    public static int TYPE_FILEIMAGE      = 10000;
    public static int TYPE_CAMERAIMAGE    = 10001;
    public static int TYPE_FILEVIDEO      = 10002;
    public static int TYPE_CAMERAVIDEO    = 10003;
    public static int TYPE_FACEBOOK       = 10004;
    public static int TYPE_FILE           = 10005;
    public static int TYPE_AUDIO          = 10006;
    public static int TYPE_CUSTOM         = 10007;

    public static int TYPE_CAPTION        = 10015; //maximum value


    public static int BASE_TYPE_VALUE        = 10000;

    //private static Map<String, ImageEditorListener> mEditorListners = new HashMap<String, ImageEditorListener>();\\\

    private static ImageEditorListener mImageEditorListener = null;

    private static String mTempPath = null;
    private static String mAuthority = null;

    public interface ImageEditorListener  {
        void onImageEdit(int type, String caption, String filePath, Bitmap bmp, int result);
    }

    public static void setBaseTypeValue(int val) {

        TYPE_FILEIMAGE = (TYPE_FILEIMAGE-BASE_TYPE_VALUE) + val;
        TYPE_CAMERAIMAGE = (TYPE_CAMERAIMAGE-BASE_TYPE_VALUE) + val;
        TYPE_FILEVIDEO = (TYPE_FILEVIDEO-BASE_TYPE_VALUE) + val;
        TYPE_CAMERAVIDEO = (TYPE_CAMERAVIDEO-BASE_TYPE_VALUE) + val;
        TYPE_FACEBOOK = (TYPE_FACEBOOK-BASE_TYPE_VALUE) + val;
        TYPE_AUDIO = (TYPE_AUDIO-BASE_TYPE_VALUE) + val;
        TYPE_CUSTOM = (TYPE_CUSTOM-BASE_TYPE_VALUE) + val;
        TYPE_CAPTION = (TYPE_CAPTION-BASE_TYPE_VALUE) + val;

        BASE_TYPE_VALUE = val;

    }

    public static int getBaseTypeValue() {
        return BASE_TYPE_VALUE;
    }

    public static int getMaxTypeValue() {
        return TYPE_CAPTION;
    }

    private static int mToolbarColor = 0xff00868b;
    public static void setToolbarColor(int color) {
        mToolbarColor = color;
    }

    public static int getToolbarColor() {
        return mToolbarColor;
    }

    protected static ImageEditorListener getImageEditorListener() {
        return mImageEditorListener;
    }

    public static void launchEditor(Activity context, int type, int drawableid, String title, String filePath, boolean showEditControls, boolean showTitle, boolean showCropOverlay, boolean squareCrop, int maxDimension, ImageEditorListener listener) {
        Intent in = new Intent(context, ImageEditor.class);
        in.putExtra("title", title);
        in.putExtra("filepath", filePath);
        in.putExtra("showEditControls", showEditControls);
        in.putExtra("showTitle", showTitle);
        in.putExtra("showCrop", showCropOverlay);
        in.putExtra("squareCrop", squareCrop);
        in.putExtra("type", type);
        in.putExtra("drawableid", drawableid);
        //in.putExtra("listener", listener);
        mImageEditorListener = listener;
        if(maxDimension > 0)
            in.putExtra("maxDimension", maxDimension);


        if(null == listener)
            context.startActivityForResult(in, TYPE_CAPTION);
        else
            context.startActivity(in);
    }

    public static void launchImageViewer(Activity context, String filePath) {
        Intent intent = new Intent (context, zoomVuPictureActivity.class);
        intent.putExtra("filePath", filePath);
        context.startActivity(intent);
    }

    public static void launchImageViewer(Activity context, ArrayList<String> files, int firstIndex) {
        Intent intent = new Intent (context, zoomVuPictureActivity.class);
        intent.putExtra("position", firstIndex);
        intent.putStringArrayListExtra("stringImageArray", files);
        context.startActivity(intent);
    }

    private static List<AlbumListData> mAlbumList=null;

    public static void launchAlbum(Activity context, List<AlbumListData> albumList) {
        mAlbumList = albumList;
        Intent newIntent = new Intent (context, AlbumStartActivity.class);
        context.startActivity(newIntent);
    }

    public static List<AlbumListData> getAlbumList() {
        return mAlbumList;
    }

    public static void launchPicker(Activity activity, int fileType, String path) {
        ImagePicker.getInstance().pick(activity, fileType, path);
    }

    public static void launchPicker(Activity activity, int fileType) {
        ImagePicker.getInstance().pick(activity, fileType);
    }


    public boolean isActivityStarted(int code) {
        return (code >= getBaseTypeValue() || code <= getMaxTypeValue());
    }

    public static String processOnActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        return ImagePicker.getInstance().processOnActivityResult(activity, requestCode, resultCode, data);
    }

    public static void setPath(String path, String authority) {
        mTempPath = path;
        mAuthority = authority;
    }

    public static void setPath(String path) {
        mTempPath = path;
    }

    public static void setAuthority(String authority) {
        mAuthority = authority;
    }

    public static String getPath() {
        if(TextUtils.isEmpty(mTempPath))
            return android.os.Environment.getExternalStorageDirectory().getPath();

        return mTempPath;
    }

    public static String getAuthority(Context context) {
        if(TextUtils.isEmpty(mAuthority)) {
            return context.getPackageName() + ".provider";
        }
        return mAuthority;
    }

    public static Uri getUri(Context context, File file) {
        return FileProvider.getUriForFile(context, getAuthority(context), file);
    }

    protected static String getTempPath(Context context, String name, String ext, boolean video) {
        //File outputDir = context.getCacheDir(); // context being the Activity pointer
        //File outputFile = File.createTempFile("prefix", "extension", outputDir);

        File storageDir = context.getExternalFilesDir(video?Environment.DIRECTORY_MOVIES:Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
                    name,  /* prefix */
                    ext,         /* suffix */
                    storageDir      /* directory */
            );

            return image.getAbsolutePath();

        } catch (Exception e) {
            return null;
        }
    }
}
