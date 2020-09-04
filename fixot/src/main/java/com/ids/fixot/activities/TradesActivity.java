package com.ids.fixot.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.os.Handler;
import android.telecom.GatewayInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
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
import com.ids.fixot.adapters.OrdersPopupTrades;
import com.ids.fixot.adapters.OrdersPopupTrades;
import com.ids.fixot.adapters.PredefineQuantityAdapter;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.adapters.stockQuotationPopupNewAdapter;
import com.ids.fixot.classes.NumberTextWatcher;
import com.ids.fixot.enums.enums;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.BrokerageFee;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.Ordertypes;
import com.ids.fixot.model.SectorTitles;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.SubAccount;
import com.ids.fixot.model.Trade;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TradesActivity extends AppCompatActivity implements OrderDurationTypeAdapter.RecyclerViewOnItemClickListener,
        PredefineQuantityAdapter.RecyclerViewOnItemClickListener, MarketStatusListener, OrdersPopupTrades.RecyclerViewOnItemClickListener, OrdersPopupTrades.RefreshInterface,stockQuotationPopupNewAdapter.RecyclerViewOnItemClickListener {

    int selectedPos = -3;
    private boolean firstTime=true;
    Toolbar myToolbar;
    ImageView ivPortfolio, ivArrow, iv_PredfQtty, BtnCalc;
    TextView tvToolbarTitle, tvToolbarStatus, tvUserName, tvPortfolioNumber, tvInstrumentValue, tvSessionValue, close;
    TextView tvQuantityValue, tvPurchasePowerValue, tvStockTitle, tvCloseValue, tvLastValue, tvBidValue, tvAskValue;
    TextView tvHighValue, tvLowValue;
    Button btTimeSales, btOrderBook;
    Button btSell, btBuy;
    //Button btMarketPrice, /*btLimit*/;
    LinearLayout llTradeSection;
    OrderDurationType orderDurationType = new OrderDurationType();
    OrderDurationTypeAdapter adapter;
    PredefineQuantityAdapter predfQttyAdapter;
    EditText etLimitPrice, etQuantity, etDurationType;
    Button btLimitPlus, btLimitMinus, btQuantityPlus, btQuantityMinus,btMaxFloorMinus,btMaxFloorPlus;
    Spinner spLimitedPrice;
    SwipeRefreshLayout swipeContainer;
    TextView tvCostValue, tvCommissionValue, tvOverallValue;
    ImageView tvFill;
    Button btReview, btCancel;
    double price = 0;
    //    double tickDirection = 0 ;
    double ticketPrice = 0.1, ticketTriggerPrice=0.1,ticketQtt = 0.1;
    double HiLimit = 1000000000;
    int quantity = 0, maxFloor,tradeType = MyApplication.ORDER_BUY,
            orderType = MyApplication.LIMIT;
    int orderTypeAdvanced=0;
    StockQuotation stockQuotation;
    RelativeLayout rlUserHeader, rlBuySell;
    RelativeLayout rootLayout;
    Trade trade = new Trade();
    Trade ocaParentTrade = new Trade();
    ArrayList<Trade> arrayRelatedOrders=new ArrayList<>();
    OnlineOrder onlineOrder = new OnlineOrder();
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    LinearLayout llOrderType;
    TextView tvOtcPrice;
    //MKobaissy Popup
    AlertDialog.Builder builder;
    AlertDialog dialog;
    RecyclerView rvDurationType;
    GetOrderTypes getOrderTypes;
    int remainingQty = 0;
    int[] predefinedQuantityData = new int[]{};
    GetOrderDurationTypes getOrderDurationTypes;
    GetTradeInfo getTradeInfo;
    GetStockQuotation getStockById;
    CheckBox cbPrivate;
    String dateFormatter = "dd/MM/yyyy 00:00:00";
    String setDateFormatter = "dd/MM/yyyy";
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    boolean setMaxMin = false;
    private BroadcastReceiver receiver;
    private boolean started = false, running = true, firstOpen = true;
    private boolean isFromOrderDetails = false;
    Spinner spInstrumentsTop;
    Spinner spOrderType;
    public TradesActivity() {
        LocalUtils.updateConfig(this);
    }
    ArrayList<Ordertypes> arrayAllOrdertypes = new ArrayList<>();
    ArrayList<Ordertypes> arrayAllOrdertypesAdvanced = new ArrayList<>();
    ArrayList<Ordertypes> arraySpinnerOrdertypes = new ArrayList<>();
    ArrayList<Ordertypes> arraySpinnerOrdertypesAdvanced = new ArrayList<>();
    TextView tvPriceLable,tvTriggerPricelabel;
    OrderTypeSpinnerAdapter orderTypeSpinnerAdapter;
    OrderTypeSpinnerAdapter orderTypeAdvancedSpinnerAdapter;
    LinearLayout linearTriggerPrice,linearMaxFloor;
    private String priceFormat=Actions.TwoDecimalThousandsSeparator;
    private String limitPriceFormat="##.##";

    private LinearLayout llDuration;
    double triggerPrice=0;

    enums.OrderTypes selectOrder = enums.OrderTypes.LIMIT;
    ArrayList<enums.OrderTypes> allOrdertypes = new ArrayList<>();
    Button btTriggerLimitMinus,btTriggerLimitPlus;
    EditText etLimitTriggerPrice;
    private int stockId=0;
    private LinearLayout llAdvanced;
    private Spinner spOrderTypeAdvanced;
    private LinearLayout loading;

    private LinearLayout linearSelectAdvanced,linearSelectedOrder,linearSelectOrder;
    private TextView tvSelectedSymbol,tvSelectedInstruments,tvSelectedPrice,tvSelectedQuantity,tvSelectedExecutedQuantity,tvStatusHeader;

    private LinearLayout linearHeaderOrders,linearFilterType,linearFilterTypePopup;
    private ArrayList<OnlineOrder> allOrders = new ArrayList<>();
    LinearLayoutManager llm;
    private NestedScrollView nsScroll;

    OrdersPopupTrades adapterOrders;
    RecyclerView rvOrders;

    private Dialog ordersDialog;
    private Button btClear;
    private RecyclerView rvDialogOrders;
    OrdersPopupTrades adapterDialogOrders;
    private EditText etDialogSearch;
    private ArrayList<OnlineOrder> arrayFilteredOrderDialog = new ArrayList<>();
    private ArrayList<OnlineOrder> arrayAllOrdersDialog = new ArrayList<>();

    private int selectedRelatedOrderId;
    private OnlineOrder relatedOnlineOrder;
    private Button btAdd;
    // private ImageView ivShowHideOrders;

    private Boolean isShowOrder=false;
    EditText etPurchasePowerPopup;
    EditText etQuantityPopup;
    private LinearLayout linearParentOrder;
    private TextView tvSymbolItemsParent,tvPriceItemParent,tvQuantityItemParent,tvExecutedQuantityItemParent,tvExecutedQuantityHeader;
    private LinearLayout popupLoading;

    GetUserOrders getUserOrders;
    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private Dialog stocksDialog;
    private Dialog fillDataDialog;
    stockQuotationPopupNewAdapter adapterStockPopup;
    RecyclerView rvStocks;
    EditText etSearchStock;
    private EditText etMaxFloor;
    private LinearLayout linearSessions;
    private Boolean isAutoUpdate=false;

    private LinearLayout linearTopHighLow,linearDataTopNew,linearDataTop,llColumn1,llColumn2,llColumn3,llColumn4,llColumn5,llColumn6;
    private TextView tvHighTitleNew,tvHighValueNew,tvLowTitleNew,tvLowValueNew,tvVolumeTitleNew,tvVolumeValueNew;
    private TextView tvColumn1Title,tvColum1Value, tvColumn2Title,tvColum2Value, tvColumn3Title,tvColum3Value, tvColumn4Title,tvColum4Value, tvColumn5Title,tvColum5Value, tvColumn6Title,tvColum6Value;


    private CountDownTimer localTimer;
    public boolean localFinish=true;








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
        Log.wtf("onCreate", " onCreate");

        Actions.setLocal(MyApplication.lang, this);
        Actions.initializeBugsTracking(this);

        setContentView(R.layout.activity_trades_new);

        df = new DecimalFormat("#,###.##",Actions.setInEnglish());
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###",Actions.setInEnglish());
        hasFractionalPart = false;

        if(Actions.getSubAccountOTCCount()==0 && Actions.getLastMarketId(getApplicationContext())==enums.MarketType.KWOTC.getValue()){
            CreateDialog(this,getString(R.string.plz_contact_kic));
        }else {

            setData();
            findViews();
            initAndCall();

        }
/*        LocalBroadcastManager.getInstance(TradesActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                             try{ adapterStockPopup.notifyDataSetChanged();}catch (Exception e){}

                            getTradeInfo = new GetTradeInfo();
                            getTradeInfo.execute();
                        } catch (Exception e) {
                        }

                    }
                }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
        );*/

    }


    public static void CreateDialog(final Activity c, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctw);
        builder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    dialog.cancel();
                    c.finish();
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void initAndCall(){


 /*       getUserOrders = new GetUserOrders();
        getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);*/
        started = true;


        if(stockId!=0) {
            getTradeInfo = new GetTradeInfo();
            getTradeInfo.execute();
            setInitialData();
            setOrderDurTypes();
        }




        Actions.initializeToolBar(getString(R.string.trades_page_title), TradesActivity.this);
        Actions.overrideFonts(this, rootLayout, false);
        setTypefaces();

        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
  /*          if(!isFromOrderDetails)
              Actions.setSpinnerTop(this, spInstrumentsTop, this);
            else*/
            spInstrumentsTop.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }
        // setPortfolioData(trade);


        try {
            Log.wtf("filterSub",Actions.getfilteredSubAccount().size()+"");
            subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
            spSubAccounts.setSelection(returnAccountIndex(),false);
            spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getSubAccounts().get(position);
                    }catch (Exception e){
                        Log.wtf("filterSub_exception1",e.toString());
                        e.printStackTrace();
                        try{MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getSubAccounts().get(0);
                        }catch (Exception e1){
                            Log.wtf("filterSub_exception2",e1.toString());
                            e1.printStackTrace();
                        }
                    }
                    if(stockId!=0){
                        getTradeInfo = new GetTradeInfo();
                        getTradeInfo.execute();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if(isFromOrderDetails)
                spSubAccounts.setEnabled(false);
        }catch (Exception e){}


        tvStockTitle.setOnClickListener(view -> {
            // if(orderTypeAdvanced!=MyApplication.OCA)
            if(!isFromOrderDetails)
                showStocksDialog();
        });
    }


    private void setAutoUpdate(){
        localTimer= new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

                localFinish=false;
            }
            public void onFinish() {
                localFinish=true;
                isAutoUpdate=true;

                reloadData();
                setAutoUpdate();
            }

        }.start();
    }

    private void stopAutoUpdate(){
        localTimer.cancel();
        isAutoUpdate=false;
        try {
            getOrderDurationTypes.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getOrderDurationTypes);
        } catch (Exception e) {
        }
        try {
            getOrderTypes.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getOrderTypes);
        } catch (Exception e) {
        }

        try {
            getStockById.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getStockById);
        } catch (Exception e) {
        }
    }

    public void back(View v) {
        this.finish();
    }

    private void reloadData(){
        boolean callDuration=false;
        if(stockId==0)
            callDuration=true;

        MyApplication.selectedStockId=stockId;
        if (orderTypeAdvanced == MyApplication.OCA && allOrders.size() == 0 || orderTypeAdvanced == MyApplication.OCA && ocaParentTrade.getId() == 0)
            ocaParentTrade = trade;

        getOrderTypes = new GetOrderTypes();
        getOrderTypes.execute();
        getTradeInfo = new GetTradeInfo();
        getTradeInfo.execute();
        if(callDuration){
            setOrderDurTypes();
        }
    }




    private void setData(){
        if (getIntent().hasExtra("isFromOrderDetails")) {

            isFromOrderDetails = true;
            StockQuotation s=new StockQuotation();
            s=getIntent().getExtras().getParcelable("stockQuotation");
            stockId=s.getStockID();
            MyApplication.selectedStockId=stockId;
            stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, s.getStockID());
            onlineOrder = getIntent().getExtras().getParcelable("onlineOrder");
            trade.setDurationTypeId(onlineOrder.getDurationID());
            trade.setQuantity(onlineOrder.getQuantity());
            trade.setExecutedQuantity(onlineOrder.getQuantityExecuted());
            trade.setGoodUntilDate(onlineOrder.getGoodUntilDate());
            trade.setTradeTypeID(onlineOrder.getTradeTypeID());
            trade.setOrderType(onlineOrder.getOrderTypeID());
            trade.setPrice(onlineOrder.getPrice());
            trade.setStockQuotation(stockQuotation);
            orderType = onlineOrder.getOrderTypeID();
            selectedPos = Actions.returnDurationIndex(orderType);
            orderTypeAdvanced=onlineOrder.getAdvancedOrderTypeID();
            selectedRelatedOrderId=onlineOrder.getRelatedOnlineOrderID();
            maxFloor=onlineOrder.getMaxfloor();



        } else {

            isFromOrderDetails = false;
            if (getIntent().hasExtra("stockQuotation")) { //from stocks activity
                Log.wtf("trade_from","1");
                StockQuotation s=new StockQuotation();
                s=getIntent().getExtras().getParcelable("stockQuotation");
                stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, s.getStockID());
                stockId=s.getStockID();
                MyApplication.selectedStockId=stockId;
                trade.setStockQuotation(stockQuotation);
                Log.wtf("trading_session_1",trade.getStockQuotation().getTradingSession()+"");

            } else if (getIntent().hasExtra("stockID")) { //from portfolio
                Log.wtf("trade_from","2");
                stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, getIntent().getExtras().getInt("stockID"));
                stockQuotation.setStockID(getIntent().getExtras().getInt("stockID"));
                stockId=getIntent().getExtras().getInt("stockID");
                MyApplication.selectedStockId=stockId;
                trade.setStockQuotation(stockQuotation);
            } else {
                Log.wtf("trade_from","3");
                stockQuotation = new StockQuotation();
            }
        }
    }

    private void setOrderDurTypes(){
        if (MyApplication.allOrderDurationType.size() == 0) {
            Log.wtf("MyApplication.allOrderDurationType.size() ", "0");
            getOrderDurationTypes = new GetOrderDurationTypes();
            getOrderDurationTypes.executeOnExecutor(MyApplication.threadPoolExecutor);

        } else {
            if (getIntent().hasExtra("isFromOrderDetails")) {
                Log.wtf("MyApplication.allOrderDurationType.size() > 0", "isFromOrderDetails");

                int index = Actions.returnDurationIndex(onlineOrder.getDurationID());
                selectedPos = index;
                setOrderDuration(index);
                trade.setDurationType(MyApplication.lang == MyApplication.ARABIC ? MyApplication.allOrderDurationType.get(index).getDescriptionAr() : MyApplication.allOrderDurationType.get(index).getDescriptionEn());
            } else {
                Log.wtf("MyApplication.allOrderDurationType.size() > 0", " Not have extra isFromOrderDetails ");
                if (!Actions.isMarketOpen() && BuildConfig.Enable_Markets) {
                    try {
                        selectedPos = Actions.returnDurationIndex(0);
                        setOrderDuration(Actions.returnDurationIndex(0));
                    } catch (Exception e) {
                    }
                }

                else {
                    try {
                        selectedPos = Actions.returnDurationIndex(0);

                        setOrderDuration(Actions.returnDurationIndex(0));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    private void setOrders(){
        try {
            llm = new LinearLayoutManager(TradesActivity.this);
            adapterOrders = new OrdersPopupTrades(TradesActivity.this, allOrders, this, this,!isFromOrderDetails,0,false,false);
            rvOrders.setLayoutManager(llm);
            rvOrders.setAdapter(adapterOrders);
        } catch (Exception e) {
            Log.wtf("OrdersPopupTrades error", "error = " + e.getMessage());
        }
    }

    private void setAllOrdersDialog(){
        try {
            llm = new LinearLayoutManager(TradesActivity.this);
            adapterDialogOrders = new OrdersPopupTrades(TradesActivity.this, allOrders, this, this,false,0,false,false);
            rvDialogOrders.setLayoutManager(llm);
            rvDialogOrders.setAdapter(adapterDialogOrders);
        } catch (Exception e) {
            Log.wtf("OrdersPopupTrades error", "error = " + e.getMessage());
        }
    }


    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actions.startStockQuotationService(this);
        Actions.checkSession(this);

        Actions.checkLanguage(this, started);

        Actions.InitializeSessionServiceV2(this);
        firstTime=true;
        running = true;
        setAutoUpdate();
        Log.wtf("onResume", "getTradeInfo");
/*
        try{ if(!isFromOrderDetails)
            Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}
*/

    }

    private void setHeaderColors(){

        if(!BuildConfig.Enable_Markets) {
            if (tradeType == MyApplication.ORDER_BUY) {
                tvColumn1Title.setBackgroundColor(getResources().getColor(R.color.blue_gig));
                tvColumn2Title.setBackgroundColor(getResources().getColor(R.color.blue_gig));
                tvColumn3Title.setBackgroundColor(getResources().getColor(R.color.blue_gig));
                tvColumn4Title.setBackgroundColor(getResources().getColor(R.color.blue_gig));
                tvColumn5Title.setBackgroundColor(getResources().getColor(R.color.blue_gig));
                tvColumn6Title.setBackgroundColor(getResources().getColor(R.color.blue_gig));
            } else {
                tvColumn1Title.setBackgroundColor(getResources().getColor(R.color.red_color));
                tvColumn2Title.setBackgroundColor(getResources().getColor(R.color.red_color));
                tvColumn3Title.setBackgroundColor(getResources().getColor(R.color.red_color));
                tvColumn4Title.setBackgroundColor(getResources().getColor(R.color.red_color));
                tvColumn5Title.setBackgroundColor(getResources().getColor(R.color.red_color));
                tvColumn6Title.setBackgroundColor(getResources().getColor(R.color.red_color));
            }
        }

    }


    private void findViews() {


        TextView tvStocksNo=findViewById(R.id.tvStocksNo);
        if(!BuildConfig.Enable_Markets)
            tvStocksNo.setText(getString(R.string.shares));

        linearTopHighLow=findViewById(R.id.linearTopHighLow);
        linearDataTopNew=findViewById(R.id.linearDataTopNew);
        linearDataTop=findViewById(R.id.linearDataTop);
        llColumn1=findViewById(R.id.llColumn1);
        llColumn2=findViewById(R.id.llColumn2);
        llColumn3=findViewById(R.id.llColumn3);
        llColumn4=findViewById(R.id.llColumn4);
        llColumn5=findViewById(R.id.llColumn5);
        llColumn6=findViewById(R.id.llColumn6);
        tvHighTitleNew=findViewById(R.id.tvHighTitleNew);
        tvHighValueNew=findViewById(R.id.tvHighValueNew);
        tvLowTitleNew=findViewById(R.id.tvLowTitleNew);
        tvLowValueNew=findViewById(R.id.tvLowValueNew);

        tvVolumeTitleNew=findViewById(R.id.tvVolumeTitleNew);
        tvVolumeValueNew=findViewById(R.id.tvVolumeValueNew);

        tvColumn1Title=findViewById(R.id.tvColumn1Title);
        tvColum1Value=findViewById(R.id.tvColum1Value) ;
        tvColumn2Title=findViewById(R.id.tvColumn2Title);
        tvColum2Value=findViewById(R.id.tvColum2Value) ;
        tvColumn3Title=findViewById(R.id.tvColumn3Title);
        tvColum3Value=findViewById(R.id.tvColum3Value);
        tvColumn4Title=findViewById(R.id.tvColumn4Title);
        tvColum4Value=findViewById(R.id.tvColum4Value);
        tvColumn5Title=findViewById(R.id.tvColumn5Title);
        tvColum5Value=findViewById(R.id.tvColum5Value);
        tvColumn6Title=findViewById(R.id.tvColumn6Title);
        tvColum6Value=findViewById(R.id.tvColum6Value);

        if(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true)){
            tvColumn1Title.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvColumn2Title.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvColumn3Title.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvColumn4Title.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvColumn5Title.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvColumn6Title.setTextColor(ContextCompat.getColor(this, R.color.white));
        }




        if(BuildConfig.Enable_Markets) {
            priceFormat = Actions.TwoDecimalThousandsSeparator;
            limitPriceFormat="##.##";
        }
        else {
            priceFormat = Actions.ThreeDecimalThousandsSeparator;
            limitPriceFormat="##.###";
        }



        nsScroll=findViewById(R.id.nsScroll);
        btTriggerLimitMinus=findViewById(R.id.btTriggerLimitMinus) ;
        btTriggerLimitPlus=findViewById(R.id.btTriggerLimitPlus) ;
        etLimitTriggerPrice=findViewById(R.id.etLimitTriggerPrice) ;
        linearTriggerPrice=findViewById(R.id.linearTriggerPrice) ;
        linearSelectAdvanced=findViewById(R.id.linearSelectAdvanced);
        llAdvanced=findViewById(R.id.llAdvanced);
        spSubAccounts = findViewById(R.id.spSubAccounts);
        ImageView ivUserSubAccount=findViewById(R.id.ivUserSubAccount);
        if(BuildConfig.Enable_Markets)
            ivUserSubAccount.setVisibility(View.GONE);
        else
            ivUserSubAccount.setVisibility(View.VISIBLE);
        linearHeaderOrders=findViewById(R.id.linearHeaderOrders);
        // linearFilterType=findViewById(R.id.linearFilterType);
        rvOrders=findViewById(R.id.rvOrders);
        llDuration=findViewById(R.id.llDuration);
        linearSelectedOrder=findViewById(R.id.linearSelectedOrder);
        linearSelectOrder=findViewById(R.id.linearSelectOrder);
        tvSelectedSymbol=findViewById(R.id.tvSelectedSymbol);
        tvSelectedInstruments=findViewById(R.id.tvSelectedInstruments);
        tvSelectedPrice=findViewById(R.id.tvSelectedPrice);
        tvSelectedQuantity=findViewById(R.id.tvSelectedQuantity);
        tvSelectedExecutedQuantity=findViewById(R.id.tvSelectedExecutedQuantity);


        linearSessions = findViewById(R.id.linearSessions);
        if(BuildConfig.Enable_Markets) {
            linearSessions.setVisibility(View.VISIBLE);
            linearTopHighLow.setVisibility(View.GONE);
            linearDataTopNew.setVisibility(View.GONE);
            linearDataTop.setVisibility(View.VISIBLE);
        }
        else {
            linearSessions.setVisibility(View.GONE);
            linearTopHighLow.setVisibility(View.VISIBLE);
            linearDataTopNew.setVisibility(View.VISIBLE);
            linearDataTop.setVisibility(View.GONE);
        }

        btAdd=findViewById(R.id.btAdd);
        // ivShowHideOrders=findViewById(R.id.ivShowHideOrders);

        linearParentOrder=findViewById(R.id.linearParentOrder);
        tvSymbolItemsParent=findViewById(R.id.tvSymbolItemsParent);
        tvPriceItemParent=findViewById(R.id.tvPriceItemParent);
        tvQuantityItemParent=findViewById(R.id.tvQuantityItemParent);
        tvExecutedQuantityItemParent=findViewById(R.id.tvExecutedQuantityItemParent);
        linearMaxFloor=findViewById(R.id.linearMaxFloor);

        etMaxFloor=findViewById(R.id.etMaxFloor);
        // ivArrowOrder=findViewById(R.id.ivArrowOrder);

        loading=findViewById(R.id.loading);

        tvStatusHeader=findViewById(R.id.tvStatusHeader);
        tvStatusHeader.setVisibility(View.INVISIBLE);

        tvExecutedQuantityHeader=findViewById(R.id.tvExecutedQuantityHeader);
        tvExecutedQuantityHeader.setVisibility(View.GONE);



        // linearFilterType.setVisibility(View.GONE);


/*        subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, MyApplication.currentUser.getSubAccounts());
        spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
        spSubAccounts.setSelection(returnAccountIndex());
        spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);
                getTradeInfo = new GetTradeInfo();
                getTradeInfo.execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/

        //<editor-fold desc="find views">
        tvTriggerPricelabel=findViewById(R.id.tvTriggerPricelabel) ;
        tvPriceLable=findViewById(R.id.tvPricelabel) ;
        spOrderType = findViewById(R.id.spOrderType);
        spOrderTypeAdvanced = findViewById(R.id.spOrderTypeAdvanced);
        linearTriggerPrice=findViewById(R.id.linearTriggerPrice) ;

        llOrderType = findViewById(R.id.llOrderType);
        tvOtcPrice = findViewById(R.id.tvOtcPrice);

        rlBuySell = findViewById(R.id.rlBuySell);
        rootLayout = findViewById(R.id.rootLayout);
        cbPrivate = findViewById(R.id.cbPrivate);
        llTradeSection = findViewById(R.id.llTradeSection);
        rlUserHeader = findViewById(R.id.rlUserHeader);
        myToolbar = findViewById(R.id.my_toolbar);
        spLimitedPrice = findViewById(R.id.spLimitedPrice);
        swipeContainer = findViewById(R.id.swipeContainer);

        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
        ivArrow = findViewById(R.id.ivArrow);


        close = findViewById(R.id.close);
        tvUserName = rlUserHeader.findViewById(R.id.tvUserName);
        tvPortfolioNumber = findViewById(R.id.tvPortfolioNumber);
        tvInstrumentValue = findViewById(R.id.tvInstrumentValue);
        tvSessionValue = findViewById(R.id.tvSessionValue);
        tvToolbarStatus = findViewById(R.id.toolbar_status);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        tvPurchasePowerValue = findViewById(R.id.tvPurchasePowerValue);

        if(!BuildConfig.Enable_Markets){
            tvPurchasePowerValue.setBackgroundColor(Color.TRANSPARENT);
        }

        if(!BuildConfig.Enable_Markets  && !MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme),true))
            tvPurchasePowerValue.setTextColor(ContextCompat.getColor(this, R.color.colorValuesInv));

        tvStockTitle = findViewById(R.id.tvStockTitle);


        if (MyApplication.lang == MyApplication.ARABIC) {
            if(stockQuotation.getSymbolAr()!=null && stockQuotation.getNameAr()!=null)
                tvStockTitle.setText(stockQuotation.getSymbolAr()+"-"+stockQuotation.getNameAr()); //getStockID()
        } else {
            if(stockQuotation.getSymbolEn()!=null && stockQuotation.getNameEn()!=null)
                tvStockTitle.setText(stockQuotation.getSymbolEn()+"-"+stockQuotation.getNameEn()); //getStockID()

        }


        tvCloseValue = findViewById(R.id.tvCloseValue);
        tvLastValue = findViewById(R.id.tvLastValue);
        tvBidValue = findViewById(R.id.tvBidValue);
        tvAskValue = findViewById(R.id.tvAskValue);
        tvHighValue = findViewById(R.id.tvHighValue);
        tvLowValue = findViewById(R.id.tvLowValue);
        tvCostValue = findViewById(R.id.tvCostValue);
        tvCommissionValue = findViewById(R.id.tvCommissionValue);
        tvOverallValue = findViewById(R.id.tvOverallValue);
        iv_PredfQtty = findViewById(R.id.iv_PredfQtty);
        BtnCalc = findViewById(R.id.BtnCalc);
        tvFill = findViewById(R.id.tvFill);

        btTimeSales = findViewById(R.id.btTimeSales);
        btOrderBook = findViewById(R.id.btOrderBook);
        btReview = findViewById(R.id.btReview);
        btCancel = findViewById(R.id.btCancel);

        btSell = findViewById(R.id.btSell);
        btBuy = findViewById(R.id.btBuy);
        // btMarketPrice = findViewById(R.id.btMarketPrice);
        //btLimit = findViewById(R.id.btLimit);

        etLimitPrice = findViewById(R.id.etLimitPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etDurationType = findViewById(R.id.etDurationType);

        btLimitPlus = findViewById(R.id.btLimitPlus);
        btLimitMinus = findViewById(R.id.btLimitMinus);
        btQuantityPlus = findViewById(R.id.btQuantityPlus);
        btQuantityMinus = findViewById(R.id.btQuantityMinus);
        btMaxFloorMinus = findViewById(R.id.btMaxFloorMinus);
        btMaxFloorPlus = findViewById(R.id.btMaxFloorPlus);
        //</editor-fold>


        if (tradeType == MyApplication.ORDER_SELL) {

            setSellChecked(true);
        } else {

            setSellChecked(false);
        }

        if (MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {

            tvOtcPrice.setVisibility(View.VISIBLE);
            // llOrderType.setVisibility(View.GONE);
            spOrderType.setVisibility(View.GONE);
            spOrderTypeAdvanced.setVisibility(View.GONE);
            trade.setOrderType(MyApplication.LIMIT);
            trade.setOrdertypeValueEn(Actions.getStringFromValue(MyApplication.LIMIT));
            trade.setOrdertypeValueAr(Actions.getStringFromValue(MyApplication.LIMIT));

            etLimitPrice.setText("0");

        } else {

/*            if(isFromOrderDetails && onlineOrder.getAdvancedOrderTypeID()==0){
                llAdvanced.setVisibility(View.GONE);
            }else {*/
            //   llAdvanced.setVisibility(View.VISIBLE);

            getOrderTypes = new GetOrderTypes();
            getOrderTypes.execute();
        }



        ivArrow.setOnClickListener(v -> showPopupDurationType());
        etDurationType.setOnClickListener(v -> showPopupDurationType());  //Mkobaissy
        iv_PredfQtty.setOnClickListener(v -> showPredefinedQuantity());
        BtnCalc.setOnClickListener(v -> showCalculator());
        tvFill.setOnClickListener(v -> fillData());


        if (onlineOrder.getDurationID() == 6) {
            String goodUntil = onlineOrder.getGoodUntilDate();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
                Date date = sdf.parse(goodUntil);
                goodUntil = sdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            etDurationType.setText(goodUntil);
        }


        swipeContainer.setOnRefreshListener(() -> {
            stopAutoUpdate();
            getTradeInfo = new GetTradeInfo();
            getTradeInfo.executeOnExecutor(MyApplication.threadPoolExecutor);
        });

        swipeContainer.setEnabled(false);

        etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (etQuantity.getText().length() > 0) {

                        try {

                            long quantityValue = Long.parseLong(getNumberFromString(etQuantity.getText().toString()));
                            Log.wtf("long Value", ": " + quantityValue);

                            if (quantityValue > 1000000000) {
                                quantityValue = 1000000000;
                            }
                            quantity = (int) quantityValue;
                            Log.wtf("quantity Value", ": " + quantity);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            quantity = 0;
                        }
                    } else {
                        quantity = 0;
                    }
                    Log.wtf("quantity", ": " + quantity);
                    updateOverAllViews(price, quantity);

                    if (etQuantity.getText().length() != Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator).length()) {
                        etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                        int pos = etQuantity.getText().length();
                        etQuantity.setSelection(pos);
                    }

                }
                return false;
            }
        });



        etQuantity.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {


                etQuantity.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = etQuantity.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = etQuantity.getSelectionStart();
                    if (hasFractionalPart) {
                        etQuantity.setText(df.format(n));
                    } else {
                        etQuantity.setText(dfnd.format(n));
                    }
                    endlen = etQuantity.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= etQuantity.getText().length()) {
                        etQuantity.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        etQuantity.setSelection(etQuantity.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }

                etQuantity.addTextChangedListener(this);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                long quantityValue = Long.parseLong(getNumberFromString(etQuantity.getText().toString()));
                Log.wtf("long Value", ": " + quantityValue);

                if (quantityValue > 1000000000) {
                    quantityValue = 1000000000;
                }
                quantity = (int) quantityValue;
                try{updateOverAllViews(price, quantity);}catch (Exception e){
                    Log.wtf("exception_update",e.toString());
                }
            }
        });







        etMaxFloor.addTextChangedListener(new NumberTextWatcher(etMaxFloor));




        if(BuildConfig.Enable_Markets) {
            tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.even_green_color));
            btReview.setBackgroundColor(getResources().getColor(R.color.green_color));
        }else {
            if(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true))
                tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.blue_gig_light));
            btReview.setBackgroundColor(getResources().getColor(R.color.blue_gig));
        }


        date = (view, year, monthOfYear, dayOfMonth) -> {
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDurationType);
            }
        };

        if (getIntent().hasExtra("action")) {

            tradeType = getIntent().getExtras().getInt("action");

            if (tradeType == MyApplication.ORDER_BUY)
                btBuy.performClick();
            else
                btSell.performClick();
        }


        if (isFromOrderDetails) {
            rlBuySell.setEnabled(false);
            btSell.setEnabled(false);
            btBuy.setEnabled(false);

            Log.wtf("trade_price_1",onlineOrder.getPrice()+"");
            etLimitPrice.setText(String.valueOf(onlineOrder.getPrice()));
            try{
                etLimitTriggerPrice.setText(onlineOrder.getTriggerPrice()+"");
                Log.wtf("bobo",onlineOrder.getTriggerPrice()+"aa");
            }catch (Exception e){}
    /*        if (trade.getOrderType() == MyApplication.MARKET_PRICE) {

                btMarketPrice.performClick();
            } else {

                btLimit.performClick();
            }
*/
//        }
//        if (isFromOrderDetails) {

            TextView quantity_title = findViewById(R.id.quantity_title);
            quantity_title.setText(getResources().getString(R.string.fs_rem_qtty));

            if (onlineOrder.getStatusID() == 16) {
                cbPrivate.setChecked(true);
            } else {
                cbPrivate.setChecked(false);
            }
            cbPrivate.setEnabled(false);
        }

        etLimitPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {

                    if (!hasFocus) {

                        if (!MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {

                            if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {

                                if ((Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > HiLimit) || (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0)) {

                                    if (tradeType == MyApplication.ORDER_SELL) {
                                        etLimitPrice.setText("" + stockQuotation.getHiLimit());
                                        Actions.CreateDialog(TradesActivity.this,getString(R.string.check_price_msg),false,false);

                                    } else {
                                        etLimitPrice.setText("" + stockQuotation.getLowlimit());
                                        Actions.CreateDialog(TradesActivity.this,getString(R.string.check_price_msg),false,false);

                                    }

                                }
                            } else {

                                if ((Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > stockQuotation.getHiLimit())
                                        || (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < stockQuotation.getLowlimit())) {

                                    if (tradeType == MyApplication.ORDER_SELL) {
                                        etLimitPrice.setText("" + stockQuotation.getHiLimit());
                                        Actions.CreateDialog(TradesActivity.this,getString(R.string.check_price_msg),false,false);

                                    } else {
                                        etLimitPrice.setText("" + stockQuotation.getLowlimit());
                                        Actions.CreateDialog(TradesActivity.this,getString(R.string.check_price_msg),false,false);


                                    }

                                }
                            }

                            if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > 100.9) {
                                etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), priceFormat));
                            } else {
                                etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), priceFormat));
                            }
                            price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                            setTick();

                        } else {

                            if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > 100.9) {
                                etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), priceFormat));
                            } else {
                                etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), priceFormat));
                            }
                            price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                            setTick();

                        }



                    }


                }catch (Exception e){}
            }
        });




    /*    etLimitPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.length() == 0) {

                   // checkPriceLimit();
                } else {

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                checkPriceLimit();
            }
        });*/


        etLimitTriggerPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString())) > 100.9) {
                        etLimitTriggerPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString())), priceFormat));
                    } else {
                        etLimitTriggerPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString())), priceFormat));
                    }
                    triggerPrice = Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString()));
                    setTriggerTick();
                }
            }
        });


        if(!BuildConfig.Enable_Markets) {
            linearMaxFloor.setVisibility(View.VISIBLE);
            spOrderTypeAdvanced.setVisibility(View.GONE);
            linearSelectOrder.setVisibility(View.GONE);
            linearSelectedOrder.setVisibility(View.GONE);

        }

    }


    public void setTick() {
        ticketQtt = 1;
        for (int i = 0; i < MyApplication.units.size(); i++) {
            if (MyApplication.units.get(i).getFromPrice() <= price && price <= MyApplication.units.get(i).getToPrice()) {
                ticketPrice = MyApplication.units.get(i).getPriceUnit();
      /*          if(!BuildConfig.Enable_Markets && ticketPrice==0.001)
                    ticketPrice=0.01;*/
                Log.wtf("setTick - Price Change", "price = " + price + " / ticketPrice = " + ticketPrice + " / ticketQtt = " + ticketQtt);
            }
        }

        if (BuildConfig.Enable_Markets && price > 100.9) {
            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.NoDecimalSeparator));
        }else if(!BuildConfig.Enable_Markets && price > 100.9){
            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), limitPriceFormat));

        }
    }


    private String setEtTriggerPriceValue() {
        Log.wtf("setEtTriggerPriceValue", "setEtTriggerPriceValue");

        if (!isFromOrderDetails) {

            triggerPrice = setPriceWithMarketPrice(true);

        } else {

            triggerPrice = onlineOrder.getTriggerPrice();
        }


        setTriggerTick();

        String priceValue = "";
        if (triggerPrice > 100.9) {

            priceValue = Actions.formatNumber(triggerPrice, Actions.NoDecimalThousandsSeparator);
            return priceValue;

        } else {

            if (triggerPrice == 0.0)
                return "0.0";
            else {

                priceValue = Actions.formatNumber(triggerPrice, priceFormat);
                return priceValue;
            }
        }
    }

    public void setTriggerTick() {
        ticketQtt = 1;
        for (int i = 0; i < MyApplication.units.size(); i++) {
            if (MyApplication.units.get(i).getFromPrice() <= triggerPrice && triggerPrice <= MyApplication.units.get(i).getToPrice()) {
                ticketTriggerPrice = MyApplication.units.get(i).getPriceUnit();
                Log.wtf("setTick - Price Change", "triggerPrice = " + triggerPrice + " / ticketTriggerPrice = " + ticketTriggerPrice + " / ticketQtt = " + ticketQtt);
            }
        }

        if (triggerPrice > 100.9 && BuildConfig.Enable_Markets) {
            etLimitTriggerPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString())), Actions.NoDecimalSeparator));
        }
        else if(triggerPrice > 100.9 && !BuildConfig.Enable_Markets){
            etLimitTriggerPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString())), limitPriceFormat));

        }
    }


    public void showCalculator() {
        try {
            startActivity(new Intent(TradesActivity.this, SiteMapDataActivity.class)
                    .putExtra("calcualtor", true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fillData() {
        Log.wtf("tvFill", "fillData ");

        // int qtyUpdate=

        if (tradeType == MyApplication.ORDER_SELL) {
            Log.wtf("tradeType", "ORDER_SELL ");
         /*   if(isFromOrderDetails)
               quantity = trade.getAvailableShareCount()+onlineOrder.getQuantity();
            else*/
            quantity = trade.getAvailableShareCount();

//details =>online order qty-executed qty(remaing)
            etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalSeparator));
            updateOverAllViews(price, quantity);
        } else {
            Double p=0.0;
            if(!tvPurchasePowerValue.getText().toString().isEmpty())
                p=Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
            if(!tvPurchasePowerValue.getText().toString().matches("0.0")  && (p > 0.0 || orderType == MyApplication.MARKET_PRICE)) {
                showFillDataDialog();
            }



            Log.wtf("tradeType", "ORDER_BUY ");
    /*        price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));


            if (trade.getPurchasePower() != 0 && price != 0) {

                for (int i =z 0; i < MyApplication.allBrokerageFees.size(); i++) {
                    if (MyApplication.allBrokerageFees.get(i).getInstrumentId().equals(trade.getStockQuotation().getInstrumentId())) {
                        BrokerageFee data = MyApplication.allBrokerageFees.get(i);
                        double fr = trade.getPurchasePower() - data.getClearing();
                        double sr = price * (1 + data.getTotalBrokerageFee());
                         double rawqty =(BuildConfig.Enable_Markets? ((fr / sr) * 1000):(fr / sr));
                        quantity = (int) rawqty;

                        if(isFromOrderDetails)
                            quantity = trade.getAvailableShareCount()+onlineOrder.getQuantity();

                        //quantity=onlineqty+

                        etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalSeparator));
                    }
                }


            }*/
        }

    }


    private void updateQty(){
        //    if (trade.getPurchasePower() != 0 && price != 0) {

  /*      price=Double.parseDouble(stockQuotation.gethi()+"");
    }    if(orderType == MyApplication.MARKET_PRICE) {
    */

        for (int i = 0; i < MyApplication.allBrokerageFees.size(); i++) {

            try{
                if (MyApplication.allBrokerageFees.get(i).getInstrumentId().equals(stockQuotation.getInstrumentId())) {
                    BrokerageFee brokerageFee = MyApplication.allBrokerageFees.get(i);

                    Double totalBrokerageFee=brokerageFee.getTotalBrokerageFee();
                    Double minimumBrokerageFee=brokerageFee.getMinimumBrokerageFee();
                    Double commission=0.0;
                    if(totalBrokerageFee < minimumBrokerageFee) {
                        commission = minimumBrokerageFee;
                    }

                    Double purchasePower=0.0;
                    if(!etPurchasePowerPopup.getText().toString().isEmpty() && Double.parseDouble(etPurchasePowerPopup.getText().toString().replaceAll("\\D+",""))<=trade.getPurchasePower() && Double.parseDouble(etPurchasePowerPopup.getText().toString().replaceAll("\\D+",""))> 0)
                        purchasePower=Double.parseDouble(etPurchasePowerPopup.getText().toString().replaceAll("\\D+",""));
                    else
                        purchasePower=trade.getPurchasePower();

                    //(Double.parseDouble(etPurchasePowerPopup.getText().toString().replaceAll("\\D+","")))
                    double fr = purchasePower- brokerageFee.getClearing()-commission;
                    double sr = price * (1 +(commission>0?0: brokerageFee.getTotalBrokerageFee()));
                    double rawqty = (BuildConfig.Enable_Markets ? ((fr / sr) * 1000) : (fr / sr));
                    quantity = (int) rawqty;

                    if (isFromOrderDetails)
                        quantity = trade.getAvailableShareCount() + onlineOrder.getQuantity();

                    //quantity=onlineqty+
                    if(quantity>=0)
                      etQuantityPopup.setText(quantity+"");
                    else
                        etQuantityPopup.setText("0");
                    // etQuantityPopup.setText(Actions.formatNumber(quantity, Actions.NoDecimalSeparator));
                }}catch (Exception e){
                    Log.wtf("fill_exception",e.toString());
               }



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


    public void setOrderDuration(int position) {
        String txt = "";
        orderDurationType = MyApplication.allOrderDurationType.get(position);

        if (MyApplication.allOrderDurationType.get(position).getID() == 6) {
            txt = "" + onlineOrder.getGoodUntilDate();
        } else {
            if (MyApplication.lang == MyApplication.ARABIC) {
                etDurationType.setTypeface(MyApplication.droidbold);
                txt = "" + MyApplication.allOrderDurationType.get(position).getDescriptionAr();
            } else {
                etDurationType.setTypeface(MyApplication.giloryBold);
                txt = "" + MyApplication.allOrderDurationType.get(position).getDescriptionEn();
            }
        }

        etDurationType.setText(txt);
    }


    private void showPopupDurationType() {

        builder = new AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialog = inflater.inflate(R.layout.popup_order_duration_type, null);

        rvDurationType = editDialog.findViewById(R.id.rvDurationType);

        layoutManager = new LinearLayoutManager(this);
        rvDurationType.setLayoutManager(layoutManager);


        adapter = new OrderDurationTypeAdapter(this, MyApplication.allOrderDurationType, this, selectedPos);
        rvDurationType.setAdapter(adapter);

        builder.setView(editDialog);
        dialog = builder.create();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.wtf("date", "value change");

                orderDurationType = MyApplication.allOrderDurationType.get(Actions.returnDurationIndex(6));
                updateLabel(etDurationType);
                selectedPos = Actions.returnDurationIndex(6);
                dialog.dismiss();
            }
        };

        dialog.show();
    }


    private void showPredefinedQuantity() {

        builder = new AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialog = inflater.inflate(R.layout.popup_order_duration_type, null);

        rvDurationType = editDialog.findViewById(R.id.rvDurationType);

        layoutManager = new LinearLayoutManager(this);
        rvDurationType.setLayoutManager(layoutManager);

        predefinedQuantityData = new int[]{5000, 10000, 15000, 20000, 30000, 50000, 100000, 200000, 300000};

        predfQttyAdapter = new PredefineQuantityAdapter(this, predefinedQuantityData, this, 0);
        rvDurationType.setAdapter(predfQttyAdapter);

        builder.setView(editDialog);
        dialog = builder.create();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.wtf("date", "value change");

                orderDurationType = MyApplication.allOrderDurationType.get(Actions.returnDurationIndex(6));
                updateLabel(etDurationType);
                selectedPos = Actions.returnDurationIndex(6);
                dialog.dismiss();
            }
        };

        dialog.show();
    }


    @Override
    public void onItemClicked(View v, int position) {

        if(v.getId()==R.id.rllayout){
            try {
                if(adapterStockPopup.getFilteredItems().get(position).getStockID()!=-1){
                    stocksDialog.dismiss();
                    loading.setVisibility(View.VISIBLE);
                    // Toast.makeText(getApplicationContext(),"aaa",Toast.LENGTH_LONG).show();
                    boolean callDuration=false;
                    if(stockId==0)
                        callDuration=true;
                    stopAutoUpdate();


                    stockId=adapterStockPopup.getFilteredItems().get(position).getStockID();
                    MyApplication.selectedStockId=stockId;
                    if (orderTypeAdvanced == MyApplication.OCA && allOrders.size() == 0 || orderTypeAdvanced == MyApplication.OCA && ocaParentTrade.getId() == 0)
                        ocaParentTrade = trade;

                    getOrderTypes = new GetOrderTypes();
                    getOrderTypes.execute();
                    getTradeInfo = new GetTradeInfo();
                    getTradeInfo.execute();
                    if(callDuration){
                        setOrderDurTypes();
                    }

                }





           /*     tvStock.setText(adapterStockPopup.getFilteredItems().get(position).getStockID()+"-"+(MyApplication.lang == MyApplication.ARABIC ?adapterStockPopup.getFilteredItems().get(position).getSymbolAr():adapterStockPopup.getFilteredItems().get(position).getSymbolEn()));
                selectedStockId = adapterStockPopup.getFilteredItems().get(position).getStockID();*/

            }catch (Exception e){}
        }
        else if(v.getId()==R.id.linearPopupOrder){

            try{

                relatedOnlineOrder=arrayFilteredOrderDialog.get(position);

            }catch (Exception e){
                relatedOnlineOrder=new OnlineOrder();
            }
            try{
                selectedRelatedOrderId=arrayFilteredOrderDialog.get(position).getId();
            }catch (Exception e){
                selectedRelatedOrderId=0;
            }
            try{tvSelectedSymbol.setText("# "+arrayFilteredOrderDialog.get(position).getID());}catch (Exception e){}
            try{tvSelectedExecutedQuantity.setVisibility(View.GONE);}catch (Exception e){}
            try {
                tvSelectedInstruments.setVisibility(View.GONE);
            }catch (Exception e){}
            try{tvSelectedPrice.setVisibility(View.GONE);}catch (Exception e){}
            try{tvSelectedQuantity.setVisibility(View.GONE);}catch (Exception e){}


           /*           try{tvSelectedSymbol.setText(arrayFilteredOrderDialog.get(position).getStockSymbol());}catch (Exception e){}
           try{tvSelectedExecutedQuantity.setText(Actions.formatNumber(arrayFilteredOrderDialog.get(position).getQuantityExecuted(), Actions.NoDecimalThousandsSeparator));}catch (Exception e){}
           try {
               tvSelectedInstruments.setText(MyApplication.lang == MyApplication.ENGLISH ?
                       MyApplication.instrumentsHashmap.get(arrayFilteredOrderDialog.get(position).getInstrumentID()).getInstrumentNameEn() :
                       MyApplication.instrumentsHashmap.get(arrayFilteredOrderDialog.get(position).getInstrumentID()).getInstrumentNameAr());
           }catch (Exception e){}
           try{tvSelectedPrice.setText(String.valueOf(arrayFilteredOrderDialog.get(position).getPrice()));}catch (Exception e){}
           try{tvSelectedQuantity.setText(Actions.formatNumber(arrayFilteredOrderDialog.get(position).getQuantity(), Actions.NoDecimalThousandsSeparator));}catch (Exception e){}
          */

            try{ordersDialog.dismiss();}catch (Exception e){}
            // linearSelectOrder.setVisibility(View.GONE);
            linearSelectedOrder.setVisibility(View.VISIBLE);


        }



        else if (MyApplication.allOrderDurationType.get(position).getID() != 6) {
            if (!Actions.isMarketOpen() && BuildConfig.Enable_Markets) {
                if (MyApplication.allOrderDurationType.get(position).getID() == 1 || ((MyApplication.marketStatus.getStatusID() != MyApplication.MARKET_CLOSED || MyApplication.marketStatus.getStatusID() != MyApplication.Enquiry) && position == 0)) {
                    orderDurationType = MyApplication.allOrderDurationType.get(position);
                    selectedPos = position;
                    try {
                        setOrderDuration(position);
                    }catch (Exception e){}
                    dialog.dismiss();
                }
            }

            else if (!Actions.isMarketOpen() && !BuildConfig.Enable_Markets) {
                if (MyApplication.allOrderDurationType.get(position).getID() == 1 || position == 0) {
                    orderDurationType = MyApplication.allOrderDurationType.get(position);
                    selectedPos = position;
                    try {
                        setOrderDuration(position);
                    }catch (Exception e){}
                    dialog.dismiss();
                }
            }


            else {
                orderDurationType = MyApplication.allOrderDurationType.get(position);
                selectedPos = position;
                try {
                    setOrderDuration(position);
                }catch (Exception e){}
                dialog.dismiss();
            }
        } else {
            showDateDialog();
        }
    }


    @Override
    public void onItemClickedd(View v, int position) {

        quantity = predefinedQuantityData[position];
        Log.wtf("onItemClickedd", "quantity = " + quantity);
        etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
        dialog.dismiss();
    }


    public void setLimit(View v) {
        try {
            if (etLimitPrice.getText().length() > 0) {

                try {
                    price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    price = 0.0;
                }
            } else {

                try {
                    if (orderType == MyApplication.LIMIT) {

                        if (tradeType == MyApplication.ORDER_SELL)
                            price = stockQuotation.getHiLimit();
                        else
                            price = stockQuotation.getLowlimit();
                    } else {

                        if (tradeType == MyApplication.ORDER_SELL)
                            price = stockQuotation.getLowlimit();
                        else
                            price = stockQuotation.getHiLimit();
                    }
                } catch (Exception e) {
                    price = 0.0;
                }
            }

            switch (v.getId()) {

                case R.id.btLimitMinus:
                    String quantityText = "";
                    if(stockId!=0) {

                        if (orderType != MyApplication.MARKET_PRICE) {
                            if (setMaxMin) {
                                if (price > 1) {
                                    double pr = price - ticketPrice;
                                    if (pr >= 1) {
                                        price = pr;
                                        etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                    }
                                } else {

                                    etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                }

                            } else {


                                if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {
                                    if (price > 0) {
                                        double pr = price - ticketPrice;
                                        if (pr >= 0) {
                                            price = pr;
                                            etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                        }
                                    } else {

                                        etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                    }
                                } else {
                                    if (price > stockQuotation.getLowlimit()) {
                                        double pr = price - ticketPrice;
                                        if (pr >= stockQuotation.getLowlimit()) {
                                            price = pr;
                                            etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                        }
                                    } else {

                                        etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                    }
                                }
                            }
                        }


                        quantityText = etQuantity.getText().toString();
                        if (isFromOrderDetails && quantityText.length() > 0) {
                            quantity = Integer.parseInt(getNumberFromString(quantityText));
                        }
                        updateOverAllViews(price, quantity);
                        setTick();


                    }else {
                        if(!isAutoUpdate) {
                            price = 0;
                            etLimitPrice.setText("0");
                        }
                    }


                    break;


                case R.id.btTriggerLimitMinus:

                    if (triggerPrice > 0) {
                        double pr = triggerPrice - ticketTriggerPrice;
                        if (pr >= 0) {
                            triggerPrice = pr;
                            etLimitTriggerPrice.setText(Actions.formatNumber(triggerPrice, limitPriceFormat));
                        }
                    } else {

                        etLimitTriggerPrice.setText(Actions.formatNumber(triggerPrice, limitPriceFormat));
                    }

                    setTriggerTick();

                    break;


                case R.id.btTriggerLimitPlus:
                    if (Double.parseDouble(getNumberFromString(etLimitTriggerPrice.getText().toString())) < 0) {
                        etLimitTriggerPrice.setText(Actions.formatNumber(0, limitPriceFormat));
                        etLimitTriggerPrice.setText(0);
                        triggerPrice = 0;
                    } else {
                        double pr = triggerPrice + ticketTriggerPrice;
                        triggerPrice = pr;
                        etLimitTriggerPrice.setText(Actions.formatNumber(pr, limitPriceFormat));

                    }

                    setTriggerTick();
                    break;


                case R.id.btLimitPlus:
                    if (stockId != 0) {
                        if (orderType != MyApplication.MARKET_PRICE) {

                            if (setMaxMin) {

                                int maxHigh = 1000000;

                                if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0) {
                                    if (BuildConfig.Enable_Markets)
                                        etLimitPrice.setText(Actions.formatNumber(0, limitPriceFormat));
                                    else
                                        etLimitPrice.setText(Actions.formatNumber(0, limitPriceFormat));
                                    price = 0;
                                } else if (price < maxHigh) {
                                    double pr = price + ticketPrice;
                                    if (pr <= maxHigh) {
                                        price = pr;
                                        etLimitPrice.setText(Actions.formatNumber(pr, limitPriceFormat));
                                    }
                                } else {
                                    etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                }


                            } else {

                                if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {

                                    if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0) {
                                        etLimitPrice.setText(Actions.formatNumber(0, limitPriceFormat));
                                        price = 0;
                                    } else if (price < HiLimit) {
                                        double pr = price + ticketPrice;
                                        if (pr <= HiLimit) {
                                            price = pr;
                                            etLimitPrice.setText(Actions.formatNumber(pr, limitPriceFormat));
                                        }
                                    } else {
                                        etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                    }
                                } else {
                                    if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < stockQuotation.getLowlimit()) {
                                        etLimitPrice.setText(Actions.formatNumber(stockQuotation.getLowlimit(), limitPriceFormat));
                                        price = stockQuotation.getLowlimit();
                                    } else if (price < stockQuotation.getHiLimit()) {
                                        double pr = price + ticketPrice;
                                        if (pr <= stockQuotation.getHiLimit()) {
                                            price = pr;
                                            etLimitPrice.setText(Actions.formatNumber(pr, limitPriceFormat));
                                        }
                                    } else {
                                        etLimitPrice.setText(Actions.formatNumber(price, limitPriceFormat));
                                    }
                                }
                            }


                        }

                        quantityText = etQuantity.getText().toString();
                        if (isFromOrderDetails && quantityText.length() > 0) {
                            quantity = Integer.parseInt(getNumberFromString(quantityText));
                        }
                        updateOverAllViews(price, quantity);
                        setTick();
                    }else {
                        if(!isAutoUpdate) {
                            price = 0;
                            etLimitPrice.setText("0");
                        }
                    }
                    break;
            }
        }catch (Exception e){}
    }


    private Boolean checkPriceLimit(){
        Boolean isCheck=true;
        Double pr=0.0;
                if(!etLimitPrice.getText().toString().isEmpty()){
                    pr=  Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                }
          if(pr!=0.0){
           if(pr >= stockQuotation.getLowlimit() || pr <= stockQuotation.getHiLimit())
               isCheck=false;
       /*       // etLimitPrice.setText(stockQuotation.getLowlimit()+"");
               isCheck=true;
              // Actions.CreateDialog(this,getString(R.string.check_price_msg),false,false);
           } else if(pr <= stockQuotation.getHiLimit()){
             //  etLimitPrice.setText(stockQuotation.getHighlimit()+"");
              // Actions.CreateDialog(this,getString(R.string.check_price_msg),false,false);

           }   */

        }
          return isCheck;
    }




    private void setInitialData() {

        try {
            tvToolbarStatus.setText(MyApplication.marketStatus.getStatusName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvPortfolioNumber.setText(String.valueOf(MyApplication.currentUser.getPortfolioNumber()));
        if(stockId!=0) {
            Log.wtf("trade_price_2",setEtPriceValue());
            if(!isAutoUpdate) {
                etLimitPrice.setText(setEtPriceValue());
                etLimitTriggerPrice.setText(setEtTriggerPriceValue());
            }
        }else {
            etLimitPrice.setText("0");
        }

        trade.setPortfolioId(MyApplication.currentUser.getPortfolioId());
        trade.setPortfolioNumber(MyApplication.currentUser.getPortfolioNumber());
        //trade.setOrderType(LIMIT);

        if (isFromOrderDetails) {

            int remainingQuantity = onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted();
            etQuantity.setText(String.valueOf(remainingQuantity));
            price = onlineOrder.getPrice();
            triggerPrice=onlineOrder.getTriggerPrice();
            updateOverAllViews(price, remainingQuantity);
        }
    }


    private void setMaxFloorEdit(Boolean enable){
        etMaxFloor.setEnabled(enable);
        btMaxFloorMinus.setEnabled(enable);
        btMaxFloorPlus.setEnabled(enable);
    }


    private void showDateDialog() {

        Log.wtf("open", "date");
        myCalendar = Calendar.getInstance();
        myCalendar.roll(Calendar.DATE, 1);
        Calendar calendar = new GregorianCalendar();

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatter);
        int dyear = myCalendar.get(Calendar.YEAR), dmonth = myCalendar.get(Calendar.MONTH), dday = myCalendar.get(Calendar.DAY_OF_MONTH);

        if (selectedPos == 5) {
            try {
                calendar.setTime(dateFormat.parse(etDurationType.getText().toString()));
                dyear = calendar.get(Calendar.YEAR);
                dmonth = calendar.get(Calendar.MONTH);
                dday = calendar.get(Calendar.DAY_OF_MONTH);

            } catch (Exception e) {
                Log.wtf("Exception", "Exception" + e.getMessage());
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(TradesActivity.this, date, dyear, dmonth, dday);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogs, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    Log.wtf("calendr ", "btn cancel click");
                    dialogs.dismiss();
                    //    dialog.dismiss();
                }
            }
        });

        datePickerDialog.show();
    }


    private void updateLabel(EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat(setDateFormatter, Locale.ENGLISH);
        try{editText.setText((MyApplication.lang == MyApplication.ARABIC ? MyApplication.allOrderDurationType.get(5).getDescriptionAr():MyApplication.allOrderDurationType.get(5).getDescriptionEn())+" - "+sdf.format(myCalendar.getTime()));}catch (Exception e){}
    }


    public void close(View v) {
        finish();
    }

    //<editor-fold desc="Design">
    private void setSellChecked(boolean sellChecked) {

        if (sellChecked) {

            btSell.setTextColor(ContextCompat.getColor(this, R.color.white));
            btSell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_sell_selected));

            btBuy.setTextColor(ContextCompat.getColor(this, R.color.red_color));
            btBuy.setBackground(ContextCompat.getDrawable(this, R.drawable.border_buy_not_selected));

            llTradeSection.setBackgroundColor(ContextCompat.getColor(this, R.color.trade_sell_color));
        } else {

            btBuy.setTextColor(ContextCompat.getColor(this, R.color.white));

            if(BuildConfig.Enable_Markets) {
                btBuy.setBackground(ContextCompat.getDrawable(this, R.drawable.border_buy_selected));

                btSell.setTextColor(ContextCompat.getColor(this, R.color.green_color));
                btSell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_sell_not_selected));
            }else {
                btBuy.setBackground(ContextCompat.getDrawable(this, R.drawable.border_buy_gig_selected));

                btSell.setTextColor(ContextCompat.getColor(this, R.color.blue_gig));
                btSell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_sell_gig_not_selected));
            }

            llTradeSection.setBackgroundColor(ContextCompat.getColor(this, R.color.trade_buy_color));
        }
    }




    private void showLimitPrice(boolean show) {
        if (show) {
            etLimitPrice.setBackgroundColor(getResources().getColor(R.color.colorLight));
            etLimitPrice.setTextColor(getResources().getColor(R.color.black));

            etLimitPrice.setEnabled(true);
            btLimitMinus.setEnabled(true);
            btLimitPlus.setEnabled(true);
        } else {
            etLimitPrice.setBackgroundColor(getResources().getColor(R.color.lightgrey));
            etLimitPrice.setTextColor(getResources().getColor(R.color.black));
            etLimitPrice.setText("0.0");
            etLimitPrice.setEnabled(false);
            btLimitMinus.setEnabled(false);
            btLimitPlus.setEnabled(false);
        }

    /*    if (!Actions.isMarketOpen()) {
            btLimit.setAlpha((float) 0.5);// setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_selected));
        }*/
    }
    //</editor-fold>


    public void setTradeType(View v) {

        LinearLayout.LayoutParams paramsElev = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams paramsNull = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        paramsElev.setMargins(10, 10, 10, 10);
        paramsNull.setMargins(0, 0, 0, 0);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        switch (v.getId()) {

            case R.id.btSell:
                sellClick();
                break;
//
            case R.id.btBuy:
                buyClick();
                break;
        }

        setPriceWithMarketPrice(true);
    }

    private void buyClick(){

        if(stockId!=0){
            tradeType = MyApplication.ORDER_BUY;
            // etQuantity.setText("0");
            firstTime=false;
            setSellChecked(false);
            if(stockId!=0 && !isAutoUpdate)
                etLimitPrice.setText(setEtPriceValue());
            else if(!isAutoUpdate)
                etLimitPrice.setText("0");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    tvPurchasePowerValue.setLayoutParams(params);
//                    tvQuantityValue.setLayoutParams(paramsNull);

                tvPurchasePowerValue.setElevation(getResources().getDimension(R.dimen.padding));
                tvQuantityValue.setElevation(0);

                if(BuildConfig.Enable_Markets) {
                    tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.even_green_color));
                    btReview.setBackgroundColor(getResources().getColor(R.color.green_color));
                }else {
             /*   if(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true))
                  tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.blue_gig_light));
             */   btReview.setBackgroundColor(getResources().getColor(R.color.blue_gig));
                }
