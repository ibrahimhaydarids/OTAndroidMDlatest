package com.ids.fixot.activities;



import android.app.Activity;
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

import com.ids.fixot.adapters.StockAlertsAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.Lookups;


import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Amal on 4/6/2017.
 */

public class StockAlertActivity extends AppCompatActivity implements MarketStatusListener, StockAlertsAdapter.RecyclerViewOnItemClickListener {

    RelativeLayout rlLayout;
    RecyclerView rvStockAlerts;
    private BroadcastReceiver receiver;
    LinearLayoutManager llm;
    StockAlertsAdapter adapter;
    LinearLayout linearAddNewAlert;

    private ArrayList<Lookups> arrayUserAlerts;
    private ArrayList<Lookups> arrayAdapterAlerts;
  //  GetUserNotificationSettings mGetUserNotification;
    GetStockAlerts mGetStockAlerts;
    ActivateStockAlert mActivateStockAlert;
    DeleteStockAlert mDeleteStockAlert;
    LinearLayout loading;
    Spinner spInstrumentsTop;

    public StockAlertActivity() {
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
        setContentView(R.layout.activity_stock_alerts);
        Actions.initializeBugsTracking(this);

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.settings), StockAlertActivity.this);
        Actions.overrideFonts(this, rlLayout, false);
        Actions.showHideFooter(this);


        MyApplication.editor.putInt("lang", MyApplication.lang).apply();

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
        rvStockAlerts=findViewById(R.id.rvStockAlerts);
        linearAddNewAlert=findViewById(R.id.linearAddNewAlert);
        loading=findViewById(R.id.loading);

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

        linearAddNewAlert.setOnClickListener(v->{
            startActivity(new Intent(StockAlertActivity.this,AddStockAlertActivity.class));
        });

    }




    public void back(View v) {

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Actions.checkSession(this);
        Actions.InitializeSessionServiceV2(this);
        getStocks();

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

    @Override
    public void onItemClicked(View v, int position) {


        if (adapter.getAlerts().get(position).getStatusID() == enums.AlertStatus.ACTIVE.getValue()){
            showDialogConfirmation(this,adapter.getAlerts().get(position).getStatusID(),getString(R.string.confirm_stock_alert_delete),getString(R.string.delete_alert),adapter.getAlerts().get(position).getID());

        } else{
            showDialogConfirmation(this,adapter.getAlerts().get(position).getStatusID(),getString(R.string.confirm_stock_alert_activate),getString(R.string.activate_alert),adapter.getAlerts().get(position).getID());

        }

/*        if (adapter.getAlerts().get(position).getStatusID() == enums.AlertStatus.ACTIVE.getValue()){
            mDeleteStockAlert = new DeleteStockAlert();
            mDeleteStockAlert.executeOnExecutor(MyApplication.threadPoolExecutor,adapter.getAlerts().get(position).getID()+"");
        } else{
            mActivateStockAlert = new ActivateStockAlert();
            mActivateStockAlert.executeOnExecutor(MyApplication.threadPoolExecutor, adapter.getAlerts().get(position).getID() + "");
        }*/
    }

private void getStocks(){
    mGetStockAlerts = new GetStockAlerts();
    mGetStockAlerts.executeOnExecutor(MyApplication.threadPoolExecutor);
}

    private class GetStockAlerts extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            MyApplication.userStockAlerts.clear();
            String url = MyApplication.link + MyApplication.GetStockAlerts.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("userid", MyApplication.currentUser.getId()+"");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                MyApplication.userStockAlerts.addAll(GlobalFunctions.GetStockAlerts(result));
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            setStockAlerts();

        }
    }






    private class ActivateStockAlert extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          loading.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.ActivateStockAlert.getValue(); // this method uses key after login

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ID").value(params[0])
                        .key("key").value(MyApplication.mshared.getString(getString(R.string.afterkey), ""))

                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.ActivateStockAlert.getKey(), Toast.LENGTH_LONG).show();
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
            loading.setVisibility(View.GONE);
            getStocks();

        }
    }



    private void showDialogConfirmation(Activity context, int type, String message, String title, int Id) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> {

                            if (type == enums.AlertStatus.ACTIVE.getValue()){
                                mDeleteStockAlert = new DeleteStockAlert();
                                mDeleteStockAlert.executeOnExecutor(MyApplication.threadPoolExecutor,Id+"");
                            } else{
                                mActivateStockAlert = new ActivateStockAlert();
                                mActivateStockAlert.executeOnExecutor(MyApplication.threadPoolExecutor, Id + "");
                            }
                        })

                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> {
                            // do nothing
                            dialog.cancel();
                        })
                .show();
    }




    private class DeleteStockAlert extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          loading.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.DeleteStockAlert.getValue(); // this method uses key after login

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ID").value(params[0])
                        .key("key").value(MyApplication.mshared.getString(getString(R.string.afterkey), ""))

                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.DeleteStockAlert.getKey(), Toast.LENGTH_LONG).show();
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
            loading.setVisibility(View.GONE);
            getStocks();

        }
    }



private void setStockAlerts(){
    llm = new LinearLayoutManager(StockAlertActivity.this);
    adapter = new StockAlertsAdapter(StockAlertActivity.this, MyApplication.userStockAlerts, this);
    rvStockAlerts.setLayoutManager(llm);
    rvStockAlerts.setAdapter(adapter);
}


}
