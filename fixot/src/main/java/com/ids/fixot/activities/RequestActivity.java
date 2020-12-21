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
import com.ids.fixot.adapters.PortoflioStockForwardAdapter;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.adapters.ValuesGridRecyclerAdapter;
import com.ids.fixot.interfaces.spItemListener;
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

public class RequestActivity extends AppCompatActivity implements PortoflioStockForwardAdapter.RecyclerViewOnItemClickListener, MarketStatusListener , spItemListener {

    Toolbar myToolbar;
    ImageView ivBack, ivPortfolio;
    TextView tvToolbarTitle, tvToolbarStatus, tvUserName, tvPortfolioNumber, tvLogout,tvToDate;
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    //SwipeRefreshLayout swipeContainer;
    ScrollView scrollView;
    EditText etAmount;
    ImageView btReloadAmount;
    LinearLayout loading;
    Button btSubmit;
    Double initialAvailableAmount=0.0;
    private String selectedDate="";


    RelativeLayout rlUserHeader, rootLayout;
    GetAvailableForWithdrawal getAvailableForWithdrawal = new GetAvailableForWithdrawal();

    SaveChequeRequest saveChequeRequest = new SaveChequeRequest();

    LinearLayout llTotalStocks;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;



    private BroadcastReceiver receiver;
    private boolean started = false, showProgress = false;
    Spinner spInstrumentsTop;
    public RequestActivity() {
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

        try{subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);}catch (Exception e){}
        try{ spSubAccounts.setSelection(Actions.getDefaultSubPosition(),false);}catch (Exception e){}


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getAvailableForWithdrawal = new GetAvailableForWithdrawal();
                getAvailableForWithdrawal.executeOnExecutor(MyApplication.threadPoolExecutor);
            }
        }, 200);


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_requests_old);
        Actions.initializeBugsTracking(this);

        findViews();
        getAvailableForWithdrawal = new GetAvailableForWithdrawal();
        getAvailableForWithdrawal.executeOnExecutor(MyApplication.threadPoolExecutor);
        Log.wtf("portfolio_id",MyApplication.selectedSubAccount.getPortfolioId()+"");
        Log.wtf("user_id",    MyApplication.selectedSubAccount.getUserId() + "");
        Log.wtf("after_key",    MyApplication.mshared.getString(getString(R.string.afterkey), "") + "");
              //  .key("PortfolioID").value(MyApplication.selectedSubAccount.getPortfolioId())
        Actions.showHideFooter(this);
        started = true;

        tvUserName.setText(MyApplication.lang == MyApplication.ARABIC ? MyApplication.currentUser.getNameAr() : MyApplication.currentUser.getNameEn());
        tvPortfolioNumber.setText(String.valueOf(MyApplication.currentUser.getPortfolioNumber()));

        tvPortfolioNumber.setText(String.valueOf(MyApplication.currentUser.getPortfolioNumber()));

        try {
            tvToolbarStatus.setText(MyApplication.marketStatus.getStatusName());
        } catch (Exception e) {
            e.printStackTrace();
        }




        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

        Actions.initializeToolBar(getString(R.string.portfolio), RequestActivity.this);

        Actions.overrideFonts(this, rootLayout, false);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

        if (MyApplication.lang == MyApplication.ARABIC) {

            tvUserName.setText(MyApplication.currentUser.getNameAr());
            tvUserName.setTypeface(MyApplication.droidbold);
            tvPortfolioNumber.setTypeface(MyApplication.droidbold);
        } else {

            tvUserName.setText(MyApplication.currentUser.getNameEn());
            tvUserName.setTypeface(MyApplication.giloryBold);
            tvPortfolioNumber.setTypeface(MyApplication.giloryBold);
        }

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

    private void findViews() {

        spSubAccounts = findViewById(R.id.spSubAccounts);
        ImageView ivUserSubAccount=findViewById(R.id.ivUserSubAccount);
        if(BuildConfig.Enable_Markets)
            ivUserSubAccount.setVisibility(View.GONE);
        else
            ivUserSubAccount.setVisibility(View.VISIBLE);
        llTotalStocks = findViewById(R.id.llTotalStocks);


        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);



        rlUserHeader = findViewById(R.id.rlUserHeader);

        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvUserName = rlUserHeader.findViewById(R.id.tvUserName);
        tvPortfolioNumber = rlUserHeader.findViewById(R.id.tvPortfolioNumber);
        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
        tvToolbarStatus = findViewById(R.id.toolbar_status);

        ivBack = findViewById(R.id.ivBack);

        scrollView = findViewById(R.id.scrollView);
        loading=findViewById(R.id.loading);

        tvToDate=findViewById(R.id.tvToDate);
        tvToDate.setOnClickListener(v -> new DatePickerDialog(RequestActivity.this, date, myCalendar
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






         etAmount=findViewById(R.id.etAmount);
         btReloadAmount=findViewById(R.id.btReloadAmount);
         btSubmit=findViewById(R.id.btSubmit);


        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(RequestActivity.this));
        tvLogout.setVisibility(View.GONE);




        try {
            subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, Actions.getfilteredSubAccount()/*MyApplication.currentUser.getSubAccounts()*/);
            spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
            spSubAccounts.setSelection(returnAccountIndex());
            spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);
                    getAvailableForWithdrawal = new GetAvailableForWithdrawal();
                    getAvailableForWithdrawal.executeOnExecutor(MyApplication.threadPoolExecutor);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){}


    }


    private void updateLabel(TextView editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        editText.setText(sdf.format(myCalendar.getTime()));
        selectedDate=sdf.format(myCalendar.getTime());
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




/*    private class GetPortfolio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            allValueItems.clear();
            adapter.notifyDataSetChanged();
            arrayOfStocks.clear();

            StockSummary fakeStock = new StockSummary();
            fakeStock.setId(-1);
            arrayOfStocks.add(fakeStock);

//            StockSummary stockss = new StockSummary();
//            stockss.setUnrealized("-45");
//            stockss.setUnrealizedPercent("+45");
//            stockss.setAvailableShares("+10");
//            stockss.setBid("+55");
//            stockss.setLast("-55");
//            arrayOfStocks.add(stockss);//header
            portoflioStockForwardAdapter.notifyDataSetChanged();

            if (showProgress)
                MyApplication.showDialog(RequestActivity.this);

        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetPortfolio.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("Lang", Actions.getLanguage());
            parameters.put("MarketID", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, RequestActivity.this, parameters);
                Log.wtf("porfolio", "result = " + result);
                MyApplication.portfolio = GlobalFunctions.GetPortfolio(result);
            } catch (IOException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetPortfolio.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetPortfolio.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            MyApplication.dismiss();

            allValueItems.clear();
            allValueItems.addAll(MyApplication.portfolio.getCashSummary().getAllvalueItems());
            adapter.notifyDataSetChanged();


            rvUserData.setLayoutManager(llUserData);
            if (allValueItems.size() % 2 != 0) {

                llUserData.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {

                        //Log.wtf("position" , "is " + position);

                        if (position == allValueItems.size() - 1)
                            return MyApplication.GRID_VALUES_SPAN_COUNT;
                        else return 1;
                    }
                });
            } else {


                llUserData.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {

                        //Log.wtf("position" , "is " + position);

                        if (position == allValueItems.size() - 1) return 1;
                        else return 1;
                    }
                });
            }

            initializePortfolioStockList();
        }
    }*/





    private class GetAvailableForWithdrawal extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetAvailableForWithdrawal.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("UserId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("PortfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            JSONObject object = null;
            try {

                 object = new JSONObject(result);


                String msgdata = object.getString("ResponseMessage");
                JSONObject jsondata_msg = new JSONObject(msgdata);
                Boolean success = jsondata_msg.getBoolean("IsSuccess");
                try{
                initialAvailableAmount= Double.parseDouble(object.getString("Value"));
                etAmount.setText(initialAvailableAmount.toString());
                btReloadAmount.setOnClickListener(v->{
                    etAmount.setText(initialAvailableAmount.toString());
                    etAmount.setSelection(etAmount.getText().length());
                });
                btSubmit.setOnClickListener(v->{
                    checkBeforeSending();
                });
                }catch (Exception e){}



            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(RequestActivity.this, getResources().getString(R.string.error), false, false);
            }
           // setStockAlerts();

        }
    }



    private void checkBeforeSending(){
        if(etAmount.getText()==null){
            Actions.CreateDialog(this,getString(R.string.plz_add_amount),false,false);
        }else if(selectedDate.isEmpty()){
            Actions.CreateDialog(this,getString(R.string.plz_add_date),false,false);
        }else {
            saveChequeRequest = new SaveChequeRequest();
            saveChequeRequest.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
    }






    private class SaveChequeRequest extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.SaveChequeRequest.getValue(); // this method uses key after login
            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("UserID").value(MyApplication.selectedSubAccount.getUserId() + "")
                        .key("PortfolioID").value( MyApplication.selectedSubAccount.getPortfolioId() + "")
                        .key("Amount").value(etAmount.getText().toString())
                        .key("ExpiryDate").value(tvToDate.getText().toString())
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
                String message="";
                object = new JSONObject(result);
                Boolean success = object.getBoolean("IsSuccess");
                message = MyApplication.lang == MyApplication.ARABIC?object.getString("MessageAr"):object.getString("MessageEn");

                if (success) {
                    CreateDialog(RequestActivity.this,message);
                } else {
                    String error;
                    Actions.CreateDialog(RequestActivity.this, message, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(RequestActivity.this, getResources().getString(R.string.error), false, false);
            }


        }
    }





    public static void CreateDialog(final Activity c, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    dialog.cancel();
                    //Actions.hideKeyboard(c);
                   // c.startActivity(new Intent(c.getApplicationContext(),LoginFingerPrintActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    c.finish();


                });

        AlertDialog alert = builder.create();
        alert.show();

    }

}
