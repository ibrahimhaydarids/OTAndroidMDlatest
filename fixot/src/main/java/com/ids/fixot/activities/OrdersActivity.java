package com.ids.fixot.activities;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.ids.fixot.adapters.OrderStatusAdapterSpinner;
import com.ids.fixot.adapters.OrdersRecyclerAdapter;
import com.ids.fixot.adapters.OrdersRecyclerAdapter.RefreshInterface;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderFilter;
import com.ids.fixot.model.SubAccount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import me.grantland.widget.AutofitHelper;

import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 4/4/2017.
 */

public class OrdersActivity extends AppCompatActivity implements OrdersRecyclerAdapter.RecyclerViewOnItemClickListener, RefreshInterface, MarketStatusListener, spItemListener {

    ImageView ivBack;
    Toolbar myToolbar;
    RecyclerView rvOrders;
    LinearLayoutManager llm;
    ProgressBar progressBar;
    OrdersRecyclerAdapter adapter;
    RelativeLayout rootLayout;
    Spinner spSubAccounts;
    Spinner spStatus;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    GetUserOrders getUserOrders;
    TextView tvSymbolHeader, tvPriceHeader, tvQuantityHeader, tvExecutedQuantityHeader, tvActionHeader, tvStatusHeader, tvLogout, tvInstruments;
    int ii = 0;
    private BroadcastReceiver receiver;
    private ArrayList<OnlineOrder> allOrders = new ArrayList<>();
    private ArrayList<OnlineOrder> allOrdersMerged = new ArrayList<>();
    private ArrayList<OnlineOrder> lastAllFilteredStock = new ArrayList<>();
    private boolean started = false;
    private boolean running = true;
    ArrayList<String> arrayFilers=new ArrayList<>();
    private boolean pause = false;
    ArrayList<String> arrayStockFilter=new ArrayList<>();

    ArrayList<OrderFilter> newArrayFilter=new ArrayList<>();

    private boolean isSelectedFilter=false;
    private boolean isSelectedFilterStock=false;
    private ImageView ivDescriptionStatusArrow;
    private Activity activity;
    Spinner spInstrumentsTop;
    private String selectedStockId="";
    public OrdersActivity() {
        LocalUtils.updateConfig(this);
    }

    LinearLayout linearStockFilterType,linearTotalResults;
    Spinner spStocks;
    ImageView ivStocksArrow;

    private int selectedStatusPosition=0;
    private int selectedStockPosition=0;
    private Boolean isCalled=false;
    private Boolean isFirsttime=true;
    private LinearLayout llItem;
    private TextView tvRemainingQuantityHeader;
    LinearLayout disableLayout;
    TextView tvTotalBuyTitle,tvTotalBuyValue,tvBuyAmountTitle,tvBuyAmountValue,tvTotalSellTitle,tvTotalSellValue,tvSellAmountTitle,tvSellAmountValue;


    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
    /*    try{ subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()*//*MyApplication.currentUser.getSubAccounts()*//*);
             spSubAccounts.setAdapter(subAccountsSpinnerAdapter);}catch (Exception e){}
        try{
            if(MyApplication.selectedSubAccount==null)
                spSubAccounts.setSelection(Actions.getDefaultSubPosition());



        }catch (Exception e){
            Log.wtf("selection_exceptipn",e.toString());
        }*/

