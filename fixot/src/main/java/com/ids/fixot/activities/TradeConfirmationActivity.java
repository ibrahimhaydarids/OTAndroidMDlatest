package com.ids.fixot.activities;

import android.app.Activity;
import android.app.AlertDialog;
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

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.OrderInfoRecyclerAdapter;
import com.ids.fixot.adapters.OrdersPopupTrades;
import com.ids.fixot.adapters.stockQuotationPopupAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderInfo;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.Trade;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DEV on 2/16/2018.
 */

public class TradeConfirmationActivity extends AppCompatActivity implements MarketStatusListener, spItemListener , OrdersPopupTrades.RefreshInterface,OrdersPopupTrades.RecyclerViewOnItemClickListener{

    RelativeLayout rootLayout;
    LinearLayout llConfirm,linearSubOrders,linearFilterType;
    Button btConfirm;
    EditText etConfirm;
    TextView tvSubOrderHeader;

    private ArrayList<OrderInfo> arrayOrderInfo=new ArrayList<>();

    int quantity = 0;
    Trade trade;
    StockQuotation stockQuotation;
    String dateFormatter = "dd/MM/yyyy 00:00:00";
    private BroadcastReceiver receiver;
    private boolean started = false;
    Spinner spInstrumentsTop;
    int trading_session=0;

    private RecyclerView rvSubOrders,rvOrderInfo;
    OrdersPopupTrades adapterOrders;
    OrderInfoRecyclerAdapter adapterOrderInfo;

    private String priceFormat=Actions.TwoDecimal;






    public TradeConfirmationActivity() {
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
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(BuildConfig.Enable_Markets){
            priceFormat=Actions.TwoDecimal;
        }else {
            priceFormat=Actions.ThreeDecimal;
        }

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_confirm_trade);
        Actions.initializeBugsTracking(this);



        started = true;
    try{        trading_session=getIntent().getExtras().getInt("trading_session");}catch (Exception e){}
        trade = getIntent().getExtras().getParcelable("trade");

    try{
        Log.wtf("duration_type",trade.getDurationTypeId()+"aaa");
        Log.wtf("duration_type_gtd",trade.getGoodUntilDate());
       // Log.wtf("duration_type_gtd_sub",trade.getGoodUntilDate().substring(trade.getGoodUntilDate().indexOf("-")+1));
        if(trade.getDurationTypeId()==6){
            String gtd=trade.getGoodUntilDate().substring(trade.getGoodUntilDate().indexOf("-")+1).trim();
            Log.wtf("duration_type_gtd_sub1",gtd);


            trade.setGoodUntilDate(gtd);
            Log.wtf("duration_type_gtd_sub2",trade.getGoodUntilDate());
        }

    }catch (Exception e){
        Log.wtf("duration_type_excep",e.toString());
    }

    try {
        Log.wtf("trading_session",trade.getStockQuotation().getTradingSession()+"");
        Log.wtf("trading_session_2",trading_session+"");
    }catch (Exception e){}

    try{Log.wtf("trigger_price",trade.getTriggerPrice()+"aa");}catch (Exception e){}
        try{Log.wtf("trigger_price_advanced_order",trade.getAdvancedOrderType()+"aa");}catch (Exception e){}

