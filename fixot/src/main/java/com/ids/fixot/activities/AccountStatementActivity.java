package com.ids.fixot.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.ids.fixot.adapters.OrderDurationTypeAdapter;
import com.ids.fixot.adapters.OrderTypeSpinnerAdapter;
import com.ids.fixot.adapters.ReportAdapter;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.SubAccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user on 10/5/2017.
 */

public class AccountStatementActivity extends AppCompatActivity implements MarketStatusListener , spItemListener {


    LinearLayout llDate;
    RelativeLayout rootLayout;
    TextView tvToDate,tvFromDate;
    Button btAccountStatement;
    WebView wvData;
    Calendar myCalendar;
    Calendar myCalendarFrom;
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener dateFrom;
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    int reportType = 1;
    RadioGroup rgAccounts;
    RadioButton rbReport, rbGroupedReport;
    private BroadcastReceiver receiver;
    private boolean started = false;
    Spinner spInstrumentsTop;
    String reportLink;
    GetMobileReportPageSetup getReportList;
    AlertDialog.Builder builder;
    ReportAdapter reportSpinnerAdapter;
    Spinner spReportList;
    LinearLayout loading;



    public AccountStatementActivity() {
        LocalUtils.updateConfig(this);
    }
    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();
        try{subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);}catch (Exception e){}
        try{ spSubAccounts.setSelection(Actions.getDefaultSubPosition());}catch (Exception e){}
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
        setContentView(R.layout.activity_account_statement);
        Actions.initializeBugsTracking(this);

        findViews();

        started = true;

        Actions.showHideFooter(this);
        Actions.initializeToolBar(getResources().getString(R.string.account_statement_title), AccountStatementActivity.this);
        Actions.overrideFonts(this, rootLayout, false);

        Actions.showHideFooter(this);
        Actions.initializeToolBar(getResources().getString(R.string.account_statement_title), AccountStatementActivity.this);
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

        if(MyApplication.reportPageList.size()>0){
            setData(0);
        }else {
            getReportList = new GetMobileReportPageSetup();
            getReportList.executeOnExecutor(MyApplication.threadPoolExecutor);

        }


        spReportList=findViewById(R.id.spReportList);

        reportSpinnerAdapter = new ReportAdapter(this, MyApplication.reportPageList, true);
        spReportList.setAdapter(reportSpinnerAdapter);
        spReportList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                reportType = MyApplication.reportPageList.get(position).getTypeID();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    private void setData(int position){
        if(MyApplication.reportPageList.get(position).isHasFromDate()) tvFromDate.setVisibility(View.VISIBLE); else tvFromDate.setVisibility(View.GONE);
        if(MyApplication.reportPageList.get(position).isHasTodate()) tvToDate.setVisibility(View.VISIBLE); else tvToDate.setVisibility(View.GONE);
        reportLink=MyApplication.reportPageList.get(position).getReportLink();
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

    private void findViews() {

        spSubAccounts = findViewById(R.id.spSubAccounts);
        ImageView ivUserSubAccount=findViewById(R.id.ivUserSubAccount);
        if(BuildConfig.Enable_Markets)
            ivUserSubAccount.setVisibility(View.GONE);
        else
            ivUserSubAccount.setVisibility(View.VISIBLE);
        rgAccounts = findViewById(R.id.rgAccounts);
        loading=findViewById(R.id.loading);

        rbReport = findViewById(R.id.rbReport);
        rbGroupedReport = findViewById(R.id.rbGroupedReport);

        llDate = findViewById(R.id.llDate);
        rootLayout = findViewById(R.id.rootLayout);
        wvData = findViewById(R.id.wvData);
        tvToDate = findViewById(R.id.tvToDate);
        tvFromDate = findViewById(R.id.tvFromDate);
     //   tvFromDate.setVisibility(View.VISIBLE);
/*        if(BuildConfig.Enable_Markets) {
            tvFromDate.setVisibility(View.GONE);
        }
        else {
            tvFromDate.setVisibility(View.VISIBLE);
        }*/
        btAccountStatement = findViewById(R.id.btAccountStatement);

        //<editor-fold desc="Webview Settings">
        wvData.getSettings().setDomStorageEnabled(true);
        wvData.clearCache(true);
        wvData.getSettings().setBuiltInZoomControls(true);
        wvData.getSettings().setSupportZoom(true);
        wvData.getSettings().setUseWideViewPort(true);
        wvData.getSettings().setJavaScriptEnabled(true);
        wvData.getSettings().setLoadWithOverviewMode(true);
        wvData.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvData.getSettings().setDomStorageEnabled(true);

        wvData.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                //handler.proceed(); // Ignore SSL certificate errors
                Log.wtf("onReceivedSslError", "onReceivedSslError");


                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountStatementActivity.this, R.style.AlertDialogCustom);
                builder.setMessage("SSL Approval");
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                MyApplication.showDialog(AccountStatementActivity.this);
                //   loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.wtf("onPageFinished", "onPageFinished");
                Log.wtf("PAGE URL", url);
                MyApplication.dismiss();
                //loading.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.wtf("onReceivedError", "onReceivedError");
                MyApplication.dismiss();
                //  loading.setVisibility(View.GONE);
            }
        });
        //</editor-fold>

        if (getIntent().getExtras().getBoolean("isAccountStatement")) {

            llDate.setVisibility(View.VISIBLE);
            btAccountStatement.setVisibility(View.VISIBLE);

            //<editor-fold desc="Load Account Statement Page">
            tvToDate.setOnClickListener(v -> new DatePickerDialog(AccountStatementActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show());

            date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(tvToDate);
            };
            myCalendar = Calendar.getInstance();





            tvFromDate.setOnClickListener(v -> new DatePickerDialog(AccountStatementActivity.this, dateFrom, myCalendarFrom
                    .get(Calendar.YEAR), myCalendarFrom.get(Calendar.MONTH),
                    myCalendarFrom.get(Calendar.DAY_OF_MONTH)).show());

            dateFrom = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendarFrom.set(Calendar.YEAR, year);
                myCalendarFrom.set(Calendar.MONTH, monthOfYear);
                myCalendarFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFrom(tvFromDate);
            };
            myCalendarFrom = Calendar.getInstance();

            updateLabel(tvToDate);
            updateLabelFrom(tvFromDate);
            //this.updateLabel(tvToDate);

            btAccountStatement.setOnClickListener(v -> {

                HashMap<String, String> hash = new HashMap<>();
                hash.put("userid", MyApplication.selectedSubAccount.getUserId() + "");
                hash.put("PortfolioID", MyApplication.selectedSubAccount.getPortfolioId() + "");
                hash.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
                hash.put("todate", tvToDate.getText().toString());
                hash.put("key", MyApplication.currentUser.getKey());
              //  if(!BuildConfig.Enable_Markets)
                    hash.put("fromdate", tvFromDate.getText().toString());

                hash.put("reporttype", reportType+"");

                //    hash.put("reporttype", BuildConfig.Enable_Markets? reportType + "":MyApplication.reportPageList.get(0).getTypeID()+"");

                try {
                    //   wvData.loadUrl(MyApplication.statementLink + "ReportingWebsite/Mobile/AccountStatementReport.aspx", hash);
                    wvData.loadUrl(MyApplication.parameter.getMobileReportingPath() + "AccountStatementReport.aspx", hash);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("exception", e.getMessage());
                }
            });




            //</editor-fold>
        }


      /*  if(BuildConfig.Enable_Markets)
            rgAccounts.setVisibility(MyApplication.isMultiAccountStatements ? View.VISIBLE : View.GONE);
        else*/
            rgAccounts.setVisibility(View.GONE);
        rbReport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                reportType = 1;
            }
        });
        rbGroupedReport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                reportType = 2;
            }
        });

        try{
            subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
            spSubAccounts.setSelection(returnAccountIndex());
            spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){}
    }


