package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.ids.fixot.adapters.InstrumentsRecyclerAdapter;
import com.ids.fixot.adapters.MarketsSpinnerAdapter;
import com.ids.fixot.adapters.StockQuotationRecyclerAdapter;
import com.ids.fixot.adapters.StockQuotationRecyclerNewAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.enums.enums.TradingSession;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.stockQuotationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class StockActivity extends AppCompatActivity implements InstrumentsRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener, spItemListener {

    FrameLayout rlStockSearch;
    RelativeLayout rootLayout;
    RecyclerView rvStocks;
    ArrayList<StockQuotation> allStocks = new ArrayList<>();
    ArrayList<StockQuotation> tmpStocks = new ArrayList<>();
    String sectorId = "12";
    String instrumentId = "";
    Instrument selectedInstrument = new Instrument();
    GetInstruments getInstruments;
    boolean firstTabClick = true;
    EditText etSearch;
    Button btClear;
    LinearLayoutManager llm;
    RecyclerView rvInstruments;
    InstrumentsRecyclerAdapter instrumentsRecyclerAdapter;
    Spinner spMarkets;
    MarketsSpinnerAdapter marketsSpinnerAdapter;
    TradingSession selectMarket = TradingSession.All;
    ArrayList<TradingSession> AllMarkets = new ArrayList<>();
    ArrayList<Instrument> marketInstruments = new ArrayList<>();
    ArrayList<Instrument> filteredmarketInstruments = new ArrayList<>();
    ArrayList<Instrument> allInstruments = new ArrayList<>();
    Boolean isSelectInstrument = false;
    TextView tvStock,tvStockGig, tvSessionName, tvPrice, tvChange;
    TextView sectorTitle;
    boolean isIslamic = false;
    private BroadcastReceiver receiver;
    private StockQuotationRecyclerNewAdapter adapter;
    private boolean running = true, firstTime = true;
    private boolean started = false;
    Spinner spInstrumentsTop;
    private boolean isBond=false;

    private TextView tvColumn1Title,tvColumn2Title,tvColumn3Title,tvColumn4Title,tvColumn5Title,tvColumn6Title;
    private LinearLayout linearHeaderFilters,linearFilterAllStocks,linearFilterActive,linearFilterGainer,linearFilterLosers,linearFilterUnchanged;
    private TextView tvAllStockCount,tvAllStocksTitle,tvActiveCount,tvActiveTitle,tvGainerCount,tvGainerTitle,tvLooserCount,tvLoosersTitle,tvUnchangedCount,tvUnchangedTitle;
    LinearLayout llTab;
    LinearLayout linearStockHeader,linearStockHeaderGig;

    GetBonds mGetBonds;

    TextView tvBidQty,tvBidPrice,tvLastPrice,tvAskPrice,tvAskQty;
    public StockActivity() {
        LocalUtils.updateConfig(this);
    }
    private ArrayList<StockQuotation> changedStocks = new ArrayList<>();
    
    private boolean isFilterAll=true;
    private boolean isFilterActive=false;
    private boolean isFilterGainers=false;
    private boolean isFilterLoosers=false;
    private boolean isFilterUnchanged=false;



    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {


            selectMarket = TradingSession.All;
            MyApplication.instrumentId = "";
            isSelectInstrument = false;

        try{adapter.notifyDataSetChanged();}catch (Exception e){}
        filteredmarketInstruments.clear();
        for (int i=0;i<marketInstruments.size();i++)
            marketInstruments.get(i).setIsSelected(false);
        filteredmarketInstruments=Actions.filterMarketInstruments(marketInstruments);
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(StockActivity.this, filteredmarketInstruments, StockActivity.this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);
        //MyApplication.instrumentId = "";
        checkspMarket();
        try{ spMarkets.setSelection(0,false);}catch (Exception e){}
        retrieveFiltered(getIntent().hasExtra("sectorId"));

        try {
            if (MyApplication.stockTimesTampMap.get(MyApplication.marketID).matches("0"))
                MyApplication.appServiceFirstTime = true;
        }catch (Exception e){
            Log.wtf("exception_appservice",e.toString());
          //  Toast.makeText(getApplicationContext(),"asdsad",Toast.LENGTH_LONG).show();
        }


       try{instrumentsRecyclerAdapter.notifyDataSetChanged();}catch (Exception e){}

        final int pos = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    if (MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {
                        if(!instrumentsRecyclerAdapter.getAllInstruments().get(0).getIsSelected())
                            rvInstruments.findViewHolderForAdapterPosition(pos).itemView.performClick();
                    }
                    }catch (Exception e){

                }
            }
        },200);
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


        MyApplication.expandedStock=new ArrayList<>();
        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_stocks);
        Actions.initializeBugsTracking(this);

        if (getIntent().hasExtra("isIslamicStocks")) {
            isIslamic = true;
        }

        if (getIntent().hasExtra("isBond")) {
            isBond = true;
        }

        if (MyApplication.lang == MyApplication.ARABIC) {
            AllMarkets.add(TradingSession.All_ar);
            if(!BuildConfig.Enable_Markets)
                AllMarkets.add(TradingSession.NM_ar);
            else {
               AllMarkets.add(TradingSession.REG_ar);
               AllMarkets.add(TradingSession.FUNDS_ar);
            }

        } else {
            AllMarkets.add(TradingSession.All);
            if(!BuildConfig.Enable_Markets)
                AllMarkets.add(TradingSession.NM);
            else {
                AllMarkets.add(TradingSession.REG);
                AllMarkets.add(TradingSession.FUNDS);
            }
        }

        MyApplication.instrumentId = "";
        findViews();
        MyApplication.bondsTimesTamp="0";
        MyApplication.bondsTimesTampMap.put(MyApplication.marketID,"0");
        if (getIntent().hasExtra("sectorId")) {

            MyApplication.stockTimesTamp = "0";

            MyApplication.stockTimesTampMap.put(MyApplication.marketID,"0");

            sectorId = getIntent().getExtras().getString("sectorId");
            Actions.initializeToolBar(getIntent().getExtras().getString("sectorName"), StockActivity.this);
            // sectorTitle.setText(getIntent().getExtras().getString("sectorName"));
            allStocks.clear();
            allStocks.addAll(Actions.filterStocksBySectorAndInstrumentID(MyApplication.stockQuotations, instrumentId, sectorId));
            Log.wtf("hasExtra sectorId", ": " + sectorId + " , allStocks count = " + allStocks.size());

        } else {

            //  sectorTitle.setVisibility(View.GONE);

            if (!Actions.isNetworkAvailable(this)) {

                Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            }

            Actions.initializeToolBar(getString(R.string.stock), StockActivity.this);
            allStocks.addAll(MyApplication.stockQuotations);
            Log.wtf("no sectorId", ": allStocks count = " + allStocks.size());

            if (getIntent().hasExtra("isIslamicStocks")) {
                isIslamic = true;
                tmpStocks = Actions.filterStocksByIsIslamic(allStocks);
                allStocks.clear();
                allStocks.addAll(tmpStocks);
            }
        }

        tmpStocks = allStocks;
        allStocks.clear();
        /*for(int i=0; i<marketInstruments.size(); i++) {
            allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, marketInstruments.get(i).getInstrumentCode() ));
        }*/
        allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, filteredmarketInstruments));

        started = true;
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);
        //   sectorTitle.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        adapter = new StockQuotationRecyclerNewAdapter(this, allStocks);//, this);
        rvStocks.setAdapter(adapter);

        if(!isBond){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (running) {

                            tmpStocks = new ArrayList<>();
                            Log.wtf("LocalBroadcastManager", "before allStocks size = " + allStocks.size());
                            allStocks.clear();
                            if (getIntent().hasExtra("sectorId")) {

                                tmpStocks.addAll(Actions.filterStocksBySectorAndInstrumentID(MyApplication.stockQuotations, instrumentId, sectorId));
                            } else {

                                MyApplication.stockQuotations = intent.getExtras().getParcelableArrayList(AppService.EXTRA_STOCK_QUOTATIONS_LIST);
                                tmpStocks.addAll(Actions.filterStocksByInstrumentID(MyApplication.stockQuotations, MyApplication.instrumentId));
                            }

                            if (isSelectInstrument) {

                                allStocks.addAll(tmpStocks);
                            } else {

                                if (selectMarket.getValue() == TradingSession.All.getValue()) {
                                    allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, allInstruments));
                                } else {
                                    allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, filteredmarketInstruments));
                                }
                            }

                            if (isIslamic) {
                                tmpStocks = Actions.filterStocksByIsIslamic(allStocks);
                                allStocks.clear();
                                allStocks.addAll(tmpStocks);
                            }




                            checkFilter();


                            //tmpStocks = new ArrayList<>();
                            //tmpStocks.addAll(allStocks);
                            //allStocks = new ArrayList<>();
                            //allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, marketInstruments));

                            try {
                                Log.wtf("allStocks", "count = " + allStocks.size());


                                Log.wtf("stock_all",allStocks.size()+"");


                                Log.wtf("LocalBroadcastManager", "after allStocks size = " + allStocks.size());
                                if (etSearch.length() > 0) {
                                    adapter.getFilter().filter(etSearch.getText().toString());
                                }
                                Log.wtf("allStocks in adapter", "count = " + adapter.getFilteredItems().size());
                                adapter.notifyDataSetChanged();
                                setFilters();
                            }catch (Exception e){}


                            //rvStocks.setAdapter(adapter);
                        }
                    }
                }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
        );
        }else {
            mGetBonds = new GetBonds();
            mGetBonds.executeOnExecutor(MyApplication.threadPoolExecutor);
        }

        //<editor-fold desc="instruments section">
        if (MyApplication.instruments.size() < 2) { //being empty or only having the fake entry
            Actions.initializeInstruments(this);
            getInstruments = new GetInstruments();
            getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            allInstruments.clear();

         //   if (!MyApplication.isOTC) {
             //   for (int i = 1; i < AllMarkets.size(); i++) {
              //      allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
            //    }
          //  } else {
                allInstruments.addAll(MyApplication.instruments);
         //   }
        }
        //</editor-fold>

        Actions.setTypeface(new TextView[]{tvStock,tvStockGig, tvSessionName, tvPrice, tvChange,tvBidQty,tvBidPrice,tvLastPrice,tvAskPrice,tvAskQty,tvColumn1Title,tvColumn2Title,tvColumn3Title,tvColumn4Title,tvColumn5Title,tvColumn6Title}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        Actions.setTypeface(new TextView[]{tvAllStockCount, tvAllStocksTitle, tvActiveCount, tvActiveTitle, tvGainerCount, tvGainerTitle, tvLooserCount, tvLoosersTitle, tvUnchangedCount, tvUnchangedTitle}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);






        tvSessionName.setGravity(MyApplication.lang == MyApplication.ARABIC ? Gravity.LEFT : Gravity.RIGHT);


        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
            if(getIntent().hasExtra("sectorId"))
                spInstrumentsTop.setVisibility(View.GONE);
            else
               Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }


        final int pos = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               try{
                   if (MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {
                       if (!instrumentsRecyclerAdapter.getAllInstruments().get(0).getIsSelected())
                           rvInstruments.findViewHolderForAdapterPosition(pos).itemView.performClick();
                   }
                   }catch (Exception e){

               }
            }
        },300);


        filterListeners();

    }



    private void resetHeaders(){
        MyApplication.STOCK_COLUMN_1 = 1;
        MyApplication.STOCK_COLUMN_2 = 1;
        MyApplication.STOCK_COLUMN_3 = 1;
        MyApplication.STOCK_COLUMN_4 = 1;
        MyApplication.STOCK_COLUMN_5 = 1;
    }



    private void retrieveFiltered(boolean hasSectorId) {
        tmpStocks = new ArrayList<>();
        allStocks = new ArrayList<>();

        if (hasSectorId) {

            tmpStocks = (Actions.filterStocksBySectorAndInstrumentID(MyApplication.stockQuotations, MyApplication.instrumentId, sectorId));
        } else {

            tmpStocks = (Actions.filterStocksByInstrumentID(MyApplication.stockQuotations, MyApplication.instrumentId));
        }

        if (isSelectInstrument) {

            allStocks.addAll(tmpStocks);
        } else {

            if (selectMarket.getValue() == TradingSession.All.getValue()) {
                /*for(int i=0; i<allInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, allInstruments.get(i).getInstrumentCode() ));
                }*/
                allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, allInstruments));
            } else {
                /*for(int i=0; i<marketInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, marketInstruments.get(i).getInstrumentCode() ));
                }*/
                allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, filteredmarketInstruments));
            }
        }

        if (isIslamic) {

            tmpStocks = Actions.filterStocksByIsIslamic(allStocks);
            allStocks.clear();
            allStocks.addAll(tmpStocks);
        }

        if (etSearch.length() > 0) {
            allStocks = FilterStocks(allStocks);
            //adapter.getFilter().filter(etSearch.getText().toString());
        }

         checkFilter();

        Log.wtf("bob_allstocks",allStocks.size()+"rr");
        adapter = new StockQuotationRecyclerNewAdapter(StockActivity.this, allStocks);
        rvStocks.setAdapter(adapter);






        Log.wtf("on instr click", "allStocks count = " + allStocks.size());

        //rvStocks.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Actions.InitializeMarketServiceV2(this);
        Actions.startStockQuotationService(StockActivity.this);
        running = true;

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

        //Actions.InitializeSessionService(this);
        //Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        Actions.unregisterSessionReceiver(this);
        try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        Actions.unregisterSessionReceiver(this);
        Actions.unregisterMarketReceiver(this);
        try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }
        MyApplication.instrumentId = "";
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back(View v) {
        this.finish();
    }

    public void loadFooter(View v) {
        Actions.loadFooter(this, v);
    }

    private void checkspMarket() {
        LinearLayout vs = findViewById(R.id.spMarketLayout);
        if (MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {
            ViewGroup.LayoutParams params = vs.getLayoutParams();
            params.width =0;
            vs.setVisibility(View.INVISIBLE);
        }else {
            ViewGroup.LayoutParams params = vs.getLayoutParams();
            params.width =ViewGroup.LayoutParams.WRAP_CONTENT;;
            vs.setVisibility(View.VISIBLE);

        }
    }

    private void setHeader(){
         MyApplication.STOCK_COLUMN_1 = MyApplication.STOCK_TYPE_STOCK_SYMBOL;
         setTitles();


        tvColumn1Title.setOnClickListener(v->{
            if(MyApplication.STOCK_COLUMN_1 == MyApplication.STOCK_TYPE_STOCK_NAME){
               MyApplication.STOCK_COLUMN_1= MyApplication.STOCK_TYPE_STOCK_SYMBOL;
            }else {
                MyApplication.STOCK_COLUMN_1= MyApplication.STOCK_TYPE_STOCK_NAME;
            }
            setTitles();
            try{adapter.notifyDataSetChanged();}catch (Exception e){}
        });

        tvColumn2Title.setOnClickListener(v->{


            if(MyApplication.STOCK_COLUMN_2 == MyApplication.STOCK_TYPE_BID_PRICE){
                MyApplication.STOCK_COLUMN_2= MyApplication.STOCK_TYPE_BID_VOLUME;
            }else {
                MyApplication.STOCK_COLUMN_2= MyApplication.STOCK_TYPE_BID_PRICE;
            }
            setTitles();
            try{adapter.notifyDataSetChanged();}catch (Exception e){}
        });


        tvColumn3Title.setOnClickListener(v->{
            if(MyApplication.STOCK_COLUMN_3 == MyApplication.STOCK_TYPE_CURRENT_PRICE){
                MyApplication.STOCK_COLUMN_3= MyApplication.STOCK_TYPE_LOW_PRICE;
            }
            else if(MyApplication.STOCK_COLUMN_3 == MyApplication.STOCK_TYPE_LOW_PRICE){
                MyApplication.STOCK_COLUMN_3= MyApplication.STOCK_TYPE_HIGH_PRICE;
            }
            else if(MyApplication.STOCK_COLUMN_3 == MyApplication.STOCK_TYPE_HIGH_PRICE){
               // MyApplication.STOCK_COLUMN_3= MyApplication.STOCK_TYPE_IMP;
                MyApplication.STOCK_COLUMN_3= MyApplication.STOCK_TYPE_CURRENT_PRICE;
            }
         /*   else  if(MyApplication.STOCK_COLUMN_3 == MyApplication.STOCK_TYPE_IMP) {
                MyApplication.STOCK_COLUMN_3= MyApplication.STOCK_TYPE_CURRENT_PRICE;
            }*/
            setTitles();
            try{adapter.notifyDataSetChanged();}catch (Exception e){}
        });



        tvColumn4Title.setOnClickListener(v->{
            if(MyApplication.STOCK_COLUMN_4 == MyApplication.STOCK_TYPE_OFFER_PRICE){
                MyApplication.STOCK_COLUMN_4= MyApplication.STOCK_TYPE_OFFER_VOLUME;
            }else {
                MyApplication.STOCK_COLUMN_4= MyApplication.STOCK_TYPE_OFFER_PRICE;
            }
            setTitles();
            try{adapter.notifyDataSetChanged();}catch (Exception e){}
        });

        tvColumn5Title.setOnClickListener(v->{
            if(MyApplication.STOCK_COLUMN_5 == MyApplication.STOCK_TYPE_CHANGE_PER){
                MyApplication.STOCK_COLUMN_5= MyApplication.STOCK_TYPE_CHANGE;
            }else {
                MyApplication.STOCK_COLUMN_5= MyApplication.STOCK_TYPE_CHANGE_PER;
            }
            setTitles();
            try{adapter.notifyDataSetChanged();}catch (Exception e){}
        });
    }

    private void setTitles(){
        if(MyApplication.STOCK_COLUMN_1==MyApplication.STOCK_TYPE_STOCK_NAME){
            tvColumn1Title.setText(getString(R.string.symbol));
        }else {
            tvColumn1Title.setText(getString(R.string.stocks_name));
        }

        if(MyApplication.STOCK_COLUMN_2==MyApplication.STOCK_TYPE_OFFER_PRICE){
            tvColumn2Title.setText(getString(R.string.bid_price));
        }else {
            tvColumn2Title.setText(getString(R.string.bid_quantity));
        }



        if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_CURRENT_PRICE){
            tvColumn3Title.setText(getString(R.string.last_price_title));
        }

        else if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_LOW_PRICE){
            tvColumn3Title.setText(getString(R.string.low));
        }else if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_HIGH_PRICE){
            tvColumn3Title.setText(getString(R.string.high));
        }

        /*else  if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_IMP){
            tvColumn3Title.setText(getString(R.string.theoretical_price));
        }*/



        if(MyApplication.STOCK_COLUMN_4==MyApplication.STOCK_TYPE_BID_PRICE){
            tvColumn4Title.setText(getString(R.string.ask_price));
        }else {
            tvColumn4Title.setText(getString(R.string.ask_quantity));
        }


        if(MyApplication.STOCK_COLUMN_5==MyApplication.STOCK_TYPE_CHANGE_PER){
            tvColumn5Title.setText(getString(R.string.change_percent));
        }else {
            tvColumn5Title.setText(getString(R.string.change));
        }
    }

    public void findViews() {
        resetHeaders();
        linearHeaderFilters = findViewById(R.id.linearHeaderFilters);
        linearFilterAllStocks = findViewById(R.id.linearFilterAllStocks);
        linearFilterActive = findViewById(R.id.linearFilterActive);
        linearFilterGainer = findViewById(R.id.linearFilterGainer);
        linearFilterLosers = findViewById(R.id.linearFilterLosers);
        linearFilterUnchanged = findViewById(R.id.linearFilterUnchanged);
        tvAllStockCount = findViewById(R.id.tvAllStockCount);
        tvAllStocksTitle = findViewById(R.id.tvAllStocksTitle);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvActiveTitle = findViewById(R.id.tvActiveTitle);
        tvGainerCount = findViewById(R.id.tvGainerCount);
        tvGainerTitle = findViewById(R.id.tvGainerTitle);
        tvLooserCount = findViewById(R.id.tvLooserCount);
        tvLoosersTitle = findViewById(R.id.tvLoosersTitle);
        tvUnchangedCount = findViewById(R.id.tvUnchangedCount);
        tvUnchangedTitle = findViewById(R.id.tvUnchangedTitle);


        llTab = findViewById(R.id.llTab);
        if(BuildConfig.Enable_Markets) {
            llTab.setVisibility(View.VISIBLE);

        }
        else {
            llTab.setVisibility(View.GONE);

        }



        tvColumn1Title = findViewById(R.id.tvColumn1Title);
        tvColumn2Title = findViewById(R.id.tvColumn2Title);
        tvColumn3Title = findViewById(R.id.tvColumn3Title);
        tvColumn4Title = findViewById(R.id.tvColumn4Title);
        tvColumn5Title = findViewById(R.id.tvColumn5Title);
        tvColumn6Title = findViewById(R.id.tvColumn6Title);

        tvBidQty = findViewById(R.id.tvBidQty);
        tvBidPrice = findViewById(R.id.tvBidPrice);
        tvLastPrice = findViewById(R.id.tvLastPrice);
        tvAskPrice = findViewById(R.id.tvAskPrice);
        tvAskQty = findViewById(R.id.tvAskQty);

        rlStockSearch = findViewById(R.id.rlStockSearch);
        rootLayout = findViewById(R.id.rootLayout);
        rvStocks = findViewById(R.id.rvStocks);



        checkspMarket();
        ImageView iv_arrow = findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spMarkets.performClick();
            }
        });


        rvInstruments = findViewById(R.id.RV_instrument);
        spMarkets = findViewById(R.id.spMarket);
        marketsSpinnerAdapter = new MarketsSpinnerAdapter(this, AllMarkets, true);
        spMarkets.setAdapter(marketsSpinnerAdapter);
        spMarkets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMarket = AllMarkets.get(position);
                MyApplication.instrumentId = "";
                isSelectInstrument = false;

               if (selectMarket.getValue() == TradingSession.All.getValue() || MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {
                    marketInstruments = allInstruments;
                    filteredmarketInstruments=Actions.filterMarketInstruments(allInstruments);
              } else {
                   marketInstruments = Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, selectMarket.getValue());
                   filteredmarketInstruments = Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, selectMarket.getValue());

               }

                for (Instrument inst : filteredmarketInstruments) {
                    inst.setIsSelected(false);
                }

                instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(StockActivity.this, filteredmarketInstruments, StockActivity.this);
                rvInstruments.setAdapter(instrumentsRecyclerAdapter);
                Log.wtf("select Market : " + selectMarket.toString(), "instrument count = " + filteredmarketInstruments.size());

                retrieveFiltered(getIntent().hasExtra("sectorId"));

                Log.wtf("on instr click", "allStocks count = " + allStocks.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rvInstruments.setLayoutManager(new LinearLayoutManager(StockActivity.this, LinearLayoutManager.HORIZONTAL, false));
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(this, filteredmarketInstruments, this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);

        llm = new LinearLayoutManager(StockActivity.this);
        rvStocks.setLayoutManager(llm);
        btClear = rlStockSearch.findViewById(R.id.btClear);
        etSearch = rlStockSearch.findViewById(R.id.etSearch);
        etSearch.setSelected(false);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                if (arg0.length() == 0) {
                    btClear.setVisibility(View.GONE);

                    try {
                        Actions.closeKeyboard(StockActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }

                    allStocks.clear();
                    retrieveFiltered(getIntent().hasExtra("sectorId"));
                    adapter = new StockQuotationRecyclerNewAdapter(StockActivity.this, allStocks);//, StockActivity.this);
                    adapter.notifyDataSetChanged();
                    rvStocks.setAdapter(adapter);
                } else {

                    adapter.getFilter().filter(arg0);
                    btClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        btClear.setOnClickListener(v -> etSearch.setText(""));


        tvStock = findViewById(R.id.tvStock);
        tvStockGig = findViewById(R.id.tvStockGig);
        tvSessionName = findViewById(R.id.tvSessionName);

        if(BuildConfig.Enable_Markets){
            tvSessionName.setVisibility(View.VISIBLE);
        }else {
            tvSessionName.setVisibility(View.GONE);
        }

        tvPrice = findViewById(R.id.tvPrice);
        tvChange = findViewById(R.id.tvChange);

        linearStockHeader= findViewById(R.id.linearStockQuotationHeader);
        linearStockHeaderGig= findViewById(R.id.linearStockQuotationHeaderGig);

        if(BuildConfig.Enable_Markets){
            linearStockHeader.setVisibility(View.VISIBLE);
            linearStockHeaderGig.setVisibility(View.GONE);
            linearHeaderFilters.setVisibility(View.GONE);
      }else {
            linearHeaderFilters.setVisibility(View.VISIBLE);
            linearStockHeader.setVisibility(View.GONE);
            linearStockHeaderGig.setVisibility(View.VISIBLE);
            setHeader();
        }

    }


    private void changeTabsFont(TabLayout tabLayout, Typeface typeface) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {

                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

    @Override
    public void onItemClicked(View v, int position) {

        for (int i = 0; i < filteredmarketInstruments.size(); i++) {
            if (i == position) {
                filteredmarketInstruments.get(i).setIsSelected(!filteredmarketInstruments.get(i).getIsSelected());
                selectedInstrument = filteredmarketInstruments.get(i).getIsSelected() ? filteredmarketInstruments.get(i) : new Instrument();
                instrumentId = filteredmarketInstruments.get(i).getIsSelected() ? selectedInstrument.getInstrumentCode() : "";
                isSelectInstrument = filteredmarketInstruments.get(i).getIsSelected();
                MyApplication.instrumentId = instrumentId;
            } else {
                filteredmarketInstruments.get(i).setIsSelected(false);
            }
        }

        /*if(allInstruments.get(position).getIsSelected()){ allInstruments.get(position).setIsSelected(false); }
        else{ allInstruments.get(position).setIsSelected(true); }*/

        instrumentsRecyclerAdapter.notifyDataSetChanged();

        allStocks.clear();
        retrieveFiltered(getIntent().hasExtra("sectorId"));
    }

    public ArrayList<StockQuotation> FilterStocks(ArrayList<StockQuotation> stockList) {

        String filterString = etSearch.getText().toString().toLowerCase();

        ArrayList<StockQuotation> results = new ArrayList<StockQuotation>();

        final ArrayList<StockQuotation> list = stockList;

        int count = list.size();
        final ArrayList<StockQuotation> nlist = new ArrayList<>(count);

        String filterableString;

        for (int i = 0; i < count; i++) {

            if (MyApplication.instrumentId.length() > 0) {

                if (list.get(i).getInstrumentId().equals(MyApplication.instrumentId)) {

                    filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                            + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }
            } else {

                filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                        + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }
        }

        results = nlist;
        return results;
    }

    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(StockActivity.this);
        }

        @Override
        protected String doInBackground(Void... a) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetInstruments.getValue(); // this method uses key after login

            int market=2;
            while (market<4) {
                try {
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("id", "0");
                    parameters.put("key", getResources().getString(R.string.beforekey));
                    parameters.put("MarketId", market+"");/*Integer.toString(enums.MarketType.KWOTC.getValue())*/
                    result = ConnectionRequests.GET(url, StockActivity.this, parameters);

                    if(market==2)
                        MyApplication.instruments = new ArrayList<>();
                    MyApplication.instruments.addAll(GlobalFunctions.GetInstrumentsList(result));
                    Log.wtf("stock_instruments", "MyApplication.instruments count = " + MyApplication.instruments.size());

                    for (int i = 0; i < MyApplication.instruments.size(); i++) {
                        if (!MyApplication.availableMarkets.contains(MyApplication.instruments.get(i).getMarketID()))
                            MyApplication.availableMarkets.add(MyApplication.instruments.get(i).getMarketID());
                    }

                    if(BuildConfig.Enable_Markets) {
                        if(!MyApplication.availableMarkets.contains(3))
                            MyApplication.availableMarkets.add(3);
                        Actions.setLastMarketId(getApplication(), enums.MarketType.XKUW.getValue());
                        Log.wtf("trade_instrument_splash_size", MyApplication.instruments.size() + "");
                    }else {
                        Actions.setLastMarketId(getApplication(), enums.MarketType.DSMD.getValue());
                        Log.wtf("trade_instrument_splash_size", MyApplication.instruments.size() + "");

                    }

                    //to be removed
                    //MyApplication.availableMarkets.add(3);

                    Log.wtf("Splash", "MyApplication.availableMarkets count = " + MyApplication.availableMarkets.size());

                    if (Actions.getLastMarketId(getApplicationContext()) == -1) {
                       if(BuildConfig.Enable_Markets)
                           Actions.setLastMarketId(getApplication(), enums.MarketType.XKUW.getValue());
                       else
                           Actions.setLastMarketId(getApplication(), enums.MarketType.DSMD.getValue());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                                Log.wtf("GetInstruments", "error : " + e.getMessage());
                            }
                        });
                    }
                }
                market++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MyApplication.dismiss();

            allInstruments.clear();

         //   if (!MyApplication.isOTC) {
             //   for (int i = 1; i < AllMarkets.size(); i++) {
              //      allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
             //   }
         //   } else {
               allInstruments.addAll(MyApplication.instruments);
           // }

            marketInstruments = allInstruments;
            filteredmarketInstruments = Actions.filterMarketInstruments(allInstruments);
            instrumentsRecyclerAdapter.notifyDataSetChanged();
            try {
                spMarkets.setSelection(1,false);
                spMarkets.setSelection(0,false);
            }catch (Exception e){}
        }
    }






    private class GetBonds extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            sendupdates();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";


            HashMap<String, String> parameters = new HashMap<String, String>();

           while (running) {
                MyApplication.setWebserviceItem();
                String url = MyApplication.link + MyApplication.GetBonds.getValue();


                Log.wtf("bob", "TStamp:  " + MyApplication.bondsTimesTampMap.get(MyApplication.marketID));
                Log.wtf("bob", "MarketId:  " + MyApplication.marketID);
                Log.wtf("bob", "url:" + url);
                String marketid = "";


                try {

                    parameters.put("TStamp", MyApplication.bondsTimesTamp /*MyApplication.stockTimesTamp*/);
                    parameters.put("key", getResources().getString(R.string.beforekey));
                    parameters.put("stockId", "");


                    result = ConnectionRequests.GET(url, getApplicationContext(), parameters);


     /*               if (firstTime || MyApplication.stockQuotationsBonds.size() == 0) {
                        Log.wtf("bob_","1111");
                        MyApplication.stockQuotationsBonds.addAll(GlobalFunctions.GetStockQuotationBonds(result,true));
                        firstTime = false;
                    } else if (!MyApplication.bondsTimesTampMap.get(MyApplication.marketID).equals("0")) {
                        try {
                            Log.wtf("bob_","2222");
                            changedStocks = GlobalFunctions.GetStockQuotationBonds(result,true);
                        } catch (Exception e) {
                            Log.wtf("bob_","4444");
                            e.printStackTrace();
                        }
                    }

                    if (changedStocks.size() != 0 && !MyApplication.bondsTimesTampMap.get(MyApplication.marketID).equals("0")) {
                        Log.wtf("bob_","5555");
                        replaceOldStocks(MyApplication.stockQuotationsBonds, changedStocks);
                    }*/




                    if (MyApplication.stockQuotationsBonds.size() == 0) {
                        MyApplication.stockQuotationsBonds.addAll(GlobalFunctions.GetStockQuotationBonds(result, true));
                    }else if (!MyApplication.bondsTimesTampMap.get(MyApplication.marketID).equals("0")) {
                        try {
                            Log.wtf("bob_","2222");
                            changedStocks = GlobalFunctions.GetStockQuotationBonds(result,true);
                        } catch (Exception e) {
                            Log.wtf("bob_","4444");
                            e.printStackTrace();
                        }
                    }

                    if (changedStocks.size() != 0 && !MyApplication.bondsTimesTampMap.get(MyApplication.marketID).equals("0")) {
                        Log.wtf("bob_","5555");
                        replaceOldStocks(MyApplication.stockQuotationsBonds, changedStocks);
                    }

                } catch (Exception e) {
                    Log.wtf("bob_", "6666" + e.toString());
                    e.printStackTrace();
                    if (MyApplication.showBackgroundRequestToastError) {
                        try {
                            if (MyApplication.isDebug) {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetBonds.getKey(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception es) {
                            Log.wtf("Appservice ", "Error - " + MyApplication.GetBonds.getKey() + " : " + es.getMessage());
                        }
                    }
                }
                publishProgress();
                try {
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

          }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {


            sendupdates();
            super.onPostExecute(result);






/*
            if (running) {

                tmpStocks = new ArrayList<>();
                Log.wtf("LocalBroadcastManager", "before allStocks size = " + allStocks.size());
                allStocks.clear();
                if(!instrumentId.equals(""))
                    tmpStocks.addAll(Actions.filterStocksByInstrumentID(MyApplication.stockQuotationsBonds, MyApplication.instrumentId));
                else
                    tmpStocks.addAll(MyApplication.stockQuotationsBonds);

                allStocks.addAll(tmpStocks);
  *//*              if (isSelectInstrument) {

                    allStocks.addAll(tmpStocks);
                } else {

                    if (selectMarket.getValue() == TradingSession.All.getValue()) {
                        allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, allInstruments));
                    } else {
                        allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, filteredmarketInstruments));
                    }
                }*//*




                try {
                    Log.wtf("allStocks", "count = " + allStocks.size());


                    Log.wtf("stock_all",allStocks.size()+"");


                    Log.wtf("LocalBroadcastManager", "after allStocks size = " + allStocks.size());
                    if (etSearch.length() > 0) {
                        adapter.getFilter().filter(etSearch.getText().toString());
                    }
                    Log.wtf("allStocks in adapter", "count = " + adapter.getFilteredItems().size());
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}


                //rvStocks.setAdapter(adapter);
            }*/







        }
    }

    private void sendupdates(){
        tmpStocks = new ArrayList<>();
        Log.wtf("LocalBroadcastManager", "before allStocks size = " + allStocks.size());
        allStocks.clear();

        if(!instrumentId.equals(""))
            tmpStocks.addAll(Actions.filterStocksByInstrumentID(MyApplication.stockQuotationsBonds, MyApplication.instrumentId));
        else
            tmpStocks.addAll(MyApplication.stockQuotationsBonds);

        allStocks.addAll(tmpStocks);
        if (isSelectInstrument) {

            allStocks.addAll(tmpStocks);
        } else {

            if (selectMarket.getValue() == TradingSession.All.getValue()) {
                allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, allInstruments));
            } else {
                allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, filteredmarketInstruments));
            }
        }

        try {

            if (etSearch.length() > 0) {
                adapter.getFilter().filter(etSearch.getText().toString());
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e){
            Log.wtf("exception",e.toString());
        }

    }

    private void replaceOldStocks(ArrayList<StockQuotation> oldstocks, ArrayList<StockQuotation> newstocks) {

        for (int i = 0; i < oldstocks.size(); i++) {
            boolean contains = false;
            for (int k = 0; k < newstocks.size(); k++) {
                if (oldstocks.get(i).getStockID() == newstocks.get(k).getStockID()) {
                    contains = true;
                    //Log.wtf("replaceOldStocks",newstocks.get(k).getSymbolEn() + " contains");
                    newstocks.get(k).setChanged(true);
                    oldstocks.set(i, newstocks.get(k));
                }
            }
            if (!contains)
                oldstocks.get(i).setChanged(false);
        }

    }

    private void setFilters(){
        int allStockCount=0;
        int activeStockCount=0;
        int gainerCount=0;
        int losersCount=0;
        int unchangedCount=0;

        if (getIntent().hasExtra("sectorId")) {
            allStockCount=allStocks.size();
            for (int i = 0; i < allStocks.size(); i++) {
                if (Double.parseDouble(allStocks.get(i).getChange()) > 0) {
                    gainerCount++;
                } else if (Double.parseDouble(allStocks.get(i).getChange()) < 0) {
                    losersCount++;
                } else {
                    unchangedCount++;
                }

                if (allStocks.get(i).getVolume() > 0) {
                    activeStockCount++;
                }
            }
        }else {
             allStockCount=MyApplication.stockQuotations.size();
            for (int i = 0; i < MyApplication.stockQuotations.size(); i++) {
                if (Double.parseDouble(MyApplication.stockQuotations.get(i).getChange()) > 0) {
                    gainerCount++;
                } else if (Double.parseDouble(MyApplication.stockQuotations.get(i).getChange()) < 0) {
                    losersCount++;
                } else {
                    unchangedCount++;
                }

                if (MyApplication.stockQuotations.get(i).getVolume() > 0) {
                    activeStockCount++;
                }
            }
        }

        tvAllStockCount.setText(allStockCount+"");
        tvActiveCount.setText(activeStockCount+"");
        tvGainerCount.setText(gainerCount+"");
        tvLooserCount.setText(losersCount+"");
        tvUnchangedCount.setText(unchangedCount+"");
    }


    private void filterListeners(){
        linearFilterAllStocks.setOnClickListener(v->{
            resetFilters();
            isFilterAll=true;
            retrieveFiltered(getIntent().hasExtra("sectorId"));
           // tvAllStockCount.setTextColor(getResources().getColor(R.color.white));
            linearFilterAllStocks.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

        });

        linearFilterActive.setOnClickListener(v->{
            resetFilters();
            isFilterActive=true;
            retrieveFiltered(getIntent().hasExtra("sectorId"));
            linearFilterActive.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            //tvActiveCount.setTextColor(getResources().getColor(R.color.white));

        });


        linearFilterGainer.setOnClickListener(v->{
            resetFilters();
            isFilterGainers=true;
            retrieveFiltered(getIntent().hasExtra("sectorId"));
            linearFilterGainer.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
           // tvGainerCount.setTextColor(getResources().getColor(R.color.white));

        });


        linearFilterLosers.setOnClickListener(v->{
            resetFilters();
            isFilterLoosers=true;
            retrieveFiltered(getIntent().hasExtra("sectorId"));
            linearFilterLosers.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
          //  tvLooserCount.setTextColor(getResources().getColor(R.color.white));

        });


        linearFilterUnchanged.setOnClickListener(v->{
            resetFilters();
            isFilterUnchanged=true;
            retrieveFiltered(getIntent().hasExtra("sectorId"));
            linearFilterUnchanged.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
           // tvUnchangedCount.setTextColor(getResources().getColor(R.color.white));
        });
    }
    
    
    private void resetFilters(){
        isFilterAll=false;
        isFilterActive=false;
        isFilterGainers=false;
        isFilterLoosers=false;
        isFilterUnchanged=false;

        tvAllStockCount.setTextColor(getResources().getColor(R.color.white));
        tvActiveCount.setTextColor(getResources().getColor(R.color.white));
        tvGainerCount.setTextColor(getResources().getColor(R.color.white));
        tvLooserCount.setTextColor(getResources().getColor(R.color.white));
        tvUnchangedCount.setTextColor(getResources().getColor(R.color.white));


        linearFilterAllStocks.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
        linearFilterActive.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
        linearFilterGainer.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
        linearFilterLosers.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
        linearFilterUnchanged.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
    }

    private void checkFilter(){

        if(!BuildConfig.Enable_Markets) {
            if (!isFilterAll) {
                tmpStocks.clear();

                for (int i = 0; i < allStocks.size(); i++) {
                    if(isFilterActive) {
                        if (allStocks.get(i).getVolume() > 0)
                            tmpStocks.add(allStocks.get(i));
                    }else if(isFilterGainers){
                        if (Double.parseDouble(allStocks.get(i).getChangePercent()) > 0)
                            tmpStocks.add(allStocks.get(i));

                    }
                    else if(isFilterLoosers){
                        if (Double.parseDouble(allStocks.get(i).getChangePercent()) < 0)
                            tmpStocks.add(allStocks.get(i));

                    }
                    else if(isFilterUnchanged){
                        if (Double.parseDouble(allStocks.get(i).getChangePercent()) == 0)
                            tmpStocks.add(allStocks.get(i));
                    }

                }

                allStocks.clear();
                allStocks.addAll(tmpStocks);

                //sorting by change desc

                try{
                    if(isFilterLoosers){
                    Collections.sort(allStocks, new Comparator<StockQuotation>() {
                        @Override
                        public int compare(StockQuotation lhs, StockQuotation rhs) {
                            return Double.compare(Double.parseDouble(lhs.getChangePercent()), Double.parseDouble(rhs.getChangePercent()));
                        }
                    });
                    }else {

                        Collections.sort(allStocks, new Comparator<StockQuotation>() {
                            @Override
                            public int compare(StockQuotation lhs, StockQuotation rhs) {
                                return Double.compare(Double.parseDouble(rhs.getChangePercent()), Double.parseDouble(lhs.getChangePercent()));
                            }
                        });

                    }



                }catch (Exception e){
                    Log.wtf("exception_1",e.toString());
                }
            }
        }
    }

}
