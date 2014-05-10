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

public class MkdirTask {

    private static final String TAG = "MkdirTask";

    private ProgressDialog dialog;
    private Activity activity;

    public MkdirTask(Activity activity) {
        this.activity = activity;
    }

    public void showProgressDialog(String msg) {
        dialog = new ProgressDialog(activity);
        dialog.setMessage(msg);
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

        confirm.show().show();
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

        confirm.show().show();
    }

    public void start(RequestBase req) {
        new Mkdir().execute(req);
    }

    private class Mkdir extends AsyncTask<RequestBase, Void, ResultBase> {

        protected void onPreExecute() {
            showProgressDialog("create directory");
        }

        protected ResultBase doInBackground(RequestBase... params) {
            Log.v(TAG, "====mkdir task doInBackground====");
            if (params == null || params.length != 1) {
                return null;
            }

            final RequestBase req = (RequestBase) params[0];
            final String remotepath = req.getRemotePath();
            final KuaipanAPI api = req.getApi();

            ResultBase result = new ResultBase();

            result.setRemotePath(remotepath);
            Log.v(TAG, "remotepath:["+remotepath+"], you want to create");
            try {
                api.mkdirs(remotepath);
            } catch (KscRuntimeException e) {
                e.printStackTrace();
                result.setErrorMsg(e.toString());
                Log.v(TAG, "KscRuntimeException:"+e.toString());
            } catch (KscException e) {
                e.printStackTrace();
                result.setErrorMsg(e.toString());
                Log.v(TAG, "KscException:"+e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "====mkdir task doInBackground end====");
            return result;

        }

        protected void onPostExecute(ResultBase result) {

            hideProgressDialog();
            if (result == null) {
                Toast.makeText(activity,
                        "mkdir failed",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (result.getErrorMsg() != null) {
                displayErrorAlert("Mkdir",
                        result.getErrorMsg());
            } else {
                displayAlert("Mkdir Task Finish",
                        "Mkdir file [" + result.getRemotePath() + "] Successed!");
            }
        }
    }
}