        disableLayout=findViewById(R.id.disableLayout);
        if(Actions.getSubAccountOTCCount()==0 && Actions.getLastMarketId(getApplicationContext())== enums.MarketType.KWOTC.getValue()){
            disableLayout.setVisibility(View.VISIBLE);
        }else {
            disableLayout.setVisibility(View.GONE);
        }
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirsttime=true;

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));
         activity=this;
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_orders);
        Actions.initializeBugsTracking(this);

        disableLayout=findViewById(R.id.disableLayout);
        if(Actions.getSubAccountOTCCount()==0 && Actions.getLastMarketId(getApplicationContext())== enums.MarketType.KWOTC.getValue()){
            disableLayout.setVisibility(View.VISIBLE);
        }else {
            disableLayout.setVisibility(View.GONE);
        }


        MyApplication.userOrderTimesTamMap.put(MyApplication.marketID,"0");
        selectedStockId=getString(R.string.stock);
        findViews();

        Actions.initializeToolBar(getString(R.string.orders), OrdersActivity.this);

        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);

        started = true;

        Actions.setTypeface(new TextView[]{tvSymbolHeader, tvPriceHeader, tvInstruments, tvQuantityHeader, tvExecutedQuantityHeader,tvRemainingQuantityHeader, tvActionHeader, tvStatusHeader},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

        Actions.setTypeface(new TextView[]{tvTotalBuyTitle,tvBuyAmountTitle,tvTotalSellTitle,tvSellAmountTitle},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

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

    public void back(View v) {
        finish();
    }


    private void findViews() {

/*        OrderFilter o1=new OrderFilter(0,getString(R.string.all));
        OrderFilter o2=new OrderFilter(0,getString(R.string.order_filter_1));
        OrderFilter o3=new OrderFilter(0,getString(R.string.order_filter_2));
        OrderFilter o4=new OrderFilter(0,getString(R.string.order_filter_3));
        newArrayFilter=new ArrayList<>();
        newArrayFilter.add(o1);
        newArrayFilter.add(o2);
        newArrayFilter.add(o3);
        newArrayFilter.add(o4);*/



        tvTotalBuyTitle=findViewById(R.id.tvTotalBuyTitle);
        tvTotalBuyValue=findViewById(R.id.tvTotalBuyValue);
        tvBuyAmountTitle=findViewById(R.id.tvBuyAmountTitle);
        tvBuyAmountValue=findViewById(R.id.tvBuyAmountValue);
        tvTotalSellTitle=findViewById(R.id.tvTotalSellTitle);
        tvTotalSellValue=findViewById(R.id.tvTotalSellValue);
        tvSellAmountTitle=findViewById(R.id.tvSellAmountTitle);
        tvSellAmountValue=findViewById(R.id.tvSellAmountValue);
        tvRemainingQuantityHeader=findViewById(R.id.tvRemainingQuantityHeader);
        if(!BuildConfig.Enable_Markets)
            tvRemainingQuantityHeader.setVisibility(View.VISIBLE);

        spStatus=findViewById(R.id.spFilter);
        ivDescriptionStatusArrow=(ImageView)findViewById(R.id.ivDescriptionStatusArrow);
        ivDescriptionStatusArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spStatus.performClick();
            }
        });
        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);
        ivBack = myToolbar.findViewById(R.id.ivBack);
        linearTotalResults=findViewById(R.id.linearTotalResults);

        llItem=findViewById(R.id.llOrdersHeader);
        if(!BuildConfig.Enable_Markets) {
            llItem.setBackgroundColor(ContextCompat.getColor(this, R.color.portfolio_header));
            linearTotalResults.setBackgroundColor(ContextCompat.getColor(this, R.color.portfolio_header));
        }

        ivBack.setVisibility((BuildConfig.GoToMenu) ? View.VISIBLE : View.GONE);

        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(OrdersActivity.this));
        tvLogout.setVisibility((BuildConfig.GoToMenu) ? View.GONE : View.VISIBLE);


        tvInstruments = findViewById(R.id.tvInstruments);
        tvSymbolHeader = findViewById(R.id.tvSymbolHeader);

        tvPriceHeader = findViewById(R.id.tvPriceHeader);
        tvQuantityHeader = findViewById(R.id.tvQuantityHeader);
        tvExecutedQuantityHeader = findViewById(R.id.tvExecutedQuantityHeader);

        AutofitHelper.create(tvPriceHeader);
        AutofitHelper.create(tvQuantityHeader);
        AutofitHelper.create(tvExecutedQuantityHeader);
        AutofitHelper.create(tvRemainingQuantityHeader);
        if(!BuildConfig.Enable_Markets ){
            tvPriceHeader.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));
            tvQuantityHeader.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));
            tvExecutedQuantityHeader.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));
            tvRemainingQuantityHeader.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));

            tvTotalBuyTitle.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));
            tvBuyAmountTitle.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));
            tvTotalSellTitle.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));
            tvSellAmountTitle.setTextColor(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true )? ContextCompat.getColor(this, R.color.colorLight):ContextCompat.getColor(this, R.color.colorLightInv));

            Actions.autofitText(tvPriceHeader,tvQuantityHeader,tvExecutedQuantityHeader,tvRemainingQuantityHeader,tvTotalBuyTitle,tvBuyAmountTitle,tvTotalSellTitle,tvSellAmountTitle);
        }

        tvActionHeader = findViewById(R.id.tvActionHeader);
        tvStatusHeader = findViewById(R.id.tvStatusHeader);


        linearStockFilterType = findViewById(R.id.linearStockFilterType);
        spStocks = findViewById(R.id.spStocks);
        ivStocksArrow = findViewById(R.id.ivStocksArrow);
        if(BuildConfig.Enable_Markets) {
            linearStockFilterType.setVisibility(View.GONE);
            linearTotalResults.setVisibility(View.GONE);
            tvSymbolHeader.setVisibility(View.VISIBLE);
        }
        else {
            tvSymbolHeader.setVisibility(View.GONE);
            linearStockFilterType.setVisibility(View.VISIBLE);
            ivStocksArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spStocks.performClick();
                }
            });
            linearTotalResults.setVisibility(View.VISIBLE);
        }




        try {
            llm = new LinearLayoutManager(OrdersActivity.this);
            adapter = new OrdersRecyclerAdapter(OrdersActivity.this, allOrdersMerged, this, this);
            rvOrders.setLayoutManager(llm);
            rvOrders.setAdapter(adapter);
        } catch (Exception e) {
            Log.wtf("OrdersRecyclerAdapter error", "error = " + e.getMessage());
        }

        spSubAccounts = findViewById(R.id.spSubAccounts);
        ImageView ivUserSubAccount=findViewById(R.id.ivUserSubAccount);
        if(BuildConfig.Enable_Markets)
            ivUserSubAccount.setVisibility(View.GONE);
        else
            ivUserSubAccount.setVisibility(View.VISIBLE);

        try {
            subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);

            spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);

                    if (Actions.isNetworkAvailable(OrdersActivity.this)) {
                      if(!isFirsttime) {
                          isCalled=true;
                          Log.wtf("get_order", "1");
                          try {
                              getUserOrders.cancel(true);
                              MyApplication.threadPoolExecutor.getQueue().remove(getUserOrders);
                          } catch (Exception e) {
                              e.printStackTrace();
                              Log.wtf("Orders ex", e.getMessage());
                          }
                          allOrders.clear();
                          allOrdersMerged.clear();
                          try{ adapter.notifyDataSetChanged();}catch (Exception e){}
                          MyApplication.userOrderTimesTamMap.put(MyApplication.marketID,"0");

                          getUserOrders = new GetUserOrders();
                          getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
                      }
                    } else {
                        Actions.CreateDialog(OrdersActivity.this, getString(R.string.no_net), false, false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spSubAccounts.setSelection(returnAccountIndex(),false);
        }catch (Exception e){
            Log.wtf("selection_exceptipn2",e.toString());

                }

        if(!isCalled) {
            isCalled=true;
            Log.wtf("get_order","2");
            allOrders.clear();
            allOrdersMerged.clear();
            MyApplication.userOrderTimesTamMap.put(MyApplication.marketID,"0");
            try{ adapter.notifyDataSetChanged();}catch (Exception e){}
            getUserOrders = new GetUserOrders();
            getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
        }


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
    protected void onStop() {
        super.onStop();
        running = false;
        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    public void refreshData() {
        Log.wtf("refresh_data","refresh_data");
        Log.wtf("get_order","3");
        try {
            getUserOrders.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getUserOrders);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Orders ex", e.getMessage());
        }
        allOrders.clear();
        allOrdersMerged.clear();
        MyApplication.userOrderTimesTamMap.put(MyApplication.marketID,"0");
        try{ adapter.notifyDataSetChanged();}catch (Exception e){}
        getUserOrders = new GetUserOrders();
        getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
    }

    @Override
    public void onItemClicked(View v, int position) {
        Log.wtf("click","click");

        Bundle b = new Bundle();
        b.putParcelable("order", allOrdersMerged.get(position));
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(OrdersActivity.this, OrderDetailsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Actions.checkSession(this);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);

        Actions.checkLanguage(this, started);
        running = true;
        Log.wtf("Orders Activity OnResume", "Actions.isNetworkAvailable(this) : " + Actions.isNetworkAvailable(this) + " / running = " + running);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}
        try{spSubAccounts.setSelection(returnAccountIndex(),false);}catch (Exception e){
            Log.wtf("selection_exceptipn3",e.toString());
        }
       if(pause){
           allOrders.clear();
           allOrdersMerged.clear();
           MyApplication.userOrderTimesTamMap.put(MyApplication.marketID,"0");
           try{ adapter.notifyDataSetChanged();}catch (Exception e){}
           getUserOrders = new GetUserOrders();
           getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
           pause=false;
       }

    }


    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        pause=true;
        MyApplication.sessionOut = Calendar.getInstance();
    }

 /*   @Override
    public void onBackPressed() {
        if (BuildConfig.GoToMenu) {
            super.onBackPressed();
        }

    }*/
 @Override
 public void onBackPressed() {
     Actions.exitApp(this);
 }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Actions.unregisterSessionReceiver(this);
        try {
            getUserOrders.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getUserOrders);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Orders ex", e.getMessage());
        }
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class GetUserOrders extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            Log.wtf("get_order","get_order");
            isCalled=true;
            isFirsttime=false;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetUserOrders.getValue(); // this method uses key after login

         while (running) {

                Log.wtf("Thread", "runnig " + ii);
                ii++;
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
                parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
                parameters.put("key", MyApplication.currentUser.getKey());
                parameters.put("Lang", Actions.getLanguage());
                parameters.put("MarketID",MyApplication.marketID);
                parameters.put("Tstamp", MyApplication.userOrderTimesTamMap.get(MyApplication.marketID));

                try {
                    result = ConnectionRequests.GET(url, OrdersActivity.this, parameters);
                    publishProgress(result);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetUserOrders.getKey(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                try {

                    Thread.sleep(1000);
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

           /*     if(BuildConfig.expanded_order)
                   allOrders.clear();*/
                ArrayList<OnlineOrder> retrievedOrders = GlobalFunctions.GetUserOrders(values[0]);

                try{checkExecuted(retrievedOrders);}catch (Exception e){}

                allOrders.addAll(0,retrievedOrders);
                try {
                   // if(ii<6)
                       setMergedArray();
                    Log.wtf("merge_array", allOrdersMerged.size() + "aaaa");
                }catch (Exception  e){
                    Log.wtf("exception",e.toString());
                }


                try {

                        if(!BuildConfig.Enable_Markets)
                          setfilterStockArray();
                }catch (Exception e){

                }

                try {

                      setfilterArray();
                }catch (Exception e){

                }

                if(!BuildConfig.Enable_Markets){
                    try{setTotalsData();}catch (Exception e){ Log.wtf("exception",e.toString()); }
                }


              //  try{adapter.notifyDataSetChanged();}catch (Exception e){}
/*                try{
               runOnUiThread(() ->


                );}catch (Exception e){}*/



                Log.wtf("allOrders", "ss " + retrievedOrders.size());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void checkExecuted(ArrayList<OnlineOrder> orders){
        int position=-1;
        outerloop:
        for(int i=0;i<allOrders.size();i++){
            for(int j=0;j<orders.size();j++){
               if(allOrders.get(i).getId()==orders.get(j).getId()){
                   position=i;
                   break outerloop;

               }
            }

        }
        Log.wtf("position",position+"");
        if(position!=-1){
            allOrders.remove(position);
        }
    }

    private void setMergedArray(){
        allOrdersMerged.clear();

        if(!BuildConfig.expanded_order){
        for (int i=0;i<allOrders.size();i++){
           if(allOrders.get(i).getRelatedOnlineOrderID()==0/* || (allOrders.get(i).getRelatedOnlineOrderID()>0 && allOrders.get(i).getRelatedOnlineOrderID()==allOrders.get(i).getID()) */)/*countId(allOrders.get(i).getRelatedOnlineOrderID())>1*/{
               allOrdersMerged.add(allOrders.get(i));
           }
        }
            checkMergeArray();
        }else {


            for (int i = 0; i < allOrders.size(); i++) {
                if (allOrders.get(i).getRelatedOnlineOrderID() == 0) {
                    allOrdersMerged.add(allOrders.get(i));
                    for (int j=0;j<allOrders.size();j++){
                        if(allOrders.get(j).getRelatedOnlineOrderID()!=0 && allOrders.get(i).getId()==allOrders.get(j).getRelatedOnlineOrderID() ){
                           if(!allOrdersMerged.get(allOrdersMerged.size()-1).getArraySubOrders().contains(allOrders.get(j)))
                              allOrdersMerged.get(allOrdersMerged.size()-1).getArraySubOrders().add(allOrders.get(j));
                        }

                    }
                }

            }


        }




    }
    
    
    private void setTotalsData(){
        Double totalSellQty=0.0;
        Double totalSellAmount=0.0;
        Double totalBuyQty=0.0;
        Double totalBuyAmount=0.0;

        for(int i=0;i<allOrders.size();i++)
        {
            if (allOrders.get(i).getQuantityExecuted() > 0)
            {
                if (Integer.parseInt(allOrders.get(i).getTradeID()) == MyApplication.ORDER_SELL)
                {
                    totalSellQty += Double.parseDouble(allOrders.get(i).getQuantityExecuted()+"");
                    totalSellAmount += Double.parseDouble((allOrders.get(i).getQuantityExecuted() * allOrders.get(i).getAveragePrice())+"");
                }
                else
                {
                    totalBuyQty += Double.parseDouble(allOrders.get(i).getQuantityExecuted()+"");
                    totalBuyAmount += Double.parseDouble((allOrders.get(i).getQuantityExecuted() * allOrders.get(i).getAveragePrice())+"");
                }
            }
        }


        tvTotalBuyValue.setText(totalBuyQty+"");
        tvBuyAmountValue.setText(totalBuyAmount+"");
        tvTotalSellValue.setText(totalSellQty+"");
        tvSellAmountValue.setText(totalSellAmount+"");

        if(!BuildConfig.Enable_Markets){
            tvTotalSellValue.setTextColor(ContextCompat.getColor(this, R.color.bid_quanity_value_color));
            tvSellAmountValue.setTextColor(ContextCompat.getColor(this, R.color.bid_quanity_value_color));


            tvTotalBuyValue.setTextColor(ContextCompat.getColor(this, R.color.blue_text_color));
            tvBuyAmountValue.setTextColor(ContextCompat.getColor(this, R.color.blue_text_color));


        }
    }

    private int countId(int id){
        int count=0;
        for (int i=0;i<allOrders.size();i++){
            if(allOrders.get(i).getID()==id)
                count++;
            if(allOrders.get(i).getRelatedOnlineOrderID()==id)
                count++;
        }
        return count;
    }


    private void setfilterArray(){
/*       if(!containsFilter(getString(R.string.all)))
           arrayFilers.add(getString(R.string.all));
        for(int i=0;i<allOrders.size();i++){
            if(!containsFilter(allOrders.get(i).getStatusDescription()))
                arrayFilers.add(allOrders.get(i).getStatusDescription());
        }*/
        arrayFilers=new ArrayList<>();
        arrayFilers.add(getString(R.string.all));
        arrayFilers.add(getString(R.string.order_filter_1));
        arrayFilers.add(getString(R.string.order_filter_2));
        arrayFilers.add(getString(R.string.order_filter_3));
        setSpinner();
    }

    private void setfilterStockArray(){

        arrayStockFilter=new ArrayList<>();
        arrayStockFilter.add(getString(R.string.stock));
        arrayStockFilter.addAll(getFiltereStocks());
        setStockSpinner();
    }







    private boolean containsFilter(String statusDescription){
        boolean isContain=false;
        for (int i=0;i<arrayFilers.size();i++){
            if(arrayFilers.get(i).matches(statusDescription))
                isContain=true;
        }
        return isContain;
    }

    private void setSpinner(){

        OrderStatusAdapterSpinner StatusAdapter=new OrderStatusAdapterSpinner(this,arrayFilers);
        spStatus.setAdapter(StatusAdapter);


        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSelectedFilter = false;
                String selectedStatus=arrayFilers.get(position);
                selectedStatusPosition=position;
                filterByStockAndOrder(selectedStatusPosition,selectedStockId);
                adapter.notifyDataSetChanged();


              }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spStatus.setSelection(selectedStatusPosition,false);



    }




    private void setStockSpinner(){

            OrderStatusAdapterSpinner StatusAdapter=new OrderStatusAdapterSpinner(this,arrayStockFilter);
            spStocks.setAdapter(StatusAdapter);
            spStocks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    isSelectedFilterStock = false;
                    selectedStockPosition=position;
                    if(position==0)
                        selectedStockId=getString(R.string.stock);
                    else
                       selectedStockId=arrayStockFilter.get(position).substring(arrayStockFilter.get(position).lastIndexOf("-")+1);
                    filterByStockAndOrder(selectedStatusPosition,selectedStockId);
                    adapter.notifyDataSetChanged();


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spStocks.setSelection(selectedStockPosition,false);

        }


        private ArrayList<String> getFiltereStocks(){
        ArrayList<String> arrayFilteredStock=new ArrayList<>();
        for (int i=0;i<allOrdersMerged.size();i++){
            if(!arrayFilteredStock.contains(allOrdersMerged.get(i).getStockSymbol()+"-"+allOrdersMerged.get(i).getStockID()))
                arrayFilteredStock.add(allOrdersMerged.get(i).getStockSymbol()+"-"+allOrdersMerged.get(i).getStockID());
        }

        return arrayFilteredStock;


        }



        private void checkMergeArray(){
        try{
        ArrayList arrayNew=new ArrayList();
           for(int i=0;i<allOrdersMerged.size();i++){
               if(!arrayNew.contains(allOrdersMerged.get(i)) || hasId(arrayNew,allOrdersMerged.get(i).getId()))
                   arrayNew.add(allOrdersMerged.get(i));
           }

           allOrdersMerged.clear();
           allOrdersMerged.addAll(arrayNew);
        }catch (Exception e){}
    }

    private boolean hasId(ArrayList<OnlineOrder> array,int id){
        Boolean available=false;
        for(int i=0;i<array.size();i++){
            if(array.get(i).getId()==id) {
                available = true;
                break;
            }
        }

        return available;

    }



    private void filterByStockAndOrder(int indexOrdrStatus, String stockId){

            allOrdersMerged.clear();
            setMergedArray();
            ArrayList<OnlineOrder> arrayFilteredStock=new ArrayList<>();
            ArrayList<OnlineOrder> arrayFilteredStatus=new ArrayList<>();
            ArrayList<OnlineOrder> arrayFiltered=new ArrayList<>();

            Log.wtf("filter_order_status",indexOrdrStatus+"...");
            Log.wtf("filter_order_stockId",stockId+"...");
            Log.wtf("filter_allmerged_size_before",allOrdersMerged.size()+"...");


          if(!BuildConfig.Enable_Markets) {

              if (stockId.matches(getString(R.string.stock))) {
                  arrayFilteredStock.addAll(allOrdersMerged);
              } else {
                  for (int i = 0; i < allOrdersMerged.size(); i++) {
                      if (allOrdersMerged.get(i).getStockID().matches(stockId))
                          arrayFilteredStock.add(allOrdersMerged.get(i));
                  }

              }
              //
              Log.wtf("filter_array_filter_stock", arrayFilteredStock.size() + "...");

              if (indexOrdrStatus != 0) {

                  for (int i = 0; i < arrayFilteredStock.size(); i++) {
                      if (arrayFilteredStock.get(i).getOrderStatusTypeID() == indexOrdrStatus)
                          arrayFilteredStatus.add(arrayFilteredStock.get(i));
                  }

                  arrayFiltered.addAll(arrayFilteredStatus);
              } else
                  arrayFiltered.addAll(arrayFilteredStock);
          }else {
              arrayFiltered.clear();
              if (indexOrdrStatus != 0) {

                  for (int i = 0; i < allOrdersMerged.size(); i++) {
                      if (allOrdersMerged.get(i).getOrderStatusTypeID() == indexOrdrStatus)
                          arrayFilteredStatus.add(allOrdersMerged.get(i));
                  }

                  arrayFiltered.addAll(arrayFilteredStatus);
              } else
                  arrayFiltered.addAll(allOrdersMerged);

          }

        Log.wtf("order_duplicate_before",arrayFiltered.size()+"aaa");

          ArrayList<OnlineOrder> lastArray=new ArrayList<>();
          for (int i=0;i<arrayFiltered.size();i++){
              Boolean isInList=false;
              for (int j=0;j<lastArray.size();j++){
                  if(lastArray.get(j).getID()==arrayFiltered.get(i).getID()){
                      isInList=true;
                      break;
                  }
              }
              if(!isInList)
                  lastArray.add(arrayFiltered.get(i));

          }
        Log.wtf("order_duplicate_after",lastArray.size()+"aaa");
                allOrdersMerged.clear();
                allOrdersMerged.addAll(lastArray);
                Log.wtf("filter_allmerged_size_after",allOrdersMerged.size()+"aaa");



    }

}
