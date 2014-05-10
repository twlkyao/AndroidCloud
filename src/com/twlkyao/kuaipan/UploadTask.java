/*
 * Copyright 2010-2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.twlkyao.kuaipan;

import java.io.File;

import com.twlkyao.androidcloud.R;

import cn.kuaipan.android.openapi.KuaipanAPI;
import cn.kuaipan.android.sdk.exception.KscException;
import cn.kuaipan.android.sdk.exception.KscRuntimeException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class UploadTask {

	private static final String TAG = "UploadTask";
	
	private ProgressDialog dialog;
	private Activity activity;

	public UploadTask(Activity activity) {
		this.activity = activity;
	}

	public void showProgressDialog() {
		dialog = new ProgressDialog(activity);
		dialog.setMessage(activity.getString(R.string.uploading));
		dialog.setCancelable(false);
		dialog.show();
	}

	public void hideProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	protected void displayAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(activity);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton(activity.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		confirm.show();
//		confirm.show().show();
	}

	protected void displayErrorAlert(String title, String message) {

		AlertDialog.Builder confirm = new AlertDialog.Builder(activity);
		confirm.setTitle(title);
		confirm.setMessage(message);

		confirm.setNegativeButton(activity.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						activity.finish();
					}
				});

		confirm.show();
//		confirm.show().show();
	}
	
	public void start (RequestBase req) {
		new Upload ().execute(req);
	}
	
	
	private class Upload extends AsyncTask<RequestBase, Void, ResultBase> {

		protected void onPreExecute() {
			Log.v(TAG, "====Upload task onPreExecute====");
			showProgressDialog();
		}

		protected ResultBase doInBackground(RequestBase... params) {
			Log.v(TAG, "====Upload task doInBackground====");
			if (params == null || params.length != 1) {
				return null;
			}

			final RequestBase req = (RequestBase) params[0]; // Get the params passed.
			final KuaipanAPI api = req.getApi(); // Get the KuaipanAPI.
			final String filePath = req.getFilePath(); // Get the path of file to upload.
			final String remotePath = req.getRemotePath(); // Get the path of the file on the remote server.
			
			ResultBase result = new ResultBase(); // To record the final result.
			
			File file = new java.io.File(filePath);
			if (!file.exists()) {
				return null;
			}
		
			result.setFilePath(filePath);
			Log.v(TAG , "ready to upload file " + file.getAbsolutePath() + " name:"+file.getName());
			try {
				api.upload(file, remotePath + "/" + file.getName(),
						new TransportListener(TransportListener.OPERATION_UPLOAD, "Upload"));
			} catch (KscRuntimeException e) {
				e.printStackTrace();
			} catch (KscException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}				
					Log.v(TAG, "====Upload task doInBackground end====");
					return result;
			}

		protected void onPostExecute(ResultBase result) {

			hideProgressDialog ();
			if (result == null) {
				Toast.makeText(activity,
						R.string.empty_file_path,
						Toast.LENGTH_SHORT).show();
				return;
			}
			
			if (result.getErrorMsg() != null) {
				displayErrorAlert(
						activity.getString(R.string.upload_failure_title),
						result.getErrorMsg());
			} else { // Upload success, there needs to be a function to refresh the ListView.
				displayAlert(activity.getString(R.string.upload_success),
						"[" + result.getFilePath()
						+ "]" + activity.getString(R.string.upload_success));
			}
		}
	}
}