//                    tvQuantityValue.setBackgroundColor(getResources().getColor(R.color.colorLight));
                tvQuantityValue.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorMedium : R.color.colorMediumInv));
            }

            trade.setTradeTypeID(tradeType);
            updateOverAllViews(price, quantity);
        }else {
            if (!isAutoUpdate) {
                etLimitPrice.setText("0");
                price = 0;
            }
        }
        setHeaderColors();

    }


    private void sellClick(){
        if(stockId!=0) {
            tradeType = MyApplication.ORDER_SELL;
            // etQuantity.setText("0");
            setSellChecked(true);
            firstTime = false;
            if (stockId != 0 && !isAutoUpdate)
                etLimitPrice.setText(setEtPriceValue());
            else if(!isAutoUpdate)
                etLimitPrice.setText("0");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                tvQuantityValue.setElevation(getResources().getDimension(R.dimen.padding));
                tvPurchasePowerValue.setElevation(0);
                if(BuildConfig.Enable_Markets)
                    tvQuantityValue.setBackgroundColor(getResources().getColor(R.color.even_red_color));
                else {
                    if(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true))
                        tvQuantityValue.setBackgroundColor(getResources().getColor(R.color.even_red_color));
                }
                btReview.setBackgroundColor(getResources().getColor(R.color.red_color));
//                    tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.colorLight));
                tvPurchasePowerValue.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorMedium : R.color.colorMediumInv));
            }
            trade.setTradeTypeID(tradeType);
            updateOverAllViews(price, quantity);
        }else if(!isAutoUpdate){

            etLimitPrice.setText("0");
            price=0;


        }
        setHeaderColors();
    }

