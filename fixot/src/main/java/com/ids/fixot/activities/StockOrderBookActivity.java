package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import me.grantland.widget.AutofitHelper;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ids.fixot.adapters.OrderBookRecyclerAdapter;
import com.ids.fixot.fragments.OrderBookByOrderFragment;
import com.ids.fixot.fragments.OrderBookByOrderNewFragment;
import com.ids.fixot.fragments.OrderBookByPriceNew;
import com.ids.fixot.fragments.OrderBookFragment;
import com.ids.fixot.interfaces.spItemListener;
import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;
import org.json.JSONObject;



public class StockOrderBookActivity extends AppCompatActivity implements OrderBookRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener , spItemListener {

    FragmentManager fragmentManager;
    LinearLayoutManager llm;
    LinearLayout rootLayout;
    int stockId = 0, ii = 0;
    StockQuotation stock = new StockQuotation();
    boolean isFavorite = false ;
    ImageView ivFavorite;
    TextView tvStockName;
    private BroadcastReceiver receiver;
    private Button btOrderPrice,btOrderByOrder;
    private TextView tvlast;
    public boolean showLoading=true;
    private boolean started = false;
    Spinner spInstrumentsTop;
    LinearLayout linearTopLast;

    LinearLayout linearValues,llVolume,llLast,llHigh,llLow,llEquilibrium;
    LinearLayout llTradesCount;
    TextView tvVolumeHeader,tvVolumeValue,tvTradesCountValue,tvLastTitle,tvLastValue,tvHighTitle,tvHighValue,tvLowHeader,tvLowValue,tvEquilibriumValue,tvEquilibriumHeader;




    public StockOrderBookActivity() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_stock_order_book);
        Actions.initializeBugsTracking(this);


        findViews();

        if (getIntent().hasExtra("stockId")) {
            stockId = getIntent().getExtras().getInt("stockId");
            String barTitle = getString(R.string.order_book) + "-" + getIntent().getExtras().getString("stockName");
            Actions.initializeToolBar(barTitle, StockOrderBookActivity.this);
            //  tvStockTitle.setVisibility(View.GONE);

            stock = Actions.getStockQuotationById(MyApplication.stockQuotations, getIntent().getExtras().getInt("stockId"));
            stock.setStockID(getIntent().getExtras().getInt("stockId"));

            if (getIntent().getExtras().getString("isFavorite") != null) {
                isFavorite = getIntent().getExtras().getString("isFavorite").equals("1");
            }

            Log.wtf("getIntent().getExtras().getInt(\"isFavorite\")", "is " + getIntent().getExtras().getInt("isFavorite"));
            Log.wtf("getIntent().getExtras().getString(\"isFavorite\")", "is " + getIntent().getExtras().getString("isFavorite"));
            ivFavorite.setImageResource(isFavorite ? R.drawable.added_to_favorites : R.drawable.add_to_favorites);

            if(BuildConfig.Enable_Markets) {
              //  setStockName(getIntent().getExtras().getString("securityId") + " - " + getIntent().getExtras().getString("stockName")); //getInt("stockId")
                StockQuotation stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, stockId);
                setStockName(getIntent().getExtras().getString("securityId") + " - " +  (MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getSymbolEn())); //getInt("stockId")


            }else {
               StockQuotation stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, stockId);
                setStockName(getIntent().getExtras().getString("securityId") + " - " +  (MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getNameAr() : stockQuotation.getNameEn())); //getInt("stockId")

            }
             setlastPrice();



        } else {
            Actions.initializeToolBar(getString(R.string.order_book), StockOrderBookActivity.this);
        }

        Actions.showHideFooter(this);

        setListeners();

        Actions.overrideFonts(this, rootLayout, false);
        Actions.setTypeface(new TextView[]{  tvStockName},  MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        btOrderByOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoading=true;
                setOrderByOderActive();

                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stockId );



                OrderBookByOrderFragment orderBookByOrderFragment = new OrderBookByOrderFragment();
                orderBookByOrderFragment.setArguments(bundle);


                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                }


                if(BuildConfig.Enable_Markets) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.container, orderBookByOrderFragment, "OrderBookByOrderFragment")
                            .commit();
                }else {

                    OrderBookByOrderNewFragment orderBookByOrderFragmentNew = new OrderBookByOrderNewFragment();
                    orderBookByOrderFragmentNew.setArguments(bundle);
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.container, orderBookByOrderFragmentNew, "orderBookByOrderFragmentNew")
                            .commit();


                }

