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
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class  AlbumPhotoListAdapter extends RecyclerView.Adapter<AlbumPhotoListAdapter.AlbumsViewHolder> {

    private List<AlbumListData> mAlbumDataList;
    private static Context mContext;

    private static AlbumPhotoListClickListener mListListener;

    public AlbumPhotoListAdapter(Context context, List<AlbumListData> topicList) {
        this.mAlbumDataList = topicList;
        this.mContext = context;

    }


    public static class AlbumsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mAlbumName;
        protected TextView mTotalPictures;
        protected ImageView mAlbumFrontPicture;
        protected Boolean mHasImageDownloaded;
        protected ProgressBar mAlbuPrrogress;
        public AlbumsViewHolder(View v) {
            super(v);
            mAlbumName = (TextView) v.findViewById(R.id.album_name);
            mTotalPictures = (TextView) v.findViewById(R.id.album_count);
            mAlbumFrontPicture = (ImageView) v.findViewById(R.id.profile);
            mAlbuPrrogress = (ProgressBar) v.findViewById(R.id.al_progress);
            mHasImageDownloaded = false;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListListener.onItemClick(getAdapterPosition(),v);

        }
    }

    @Override
    public AlbumsViewHolder  onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.facebook_album_rv_item, viewGroup, false);
        AlbumsViewHolder tvh = new AlbumsViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(AlbumsViewHolder holder, int i) {
        AlbumListData td = mAlbumDataList.get(i);
        holder.mAlbumName.setText(td.getmAlbumName());
        holder.mTotalPictures.setText(td.getmPhotoCount()+" Pictures");
        String s = td.getmAlbumPictureUrl();

        if(null == s) {
            holder.mAlbumFrontPicture.setImageBitmap(null);
            holder.mAlbuPrrogress.setVisibility(View.GONE);


        }
        else if(!(s.toLowerCase().startsWith("http://") || s.toLowerCase().startsWith("https://"))) {

            holder.mAlbuPrrogress.setVisibility(View.GONE);
            File playFile = new File(s);
            if(playFile.exists()) {
                if(SocialUtilities.isImageFile(s)) {
                    holder.mAlbumFrontPicture.setImageBitmap(BitmapFactory.decodeFile(s));
                }else if(SocialUtilities.isVideoFile(s)) {
                    Bitmap b = SocialUtilities.createThumbnailAtTime(s,0);
                    holder.mAlbumFrontPicture.setImageBitmap(b);
                }
            }else {
                holder.mAlbumFrontPicture.setImageBitmap(null);
            }
            td.setmHasImageDownloaded(true);
        }else {
            if (!td.getmHasImageDownloaded()) {

                //if (null != td.getmAlbumPictureUrl()) {
                holder.mAlbuPrrogress.setVisibility(View.VISIBLE);
                DownloadImage request = new DownloadImage();
                request.setAlbumData(td);
                request.setHandler(holder);
                request.execute(td.getmAlbumPictureUrl());
                //}
            } else {
                holder.mAlbuPrrogress.setVisibility(View.GONE);
                holder.mAlbumFrontPicture.setImageBitmap(td.getmAlbumCoverPicture());
            }
        }

    }
    public void addItem(AlbumListData dataObj, int index) {
        mAlbumDataList.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mAlbumDataList.remove(index);
        notifyItemRemoved(index);
    }
    public interface AlbumPhotoListClickListener {
        public void onItemClick(int position, View v);
    }

    public void setListener (AlbumPhotoListClickListener ml) {
        this.mListListener = ml;
    }


    @Override
    public int getItemCount() {
        return (mAlbumDataList.size());
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        AlbumsViewHolder handler;
        AlbumListData albumData;
        public void setHandler (AlbumsViewHolder handler1) {
            this.handler = handler1;
        }

        public void setAlbumData (AlbumListData ad1) {this.albumData = ad1;}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            /*
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Download Image Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show(); */
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            Bitmap bitmap = null;
            String s = imageURL.trim().toLowerCase();
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            handler.mAlbuPrrogress.setVisibility(View.GONE);
            handler.mAlbumFrontPicture.setImageBitmap(result);
            albumData.setmHasImageDownloaded(true);
            albumData.setmAlbumCoverPicture(result);

        }
    }




}


