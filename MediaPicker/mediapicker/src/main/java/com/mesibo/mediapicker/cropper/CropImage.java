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

import com.mesibo.mediapicker.R;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper to simplify crop image work like starting pick-image acitvity and handling camera/gallery
 * intents.<br>
 * The goal of the helper is to simplify the starting and most-common usage of image cropping and
 * not all porpose all possible scenario one-to-rule-them-all code base. So feel free to use it as
 * is and as a wiki to make your own.<br>
 * Added value you get out-of-the-box is some edge case handling that you may miss otherwise, like
 * the stupid-ass Android camera result URI that may differ from version to version and from device
 * to device.
 */
@SuppressWarnings("WeakerAccess, unused")
public final class CropImage {

  // region: Fields and Consts

  /** The key used to pass crop image source URI to {@link CropImageActivity}. */
  public static final String CROP_IMAGE_EXTRA_SOURCE = "CROP_IMAGE_EXTRA_SOURCE";

  /** The key used to pass crop image options to {@link CropImageActivity}. */
  public static final String CROP_IMAGE_EXTRA_OPTIONS = "CROP_IMAGE_EXTRA_OPTIONS";

  /** The key used to pass crop image bundle data to {@link CropImageActivity}. */
  public static final String CROP_IMAGE_EXTRA_BUNDLE = "CROP_IMAGE_EXTRA_BUNDLE";

  /** The key used to pass crop image result data back from {@link CropImageActivity}. */
  public static final String CROP_IMAGE_EXTRA_RESULT = "CROP_IMAGE_EXTRA_RESULT";

  /**
   * The request code used to start pick image activity to be used on result to identify the this
   * specific request.
   */
  public static final int PICK_IMAGE_CHOOSER_REQUEST_CODE = 200;

  /** The request code used to request permission to pick image from external storage. */
  public static final int PICK_IMAGE_PERMISSIONS_REQUEST_CODE = 201;

  /** The request code used to request permission to capture image from camera. */
  public static final int CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE = 2011;

  /**
   * The request code used to start {@link CropImageActivity} to be used on result to identify the
   * this specific request.
   */
  public static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203;

  /** The result code used to return error from {@link CropImageActivity}. */
  public static final int CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE = 204;
  // endregion

  private CropImage() {}

  /**
   * Create a new bitmap that has all pixels beyond the oval shape transparent. Old bitmap is
   * recycled.
   */
  public static Bitmap toOvalBitmap(@NonNull Bitmap bitmap) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    Canvas canvas = new Canvas(output);

    int color = 0xff424242;
    Paint paint = new Paint();

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);

    RectF rect = new RectF(0, 0, width, height);
    canvas.drawOval(rect, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, 0, 0, paint);

    bitmap.recycle();

    return output;
  }


  /**
   * Check if explicetly requesting camera permission is required.<br>
   * It is required in Android Marshmellow and above if "CAMERA" permission is requested in the
   * manifest.<br>
   * See <a
   * href="http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug">StackOverflow
   * question</a>.
   */
  public static boolean isExplicitCameraPermissionRequired(@NonNull Context context) {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && hasPermissionInManifest(context, "android.permission.CAMERA")
        && context.checkSelfPermission(Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED;
  }

  /**
   * Check if the app requests a specific permission in the manifest.
   *
   * @param permissionName the permission to check
   * @return true - the permission in requested in manifest, false - not.
   */
  public static boolean hasPermissionInManifest(
      @NonNull Context context, @NonNull String permissionName) {
    String packageName = context.getPackageName();
    try {
      PackageInfo packageInfo =
          context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
      final String[] declaredPermisisons = packageInfo.requestedPermissions;
      if (declaredPermisisons != null && declaredPermisisons.length > 0) {
        for (String p : declaredPermisisons) {
          if (p.equalsIgnoreCase(permissionName)) {
            return true;
          }
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
    }
    return false;
  }

  /**
   * Get URI to image received from capture by camera.
   *
   * @param context used to access Android APIs, like content resolve, it is your
   *     activity/fragment/widget.
   */
  public static Uri getCaptureImageOutputUri(@NonNull Context context) {
    Uri outputFileUri = null;
    File getImage = context.getExternalCacheDir();
    if (getImage != null) {
      outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
    }
    return outputFileUri;
  }

  /**
   * Get the URI of the selected image from {@link #getPickImageChooserIntent(Context)}.<br>
   * Will return the correct URI for camera and gallery image.
   *
   * @param context used to access Android APIs, like content resolve, it is your
   *     activity/fragment/widget.
   * @param data the returned data of the activity result
   */
  public static Uri getPickImageResultUri(@NonNull Context context, @Nullable Intent data) {
    boolean isCamera = true;
    if (data != null && data.getData() != null) {
      String action = data.getAction();
      isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
    }
    return isCamera || data.getData() == null ? getCaptureImageOutputUri(context) : data.getData();
  }

  /**
   * Check if the given picked image URI requires READ_EXTERNAL_STORAGE permissions.<br>
   * Only relevant for API version 23 and above and not required for all URI's depends on the
   * implementation of the app that was used for picking the image. So we just test if we can open
   * the stream or do we get an exception when we try, Android is awesome.
   *
   * @param context used to access Android APIs, like content resolve, it is your
   *     activity/fragment/widget.
   * @param uri the result URI of image pick.
   * @return true - required permission are not granted, false - either no need for permissions or
   *     they are granted
   */
  public static boolean isReadExternalStoragePermissionsRequired(
      @NonNull Context context, @NonNull Uri uri) {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        && isUriRequiresPermissions(context, uri);
  }

  /**
   * Test if we can open the given Android URI to test if permission required error is thrown.<br>
   * Only relevant for API version 23 and above.
   *
   * @param context used to access Android APIs, like content resolve, it is your
   *     activity/fragment/widget.
   * @param uri the result URI of image pick.
   */
  public static boolean isUriRequiresPermissions(@NonNull Context context, @NonNull Uri uri) {
    try {
      ContentResolver resolver = context.getContentResolver();
      InputStream stream = resolver.openInputStream(uri);
      if (stream != null) {
        stream.close();
      }
      return false;
    } catch (Exception e) {
      return true;
    }
  }

}
