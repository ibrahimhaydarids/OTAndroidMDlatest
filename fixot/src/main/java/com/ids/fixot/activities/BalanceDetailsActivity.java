package com.ids.fixot.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.ids.fixot.adapters.BalanceSummaryAdapter;
import com.ids.fixot.adapters.ExecutedAdapter;
import com.ids.fixot.adapters.MarginPaymentAdapter;
import com.ids.fixot.adapters.PortoflioStockForwardAdapter;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.adapters.ValuesGridRecyclerAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.BalanceSummary;
import com.ids.fixot.model.BalanceSummaryParent;
import com.ids.fixot.model.ExecutedOrders;
import com.ids.fixot.model.MarginPayment;
import com.ids.fixot.model.NewsItem;
import com.ids.fixot.model.StockSummary;
import com.ids.fixot.model.SubAccount;
import com.ids.fixot.model.ValueItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 7/21/2017.
 */

public class BalanceDetailsActivity extends AppCompatActivity implements PortoflioStockForwardAdapter.RecyclerViewOnItemClickListener, MarketStatusListener , spItemListener {

    Toolbar myToolbar;
    ImageView ivBack, ivPortfolio;
    TextView tvToolbarTitle, tvToolbarStatus, tvLogout;
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    //SwipeRefreshLayout swipeContainer;
    ScrollView scrollView;
    ValuesGridRecyclerAdapter adapterMarginDetailsValues;

    LinearLayout loading;
    GridLayoutManager llUserData;
    Double initialAvailableAmount=0.0;


    private ArrayList<ExecutedOrders> arrayExecuted = new ArrayList<>();
    private ArrayList<MarginPayment> arrayMarginPayments = new ArrayList<>();
    private ArrayList<BalanceSummaryParent> arrayParentBalanced = new ArrayList<>();

    private ArrayList<BalanceSummary> arrayBalacedFields = new ArrayList<>();
    private ArrayList<BalanceSummary> arrayBalancedGroups = new ArrayList<>();
    LinearLayoutManager llm;
    LinearLayoutManager llm2;
    LinearLayoutManager llm3;
    LinearLayout linearExecuted,linearMarginDetails,linearMarginPayments;
    Button btBalanceSummary,btExecutedOrdersGrouped,btMarginPayments,btMarginDetails;
    RecyclerView rvBalanceSummary,rvExecuted,rvMarginDetails,rvMarginPayments;
    int selectedTab=1;

    private BalanceSummaryAdapter balanceAdapter;
    private ExecutedAdapter executedAdapter;
    private MarginPaymentAdapter marginPaymentAdapter;
    RelativeLayout rlUserHeader, rootLayout;
    private boolean isCalled=false;
    GetBalanceDetails getBalanceDetails = new GetBalanceDetails();
    ArrayList<ValueItem> arrayMarginDetailsValueItems = new ArrayList<>();
    GetMarginDetails getMarginDetails = new GetMarginDetails();
    GetMarginPayments getMarginPayments = new GetMarginPayments();
    private BroadcastReceiver receiver;
    private boolean started = false, showProgress = false;
    Spinner spInstrumentsTop;
    public BalanceDetailsActivity() {
        LocalUtils.updateConfig(this);
    }
    private SwipeRefreshLayout swipeContainer;
    TextView tvTotalAmountTitle,tvTotalAmount;

