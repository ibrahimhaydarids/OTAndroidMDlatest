package com.ids.fixot.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.NotificationActivity;
import com.ids.fixot.activities.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String refreshedToken = instanceIdResult.getToken();
                Log.wtf(TAG, "Refreshed token: " + refreshedToken);
                sendRegistrationToServer(refreshedToken);
            }
        });
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
     //   new AddDevice().execute(token);
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
                        .key("VersionNumber").value(Actions.getVersionName(getApplicationContext()) + "")
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




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.wtf("RECEIVED", "NOTIFICATION");

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.wtf(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.wtf(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        try{

            String recordID = (String)remoteMessage.getData().get("recordId");
            String typeID = (String)remoteMessage.getData().get("typeId");
            String message = (String)remoteMessage.getData().get("message");
            String notID = (String)remoteMessage.getData().get("Id");

           if(MyApplication.mshared.getBoolean("EnableNotification", true))
               sendNotification(notID, typeID, recordID, message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // [END receive_message]

    private void sendNotification(String notID, String typeID, String recordID, String messageBody) {
        MyApplication.isNotification=true;

        Intent intent = null;
        int type=0;
        try{ type = Integer.parseInt(typeID);}catch (Exception e){}

        Log.wtf("type", "is "+type);

        int recordId = 0;
        try {
            recordId = Integer.parseInt(recordID);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            recordId = 0;
        }

        int notificationId=0;
        try{ notificationId = Integer.parseInt(notID);}catch (Exception e){notificationId=0;}




        if(!MyApplication.isLoggedIn)
            intent = new Intent(this, SplashActivity.class);
        else
            intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MyApplication.UNIQUE_REQUEST_CODE++, intent, PendingIntent.FLAG_ONE_SHOT);

        /*Intent backIntent = new Intent(this, SplashActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, UNIQUE_REQUEST_CODE++, new Intent[] {backIntent, intent}, PendingIntent.FLAG_ONE_SHOT);*/

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "OT_001")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(messageBody))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("OT_001", "OT", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
    }
}