package com.ids.fixot.activities;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.NewsItem;

import java.util.Calendar;

/**
 * Created by user on 7/24/2017.
 */

public class NewsDetailsActivity extends AppCompatActivity implements MarketStatusListener , spItemListener {

    RelativeLayout rootLayout;
    TextView tvTitle, tvDate, tvDescription;
    Button btSource;
    NewsItem newsItem;
    private BroadcastReceiver receiver;
    private boolean started = false;
    Spinner spInstrumentsTop;
    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();
    }

    public NewsDetailsActivity() {
        LocalUtils.updateConfig(this);
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
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
        setContentView(R.layout.activity_news_details);
        Actions.initializeBugsTracking(this);

        findViews();
        started = true;

        try {
            newsItem = getIntent().getExtras().getParcelable("newsItem");
        } catch (Exception e) {
            e.printStackTrace();
            newsItem = new NewsItem();
        }
        try {

            if (newsItem.getLink()==null || newsItem.getLink().isEmpty()) {
                btSource.setVisibility(View.GONE);
            } else {
                btSource.setVisibility(View.VISIBLE);
                Log.wtf("news_link",newsItem.getLink());
            }

        }catch (Exception e){
            btSource.setVisibility(View.GONE);
        }

        try {
            tvTitle.setText(newsItem.getHead());
        } catch (Exception e) {
            e.printStackTrace();
            tvTitle.setText("");
        }

        try {
            tvDate.setText(newsItem.getCreationDate());
        } catch (Exception e) {
            e.printStackTrace();
            tvDate.setText("");
        }

        try {
           // tvDescription.setText(newsItem.getDetails());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvDescription.setText(Html.fromHtml(newsItem.getDetails(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvDescription.setText(Html.fromHtml(newsItem.getDetails()));
            }


        } catch (Exception e) {
            e.printStackTrace();
            tvDescription.setText("");
        }

        if (newsItem.getDetails().equals("")) {
            tvDescription.setVisibility(View.GONE);
        }

        Actions.initializeToolBar(getString(R.string.news), NewsDetailsActivity.this);
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);

        if (isProbablyArabic(newsItem.getHead())) {
            //Log.wtf("News","is arabic");
            tvTitle.setTypeface(MyApplication.droidbold);
            tvTitle.setGravity(Gravity.RIGHT);
        }

        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
          spInstrumentsTop.setVisibility(View.GONE);
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
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back(View v) {
        finish();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        btSource = findViewById(R.id.btSource);

        btSource.setOnClickListener(v ->
                {


                    Intent browserIntent ;
                    String url = newsItem.getLink();
                    if(url.substring(url.length() - 4).equals(".pdf")) {
                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }else {
                        startActivity(new Intent(NewsDetailsActivity.this, PdfDisplayActivity.class).putExtra("url", url));

                    }


/*//                    Intent browserIntent ;
                    String url = newsItem.getLink();


                    Log.wtf("last 4 character", ": " + url.substring(url.length() - 4));
                    if (url.substring(url.length() - 4).equals(".pdf")) {
                     //  url = "https://docs.google.com/viewer?url=" + url;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);

                    }else {

                        Log.wtf("origianl", "link = " + newsItem.getLink());
                        Log.wtf("news", "link = " + url);

                        startActivity(new Intent(NewsDetailsActivity.this, PdfDisplayActivity.class).putExtra("url", url));
                    }*/
                }
        );
    }


    public void goTo(View v) {

        try {

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
//            startActivity(browserIntent);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(newsItem.getLink()), "application/pdf");
            this.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            //user does not have a pdf viewer installed
            e.printStackTrace();

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
//            startActivity(browserIntent);

            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setDataAndType(Uri.parse("https://docs.google.com/viewer?url=" + newsItem.getLink()), "text/html");
            startActivity(intent2);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
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

        //Actions.InitializeMarketServiceV2(this);
    }


    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }
}
