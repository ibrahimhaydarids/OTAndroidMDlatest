package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.ids.fixot.adapters.FavoritesRecyclerAdapter;
import com.ids.fixot.adapters.InstrumentsRecyclerAdapter;
import com.ids.fixot.adapters.MarketsSpinnerAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.enums.enums.TradingSession;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.ids.fixot.MyApplication.GetFavoriteStocks;
import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 9/28/2017.
 */

public class FavoritesActivity extends AppCompatActivity implements FavoritesRecyclerAdapter.RecyclerViewOnItemClickListener, InstrumentsRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener , spItemListener {

    //    InstrumentsAdapter instrumentsAdapter;
    public static String instrumentId = "";
    public static BroadcastReceiver favMarketReceiver;
    ImageView ivBack;
    Toolbar myToolbar;
    RelativeLayout rootLayout;
    FrameLayout rlStockSearch;
    ArrayList<StockQuotation> favoriteStocks = new ArrayList<>();
    ArrayList<StockQuotation> allStocks = new ArrayList<>();
    ArrayList<StockQuotation> tmpStocks = new ArrayList<>();
    FavoritesRecyclerAdapter adapter;
    SwipeRefreshLayout swipe_container;
    EditText etSearch;
    Button btClear;
    RecyclerView rvStocks;
    GetFavoriteStocks getFavoriteStocks;
    LinearLayoutManager llm;
    Instrument selectedInstrument = new Instrument();
    GetInstruments getInstruments;
    //    TabLayout tlInstrumentItemsTabs;
    LinearLayout llTab;
    int count=0;

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

    TextView tvStock, tvSessionName, tvPrice, tvChange, tvLogout;
    ArrayList<Integer> favoritesIds = new ArrayList<>();
    private BroadcastReceiver receiver;
    private boolean started = false;
    private boolean firstLaunch = true;
    Spinner spInstrumentsTop;