/*    public void setOrderType(View v) {

        switch (v.getId()) {

            case R.id.btLimit:
                orderType = MyApplication.LIMIT;
                setLimitChecked(true);
                showLimitPrice(true);

                etLimitPrice.setText(setEtPriceValue());

                trade.setOrderType(orderType);
                if(getIndexFromId(orderType)!=-1) {
                    trade.setOrdertypeValueEn(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionEn());
                    trade.setOrdertypeValueAr(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionAr());
                }
                updateOverAllViews(price, quantity);
                break;

            case R.id.btMarketPrice:
                orderType = MyApplication.MARKET_PRICE;
                setLimitChecked(false);
                showLimitPrice(false);

                etLimitPrice.setText(setEtPriceValue());

                trade.setOrderType(orderType);
                if(getIndexFromId(orderType)!=-1) {
                    trade.setOrdertypeValueEn(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionEn());
                    trade.setOrdertypeValueAr(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionAr());
                }
                updateOverAllViews(price, quantity);
                break;
        }
    }*/


    private String setEtPriceValue() {
        Log.wtf("setEtPriceValue", "setEtPriceValue");

        if(!isAutoUpdate) {
            if (!isFromOrderDetails) {

                price = setPriceWithMarketPrice(true);
                Log.wtf("trade_price_3", price + "");
            } else {

                price = onlineOrder.getPrice();
                Log.wtf("trade_price_4", price + "");
            }

            if (orderType == MyApplication.MARKET_IF_TOUCHED || orderType == MyApplication.MARKET_PRICE) {
                if (!isAutoUpdate) {
                    etLimitPrice.setText("");
                    price = 0;
                }
            }
        }

        trade.setOrderType(orderType);
        if(getIndexFromId(orderType)!=-1) {
            trade.setOrdertypeValueEn(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionEn());
            trade.setOrdertypeValueAr(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionAr());
        }
        updateOverAllViews(price, quantity);
        setTick();

        String priceValue = "";
        if (price > 100.9) {

            if(BuildConfig.Enable_Markets)
                priceValue = Actions.formatNumber(price, Actions.NoDecimalThousandsSeparator);
            else
                priceValue=price+"";
            return priceValue;

        } else {

            if (price == 0.0)
                return "0.0";
            else {

                priceValue = Actions.formatNumber(price, priceFormat);
                return priceValue;
            }
        }
    }


    private void updateOverAllViews(double price, int quantity) {
        setTradeData(quantity, price, tradeType);
        if (quantity == 0) {
            tvCostValue.setText("0");
            tvCommissionValue.setText("0");
            tvOverallValue.setText("0");
        } else {
            if(orderType == MyApplication.MARKET_PRICE) {


                double total = (!BuildConfig.Enable_Markets ? (tradeType ==MyApplication.ORDER_SELL ? stockQuotation.getLowlimit()*quantity : stockQuotation.getHiLimit() * quantity)  : (price * quantity));
                trade.setOverallTotal(total);
            }


            tvCostValue.setText(Actions.formatNumber(trade.getOverallTotal(), Actions.ThreeDecimalThousandsSeparator));

            tvCommissionValue.setText(Actions.formatNumber(trade.getCommission(), Actions.ThreeDecimalThousandsSeparator));

            if (tradeType == MyApplication.ORDER_BUY)
                tvOverallValue.setText(Actions.formatNumber(trade.getOverallTotal() + trade.getCommission(), Actions.ThreeDecimalThousandsSeparator));
            else
                tvOverallValue.setText(Actions.formatNumber(trade.getOverallTotal() - trade.getCommission(), Actions.ThreeDecimalThousandsSeparator));


            // tvOverallValue.setText(Actions.formatNumber(trade.getCost(), Actions.ThreeDecimalThousandsSeparator));
        }
    }


    public void setQuantity(View v) {

        if (etQuantity.getText().length() > 0) {
            try {
                quantity = Integer.parseInt(getNumberFromString(etQuantity.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                quantity = 0;
            }

        } else {

            quantity = 0;
        }

        switch (v.getId()) {

            case R.id.btQuantityMinus:

                if (quantity > 1) {

                    quantity -= ticketQtt;
                    etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                } else {
                    quantity = 0;
                    etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                }
                updateOverAllViews(price, quantity);
                break;

            case R.id.btQuantityPlus:
                Log.wtf("btQuantityPlus", "quantity = " + quantity);
                quantity += ticketQtt;
                etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                updateOverAllViews(price, quantity);
                break;
        }
    }





    public void setMaxFloor(View v) {

        if (etMaxFloor.getText().length() > 0) {
            try {
                maxFloor = Integer.parseInt(getNumberFromString(etMaxFloor.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                maxFloor = 0;
            }

        } else {

            maxFloor = 0;
        }

        switch (v.getId()) {

            case R.id.btMaxFloorMinus:

                if (maxFloor > 1) {
                    maxFloor -= 1;
                    etMaxFloor.setText(Actions.formatNumber(maxFloor, Actions.NoDecimalThousandsSeparator));
                } else {
                    maxFloor = 0;
                    etMaxFloor.setText(Actions.formatNumber(maxFloor, Actions.NoDecimalThousandsSeparator));
                }
                break;

            case R.id.btMaxFloorPlus:
                Log.wtf("btQuantityPlus", "maxFloor = " + maxFloor);
                // if(maxFloor < quantity-1) {
                maxFloor += 1;
                etMaxFloor.setText(Actions.formatNumber(maxFloor, Actions.NoDecimalThousandsSeparator));
                // }
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        localTimer.cancel();
        MyApplication.sessionOut = Calendar.getInstance();
    }


    @Override
    protected void onStop() {
        try{
            localTimer.cancel();
            //       Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }
        super.onStop();
        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
   /*     try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }*/
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void goTo(View v) {

        switch (v.getId()) {

            case R.id.btTimeSales:
                startActivity(new Intent(TradesActivity.this, TimeSalesActivity.class)
                        .putExtra("stockId", stockId)
                        .putExtra("securityId", stockQuotation.getSecurityId())
                        .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
                );
                break;

            case R.id.btOrderBook:
                startActivity(new Intent(TradesActivity.this, StockOrderBookActivity.class)
                        .putExtra("stockId", stockId)
                        .putExtra("securityId", stockQuotation.getSecurityId())
                        .putExtra("last",stockQuotation.getLast())
                        .putExtra("high",stockQuotation.getHiLimit())
                        .putExtra("low",stockQuotation.getLowlimit())
                        .putExtra("volume",stockQuotation.getPreviousClosing())
                        .putExtra("Trades",stockQuotation.getTrade())


                        .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
                );
                break;

            case R.id.btReview:
                addReviewOrder(MyApplication.TYPE_NORMAL_REVIEW);
                break;

            case R.id.btCancel:
                finish();
                break;
        }
    }

    private void normalReviewClick(){
        try {
            quantity = Integer.parseInt(getNumberFromString(etQuantity.getText().toString()));
        } catch (Exception e) {
            //quantity = 0;
            e.printStackTrace();
        }

        price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
        Log.wtf("trade_price_5",price+"");
        setTradeData(quantity, price, tradeType);
        try{maxFloor=Integer.parseInt(getNumberFromString(etMaxFloor.getText().toString()));}catch (Exception e){maxFloor=0;}
        if (tradeType == MyApplication.ORDER_BUY) { //أBuy

            //<editor-fold desc="buy validation">
                        /*if (trade.getPurchasePower() < trade.getOverallTotal()) {

                            //dialog
                            Actions.CreateDialog(TradesActivity.this, getResources().getString(R.string.error_purchase_power), false, false);
                        } else {

                            Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                            Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                            Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

                            startActivity(new Intent(TradesActivity.this, TradesActivity.class)
                                    .putExtra("isUpdate", isFromOrderDetails)
                                    .putExtra("trade", trade));
                        }*/
            //</editor-fold>

            Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
            Log.wtf("trade", " getDurationType = " + trade.getDurationType());
            Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

            if(orderTypeAdvanced==MyApplication.OCA && allOrders.size()<2 && !isFromOrderDetails) {
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_related_order), false, false);

            } else if(orderTypeAdvanced==MyApplication.ICEBERG && maxFloor >= quantity) {
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_max_floor), false, false);

            }
            else if(orderTypeAdvanced==MyApplication.ICEBERG && maxFloor!=0 && (quantity % maxFloor) !=0){
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_max_floor_divide), false, false);

            }
    /*        else if(isFromOrderDetails && orderTypeAdvanced==MyApplication.ICEBERG && maxFloor <= onlineOrder.getMaxfloor())
                Actions.CreateDialog(TradesActivity.this, getString(R.string.max_floor_old_value), false, false);*/
            else if(isFromOrderDetails && orderTypeAdvanced==MyApplication.ICEBERG && maxFloor <=onlineOrder.getQuantityExecuted())
                Actions.CreateDialog(TradesActivity.this, getString(R.string.max_floor_executed), false, false);

            else {


                Log.wtf("trading_before", trade.getStockQuotation().getTradingSession() + "");
                startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                        .putExtra("isUpdate", isFromOrderDetails)
                        .putExtra("trading_session", trade.getStockQuotation().getTradingSession())

                        .putExtra("trade",  orderTypeAdvanced==MyApplication.OCA ? ocaParentTrade : trade));
            }


        } else { //Sell

            //<editor-fold desc="sell validaiton">
                        /*if (trade.getAvailableShareCount() < quantity) {

                            Actions.CreateDialog(TradesActivity.this, getResources().getString(R.string.error_share_count), false, false);

                        } else {

                            Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                            Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                            Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

                            startActivity(new Intent(TradesActivity.this, TradesActivity.class)
                                    .putExtra("isUpdate", isFromOrderDetails)
                                    .putExtra("trade", trade));
                        }*/
            //</editor-fold>

            Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
            Log.wtf("trade", " getDurationType = " + trade.getDurationType());
            Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());
            if(orderTypeAdvanced==MyApplication.OCA && allOrders.size()<2 && !isFromOrderDetails) {
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_related_order), false, false);

            }
            else if(orderTypeAdvanced==MyApplication.ICEBERG && maxFloor >= quantity){
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_max_floor), false, false);

            }
            else if(orderTypeAdvanced==MyApplication.ICEBERG && maxFloor!=0 && (quantity % maxFloor) !=0){
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_max_floor_divide), false, false);

            }
