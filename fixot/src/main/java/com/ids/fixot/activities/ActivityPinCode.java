package com.ids.fixot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ActivityPinCode extends AppCompatActivity {

    Button btVerify;
    LinearLayout loading;


    VerifySubscriber mVerifySubscriber;
    RelativeLayout rootLayout;
    PinView pvCode;
    Toolbar myToolbar;
    String myCode="";
    int subscriberId=0;
    AddDevice addDevice;
    public ActivityPinCode() {
        LocalUtils.updateConfig(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        setContentView(R.layout.activity_pin_code);
        Actions.initializeBugsTracking(this);
        findViews();
        Actions.initializeToolBar(getString(R.string.pin_verification), ActivityPinCode.this);
        Actions.overrideFonts(this, rootLayout, false);
        setListeners();
    }


    private void findViews(){
        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);
        loading=findViewById(R.id.loading);
        btVerify=findViewById(R.id.btVerify);
        pvCode = findViewById(R.id.pvCode);

        subscriberId=getIntent().getIntExtra("subscriber_id",0);

    }

    private void setListeners(){
        pvCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 4){ //check code and start profile fragment
                    myCode=s.toString();
                   if(MyApplication.mshared.getInt("deviceId",0)!=0) {
                       mVerifySubscriber = new VerifySubscriber();
                       mVerifySubscriber.executeOnExecutor(MyApplication.threadPoolExecutor);
                   }else {
                       addDevice = new AddDevice();
                       addDevice.execute();
                   }
                  //  CreateDialog(ActivityPinCode.this,"aaa");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void back(View v) {
        View view = this.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
    }



    private class VerifySubscriber extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.VerifySubscriber.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();


            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("SubscriberID").value(subscriberId)
                        .key("DeviceID").value(MyApplication.mshared.getInt("deviceId",0)+"")
                        .key("Code").value(myCode)
                        .key("key").value(getString(R.string.beforekey)+"")


                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.SaveUserNotificationSettings.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }



            try {
                Log.wtf("json_stringer_pin",stringer.toString());
                result = ConnectionRequests.POSTWCF(url, stringer);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            try{ Log.wtf("result_subscription",result);}catch (Exception e){}
            JSONObject object = null;
            try {

                object = new JSONObject(result);
                String message = "";
                message = MyApplication.lang == MyApplication.ARABIC?object.getString("MessageAr"):object.getString("MessageEn");

                String status=object.getString("Status");
                if (status.equals("0")) {
                    message=getString(R.string.subscribe_success);
                    CreateDialog(ActivityPinCode.this,message);
                } else {
                    //CreateDialog(ActivityPinCode.this,message);
                    Actions.CreateDialog(ActivityPinCode.this,message,false,false);

                }



            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.VerifySubscriber.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

        }
    }





    public static void CreateDialog(final Activity c, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    dialog.cancel();
                    Actions.hideKeyboard(c);
                    c.startActivity(new Intent(c.getApplicationContext(),LoginFingerPrintActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    c.finish();


                });

        AlertDialog alert = builder.create();
        alert.show();

    }



    private class AddDevice extends AsyncTask<Void, Void, Void> {

        String token="";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String android_id;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (FirebaseInstanceId.getInstance().getToken() == null) {
                token = "";
            } else {

                token = FirebaseInstanceId.getInstance().getToken();
            }
            Log.wtf("token_login",token);
        }

        @Override
        protected Void doInBackground(Void... params) {


            String url = MyApplication.link + MyApplication.AddDevice2.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ID").value(0)
                        .key("DeviceTypeId").value(2)
                        .key("EnableNotifications").value(1)
                        .key("IMEI").value(Actions.GetUniqueID(ActivityPinCode.this))
                        .key("Model").value(Actions.getDeviceName())
                        .key("DeviceToken").value(token)
                        .key("UserID").value(MyApplication.currentUser.getId())

                        .key("OsVersion").value(Actions.getAndroidVersion())
                        .key("RegistrationDate").value(dateFormat.format(cal.getTime()))
                        .key("VersionNumber").value(Actions.getVersionName(ActivityPinCode.this) + "")
                        .key("Key").value(getString(R.string.beforekey))
                        // .key("MarketId").value(MyApplication.marketID)
                        .endObject();
                Log.wtf("add_device",stringer.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_code) + MyApplication.AddDevice2.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            String result = ConnectionRequests.POSTWCF(url, stringer);

            Log.wtf("add_device_result",result);
            try {
                JSONObject object = new JSONObject(result);
                String msgdata = object.getString("ResponseMessage");
                JSONArray jarray = object.getJSONArray("DeviceList");
                JSONObject json_data = jarray.getJSONObject(0);
                MyApplication.editor.putInt("deviceId", json_data.getInt("ID")).apply();
                Log.wtf("device_id",json_data.getInt("ID")+"");


            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //after add
            mVerifySubscriber = new VerifySubscriber();
            mVerifySubscriber.executeOnExecutor(MyApplication.threadPoolExecutor);
           // Toast.makeText(getApplicationContext(),MyApplication.mshared.getInt("deviceId",0)+"",Toast.LENGTH_LONG).show();
        }
    }



}
