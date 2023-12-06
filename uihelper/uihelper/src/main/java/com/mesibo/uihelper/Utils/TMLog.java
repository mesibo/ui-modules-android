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

package com.mesibo.uihelper.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.text.format.DateFormat;
import android.util.Log;

import com.mesibo.uihelper.BuildConfig;

public class TMLog {
	//private static String TAG = "TringMe.TMDebug";
	private static String TAG = "TringMe";
	//private static boolean logMessage = true;  // default, false
	//private static boolean logInFile = false;	// default, false
	private static boolean enableTimeStampe = false;
	private static String mLogFileWithPath = null;
    private static FileOutputStream mFileStream = null;

	private static final int logLevelVerbose = 0;
	private static final int logLevelInfo = logLevelVerbose + 1;
	private static final int logLevelWarn = logLevelInfo + 1;
	private static final int logLevelError = logLevelWarn + 1;
	private static final int logLevelDebug = logLevelError + 1;

	//pass null to disable
	public static void enableFileLogs(String logFileWithPath) {
		if (!BuildConfig.DEBUG) {
			return;
		}

		mLogFileWithPath = logFileWithPath;

        File file = new File(logFileWithPath);

        //OutputStream out = null;
        try {
            //mFileStream = new BufferedOutputStream(new FileOutputStream(file, true));
            mFileStream = new FileOutputStream(file, true);
        } catch (Exception e) {
            Log.d(TAG, "Exception creating file: " + e);
        }

		printFileLog(TAG, "========================");
		printFileLog(TAG, "==== App Starting ("+getCurTime()+")======");
		printFileLog(TAG, "========================");
	}

	//public static void log (String s) {
	//	printLog(TAG, s, logLevelVerbose);
	//}

	public static void v(String tag, String s) {
		printLog(TAG + "-" + tag, s, logLevelVerbose);
	}

	public static void i(String tag, String s) {
		printLog(TAG + "-" + tag, s, logLevelInfo);
	}

	public static void w(String tag, String s) {
		printLog(TAG + "-" + tag, s, logLevelWarn);
	}

	public static void e(String tag, String s) {
		printLog(TAG + "-" + tag, s, logLevelError);
	}

	public static void d(String tag, String s) {
		printLog(TAG + "-" + tag, s, logLevelDebug);
	}

	private static void printLog (String tag, String data, int level) {
		if (!BuildConfig.DEBUG) {
			return;
		}

		//This is useful if we want to hide logs from a particular module
		if(tag.startsWith("NOLOGS"))
			return;
		//if(false == logMessage)
		//	return;

		//String TAG = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(enableTimeStampe)
			data = "(" + Long.toString(System.currentTimeMillis()-1420000000000L) + ") " + data;

		switch(level) {
			case logLevelVerbose: Log.v(tag, data);
				break;
			case logLevelInfo: Log.i(tag, data);
				break;
			case logLevelWarn: Log.w(tag, data);
				break;
			case logLevelError: Log.e(tag, data);
				break;
			case logLevelDebug: Log.d(tag, data);
				break;
		}

		printFileLog(tag, data);
	}

	private static String getCurTime() {
		return DateFormat.format("ddMM-HH:mm:ss", System.currentTimeMillis()).toString();
	}

    private static void printFileLog(String tag, String info) {
        if(null == mLogFileWithPath)
            return;

        try {
            info = getCurTime() + ":"+tag +":"+info+"\n";
            mFileStream.write(info.getBytes());
        } catch (Exception e) {

        }
    }

	private static void printFileLog_OLD(String info) {
		if(null == mLogFileWithPath)
			return;

		File file = new File(mLogFileWithPath);
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file, true));
			info = getCurTime() + ":"+info+"\n";
			out.write(info.getBytes());
		} catch (Exception e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void dumpStackTrace() {
		if(null == mLogFileWithPath)
			return;

		/*printFileLog("==Dumping stack trace for debugging==");

		File TringMeFolder = MediaStorageManager.getTringMeFolder();
		File file = new File(TringMeFolder, "tringmelog.txt");

		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(file, true));
			Throwable t = new Throwable();
			t.printStackTrace(out);
		} catch (Exception e) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}*/
	}
		
}
