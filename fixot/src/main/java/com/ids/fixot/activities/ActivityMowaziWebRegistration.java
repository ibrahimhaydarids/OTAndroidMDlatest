package com.ids.fixot.activities;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.json.JSONStringer;

/**
 * Created by DEV on 3/20/2018.
 */

public class ActivityMowaziWebRegistration extends AppCompatActivity implements MarketStatusListener  {

    RelativeLayout rootLayout;
    WebView wvDetails;
    String url;
    private BroadcastReceiver receiver;
    private boolean started = false;
    CheckBox cbTerms;
    Button btSubmit;
    Spinner spInstrumentsTop;

    public ActivityMowaziWebRegistration() {
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_agreement);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();

       try{ url = getIntent().getExtras().getString("url");}catch (Exception e){}

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
        Actions.unregisterSessionReceiver(this);
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWebView() {

        wvDetails.getSettings().setJavaScriptEnabled(true);
        wvDetails.getSettings().setUseWideViewPort(true);
        wvDetails.getSettings().setLoadWithOverviewMode(true);
        wvDetails.getSettings().setAllowFileAccess(true);
        wvDetails.getSettings().setAllowContentAccess(true);
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
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


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

        btSubmit.setVisibility(View.GONE);
        cbTerms.setVisibility(View.GONE);
    }

    public void back(View v) {
        relogin();
   /*     Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkLanguage(this, started);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);

        Actions.InitializeMarketServiceV2(this);
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
        cbTerms = findViewById(R.id.cbTerms);
        btSubmit =findViewById(R.id.btSubmit);


     /*   btSubmit.setOnClickListener(v -> {
            if (!cbTerms.isChecked())
                Toast.makeText(ActivityMowaziWebRegistration.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();
            else {
              //  relogin();
                Intent returnIntent = new Intent();
               // returnIntent.putExtra("result","1");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

        });*/
    }


    @Override
    public void onBackPressed() {
     /*   Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();*/
        relogin();
       // super.onBackPressed();
    }

    public void share(View v) {
        String shareBody = url;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    private void relogin(){
        LoginTask login = new LoginTask();
        login.execute();
    }

    private void goLogin(){
        MyApplication.isAutoLogin=false;
        startActivity(new Intent(ActivityMowaziWebRegistration.this, LoginFingerPrintActivity.class));
        finish();
    }


    public class LoginTask extends AsyncTask<Void, Void, String> {

        String username = "", password = "";
        String random = Actions.getRandom();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = MyApplication.mshared.getString("cashed_username","");
            password = MyApplication.mshared.getString("cashed_password","");

            Log.wtf("login", "password = " + password);
            try {
                MyApplication.showDialog(ActivityMowaziWebRegistration.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.Login.getValue();
            Log.wtf("login_password",Actions.MD5(Actions.MD5(password) + random));
            Log.wtf("login_random",random);
            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("Username").value(username)
                        .key("Password").value(Actions.MD5(Actions.MD5(password) + random))
                        .key("Random").value(random)
                        .key("Key").value(getString(R.string.beforekey))
                        .key("DeviceType").value(Actions.getApplicationType())
                        .key("VersionNumber").value(Actions.getVersionName(ActivityMowaziWebRegistration.this))
                        .endObject();

                Log.wtf("login", "stringer = " + stringer);
                result = ConnectionRequests.POSTWCF(url, stringer);
                //Log.wtf("Login","result = " + result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //  load();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                MyApplication.currentUser = GlobalFunctions.GetUserInfo(result);
                MyApplication.editor.putString(getResources().getString(R.string.afterkey), MyApplication.currentUser.getKey()).apply();
                MyApplication.afterKey = MyApplication.currentUser.getKey();
                MyApplication.editor.putBoolean("fingerprintBlock", false).apply();
                if (MyApplication.currentUser.getStatus()==0||MyApplication.currentUser.getStatus()==52) {

                    Log.wtf("status_id",MyApplication.currentUser.getStatus()+"aaaaaaa");

                    for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
                        if (MyApplication.currentUser.getSubAccounts().get(i).isDefault()) {

                            MyApplication.selectedSubAccount = MyApplication.currentUser.getSubAccounts().get(i);
                            break;
                        }
                    }
                            MyApplication.currentUser.setUsername(username);
                            Actions.setLastUserId(ActivityMowaziWebRegistration.this,MyApplication.currentUser.getId());

                            if (MyApplication.currentUser.getStatus() == 0) {
                                     MyApplication.isLoggedIn=true;
                                     MyApplication.isAutoLogin=false;
                                     Actions.setLastMarketId(ActivityMowaziWebRegistration.this, 3);
                                     MyApplication.marketID ="3";
                                     finish();


                            }else {
                               finish();
                            }
             }
                else {
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
              finish();
            }
        }
    }


}
