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

package com.mesibo.messaging;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;

public class MesiboRecycleViewHolder extends RecyclerView.ViewHolder {

    public static final int TYPE_NONE = 0;

    public void reset() {
    }

    private int mType = TYPE_NONE;
    private int mPosition = -1;
    private boolean mCustom = false;
    private MessageAdapter mAdapter = null;

    public MesiboRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected int getItemPosition() {
        return mPosition;
    }

    protected void setItemPosition(int pos) {
        mPosition = pos;
    }

    public int getType() {
        return mType;
    }

    public void refresh() {
        if(null != mAdapter && mPosition > 0)
            mAdapter.notifyItemChanged(mPosition);
    }

    protected void setAdapter(MessageAdapter adapter) {
        mAdapter = adapter;
    }

    protected void setType(int type) {
        mType = type;
    }

    protected void setCustom(boolean custom) {
        mCustom = custom;
    }

    protected boolean getCustom() {
        return mCustom;
    }

    public void cleanup() {

    }

    //virtual
    public void setRow(MesiboUI.MesiboRow row) {

    }

}
