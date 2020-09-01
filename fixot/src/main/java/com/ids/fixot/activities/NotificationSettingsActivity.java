package com.ids.fixot.activities;


import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Created by Amal on 4/6/2017.
 */

public class NotificationSettingsActivity extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rlLayout,layoutPushNotification,layoutSell,layoutBuy,layoutCashEvents,layoutStockEvents;
    Switch switchNot,switchBuy,switchCashEvents,switchSell,switchStockEvents;
    private BroadcastReceiver receiver;
    GetUserNotificationSettings mGetUserNotification;
    SaveUserNotificationSettings mSaveSettings;
    Spinner spInstrumentsTop;
    public NotificationSettingsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void refreshMarketTime(String status, String time, Integer color) {

        final TextView marketstatustxt = findViewById(R.id.market_state_value_textview);
        final LinearLayout llmarketstatus = findViewById(R.id.ll_market_state);
        final TextView markettime = findViewById(R.id.market_time_value_textview);

        marketstatustxt.setText(status);
        markettime.setText(time);
        llmarketstatus.setBackground(ContextCompat.getDrawable(this, color));
     }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_notification_settings);
        Actions.initializeBugsTracking(this);

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.settings), NotificationSettingsActivity.this);
        Actions.overrideFonts(this, rlLayout, false);
        Actions.showHideFooter(this);

      if(MyApplication.mshared.getBoolean("enable_all_notifation",true))
          switchNot.setChecked(true);
      else {
          switchNot.setChecked(false);
          disableAll();
      }

        MyApplication.editor.putInt("lang", MyApplication.lang).apply();

        mGetUserNotification = new GetUserNotificationSettings();
        mGetUserNotification.executeOnExecutor(MyApplication.threadPoolExecutor);

        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
            spInstrumentsTop.setVisibility(View.GONE);
            //  Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }
    }

    private void findViews() {
        rlLayout = findViewById(R.id.rlLayout);

        layoutPushNotification = findViewById(R.id.layoutPushNotification);
        switchNot = findViewById(R.id.switchNot);

        layoutBuy = findViewById(R.id.layoutBuy);
        switchBuy = findViewById(R.id.switchBuy);

        layoutSell = findViewById(R.id.layoutSell);
        switchSell = findViewById(R.id.switchSell);

        layoutCashEvents = findViewById(R.id.layoutCashEvents);
        switchCashEvents = findViewById(R.id.switchCashEvents);

        layoutStockEvents = findViewById(R.id.layoutStockEvents);
        switchStockEvents = findViewById(R.id.switchStockEvents);



  }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //   Toast.makeText(getApplicationContext(),"asdasdas",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListeners() {

        switchNot.setOnCheckedChangeListener((buttonView, isChecked) -> {
           MyApplication.editor.putBoolean("enable_all_notifation",isChecked).apply();
            if(isChecked){
                enableAll();
                }
            else {
                disableAll();
            }

        });


        switchBuy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeSetting();
        });

        switchSell.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeSetting();
        });

        switchCashEvents.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeSetting();
        });

        switchStockEvents.setOnCheckedChangeListener((buttonView, isChecked) -> {
            changeSetting();
        });

        layoutPushNotification.setOnClickListener(view -> disableAll());
        layoutStockEvents.setOnClickListener(view -> switchStockEvents.performClick());
        layoutBuy.setOnClickListener(view -> switchBuy.performClick());
        layoutSell.setOnClickListener(view -> switchSell.performClick());
        layoutCashEvents.setOnClickListener(view -> switchCashEvents.performClick());

    }


    private void changeSetting(){
        mSaveSettings = new SaveUserNotificationSettings();
        mSaveSettings.executeOnExecutor(MyApplication.threadPoolExecutor);

    }


    public void back(View v) {

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Actions.checkSession(this);
        Actions.InitializeSessionServiceV2(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }




    private class GetUserNotificationSettings extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetUserNotificationSettings.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("userid", MyApplication.currentUser.getId()+"");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                MyApplication.notificationSettings = GlobalFunctions.GetUserNotificationSettings(result);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try{setSettings();}catch (Exception e){}

        }
    }


    private void setSettings(){
        switchStockEvents.setChecked(MyApplication.notificationSettings.isStockEvents());
        switchCashEvents.setChecked(MyApplication.notificationSettings.isCashEvents());
        switchBuy.setChecked(MyApplication.notificationSettings.isBuy());
        switchSell.setChecked(MyApplication.notificationSettings.isSell());

/*        if(!MyApplication.notificationSettings.isStockEvents() && !MyApplication.notificationSettings.isCashEvents() && !MyApplication.notificationSettings.isBuy() && !MyApplication.notificationSettings.isSell()) {
            switchNot.setChecked(false);
            disableAll();
        }
        else
            switchNot.setChecked(true);*/
    }




    private class SaveUserNotificationSettings extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.SaveUserNotificationSettings.getValue(); // this method uses key after login

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("Buy").value(switchBuy.isChecked())
                        .key("CashEvents").value(switchCashEvents.isChecked())
                        .key("Lang").value("1")
                        .key("Sell").value(switchSell.isChecked())
                        .key("StockEvents").value(switchStockEvents.isChecked())
                        .key("UserID").value(MyApplication.currentUser.getId())
                        .key("key").value(MyApplication.currentUser.getKey())
                        .key("SendPushNotification").value(true)
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
                result = ConnectionRequests.POSTWCF(url, stringer);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try{Log.wtf("result_save",result);}catch (Exception e){}
            MyApplication.saveNotificationSettings(MyApplication.notificationSettings);

        }
    }







    private void disableAll(){

        switchNot.setChecked(false);
/*        switchStockEvents.setChecked(false);
        switchBuy.setChecked(false);
        switchSell.setChecked(false);
        switchCashEvents.setChecked(false);*/

        switchStockEvents.setEnabled(false);
        switchBuy.setEnabled(false);
        switchSell.setEnabled(false);
        switchCashEvents.setEnabled(false);

        switchStockEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStockEvents.setChecked(!switchStockEvents.isChecked());
            }
        });


        switchBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBuy.setChecked(!switchBuy.isChecked());
            }
        });


        switchSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchSell.setChecked(!switchSell.isChecked());
            }
        });

        switchCashEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCashEvents.setChecked(!switchCashEvents.isChecked());
            }
        });



    }


    private void enableAll(){

        switchNot.setChecked(true);
/*        switchStockEvents.setChecked(true);
        switchBuy.setChecked(true);
        switchSell.setChecked(true);
        switchCashEvents.setChecked(true);*/

        switchStockEvents.setEnabled(true);
        switchBuy.setEnabled(true);
        switchSell.setEnabled(true);
        switchCashEvents.setEnabled(true);


        switchStockEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStockEvents.setChecked(switchStockEvents.isChecked());
            }
        });


        switchBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchBuy.setChecked(switchBuy.isChecked());
            }
        });


        switchSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchSell.setChecked(switchSell.isChecked());
            }
        });

        switchCashEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCashEvents.setChecked(switchCashEvents.isChecked());
            }
        });
    }
}
