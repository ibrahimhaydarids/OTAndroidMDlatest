package com.ids.fixot.activities;

import android.app.DatePickerDialog;
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
import com.ids.fixot.adapters.NotificationRecyclerAdapter;
import com.ids.fixot.model.NotificationItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class NotificationActivity extends AppCompatActivity implements MarketStatusListener {

    LinearLayoutManager llm;
    GetNotifications mGetNews;
    RelativeLayout rootLayout;
    private BroadcastReceiver receiver;
    private RecyclerView rvNews;
    private TextView tvNoData;
    private NotificationRecyclerAdapter adapter;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisibleItems;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<NotificationItem> allNotifications = new ArrayList<>();
    private boolean pulltoRefresh = false, flagLoading = false;
    private boolean started = false;

    TextView tvToDate;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf;
    Spinner spInstrumentsTop;

    public NotificationActivity() {
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
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_notification);
        Actions.initializeBugsTracking(this);

        started = true;
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        findViews();
        setListeners();

        Actions.overrideFonts(this, rootLayout, false);
        Actions.showHideFooter(this);

        if (Actions.isNetworkAvailable(this)) {

            mGetNews = new GetNotifications();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

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


    public void back(View v) {
        if(MyApplication.isNotification){
            startActivity(new Intent(NotificationActivity.this, MarketIndexActivity.class));
            MyApplication.isNotification=false;
            finish();
        }else
            supportFinishAfterTransition();
    }

    private void findViews() {
       // MyApplication.editor.putInt("deviceId",0).apply();
        rootLayout = findViewById(R.id.rootLayout);
        rvNews = findViewById(R.id.rvNews);
        llm = new LinearLayoutManager(NotificationActivity.this);

        swipeContainer = findViewById(R.id.swipeContainer);
        tvNoData = findViewById(R.id.tvNoData);
        tvToDate = findViewById(R.id.tvToDate);
        rvNews.setLayoutManager(llm);
        Actions.initializeToolBar(getString(R.string.notifications), NotificationActivity.this);


        tvToDate.setOnClickListener(v -> new DatePickerDialog(NotificationActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        myCalendar = Calendar.getInstance();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(tvToDate);
            new GetNotifications().execute();
        };
    }


    private void updateLabel(TextView editText) {
        editText.setText(sdf.format(myCalendar.getTime()));
    }


    private void setListeners() {

        swipeContainer.setOnRefreshListener(() -> {

            allNotifications.clear();
            adapter.notifyDataSetChanged();
            mGetNews = new GetNotifications();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
            swipeContainer.setRefreshing(false);
        });

        adapter = new NotificationRecyclerAdapter(this, allNotifications);
        rvNews.setAdapter(adapter);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    public void addItems() {
        if (allNotifications.size() > 0) {
            flagLoading = false;
            //getNews = new GetNotifications();
            //getNews.execute(allNotifications.get(allNotifications.size() - 1).getId() + "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //   Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mGetNews.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(mGetNews);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("News ex", e.getMessage());
        }
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyApplication.isNotification=false;
    }

    private class GetNotifications extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetMobileNotification.getValue(); // this method uses key after login

            allNotifications.clear();
            HashMap<String, String> parameters = new HashMap<String, String>();

            if(MyApplication.currentUser.getId()==0)
                return "";

            parameters.put("userId",  MyApplication.currentUser.getId()+"");
            parameters.put("fromdate", sdf.format(myCalendar.getTime()));
            parameters.put("todate", sdf.format(myCalendar.getTime()));
            parameters.put("typeid", "0");


            parameters.put("isSent", "1");
            parameters.put("count", "" + 0);
            parameters.put("key", getResources().getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, NotificationActivity.this, parameters);
                Log.wtf("result", result);
                allNotifications.addAll(GlobalFunctions.GetNotifications(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetMobileNotification.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if(MyApplication.isNotification) {
            startActivity(new Intent(NotificationActivity.this, MarketIndexActivity.class));
            MyApplication.isNotification=false;
            finish();
        }else
           super.onBackPressed();
    }


}