/*    private int returnAccountIndex() {

        int index = -1;

        for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
            if (MyApplication.currentUser.getSubAccounts().get(i).getPortfolioId() == MyApplication.selectedSubAccount.getPortfolioId()) {
                index = i;
            }
        }

        return index;
    }*/


    private int returnAccountIndex() {

        int index = 0;
        ArrayList<SubAccount> arrayAccounts=new ArrayList<>();
        if(BuildConfig.Enable_Markets){
            arrayAccounts.addAll(Actions.getfilteredSubAccount());

        }else {
            arrayAccounts.addAll(MyApplication.currentUser.getSubAccounts());
        }

        for (int i = 0; i < arrayAccounts.size(); i++) {
            if (arrayAccounts.get(i).getPortfolioId() == MyApplication.selectedSubAccount.getPortfolioId()) {
                index = i;
                break;
            }
        }
        Log.wtf("index_account",index+"");

        return index;
    }

    private void updateLabel(TextView editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelFrom(TextView editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        editText.setText(sdf.format(myCalendarFrom.getTime()));
    }


    public void back(View v) {

        finish();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //   Actions.InitializeMarketServiceV2(this);
        Actions.checkLanguage(this, started);
        Actions.checkSession(this);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

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
                result = ConnectionRequests.GET(url, AccountStatementActivity.this, parameters);
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
            if(MyApplication.reportPageList.size()>0)
                setData(0);
            super.onPostExecute(s);
        }
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


}
