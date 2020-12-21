package com.ids.fixot.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.interfaces.spItemListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.ids.fixot.MyApplication.lang;

public class MoreActivity extends AppCompatActivity implements MarketStatusListener , spItemListener {

    ImageView ivBack;
    Toolbar myToolbar;
    TextView tvLogout,menu_register_title;
    RelativeLayout rootLayout, rlMowazi, rlSectors, rlTops, rlNews, rlLinks, rlChangePassword, rlSettings,rlRegister,rlBonds,rlNotification,rlStockAlerts,rlOffMarketQuotes;

    private LinearLayout center_part;
    private BroadcastReceiver receiver;
    private boolean started = false;
    Spinner spInstrumentsTop;
    androidx.appcompat.app.AlertDialog.Builder builder;
    androidx.appcompat.app.AlertDialog dialog;
    public MoreActivity() {
        LocalUtils.updateConfig(this);
    }


    @Override
    public void onItemSelectedListener(AdapterView<?> parent, View v, int p, long id) {
        // Toast.makeText(getApplicationContext(),p+"aaaaa",Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_more);
        Actions.initializeBugsTracking(this);

        Actions.showHideFooter(this);
        findViews();

        started = true;

        Actions.initializeToolBar(getString(R.string.menu_more), MoreActivity.this);

        Actions.overrideFonts(this, rootLayout, false);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Actions.unregisterSessionReceiver(this);
    }

    public void back(View v) {
        finish();
    }

    public void goTo(View v) {
        Actions.goTo(this, v);
    }


    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);
        ivBack = myToolbar.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);

        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(MoreActivity.this));

        rlMowazi = findViewById(R.id.rlMowazi);
        rlMowazi.setVisibility(MyApplication.showMowazi ? View.VISIBLE : View.GONE);
        rlOffMarketQuotes= findViewById(R.id.rlOffMarketQuotes);
        rlSectors = findViewById(R.id.rlSectors);
        rlTops = findViewById(R.id.rlTops);
        rlNews = findViewById(R.id.rlNews);
        rlLinks = findViewById(R.id.rlLinks);
        rlChangePassword = findViewById(R.id.rlChangePassword);
        rlChangePassword.setVisibility(View.GONE);
        rlSettings = findViewById(R.id.rlSettings);
        rlRegister = findViewById(R.id.rlRegister);
        rlNotification = findViewById(R.id.rlNotification);
        rlStockAlerts = findViewById(R.id.rlStockAlerts);

        center_part   = findViewById(R.id.center_part);
        rlBonds = findViewById(R.id.rlBonds);
        menu_register_title = findViewById(R.id.menu_register_title);

        if(MyApplication.currentUser.getStatus()==52) {
            rlRegister.setVisibility(View.VISIBLE);
            if(MyApplication.currentUser.getClientID()!=0)
                menu_register_title.setText(getString(R.string.updateOnAlMowazi));
            else
                menu_register_title.setText(getString(R.string.registerOnAlMowazi));

        }
        else
            rlRegister.setVisibility(View.GONE);

       // if(BuildConfig.Enable_Markets)
            rlBonds.setVisibility(View.GONE);
       // else
          //  rlBonds.setVisibility(View.VISIBLE);

        rlNotification.setVisibility(MyApplication.mshared.getBoolean("EnableNotification", false) ? View.VISIBLE : View.GONE);
      //  rlNotification.setVisibility(View.GONE);

        rlStockAlerts.setVisibility(MyApplication.mshared.getBoolean("EnableNotification", false) ? View.VISIBLE : View.GONE);

        if(BuildConfig.Enable_Markets)
            rlOffMarketQuotes.setVisibility(View.VISIBLE);
        else
            rlOffMarketQuotes.setVisibility(View.GONE);

        setBackgrounds();

