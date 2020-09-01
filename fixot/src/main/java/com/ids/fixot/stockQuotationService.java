package com.ids.fixot;



import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ids.fixot.activities.AddStockAlertActivity;
import com.ids.fixot.activities.FavoritesActivity;
import com.ids.fixot.activities.MarketIndexActivity;
import com.ids.fixot.activities.StockActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ids.fixot.MyApplication.appServiceFirstTime;
import static com.ids.fixot.MyApplication.context;


public class stockQuotationService extends Service {


    public static final String ACTION_STOCKS_SERVICE = AppService.class.getName() + "AppService";
    public static final String ACTION_MARKET_SERVICE = AppService.class.getName() + "MarketService";
    public static final String ACTION_SESSION_SERVICE = AppService.class.getName() + "SessiontService";
    public static String EXTRA_MARKET_STATUS = "marketstatus";
    public static String EXTRA_MARKET_TIME = "markettime";
    public static String EXTRA_SESSION = "session";
    public static String EXTRA_STOCK_QUOTATIONS_LIST = "stocksList";
    public static SimpleDateFormat marketDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat marketSetDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    Date date;
    boolean running = true;
    boolean isretreive=false;
    private Handler handler;
    private ArrayList<StockQuotation> changedStocks = new ArrayList<>();
    GetStockQuotation mGetStockQuotation;

    @Override
    public void onDestroy() {

        Log.wtf("AppService on", "destroy");

        try {

            stopForeground(true);
            stopSelf();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
                mGetStockQuotation.cancel(true);
                MyApplication.threadPoolExecutor.getQueue().remove(mGetStockQuotation);
                Log.wtf("quotation_service","stop_call");

        }catch (Exception e){
            Log.wtf("quotation_service","stop_call_exception "+e.toString());
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        if(MyApplication.currentUser.getId() == Actions.getLastUserId(getApplicationContext())) {
            if (Actions.getLastMarketId(getApplicationContext())!=0)
                MyApplication.marketID = Actions.getLastMarketId(getApplicationContext()) + "";
            else {
                if(BuildConfig.Enable_Markets) {
                    MyApplication.marketID = "2";
                    Actions.setLastMarketId(getApplicationContext(),2);
                }
                else {
                    MyApplication.marketID = "1";
                    Actions.setLastMarketId(getApplicationContext(),1);
                }
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyApplication.setWebserviceItem();
        Log.wtf("quotation_service","start");
        isretreive=true;
        mGetStockQuotation = new GetStockQuotation();
        mGetStockQuotation.executeOnExecutor(MyApplication.threadPoolExecutor);
        return START_STICKY;
    }

    private void sendBroadcastUpdatedList(ArrayList<StockQuotation> stockQuotationsList) {
        Log.wtf("quotation_service","broadcast");
        Log.wtf("quotation_service",MyApplication.stockQuotations.size()+"");

        Intent intent = new Intent(ACTION_STOCKS_SERVICE);
        intent.putParcelableArrayListExtra(EXTRA_STOCK_QUOTATIONS_LIST, stockQuotationsList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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



    private class GetStockQuotation extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            isretreive=true;
            sendBroadcastUpdatedList(MyApplication.stockQuotations);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";


            HashMap<String, String> parameters = new HashMap<String, String>();

            while (running && isretreive) {
                isretreive=false;
                Log.wtf("quotation_service","call");
                if (isCancelled())
                    break;


                MyApplication.setWebserviceItem();
                String url = MyApplication.link + MyApplication.GetStockQuotation.getValue();


                Log.wtf("bob","TStamp:  "+MyApplication.stockTimesTampMap.get(MyApplication.marketID));
                Log.wtf("bob","MarketId:  "+MyApplication.marketID);
                Log.wtf("bob","url:"+url);
                String marketid = "";
               /* if (appServiceFirstTime)
                    marketid = "0";
                else*/
                marketid = MyApplication.marketID;

                if(MyApplication.stockTimesTampMap.get(MyApplication.marketID)==null)
                    MyApplication.stockTimesTampMap.put(MyApplication.marketID,"0");

                try {

                    String timeStamp=MyApplication.stockTimesTampMap.get(MyApplication.marketID);
                    if(timeStamp==null) {
                        timeStamp = "0";
                    }

                    if(marketid.matches("0")) {

                        if(BuildConfig.Enable_Markets) {
                            marketid = "2";
                            MyApplication.marketID="2";
                            Actions.setLastMarketId(getApplicationContext(),2);
                            Log.wtf("bob","MarketIdChanged->2"); }
                        else {
                            marketid = "1";
                            MyApplication.marketID="1";
                            Actions.setLastMarketId(getApplicationContext(),1);
                            Log.wtf("bob","MarketIdChanged->4");}


                    }


                    parameters.clear();
                    parameters.put("InstrumentId", "");
                    parameters.put("stockIds", "");
                    parameters.put("TStamp",timeStamp /*MyApplication.stockTimesTamp*/);
                    parameters.put("key", getResources().getString(R.string.beforekey));
                    parameters.put("MarketId", marketid);
                    parameters.put("sectorId", "0");


                    result = ConnectionRequests.GET(url, getApplicationContext(), parameters);

                    if (appServiceFirstTime || MyApplication.stockQuotations.size() == 0) {
                        Log.wtf("bob_","1111");
                        MyApplication.stockQuotations.addAll(GlobalFunctions.GetStockQuotation(result,true));

                        if(appServiceFirstTime){
                        if (context instanceof StockActivity || context instanceof StockOrderBookActivity ||context instanceof AddStockAlertActivity || context instanceof FavoritesActivity) {
                            Log.wtf("bob_","continuuuueee");
                        }else {
                            stopSelf();
                        }}
                        appServiceFirstTime = false;
                    } else if (!MyApplication.stockTimesTampMap.get(MyApplication.marketID).equals("0")) {
                        try {
                            Log.wtf("bob_","2222");
                            changedStocks = GlobalFunctions.GetStockQuotation(result,true);
                        } catch (Exception e) {
                            Log.wtf("bob_","4444");
                            e.printStackTrace();
                        }
                    }

                    if (changedStocks.size() != 0 && !MyApplication.stockTimesTampMap.get(MyApplication.marketID).equals("0")) {
                        Log.wtf("bob_","5555");
                        replaceOldStocks(MyApplication.stockQuotations, changedStocks);
                    }
                } catch (Exception e) {
                    Log.wtf("bob_","6666"+e.toString());
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

            super.onPostExecute(result);
        }
    }






}
