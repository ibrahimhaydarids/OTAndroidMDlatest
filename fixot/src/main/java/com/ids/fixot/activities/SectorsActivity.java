package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.ids.fixot.adapters.SectorRecyclerAdapter;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.Sector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by user on 7/21/2017.
 */

public class SectorsActivity extends AppCompatActivity implements SectorRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener , spItemListener {

    Toolbar myToolbar;
    ImageView ivBack;
    TextView tvToolbarTitle, tvToolbarStatus, tvStockIDHeader, tvPriceHeader, tvChangePercentHeader;
    RecyclerView rvSectorIndex;
    LinearLayoutManager llm;
    SectorRecyclerAdapter adapter;
    ArrayList<Sector> allSectors = new ArrayList<>();
    RelativeLayout rootLayout;
    GetSectorIndex mGetSectorIndex;
    private BroadcastReceiver receiver;
    private boolean started = false, running = true, connected = false;
    Spinner spInstrumentsTop;
    LinearLayout linearSessionsToolbar;
    public SectorsActivity() {
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
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();
        mGetSectorIndex = new GetSectorIndex();
        mGetSectorIndex.executeOnExecutor(MyApplication.threadPoolExecutor);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_sector_index);
        Actions.initializeBugsTracking(this);

        Actions.showHideFooter(this);

        findViews();
        MyApplication.sectorsTimesTamp = "0";
        started = true;

        adapter = new SectorRecyclerAdapter(SectorsActivity.this, allSectors, SectorsActivity.this);
        llm = new LinearLayoutManager(SectorsActivity.this);
        rvSectorIndex.setAdapter(adapter);
        rvSectorIndex.setLayoutManager(llm);

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            connected = false;
        } else {

            connected = true;
        }

        Actions.initializeToolBar(getString(R.string.sectors), SectorsActivity.this);
        Actions.overrideFonts(this, rootLayout, false);
        Actions.showHideFooter(this);
        Actions.setTypeface(new TextView[]{tvStockIDHeader, tvChangePercentHeader, tvPriceHeader}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        Actions.autofitText(tvStockIDHeader, tvChangePercentHeader, tvPriceHeader);
        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
            Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }

    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Actions.unregisterSessionReceiver(this);

        try {
            running = false;

            mGetSectorIndex.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(mGetSectorIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void onResume() {
        super.onResume();


        Actions.checkSession(this);

        if (connected) {

            mGetSectorIndex = new GetSectorIndex();
            mGetSectorIndex.executeOnExecutor(MyApplication.threadPoolExecutor);
        }

        Actions.checkLanguage(this, started);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);
    }

    public void back(View v) {
        this.finish();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        linearSessionsToolbar=findViewById(R.id.linearSessionsToolbar);
               linearSessionsToolbar.setVisibility(View.GONE);
        myToolbar = findViewById(R.id.my_toolbar);

        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvToolbarStatus = findViewById(R.id.toolbar_status);
        tvStockIDHeader = findViewById(R.id.tvStockIDHeader);
        tvPriceHeader = findViewById(R.id.tvPriceHeader);
        tvChangePercentHeader = findViewById(R.id.tvChangePercentHeader);

        ivBack = findViewById(R.id.ivBack);
        rvSectorIndex = findViewById(R.id.rvSectorIndex);
    }

    @Override
    public void onItemClicked(View v, int position) {

        try {
            Bundle b = new Bundle();
            b.putParcelable("sector", allSectors.get(position));
            Intent i = new Intent();
            i.putExtras(b);
            i.setClass(SectorsActivity.this, SectorDetailActivity.class);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetSectorIndex extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetSectorIndex.getValue();

            HashMap<String, String> parameters = new HashMap<String, String>();

            while (running) {

                if (isCancelled())
                    break;

                parameters.put("sectorId", "");
                parameters.put("MarketID", MyApplication.marketID);
                parameters.put("FromTS", "0");//MyApplication.sectorsTimesTamp);
                parameters.put("key", getResources().getString(R.string.beforekey));

                try {
                    result = ConnectionRequests.GET(url, SectorsActivity.this, parameters);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!result.equals("")) {
                        if (MyApplication.isDebug) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSectorIndex.getKey(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

                publishProgress(result);
                try {

                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {

                allSectors.clear();
                ArrayList<Sector> retrievedSectors = GlobalFunctions.GetSectorIndex(values[0]);
                allSectors.addAll(retrievedSectors);
                runOnUiThread(() -> adapter.notifyDataSetChanged());

                Log.wtf("retrievedSectors", "ss " + retrievedSectors.size());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }
}
