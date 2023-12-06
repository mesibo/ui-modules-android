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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

//import com.theartofdev.edmodo.cropper.CropImageView;
import com.mesibo.mediapicker.cropper.CropImageView;

public class ImageEditor extends AppCompatActivity {
    CropImageView mImageView =null;
    Bitmap mImage = null;

    EditText mCaptionEditText=null;
    ImageButton mSendBtn=null;
    String mFilePath=null;
    boolean mShowEditMenu = false;
    boolean mCropMode = false;
    boolean mShowTitle = false;
    boolean mShowToolbar = true;
    boolean mForceShowCropOverlay = false;
    boolean mSquareCrop = false;
    RelativeLayout mCaptionView = null;
    int mFileType = -1; //user provided type

    int mRotation = 0;
    int maxDimension = 1280;

    int mExifRotation = 0;
    private MediaPicker.ImageEditorListener mListener = null;

    public static final int SUCCESS=0;
    public static final int CANCEL=-1;

    private Rect mCropRect = new Rect();
    int mDisplayWidth =0, mDisplayHeght = 0;
    ScalingUtilities.Result mScaleResult = new  ScalingUtilities.Result();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_caption);

        Toolbar toolbar = null; //(Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(MediaPicker.getToolbarColor()));

        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("filepath");
        maxDimension = intent.getIntExtra("maxDimension", 1280);
        mShowEditMenu = intent.getBooleanExtra("showEditControls", false) ;
        mShowTitle = intent.getBooleanExtra("showTitle", true) ;
        //mShowToolbar = intent.getBooleanExtra("showToolbar", true) ;
        mForceShowCropOverlay = intent.getBooleanExtra("showCrop", false) ;
        mSquareCrop = intent.getBooleanExtra("squareCrop", false) ;
        String title = intent.getStringExtra("title");
        if(!TextUtils.isEmpty(title))
            actionBar.setTitle(title);

        if(!mShowEditMenu)
            actionBar.hide();

        mFileType = intent.getIntExtra("type", -1) ;
        mListener = MediaPicker.getImageEditorListener(); //(ImageEditorListener) intent.getSerializableExtra("listener");


        mSendBtn = (ImageButton) findViewById(R.id.caption_send);
        mCaptionEditText = (EditText) findViewById(R.id.caption_edit);
        mCaptionView = (RelativeLayout) findViewById(R.id.caption_view);

        if(!mShowTitle)
            mCaptionEditText.setVisibility(View.INVISIBLE);

        mImageView = (CropImageView) findViewById(R.id.caption_image);
        //if(TextUtils.isEmpty(mFilePath))
         //   return;
        mExifRotation = SocialUtilities.getExifRotation(mFilePath);

        int deviceRotation = SocialUtilities.getDeviceRotation(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;


        Matrix matrix = new Matrix();
        if (mExifRotation != deviceRotation) {
            //matrix.preRotate(deviceRotation - mExifRotation);
        }


        if(MediaPicker.TYPE_CAMERAIMAGE == mFileType || MediaPicker.TYPE_FILEIMAGE == mFileType) {
            loadImage();
        } else if(MediaPicker.TYPE_CAMERAVIDEO == mFileType || MediaPicker.TYPE_FILEVIDEO == mFileType ){

            mImage = ThumbnailUtils.createVideoThumbnail(mFilePath, MediaStore.Images.Thumbnails.MINI_KIND);
            mShowEditMenu = false;
            mForceShowCropOverlay = false;
        }
        else {

            int drawableid = intent.getIntExtra("drawableid", -1) ;
            if(drawableid >= 0) {
                mImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), drawableid);
            }
            mShowEditMenu = false;
            mForceShowCropOverlay = false;
        }

        if(null == mImage) {
            //TBD, alert as not image
            Toast toast=Toast.makeText(getApplicationContext(),"Not an image or corrupt image",Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
            finish();
            return;
        }

        setImage(mImage);
        //if(mExifRotation>0)
          //  mImageView.rotateImage(mExifRotation);
        //Bitmap bmp = ScalingUtilities.decodeFile(mFilePath, 0, 0, 1200, ScalingUtilities.ScalingLogic.FIT);

        //mCaptionView.setImageBitmap(bmp);
        if(mSquareCrop)
            mImageView.setAspectRatio(1, 1);
        else
            mImageView.clearAspectRatio();

        mImageView.setShowCropOverlay(mForceShowCropOverlay);

        if(null != mSendBtn) {
            mSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mImageView.isShowCropOverlay()){
                        updateCropRect(mImageView.getCropRect());
                        loadImage();
                    }

                    String caption = mCaptionEditText.getText().toString();

                    if(null != mListener) {
                        mListener.onImageEdit(mFileType, caption, mFilePath, mImage, SUCCESS);
                        finish();
                        return;
                    }

                    if(null != mImage)
                        mImage.recycle();

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("filepath", mFilePath);
                    returnIntent.putExtra("message", caption);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }

        hideKeyboard();

    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater menuInflater = getMenuInflater();

        if(mShowEditMenu)
            menuInflater.inflate(R.menu.add_image_caption_menu, menu);

        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId()==R.id.action_crop) {
            cropBitmap(true);

        }else {

            if (item.getItemId() == R.id.action_rotate) {

                //we are using our own rotating instead of mImageView.rotateImage to maintain rectangle calculation
                //mImageView.rotateImage(90);
                mImage = rotateBitmap(mImage, 90);
                setImage(mImage);
                // This we are doing since curently no interface in mImageView to get rotated bitmap
                // unless we get cropped view, so we are doing this till it's fixed.
                mRotation += 90;
                if(mRotation == 360)
                    mRotation = 0;

                //TBD, we need to update mImage with rotated view
                return true;

            } else if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);

    }

    public int adjustAngle(int angle) {
        while(angle < 0)
            angle += 360;
        while(angle >= 360)
            angle -= 360;

        // in case some exif gives non 90 degree results
        if(angle != 0 && angle != 90 && angle != 270) {
            angle -= (angle%90);
        }

        return angle;
    }

    public Bitmap rotateBitmap(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void loadImage() {
        mScaleResult = new  ScalingUtilities.Result();
        mImage = ScalingUtilities.scale(mFilePath, mCropRect, maxDimension, ScalingUtilities.ScalingLogic.FIT, mScaleResult);
        if(null == mImage)
            return;

        if(mCropRect.isEmpty()) {
            mCropRect.left = 0;
            mCropRect.top = 0;
            mCropRect.bottom = mScaleResult.origHeight;
            mCropRect.right = mScaleResult.origWidth;
        }

        int angle = adjustAngle(mExifRotation + mRotation);
        //int angle = adjustAngle(mRotation);
        if(angle > 0)
            mImage = rotateBitmap(mImage, angle);

        mDisplayWidth = mImage.getWidth();
        mDisplayHeght = mImage.getHeight();
    }

    public void updateCropRect(Rect r) {
        int angle = adjustAngle(mExifRotation + mRotation);
        if(angle > 0)
            rotateRectangle(mDisplayWidth, mDisplayHeght, r, 0-angle);

        //we need to add scaling factor too
        mCropRect.left += r.left*mScaleResult.scale;
        mCropRect.top += r.top*mScaleResult.scale;
        mCropRect.right = mCropRect.left + (int) ((r.right-r.left)*mScaleResult.scale);
        mCropRect.bottom = mCropRect.top + (int)((r.bottom-r.top)*mScaleResult.scale);
    }

    // x and y is width and height of enclosing rectangle
    public void rotateRectangle(int x, int y, Rect rect, int angle) {
        angle = adjustAngle(angle);
        if(0 == angle)
            return;

        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;

        int r=0, l=0, t=0, b=0;

        if(90 == angle) {
            t = rect.left;
            r = y - rect.top;
            l = r - h; // w is h now
            b = t + w;
        } else if(180 == angle) {
            t = y - rect.bottom;
            r = x - rect.left;
            l = r - w;
            b = t + h;
        } else {
            t = x - rect.right;
            l = rect.top;
            r = l + h; // w is h now
            b = t + w;
        }

        rect.right = r;
        rect.left = l;
        rect.top = t;
        rect.bottom = b;
    }

    public void setImage(Bitmap bmp) {
        mImage = bmp;
        mImageView.setImageBitmap(bmp);
        mDisplayWidth = mImage.getWidth();
        mDisplayHeght = mImage.getHeight();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mCaptionEditText.getWindowToken(), 0);
    }

    public void cropBitmap(boolean crop) {

        mCropMode = false;
        if(mImageView.isShowCropOverlay()) {
            mCropMode = true;
        }

        if(mShowTitle)
            mCaptionEditText.setVisibility(!mCropMode?View.GONE:View.VISIBLE);

        mCaptionView.setVisibility(!mCropMode?View.GONE:View.VISIBLE);

        if(mCropMode) {
            if(crop) {
                updateCropRect(mImageView.getCropRect());
                loadImage();
                setImage(mImage);
            }

            mCropMode = false;

        } else {

            mCropMode = true;
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            hideKeyboard();
        }

        mImageView.setShowCropOverlay(mCropMode);

    }




    @Override
    public void onBackPressed() {

        if(!mForceShowCropOverlay && mImageView.isShowCropOverlay()) {
            cropBitmap(false);
            return;
        }

        if(null != mImage)
            mImage.recycle();

        if(null != mListener) {
            mListener.onImageEdit(mFileType, null, mFilePath, null, CANCEL);
            finish();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
