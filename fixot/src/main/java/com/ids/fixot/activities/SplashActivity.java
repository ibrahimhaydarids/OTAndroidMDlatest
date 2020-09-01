package com.ids.fixot.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.classes.SqliteDb_TimeSales;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.Lookups;
import com.ids.fixot.model.WebItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by user on 2/20/2017.
 */

public class SplashActivity extends AppCompatActivity {

    boolean firstRun = true;
    LinearLayout llLanguage;
    TextView tvVersionNumber;
    Button btArabic, btEnglish;
    AddDevice addDevice;
    GetParameters getParameters;
    GetSiteMap getSiteMap;
    GetBrokerageFrees getBrokerageFrees;
    GetInstruments getInstruments;
    GetUnits getUnits;
    GetApplications getApplist;
    GetMobileReportPageSetup getReportList;
    ImageView iv_logo;

    GetLookups getAlertTypes;
    GetLookups getAlertOperator;

    CheckIfWebserviceAvailable checkIfWebserviceAvailable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getApplicationContext(),"asdsad222",Toast.LENGTH_LONG).show();

     //  Actions.trustEveryone();
        MyApplication.isAutoLogin=false;

        try{
        if(MyApplication.mshared.getBoolean("newToMerge", true)) {

            SqliteDb_TimeSales timeSales_DB = new SqliteDb_TimeSales(this);
            timeSales_DB.open();
            timeSales_DB.deleteTimeSales();
            timeSales_DB.close();

            MyApplication.editor.putBoolean("saveusernamepassword", false).apply();
            MyApplication.editor.putString("etUsername", "").apply();
            MyApplication.editor.putString("etPassword", "").apply();
            MyApplication.editor.putBoolean("firstLogin", true).apply();
            MyApplication.editor.putInt("lang", 0).apply();
            MyApplication.timeSales=new ArrayList<>();
            MyApplication.IsTimeSaleLoginRetreived=false;
            MyApplication.timeSalesTimesTamp = "0";
            MyApplication.timeSalesTimesTampMap.put("2","0");
            MyApplication.timeSalesTimesTampMap.put("3","0");
            MyApplication.timeSalesTimesTampMap.put("1","0");
            MyApplication.threadPoolExecutor = null;
            this.deleteDatabase("LIBRA_DB");
            MyApplication.threadPoolExecutor = new ThreadPoolExecutor(MyApplication.corePoolSize, MyApplication.maximumPoolSize,
                    MyApplication.keepAliveTime, TimeUnit.SECONDS, MyApplication.workQueue);

            Actions.deleteCache(this);
            MyApplication.editor.putBoolean("newToMerge", false).apply();


        }}catch (Exception e){}


        try {

            Actions.setActivityTheme(this);
        } catch (Exception e) {
            e.getMessage();
        }

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_splash);
//        Log.wtf("normal theme","" + MyApplication.mshared.getBoolean(getResources().getString(R.string.normal_theme), true));

        try {
            firstRun = MyApplication.mshared.getInt("lang", 0) == 0;
        } catch (Exception e) {
            e.printStackTrace();
            firstRun = false;
        }


        MyApplication.instruments.clear();
        MyApplication.instrumentsHashmap.clear();

        findViews();

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        } else {

            checkIfWebserviceAvailable = new CheckIfWebserviceAvailable();
            checkIfWebserviceAvailable.executeOnExecutor(MyApplication.threadPoolExecutor);

        }

        try {
          Actions.overrideFonts(this, llLanguage, false);
        } catch (Exception e) {
            e.getMessage();
        }

      //  MyApplication.showOTC = (BuildConfig.BrokerId.equals("150") || BuildConfig.BrokerId.equals("140"));
       // MyApplication.isOTC = (BuildConfig.BrokerId.equals("150") || BuildConfig.BrokerId.equals("140"));
      //  MyApplication.marketID= (MyApplication.isOTC ? Integer.toString(enums.MarketType.KWOTC.getValue()) : Integer.toString(enums.MarketType.XKUW.getValue()));
        if(BuildConfig.Enable_Markets)
            MyApplication.marketID=Integer.toString(enums.MarketType.XKUW.getValue());
        else
            MyApplication.marketID=Integer.toString(enums.MarketType.DSMD.getValue());

        MyApplication.setWebserviceItem();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*try {
            getBrokerageFrees.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exception", "brokerage");
        }

        try {
            getParameters.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exception", "parameters");
        }

        try {
            getSiteMap.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exception", "sitemap");
        }*/

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void findViews() {

        llLanguage = findViewById(R.id.llLanguage);
        tvVersionNumber = findViewById(R.id.tvVersionNumber);
        btArabic = findViewById(R.id.btArabic);
        btEnglish = findViewById(R.id.btEnglish);
        iv_logo = findViewById(R.id.iv_logo);

        iv_logo.setImageResource(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.drawable.logo_name : R.drawable.logo_name_white);

        String versionNumber = this.getResources().getString(R.string.current_version) + " " + Actions.GetVersionCode(SplashActivity.this);

        tvVersionNumber.setText(versionNumber);
        btArabic.setOnClickListener(v -> {

            MyApplication.lang = MyApplication.ARABIC;
            MyApplication.editor.putInt("lang", MyApplication.lang).apply();
            LocalUtils.setLocale(new Locale("ar"));
            LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

            //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            startActivity(new Intent(SplashActivity.this, LoginFingerPrintActivity.class));
            finish();
        });

        btEnglish.setOnClickListener(v -> {

            MyApplication.lang = MyApplication.ENGLISH;
            MyApplication.editor.putInt("lang", MyApplication.lang).apply();
            LocalUtils.setLocale(new Locale("en"));
            LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

            //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            startActivity(new Intent(SplashActivity.this, LoginFingerPrintActivity.class));
            finish();
        });
    }

    private void sendBroadcastUpdatedList(ArrayList<WebItem> webItems) {

        Intent intent = new Intent("WebItemsService");
        intent.putParcelableArrayListExtra("webItems", webItems);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //<editor-fold desc="check for update dialogs">
    public void onCreateDialogForPlayStore(final Activity activity) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        TextView textView;
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View textEntryView = inflater.inflate(R.layout.item_dialog, null);
        textView = textEntryView.findViewById(R.id.dialogMsg);
        textView.setGravity(Gravity.CENTER);
        textView.setText(activity.getResources().getString(R.string.update_message));
        builder.setTitle(activity.getResources().getString(R.string.update_title));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(textEntryView)

                // Add action buttons
                .setNegativeButton(activity.getResources().getString(R.string.update_button), (dialog, id) -> {
                    dialog.dismiss();


                    final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                    try {

                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        activity.finish();

                    } catch (android.content.ActivityNotFoundException anfe) {

                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        activity.finish();

                    }
                });
        final AlertDialog d = builder.create();
        d.setOnShowListener(arg0 -> {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SplashActivity.this, R.color.colorDark));
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        });
        d.setCancelable(false);
        d.show();
    }

    public void onCreateDialogForPlayStoreNoForce(final Activity activity) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        TextView textView;
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View textEntryView = inflater.inflate(R.layout.item_dialog,
                null);
        textView = textEntryView.findViewById(R.id.dialogMsg);
        textView.setGravity(Gravity.CENTER);
        textView.setText(activity.getResources().getString(R.string.update_message));
        builder.setTitle(activity.getResources().getString(R.string.update_title));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(textEntryView)

                // Add action buttons
                .setPositiveButton(activity.getResources().getString(R.string.update_button), (dialog, id) -> {
                    dialog.dismiss();
                    final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        activity.finish();

                    } catch (android.content.ActivityNotFoundException anfe) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        activity.finish();

                    }
                })

                .setNegativeButton(activity.getResources().getString(R.string.update_cancel), (dialog, id) -> {
                    dialog.dismiss();
                    //get sitemap data

                    if (firstRun) {//first run and no language selected

                        llLanguage.setVisibility(View.VISIBLE);
                        llLanguage.setAlpha(0.0f);
                        llLanguage.animate()
                                .setDuration(1000)
                                .translationY(llLanguage.getHeight())
                                .alpha(1.0f)
                                .setListener(null);
                    } else {

                        //Actions.startActivity(SplashActivity.this, LoginActivity.class, true);
                        Actions.startActivity(SplashActivity.this, LoginFingerPrintActivity.class, true);
                    }
                });

        final AlertDialog d = builder.create();
        d.setOnShowListener(arg0 -> {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SplashActivity.this, R.color.colorDark));
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);

            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SplashActivity.this, R.color.colorDark));
            d.getButton(AlertDialog.BUTTON_POSITIVE).setTransformationMethod(null);
            d.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        });
        d.setCancelable(false);

        //Actions.setLocalSplash(MyApplication.lang, activity);
        d.show();

    }

