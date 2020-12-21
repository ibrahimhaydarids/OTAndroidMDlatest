package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.ids.fixot.adapters.NewsRecyclerAdapter;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.NewsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class NewsActivity extends AppCompatActivity implements MarketStatusListener , spItemListener {

    LinearLayoutManager llm;
    GetNews mGetNews;
    RelativeLayout rootLayout;
    private BroadcastReceiver receiver;
    private RecyclerView rvNews;
    private TextView tvNoData;
    private NewsRecyclerAdapter adapter;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisibleItems;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<NewsItem> allNews = new ArrayList<>();
    private boolean pulltoRefresh = false, flagLoading = false;
    private boolean started = false;
    Spinner spInstrumentsTop;
    public NewsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();

        if (Actions.isNetworkAvailable(this)) {
           allNews.clear();
          try{ adapter.notifyDataSetChanged();}catch (Exception e){}
            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

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
        setContentView(R.layout.activity_news);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();
        setListeners();

        Actions.overrideFonts(this, rootLayout, false);
        Actions.showHideFooter(this);

        if (Actions.isNetworkAvailable(this)) {

            mGetNews = new GetNews();
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
          //  spInstrumentsTop.setVisibility(View.GONE);
            Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }
    }


    public void back(View v) {
        supportFinishAfterTransition();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        rvNews = findViewById(R.id.rvNews);
        llm = new LinearLayoutManager(NewsActivity.this);

        swipeContainer = findViewById(R.id.swipeContainer);
        tvNoData = findViewById(R.id.tvNoData);

        rvNews.setLayoutManager(llm);
        Actions.initializeToolBar(getString(R.string.news), NewsActivity.this);
    }

    private void setListeners() {

        swipeContainer.setOnRefreshListener(() -> {

            allNews.clear();
            adapter.notifyDataSetChanged();
            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
            swipeContainer.setRefreshing(false);
        });

        adapter = new NewsRecyclerAdapter(this, allNews);
        rvNews.setAdapter(adapter);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    public void addItems() {
        if (allNews.size() > 0) {
            flagLoading = false;
            //getNews = new GetNews();
            //getNews.execute(allNews.get(allNews.size() - 1).getId() + "");
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
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

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
        Actions.unregisterSessionReceiver(this);
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
    }

    private class GetNews extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetNews.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("newsId", "0");
            parameters.put("stockId", "");
            parameters.put("language", Actions.getLanguage()); //Actions.getLanguage()
            parameters.put("count", "400"  /*MyApplication.count*/);
            parameters.put("MarketID", MyApplication.marketID);
            parameters.put("Tstamp", "0");
            parameters.put("key", getResources().getString(R.string.beforekey));

            Log.wtf("news_market_id",MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, NewsActivity.this, parameters);
                Log.wtf("result", result);
                allNews.addAll(GlobalFunctions.GetNews(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetNews.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!BuildConfig.Enable_Markets) {
                ArrayList<NewsItem> filtered = new ArrayList<>(filterNews());
                allNews.clear();
                allNews.addAll(filtered);
            }

            adapter.notifyDataSetChanged();
        }
    }



    private ArrayList<NewsItem> filterNews(){


        ArrayList<String> arraySections=new ArrayList<>();
        for (int i=0;i<allNews.size();i++){
          Log.wtf("news_month",i+"---"+getMonth(allNews.get(i).getTime()));
          if(!arraySections.contains(getMonth(allNews.get(i).getTime())))
              arraySections.add(getMonth(allNews.get(i).getTime()));
        }

        ArrayList<NewsItem> filteredNews=new ArrayList<>();
        for (int i=0;i<arraySections.size();i++){
            filteredNews.add(new NewsItem(0,true,arraySections.get(i)));
            for (int j=0;j<allNews.size();j++){
                if(getMonth(allNews.get(j).getTime()).matches(arraySections.get(i))){
                    filteredNews.add(allNews.get(j));
                }
            }
        }

        return filteredNews;
    }



  private String getMonth(String newsDate){
      String onlyMonth="";
      SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a", Locale.ENGLISH);
      try {
          Date date = format.parse(newsDate);
           onlyMonth = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(date);
      } catch (ParseException e) {
          e.printStackTrace();
      }
      return onlyMonth;
  }

}
