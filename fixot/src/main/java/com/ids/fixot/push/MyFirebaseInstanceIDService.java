package com.ids.fixot.push;

import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user on 11/1/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
      //  new AddDevice().execute(token);
    }




    private class AddDevice extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String url = MyApplication.link + MyApplication.AddDevice2.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("DeviceID").value(0)
                        .key("DeviceType").value(1)
                        .key("EnableNotifications").value(true)
                        .key("IMEI").value(Actions.GetUniqueID(getApplication()))
                        .key("Model").value(Actions.getDeviceName())
                        .key("Token").value(params[0])
                        .key("UserID").value(0)
                        .key("MarketId").value(MyApplication.marketID)
                        .key("VersionNumber").value(Actions.getVersionName(getApplicationContext()))
                        .endObject();

            } catch (JSONException e) {
                e.printStackTrace();

            }
            String result = ConnectionRequests.POSTWCF(url, stringer);

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);


        }
    }












}