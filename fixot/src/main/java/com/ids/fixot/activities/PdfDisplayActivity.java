package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
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

/**
 * Created by DEV on 3/20/2018.
 */

public class PdfDisplayActivity extends AppCompatActivity implements MarketStatusListener , spItemListener {

    RelativeLayout rootLayout;
    WebView wvDetails;
    String url;
    private BroadcastReceiver receiver;
    private boolean started = false;
    Spinner spInstrumentsTop;
    public PdfDisplayActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_webpage);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();

        url = getIntent().getExtras().getString("url");
        try{ Log.wtf("intent_url",url);}catch (Exception e){}
        if(!url.contains("http")){
            url="http://"+url;
            Log.wtf("updated_url",url);
        }

        loadWebView();
        wvDetails.loadUrl(url);




        Actions.initializeToolBar(getString(R.string.news), this);
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);


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
        spInstrumentsTop.setVisibility(View.GONE);
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

    private void loadWebView() {
        MyApplication.showDialog(PdfDisplayActivity.this);
        wvDetails.getSettings().setDomStorageEnabled(true);
        wvDetails.getSettings().setJavaScriptEnabled(true);

        wvDetails.getSettings().setUseWideViewPort(true);
        wvDetails.getSettings().setLoadWithOverviewMode(true);
        wvDetails.getSettings().setAllowFileAccess(true);
        wvDetails.getSettings().setAllowContentAccess(true);



        wvDetails.getSettings().setAllowFileAccess(true);
        wvDetails.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvDetails.getSettings().setSupportMultipleWindows(true);
        wvDetails.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);



        wvDetails.setLayerType(WebView.LAYER_TYPE_NONE, null);

        wvDetails.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.wtf("url", url);

                /*if (url.contains(".pdf")) {

                    try {
                        Log.wtf("contains pdf", "yes");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "application/pdf");
                        view.getContext().startActivity(intent);

                        //startActivity(new Intent(AkIndexActivity.this, ReportsViewActivity.class).putExtra("url", url));

                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        e.printStackTrace();

                        //startActivity(new Intent(AkIndexActivity.this, ReportsViewActivity.class).putExtra("url", url));

                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setDataAndType(Uri.parse("https://docs.google.com/viewer?url=" + url), "text/html");
                        startActivity(intent2);
                    }
                } else {
                    Log.wtf("contains pdf", "no");
                    //view.loadUrl(url);

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }*/
                return false;
            }


            @Override
            public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.wtf("onPageFinished", "onPageFinished");
                Log.wtf("PAGE URL", url);
                MyApplication.dismiss();
            }

        });

        wvDetails.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

            }
        });
    }

    public void back(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Actions.checkLanguage(this, started);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //Actions.InitializeMarketServiceV2(this);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        wvDetails = findViewById(R.id.wvDetails);
    }


    public void share(View v) {
        String shareBody = url;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }
}
