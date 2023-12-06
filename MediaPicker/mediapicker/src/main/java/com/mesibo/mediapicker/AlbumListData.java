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

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class AlbumListData {

    private String mAlbumName ;
    private String mAlbumPictureUrl;
    private Integer mPhotoCount;
    private Bitmap mAlbumCoverPicture;
    private Boolean mHasImageDownloaded;
    private List<AlbumPhotosData> mPhotosList;


    public AlbumListData(){

        mAlbumName = null;
        mAlbumPictureUrl = null;
        mPhotoCount = 0;
        mAlbumCoverPicture = null;
        mHasImageDownloaded = false;
        mPhotosList = new ArrayList<>();

    }
    public String  getmAlbumName () {

        return mAlbumName;

    }
    public String getmAlbumPictureUrl() {
        return mAlbumPictureUrl;
    }
    public Bitmap getmAlbumCoverPicture(){
        return mAlbumCoverPicture;
    }

    public void setmAlbumName (String name) {
        mAlbumName = name;
    }

    public void setmAlbumPictureUrl(String totalPictures) {
        mAlbumPictureUrl = totalPictures;
    }
    public void setmAlbumCoverPicture (Bitmap albumCover) {
        mAlbumCoverPicture = albumCover;
    }

    public Integer getmPhotoCount() {
        return mPhotoCount;
    }

    public void setmPhotoCount(Integer mPhotoCount) {
        this.mPhotoCount = mPhotoCount;
    }

    public List<AlbumPhotosData> getmPhotosList() {
        return mPhotosList;
    }

    public void setmPhotosList(List<AlbumPhotosData> mPhotosList) {
        this.mPhotosList = mPhotosList;
    }

    public Boolean getmHasImageDownloaded() {
        return mHasImageDownloaded;
    }

    public void setmHasImageDownloaded(Boolean mHasImageDownloaded) {
        this.mHasImageDownloaded = mHasImageDownloaded;
    }
}
