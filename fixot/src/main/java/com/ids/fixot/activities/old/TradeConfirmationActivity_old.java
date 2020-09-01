package com.ids.fixot.activities.old;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.ids.fixot.activities.OrdersActivity;
import com.ids.fixot.adapters.OrdersPopupTrades;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.Trade;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by DEV on 2/16/2018.
 */

public class TradeConfirmationActivity_old extends AppCompatActivity implements MarketStatusListener, spItemListener , OrdersPopupTrades.RefreshInterface,OrdersPopupTrades.RecyclerViewOnItemClickListener{

    RelativeLayout rootLayout;
    LinearLayout llConfirm;
    Button btConfirm;
    EditText etConfirm;
    TextView tvStockValue,tvOrderTypeValue,tvAdvancedOrderTypeValue,tvPriceTitle,tvTriggerPriceTitle,tvTriggerPricevalue, tvQuantityValue, tvPriceValue, tvTradeTypeValue, tvDateValue, tvDurationTypeValue;
    TextView tvCostValue, tvCommissionValue, tvOverallValue,tvMaxFloorValue,tvRelatedOrderValue;

    int quantity = 0;
    Trade trade;
    StockQuotation stockQuotation;
    String dateFormatter = "dd/MM/yyyy 00:00:00";
    private BroadcastReceiver receiver;
    private boolean started = false;
    Spinner spInstrumentsTop;
    int trading_session=0;
    private LinearLayout llTriggerPrice,linearAdvancedOrderType,linearMaxFloor,linearRelatedOrder,linearSubOrders,linearFilterType;
    private RecyclerView rvSubOrders;
    OrdersPopupTrades adapterOrders;


    public TradeConfirmationActivity_old() {
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

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_confirm_trade_old);
        Actions.initializeBugsTracking(this);

