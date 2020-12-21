package com.ids.fixot;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ids.fixot.MyApplication.appServiceFirstTime;
import static com.ids.fixot.MyApplication.count;


public class AppService extends Service {


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
    public boolean localFinish=true;
    boolean running = true;
    private Handler handler;
    private CountDownTimer localTimer;
    private int testCount=1;

    private ArrayList<StockQuotation> changedStocks = new ArrayList<>();

    @Override
    public void onDestroy() {
        try{localTimer.cancel();}catch (Exception e){}
        Log.wtf("AppService on", "destroy");

        try {


            stopForeground(true);
            stopSelf();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
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
            else
            {
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


        int delay = 1000; //milliseconds

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                try {
//                    Log.wtf("MyApplication.currentUser","getId : " + MyApplication.currentUser.getId());
                    if (MyApplication.currentUser.getId() != -1) {
                       // new GetRealTimeData().executeOnExecutor(MyApplication.threadPoolExecutor);
                        new CheckSessionWithServerTime().executeOnExecutor(MyApplication.threadPoolExecutor);
                        new GetMarketStatusMobile().executeOnExecutor(MyApplication.threadPoolExecutor);

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    MyApplication.threadPoolExecutor = null;
                    MyApplication.threadPoolExecutor = new ThreadPoolExecutor(MyApplication.corePoolSize, MyApplication.maximumPoolSize,
                            MyApplication.keepAliveTime, TimeUnit.SECONDS, MyApplication.workQueue);
                }


                handler.postDelayed(this, 2000);
            }
        }, delay);




        return START_STICKY;
    }

    private void setLocalTime(){
       localTimer= new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
               // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);

                final Calendar calendar = Calendar.getInstance();

                calendar.setTime(date);
                calendar.add(Calendar.SECOND, +1);
                date = calendar.getTime();

                if (MyApplication.marketStatus.getMarketTime().equals("")) {
                    sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), marketDateFormat.format(date));
                } else {
                    sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), "");
                }
                Log.wtf("time_update","local");
               localFinish=false;
            }

            public void onFinish() {
               localFinish=true;
            }

        }.start();
    }

    private void sendBroadcastMarket(String marketstatus, String markettime) {

        Intent intent = new Intent(ACTION_MARKET_SERVICE);
        intent.putExtra(EXTRA_MARKET_STATUS, marketstatus);
        intent.putExtra(EXTRA_MARKET_TIME, markettime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.wtf("sendBroadcastMarketStatus", "ACTION_MARKET_SERVICE");
    }


    private void sendBroadcastSession(boolean session) {
        Intent intent = new Intent(ACTION_SESSION_SERVICE);
        intent.putExtra(EXTRA_SESSION, session);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastUpdatedList(ArrayList<StockQuotation> stockQuotationsList) {

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



    private class CheckSessionWithServerTime extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.CheckSessionWithServerTime.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.currentUser.getId() + "");
            parameters.put("currentUserSessionID", MyApplication.currentUser.getSessionID() + "");
          // parameters.put("key", getResources().getString(R.string.beforekey));
            parameters.put("key",MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            parameters.put("MarketID", MyApplication.marketID);


            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                Log.wtf("CheckSessionWithServerTime", "result : " + result);

            } catch (IOException e) {
                e.printStackTrace();
                if (MyApplication.showBackgroundRequestToastError) {
                    try {
                        if (MyApplication.isDebug) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.CheckSessionWithServerTime.getKey(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception es) {
                        Log.wtf("Appservice ", "Error - " + MyApplication.CheckSessionWithServerTime.getKey() + " : " + es.getMessage());
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                MyApplication.marketServerTime = GlobalFunctions.GetMarketStatus(s);


                try {

                    String marketTime = MyApplication.marketServerTime.getServerTime();

                    sendBroadcastSession(MyApplication.marketServerTime.isSessionChanged() || MyApplication.marketServerTime.getMessageStatus()==2);

                    if(localFinish) {
                        try {

                            date = marketDateFormat.parse(marketTime);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (MyApplication.marketStatus.getMarketTime().equals("")) {
                            sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), marketDateFormat.format(date));
                        } else {
                            sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), "");
                        }

                        setLocalTime();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetMarketStatusMobile extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetMarketStatusMobile.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.currentUser.getId() + "");
            parameters.put("currentUserSessionID", MyApplication.currentUser.getSessionID() + "");
            parameters.put("key", getResources().getString(R.string.beforekey));
           // parameters.put("MarketID", MyApplication.marketID);


            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                Log.wtf("GetMarketStatusMobile", "result : " + result);

            } catch (IOException e) {
                e.printStackTrace();
                if (MyApplication.showBackgroundRequestToastError) {
                    try {
                        if (MyApplication.isDebug) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.CheckSessionWithServerTime.getKey(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception es) {
                        Log.wtf("Appservice ", "Error - " + MyApplication.GetMarketStatusMobile.getKey() + " : " + es.getMessage());
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                MyApplication.marketStatus = GlobalFunctions.GetMarketStatus(s);


                try {

        /*            String marketTime = MyApplication.marketStatus.getServerTime();



                    try {

                        date = marketDateFormat.parse(marketTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
/*        testCount++;
        Log.wtf("counter_test",count+"")
        if(testCount==10)
                    sendBroadcastSession(true);
        else*/

/*                    if (MyApplication.marketStatus.getMarketTime().equals("")) {
                        sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), marketDateFormat.format(date));
                    } else {
                        sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), "");
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
