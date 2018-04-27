package com.nuzharukiya.authlauncher.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.nuzharukiya.authlauncher.R;

/**
 * Created by Nuzha Rukiya on 18/01/10.
 */

public class BaseUtils {

    private ProgressDialog progressDialog;

    public static boolean isOnline(Context _context) {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Creates a short toast given a string resource
     *
     * @param context
     * @param message
     */
    public static void makeToast(Context context, int message) {
        makeToast(context, context.getString(message));
    }

    /**
     * Creates a short toast given a string
     *
     * @param context
     * @param message
     */
    public static void makeToast(final Context context, final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void getLoader(Context context) {
        progressDialog = new ProgressDialog(context, R.style.AppDialogTheme);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.show();
    }

    public void dismissLoader() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