//                    getStockOrderBookByOrder = new GetStockOrderBookByOrder();
//                    getStockOrderBookByOrder.executeOnExecutor(MyApplication.threadPoolExecutor);

            }
        });


        btOrderPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stockId );

                OrderBookFragment oderBookFragment = new OrderBookFragment();
                oderBookFragment.setArguments(bundle);

                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                    .beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                }

                showLoading=true;
                setOrderPriceActive();

                if(BuildConfig.Enable_Markets) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.container, oderBookFragment, "OrderBookFragment")
                            .commit();
                }else {
                    OrderBookByPriceNew orderBookByPriceNew = new OrderBookByPriceNew();
                    orderBookByPriceNew.setArguments(bundle);
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.container, orderBookByPriceNew, "orderBookByPriceNew")
                            .commit();
                }

            }
        });


        Bundle bundle = new Bundle();
        bundle.putInt("stockId", stockId );

        if(BuildConfig.Enable_Markets) {
            OrderBookFragment oderBookFragment = new OrderBookFragment();
            oderBookFragment.setArguments(bundle);

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, oderBookFragment, "OrderBookFragment")
                    .commit();

        }else {
            OrderBookByPriceNew orderBookByPriceNew = new OrderBookByPriceNew();
            orderBookByPriceNew.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.container, orderBookByPriceNew, "OrderBookByOrderFragment")
                    .commit();
         }

        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
            spInstrumentsTop.setVisibility(View.GONE);
           // Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }
   }

    private void setStockName(String stockName) {
        tvStockName.setText(stockName);
        if (MyApplication.lang == MyApplication.ARABIC) {
            tvStockName.setTypeface(MyApplication.droidbold);
        } else {
            tvStockName.setTypeface(MyApplication.giloryBold);
        }
    }


    private void setlastPrice(){
        try {
         //   if(getIntent().getExtras().getDouble("last") ==0) {

                tvlast.setText(getLastFromId());

                tvLastValue.setText(getLastFromId());
           // if(!BuildConfig.Enable_Markets){
                if(Double.parseDouble(getLastFromId()) >0 ){
                    tvlast.setTextColor(getResources().getColor(R.color.green_color));
                    tvLastValue.setTextColor(getResources().getColor(R.color.green_color));
                }else  if(Double.parseDouble(getLastFromId()) <0){
                    tvlast.setTextColor(getResources().getColor(R.color.red_color));
                    tvLastValue.setTextColor(getResources().getColor(R.color.red_color));
                }else {
                    tvlast.setTextColor(getResources().getColor(R.color.orange));
                    tvLastValue.setTextColor(getResources().getColor(R.color.orange));
                }

                tvHighValue.setTextColor(getResources().getColor(R.color.green_color));
                tvLowValue.setTextColor(getResources().getColor(R.color.red_color));
         //   }


           /* }
            else {
                tvlast.setText(getIntent().getExtras().getDouble("last") + "");
                tvLastValue.setText(getIntent().getExtras().getDouble("last") + "");
            }*/
        }catch (Exception e){ }
        setTopDataFromId();

    /*    try {
            tvLowValue.setText(getIntent().getExtras().getDouble("low")+"");
        }catch (Exception e){ }

        try {
            tvHighValue.setText(getIntent().getExtras().getDouble("high")+"");
        }catch (Exception e){ }

        try {
            tvVolumeValue.setText(getIntent().getExtras().getDouble("volume")+"");
        }catch (Exception e){ }

        try {
            tvTradesCountValue.setText(getIntent().getExtras().getDouble("Trades")+"");
            if(getIntent().getExtras().getDouble("Trades")==0.0)
               llTradesCount.setVisibility(View.GONE);
            else
                llTradesCount.setVisibility(View.VISIBLE);
        }catch (Exception e){
            llTradesCount.setVisibility(View.GONE);
        }*/





    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        linearTopLast=findViewById(R.id.linearTopLast);

        if(!BuildConfig.Enable_Markets) {
            Button btBuy = findViewById(R.id.btBuy);
           // btBuy.setTextColor(getResources().getColor(R.color.blue_gig));

            btBuy.setBackgroundColor(ContextCompat.getColor(this,R.color.blue_gig ));


        }
        //  tvStockTitle = findViewById(R.id.market_time_value_textview);

        tvStockName = findViewById(R.id.stockName);
        ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> new StockOrderBookActivity.AddRemoveFavoriteStock().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

        btOrderByOrder=(Button)findViewById(R.id.btOrderBookOrder);
        btOrderPrice=(Button)findViewById(R.id.btOrderBookPrice);
        tvlast=(TextView)findViewById(R.id.tvlast);

        linearValues = findViewById(R.id.linearValues);
        llVolume = findViewById(R.id.llVolume);
        llEquilibrium=findViewById(R.id.llEquilibrium);
        //if(!BuildConfig.Enable_Markets)
            llEquilibrium.setVisibility(View.VISIBLE);
        llLast = findViewById(R.id.llLast);
        llHigh = findViewById(R.id.llHigh);
        llLow = findViewById(R.id.llLow);
        tvVolumeHeader = findViewById(R.id.tvVolumeHeader);
        tvVolumeValue = findViewById(R.id.tvVolumeValue);
        tvTradesCountValue = findViewById(R.id.tvTradesCountValue);
        tvLastTitle = findViewById(R.id.tvLastTitle);
        tvLastValue = findViewById(R.id.tvLastValue);
        tvHighTitle = findViewById(R.id.tvHighTitle);
        tvHighValue = findViewById(R.id.tvHighValue);
        tvLowHeader = findViewById(R.id.tvLowHeader);
        tvLowValue = findViewById(R.id.tvLowValue);

        tvEquilibriumHeader = findViewById(R.id.tvEquilibriumHeader);
        tvEquilibriumValue = findViewById(R.id.tvEquilibriumValue);

        llTradesCount= findViewById(R.id.llTradesCount);





    /*    if(BuildConfig.Enable_Markets){
            linearTopLast.setVisibility(View.VISIBLE);
            linearValues.setVisibility(View.GONE);
        }else {*/
            linearValues.setVisibility(View.VISIBLE);
            linearTopLast.setVisibility(View.GONE);
      //  }


    }

    private String getLastFromId(){
        String last="";
        try {
            for (int i=0;i< MyApplication.stockQuotations.size();i++){
                if(MyApplication.stockQuotations.get(i).getStockID()==stockId) {
                    last = MyApplication.stockQuotations.get(i).getLast() + "";
                    return last;

                }

            }
            return last;
        }catch (Exception e){
            return "";
        }

    }


    private void setTopDataFromId(){
        String last="";
        try {
            for (int i=0;i< MyApplication.stockQuotations.size();i++){
                if(MyApplication.stockQuotations.get(i).getStockID()==stockId) {
                    last = MyApplication.stockQuotations.get(i).getLast() + "";

                    AutofitHelper.create(tvLowValue);
                    AutofitHelper.create(tvLastValue);
                    AutofitHelper.create(tvHighValue);
                    AutofitHelper.create(tvEquilibriumValue);
                    AutofitHelper.create(tvVolumeValue);
                    AutofitHelper.create(tvTradesCountValue);





                    try { tvLowValue.setText(MyApplication.stockQuotations.get(i).getLow()+""); }catch (Exception e){ }

                    try {tvHighValue.setText(MyApplication.stockQuotations.get(i).getHigh()+""); }catch (Exception e){ }
                    try {tvEquilibriumValue.setText(MyApplication.stockQuotations.get(i).getEquilibriumPriceFormatted()+""); }catch (Exception e){ }

                    try {
                       // if(!BuildConfig.Enable_Markets)
                           tvVolumeValue.setText(MyApplication.stockQuotations.get(i).getVolumeFormatted()+"");
                 /*   else
                        tvVolumeValue.setText(MyApplication.stockQuotations.get(i).getVolume()+"");*/
                    }catch (Exception e){ }



                    try { tvTradesCountValue.setText(MyApplication.stockQuotations.get(i).getTrade()+"");
                        if(MyApplication.stockQuotations.get(i).getTrade()==0)
                            llTradesCount.setVisibility(View.GONE);
                        else
                            llTradesCount.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        llTradesCount.setVisibility(View.GONE);
                    }

                }

            }

        }catch (Exception e){

        }

    }

    private void setOrderPriceActive(){
        btOrderPrice.setBackgroundResource(R.drawable.order_book_border_active);
      //  btOrderPrice.setTextColor(getResources().getColor(R.color.colorDark));

        btOrderByOrder.setBackgroundResource(R.drawable.order_book_border_disable);
       // btOrderByOrder.setTextColor(getResources().getColor(R.color.colorValues));

        setListeners();
    }


    private void setOrderByOderActive(){
        btOrderByOrder.setBackgroundResource(R.drawable.order_book_border_active);
       // btOrderByOrder.setTextColor(getResources().getColor(R.color.colorDark));


        btOrderPrice.setBackgroundResource(R.drawable.order_book_border_disable);
      //  btOrderPrice.setTextColor(getResources().getColor(R.color.colorValues));

        setListeners();

    }

    public void goToTrade(View v) {

        switch (v.getId()) {

            case R.id.btSell:

                Bundle sellBundle = new Bundle();
                sellBundle.putParcelable("stockQuotation", stock);
                sellBundle.putInt("action", MyApplication.ORDER_SELL);
                Intent sellIntent = new Intent(StockOrderBookActivity.this, TradesActivity.class);
                sellIntent.putExtras(sellBundle);
                StockOrderBookActivity.this.startActivity(sellIntent);
                break;

            case R.id.btBuy:

                Bundle buyBundle = new Bundle();
                buyBundle.putParcelable("stockQuotation", stock);
                buyBundle.putInt("action", MyApplication.ORDER_BUY);
                Intent buyIntent = new Intent(StockOrderBookActivity.this, TradesActivity.class);
                buyIntent.putExtras(buyBundle);
                StockOrderBookActivity.this.startActivity(buyIntent);
                break;
        }

    }

    private void setListeners() {

//        llm = new LinearLayoutManager(StockOrderBookActivity.this);
//        adapter = new StockOrderBookRecyclerAdapter(StockOrderBookActivity.this, allOrders,isOrderByPrice);

        LocalBroadcastManager.getInstance(StockOrderBookActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                          setlastPrice();
                         } catch (Exception e) {
                        }
                    }
                }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
        );

    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    public void back(View v) {

        finish();
    }

    public void close(View v) {
        this.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Actions.startStockQuotationService(this);
        Actions.checkSession(this);
        Actions.checkLanguage(this, started);
        Actions.InitializeSessionServiceV2(this);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClicked(View v, int position) {

    }

    private class AddRemoveFavoriteStock extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(StockOrderBookActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddFavoriteStocks.getValue();

            if (isFavorite) {
                url = MyApplication.link + MyApplication.RemoveFavoriteStocks.getValue();
            }

            String stringer = "{\"StockIDs\":[\"" + stock.getStockID() + "\"],\"UserID\":" + MyApplication.currentUser.getId()
                    + ",\"key\":\"" + getString(R.string.beforekey) + "\"}";

            result = ConnectionRequests.POSTWCF2(url, stringer);

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
                String success = object.getString("MessageEn");
                if (success.equals("Success")) {

                    if (!isFavorite) {
                        Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.save_success), false, false);
                        ivFavorite.setImageResource(R.drawable.added_to_favorites);
                        isFavorite = true;
                    } else {
                        Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.delete_success), false, false);
                        ivFavorite.setImageResource(R.drawable.add_to_favorites);
                        isFavorite = false;
                    }
                } else {
                    Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.error), false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();


                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isFavorite) {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.RemoveFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onStop() {
        try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Actions.unregisterSessionReceiver(this);
        try{
            Actions.stopStockQuotationService(this);
            Log.wtf("quotation_service","destroy_stop ");
        }catch (Exception e){
            Log.wtf("quotation_service","destroy_exception "+e.toString());
        }
        super.onDestroy();
    }
}