/*           else if(isFromOrderDetails && orderTypeAdvanced==MyApplication.ICEBERG && maxFloor <= onlineOrder.getMaxfloor())
                Actions.CreateDialog(TradesActivity.this, getString(R.string.max_floor_old_value), false, false);*/
            else if(isFromOrderDetails && orderTypeAdvanced==MyApplication.ICEBERG && maxFloor <=onlineOrder.getQuantityExecuted())
                Actions.CreateDialog(TradesActivity.this, getString(R.string.max_floor_executed), false, false);

            else {


                startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                        .putExtra("isUpdate", isFromOrderDetails)
                        .putExtra("trading_session", trade.getStockQuotation().getTradingSession())
                        .putExtra("trade",  orderTypeAdvanced==MyApplication.OCA ? ocaParentTrade : trade));

            }

        }
    }


    private void addOrderToRelated(){

        try{
            if(allOrders.size()==0 && ocaParentTrade.getId()!=0)
                ocaParentTrade=trade;}catch (Exception e){

        }
        try {
            quantity = Integer.parseInt(getNumberFromString(etQuantity.getText().toString()));
        } catch (Exception e) {
            //quantity = 0;
            e.printStackTrace();
        }

        price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
        setTradeData(quantity, price, tradeType);

        try{
            if (tradeType == MyApplication.ORDER_BUY) { //أBuy
                Log.wtf("trading_before",trade.getStockQuotation().getTradingSession()+"");
            } else { //Sell
                Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());
            }}catch (Exception e){}
        isShowOrder=true;
        // ivShowHideOrders.setRotationX(180);
        rvOrders.setVisibility(View.VISIBLE);

        allOrders.add(new OnlineOrder(trade.getId(),0,0,0,0,trade.getDurationTypeId(),0,trade.getReference(),0,0,0,0,0,trade.getTradeTypeID(),0,trade.getStatusTypeId(),trade.getExecutedQuantity(),trade.getQuantity(),trade.getOrderType(),trade.getOperationTypeID(),trade.getStatusTypeId(),0,0,trade.getPrice(),trade.getTriggerPrice(),"","",trade.getDate(),trade.getGoodUntilDate(),"","","",trade.getStockQuotation().getStockID()+"","","","","","",trade.getStockQuotation().getSymbolEn(),trade.getStockQuotation().getInstrumentId(),true,true,true,true,true,null,"",MyApplication.lang == MyApplication.ARABIC ? trade.getOrdertypeValueAr() : trade.getOrdertypeValueEn(),trade.getMaxFloor(),orderTypeAdvanced));
        adapterOrders.notifyDataSetChanged();
        nsScroll.post(new Runnable() {
            @Override
            public void run() {
                nsScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void addReviewOrder(int type){

        String quantityTxt = etQuantity.getText().toString();
        String limitTxt = etLimitPrice.getText().toString();
        trade.setStockQuotation(stockQuotation);
        Animation shake = AnimationUtils.loadAnimation(TradesActivity.this, R.anim.shake);

        if (cbPrivate.isChecked() && type==MyApplication.TYPE_NORMAL_REVIEW) {

            try {
                quantity = Integer.parseInt(getNumberFromString(quantityTxt));
            } catch (Exception e) {
                quantity = 0;
                e.printStackTrace();
            }

            price = Double.parseDouble(getNumberFromString(limitTxt));
            setTradeData(quantity, price, tradeType);

            try{maxFloor=Integer.parseInt(getNumberFromString(etMaxFloor.getText().toString()));}catch (Exception e){maxFloor=0;}

            if(orderTypeAdvanced==MyApplication.OCA && allOrders.size()<2 && !isFromOrderDetails) {
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_related_order), false, false);

            }
            else if(orderTypeAdvanced==MyApplication.ICEBERG && maxFloor >= quantity){
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_max_floor), false, false);

            }
            else if(orderTypeAdvanced==MyApplication.ICEBERG && maxFloor!=0 && (quantity % maxFloor) !=0){
                Actions.CreateDialog(TradesActivity.this, getString(R.string.check_max_floor_divide), false, false);

            }

            /*else if(isFromOrderDetails && orderTypeAdvanced==MyApplication.ICEBERG && maxFloor <= onlineOrder.getMaxfloor())
                Actions.CreateDialog(TradesActivity.this, getString(R.string.max_floor_old_value), false, false);*/
            else if(isFromOrderDetails && orderTypeAdvanced==MyApplication.ICEBERG && maxFloor <=onlineOrder.getQuantityExecuted())
                Actions.CreateDialog(TradesActivity.this, getString(R.string.max_floor_executed), false, false);

            else {
                startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                        .putExtra("isUpdate", isFromOrderDetails)
                        .putExtra("trading_session",trade.getStockQuotation().getTradingSession())
                        .putExtra("trade",  orderTypeAdvanced==MyApplication.OCA ? ocaParentTrade : trade));
            }



        } else if ((limitTxt.length() == 0 || getNumberFromString(limitTxt).equals("0"))) {

            etLimitPrice.startAnimation(shake);
            Actions.CreateDialog(TradesActivity.this, getString(R.string.check_limit_price), false, false);

        } else if(checkPriceLimit()){
            Actions.CreateDialog(this,getString(R.string.check_price_msg),false,false);
        }


        else if ((quantityTxt.length() == 0 || getNumberFromString(quantityTxt).equals("0"))) {

            etQuantity.startAnimation(shake);
            Actions.CreateDialog(TradesActivity.this, getString(R.string.check_Quantity), false, false);

        }else if (orderTypeAdvanced==MyApplication.MO  && selectedRelatedOrderId==0) {

            Actions.CreateDialog(TradesActivity.this, getString(R.string.plz_add_ro), false, false);

        }


        else {
            if(type==MyApplication.TYPE_NORMAL_REVIEW)
                normalReviewClick();
            else if(type==MyApplication.TYPE_OCA_ADD){
                //insert order to related orders
                new ValidateOrder().execute();



            }
        }
    }

    private String setOrderDate() {

        String date = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss", Locale.ENGLISH);
        date = df.format(c.getTime());

        return date;
    }

    private void setTradeData(int quantity, double price, int tradeType) {

        BrokerageFee brokerageFee;

        try {
            brokerageFee = Actions.getBrokerageFeeByInstrumentID(MyApplication.allBrokerageFees, stockQuotation.getInstrumentId());
        } catch (Exception e) {
            e.printStackTrace();
            brokerageFee = new BrokerageFee();
            brokerageFee.setTotalBrokerageFee(0.00125);
            brokerageFee.setClearing(0.5);
        }

        double total =(BuildConfig.Enable_Markets? (price * quantity) /1000: (price * quantity));


        double commission;

        double brokerage = total * brokerageFee.getTotalBrokerageFee();

        if (brokerage < brokerageFee.getMinimumBrokerageFee())
            brokerage = brokerageFee.getMinimumBrokerageFee();

        if (total >= 50) {

            commission = brokerage + brokerageFee.getClearing();
        } else {

            commission = brokerage;
        }

        String orderDuration = "";
        String goodUntilDate = "";
        orderDuration = MyApplication.lang == MyApplication.ARABIC ? orderDurationType.getDescriptionAr() : orderDurationType.getDescriptionEn();

        if (orderDurationType.getID() == 6) {
            // orderDuration = orderDuration + " " + etDurationType.getText().toString();
            orderDuration = etDurationType.getText().toString();
            goodUntilDate = etDurationType.getText().toString();
        }

        trade.setDurationTypeId(orderDurationType.getID());
        trade.setDurationType(orderDuration);
        trade.setOverallTotal(Actions.roundNumber(total, Actions.ThreeDecimal));
        trade.setCommission(Actions.roundNumber(commission, Actions.ThreeDecimal));
        //trade.setStockQuotation(stockQuotation);

        if(!goodUntilDate.isEmpty())
            trade.setGoodUntilDate(goodUntilDate + " 00:00:00");
        else
            trade.setGoodUntilDate(goodUntilDate);
        trade.setTradeTypeID(tradeType);
        trade.setPrice(price);
        trade.setQuantity(quantity);
        trade.setTriggerPrice(triggerPrice);

        trade.setCost(tradeType == MyApplication.ORDER_SELL ? (total - commission) : (total + commission));

        if (cbPrivate.isChecked()) {

            trade.setStatusTypeId(16);
            trade.setOperationTypeID(enums.OrderOperationType.PRIVATE_ORDER.getValue());
        } else {

            trade.setStatusTypeId(1);

            if(orderTypeAdvanced==MyApplication.MO)
                trade.setOperationTypeID(enums.OrderOperationType.MANAGED_ORER.getValue());
            else if(orderTypeAdvanced==MyApplication.ICEBERG)
                trade.setOperationTypeID(enums.OrderOperationType.ICEBERG_ORDER.getValue());
            else
                trade.setOperationTypeID(enums.OrderOperationType.NEW_ORDER.getValue());
        }

        trade.setDate(setOrderDate());
        if (isFromOrderDetails) {
            trade.setReference(onlineOrder.getReference());
            trade.setExecutedQuantity(onlineOrder.getQuantityExecuted());
        } else {
            trade.setReference(0);
        }

        try{
            trade.setAdvancedOrderType(orderTypeAdvanced);
        }catch (Exception e){
            trade.setAdvancedOrderType(0);
        }

        try{
            trade.setArraySubOrders(allOrders);
        }catch (Exception e){
            trade.setArraySubOrders(new ArrayList<>());
        }


        try{
            trade.setMaxFloor(Integer.parseInt(getNumberFromString(etMaxFloor.getText().toString())));
        } catch (Exception e){
            trade.setMaxFloor(0);
        }

        try{
            if(!isFromOrderDetails) {
                trade.setRelatedOrderId(selectedRelatedOrderId);
                trade.setRelatedOrder(relatedOnlineOrder);
            }else {
                trade.setRelatedOrderId(MyApplication.onlineParentOrder.getID());
                trade.setRelatedOrder(MyApplication.onlineParentOrder);
            }
        }catch (Exception e){
            trade.setRelatedOrderId(0);
        }






    }

    private void setPortfolioData(Trade trade) {

        //<editor-fold desc="Trade object">
        tvCloseValue.setText(String.valueOf(stockQuotation.getPreviousClosing()));
        tvLastValue.setText(String.valueOf(stockQuotation.getLast()));
        tvBidValue.setText(String.valueOf(stockQuotation.getBid()));
        tvAskValue.setText(String.valueOf(stockQuotation.getVolumeAsk()));
        tvHighValue.setText(String.valueOf(stockQuotation.getHiLimit()));



        if(!BuildConfig.Enable_Markets){
            tvColum1Value.setText(String.valueOf(stockQuotation.getVolumeBidFormatted()));
            tvColum2Value.setText(String.valueOf(stockQuotation.getBidFormatted()));
            tvColum3Value.setText(String.valueOf(stockQuotation.getLastFormatted()));
            tvColum4Value.setText(String.valueOf(stockQuotation.getAskFormatted()));
            tvColum5Value.setText(String.valueOf(stockQuotation.getVolumeAskFormatted()));
            tvColum6Value.setText(String.valueOf(stockQuotation.getChangeFormatted()));

            tvHighValueNew.setText(String.valueOf(stockQuotation.getHiLimitFormatted()));
            tvLowValueNew.setText(String.valueOf(stockQuotation.getLowLimitFormatted()));
            tvVolumeValueNew.setText(String.valueOf(stockQuotation.getVolumeFormatted()));

            tvHighValueNew.setTextColor(ContextCompat.getColor(this, R.color.green_color));
            tvLowValueNew.setTextColor(ContextCompat.getColor(this, R.color.red_color));
            if(Double.parseDouble(stockQuotation.getChange())>0){
                tvColum6Value.setTextColor(ContextCompat.getColor(this, R.color.green_color));
                tvColum3Value.setTextColor(ContextCompat.getColor(this, R.color.green_color));
            }else if(Double.parseDouble(stockQuotation.getChange())<0){
                tvColum6Value.setTextColor(ContextCompat.getColor(this, R.color.red_color));
                tvColum3Value.setTextColor(ContextCompat.getColor(this, R.color.red_color));

            }else {
                tvColum6Value.setTextColor(ContextCompat.getColor(this, R.color.orange));
                tvColum3Value.setTextColor(ContextCompat.getColor(this, R.color.orange));

            }

        }






        /*try {

            tvHighValue.setText(Actions.formatNumber(stockQuotation.getHiLimit(), Actions.TwoDecimal)); // kenet One Decimal
        } catch (Exception e) {
            e.printStackTrace();
            tvHighValue.setText(String.valueOf(stockQuotation.getHiLimit()));
        }
        try {
            tvLowValue.setText(Actions.formatNumber(stockQuotation.getLowlimit(), Actions.TwoDecimal)); // kenet One Decimal
        } catch (Exception e) {
            e.printStackTrace();
            tvLowValue.setText(String.valueOf(stockQuotation.getLowlimit()));
        }*/

        tvLowValue.setText(String.valueOf(stockQuotation.getLowlimit()));

        String stockName = "--";
        if (MyApplication.lang == MyApplication.ARABIC) {

            tvUserName.setText(MyApplication.currentUser.getNameAr());
            //   tvStockTitle.setText(stockQuotation.getNameAr());
            stockName = /*stockQuotation.getSecurityId() + "-" + */stockQuotation.getSymbolAr()+"-"+stockQuotation.getNameAr(); //getStockID()
            tvSessionValue.setText(stockQuotation.getSessionNameAr());
//            tvInstrumentValue.setText(stockQuotation.getInstrumentNameAr());
        } else {

            tvUserName.setText(MyApplication.currentUser.getNameEn());
            //   tvStockTitle.setText(stockQuotation.getNameEn());
            stockName = /*stockQuotation.getSecurityId() + "-" + */stockQuotation.getSymbolEn()+"-"+stockQuotation.getNameEn(); //getStockID()
            tvSessionValue.setText(stockQuotation.getSessionNameEn());
//            tvInstrumentValue.setText(stockQuotation.getInstrumentNameEn());
        }

        tvInstrumentValue.setText(Actions.formatNumber(stockQuotation.getNormalMarketSize(), Actions.NoDecimalThousandsSeparator));
        tvStockTitle.setText(stockName);
        //</editor-fold>

        tvPurchasePowerValue.setText(Actions.formatNumber(trade.getPurchasePower(), Actions.NoDecimalThousandsSeparator));
        tvQuantityValue.setText(Actions.formatNumber(trade.getAvailableShareCount(), Actions.NoDecimalThousandsSeparator));
    }

    private void setTypefaces() {

        Actions.setTypeface(new TextView[]{tvUserName, tvPortfolioNumber, tvStockTitle, tvInstrumentValue, tvSessionValue},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        Actions.setTypeface(new TextView[]{tvCloseValue, tvLastValue, tvBidValue, tvAskValue, tvHighValue,
                tvLowValue, tvPurchasePowerValue, tvQuantityValue, tvCostValue, tvCommissionValue, tvOverallValue,tvColum1Value,tvColum2Value,tvColum3Value,tvColum4Value,tvColum5Value,tvColum6Value,tvHighValueNew,tvLowValueNew,tvVolumeValueNew}, MyApplication.giloryBold);

        etLimitPrice.setTypeface(MyApplication.giloryBold);
        etQuantity.setTypeface(MyApplication.giloryBold);
        etMaxFloor.setTypeface(MyApplication.giloryBold);
        etDurationType.setTypeface(MyApplication.giloryBold);
        close.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public String getNumberFromString(String qtty) {
        String nQtty = "";
        //Log.wtf("qtty" , "length = " + qtty.length());
        for (int i = 0; i < qtty.length(); i++) {
            //Log.wtf("nQtty" , nQtty + " - i = " + i);
            /*if(qtty.charAt(i) == '.'){
                break;
            }*/
            if (qtty.charAt(i) == '.' || qtty.charAt(i) == '0' || qtty.charAt(i) == '1' || qtty.charAt(i) == '2' || qtty.charAt(i) == '3' || qtty.charAt(i) == '4' || qtty.charAt(i) == '5'
                    || qtty.charAt(i) == '6' || qtty.charAt(i) == '7' || qtty.charAt(i) == '8' || qtty.charAt(i) == '9') {
                nQtty += qtty.charAt(i);
            }
        }
        if (nQtty.equals("")) {
            nQtty = "0";
        }
        return nQtty;
    }

    public Double setPriceWithMarketPrice(Boolean onLoad) {
        Double pricee = 0.0;

        Boolean condition = (MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.PreClose.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.TradeAtLast.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.Acceptance.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.PreOpen.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.CLOSE_ACCPT2.getValue());

        Log.wtf("condition", ": " + condition);
        Log.wtf("MyApplication.marketID", ": " + MyApplication.marketID);
        Log.wtf("enums.MarketType.KWOTC.getValue()", ": " + enums.MarketType.KWOTC.getValue());
        Log.wtf("orderType", ": " + orderType);
        Log.wtf("MyApplication.MARKET_PRICE", ": " + MyApplication.MARKET_PRICE);


        if (MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue()))) {

            pricee = price;
        } else {
            if (condition && onLoad) {
                pricee = (stockQuotation.getLast() > 0 ? stockQuotation.getLast() : stockQuotation.getReferencePrice());
            } else {
                if (onLoad) {

                    if (MyApplication.defaultPriceType == 0) {
                        pricee = 0.0;
                    } else if (MyApplication.defaultPriceType == 1) {

                        if (tradeType == MyApplication.ORDER_SELL) {
                            pricee = (stockQuotation.getAsk() > 0 ? stockQuotation.getAsk() : stockQuotation.getHiLimit());
                        } else {
                            pricee = (stockQuotation.getBid() > 0 ? stockQuotation.getBid() : stockQuotation.getLowlimit());
                        }
                    } else if (MyApplication.defaultPriceType == 2) {

                        pricee = (tradeType == MyApplication.ORDER_SELL) ? stockQuotation.getHiLimit() : stockQuotation.getLowlimit();
                    }

                    else if (MyApplication.defaultPriceType == 3) {

                        pricee = stockQuotation.getLast();
                    }
                } else {
                    pricee = price;
                }
            }
        }
        price = pricee;
        return pricee;

    }

    public Double getDefaultPrice() {
        Double pricee = 0.0;

        Log.wtf("MyApplication.defaultPriceType", ": " + MyApplication.defaultPriceType);

        if (MyApplication.defaultPriceType == 0) {
            pricee = 0.0;
        } else if (MyApplication.defaultPriceType == 1) {

            if (tradeType == MyApplication.ORDER_SELL) {
                pricee = (stockQuotation.getAsk() > 0 ? stockQuotation.getAsk() : stockQuotation.getHiLimit());
            } else {
                pricee = (stockQuotation.getBid() > 0 ? stockQuotation.getBid() : stockQuotation.getLowlimit());
            }
        } else if (MyApplication.defaultPriceType == 2) {

            pricee = (tradeType == MyApplication.ORDER_SELL) ? stockQuotation.getHiLimit() : stockQuotation.getLowlimit();
        }
        else if (MyApplication.defaultPriceType == 3) {

            pricee = stockQuotation.getLast();
        }
        return pricee;
    }

    @Override
    public void refreshData() {

    }

    private class GetOrderDurationTypes extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradesActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetOrderDurationTypes.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("key", MyApplication.currentUser.getKey());
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, TradesActivity.this, parameters);

                MyApplication.allOrderDurationType.addAll(GlobalFunctions.GetOrderDurationList(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetOrderDurationTypes.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

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

            if (getIntent().hasExtra("isFromOrderDetails")) {
                Log.wtf("onPostExecute - isFromOrderDetails", "isFromOrderDetails ");
                int index = Actions.returnDurationIndex(onlineOrder.getDurationID());
                Log.wtf("onPostExecute - isFromOrderDetails", "index : " + index);

                selectedPos = index;
                try {
                    setOrderDuration(index);
                }catch (Exception e){}

                //   previous = MyApplication.allOrderDurationType.get(index);
//                trade.setDurationType(MyApplication.lang == MyApplication.ARABIC ? adapter.getItem(index).getDescriptionAr() : adapter.getItem(index).getDescriptionEn());
                trade.setDurationType(MyApplication.lang == MyApplication.ARABIC ? MyApplication.allOrderDurationType.get(index).getDescriptionAr() : MyApplication.allOrderDurationType.get(index).getDescriptionEn());
            } else {

                Log.wtf("onPostExecute - isFromOrderDetails", " Not have extra isFromOrderDetails ");
                Log.wtf("Actions - isMarketOpen : ", " Actions.isMarketOpen() : " + Actions.isMarketOpen());
                if (!Actions.isMarketOpen()) {
                    try{
                        selectedPos = Actions.returnDurationIndex(0);
                        setOrderDuration(Actions.returnDurationIndex(0));}catch (Exception e){}
                } else {
                    try {  selectedPos = Actions.returnDurationIndex(0);

                        setOrderDuration(Actions.returnDurationIndex(0));
                    }catch (Exception e){}

                }
            }
        }
    }

    private class GetTradeInfo extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {




            String result = "";
            String url = MyApplication.link + MyApplication.GetTradeInfo.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key",MyApplication.mshared.getString(getString(R.string.afterkey), ""))/* getResources().getString(R.string.beforekey))*/;
            parameters.put("stockId", stockId + "");
            parameters.put("MarketID", MyApplication.marketID);

            Log.wtf("trade_activity","get_trade_info call");
            for (Map.Entry<String, String> map : parameters.entrySet()) {
                Log.wtf("trade_activity"," get_trade_info  parameters : " + map.getKey() + "= " + map.getValue());
            }
            Log.wtf("trade_activity","get_trade_info url");

//            while (running) {

//                if (isCancelled())
//                    break;


            try {

                result = ConnectionRequests.GET(url, TradesActivity.this, parameters);

//                    publishProgress(result);
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetTradeInfo.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }


            return result;
        }


        @Override
        protected void onPostExecute(String aVoid) {

            super.onPostExecute(aVoid);



            try {
                trade = GlobalFunctions.GetTradeInfo(aVoid);
                getStockById = new GetStockQuotation();
                getStockById.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
            swipeContainer.setRefreshing(false);
        }
    }







    private class GetOrderTypes extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            //  try{MyApplication.progress.show();}catch (Exception e){}
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetOrderTypeByStock.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("enabled", "true");
            parameters.put("userId", MyApplication.currentUser.getId()+"");
            parameters.put("stockId", stockId+"");

            try {

                result = ConnectionRequests.GET(url, TradesActivity.this, parameters);

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetTradeInfo.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return result;
        }


        @Override
        protected void onPostExecute(String aVoid) {

            super.onPostExecute(aVoid);

            Log.wtf("GetOrderTypes", "GetOrderTypes");

            try {
                arrayAllOrdertypes.clear();
                arrayAllOrdertypesAdvanced.clear();
                int lastSelectedOrderAdvanced=orderTypeAdvanced;
                ArrayList<Ordertypes> allTypeOrders=new ArrayList<>();
                allTypeOrders= GlobalFunctions.GetOrderTypes(aVoid);
                for (int i=0;i< allTypeOrders.size();i++){
                    if(allTypeOrders.get(i).getIsAdvanced()==1)
                        arrayAllOrdertypesAdvanced.add(allTypeOrders.get(i));
                    else {
                        if(!MyApplication.mshared.getBoolean("EnableAdvancedTypeSection",false)) {
                            if(allTypeOrders.get(i).getOrderTypeID()==MyApplication.LIMIT || allTypeOrders.get(i).getOrderTypeID()==MyApplication.MARKET_PRICE)
                                arrayAllOrdertypes.add(allTypeOrders.get(i));
                        }else
                            arrayAllOrdertypes.add(allTypeOrders.get(i));
                    }
                }

                //   arrayAllOrdertypesAdvanced.add(new Ordertypes(13,1,1,1,"Iceberg","Iceberg"));

     /*           arrayAllOrdertypesAdvanced.add(new Ordertypes(11,1,1,1,"MO","MO"));
                arrayAllOrdertypesAdvanced.add(new Ordertypes(12,1,1,1,"OCA","OCA"));
*/
                //  arrayAllOrdertypes = GlobalFunctions.GetOrderTypes(aVoid);
                //    try{ MyApplication.progress.hide();}catch (Exception e){}
                if(!isAutoUpdate)
                    setOrderTypeSpinner();

                if(!BuildConfig.Enable_Markets && !isAutoUpdate){
                    maxFloor=onlineOrder.getMaxfloor();
                    etMaxFloor.setText(maxFloor+"");
                }


                if((isFromOrderDetails && lastSelectedOrderAdvanced==0) ||  !MyApplication.mshared.getBoolean("EnableAdvancedTypeSection",false)){
                    llAdvanced.setVisibility(View.GONE);
                    btAdd.setVisibility(View.GONE);
                    // ivShowHideOrders.setVisibility(View.GONE);
                    linearParentOrder.setVisibility(View.GONE);
                    linearSelectOrder.setVisibility(View.GONE);
                }else {
                    if(!isAutoUpdate) {
                        if (arrayAllOrdertypesAdvanced.size() > 0) {
                            arrayAllOrdertypesAdvanced.add(0, new Ordertypes(0, 0, 0, 1, getString(R.string.select_order), getString(R.string.select_order)));
                            llAdvanced.setVisibility(View.VISIBLE);
                            setOrderTypeAdvancedSpinner();
                            try {
                                spOrderTypeAdvanced.setSelection(getIndexFromIdAdvanced(lastSelectedOrderAdvanced));
                            } catch (Exception e) {
                                Log.wtf("exception_advanced", e.toString());
                            }


                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFromOrderDetails && orderTypeAdvanced == MyApplication.MO) {
                                        selectedRelatedOrderId = onlineOrder.getRelatedOnlineOrderID();
                                        try {
                                            tvSelectedSymbol.setText("# " + selectedRelatedOrderId);
                                        } catch (Exception e) {
                                        }
                                        try {
                                            tvSelectedExecutedQuantity.setVisibility(View.GONE);
                                        } catch (Exception e) {
                                        }
                                        try {
                                            tvSelectedInstruments.setVisibility(View.GONE);
                                        } catch (Exception e) {
                                        }
                                        try {
                                            tvSelectedPrice.setVisibility(View.GONE);
                                        } catch (Exception e) {
                                        }
                                        try {
                                            tvSelectedQuantity.setVisibility(View.GONE);
                                        } catch (Exception e) {
                                        }
                                        linearSelectOrder.setVisibility(View.GONE);
                                        linearSelectedOrder.setVisibility(View.VISIBLE);


                                    } else if (isFromOrderDetails && orderTypeAdvanced == MyApplication.ICEBERG) {
                                        spOrderTypeAdvanced.setEnabled(false);
                                        maxFloor = onlineOrder.getMaxfloor();
                                        etMaxFloor.setText(maxFloor + "");
                                    }
                                }
                            }, 300);


                        } else {
                            llAdvanced.setVisibility(View.GONE);
                        }

                    }
                }


             /*   if(!BuildConfig.Enable_Markets){
                    maxFloor=onlineOrder.getMaxfloor();
                    etMaxFloor.setText(maxFloor+"");
                }*/

                spOrderType.setVisibility(View.VISIBLE);
                tvPriceLable.setVisibility(View.VISIBLE);
                tvOtcPrice.setVisibility(View.GONE);
                if(getIndexFromId(orderType)!=-1) {
                    trade.setOrdertypeValueEn(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionEn());
                    trade.setOrdertypeValueAr(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionAr());
                }
                if (orderType == MyApplication.LIMIT) {
                    trade.setOrderType(MyApplication.LIMIT);
                } else {
                    trade.setOrderType(MyApplication.MARKET_PRICE);
                }

                setInitialData();
                if(tradeType == MyApplication.ORDER_BUY){
                    buyClick();
                }else {
                    sellClick();
                }


                if(stockId==0) {
                    Log.wtf("trade_price_6","0");
                    etLimitPrice.setText("0");
                }

            } catch (Exception e) {
                Log.wtf("exception",e.toString());
                e.printStackTrace();
            }

        }
    }


    private void showDeleteConfirmationDialog(Activity context, int type, String message, String title, int Id) {

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> {



                        })

                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> {
                            // do nothing
                            dialog.cancel();
                        })
                .show();
    }


    private void setOrderTypeSpinner(){

        arraySpinnerOrdertypes.clear();
        for (int i=0;i<arrayAllOrdertypes.size();i++){
            // if(arrayAllOrdertypes.get(i).getEnabled()==1)

            if(arrayAllOrdertypes.get(i).getOrderTypeID()!=MyApplication.MARKET_PRICE)
                arraySpinnerOrdertypes.add(arrayAllOrdertypes.get(i));
            else {
                //  if (!Actions.isMarketClosed())
                arraySpinnerOrdertypes.add(arrayAllOrdertypes.get(i));
            }
        }



        orderTypeSpinnerAdapter = new OrderTypeSpinnerAdapter(this, arraySpinnerOrdertypes, true);
        spOrderType.setAdapter(orderTypeSpinnerAdapter);
        spOrderType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                orderType = arraySpinnerOrdertypes.get(position).getOrderTypeID();
                checkMaxFloor();
                if(getIndexFromId(orderType)!=-1) {
                    trade.setOrdertypeValueEn(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionEn());
                    trade.setOrdertypeValueAr(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionAr());
                }
                if(orderType == MyApplication.LIMIT || orderType==MyApplication.LIMIT_IF_TOUCHED){
                    // orderType = MyApplication.LIMIT;
                    // setLimitChecked(true);
                    showLimitPrice(true);


                    if(stockId!=0) {
                        Log.wtf("trade_price_7",setEtPriceValue()+"");
                        if(!isAutoUpdate)
                            etLimitPrice.setText(setEtPriceValue());
                    }

                    tvPriceLable.setText(getString(R.string.price));

                    trade.setOrderType(orderType);

                    try {
                        setMaxMin = stockQuotation.getInstrumentId().equals(MyApplication.Auction_Instrument_id) || MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue())) || cbPrivate.isChecked();
                    }catch (Exception e){}

                    // setMaxMin=true;
                    updateOverAllViews(price, quantity);
                    if(!isAutoUpdate){
                        try{  btLimitPlus.performClick();}catch (Exception e){}
                        try{  btLimitMinus.performClick();}catch (Exception e){}
                    }
                }else if(orderType == MyApplication.MARKET_PRICE || orderType==MyApplication.MARKET_IF_TOUCHED){
                    // orderType = MyApplication.MARKET_PRICE;
                    // setLimitChecked(false);
                    showLimitPrice(false);
                    // setMaxMin=false;

                    tvPriceLable.setText(getString(R.string.price));


                    if(stockId!=0 && !isAutoUpdate){

                        //after_submission
                        //   if(orderType==MyApplication.MARKET_IF_TOUCHED) {
                        etLimitPrice.setText("");
                        price=0;
       /*             }
                    else
                        etLimitPrice.setText(setEtPriceValue());*/
                    }

                    trade.setOrderType(orderType);
                    updateOverAllViews(price, quantity);

                    if(tradeType == MyApplication.ORDER_BUY){
                        /*btSell.performClick();
                        btBuy.performClick();*/
                        sellClick();
                        buyClick();
                    }else {
                        buyClick();
                        sellClick();
                    }




                }


                else {

                    // tvPriceLable.setText(getString(R.string.trigger_price));
                    // orderType = MyApplication.MARKET_PRICE;
                    //setLimitChecked(true);
                    showLimitPrice(true);
                    setMaxMin=true;

                    //   etLimitPrice.setText("");
                    trade.setOrderType(orderType);
                    updateOverAllViews(price, quantity);
                    if(!isAutoUpdate){
                        try{  btLimitPlus.performClick();}catch (Exception e){}
                        try{  btLimitMinus.performClick();}catch (Exception e){}
                    }
                }

                if((orderType == MyApplication.LIMIT || orderType==MyApplication.MARKET_PRICE) && orderTypeAdvanced==0){
                    cbPrivate.setVisibility(View.VISIBLE);
                }else {
                    cbPrivate.setVisibility(View.GONE);
                }

                if (arrayAllOrdertypesAdvanced.size() > 0) {

                    if (orderType == MyApplication.MARKET_IF_TOUCHED || orderType == MyApplication.LIMIT_IF_TOUCHED || orderType == MyApplication.SI)
                        llAdvanced.setVisibility(View.GONE);
                    else
                        llAdvanced.setVisibility(View.VISIBLE);
                }
                setPriceViews();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(isFromOrderDetails) {
            orderType = onlineOrder.getOrderTypeID();
            try { spOrderType.setSelection(getIndexFromId(orderType)); } catch (Exception e) { }
            if(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getIsEditable()==1)
                spOrderType.setEnabled(true);
            else
                spOrderType.setEnabled(false);
        }else {
            spOrderType.setEnabled(true);
            try { spOrderType.setSelection(1); } catch (Exception e) { }
        }
    }




    private void setOrderTypeAdvancedSpinner(){

        arraySpinnerOrdertypesAdvanced.clear();
        for (int i=0;i<arrayAllOrdertypesAdvanced.size();i++){
            // if(arrayAllOrdertypesAdvanced.get(i).getEnabled()==1)

            if(arrayAllOrdertypesAdvanced.get(i).getOrderTypeID()!=MyApplication.MARKET_PRICE)
                arraySpinnerOrdertypesAdvanced.add(arrayAllOrdertypesAdvanced.get(i));
            else {
                //  if (!Actions.isMarketClosed())
                arraySpinnerOrdertypesAdvanced.add(arrayAllOrdertypesAdvanced.get(i));
            }
        }



        orderTypeAdvancedSpinnerAdapter = new OrderTypeSpinnerAdapter(this, arraySpinnerOrdertypesAdvanced, true);
        spOrderTypeAdvanced.setAdapter(orderTypeAdvancedSpinnerAdapter);
        spOrderTypeAdvanced.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRelatedOrderId=0;
                relatedOnlineOrder=new OnlineOrder();
                //  allOrders.clear();
                etMaxFloor.setText("");
                orderTypeAdvanced=arraySpinnerOrdertypesAdvanced.get(position).getOrderTypeID();
             /*  if(orderTypeAdvanced==0) {
                   linearSelectAdvanced.setVisibility(View.INVISIBLE);
                   linearHeaderOrders.setVisibility(View.GONE);
               }
               else*/
                if(orderTypeAdvanced==MyApplication.MO){
                    linearSelectAdvanced.setVisibility(View.VISIBLE);
                    linearHeaderOrders.setVisibility(View.GONE);
                    linearSelectedOrder.setVisibility(View.GONE);
                    linearSelectOrder.setVisibility(View.VISIBLE);
                    linearMaxFloor.setVisibility(View.GONE);
                    //ivArrowOrder.setVisibility(View.VISIBLE);
                    rvOrders.setVisibility(View.GONE);
                    allOrders.clear();
                  /*  arrayFilteredOrderDialog.clear();
                    arrayFilteredOrderDialog.add(new OnlineOrder(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1.2,1.3,1.4,1.5,"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",true,true,true,true,true,null,"1","1"));
                    arrayFilteredOrderDialog.add(new OnlineOrder(2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1.6,1.7,1.8,1.9,"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",true,true,true,true,true,null,"1","1"));
                    arrayFilteredOrderDialog.add(new OnlineOrder(3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2.2,2.32,1.23,1.24,"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",true,true,true,true,true,null,"1","1"));
                    arrayFilteredOrderDialog.add(new OnlineOrder(4,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1.25,1.26,1.27,1.28,"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1",true,true,true,true,true,null,"1","1"));
                    */// setAllOrdersDialog();
                    linearSelectAdvanced.setOnClickListener(v->{
                        if(!isFromOrderDetails)
                            showOrderDialog();
                    });
                    btAdd.setVisibility(View.GONE);
                    //  ivShowHideOrders.setVisibility(View.GONE);
                    linearParentOrder.setVisibility(View.GONE);


                } else if(orderTypeAdvanced==MyApplication.OCA){
                    linearHeaderOrders.setVisibility(View.VISIBLE);
                    linearSelectAdvanced.setVisibility(View.VISIBLE);
                    linearSelectedOrder.setVisibility(View.GONE);
                    linearSelectOrder.setVisibility(View.GONE);
                    if(!isFromOrderDetails)
                        btAdd.setVisibility(View.VISIBLE);

                    // ivShowHideOrders.setVisibility(View.VISIBLE);
                    //  ivShowHideOrders.setVisibility(View.GONE);

                    rvOrders.setVisibility(View.VISIBLE);
                    linearMaxFloor.setVisibility(View.GONE);
                    //  allOrders.clear();
                    setOrders();
                    isShowOrder=false;
     /*               ivShowHideOrders.setOnClickListener(v->{
                        if(isShowOrder) {
                            rvOrders.setVisibility(View.GONE);
                            ivShowHideOrders.setRotationX(0);
                        }
                        else {
                            ivShowHideOrders.setRotationX(180);
                            rvOrders.setVisibility(View.VISIBLE);
                        }
                    isShowOrder=!isShowOrder;
                    });*/
                    linearParentOrder.setVisibility(View.GONE);
                    btAdd.setOnClickListener(v->{
                        //Toast.makeText(getApplicationContext(),"aaaaaa",Toast.LENGTH_LONG).show();
                        addReviewOrder(MyApplication.TYPE_OCA_ADD);
                    });
                } else if(orderTypeAdvanced==MyApplication.ICEBERG){

                    linearSelectAdvanced.setVisibility(View.VISIBLE);
                    //ivArrowOrder.setVisibility(View.GONE);
                    linearHeaderOrders.setVisibility(View.GONE);
                    linearSelectedOrder.setVisibility(View.GONE);
                    linearSelectOrder.setVisibility(View.GONE);
                    linearMaxFloor.setVisibility(View.VISIBLE);
                    allOrders.clear();
                    rvOrders.setVisibility(View.GONE);

                    btAdd.setVisibility(View.GONE);
                    //  ivShowHideOrders.setVisibility(View.GONE);
                    linearParentOrder.setVisibility(View.GONE);

                }



                else {
                    linearSelectAdvanced.setVisibility(View.GONE);
                    linearHeaderOrders.setVisibility(View.GONE);
                    rvOrders.setVisibility(View.GONE);
                    btAdd.setVisibility(View.GONE);
                    linearParentOrder.setVisibility(View.GONE);
                    // ivShowHideOrders.setVisibility(View.GONE);
                }

                if(isFromOrderDetails)
                    btAdd.setVisibility(View.GONE);

                if((orderType == MyApplication.LIMIT || orderType==MyApplication.MARKET_PRICE) && orderTypeAdvanced==0){
                    cbPrivate.setVisibility(View.VISIBLE);
                }else {
                    cbPrivate.setVisibility(View.GONE);
                }

                checkMaxFloor();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(isFromOrderDetails) {
            orderTypeAdvanced = onlineOrder.getAdvancedOrderTypeID();
            try { spOrderTypeAdvanced.setSelection(getIndexFromIdAdvanced(orderTypeAdvanced)); } catch (Exception e) { }
            if(arraySpinnerOrdertypesAdvanced.get(getIndexFromIdAdvanced(orderTypeAdvanced)).getIsEditable()==1)
                spOrderTypeAdvanced.setEnabled(true);
            else
                spOrderTypeAdvanced.setEnabled(false);
        }else {
            spOrderTypeAdvanced.setEnabled(true);
            try { spOrderTypeAdvanced.setSelection(0);} catch (Exception e) { }
        }
    }


    private void setPriceViews(){
        if(orderType == MyApplication.MARKET_IF_TOUCHED || orderType==MyApplication.LIMIT_IF_TOUCHED || orderType==MyApplication.SI){
            linearTriggerPrice.setVisibility(View.VISIBLE);
            tvTriggerPricelabel.setVisibility(View.VISIBLE);
            etLimitTriggerPrice.setText("");
        }else {
            linearTriggerPrice.setVisibility(View.GONE);
            tvTriggerPricelabel.setVisibility(View.GONE);
            etLimitTriggerPrice.setText("");
        }
    }

    private void checkMaxFloor(){
        if(orderType == MyApplication.MARKET_PRICE) {
            linearMaxFloor.setVisibility(View.GONE);
            setMaxFloorEdit(false);
        }
        else {
            linearMaxFloor.setVisibility(View.VISIBLE);
            setMaxFloorEdit(true);
        }
    }

    private int getIndexFromId(int id){
        int index=-1;
        for (int i=0;i<arraySpinnerOrdertypes.size();i++){
            if(arraySpinnerOrdertypes.get(i).getOrderTypeID()==id) {
                index = i;
                break;
            }
        }
        return index;
    }


    private void setParentOrder(){
        try{ tvSymbolItemsParent.setText(trade.getStockQuotation().getSymbolEn());}catch (Exception e){}
        try{tvPriceItemParent.setText(trade.getPrice()+""); }catch (Exception e){}
        try{tvQuantityItemParent.setText(Actions.formatNumber(trade.getQuantity(), Actions.NoDecimalThousandsSeparator));}catch (Exception e){}
        try{tvExecutedQuantityItemParent.setText(Actions.formatNumber(trade.getExecutedQuantity(), Actions.NoDecimalThousandsSeparator));}catch (Exception e){}

    }

    private int getIndexFromIdAdvanced(int id){
        int index=-1;
        for (int i=0;i<arraySpinnerOrdertypesAdvanced.size();i++){
            if(arraySpinnerOrdertypesAdvanced.get(i).getOrderTypeID()==id) {
                index = i;
                break;
            }
        }
        return index;
    }


    private class GetStockQuotation extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";


            HashMap<String, String> parameters = new HashMap<String, String>();


            MyApplication.setWebserviceItem();
            String url = MyApplication.link + MyApplication.GetStockQuotation.getValue();
            String marketid = "";
            marketid = MyApplication.marketID;
            Log.wtf("trade_activity","get_stock_quotation call"+url);


            try {

                if(marketid.matches("0")) {

                    if(BuildConfig.Enable_Markets) {
                        marketid = "2";
                        MyApplication.marketID = "2";
                        Actions.setLastMarketId(getApplicationContext(), 2);
                    }else {
                        marketid = "1";
                        MyApplication.marketID = "1";
                        Actions.setLastMarketId(getApplicationContext(), 1);

                    }
                }

                parameters.clear();
                parameters.put("InstrumentId", "");
                parameters.put("stockIds", stockId+"");
                parameters.put("TStamp",/*timeStamp*/"0" );
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", marketid);
                parameters.put("sectorId", "0");


                for (Map.Entry<String, String> map : parameters.entrySet()) {
                    Log.wtf("trade_activity"," get_stock_quotation  parameters : " + map.getKey() + "= " + map.getValue());
                }

                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                stockQuotation=GlobalFunctions.GetStockQuotation(result,false).get(0);

            } catch (Exception e) {
                Log.wtf("trade_activity","get_stock_quotation exception"+e.toString());
                e.printStackTrace();
                if (MyApplication.showBackgroundRequestToastError) {
                    try {
                        if (MyApplication.isDebug) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetStockQuotation.getKey(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception es) {
                        Log.wtf("Appservice ", "Error - " + MyApplication.GetStockQuotation.getKey() + " : " + es.getMessage());
                    }
                }
            }
            // publishProgress();



            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.wtf("trade_activity","get_stock_quotation result"+result);
            loading.setVisibility(View.GONE);
            cbPrivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {

                    trade.setStatusTypeId(16);
                    trade.setOperationTypeID(enums.OrderOperationType.PRIVATE_ORDER.getValue());
                } else {

                    trade.setStatusTypeId(1);
                    if(orderTypeAdvanced==MyApplication.MO)
                        trade.setOperationTypeID(enums.OrderOperationType.MANAGED_ORER.getValue());
                    else if(orderTypeAdvanced==MyApplication.ICEBERG)
                        trade.setOperationTypeID(enums.OrderOperationType.ICEBERG_ORDER.getValue());
                    else
                        trade.setOperationTypeID(enums.OrderOperationType.NEW_ORDER.getValue());
                }

                try{setMaxMin = stockQuotation.getInstrumentId().equals(MyApplication.Auction_Instrument_id) || MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue())) || cbPrivate.isChecked();}catch (Exception e){}

            });

            try {

                try {
                    setMaxMin = stockQuotation.getInstrumentId().equals(MyApplication.Auction_Instrument_id) || MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue())) || cbPrivate.isChecked();
                }catch (Exception e){}
                try {
                    /*if (isFromOrderDetails) {

                        if (tradeType == MyApplication.ORDER_SELL){

                            trade.setAvailableShareCount(trade.getAvailableShareCount() + (onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted()));
                        }else{

                            trade.setAvailableShareCount(trade.getAvailableShareCount());
                        }
                    }*/
                    tvPurchasePowerValue.setText(Actions.formatNumber(trade.getPurchasePower(), Actions.ThreeDecimalThousandsSeparator));
                    tvQuantityValue.setText(Actions.formatNumber(trade.getAvailableShareCount(), Actions.NoDecimalSeparator));
                    // trade.setAvailableShareCount(trade.getAvailableShareCount());
                } catch (Exception e) {
                    try {
                        Toast.makeText(TradesActivity.this, "error in setAvailableShareCount", Toast.LENGTH_SHORT).show();
                    } catch (Exception es) {
                        Log.wtf("setAvailableShareCount  ", "error : " + es.getMessage());
                    }
                }


                if (stockQuotation.getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {
                    stockQuotation.setHiLimit(0);
                    stockQuotation.setLowlimit(0);
                } else {
                    stockQuotation.setHiLimit(stockQuotation.getHiLimit());
                    stockQuotation.setLowlimit(stockQuotation.getLowlimit());
                }

                if (stockQuotation.getSessionId().equals(MyApplication.CB_Auction_id)) {
                    tvSessionValue.setBackgroundColor(getResources().getColor(R.color.orange));
                }

                trade.setStockQuotation(stockQuotation);
                trade.setOrderType(orderType);
                if(getIndexFromId(orderType)!=-1) {
                    trade.setOrdertypeValueEn(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionEn());
                    trade.setOrdertypeValueAr(arraySpinnerOrdertypes.get(getIndexFromId(orderType)).getDescriptionAr());
                }
                //</editor-fold>

                setPortfolioData(trade);
                if (firstOpen && !isFromOrderDetails &&!isAutoUpdate) {
                    double price = 0.0;

//                    etLimitPrice.setText("" + price);

                    price = setPriceWithMarketPrice(true);
                    //getDefaultPrice();
                    Log.wtf("trade_price_8",price+"");

                    etLimitPrice.setText("" + price);
                    etLimitTriggerPrice.setText(setEtTriggerPriceValue());

                    if (price == 0.0) {
                        etLimitPrice.setText("0.0");
                    } else {

                        if (price > 100.9) {
                            Log.wtf("trade_price_9",Actions.formatNumber(price, Actions.NoDecimalThousandsSeparator)+"");
                            etLimitPrice.setText(Actions.formatNumber(price, Actions.NoDecimalThousandsSeparator));
                        } else {
                            Log.wtf("trade_price_10",Actions.formatNumber(price, Actions.NoDecimalThousandsSeparator)+"");
                            etLimitPrice.setText(Actions.formatNumber(price, priceFormat));
                        }
                    }

                    setParentOrder();
                }
                firstOpen = false;
                setInitialData();
                if(tradeType == MyApplication.ORDER_BUY){
                    buyClick();
                }else {
                    sellClick();
                }
                if(stockId==0)
                    etLimitPrice.setText("0");

                if(!BuildConfig.Enable_Markets)
                    llDuration.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!isAutoUpdate)
                setAutoUpdate();






            super.onPostExecute(result);
        }
    }





    private void showOrderDialog(){

        ordersDialog=new Dialog(this);
        ordersDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ordersDialog.setContentView(R.layout.popup_orders);
        ordersDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        rvDialogOrders = ordersDialog.findViewById(R.id.rvDialogOrders);
        btClear = ordersDialog.findViewById(R.id.btClear);
        etDialogSearch = ordersDialog.findViewById(R.id.etSearch);
        popupLoading=ordersDialog.findViewById(R.id.popupLoading);
        TextView tvExecutedQuantityHeader=ordersDialog.findViewById(R.id.tvExecutedQuantityHeader);
        tvExecutedQuantityHeader.setVisibility(View.VISIBLE);
        arrayFilteredOrderDialog.clear();
        adapterDialogOrders=new OrdersPopupTrades(this, arrayFilteredOrderDialog, TradesActivity.this,TradesActivity.this,false,selectedRelatedOrderId,true,false);
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        rvDialogOrders.setLayoutManager(glm);
        rvDialogOrders.setAdapter(adapterDialogOrders);



        // if(arrayFilteredOrderDialog.size()==0) {
        getUserOrders = new GetUserOrders();
        getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
        // }

        Spinner spFilter=ordersDialog.findViewById(R.id.spFilter);
        linearFilterTypePopup=ordersDialog.findViewById(R.id.linearFilterType);
        linearFilterTypePopup.setVisibility(View.INVISIBLE);


/*        etDialogSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.length() == 0) {

                    btClear.setVisibility(View.GONE);

                    try {
                        Actions.closeKeyboard(TradesActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }

                    adapterDialogOrders = new OrdersPopupTrades(TradesActivity.this, arrayFilteredOrderDialog,TradesActivity.this, TradesActivity.this::onItemClicked);
                    adapterDialogOrders.notifyDataSetChanged();
                    rvDialogOrders.setAdapter(adapterDialogOrders);
                } else {

                    btClear.setVisibility(View.VISIBLE);
                    adapterDialogOrders.getFilter().filter(arg0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });*/


        ordersDialog.show();
    }





    private class GetUserOrders extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            try{popupLoading.setVisibility(View.VISIBLE);}catch (Exception e){}
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetUserOrders.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key", MyApplication.currentUser.getKey());
            parameters.put("Lang", Actions.getLanguage());
            parameters.put("MarketID",MyApplication.marketID);
            parameters.put("Tstamp","0");


            try {
                result = ConnectionRequests.GET(url, TradesActivity.this, parameters);
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



            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                try{popupLoading.setVisibility(View.GONE);}catch (Exception e){}
                arrayFilteredOrderDialog.clear();
                arrayAllOrdersDialog.clear();
                ArrayList<OnlineOrder> retrievedOrders = GlobalFunctions.GetUserOrders(values[0]);
                arrayAllOrdersDialog.addAll(retrievedOrders);

                for (int i=0;i<arrayAllOrdersDialog.size();i++){
                    if(!arrayAllOrdersDialog.get(i).isAdvancedOrder() && arrayAllOrdersDialog.get(i).getOrderStatusTypeID()==1)
                        arrayFilteredOrderDialog.add(arrayAllOrdersDialog.get(i));
                }

                try{
                    runOnUiThread(() ->
                            adapterDialogOrders.notifyDataSetChanged()

                    );}catch (Exception e){}

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





    private void showStocksDialog(){

        Log.wtf("dialog_stock_size",MyApplication.stockQuotations.size()+"");
        stocksDialog=new Dialog(this);
        stocksDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        stocksDialog.setContentView(R.layout.popup_stocks);
        stocksDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        rvStocks = stocksDialog.findViewById(R.id.rvStocks);
        LinearLayout linearSearch=stocksDialog.findViewById(R.id.linearSearch);
        if(BuildConfig.Enable_Markets)
            linearSearch.setVisibility(View.VISIBLE);
        else
            linearSearch.setVisibility(View.GONE);
        btClear = stocksDialog.findViewById(R.id.btClear);
        etSearchStock = stocksDialog.findViewById(R.id.etSearch);
        adapterStockPopup=new stockQuotationPopupNewAdapter(this, getOrderedStockQuotation(), TradesActivity.this);
        GridLayoutManager glm = new GridLayoutManager(this, 1);
        rvStocks.setLayoutManager(glm);
        rvStocks.setAdapter(adapterStockPopup);
        etSearchStock.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.length() == 0) {

                    btClear.setVisibility(View.GONE);

                    try {
                        Actions.closeKeyboard(TradesActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }

                    adapterStockPopup = new stockQuotationPopupNewAdapter(TradesActivity.this, getOrderedStockQuotation(), TradesActivity.this::onItemClicked);
                    adapterStockPopup.notifyDataSetChanged();
                    rvStocks.setAdapter(adapterStockPopup);
                } else {

                    btClear.setVisibility(View.VISIBLE);
                    adapterStockPopup.getFilter().filter(arg0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });


        stocksDialog.show();
    }








    private void showFillDataDialog(){
        if(orderType == MyApplication.MARKET_PRICE) {
            price=Double.parseDouble(stockQuotation.getHiLimit()+"");
            Log.wtf("price_hi",price+"");
        }

        Log.wtf("dialog_stock_size",MyApplication.stockQuotations.size()+"");
        fillDataDialog=new Dialog(this);
        fillDataDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        fillDataDialog.setContentView(R.layout.popup_fill_data);
        fillDataDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // rvStocks = fillDataDialog.findViewById(R.id.rvStocks);
        Button btCloseFillData=fillDataDialog.findViewById(R.id.btCloseFillDialog);
        Button btPurchaseDecrement=fillDataDialog.findViewById(R.id.btPurchaseDecrement);
        Button btpurchaseIncrement=fillDataDialog.findViewById(R.id.btpurchaseIncrement);
        Button btFillDialog=fillDataDialog.findViewById(R.id.btFillDialog);

        etPurchasePowerPopup =fillDataDialog.findViewById(R.id.etPurchasePowerPopup);
        etQuantityPopup =fillDataDialog.findViewById(R.id.etQuantityPopup);
        etQuantityPopup.setEnabled(false);




        btCloseFillData.setOnClickListener(v->{
            try{   getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            ); }catch (Exception e){}
            fillDataDialog.dismiss();
        });

        btpurchaseIncrement.setOnClickListener(v->{
            if(!etPurchasePowerPopup.getText().toString().isEmpty())
                etPurchasePowerPopup.setText((Integer.parseInt(etPurchasePowerPopup.getText().toString().replaceAll("\\D+",""))+1)+"");
            else
                etPurchasePowerPopup.setText("1");
        });
        btPurchaseDecrement.setOnClickListener(v->{
            if(!etPurchasePowerPopup.getText().toString().isEmpty()) {
                if(Integer.parseInt(etPurchasePowerPopup.getText().toString().replaceAll("\\D+",""))>=1)
                    etPurchasePowerPopup.setText((Integer.parseInt(etPurchasePowerPopup.getText().toString().replaceAll("\\D+","")) -1 ) + "");
            }

        });

        etPurchasePowerPopup.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.length() == 0) {
                    Log.wtf("empty","empty");
                    etQuantityPopup.setText("");

                } else {

                    // btClear.setVisibility(View.VISIBLE);
                    // etQuantityPopup.setText(arg0);
                    updateQty();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }


            @Override
            public void afterTextChanged(Editable s) {


                etPurchasePowerPopup.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = etPurchasePowerPopup.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = etPurchasePowerPopup.getSelectionStart();
                    if (hasFractionalPart) {
                        etPurchasePowerPopup.setText(df.format(n));
                    } else {
                        etPurchasePowerPopup.setText(dfnd.format(n));
                    }
                    endlen = etPurchasePowerPopup.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= etPurchasePowerPopup.getText().length()) {
                        etPurchasePowerPopup.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        etPurchasePowerPopup.setSelection(etPurchasePowerPopup.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }

                etPurchasePowerPopup.addTextChangedListener(this);

            }

        });


        etQuantityPopup.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }


            @Override
            public void afterTextChanged(Editable s) {


                etQuantityPopup.removeTextChangedListener(this);

                try {
                    int inilen, endlen;
                    inilen = etQuantityPopup.getText().length();

                    String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
                    Number n = df.parse(v);
                    int cp = etQuantityPopup.getSelectionStart();
                    if (hasFractionalPart) {
                        etQuantityPopup.setText(df.format(n));
                    } else {
                        etQuantityPopup.setText(dfnd.format(n));
                    }
                    endlen = etQuantityPopup.getText().length();
                    int sel = (cp + (endlen - inilen));
                    if (sel > 0 && sel <= etQuantityPopup.getText().length()) {
                        etQuantityPopup.setSelection(sel);
                    } else {
                        // place cursor at the end?
                        etQuantityPopup.setSelection(etQuantityPopup.getText().length() - 1);
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing?
                } catch (ParseException e) {
                    // do nothing?
                }

                etQuantityPopup.addTextChangedListener(this);

            }

        });

        btFillDialog.setOnClickListener(v->{
            if(!etQuantityPopup.getText().toString().isEmpty()) {
                quantity = (Integer.parseInt(etQuantityPopup.getText().toString().replaceAll("\\D+", "")));
                fillDataDialog.dismiss();
                etQuantity.setText(quantity+"");
                updateOverAllViews(price,quantity);
            }
        });




        fillDataDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{etPurchasePowerPopup.setText(tvPurchasePowerValue.getText().toString().replaceAll("\\D+",""));}catch (Exception e){
                    Log.wtf("aaa_",e.toString());

                }
                try{
                    btpurchaseIncrement.performClick();
                    btPurchaseDecrement.performClick();

                }catch (Exception e){}
            }
        }, 200);
    }


    private ArrayList<StockQuotation> getOrderedStockQuotation(){
        ArrayList<StockQuotation> array=new ArrayList<>();
        if(BuildConfig.Enable_Markets)
            return MyApplication.stockQuotations;
        else {
            ArrayList<String> arraySectorIds=new ArrayList<>();
            ArrayList<SectorTitles> arraySectorTitles=new ArrayList<>();
            for (int i=0;i<MyApplication.stockQuotations.size();i++){
                if(!arraySectorIds.contains(MyApplication.stockQuotations.get(i).getSectorID())) {
                    arraySectorIds.add(MyApplication.stockQuotations.get(i).getSectorID());
                    arraySectorTitles.add(new SectorTitles(
                            MyApplication.stockQuotations.get(i).getSectorID(),
                            MyApplication.stockQuotations.get(i).getSectorNameEn(),
                            MyApplication.stockQuotations.get(i).getSectorNameAr(),
                            MyApplication.stockQuotations.get(i).getSymbolEn(),
                            MyApplication.stockQuotations.get(i).getSymbolAr()


                    ));
                }
            }



            for (int i=0;i<arraySectorTitles.size();i++){
                array.add(new StockQuotation(-1,arraySectorTitles.get(i).getSectorId(),arraySectorTitles.get(i).getSectorNameAr(),arraySectorTitles.get(i).getSectorNameEn(),arraySectorTitles.get(i).getSymbolEn(),arraySectorTitles.get(i).getSymbolAr()));
                for (int j=0;j<MyApplication.stockQuotations.size();j++){
                    if(MyApplication.stockQuotations.get(j).getSectorID().matches(arraySectorTitles.get(i).getSectorId()))
                        array.add(MyApplication.stockQuotations.get(j));
                }
            }
            return array;
        }
    }


    private class ValidateOrder extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradesActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                random = Actions.getRandom();

                //   String cofirmPin = etConfirm.getText().toString();
                //   encrypted = Actions.MD5(cofirmPin);

                encrypted = encrypted + random;

                tradingPin = Actions.MD5(encrypted);
            } else {

                random = "";

                encrypted = "";

                tradingPin = "";
            }
        }



        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.ValidateOrder.getValue();
            Log.wtf("validate", "GoodUntilDate : '" + trade.getGoodUntilDate() + "'");
            Log.wtf("validate","order status: "+trade.getStatusTypeId());
            Log.wtf("validate","order type: "+trade.getOrderType());
            JSONStringer stringer = null;
            int selectedMaxFloor=0;
            if(!etMaxFloor.getText().toString().isEmpty())
                selectedMaxFloor=Integer.parseInt(getNumberFromString(etMaxFloor.getText().toString()));
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("UserID").value(MyApplication.selectedSubAccount.getUserId())
                        .key("InvestorID").value(MyApplication.selectedSubAccount.getInvestorId())
                        .key("PortfolioID").value(MyApplication.selectedSubAccount.getPortfolioId())

                        //.key("ApplicationType").value(7)
                        .key("ApplicationType").value(Actions.getApplicationType())
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("Reference").value(0)
                        .key("BrokerID").value(Actions.getBrokerEmployeeID())
                        .key("DurationID").value(trade.getDurationTypeId())
                        .key("GoodUntilDate").value(trade.getGoodUntilDate()) //iza na2a date be3abe, iza ma na2a mnb3ato fade
                        .key("Price").value(trade.getOrderType() == 1 ? 0.0 : trade.getPrice())
                        .key("TriggerPrice").value(trade.getOrderType() == 1 ? 0.0 : trade.getTriggerPrice())
                        .key("OrderTypeID").value(trade.getOrderType())
                        .key("Quantity").value(trade.getQuantity())
                        .key("StockID").value(String.valueOf(trade.getStockQuotation().getStockID()))
                        .key("TradeTypeID").value(trade.getTradeTypeID())
                        .key("StatusID").value(trade.getStatusTypeId()) //16 iza private checkbox checked, else 1
                        .key("OperationTypeID").value(trade.getOperationTypeID())//4 harcoded ademe ios if privateCb is checked, else 1
                        .key("BrokerEmployeeID").value(0)
                        .key("ForwardContractID").value(0)
                        .key("MarketID").value(Actions.getMarketType(Integer.parseInt(MyApplication.marketID)))
                        //.key("StockID").value(trade.getStockQuotation().getStockID()+"")

                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(random)
                        .key("TradingSession").value(Actions.getMarketSegmentParameter(trade.getStockQuotation().getTradingSession()))

                        .key("key").value(MyApplication.currentUser.getKey())

                        .key("AdminID").value(0)
                        .key("RelatedOnlineOrderID").value(selectedRelatedOrderId)
                        .key("AdvancedOrderTypeID").value(orderTypeAdvanced)
                        .key("MaxFloor").value(selectedMaxFloor)
                        .key("IsParentOrder").value(1)



                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddNewOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            Log.wtf("TradesActivity", "validateOrder");
            Log.wtf("TradesActivity : url ='" + url + "'", " / JSONStringer = '" + stringer.toString() + "'");
            result = ConnectionRequests.POSTWCF(url, stringer);
            Log.wtf("Result", "is " + result);
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

            JSONObject object = null;
            try {
                object = new JSONObject(result);
                int success = object.getInt("Status");
                if (success==0) {
                    String message;
                    message = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    //CreateDialogResponse(TradesActivity.this, message);
                    addOrderToRelated();

                } else {
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    Actions.CreateDialog(TradesActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(TradesActivity.this, getResources().getString(R.string.error), false, false);
            }
        }
    }






}