/*
    private class AddDevice extends AsyncTask<Void, Void, Void> {
      String token="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (FirebaseInstanceId.getInstance().getToken() == null) {
                token = "";
            } else {

                token = FirebaseInstanceId.getInstance().getToken();
            }
            Log.wtf("token",token);
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = MyApplication.link + MyApplication.AddDevice2.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("DeviceID").value(0)
                        .key("DeviceType").value(1)
                        .key("EnableNotifications").value(true)
                        .key("IMEI").value(Actions.GetUniqueID(SplashActivity.this))
                        .key("Model").value(Actions.getDeviceName())
                        .key("Token").value(token)
                        .key("UserID").value(0)
                        .key("MarketId").value(MyApplication.marketID)
                        .endObject();

            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddDevice2.getKey(), Toast.LENGTH_LONG).show();
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

       */
/*     getSiteMap = new GetSiteMap();
            getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

            getParameters = new GetParameters();
            getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

            getBrokerageFrees = new GetBrokerageFrees();
            getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

            getInstruments = new GetInstruments();
            getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

            getUnits = new GetUnits();
            getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);

            getApplist = new GetApplications();
            getApplist.executeOnExecutor(MyApplication.threadPoolExecutor);


            getAlertTypes = new GetLookups();
            getAlertTypes.executeOnExecutor(MyApplication.threadPoolExecutor,enums.LookupTypes.TYPES.getValue()+"");

            getAlertOperator = new GetLookups();
            getAlertOperator.executeOnExecutor(MyApplication.threadPoolExecutor,enums.LookupTypes.OPERATORS.getValue()+"");
*//*

        }
    }
*/








    private class AddDevice extends AsyncTask<Void, Void, Void> {
        String token="";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String android_id;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                        .key("IMEI").value(Actions.GetUniqueID(SplashActivity.this))
                        .key("Model").value(Actions.getDeviceName())
                        .key("DeviceToken").value(token)
                        .key("UserID").value(MyApplication.currentUser.getId())

                        .key("OsVersion").value(Actions.getAndroidVersion())
                        .key("RegistrationDate").value(dateFormat.format(cal.getTime()))
                        //.key("VersionNumber").value(Actions.getVersionNumber(SplashActivity.this) + "")
                        .key("VersionNumber").value(Actions.getVersionName(SplashActivity.this))
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
        }
    }







    private class CheckIfWebserviceAvailable extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = " WebService is Unavailable";
            String url = MyApplication.link + MyApplication.GetParameters.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("deviceType", "1");
            parameters.put("key", getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                Log.wtf("CheckIfWebserviceAvailable ", " success result = " + result);
                MyApplication.parameter = GlobalFunctions.GetParameters(result);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (MyApplication.isDebug) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + 00, Toast.LENGTH_LONG).show();
                        }
                    /*    GetAlternativeWebserviceUrl getAlternativeWebserviceUrl = new GetAlternativeWebserviceUrl();
                        getAlternativeWebserviceUrl.executeOnExecutor(MyApplication.threadPoolExecutor);*/
                    }
                });
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Log.wtf("CheckIfWebserviceAvailable", "result = " + result);