/*        if (MyApplication.marketID.matches(Integer.toString(enums.MarketType.KWOTC.getValue()))) {
            rlSectors.setVisibility(View.GONE);

            rlTops.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            rlNews.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));
            rlLinks.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            rlChangePassword.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));
            rlSettings.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        }*/
    }


    private void setBackgrounds(){

        final int childCount = center_part.getChildCount();
        int count=0;
        for (int i = 0; i < childCount; i++) {
            View v = center_part.getChildAt(i);
            if(v.getVisibility()==View.VISIBLE){

                TypedValue outValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.colorLight, outValue, true);
                if(count % 2==1)
                   v.setBackgroundResource(outValue.resourceId);
                else
                    v.setBackgroundResource(0);


                count++;
            }
        }
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();
     Actions.checkSession(this);

        Actions.checkLanguage(this, started);

        //Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        try{Actions.setSpinnerTop(this, spInstrumentsTop, this);}catch (Exception e){}

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

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//    }

    @Override
    public void onBackPressed() {
        Actions.exitApp(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 101) {
            if(resultCode == Activity.RESULT_OK){
                // String result=data.getStringExtra("result");
                showRegisterViaMowaziDialog();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                MyApplication.isFromLogin=false;
                Toast.makeText(MoreActivity.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();
/*                Intent  i = new Intent(MoreActivity.this, MarketIndexActivity.class);
                startActivity(i);*/
                finish();
            }
        }
    }


    public void showRegisterViaMowaziDialog() {



        builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialogRegisterViaMowazi = inflater.inflate(R.layout.popup_register_via_mowazi, null);

        TextView tv_register = editDialogRegisterViaMowazi.findViewById(R.id.tv_register);
        EditText etTradingNumber = editDialogRegisterViaMowazi.findViewById(R.id.etTradingNumber);
        EditText etEmail = editDialogRegisterViaMowazi.findViewById(R.id.etEmail);
        EditText etFirstName = editDialogRegisterViaMowazi.findViewById(R.id.etFirstName);
        EditText etLastName = editDialogRegisterViaMowazi.findViewById(R.id.etLastName);
        EditText etMobileNumber = editDialogRegisterViaMowazi.findViewById(R.id.etMobileNumber);
        Button btSubmit = editDialogRegisterViaMowazi.findViewById(R.id.btSubmit);
        Button btClose = editDialogRegisterViaMowazi.findViewById(R.id.btClose);

        if(MyApplication.currentUser.getInvestorId()!=0 && MyApplication.isFromLogin){
            etTradingNumber.setText(MyApplication.currentUser.getInvestorId()+" ");
            etTradingNumber.setEnabled(false);
        }



    /*    CheckBox cbTerms = editDialogRegisterViaMowazi.findViewById(R.id.cbTerms);
         TextView tv_view_terms = editDialogRegisterViaMowazi.findViewById(R.id.tv_view_terms);

       tv_view_terms.setOnClickListener(v -> {
                    String url = MyApplication.parameter.getAlmowaziPolicyLink();
                    startActivity(new Intent(MoreActivity.this, PdfDisplayActivity.class).putExtra("url", url));
                }
        );*/

        if (MyApplication.lang == MyApplication.ARABIC) {
            tv_register.setTypeface(MyApplication.droidbold);
            etTradingNumber.setTypeface(MyApplication.droidregular);
            etEmail.setTypeface(MyApplication.droidregular);
            etFirstName.setTypeface(MyApplication.droidregular);
            etLastName.setTypeface(MyApplication.droidregular);
            etMobileNumber.setTypeface(MyApplication.droidregular);
            btSubmit.setTypeface(MyApplication.droidbold);
            btClose.setTypeface(MyApplication.droidregular);

            etTradingNumber.setGravity(Gravity.RIGHT);
            etEmail.setGravity(Gravity.RIGHT);
            etFirstName.setGravity(Gravity.RIGHT);
            etLastName.setGravity(Gravity.RIGHT);
            etMobileNumber.setGravity(Gravity.RIGHT);
        } else {
            tv_register.setTypeface(MyApplication.giloryBold);
            etTradingNumber.setTypeface(MyApplication.giloryItaly);
            etEmail.setTypeface(MyApplication.giloryItaly);
            etFirstName.setTypeface(MyApplication.giloryItaly);
            etLastName.setTypeface(MyApplication.giloryItaly);
            etMobileNumber.setTypeface(MyApplication.giloryItaly);
            btSubmit.setTypeface(MyApplication.giloryBold);
            btClose.setTypeface(MyApplication.giloryItaly);

            etTradingNumber.setGravity(Gravity.LEFT);
            etEmail.setGravity(Gravity.LEFT);
            etFirstName.setGravity(Gravity.LEFT);
            etLastName.setGravity(Gravity.LEFT);
            etMobileNumber.setGravity(Gravity.LEFT);
        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

         /*       if (!cbTerms.isChecked())
                    Toast.makeText(MoreActivity.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();

                else */
                if (!isValidEmail(etEmail.getText().toString()))
                    Toast.makeText(MoreActivity.this, "Please Enter a valid Email", Toast.LENGTH_SHORT).show();

                else {
                    MoreActivity.RegisterViaMowaziTask registerViaMowaziTask = new MoreActivity.RegisterViaMowaziTask(etTradingNumber.getText().toString(), etEmail.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString(), etMobileNumber.getText().toString());
                    registerViaMowaziTask.executeOnExecutor(MyApplication.threadPoolExecutor);

                }
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

            /*                Intent  i = new Intent(MoreActivity.this, MarketIndexActivity.class);
                            startActivity(i);
                            MyApplication.isFromLogin=false;*/
                            Actions.closeKeyboard(MoreActivity.this);
                        } catch (Exception e) {
                            e.getMessage();
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }
        });


        builder.setView(editDialogRegisterViaMowazi);
        dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MyApplication.isFromLogin=false;
            }
        });
        dialog.show();
    }


    public Boolean isValidEmail(String target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    private class RegisterViaMowaziTask extends AsyncTask<Void, Void, String> {

        String TradingNumber = "", Email = "", FName = "", LName = "", MobileNumber = "";

        public RegisterViaMowaziTask(String TradingNumbers, String Emails, String FNames, String LNames, String MobileNumbers) {
            TradingNumber = TradingNumbers;
            Email = Emails;
            FName = FNames;
            LName = LNames;
            MobileNumber = MobileNumbers;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(MoreActivity.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            // String url = "https://almowazi.com/TradingWebservice/WSTradingClient.asmx/SRV_SaveNewUserMowazi?";
            String url = MyApplication.parameter.getAlmowaziRegistrationLink() + "?";
            Log.wtf("link_reg", "link" + url);
            Log.wtf("broker_id", MyApplication.parameter.getMowaziBrokerId());
            // https://www.almowazi.com/md3iphoneservice/services/dataservice.svc

            HashMap<String, String> parameters = new HashMap<String, String>();

            try {
                parameters.clear();
                parameters.put("TradingNumber", TradingNumber);
                parameters.put("Email", Email);
                parameters.put("FName", FName);
                parameters.put("LName", LName);
                parameters.put("MobileNumber", MobileNumber);
                parameters.put("broker_ID", MyApplication.parameter.getMowaziBrokerId());

                result = ConnectionRequests.GET(url, MoreActivity.this, parameters);

                for (Map.Entry<String, String> map : parameters.entrySet()) {
                    Log.wtf("Login RegisterViaMowaziTask", "parameters : " + map.getKey() + "= " + map.getValue());
                }
                Log.wtf("Login", "RegisterViaMowaziTask result = " + result);

                try {
                    publishProgress(params);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "SRV_SaveNewUserMowazi", Toast.LENGTH_LONG).show();
                            Toast.makeText(MoreActivity.this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "SRV_SaveNewUserMowazi", Toast.LENGTH_LONG).show();
                        Toast.makeText(MoreActivity.this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
                    }
                });
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

            try {

                JSONObject jsonobj = new JSONObject(result);

                if (jsonobj.getString("Status").equals("Success")) {
                    Toast.makeText(MoreActivity.this, (MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage") : jsonobj.getString("EnglishMessage")), Toast.LENGTH_SHORT).show();

                    boolean callLogin=false;
                    if(MyApplication.isFromLogin) {
                        dialog.dismiss();
                        callLogin=true;
                    }else {
                        dialog.dismiss();
                    }
                    final Handler handler = new Handler();
                    boolean finalCallLogin = callLogin;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                    startActivity(new Intent(MoreActivity.this,LoginFingerPrintActivity.class));
                                   /* FragmentManager fm = getSupportFragmentManager();
                                    NormalLoginFragment fragment = (NormalLoginFragment)fm.findFragmentByTag(getResources().getString(R.string.login));
                                    fragment.callLogin();*/

                                Actions.closeKeyboard(MoreActivity.this);
                                finish();

                            } catch (Exception e) {
                                e.getMessage();
                                e.printStackTrace();
                            }
                        }
                    }, 100);
                } else {
                    Toast.makeText(MoreActivity.this, "please check your data entry", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