        //Toast.makeText(getApplicationContext(),"asdsadas"+trade.getStockQuotation().getTradingSession(),Toast.LENGTH_LONG).show();
        try{stockQuotation = trade.getStockQuotation();}catch (Exception e){}

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.trades_confirmation_page_title), TradeConfirmationActivity.this);

        Actions.overrideFonts(this, rootLayout, false);
        Actions.showHideFooter(this);

        setTypefaces();

        Log.wtf("AddNewOrder", "GoodUntilDate : '" + trade.getGoodUntilDate() + "'");

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

    private void setTypefaces() {

        Actions.setTypeface(new TextView[]{tvSubOrderHeader},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);


       // Actions.setTypeface(new TextView[]{tvQuantityValue, tvPriceValue, tvCostValue, tvCommissionValue, tvOverallValue}, MyApplication.giloryBold);
    }

    public void back(View v) {
        TradeConfirmationActivity.this.finish();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        llConfirm = findViewById(R.id.llConfirm);
        btConfirm = findViewById(R.id.btConfirm);
        etConfirm = findViewById(R.id.etConfirm);
        rvSubOrders = findViewById(R.id.rvSubOrders);
        rvOrderInfo = findViewById(R.id.rvOrderInfo);
        linearSubOrders = findViewById(R.id.linearSubOrders);
        tvSubOrderHeader=findViewById(R.id.tvSubOrderHeader);
        linearFilterType=findViewById(R.id.linearFilterType);
        linearFilterType.setVisibility(View.GONE);
        etConfirm.setVisibility(MyApplication.currentUser.isTradingPasswordMandatory() ? View.VISIBLE : View.GONE);
        setData();
        TextView tvExecutedQuantityHeader=findViewById(R.id.tvExecutedQuantityHeader);
        tvExecutedQuantityHeader.setVisibility(View.INVISIBLE);

    }

    private void setData(){

        try {

            String stockValue = MyApplication.lang == MyApplication.ARABIC ?
                    /*(stockQuotation.getSecurityId() + "-" + stockQuotation.getSymbolAr())+"-"+*/stockQuotation.getNameAr()+"" : /*(stockQuotation.getSymbolEn() + "-" + */stockQuotation.getNameEn()+"";
                    arrayOrderInfo.add(new OrderInfo(getString(R.string.stock_name_title),stockValue));
        }catch (Exception e){}




        if(trade.getAdvancedOrderType()!=0) {
            if(trade.getAdvancedOrderType()==MyApplication.MO){
                arrayOrderInfo.add(new OrderInfo(getString(R.string.order_type_advanced),  "MO"));
            }else if(trade.getAdvancedOrderType()==MyApplication.ICEBERG){
                arrayOrderInfo.add(new OrderInfo(getString(R.string.order_type_advanced), "ICEBERG"));
            }else if(trade.getAdvancedOrderType()==MyApplication.OCA){
                arrayOrderInfo.add(new OrderInfo(getString(R.string.order_type_advanced), "OCA"));
            }


        }



   /*     if(trade.getRelatedOrderId()!=0)
            arrayOrderInfo.add(new OrderInfo(getString(R.string.managed_order),trade.getRelatedOrderId()+""));*/


        if (getIntent().getExtras().getBoolean("isUpdate"))
              quantity = trade.getQuantity() + trade.getExecutedQuantity();
        else
              quantity = trade.getQuantity();

        arrayOrderInfo.add(new OrderInfo(getString(R.string.quantity),Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator)));

      if(BuildConfig.Enable_Markets) {
          if (trade.getMaxFloor() != 0)
              arrayOrderInfo.add(new OrderInfo(getString(R.string.max_floor),Actions.formatNumber(trade.getMaxFloor(), Actions.NoDecimalThousandsSeparator)));
              //arrayOrderInfo.add(new OrderInfo(getString(R.string.max_floor), trade.getMaxFloor() + ""));
      }else {
          if(trade.getOrderType()!= MyApplication.MARKET_PRICE)
              arrayOrderInfo.add(new OrderInfo(getString(R.string.max_floor),Actions.formatNumber(trade.getMaxFloor(), Actions.NoDecimalThousandsSeparator)));
      }

        arrayOrderInfo.add(new OrderInfo(getString(R.string.price),Actions.formatNumber(trade.getPrice(), priceFormat)));

        if(trade.getOrderType()== MyApplication.MARKET_IF_TOUCHED || trade.getOrderType()==MyApplication.LIMIT_IF_TOUCHED || trade.getOrderType()==MyApplication.SI)
           arrayOrderInfo.add(new OrderInfo(getString(R.string.trigger_price),Actions.formatNumber(trade.getTriggerPrice(), priceFormat)));

        String orderType="";
                try{

            if (MyApplication.lang == MyApplication.ARABIC)
               orderType=trade.getOrdertypeValueAr();
            else
                orderType=trade.getOrdertypeValueEn();

        }catch (Exception e){}

        arrayOrderInfo.add(new OrderInfo(getString(R.string.action),trade.getTradeTypeID() == MyApplication.ORDER_BUY ? getResources().getString(R.string.buy) +" - "+orderType : getResources().getString(R.string.sell)+" - "+orderType ));
        arrayOrderInfo.add(new OrderInfo(getString(R.string.trade_date),trade.getDate()));



        arrayOrderInfo.add(new OrderInfo(getString(R.string.order_time),trade.getDurationType()));
        arrayOrderInfo.add(new OrderInfo(getString(R.string.trades_total_title),Actions.formatNumber(trade.getOverallTotal(), Actions.ThreeDecimalThousandsSeparator)));
        arrayOrderInfo.add(new OrderInfo(getString(R.string.trades_commission_title),Actions.formatNumber(trade.getCommission(), Actions.ThreeDecimalThousandsSeparator)));


        arrayOrderInfo.add(new OrderInfo(getString(R.string.trades_total_cost_title),Actions.formatNumber(

                trade.getTradeTypeID() == MyApplication.ORDER_BUY ?trade.getOverallTotal()+trade.getCommission():trade.getOverallTotal()-trade.getCommission()
                , Actions.ThreeDecimalThousandsSeparator)));

        // arrayOrderInfo.add(new OrderInfo(getString(R.string.trades_total_cost_title),Actions.formatNumber(trade.getCost(), Actions.ThreeDecimalThousandsSeparator)));









        if(trade.getRelatedOrderId()!=0 ){
            linearSubOrders.setVisibility(View.VISIBLE);
            tvSubOrderHeader.setText(getString(R.string.related_order));


            try {

                ArrayList<OnlineOrder> relatedOrder=new ArrayList<>();
                relatedOrder.add(trade.getRelatedOrder());
                LinearLayoutManager llm = new LinearLayoutManager(TradeConfirmationActivity.this);
                adapterOrders = new OrdersPopupTrades(TradeConfirmationActivity.this, relatedOrder, this, this,false,0,false,true);
                rvSubOrders.setLayoutManager(llm);
                rvSubOrders.setAdapter(adapterOrders);
            } catch (Exception e) {
                Log.wtf("OrdersPopupTrades error", "error = " + e.getMessage());
            }
        }
        else  if(trade.getArraySubOrders()==null || trade.getArraySubOrders().size()==0)
            linearSubOrders.setVisibility(View.GONE);
        else {
            linearSubOrders.setVisibility(View.VISIBLE);
            try {
                LinearLayoutManager llm = new LinearLayoutManager(TradeConfirmationActivity.this);
                adapterOrders = new OrdersPopupTrades(TradeConfirmationActivity.this, trade.getArraySubOrders(), this, this,false,0,false,true);
                rvSubOrders.setLayoutManager(llm);
                rvSubOrders.setAdapter(adapterOrders);
            } catch (Exception e) {
                Log.wtf("OrdersPopupTrades error", "error = " + e.getMessage());
            }
        }



        try {
            LinearLayoutManager llm = new LinearLayoutManager(TradeConfirmationActivity.this);
            adapterOrderInfo = new OrderInfoRecyclerAdapter(TradeConfirmationActivity.this, arrayOrderInfo);
            rvOrderInfo.setLayoutManager(llm);
            rvOrderInfo.setAdapter(adapterOrderInfo);
        } catch (Exception e) {
            Log.wtf("OrdersPopupTrades error", "error = " + e.getMessage());
        }

    }

    private void setListeners() {

        btConfirm.setOnClickListener(v -> {

            try {
                Actions.closeKeyboard(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                if (etConfirm.getText().toString().length() > 0) {
                    btConfirm.setClickable(false);

                    if (getIntent().getExtras().getBoolean("isUpdate")) {

                        new UpdateOrder().execute();
                    } else {
                        if(trade.getAdvancedOrderType()==MyApplication.OCA)
                            new AddOrderOCA().execute();
                        else
                            new AddOrder().execute();

                    }
                } else {

                    Animation shake = AnimationUtils.loadAnimation(TradeConfirmationActivity.this, R.anim.shake);
                    etConfirm.startAnimation(shake);
                }

            } else {
                btConfirm.setClickable(false);

                if (getIntent().getExtras().getBoolean("isUpdate")) {

                    new UpdateOrder().execute();
                } else {

                    if(trade.getAdvancedOrderType()==MyApplication.OCA)
                        new AddOrderOCA().execute();
                    else
                        new AddOrder().execute();
                    //new AddOrder().execute();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);

        //Actions.InitializeSessionService(this);
        //Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        // Actions.InitializeMarketServiceV2(this);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}


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
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onItemClicked(View v, int position) {

    }


    private class AddOrderOCA extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                random = Actions.getRandom();

                String cofirmPin = etConfirm.getText().toString();
                encrypted = Actions.MD5(cofirmPin);

                encrypted = encrypted + random;

                tradingPin = Actions.MD5(encrypted);
            } else {

                random = "";

                encrypted = "";

                tradingPin = "";
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddNewOrderList.getValue();

            Log.wtf("AddNewOrder", "GoodUntilDate : '" + trade.getGoodUntilDate() + "'");
            Log.wtf("update_order","order status: "+trade.getStatusTypeId());
            Log.wtf("update_order","order type: "+trade.getOrderType());

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("UserID").value(MyApplication.selectedSubAccount.getUserId())
                        .key("InvestorID").value(MyApplication.selectedSubAccount.getInvestorId())
                        .key("PortfolioID").value(MyApplication.selectedSubAccount.getPortfolioId())
                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(random)
                      //  .key("ApplicationType").value(7)
                        .key("ApplicationType").value(Actions.getApplicationType())
                        .key("BrokerID").value(Actions.getBrokerEmployeeID())
                        .key("BrokerEmployeeID").value(0)
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("AdminID").value(0)
                        .key("key").value(MyApplication.currentUser.getKey())
                        .key("MarketID").value(Actions.getMarketType(Integer.parseInt(MyApplication.marketID)))
                        .key("OnlineOrderRequest").array();
                        try{

                    if(trade.getArraySubOrders()!=null && trade.getArraySubOrders().size()>1){
                        OnlineOrder myOrder=null;
                        int isParentOrder=1;
                        int relatedOrder=0;
                        for (int i=0;i<trade.getArraySubOrders().size();i++){
                            myOrder=trade.getArraySubOrders().get(i);
                            if(i>0) {
                                isParentOrder = 0;
                                relatedOrder=0;
                            }
                            else  {
                                relatedOrder = trade.getId();
                                isParentOrder=1;
                            }



                                            stringer.object()
                                            .key("Reference").value(myOrder.getReference())
                                            .key("DurationID").value(myOrder.getDurationID())
                                            .key("GoodUntilDate").value(myOrder.getGoodUntilDate()) //iza na2a date be3abe, iza ma na2a mnb3ato fade
                                            .key("Price").value(myOrder.getOrderTypeID() == 1 ? 0.0 : myOrder.getPrice())
                                            .key("TriggerPrice").value(myOrder.getOrderTypeID() == 1 ? 0.0 : myOrder.getTriggerPrice())
                                            .key("OrderTypeID").value(myOrder.getOrderTypeID())
                                            .key("Quantity").value(myOrder.getQuantity())
                                            .key("TradeTypeID").value(myOrder.getTradeTypeID())
                                            .key("StatusID").value(myOrder.getStatusID()) //16 iza private checkbox checked, else 1
                                            .key("OperationTypeID").value(myOrder.getOperationTypeID())//4 harcoded ademe ios if privateCb is checked, else 1
                                            .key("ForwardContractID").value(myOrder.getForwardContractID())
                                            .key("StockID").value(myOrder.getStockID()+"")
                                            .key("TradingSession").value(Actions.getMarketSegmentParameter(myOrder.getTradingSessionID()))
                                            .key("RelatedOnlineOrderID").value(relatedOrder)
                                            .key("AdvancedOrderTypeID").value(myOrder.getAdvancedOrderTypeID())
                                            .key("MaxFloor").value(myOrder.getMaxfloor())
                                            .key("IsParentOrder").value(isParentOrder).endObject();

                        }


                    }
                /*    else {
                                         stringer.object()
                                        .key("Reference").value(0)
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
                                        .key("ForwardContractID").value(0)
                                        .key("TradingSession").value(Actions.getMarketSegmentParameter(trading_session))
                                        .key("RelatedOnlineOrderID").value(trade.getRelatedOrderId())
                                        .key("AdvancedOrderTypeID").value(trade.getAdvancedOrderType())
                                        .key("MaxFloor").value(trade.getMaxFloor())
                                        .key("IsParentOrder").value(1).endObject();
                    }*/
                    stringer.endArray().endObject();


                            Log.wtf("TradeConfiramtionActivity : url ='" + url + "'", " / JSONStringer = '" + stringer.toString() + "'");

                }catch (Exception e){}





                //     .key("OnlineOrderRequest").value(arrayOnlineOrderRequest)


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


            Log.wtf("TradeConfiramtionActivity", "AddNewOrder");
            Log.wtf("TradeConfiramtionActivity_url","url: "+url + "JSONStringer = '" + stringer.toString() + "'");
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
                if (success==0 || success==57) {
                    btConfirm.setClickable(false);
                    String message;
                   // message = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    message=getString(R.string.order_success);
                    CreateDialogResponse(TradeConfirmationActivity.this, message);
          /*          finishAffinity();
                    Intent intent = new Intent(TradeConfirmationActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);*/

                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(TradeConfirmationActivity.this, getResources().getString(R.string.error), false, false);
            }
        }
    }

    private class UpdateOrder extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            quantity = trade.getQuantity() + trade.getExecutedQuantity();


            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                random = Actions.getRandom();

                encrypted = Actions.MD5(etConfirm.getText().toString());

                encrypted = encrypted + random;

                tradingPin = Actions.MD5(encrypted);
            } else {

                random = "";

                encrypted = "";

                tradingPin = "";
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.UpdateOrder.getValue();


            String date = "";
            if (trade.getDurationTypeId() != 6) {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
                date = sdf.format(new Date());
            } else {
                date = trade.getGoodUntilDate();
            }

            String triggerPrice="";
            if(trade.getOrderType()!=MyApplication.MARKET_PRICE && trade.getOrderType()!=MyApplication.LIMIT && trade.getAdvancedOrderType()==0)
                    triggerPrice=trade.getTriggerPrice()+"";
            else
                triggerPrice="";

            Double price=0.0;
            if(trade.getAdvancedOrderType()!=MyApplication.MARKET_IF_TOUCHED && trade.getOrderType()!=1){
               price=trade.getPrice() ;
            }



            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("UserID").value(MyApplication.currentUser.getId())
                        .key("InvestorID").value(MyApplication.currentUser.getInvestorId())
                        .key("PortfolioID").value(MyApplication.currentUser.getPortfolioId())
                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(random)
                     //   .key("ApplicationType").value(7)
                        .key("ApplicationType").value(Actions.getApplicationType())
                        .key("Reference").value(trade.getReference())
                        .key("BrokerID").value(Actions.getBrokerEmployeeID())
                        .key("DurationID").value(trade.getDurationTypeId())

                        .key("GoodUntilDate").value(date) //iza na2a date be3abe, iza ma na2a mnb3ato fade


                        .key("TriggerPrice").value(triggerPrice)
                        .key("Price").value(price)
                        //.key("Price").value(trade.getOrderType() == 1 ? 0.0 : trade.getPrice())
                       // .key("TriggerPrice").value(trade.getOrderType() == 1 ? 0.0 : trade.getTriggerPrice())
                        .key("OrderTypeID").value(trade.getOrderType())
                        .key("Quantity").value(quantity)
                        .key("StockID").value(String.valueOf(trade.getStockQuotation().getStockID()))
                        .key("TradeTypeID").value(trade.getTradeTypeID())

                        .key("MarketID").value(Actions.getMarketType(Integer.parseInt(MyApplication.marketID)))
                        .key("TradingSession").value(Actions.getMarketSegmentParameter(trading_session))

                        .key("StatusID").value(trade.getStatusTypeId()) //1 iza private checkbox checked, else 16
                        .key("OperationTypeID").value(trade.getOperationTypeID())//0 harcoded ademe ios if privateCb is checked, else 4

                        .key("BrokerEmployeeID").value(0)
                        .key("ForwardContractID").value(0)
                        .key("key").value(MyApplication.currentUser.getKey())

                        .key("RelatedOnlineOrderID").value(trade.getRelatedOrderId())
                        .key("AdvancedOrderTypeID").value(trade.getAdvancedOrderType())
                        .key("MaxFloor").value(trade.getMaxFloor())
                        .key("IsSubOrder").value(trade.getIsSubOrder())



                        .key("RelatedOnlineOrderID").value(trade.getRelatedOrderId())
                        .key("AdvancedOrderTypeID").value(trade.getAdvancedOrderType())
                        .key("MaxFloor").value(trade.getMaxFloor())
                        .key("IsSubOrder").value(trade.getIsSubOrder())


                        .key("MinQty").value("")
                        .key("ParentUserID").value(MyApplication.currentUser.getParentUserId())
                        .key("ClientTypeID").value(MyApplication.currentUser.getClientTypeID())
                        .key("SubAccount").value(MyApplication.selectedSubAccount.getSubAccount())
                        .endObject();


            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.UpdateOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            Log.wtf("TradeConfiramtionActivity - UpdateOrder ", " DurationID : " + trade.getDurationTypeId());
            Log.wtf("TradeConfiramtionActivity", "UpdateOrder");
            Log.wtf("TradeConfiramtionActivity : url ='" + url + "' ", " / JSONStringer = '" + stringer + "'");
            result = ConnectionRequests.POSTWCF(url, stringer);
            Log.wtf("update result", "is " + result);
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
                if (success==0 || success==57) {

                    btConfirm.setClickable(false);
                    String message;
                   // message = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");

                     message=getString(R.string.order_success);
                    CreateDialogResponse(TradeConfirmationActivity.this, message);


                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.UpdateOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }



    private class AddOrder extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                random = Actions.getRandom();

                String cofirmPin = etConfirm.getText().toString();
                encrypted = Actions.MD5(cofirmPin);

                encrypted = encrypted + random;

                tradingPin = Actions.MD5(encrypted);
            } else {

                random = "";

                encrypted = "";

                tradingPin = "";
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddNewOrder.getValue();

            Log.wtf("AddNewOrder", "GoodUntilDate : '" + trade.getGoodUntilDate() + "'");
            Log.wtf("update_order","order status: "+trade.getStatusTypeId());
            Log.wtf("update_order","order type: "+trade.getOrderType());

            String triggerPrice="";
            if(trade.getOrderType()!=MyApplication.MARKET_PRICE && trade.getOrderType()!=MyApplication.LIMIT && trade.getAdvancedOrderType()==0)
                triggerPrice=trade.getTriggerPrice()+"";
            else
                triggerPrice="";

            Log.wtf("trigger_price","trigger "+triggerPrice);

            Double price=0.0;
            if(trade.getAdvancedOrderType()!=MyApplication.MARKET_IF_TOUCHED && trade.getOrderType()!=1){
                price=trade.getPrice() ;
            }


            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("UserID").value(MyApplication.selectedSubAccount.getUserId())
                        .key("InvestorID").value(MyApplication.selectedSubAccount.getInvestorId())
                        .key("PortfolioID").value(MyApplication.selectedSubAccount.getPortfolioId())
                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(random)
                       // .key("ApplicationType").value(7)
                        .key("ApplicationType").value(Actions.getApplicationType())
                        .key("Reference").value(0)
                        .key("BrokerID").value(Actions.getBrokerEmployeeID())
                        .key("DurationID").value(trade.getDurationTypeId())
                        .key("GoodUntilDate").value(trade.getGoodUntilDate()) //iza na2a date be3abe, iza ma na2a mnb3ato fade
                       // .key("Price").value(trade.getOrderType() == 1 ? 0.0 : trade.getPrice())
                        //.key("TriggerPrice").value(trade.getOrderType() == 1 ? 0.0 : trade.getTriggerPrice())
                        .key("Price").value(price)
                        .key("TriggerPrice").value(triggerPrice)
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
                        .key("TradingSession").value(Actions.getMarketSegmentParameter(trading_session))

                        .key("key").value(MyApplication.currentUser.getKey())
                        .key("RelatedOnlineOrderID").value(trade.getRelatedOrderId())
                        .key("AdvancedOrderTypeID").value(trade.getAdvancedOrderType())
                        .key("MaxFloor").value(trade.getMaxFloor())
                        .key("IsSubOrder").value(trade.getIsSubOrder())


                        .key("MinQty").value("")
                        .key("ParentUserID").value(MyApplication.currentUser.getParentUserId())
                        .key("ClientTypeID").value(MyApplication.currentUser.getClientTypeID())
                        .key("SubAccount").value(MyApplication.selectedSubAccount.getSubAccount())

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

            Log.wtf("TradeConfiramtionActivity - AddNewOrder ", " DurationID : " + trade.getDurationTypeId());
            Log.wtf("TradeConfiramtionActivity", "AddNewOrder");
            Log.wtf("TradeConfiramtionActivity : url ='" + url + "'", " / JSONStringer = '" + stringer.toString() + "'");
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
                if (success==0 || success==57) {
                    btConfirm.setClickable(false);
                    String message;
                   // message = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");

                    message=getString(R.string.order_success);
                    CreateDialogResponse(TradeConfirmationActivity.this, message);
          /*          finishAffinity();
                    Intent intent = new Intent(TradeConfirmationActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);*/

                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(TradeConfirmationActivity.this, getResources().getString(R.string.error), false, false);
            }
        }
    }



    public void CreateDialogResponse(final Activity c, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setCancelable(false)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    finishAffinity();
                    Intent intent = new Intent(TradeConfirmationActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }













}