/*                if (firstRun) { //first run and no language selected

                    addDevice = new AddDevice();
                    addDevice.execute();

                } else {*/

                    addDevice = new AddDevice();
                    addDevice.execute();

                    getSiteMap = new GetSiteMap();
                    getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getParameters = new GetParameters();
                    getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getBrokerageFrees = new GetBrokerageFrees();
                    getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getInstruments = new GetInstruments();
                    getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getUnits = new GetUnits();
                    getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getApplist = new GetApplications();
                    getApplist.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getReportList = new GetMobileReportPageSetup();
                    getReportList.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getAlertTypes = new GetLookups();
                    getAlertTypes.executeOnExecutor(MyApplication.threadPoolExecutor, enums.LookupTypes.TYPES.getValue() + "");

                    getAlertOperator = new GetLookups();
                    getAlertOperator.executeOnExecutor(MyApplication.threadPoolExecutor, enums.LookupTypes.OPERATORS.getValue() + "");

                //     }

            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("exception", e.getMessage());
            }
        }
    }

    private class GetAlternativeWebserviceUrl extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.AlternativeWebserviceLink;
            HashMap<String, String> parameters = new HashMap<String, String>();

            try {
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                Log.wtf("GetAlternativeWebserviceUrl ", "result = " + result);
                String altUrl = GlobalFunctions.GetWebserviceUrl(result);
                Log.wtf("Alternative", "url = " + altUrl);

                MyApplication.link = altUrl + "Services/DataService.svc";
                MyApplication.baseLink = altUrl + "Mobile/";
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + 00, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
     /*           if (firstRun) { //first run and no language selected

                    addDevice = new AddDevice();
                    addDevice.execute();

                } else {*/

                    addDevice = new AddDevice();
                    addDevice.execute();

                    getSiteMap = new GetSiteMap();
                    getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getParameters = new GetParameters();
                    getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getBrokerageFrees = new GetBrokerageFrees();
                    getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getInstruments = new GetInstruments();
                    getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getUnits = new GetUnits();
                    getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getApplist = new GetApplications();
                    getApplist.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getReportList = new GetMobileReportPageSetup();
                    getReportList.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getAlertTypes = new GetLookups();
                    getAlertTypes.executeOnExecutor(MyApplication.threadPoolExecutor,enums.LookupTypes.TYPES.getValue()+"");

                    getAlertOperator = new GetLookups();
                    getAlertOperator.executeOnExecutor(MyApplication.threadPoolExecutor,enums.LookupTypes.OPERATORS.getValue()+"");
             //   }
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("exception", e.getMessage());
            }
        }
    }

    private class GetParameters extends AsyncTask<Void, Void, String> {

        int needsUpdate = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetParameters.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("deviceType", "1");
            parameters.put("key", getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                Log.wtf("GetParameters ", "result = " + result);

                MyApplication.parameter = GlobalFunctions.GetParameters(result);
                MyApplication.defaultPriceType = Integer.parseInt(MyApplication.parameter.getDefaultPriceOnTrade());

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetParameters.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (MyApplication.parameter.getServerVersionNumber().length() > 0) {

                    MyApplication.showMowazi = MyApplication.parameter.isEnableMowazi();

                    MyApplication.mowaziBrokerId = MyApplication.parameter.getMowaziBrokerId();

                   // MyApplication.mowaziUrl = MyApplication.parameter.getMowaziServiceLink();
                      MyApplication.mowaziUrl = "https://www.almowazi.com/AlmowaziDevelopmentService/servicedata.asmx";


                    //Commemted to be change once uploaded 26_10_2018 : MKobaissy

                    needsUpdate = Actions.CheckVersion(SplashActivity.this, MyApplication.parameter.getServerVersionNumber(), MyApplication.parameter.isForceUpdate());

                    if (needsUpdate == 0) {

                        if (firstRun) {//first run and no language selected

                            llLanguage.setVisibility(View.VISIBLE);
                            llLanguage.setAlpha(0.0f);
                            llLanguage.animate()
                                    .setDuration(1000)
                                    .translationY(llLanguage.getHeight())
                                    .alpha(1.0f)
                                    .setListener(null);

                        } else {

                            //Actions.startActivity(SplashActivity.this, LoginActivity.class, true);
                            Actions.startActivity(SplashActivity.this, LoginFingerPrintActivity.class, true);
                        }

                    } else if (needsUpdate == 1) {

                        onCreateDialogForPlayStoreNoForce(SplashActivity.this);
                    } else if (needsUpdate == 2) {

                        onCreateDialogForPlayStore(SplashActivity.this);
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();

                Log.wtf("exception", e.getMessage());

/*                if (firstRun) {//first run and no language selected

                    addDevice = new AddDevice();
                    addDevice.execute();

                } else {*/

                    addDevice = new AddDevice();
                    addDevice.execute();

                    getSiteMap = new GetSiteMap();
                    getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getParameters = new GetParameters();
                    getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getBrokerageFrees = new GetBrokerageFrees();
                    getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getInstruments = new GetInstruments();
                    getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getUnits = new GetUnits();
                    getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getApplist = new GetApplications();
                    getApplist.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getReportList = new GetMobileReportPageSetup();
                    getReportList.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getAlertTypes = new GetLookups();
                    getAlertTypes.executeOnExecutor(MyApplication.threadPoolExecutor,enums.LookupTypes.TYPES.getValue()+"");


                //  }
            }

        }
    }

    private class GetSiteMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.webItems.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetSiteMapData.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("Language", MyApplication.lang == MyApplication.ENGLISH ? "english" : "arabic");
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", MyApplication.marketID);
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);

                Log.wtf("GetSiteMapData ", "result = " + result);
                MyApplication.webItems = new ArrayList<>();
                MyApplication.webItems.addAll(GlobalFunctions.GetSiteMapData(result));
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSiteMapData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.wtf("webItems size", MyApplication.webItems.size() + "");
            sendBroadcastUpdatedList(MyApplication.webItems);
        }
    }

    public class GetBrokerageFrees extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.allBrokerageFees.clear();
        }

        @Override
        protected Void doInBackground(String... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetBrokerageFees.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", MyApplication.marketID);
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                //Log.wtf("GetBrokerageFees ","result = " + result);
                MyApplication.allBrokerageFees = new ArrayList<>();
                MyApplication.allBrokerageFees.addAll(GlobalFunctions.GetBrokerageFeeList(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetBrokerageFees.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.wtf("brokerage size", "" + MyApplication.allBrokerageFees.size());
        }

    }

    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... a) {


            int market=2;
            if(BuildConfig.Enable_Markets)
                market=2;
            else
                market=1;
            String result = "";
            String url = MyApplication.link + MyApplication.GetInstruments.getValue(); // this method uses key after login
            Log.wtf("market_splash",MyApplication.marketID);
            Log.wtf("market_splash_url",url);

            while (market<4) {
                try {

                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("id", "0");
                    parameters.put("key", getResources().getString(R.string.beforekey));
                    parameters.put("MarketId", market+"");/*Integer.toString(enums.MarketType.KWOTC.getValue())*/
                    result = ConnectionRequests.GET(url, SplashActivity.this, parameters);

            /*        if(market==2)
                       MyApplication.instruments = new ArrayList<>();*/
                    MyApplication.instruments.addAll(GlobalFunctions.GetInstrumentsList(result));
                    Log.wtf("Splash", "MyApplication.instruments count = " + MyApplication.instruments.size());

                    for (int i = 0; i < MyApplication.instruments.size(); i++) {
                        if (!MyApplication.availableMarkets.contains(MyApplication.instruments.get(i).getMarketID()))
                            MyApplication.availableMarkets.add(MyApplication.instruments.get(i).getMarketID());
                    }


                    //to be removed
                    //MyApplication.availableMarkets.add(3);

                    Log.wtf("Splash", "MyApplication.availableMarkets count = " + MyApplication.availableMarkets.size());

                   // if (Actions.getLastMarketId(getApplicationContext()) == -1) {
                        if(BuildConfig.Enable_Markets) {
                            if(!MyApplication.availableMarkets.contains(3))
                                MyApplication.availableMarkets.add(3);
                            Actions.setLastMarketId(getApplication(), enums.MarketType.XKUW.getValue());
                            Log.wtf("trade_instrument_splash_size", MyApplication.instruments.size() + "");
                        }else {
                            Actions.setLastMarketId(getApplication(), enums.MarketType.DSMD.getValue());
                            Log.wtf("trade_instrument_splash_size", MyApplication.instruments.size() + "");

                        }
                       // }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                                Log.wtf("GetInstruments", "error : " + e.getMessage());
                            }
                        });
                    }
                }
                market++;

            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class GetUnits extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... a) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetUnits.getValue();// "/GetUnits?"; // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