        started = true;
        trading_session=getIntent().getExtras().getInt("trading_session");
        trade = getIntent().getExtras().getParcelable("trade");
        Log.wtf("trading_session",trade.getStockQuotation().getTradingSession()+"");
        Log.wtf("trading_session_2",trading_session+"");
        //Toast.makeText(getApplicationContext(),"asdsadas"+trade.getStockQuotation().getTradingSession(),Toast.LENGTH_LONG).show();
        stockQuotation = trade.getStockQuotation();

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.trades_confirmation_page_title), TradeConfirmationActivity_old.this);

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

        Actions.setTypeface(new TextView[]{tvStockValue, tvTradeTypeValue, tvDateValue, tvDurationTypeValue},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);


        Actions.setTypeface(new TextView[]{tvQuantityValue, tvPriceValue, tvCostValue, tvCommissionValue, tvOverallValue}, MyApplication.giloryBold);
    }

    public void back(View v) {
        TradeConfirmationActivity_old.this.finish();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        llConfirm = findViewById(R.id.llConfirm);
        btConfirm = findViewById(R.id.btConfirm);
        etConfirm = findViewById(R.id.etConfirm);


        etConfirm.setVisibility(MyApplication.currentUser.isTradingPasswordMandatory() ? View.VISIBLE : View.GONE);

        tvStockValue = findViewById(R.id.tvStockValue);
        tvOrderTypeValue = findViewById(R.id.tvOrderTypeValue);

        tvPriceTitle = findViewById(R.id.tvPriceTitle);

        tvTriggerPriceTitle = findViewById(R.id.tvTriggerPriceTitle);
        tvTriggerPricevalue = findViewById(R.id.tvTriggerPriceValue);
        llTriggerPrice = findViewById(R.id.llTriggerPrice);

        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        tvPriceValue = findViewById(R.id.tvPriceValue);
        tvTradeTypeValue = findViewById(R.id.tvTradeTypeValue);
        tvDateValue = findViewById(R.id.tvDateValue);
        tvDurationTypeValue = findViewById(R.id.tvDurationTypeValue);
        tvCostValue = findViewById(R.id.tvCostValue);
        tvCommissionValue = findViewById(R.id.tvCommissionValue);
        tvOverallValue = findViewById(R.id.tvOverallValue);

        linearAdvancedOrderType = findViewById(R.id.linearAdvancedOrderType);
        tvAdvancedOrderTypeValue = findViewById(R.id.tvAdvancedOrderTypeValue);
        linearMaxFloor = findViewById(R.id.linearMaxFloor);
        tvMaxFloorValue = findViewById(R.id.tvMaxFloorValue);
        linearRelatedOrder = findViewById(R.id.linearRelatedOrder);
        tvRelatedOrderValue = findViewById(R.id.tvRelatedOrderValue);
        linearSubOrders = findViewById(R.id.linearSubOrders);
        rvSubOrders = findViewById(R.id.rvSubOrders);

        linearFilterType=findViewById(R.id.linearFilterType);
        linearFilterType.setVisibility(View.GONE);
        if(trade.getAdvancedOrderType()==0)
            linearAdvancedOrderType.setVisibility(View.GONE);
        else {
            linearAdvancedOrderType.setVisibility(View.VISIBLE);
            tvAdvancedOrderTypeValue.setText(trade.getAdvancedOrderType()+"");
        }


        if(trade.getMaxFloor()==0)
            linearMaxFloor.setVisibility(View.GONE);
        else {
            linearMaxFloor.setVisibility(View.VISIBLE);
            tvMaxFloorValue.setText(trade.getMaxFloor()+"");
        }

        if(trade.getArraySubOrders()==null || trade.getArraySubOrders().size()==0)
            linearSubOrders.setVisibility(View.GONE);
        else {
            linearSubOrders.setVisibility(View.VISIBLE);
            try {
                LinearLayoutManager llm = new LinearLayoutManager(TradeConfirmationActivity_old.this);
                adapterOrders = new OrdersPopupTrades(TradeConfirmationActivity_old.this, trade.getArraySubOrders(), this, this,false,0,false,false);
                rvSubOrders.setLayoutManager(llm);
                rvSubOrders.setAdapter(adapterOrders);
            } catch (Exception e) {
                Log.wtf("OrdersPopupTrades error", "error = " + e.getMessage());
            }
        }


        if(trade.getRelatedOrderId()==0)
           linearRelatedOrder.setVisibility(View.GONE);
        else {
            linearRelatedOrder.setVisibility(View.VISIBLE);
            tvRelatedOrderValue.setText(trade.getRelatedOrderId()+"");
        }




        try {

            String stockValue = MyApplication.lang == MyApplication.ARABIC ?
                    (stockQuotation.getSecurityId() + "-" + stockQuotation.getSymbolAr()) : (stockQuotation.getSecurityId() + "-" + stockQuotation.getSymbolEn());
            tvStockValue.setText(stockValue);
        }catch (Exception e){}
//getStockID()
        tvPriceValue.setText(Actions.formatNumber(trade.getPrice(), Actions.TwoDecimal));


        tvTradeTypeValue.setText(trade.getTradeTypeID() == MyApplication.ORDER_BUY ? getResources().getString(R.string.buy) : getResources().getString(R.string.sell));
        tvDateValue.setText(trade.getDate());
        try{
            if (MyApplication.lang == MyApplication.ARABIC)
                tvOrderTypeValue.setText(trade.getOrdertypeValueAr());
            else
                tvOrderTypeValue.setText(trade.getOrdertypeValueEn());
            //  tvOrderTypeValue.setText(Actions.getStringFromValue(trade.getOrderType()));
        }catch (Exception e){}
        if(trade.getOrderType()== MyApplication.MARKET_IF_TOUCHED || trade.getOrderType()==MyApplication.LIMIT_IF_TOUCHED) {
            // tvPriceTitle.setText(getString(R.string.trigger_price));
            llTriggerPrice.setVisibility(View.VISIBLE);
            tvTriggerPricevalue.setText(Actions.formatNumber(trade.getTriggerPrice(), Actions.TwoDecimal));
        }else {
            llTriggerPrice.setVisibility(View.GONE);
        }

        tvDurationTypeValue.setText(trade.getDurationType());
        tvCostValue.setText(Actions.formatNumber(trade.getOverallTotal(), Actions.ThreeDecimalThousandsSeparator));
        tvCommissionValue.setText(Actions.formatNumber(trade.getCommission(), Actions.ThreeDecimalThousandsSeparator));
        tvOverallValue.setText(Actions.formatNumber(trade.getCost(), Actions.ThreeDecimalThousandsSeparator));


        if (getIntent().getExtras().getBoolean("isUpdate")) {

            quantity = trade.getQuantity() + trade.getExecutedQuantity();
        } else {

            quantity = trade.getQuantity();
        }

        tvQuantityValue.setText(String.valueOf(quantity));
    }

    private void setListeners() {

        btConfirm.setOnClickListener(v -> {

            try {
                Actions.closeKeyboard(TradeConfirmationActivity_old.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                if (etConfirm.getText().toString().length() > 0) {
                    btConfirm.setClickable(false);

                    if (getIntent().getExtras().getBoolean("isUpdate")) {

                        new UpdateOrder().execute();
                    } else {

                        new AddOrder().execute();
                    }
                } else {

                    Animation shake = AnimationUtils.loadAnimation(TradeConfirmationActivity_old.this, R.anim.shake);
                    etConfirm.startAnimation(shake);
                }

            } else {
                btConfirm.setClickable(false);

                if (getIntent().getExtras().getBoolean("isUpdate")) {

                    new UpdateOrder().execute();
                } else {

                    new AddOrder().execute();
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


    private class AddOrder extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradeConfirmationActivity_old.this);
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
                        .key("ApplicationType").value(7)
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
                        .key("StockID").value(trade.getStockQuotation().getStockID()+"")
                        .key("TradingSession").value(Actions.getMarketSegmentParameter(trading_session))

                        .key("key").value(MyApplication.currentUser.getKey())
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
                    message = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    CreateDialogResponse(TradeConfirmationActivity_old.this, message);
          /*          finishAffinity();
                    Intent intent = new Intent(TradeConfirmationActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);*/

                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity_old.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(TradeConfirmationActivity_old.this, getResources().getString(R.string.error), false, false);
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
                MyApplication.showDialog(TradeConfirmationActivity_old.this);
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
                        .key("ApplicationType").value(7)
                        .key("Reference").value(trade.getReference())
                        .key("BrokerID").value(Actions.getBrokerEmployeeID())
                        .key("DurationID").value(trade.getDurationTypeId())

                        .key("GoodUntilDate").value(date) //iza na2a date be3abe, iza ma na2a mnb3ato fade

                        .key("Price").value(trade.getOrderType() == 1 ? 0.0 : trade.getPrice())
                        .key("TriggerPrice").value(trade.getOrderType() == 1 ? 0.0 : trade.getTriggerPrice())
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
                    message = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    CreateDialogResponse(TradeConfirmationActivity_old.this, message);


                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? object.getString("MessageEn") : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity_old.this, error, false, false);
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



    public void CreateDialogResponse(final Activity c, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setCancelable(false)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    finishAffinity();
                    Intent intent = new Intent(TradeConfirmationActivity_old.this, OrdersActivity.class);
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
