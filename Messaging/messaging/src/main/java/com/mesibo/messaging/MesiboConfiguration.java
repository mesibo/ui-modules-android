/******************************************************************************
* By accessing or copying this work, you agree to comply with the following   *
* terms:                                                                      *
*                                                                             *
* Copyright (c) 2019-2024 mesibo                                              *
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
* Documentation: https://docs.mesibo.com/                                     *
*                                                                             *
* Source Code Repository: https://github.com/mesibo/                          *
*******************************************************************************/

package com.mesibo.messaging;

public class MesiboConfiguration {

    public  static final String CREATE_GROUP_NOMEMEBER_TITLE_STRING = "No Members";
    public  static final String CREATE_GROUP_NOMEMEBER_MESSAGE_STRING = "Add two or more members to create a group.";

    public  static final String CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING = "Group name should be at least 2 characters";

    public static final int MAX_GROUP_SUBJECT_LENGTH = 50;
    public static final int  MIN_GROUP_SUBJECT_LENGTH = 2;

    public  static final String CREATE_NEW_GROUP_MESSAGE_STRING = "Add group members from the next screen.";

    public  static final int DEFAULT_GROUP_MODE = 0;

    public static final int STATUS_TIMER = R.drawable.ic_av_timer_black_18dp ;
    public static final int STATUS_SEND = R.drawable.ic_done_black_18dp ;
    public static final int STATUS_NOTIFIED= R.drawable.ic_check_double_black_18dp ;
    public static final int STATUS_READ = R.drawable.ic_check_double_black_18dp;

    public static final int STATUS_ERROR = R.drawable.ic_error_black_18dp;
    public static final int DELETED_DRAWABLE = R.drawable.ic_action_cancel_black_18dp;

    public static final int MISSED_VOICECALL_DRAWABLE = R.drawable.baseline_call_missed_black_18;
    public static final int MISSED_VIDEOCALL_DRAWABLE = R.drawable.baseline_missed_video_call_black_18;
    public static final int MISSED_CALL__TINT_COLOR = 0xCC0000;

    public static final int NORMAL_TINT_COLOR = 0xAAAAAA;
    public static final int DELIVERED_TINT_COLOR = 0x777777;
    public static final int READ_TINT_COLOR = 0x60e8f7; //0x23b1ef;
    public static final int ERROR_TINT_COLOR = 0xCC0000;
    public static final int DELETED_TINT_COLOR = 0xBBBBBB;

    public static final int headerCellBackgroundColor = 0xfff5ecc4;
    //public static final int otherCellsBackgroundColor = 0xffc4dff6;
    public static final int otherCellsBackgroundColor = 0xffcce7e8;

    public static final int DEFAULT_PROFILE_PICTURE = R.drawable.default_user_image;
    public static final int DEFAULT_GROUP_PICTURE = R.drawable.default_group_image;

    public static final String FAVORITED_INCOMING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String FAVORITED_OUTGOING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String NORMAL_INCOMING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";;
    public static final String NORMAL_OUTGOING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";

    public static final int TOPIC_TEXT_COLOR_WITH_PICTURE = 0xff666666;
    public static final int TOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0xff000000;
    public static final int DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0x77000000;

    public static final String STATUS_COLOR_WITHOUT_PICTURE = "#a6abad";
    public static final String STATUS_COLOR_OVER_PICTURE = "#ffffffff";

    public static final int PROGRESSVIEW_DOWNLOAD_SYMBOL =  R.drawable.ic_file_download_white_24dp;
    public static final int PROGRESSVIEW_UPLOAD_SYMBOL = R.drawable.ic_file_upload_white_24dp;


    public static final String MESSAGE_STRING_USERLIST_SEARCH = "Messages";
    public static final String USERS_STRING_USERLIST_SEARCH = "Users";
    //public static final String CREATE_NEW_GROUP_STRING = "Create New Group";

    public static final String ATTACHMENT_STRING = "Attachment";
    public static final String LOCATION_STRING = "Location";
    public static final String VIDEO_STRING = "Video";
    public static final String AUDIO_STRING = "Audio";
    public static final String IMAGE_STRING = "Picture";

    //http://romannurik.github.io/AndroidAssetStudio/icons-generic.html
    //#7f7f7f, 1dp
    public static final int ATTACHMENT_ICON = R.drawable.ic_attachment_grey_18dp ;
    public static final int VIDEO_ICON = R.drawable.ic_video_on_grey_18dp ;
    public static final int IMAGE_ICON = R.drawable.ic_insert_photo_grey_500_18dp ;
    public static final int LOCATION_ICON = R.drawable.ic_location_on_grey_18dp ;

    public static final int LETTER_TITLAR_SIZE = 60 ;

    public static final int MESIBO_INTITIAL_READ_USERLIST = 100 ;
    public static final int MESIBO_SUBSEQUENT_READ_USERLIST = 100 ;
    public static final int MESIBO_SEARCH_READ_USERLIST = 100 ;


    public static final int MESIBO_INTITIAL_READ_MESSAGEVIEW = 30 ;
    public static final int MESIBO_SUBSEQUENT_READ_MESSAGEVIEW = 50 ;

    public static final int EXPIRY_PARAMS_MESSAGEVIEW = 24*30*3600 ;

    public static final int KEYBOARD_ICON = R.drawable.ic_action_keyboard;
    public static final int EMOJI_ICON = R.drawable.input_emoji;
    public static final int DEFAULT_LOCATION_IMAGE = R.drawable.bmap;
    public static final int DEFAULT_FILE_IMAGE = R.drawable.file_file;


    public static final String YOU_STRING_IN_REPLYVIEW = "You";

    public static final String COPY_STRING = "Copy";
    public static final String GOOGLE_PLAYSERVICE_STRING = "Please Download Google play service from Google Play store.";

    public static final String TITLE_PERMISON_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_FAIL = "One or more required permission was denied by you! Change the permission from settings and try again";


    public static final String TITLE_PERMISON_LOCATION_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_LOCATION_FAIL = "Location permission was denied by you! Change the permission from settings menu location service";

    public static final String TITLE_PERMISON_CAMERA_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_CAMERA_FAIL = "Camera permission was denied by you! Change the permission from settings menu";


    public static final String TITLE_INVALID_GROUP = "Invalid group";
    public static final String MSG_INVALID_GROUP = "You are not a member of this group or not allowed to send message to this group";


    public static final String FILE_NOT_AVAILABLE_TITLE = "File not available";
    public static final String FILE_NOT_AVAILABLE_MSG = "Sorry, this File is no longer available on the server.";

}