//                parameters.put("id", "0" );
                parameters.put("key", getResources().getString(R.string.beforekey));
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);

                MyApplication.units = new ArrayList<>();
                MyApplication.units.addAll(GlobalFunctions.GetUnitsList(result));
                Log.wtf("MyApplication.units", "count = " + MyApplication.units.size());

            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("request error " + MyApplication.GetUnits.getValue(), ": " + e.getMessage());
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetUnits.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    //</editor-fold>








    private class GetApplications extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... a) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetApplication.getValue(); // this method uses key after login
             try {
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("key", getResources().getString(R.string.beforekey));
                    result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                    MyApplication.applicationLists = new ArrayList<>();
                    MyApplication.applicationLists.addAll(GlobalFunctions.GetApplicationList(result));
                    Log.wtf("applist",MyApplication.applicationLists.size()+"");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetApplication.getKey(), Toast.LENGTH_LONG).show();
                                Log.wtf("GetInstruments", "error : " + e.getMessage());
                            }
                        });
                    }
                }



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }




    private class GetMobileReportPageSetup extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... a) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetMobileReportPageSetup.getValue(); // this method uses key after login
            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("key", getResources().getString(R.string.beforekey));
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                MyApplication.reportPageList = new ArrayList<>();
                MyApplication.reportPageList.addAll(GlobalFunctions.GetReportPageSetup(result));
                Log.wtf("applist",MyApplication.reportPageList.size()+"");
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetApplication.getKey(), Toast.LENGTH_LONG).show();
                            Log.wtf("GetReportPageSetup", "error : " + e.getMessage());
                        }
                    });
                }
            }



            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    public class GetLookups extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {



            String result = "";
            String url = MyApplication.link + MyApplication.GetLookups.getValue(); // this method uses key after login
            Log.wtf("lookup_param",params[0]);
            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("typeid", params[0]);
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                //Log.wtf("GetBrokerageFees ","result = " + result);
                if(params[0].equals(enums.LookupTypes.TYPES.getValue()+"")) {
                    MyApplication.allAlertTypes = new ArrayList<>();
                    MyApplication.allAlertTypes.add(0,  new Lookups(getString(R.string.alert_type_0_ar),getString(R.string.alert_type_0_en),getString(R.string.alert_type_0_ar),getString(R.string.alert_type_0_en),0,0));
                    MyApplication.allAlertTypes.addAll(GlobalFunctions.GetLookups(result));
                }
                else {
                    MyApplication.allAlertOperators = new ArrayList<>();
                    MyApplication.allAlertOperators.add(0,new Lookups(getString(R.string.operator_type_0_ar),getString(R.string.operator_type_0_en),getString(R.string.operator_type_0_ar),getString(R.string.operator_type_0_en),0,0));
                    MyApplication.allAlertOperators.addAll(GlobalFunctions.GetLookups(result));
                }



            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetBrokerageFees.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.wtf("lookup_types_size", "" + MyApplication.allAlertTypes.size()+"");
            Log.wtf("lookup_operator_size", "" + MyApplication.allAlertOperators.size()+"");
        }

    }

}