    LinearLayout linearStockHeader,linearStockHeaderGig;
    private TextView tvColumn1Title,tvColumn2Title,tvColumn3Title,tvColumn4Title,tvColumn5Title,tvColumn6Title;



    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        if(MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue())))
            selectMarket = TradingSession.All;

        filteredmarketInstruments.clear();
        for (int i=0;i<marketInstruments.size();i++)
            marketInstruments.get(i).setIsSelected(false);
        filteredmarketInstruments=Actions.filterMarketInstruments(marketInstruments);
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(FavoritesActivity.this, filteredmarketInstruments, FavoritesActivity.this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);
        MyApplication.instrumentId = "";
        favoriteStocks.clear();
        checkspMarket();
        retrieveFiltered(false);
        getFavoriteStocks = new GetFavoriteStocks();
        getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();
    }

    public FavoritesActivity() {
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
        setContentView(R.layout.activity_favorites);
        Actions.initializeBugsTracking(this);

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
        Actions.initializeToolBar(getString(R.string.favorite), FavoritesActivity.this);
        Actions.showHideFooter(this);

        started = true;

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

        Actions.overrideFonts(this, rootLayout, false);


        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

        //<editor-fold desc="instruments section">

        if (MyApplication.instruments.size() < 2) {

            Actions.initializeInstruments(this);
            getInstruments = new GetInstruments();
            getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            allInstruments.clear();

  /*          if (!MyApplication.isOTC) {
                for (int i = 1; i < AllMarkets.size(); i++) {
                    allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
                }
            } else {*/
                allInstruments.addAll(MyApplication.instruments);
          //  }

            getFavoriteStocks = new GetFavoriteStocks();
            getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
        //</editor-fold>

        Actions.setTypeface(new TextView[]{tvStock, tvSessionName, tvPrice, tvChange}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        tvSessionName.setGravity(MyApplication.lang == MyApplication.ARABIC ? Gravity.LEFT : Gravity.RIGHT);

        /*try { //testing

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyApplication.isDebug = true;

                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

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

    private void findViews() {

        tvColumn1Title = findViewById(R.id.tvColumn1Title);
        tvColumn2Title = findViewById(R.id.tvColumn2Title);
        tvColumn3Title = findViewById(R.id.tvColumn3Title);
        tvColumn4Title = findViewById(R.id.tvColumn4Title);
        tvColumn5Title = findViewById(R.id.tvColumn5Title);
        tvColumn6Title = findViewById(R.id.tvColumn6Title);
        linearStockHeader= findViewById(R.id.linearStockQuotationHeader);
        linearStockHeaderGig= findViewById(R.id.linearStockQuotationHeaderGig);
        if(BuildConfig.Enable_Markets){
            linearStockHeader.setVisibility(View.VISIBLE);
            linearStockHeaderGig.setVisibility(View.GONE);

        }else {
            linearStockHeader.setVisibility(View.GONE);
            linearStockHeaderGig.setVisibility(View.VISIBLE);
            setHeader();
        }

        checkspMarket();
        llTab = findViewById(R.id.llTab);

        if(BuildConfig.Enable_Markets) {
            llTab.setVisibility(View.VISIBLE);

        }
        else {
            llTab.setVisibility(View.GONE);

        }


        rlStockSearch = findViewById(R.id.rlStockSearch);
        rootLayout = findViewById(R.id.rootLayout);
        rvStocks = findViewById(R.id.rvStocks);
        swipe_container = findViewById(R.id.swipe_container);
        btClear = rlStockSearch.findViewById(R.id.btClear);
        etSearch = rlStockSearch.findViewById(R.id.etSearch);

        tvStock = findViewById(R.id.tvStock);
        tvSessionName = findViewById(R.id.tvSessionName);
        tvPrice = findViewById(R.id.tvPrice);
        tvChange = findViewById(R.id.tvChange);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(FavoritesActivity.this));
        tvLogout.setVisibility((BuildConfig.GoToMenu) ? View.GONE : View.VISIBLE);


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

             if (selectMarket.getValue() == TradingSession.All.getValue()) {
                    marketInstruments = allInstruments;
                    filteredmarketInstruments=Actions.filterMarketInstruments(allInstruments);
                } else {
                    marketInstruments = Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, selectMarket.getValue());
                   filteredmarketInstruments = Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, selectMarket.getValue());

             }



                for (Instrument inst : filteredmarketInstruments) {
                    inst.setIsSelected(false);
                }


                instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(FavoritesActivity.this, filteredmarketInstruments, FavoritesActivity.this);
                rvInstruments.setAdapter(instrumentsRecyclerAdapter);
                Log.wtf("select Market : " + selectMarket.toString(), "instrument count = " + filteredmarketInstruments.size());
                filterStock();
                Log.wtf("on instr click", "allStocks count = " + allStocks.size());
                retrieveFiltered(false);
                Log.wtf("favoriteStocks_count6", ": " + favoriteStocks.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        rvInstruments.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this, LinearLayoutManager.HORIZONTAL, false));
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(this, filteredmarketInstruments, this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);



        etSearch.setSelected(false);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.length() == 0) {

                    btClear.setVisibility(View.GONE);

                    try {
                        Actions.closeKeyboard(FavoritesActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }
                    Log.wtf("favoriteStocks_count5", ": " + favoriteStocks.size());
                    adapter = new FavoritesRecyclerAdapter(FavoritesActivity.this, favoriteStocks, FavoritesActivity.this);
                    adapter.notifyDataSetChanged();
                    rvStocks.setAdapter(adapter);
                } else {

                    btClear.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(arg0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        btClear.setOnClickListener(v -> {
            etSearch.setText("");
            try {
                Actions.closeKeyboard(FavoritesActivity.this);
            } catch (Exception e) {
                Log.wtf("catch ", "" + e.getMessage());
            }
        });

        myToolbar = findViewById(R.id.my_toolbar);
        ivBack = myToolbar.findViewById(R.id.ivBack);
        ivBack.setVisibility((BuildConfig.GoToMenu) ? View.VISIBLE : View.GONE);

        Log.wtf("favoriteStocks_count4", ": " + favoriteStocks.size());
        llm = new LinearLayoutManager(this);
        adapter = new FavoritesRecyclerAdapter(this, favoriteStocks, this);
        rvStocks.setAdapter(adapter);
        rvStocks.setLayoutManager(llm);

        swipe_container.setOnRefreshListener(() -> {

            allStocks.clear();
            favoriteStocks.clear();
            Log.wtf("favoriteStocks_count7", ": " + favoriteStocks.size());
            adapter.notifyDataSetChanged();
            swipe_container.setRefreshing(false);
            getFavoriteStocks = new GetFavoriteStocks();
            getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        });
        swipe_container.setVisibility(View.GONE);

    }


    private void setHeader(){
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
            if(MyApplication.STOCK_COLUMN_2 == MyApplication.STOCK_TYPE_OFFER_PRICE){
                MyApplication.STOCK_COLUMN_2= MyApplication.STOCK_TYPE_OFFER_VOLUME;
            }else {
                MyApplication.STOCK_COLUMN_2= MyApplication.STOCK_TYPE_OFFER_PRICE;
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
            if(MyApplication.STOCK_COLUMN_4 == MyApplication.STOCK_TYPE_BID_PRICE){
                MyApplication.STOCK_COLUMN_4= MyApplication.STOCK_TYPE_BID_VOLUME;
            }else {
                MyApplication.STOCK_COLUMN_4= MyApplication.STOCK_TYPE_BID_PRICE;
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



    @Override
    public void onItemFavClicked(View v, int position) {

        Bundle b = new Bundle();
        b.putParcelable("stock", adapter.getFilteredItems().get(position));
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(FavoritesActivity.this, StockDetailActivity.class);
        startActivity(i);
    }
    private void filterStock(){
        ArrayList<StockQuotation> filterAll=new ArrayList<>();
        for(int i=0;i<allStocks.size();i++){
            if(!filterAll.contains(allStocks.get(i))){
                filterAll.add(allStocks.get(i));
            }
        }
        allStocks.clear();
        allStocks.addAll(filterAll);
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
    protected void onResume() {
        super.onResume();
        Actions.startStockQuotationService(this);
        Actions.checkSession(this);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);

        Actions.InitializeSessionServiceV2(this);
        Actions.InitializeMarketServiceV2(this);

        //InitializeMarketServiceLocal();

        Actions.checkLanguage(this, started);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }
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
            unregisterReceiver(favMarketReceiver);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("unregisterReceiver ex", e.getMessage());
        }

        try {
            getInstruments.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getInstruments);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Instruments ex", e.getMessage());
        }

        try {
            getFavoriteStocks.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getFavoriteStocks);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Favorites ex", e.getMessage());
        }

        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    public void back(View v) {
        finish();
    }
/*
    @Override
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
        retrieveFiltered(false);
    }

    private void retrieveFiltered(boolean hasSectorId) {
        tmpStocks = new ArrayList<>();
        allStocks = new ArrayList<>();

        try{tmpStocks = (Actions.getStocksByIds(MyApplication.stockQuotations, favoritesIds));}catch (Exception e){}
        tmpStocks = (Actions.filterStocksByInstrumentID(tmpStocks, MyApplication.instrumentId));

        if (isSelectInstrument) {

            allStocks.addAll(tmpStocks);
        } else {

            if (selectMarket.getValue() == TradingSession.All.getValue()) {
                for (int i = 0; i < allInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, allInstruments.get(i).getInstrumentCode()));
                }
            } else {
                for (int i = 0; i < filteredmarketInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, filteredmarketInstruments.get(i).getInstrumentCode()));
                }
            }
        }


        if (etSearch.length() > 0) {
            allStocks = FilterStocks(allStocks);
            //adapter.getFilter().filter(etSearch.getText().toString());
        }
        filterStock();

        try{

            Collections.sort(allStocks, new Comparator<StockQuotation>() {
                public int compare(StockQuotation o1, StockQuotation o2) {
                   // return o1.getSecurityId().compareTo(o2.getSecurityId());
                    return Integer.compare(Integer.parseInt(o1.getSecurityId()), Integer.parseInt(o2.getSecurityId()));

                }
            });}catch (Exception e){
           Log.wtf("exception_2",e.toString());
        }

        adapter = new FavoritesRecyclerAdapter(FavoritesActivity.this, allStocks, this);
        rvStocks.setAdapter(adapter);

        Log.wtf("on instr click", "allStocks count = " + allStocks.size());



        //rvStocks.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
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

    void InitializeMarketServiceLocal() {
        try {
            View includedLayout = this.findViewById(R.id.my_toolbar);

            final TextView marketstatustxt = includedLayout.findViewById(R.id.market_state_value_textview);
            final LinearLayout llmarketstatus = includedLayout.findViewById(R.id.ll_market_state);


            favMarketReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);
                    Actions.setMarketStatus(llmarketstatus, marketstatustxt, FavoritesActivity.this);
                    Log.wtf("InitializeMarketServiceV2", "setMarketStatus: " + MyApplication.marketStatus.getStatusDescriptionAr());

                    if (marketTime != null) {
                        if (marketTime.equals(""))
                            marketTime = MyApplication.marketStatus.getMarketTime();
                        Actions.setMarketTime(marketTime, FavoritesActivity.this);
                        Log.wtf("InitializeMarketServiceV2 3", "setMarketTime: " + marketTime);
                    }
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(favMarketReceiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

//            LocalBroadcastManager.getInstance(this).registerReceiver(
//                    new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//
//                            String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);
//                            Actions.setMarketStatus(llmarketstatus,marketstatustxt, FavoritesActivity.this);
//                            Log.wtf("InitializeMarketService","setMarketStatus: " + MyApplication.marketStatus.getStatusDescriptionAr());
//
//                            if (marketTime != null) {
//                                if (marketTime.equals(""))
//                                    marketTime = MyApplication.marketStatus.getMarketTime();
//                                Actions.setMarketTime(marketTime, FavoritesActivity.this);
//                                Log.wtf("InitializeMarketServiceV2","setMarketTime: " + marketTime);
//                            }
//                        }
//                    }, new IntentFilter(AppService.ACTION_MARKET_SERVICE)
//            );
            Log.wtf("InitializeMarketServiceV2", "call from : " + this.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(FavoritesActivity.this);
            Log.wtf("play ", "GetInstruments");
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


                    result = ConnectionRequests.GET(url, FavoritesActivity.this, parameters);

             /*       if(market==2)
                        MyApplication.instruments = new ArrayList<>();*/
                  //  MyApplication.instruments.addAll(GlobalFunctions.GetInstrumentsList(result));
                    Log.wtf("Splash", "MyApplication.instruments count = " + MyApplication.instruments.size());

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
            Log.wtf("dismiss ", "GetInstruments");

            allInstruments.clear();
/*            if (!MyApplication.isOTC) {
                for (int i = 1; i < AllMarkets.size(); i++) {
                    allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
                }
            } else {*/
                allInstruments.addAll(MyApplication.instruments);
        //    }
            marketInstruments = allInstruments;
            filteredmarketInstruments=Actions.filterMarketInstruments(allInstruments);
            instrumentsRecyclerAdapter.notifyDataSetChanged();

            getFavoriteStocks = new GetFavoriteStocks();
            getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
    }

    private class GetFavoriteStocks extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

         //   MyApplication.showDialog(FavoritesActivity.this);
            Log.wtf("play ", "GetFavoriteStocks");
        }

        @Override
        protected String doInBackground(Void... params) {

            MyApplication.setWebserviceItem();
            String result = "";
            String url = MyApplication.link + GetFavoriteStocks.getValue(); // this method uses key after login


            try {

                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("UserID", MyApplication.currentUser.getId() + "");
                parameters.put("key", getString(R.string.beforekey));
                parameters.put("TStamp", "0");
                parameters.put("InstrumentId", ""/*instrumentId*/);
                parameters.put("MarketID", MyApplication.marketID);
                parameters.put("tradingSession", "0");
                Log.wtf("GetFavoriteStocks", "parameters : " + parameters);

                result = ConnectionRequests.GET(url, FavoritesActivity.this, parameters);
                try {
                    favoritesIds.clear();
                    favoritesIds.addAll(GlobalFunctions.GetFavoriteStocks(result));
                } catch (Exception e) {

                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;

        }

        private void filterStock(){
            ArrayList<StockQuotation> filterAll=new ArrayList<>();
            for(int i=0;i<allStocks.size();i++){
                if(!filterAll.contains(allStocks.get(i))){
                    filterAll.add(allStocks.get(i));
                }
            }
            allStocks.clear();
            allStocks.addAll(filterAll);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                MyApplication.dismiss();
                Log.wtf("dismiss ", "GetFavoriteStocks");
            } catch (Exception e) {
                e.printStackTrace();
            }
            allStocks.clear();
            favoriteStocks.clear();
            try{allStocks.addAll(Actions.getStocksByIds(MyApplication.stockQuotations, favoritesIds));}catch (Exception e){}
            favoriteStocks.addAll(allStocks);

           try{
            Collections.sort(favoriteStocks, new Comparator<StockQuotation>() {
                @Override
                public int compare(StockQuotation lhs, StockQuotation rhs) {
                    //return lhs.getSecurityId().compareTo(rhs.getSecurityId());
                    return Integer.compare(Integer.parseInt(lhs.getSecurityId()), Integer.parseInt(rhs.getSecurityId()));

                }
            });}catch (Exception e){
               Log.wtf("exception_3",e.toString());
           }
            Log.wtf("favoriteStocks_count3", ": " + favoriteStocks.size());
            adapter.notifyDataSetChanged();

/*            for(int i=0;i<favoritesIds.size();i++)
                Log.wtf("favorite_favorite_ids",favoritesIds.get(i).toString());
            for(int i=0;i<MyApplication.stockQuotations.size();i++)
                Log.wtf("favorite_myapplicationstocks",MyApplication.stockQuotations.get(i).getStockID()+"");

            for(int i=0;i<allStocks.size();i++)
                Log.wtf("favorite_allstocks",allStocks.get(i).getStockID()+"___"+allStocks.get(i).getSecurityId());*/

            Log.wtf("allStocks count", ": " + allStocks.size());
            Log.wtf("favoriteStocks_count", ": " + favoriteStocks.size());

            LocalBroadcastManager.getInstance(FavoritesActivity.this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {
           /*                     if(count==0) {
                                    getFavoriteStocks = new GetFavoriteStocks();
                                    getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
                                }*/

                                Log.wtf("broadcast","count : "+count);
                                if(adapter.getFilteredItems().size()==0) {
                                    favoritesIds.clear();
                                    getFavoriteStocks = new GetFavoriteStocks();
                                    getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
                                }


                            } catch (Exception e) {
                            }

                            count++;


                            //commented
                            allStocks.clear();
                            favoriteStocks.clear();
                          try{  allStocks.addAll(Actions.getStocksByIds(MyApplication.stockQuotations, favoritesIds));}catch (Exception e){}

                            try{
                                Collections.sort(allStocks, new Comparator<StockQuotation>() {
                                    @Override
                                    public int compare(StockQuotation lhs, StockQuotation rhs) {
                                        return Integer.compare(Integer.parseInt(lhs.getSecurityId()), Integer.parseInt(rhs.getSecurityId()));
                                    }
                                });}catch (Exception e){
                                Log.wtf("exception_1",e.toString());
                            }
                          favoriteStocks.addAll(allStocks);
                            Log.wtf("favoriteStocks_count3", ": " + favoriteStocks.size());


                            adapter.notifyDataSetChanged();
                            Log.wtf("broadcast_favorite_stock",MyApplication.stockQuotations.size()+"");
                            Log.wtf("broadcast_favorite_favorite_ids",favoritesIds.size()+"");
                            Log.wtf("broadcast_favorite_favorite_stocks",favoriteStocks.size()+"");



                        }
                    }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
            );

            MyApplication.dismiss();
            retrieveFiltered(false);
        }
    }

}
