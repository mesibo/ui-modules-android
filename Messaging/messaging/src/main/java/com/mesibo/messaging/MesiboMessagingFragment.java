/* By obtaining and/or copying this work, you agree that you have read,
 * understood and will comply with the following terms and conditions.
 *
 * Copyright (c) 2020 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the terms and condition mentioned
 * on https://mesibo.com as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions, the following disclaimer and links to documentation and
 * source code repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/ui-modules-android

 */
package com.mesibo.messaging;

import android.graphics.Bitmap;

import com.mesibo.api.Mesibo;

public class MesiboMessagingFragment extends MessagingFragment {

    public static final int ERROR_PERMISSION=1;
    public static final int ERROR_INVALIDGROUP=2;
    public static final int ERROR_FILE=3;

    public static final String MESSAGE_ID = MesiboUI.MESSAGE_ID;
    public static final String READONLY = "readonly";
    public static final String MESSAGE = "message";
    public static final String CREATE_PROFILE = "createprofile";
    public static final String HIDE_REPLY = "hidereply";
    public static final String SHOWMISSEDCALLS = "missedcalls";


    public interface FragmentListener {
        void Mesibo_onUpdateUserPicture(Mesibo.UserProfile profile, Bitmap thumbnail, String picturePath);
        void Mesibo_onUpdateUserOnlineStatus(Mesibo.UserProfile profile, String status);
        void Mesibo_onShowInContextUserInterface();
        void Mesibo_onHideInContextUserInterface();
        void Mesibo_onContextUserInterfaceCount(int count);
        void Mesibo_onError(int type, String title, String message);

        //void Mesibo_onMessageFilter(); //TBD, to be called when group or peer not defined

        //void Mesibo_onPermission();

        //void Mesibo_onItemClicked();
        //void Mesibo_onItemLongClicked();

    }

    public void sendTextMessage(String newText) {
        super.sendTextMessage(newText);
    }
}

