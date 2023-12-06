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

package com.mesibo.calls.ui.CallLogs;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.api.MesiboCall;
import com.mesibo.calls.ui.R;


import java.util.ArrayList;
import java.util.List;

import static com.mesibo.api.Mesibo.MSGSTATUS_CALLINCOMING;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLOUTGOING;


public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.CallLogViewHolder> implements Filterable {


    private Context context;
    private List<CallLogsItem> mCallList;
    private List<CallLogsItem> callLogsItemList;
    private boolean mCallLogsActivty;
    private Drawable mDefaultImageDrawable;
    private Bitmap mMissedCallIcon, mReceivedCallIcon, mCallMadeIcon, mAudioCallIcon, mVideoCallIcon;


    class CallLogViewHolder extends RecyclerView.ViewHolder {

        TextView name, callTime, msgCount;
        ImageView thumbnail, callStatus, mCallButton;
        LinearLayout mCalllogsRootLayout;

        TextView mCallType, mCallStatusDuration;


        CallLogViewHolder(View view) {
            super(view);

            if (mCallLogsActivty) {

                name = view.findViewById(R.id.name);
                callTime = view.findViewById(R.id.call_time);
                thumbnail = view.findViewById(R.id.thumbnail);
                callStatus = view.findViewById(R.id.call_status_arrow);
                mCallButton = view.findViewById(R.id.audio_video_button);
                msgCount = view.findViewById(R.id.msgCount);
                mCalllogsRootLayout = view.findViewById(R.id.callLogsItemLayout);

            } else {

                mCallType = view.findViewById(R.id.call_type);
                callTime = view.findViewById(R.id.callDetails_time);
                callStatus = view.findViewById(R.id.callDetails_status_arrow);
                mCallStatusDuration = view.findViewById(R.id.call_Status_duration);
                mCallButton = view.findViewById(R.id.call_type_img);
                mCalllogsRootLayout = view.findViewById(R.id.callLogsDetailsItemLayout);

            }

        }
    }

    // https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap resourceToBitmap (Context context, int resource) {
        final Drawable drawable = context.getDrawable(resource);
        if(null != drawable)
            return drawableToBitmap(drawable);
        return null;
    }


    public CallLogsAdapter(Context context, ArrayList<CallLogsItem> callLogsList, Boolean CallLogsActivty) {
        this.context = context;
        this.mCallList = callLogsList;
        this.callLogsItemList = callLogsList;
        this.mCallLogsActivty = CallLogsActivty;

        if(null == context)
            return;

        Resources r = context.getApplicationContext().getResources();
        if(null == r)
            return;

        int mRedColor = r.getColor(R.color.call_missed);
        int mBlueColor = r.getColor(R.color.call_received);
        int mGreenColor = r.getColor(R.color.call_made);
        int mColorPrimary = r.getColor(R.color.mesibo);
        mMissedCallIcon = tint(resourceToBitmap(context.getApplicationContext(), R.drawable.ic_mesibo_call_missed), mRedColor);
        mReceivedCallIcon = tint(resourceToBitmap(context.getApplicationContext(), R.drawable.ic_mesibo_call_received), mBlueColor);
        mCallMadeIcon = tint(resourceToBitmap(context.getApplicationContext(), R.drawable.ic_mesibo_call_made), mGreenColor);
        mAudioCallIcon = tint(resourceToBitmap(context.getApplicationContext(), R.drawable.ic_mesibo_call), mColorPrimary);
        mVideoCallIcon = tint(resourceToBitmap(context.getApplicationContext(), R.drawable.ic_mesibo_videocam), mColorPrimary);
        mDefaultImageDrawable = MesiboUtils.getRoundImageDrawable(BitmapFactory.decodeResource(r, R.drawable.default_user_image));

    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;

        if (mCallLogsActivty) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.call_logs_row_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.call_logs_details_row_item, parent, false);
        }


        return new CallLogViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final CallLogViewHolder holder, final int position) {
        if (callLogsItemList.size() < position) {
            return;
        }

        final CallLogsItem callLogsItem = callLogsItemList.get(position);
        int call_status = callLogsItem.getStatus();


        try {
            String datever = callLogsItem.getTimeStamps().dateVerbal;
            String time = callLogsItem.getTimeStamps().time;

            if (mCallLogsActivty) {
                holder.callTime.setText(datever + " " + time);
            } else {
                holder.callTime.setText(time);
            }

            //holder.callTime.setTextColor(0xff000000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean videoCall = true;
        Bitmap callButtonId = mVideoCallIcon;
        if (callLogsItem.getType() == 1 || callLogsItem.getType() == 3) {
            videoCall = true;
            callButtonId = mVideoCallIcon;
        } else if (callLogsItem.getType() == 0 || callLogsItem.getType() == 2) {
            videoCall = false;
            callButtonId = mAudioCallIcon;
        }

        holder.mCallButton.setImageBitmap(callButtonId);

        final boolean localVideoCall = videoCall;


        if (mCallLogsActivty) {

            holder.name.setText(callLogsItem.getName());


            holder.mCalllogsRootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(context, CallLogsDetailsActivity.class);
                    i.putExtra("pid", callLogsItem.getPeer());
                    context.startActivity(i);
                }
            });


            ///Set Profile Pic
            MesiboProfile mUser = Mesibo.getProfile(callLogsItem.getPeer());
            Bitmap b = mUser.getThumbnail();


            if (null != b) {
                    holder.thumbnail.setImageDrawable(MesiboUtils.getRoundImageDrawable(b));
            } else {
                //TBD, getActivity.getresource crashes sometime if activity is closing
                holder.thumbnail.setImageDrawable(mDefaultImageDrawable);
            }


            //Call button click
            holder.mCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Mesibo.MessageParams mParameter = new Mesibo.MessageParams(callLogsItem.getPeer(), 0, Mesibo.FLAG_DEFAULT, 0);
                    MesiboCall.getInstance().callUi(context, mParameter.profile.address, localVideoCall);
                }
            });

            int count = callLogsItem.getCount();
            if (count > 1) {
                holder.msgCount.setVisibility(View.VISIBLE);
                holder.msgCount.setText("(" + count + ")");
            }

            setCallStatus(holder, call_status, null);


        } else {


            holder.mCalllogsRootLayout.setBackgroundColor(callLogsItem.isSelected() ? Color.LTGRAY : Color.WHITE);


            holder.mCalllogsRootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {


                    callLogsItem.setSelected(!callLogsItem.isSelected());
                    holder.mCalllogsRootLayout.setBackgroundColor(callLogsItem.isSelected() ? Color.LTGRAY : Color.WHITE);


                    return false;
                }
            });

            if (callLogsItem.getDuration() > 0) {
                long minutes = (callLogsItem.getDuration() % 3600) / 60;
                long seconds = callLogsItem.getDuration() % 60;

                String timeString = String.format("%02d:%02d", minutes, seconds);
                holder.mCallStatusDuration.setText(String.valueOf(timeString));
            } else {
                holder.mCallStatusDuration.setText("");
            }

            setCallStatus(holder, call_status, callLogsItem);

        }


    }

    @Override
    public int getItemCount() {
        return callLogsItemList.size();
    }


    private void setCallStatus(CallLogViewHolder holder, int status, CallLogsItem callLogsItem) {
        if (status == Mesibo.MSGSTATUS_CALLMISSED) {
            holder.callStatus.setImageBitmap(mMissedCallIcon);

            if (!mCallLogsActivty) {
                holder.mCallType.setText("Call Missed");
            }

        } else if (status == MSGSTATUS_CALLINCOMING) {
            holder.callStatus.setImageBitmap(mReceivedCallIcon);
            if (!mCallLogsActivty) {
                holder.mCallType.setText("Incoming");
            }
        } else if (status == MSGSTATUS_CALLOUTGOING) {
            holder.callStatus.setImageBitmap(mCallMadeIcon);
            if (!mCallLogsActivty) {
                holder.mCallType.setText("Outgoing");
                if (!callLogsItem.isAnswered()) {
                    //TBD, add CANCEL
                    holder.mCallStatusDuration.setText(callLogsItem.isBusy() ? "Busy" : "Not Answered");

                }
            }

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    callLogsItemList = mCallList;
                } else {
                    List<CallLogsItem> filteredList = new ArrayList<>();
                    for (CallLogsItem row : mCallList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or peer match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    callLogsItemList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = callLogsItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                callLogsItemList = (ArrayList<CallLogsItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public static Bitmap tint(Bitmap bmp, int color) {
        //int color = Color.parseColor(color);
        if(null == bmp)
            return null;

        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color
                & 0xFF;

        float[] matrix = {0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0};

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);

        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);

        //ColorFilter filter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        //paint.setColorFilter(filter);


        //Bitmap resultBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight());
        if (bmp.isMutable()) {

        }

        Bitmap resultBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);

        //image.setImageBitmap(resultBitmap);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, paint);
        return resultBitmap;
    }


}