    @Override
    public void refreshMarketTime(String status, String time, Integer color) {

        final TextView marketstatustxt = findViewById(R.id.market_state_value_textview);
        final LinearLayout llmarketstatus = findViewById(R.id.ll_market_state);
        final TextView markettime = findViewById(R.id.market_time_value_textview);

        marketstatustxt.setText(status);
        markettime.setText(time);
        llmarketstatus.setBackground(ContextCompat.getDrawable(this, color));

    }

/*    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {

        try{subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()*//*MyApplication.currentUser.getSubAccounts()*//*);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);}catch (Exception e){}
        try{ spSubAccounts.setSelection(Actions.getDefaultSubPosition(),false);}catch (Exception e){}


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clearArrays();

                callData();
            }
        }, 200);


    }*/

    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCalled=false;

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_balance_request);
        Actions.initializeBugsTracking(this);

        findViews();
        if(!isCalled)
           callData();
        Log.wtf("portfolio_id",MyApplication.selectedSubAccount.getPortfolioId()+"");
        Log.wtf("user_id",    MyApplication.selectedSubAccount.getUserId() + "");
        Log.wtf("after_key",    MyApplication.mshared.getString(getString(R.string.afterkey), "") + "");
        //  .key("PortfolioID").value(MyApplication.selectedSubAccount.getPortfolioId())
        Actions.showHideFooter(this);
        started = true;


        try {
            tvToolbarStatus.setText(MyApplication.marketStatus.getStatusName());
        } catch (Exception e) {
            e.printStackTrace();
        }




        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

        Actions.initializeToolBar(getString(R.string.portfolio), BalanceDetailsActivity.this);

        Actions.overrideFonts(this, rootLayout, false);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);



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



    /*private void setInfoText(){
        String info = MyApplication.selectedSubAccount.toString() + " - " + MyApplication.selectedSubAccount.getPortfolioName();
        tvInfo.setText(info);
    }*/


    private void callData(){

        clearArrays();
        if (getBalanceDetails != null)
            getBalanceDetails.cancel(true);

        if (getMarginDetails != null)
            getMarginDetails.cancel(true);

        if (getMarginPayments != null)
            getMarginPayments.cancel(true);
        getBalanceDetails = new GetBalanceDetails();
        getBalanceDetails.executeOnExecutor(MyApplication.threadPoolExecutor);

        getMarginDetails = new GetMarginDetails();
        getMarginDetails.executeOnExecutor(MyApplication.threadPoolExecutor);

        getMarginPayments = new GetMarginPayments();
        getMarginPayments.executeOnExecutor(MyApplication.threadPoolExecutor);
    }


    private void findViews() {
        ImageView ivUserSubAccount=findViewById(R.id.ivUserSubAccount);
        if(BuildConfig.Enable_Markets)
            ivUserSubAccount.setVisibility(View.GONE);
        else
            ivUserSubAccount.setVisibility(View.VISIBLE);
        llUserData = new GridLayoutManager(this, MyApplication.GRID_VALUES_SPAN_COUNT);

        tvTotalAmountTitle = findViewById(R.id.tvTotalAmountTitle);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalAmountTitle.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

        spSubAccounts = findViewById(R.id.spSubAccounts);
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {

            try{executedAdapter.notifyDataSetChanged();}catch (Exception e){}
            try{balanceAdapter.notifyDataSetChanged();}catch (Exception e){}
            callData();

            swipeContainer.setRefreshing(false);
        });

        llm = new LinearLayoutManager(BalanceDetailsActivity.this);
        llm2 = new LinearLayoutManager(BalanceDetailsActivity.this);
        llm3 = new LinearLayoutManager(BalanceDetailsActivity.this);
        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);

        linearMarginDetails = findViewById(R.id.linearMarginDetails);
        linearMarginPayments = findViewById(R.id.linearMarginPayments);
        linearExecuted = findViewById(R.id.linearExecuted);
        btBalanceSummary = findViewById(R.id.btBalanceSummary);
        btExecutedOrdersGrouped = findViewById(R.id.btExecutedOrdersGrouped);

        btMarginDetails = findViewById(R.id.btMarginDetails);
        btMarginPayments = findViewById(R.id.btMarginPayments);

        rvBalanceSummary = findViewById(R.id.rvBalanceSummary);
        rvExecuted = findViewById(R.id.rvExecuted);
        rvMarginDetails= findViewById(R.id.rvMarginDetails);
        rvMarginPayments= findViewById(R.id.rvMarginPayments);

        btBalanceSummary.setOnClickListener(v->{
            selectedTab=MyApplication.TAB_BALANCE_SUMMARY;
            checkLayout();
        });

        btExecutedOrdersGrouped.setOnClickListener(v->{
            selectedTab=MyApplication.TAB_EXECUTED_ORDERS;
            checkLayout();
        });

        btMarginDetails.setOnClickListener(v->{
            selectedTab=MyApplication.TAB_MARGIN_DETAILS;
            checkLayout();
        });
        btMarginPayments.setOnClickListener(v->{
            selectedTab=MyApplication.TAB_MARGIN_PAYMENTS;
            checkLayout();
        });

        rlUserHeader = findViewById(R.id.rlUserHeader);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
        tvToolbarStatus = findViewById(R.id.toolbar_status);

        ivBack = findViewById(R.id.ivBack);

        scrollView = findViewById(R.id.scrollView);
        loading=findViewById(R.id.loading);

        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(BalanceDetailsActivity.this));
        tvLogout.setVisibility(View.GONE);




        try {
            subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
            spSubAccounts.setSelection(returnAccountIndex(),false);
            spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   try{ MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);}catch (Exception e){}
                    callData();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){}

         try{checkLayout();}catch (Exception e){}
         try{setAdapters();}catch (Exception e){}
    }

    private void setAdapters(){
        balanceAdapter = new BalanceSummaryAdapter(this, arrayParentBalanced);
        rvBalanceSummary.setAdapter(balanceAdapter);

        executedAdapter = new ExecutedAdapter(this, arrayExecuted);
        rvExecuted.setAdapter(executedAdapter);
        rvBalanceSummary.setLayoutManager(llm);
        rvExecuted.setLayoutManager(llm2);


        marginPaymentAdapter = new MarginPaymentAdapter(this, arrayMarginPayments);
        rvMarginPayments.setAdapter(marginPaymentAdapter);
        rvMarginPayments.setLayoutManager(llm3);
    }





    private void resetTabs(){
        btBalanceSummary.setBackgroundResource(R.drawable.order_book_border_disable);
        btExecutedOrdersGrouped.setBackgroundResource(R.drawable.order_book_border_disable);
        btMarginDetails.setBackgroundResource(R.drawable.order_book_border_disable);
        btMarginPayments.setBackgroundResource(R.drawable.order_book_border_disable);

        rvBalanceSummary.setVisibility(View.GONE);
        linearExecuted.setVisibility(View.GONE);
        linearMarginDetails.setVisibility(View.GONE);
        linearMarginPayments.setVisibility(View.GONE);

    }



    private void checkLayout(){
        if(selectedTab==MyApplication.TAB_BALANCE_SUMMARY){
            resetTabs();
            rvBalanceSummary.setVisibility(View.VISIBLE);
            btBalanceSummary.setBackgroundResource(R.drawable.order_book_border_active);
        }else  if(selectedTab==MyApplication.TAB_EXECUTED_ORDERS){
            resetTabs();
            linearExecuted.setVisibility(View.VISIBLE);
            btExecutedOrdersGrouped.setBackgroundResource(R.drawable.order_book_border_active);
        }else  if(selectedTab==MyApplication.TAB_MARGIN_DETAILS){
            resetTabs();
            linearMarginDetails.setVisibility(View.VISIBLE);
            btMarginDetails.setBackgroundResource(R.drawable.order_book_border_active);
        }else  if(selectedTab==MyApplication.TAB_MARGIN_PAYMENTS){
            resetTabs();
            linearMarginPayments.setVisibility(View.VISIBLE);
            btMarginPayments.setBackgroundResource(R.drawable.order_book_border_active);
        }

        }

    private void clearArrays(){
        try{arrayBalacedFields.clear();}catch (Exception e){}
        try{arrayBalancedGroups.clear();}catch (Exception e){}
            try{arrayExecuted.clear();}catch (Exception e){}
                try{arrayMarginPayments.clear();}catch (Exception e){}
                    try{arrayParentBalanced.clear();}catch (Exception e){}
        try{balanceAdapter.notifyDataSetChanged();}catch (Exception e){}
        try{executedAdapter.notifyDataSetChanged();}catch (Exception e){}
        try{marginPaymentAdapter.notifyDataSetChanged();}catch (Exception e){}
    }


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

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        /*getPortfolio = new GetPortfolio();
        getPortfolio.executeOnExecutor(MyApplication.threadPoolExecutor);*/
        showProgress = false;

        Actions.checkLanguage(this, started);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

        Actions.InitializeSessionServiceV2(this);
         Actions.InitializeMarketServiceV2(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Actions.unregisterSessionReceiver(this);
        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void back(View v) {
        this.finish();
    }

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//    }

    @Override
    public void onBackPressed() {
        Actions.exitApp(this);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    public void onItemClicked(View v, int position) {


    }

    private class GetBalanceDetails extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            try{arrayExecuted.clear();}catch (Exception e){}
            try{arrayBalacedFields.clear();}catch (Exception e){}
                try{arrayBalancedGroups.clear();}catch (Exception e){}
            isCalled=true;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetBalanceDetails.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();
            Log.wtf("balance_url",url);

            parameters.put("UserId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("PortfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");

            parameters.put("adminId", "0");
            parameters.put("ParentUserId", MyApplication.currentUser.getParentUserId()+ "");
            parameters.put("ClientTypeId",MyApplication.selectedSubAccount.getUserId()+"");
            parameters.put("lang", MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                arrayExecuted.addAll(GlobalFunctions.getBalancedExecuted(result));
                arrayBalacedFields.addAll(GlobalFunctions.getBalancedFields(result));
                arrayBalancedGroups.addAll(GlobalFunctions.getBalancedGroups(result));
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            setDataBalanced();
            loading.setVisibility(View.GONE);
           // arrayExecuted.add(new ExecutedOrders("1","2","3","4","5","6","7","1"));


            try{balanceAdapter.notifyDataSetChanged();}catch (Exception e){}
            try{executedAdapter.notifyDataSetChanged();}catch (Exception e){}
            // setStockAlerts();

        }
    }



    private void setDataBalanced(){
        Log.wtf("setdata","setdata");


        for (int i=0;i<arrayBalancedGroups.size();i++){

            arrayParentBalanced.add(new BalanceSummaryParent(arrayBalancedGroups.get(i).getGroupId(),arrayBalancedGroups.get(i).getKey(),arrayBalancedGroups.get(i).getSymbol(),new ArrayList<>()));


    }

        for (int i=0;i<arrayParentBalanced.size();i++){
            ArrayList<BalanceSummary> arrayChild=new ArrayList<>();
            for (int j=0;j<arrayBalacedFields.size();j++){
                if(arrayBalacedFields.get(j).getGroupId().matches(arrayParentBalanced.get(i).getGroupId()) && !arrayChild.contains(arrayBalacedFields.get(j))){
                    arrayChild.add(arrayBalacedFields.get(j));
                }
            }
            arrayParentBalanced.get(i).setArraySummary(arrayChild);
        }

        Log.wtf("array_balance_size",arrayParentBalanced.size()+"aaa");
    }





    private class GetMarginDetails extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            isCalled=true;
            arrayMarginDetailsValueItems.clear();
            if(adapterMarginDetailsValues!=null)
               adapterMarginDetailsValues.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetMarginDetails.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();
            Log.wtf("balance_url",url);

            parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("lang", MyApplication.lang == MyApplication.ARABIC ? "Arabic" : "English");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);

                arrayMarginDetailsValueItems.addAll(GlobalFunctions.getBalanceDetailsValues(result));

            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            setBalanceDetailsData();
            loading.setVisibility(View.GONE);

            // setStockAlerts();

        }
    }


    private void setBalanceDetailsData(){
        rvMarginDetails.setLayoutManager(llUserData);
        if (arrayMarginDetailsValueItems.size() % 2 != 0) {
            llUserData.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //Log.wtf("position" , "is " + position);
                    if (position == arrayMarginDetailsValueItems.size() - 1)
                        return MyApplication.GRID_VALUES_SPAN_COUNT;
                    else return 1;
                }
            });
        } else {
            llUserData.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //Log.wtf("position" , "is " + position);
                    if (position == arrayMarginDetailsValueItems.size() - 1) return 1;
                    else return 1;
                }
            });
        }
        adapterMarginDetailsValues = new ValuesGridRecyclerAdapter(BalanceDetailsActivity.this, arrayMarginDetailsValueItems);
        rvMarginDetails.setAdapter(adapterMarginDetailsValues);
        rvMarginDetails.setNestedScrollingEnabled(false);
    }




    private class GetMarginPayments extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
            isCalled=true;
            arrayMarginPayments.clear();
            if(marginPaymentAdapter!=null)
               marginPaymentAdapter.notifyDataSetChanged();
    /*        arrayMarginPayments.clear();
            if(marginPaymentAdapter!=null)
                marginPaymentAdapter.notifyDataSetChanged();*/
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetMarginPayments.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();
            Log.wtf("balance_url",url);

            parameters.put("UserID", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("PortfolioID", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);

/*                arrayMarginPayments.add(new MarginPayment("1112.234","03/12/2020","10/12/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/12/2020","11/12/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/12/2020","01/12/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/12/2020","30/11/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/12/2020","29/11/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/11/2020","28/11/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/11/2020","27/11/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/11/2020","26/11/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/11/2020","25/11/2020"));
                arrayMarginPayments.add(new MarginPayment("2112.234","03/11/2020","24/11/2020"));*/
               arrayMarginPayments.addAll(GlobalFunctions.getMarginPayments(result));

            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
           // setBalanceDetailsData();
            loading.setVisibility(View.GONE);
            try{marginPaymentAdapter.notifyDataSetChanged();}catch (Exception e){}
            setTotalAmount();
            // setStockAlerts();

        }
    }

    private void setTotalAmount(){
        Double totalAmount=0.0;
        try{
        for (int i=0;i<arrayMarginPayments.size();i++){
            totalAmount+=Double.parseDouble(arrayMarginPayments.get(i).getAmounDue());
        }}catch (Exception e){}
        tvTotalAmount.setText(Actions.formatNumber(totalAmount,Actions.TwoDecimalThousandsSeparator));

    }


}
