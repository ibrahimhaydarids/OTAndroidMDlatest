package com.ids.fixot.activities;




import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.StockAlertsAdapter;

import com.ids.fixot.adapters.StockAlertsLookupAdapter;
import com.ids.fixot.adapters.stockQuotationPopupAdapter;
import com.ids.fixot.model.Lookups;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Amal on 4/6/2017.
 */

public class AddStockAlertActivity extends AppCompatActivity implements MarketStatusListener, stockQuotationPopupAdapter.RecyclerViewOnItemClickListener {

    RelativeLayout rlLayout;

    private BroadcastReceiver receiver;
    LinearLayoutManager llm;
    StockAlertsAdapter adapter;
    Spinner spAlertTypes;
    Spinner spOperatorTypes;
    TextView tvStock;
    Spinner spStock;
    EditText etValue;
    private Dialog stocksDialog;
    private Button btClear;
    private StockAlertsLookupAdapter adapterLookupTypes;
    private StockAlertsLookupAdapter adapterlookupOperator;
    private ArrayList<Lookups> arrayUserAlerts;
    private ArrayList<Lookups> arrayAdapterAlerts;
    private int selectedtypeId=0;
    private int selectedOperatorId=0;
    private int selectedStockId=101;
    private String selectedValue="";
    AlertDialog.Builder builder;
    Button btConfirm;
    LinearLayout loading;
    RecyclerView rvStocks;
    EditText etSearchStock;
    stockQuotationPopupAdapter adapterStockPopup;
    Spinner spInstrumentsTop;
    //  GetUserNotificationSettings mGetUserNotification;

    SaveStockAlert mSaveStockAlert;
    public AddStockAlertActivity() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_add_stock_alert);
        Actions.initializeBugsTracking(this);

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.settings), AddStockAlertActivity.this);
        Actions.overrideFonts(this, rlLayout, false);
        Actions.showHideFooter(this);


        MyApplication.editor.putInt("lang", MyApplication.lang).apply();

        setAlertOperators();
        setAlertTypes();

        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if(BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
            spInstrumentsTop.setVisibility(View.GONE);
            //  Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }

       // mGetStockAlerts = new GetStockAlerts();
       // mGetStockAlerts.executeOnExecutor(MyApplication.threadPoolExecutor);
    }

    private void findViews() {
        rlLayout = findViewById(R.id.rlLayout);
        loading=findViewById(R.id.loading);
        btConfirm=findViewById(R.id.btConfirm);
        etValue=findViewById(R.id.etValue);
        tvStock=findViewById(R.id.tvStock);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //   Toast.makeText(getApplicationContext(),"asdasdas",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
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
    }

    private void setListeners() {

        btConfirm.setOnClickListener(v -> {
            if(tvStock.getText().toString().matches(getString(R.string.select_stock)))
                Actions.CreateDialog(this,getString(R.string.dialog_choose_stock),false,false);
            else if(selectedOperatorId==0){
                Actions.CreateDialog(this,getString(R.string.dialog_choose_operator),false,false);
            }else if(selectedtypeId==0)
                Actions.CreateDialog(this,getString(R.string.dialog_choose_type),false,false);
            else if(etValue.getText().toString().isEmpty())
                Actions.CreateDialog(this,getString(R.string.dialog_choose_value),false,false);

            else {
                mSaveStockAlert = new SaveStockAlert();
                mSaveStockAlert.executeOnExecutor(MyApplication.threadPoolExecutor);
            }

        });

        tvStock.setText(getText(R.string.select_stock));

        tvStock.setOnClickListener(v->{
            showStocksDialog();
        });



        LocalBroadcastManager.getInstance(AddStockAlertActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                       try{ adapterStockPopup.notifyDataSetChanged();}catch (Exception e){}
                    }
                }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
        );

    }


    public void back(View v) {

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Actions.startStockQuotationService(this);
        Actions.checkSession(this);
        Actions.InitializeSessionServiceV2(this);

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

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    public void onItemClicked(View v, int position) {
      try {
          stocksDialog.dismiss();
          tvStock.setText(adapterStockPopup.getFilteredItems().get(position).getStockID()+"-"+(MyApplication.lang == MyApplication.ARABIC ?adapterStockPopup.getFilteredItems().get(position).getSymbolAr():adapterStockPopup.getFilteredItems().get(position).getSymbolEn()));
          selectedStockId = adapterStockPopup.getFilteredItems().get(position).getStockID();

      }catch (Exception e){}
    }


    private class GetStockAlerts extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            MyApplication.userStockAlerts.clear();
            String url = MyApplication.link + MyApplication.GetStockAlerts.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("userid", MyApplication.currentUser.getId()+"");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                MyApplication.userStockAlerts.addAll(GlobalFunctions.GetStockAlerts(result));
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

           // setStockAlerts();

        }
    }





    private void setAlertTypes(){
        spAlertTypes = findViewById(R.id.spAlertTypes);
        adapterLookupTypes = new StockAlertsLookupAdapter(this, MyApplication.allAlertTypes, true);
        spAlertTypes.setAdapter(adapterLookupTypes);
        spAlertTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedtypeId= MyApplication.allAlertTypes.get(position).getID();
                selectedValue= MyApplication.allAlertTypes.get(position).getValue()+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void setAlertOperators(){
        spOperatorTypes = findViewById(R.id.spOperatorTypes);
        adapterlookupOperator = new StockAlertsLookupAdapter(this, MyApplication.allAlertOperators, true);
        spOperatorTypes.setAdapter(adapterlookupOperator);
        spOperatorTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOperatorId= MyApplication.allAlertOperators.get(position).getID();
                selectedValue= MyApplication.allAlertOperators.get(position).getValue()+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private class SaveStockAlert extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.SaveStockAlert.getValue(); // this method uses key after login
            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ID").value(0)
                        .key("OperatorID").value(selectedOperatorId)
                        .key("StockID").value(selectedStockId)
                        .key("TypeID").value(selectedtypeId)
                        .key("UserID").value(MyApplication.currentUser.getId())
                        .key("Value").value(etValue.getText().toString())
                        .key("key").value(MyApplication.mshared.getString(getString(R.string.afterkey), ""))
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.SaveUserNotificationSettings.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            try {
                result = ConnectionRequests.POSTWCF(url, stringer);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.setVisibility(View.GONE);
            try{Log.wtf("result_save",result);}catch (Exception e){}

            JSONObject object = null;
            try {
                object = new JSONObject(result);
                String success = object.getString("MessageEn");
                if (success.equals("Success")) {
                    finish();
                } else {
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? success : object.getString("MessageAr");
                    Actions.CreateDialog(AddStockAlertActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(AddStockAlertActivity.this, getResources().getString(R.string.error), false, false);
            }


        }
    }








    private void showStocksDialog(){

        stocksDialog=new Dialog(this);
        stocksDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        stocksDialog.setContentView(R.layout.popup_stocks);
        stocksDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        rvStocks = stocksDialog.findViewById(R.id.rvStocks);
        btClear = stocksDialog.findViewById(R.id.btClear);
        etSearchStock = stocksDialog.findViewById(R.id.etSearch);
        adapterStockPopup=new stockQuotationPopupAdapter(this, MyApplication.stockQuotations, AddStockAlertActivity.this);
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
                        Actions.closeKeyboard(AddStockAlertActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }

                    adapterStockPopup = new stockQuotationPopupAdapter(AddStockAlertActivity.this, MyApplication.stockQuotations, AddStockAlertActivity.this::onItemClicked);
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





}
