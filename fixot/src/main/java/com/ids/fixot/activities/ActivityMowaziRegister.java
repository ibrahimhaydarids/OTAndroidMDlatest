package com.ids.fixot.activities;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.ids.fixot.fragments.NormalLoginFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by DEV on 3/20/2018.
 */

public class ActivityMowaziRegister extends AppCompatActivity   {

    RelativeLayout rootLayout;
    WebView wvDetails;
    String url;

    private boolean started = false;
    CheckBox cbTerms;
    Button btSubmit;
    Spinner spInstrumentsTop;

    public ActivityMowaziRegister() {
        LocalUtils.updateConfig(this);
    }

    TextView tv_register;
    EditText etTradingNumber;
    EditText etEmail;
    EditText etFirstName;
    EditText etLastName;
    EditText etMobileNumber;

    Button btClose ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Actions.setActivityTheme(this);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.acitivity_mowazi_register);
        Actions.initializeBugsTracking(this);

        started = true;

        init();







    }


    

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Actions.unregisterSessionReceiver(this);
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void back(View v) {
        relogin();
       /* Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkLanguage(this, started);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);

        Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }



    public void init() {



      
       
         tv_register = findViewById(R.id.tv_register);
         etTradingNumber = findViewById(R.id.etTradingNumber);
         etEmail = findViewById(R.id.etEmail);
         etFirstName = findViewById(R.id.etFirstName);
         etLastName = findViewById(R.id.etLastName);
         etMobileNumber = findViewById(R.id.etMobileNumber);
         btSubmit = findViewById(R.id.btSubmit);
         btClose = findViewById(R.id.btClose);

        if(MyApplication.currentUser.getInvestorId()!=0){
            etTradingNumber.setText(MyApplication.currentUser.getInvestorId()+" ");
            etTradingNumber.setEnabled(false);
        }



        CheckBox cbTerms = findViewById(R.id.cbTerms);
        TextView tv_view_terms = findViewById(R.id.tv_view_terms);

       tv_view_terms.setOnClickListener(v -> {
                    String url = MyApplication.parameter.getAlmowaziPolicyLink();
                    startActivity(new Intent(ActivityMowaziRegister.this, PdfDisplayActivity.class).putExtra("url", url));
                }
        );

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

                if (!cbTerms.isChecked())
                    Toast.makeText(ActivityMowaziRegister.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();

                else
                if (!isValidEmail(etEmail.getText().toString()))
                    Toast.makeText(ActivityMowaziRegister.this, "Please Enter a valid Email", Toast.LENGTH_SHORT).show();

                else {
                    RegisterViaMowaziTask registerViaMowaziTask = new RegisterViaMowaziTask(etTradingNumber.getText().toString(), etEmail.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString(), etMobileNumber.getText().toString());
                    registerViaMowaziTask.execute();
                   /* RegisterMowazi registerMowazi = new RegisterMowazi();
                    registerMowazi.execute();*/
                }
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        relogin();
                    }
                }, 100);
            }
        });


     
    }


    public Boolean isValidEmail(String target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onBackPressed() {
        /*Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();*/
        relogin();
       // super.onBackPressed();
    }



    private void relogin(){

        LoginTask login = new LoginTask();
        login.execute();

   /*     MyApplication.isAutoLogin=true;
        startActivity(new Intent(ActivityMowaziRegister.this, LoginFingerPrintActivity.class));
        finish();*/
    }










  /*  private class RegisterMowazi extends AsyncTask<Void, Void, String> {

        String TradingNumber = "", Email = "",FName="",LName="",MobileNumber="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            TradingNumber= etTradingNumber.getText().toString();
               Email=     etEmail.getText().toString();
                FName=    etFirstName.getText().toString();
                 LName=   etLastName.getText().toString();
                 MobileNumber=   etMobileNumber.getText().toString();

            MyApplication.showDialog(ActivityMowaziRegister.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.parameter.getAlmowaziRegistrationLink().trim() + "?";

            // MyApplication.link + MyApplication.Login.getValue();
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("TradingNumber", TradingNumber);
            parameters.put("Email", Email);
            parameters.put("FName", FName);
            parameters.put("LName", LName);
            parameters.put("MobileNumber", MobileNumber);
            parameters.put("broker_ID", MyApplication.parameter.getMowaziBrokerId());
            JSONStringer stringer = null;
          *//*  try {
                stringer = new JSONStringer()
                        .object()
                        .key("TradingNumber").value(TradingNumber)
                        .key("Email").value(Email)
                        .key("FName").value(FName)
                        .key("LName").value(LName)
                        .key("MobileNumber").value(MobileNumber)
                        .key("broker_ID").value(MyApplication.parameter.getMowaziBrokerId())
                        .endObject();
                //MyApplication.currentUser.getKey())
                Log.wtf("stringer", "stringer = " + stringer);
            } catch (JSONException e) {
                e.printStackTrace();
            }*//*

            try {
                result = ConnectionRequests.GET(url,ActivityMowaziRegister.this, parameters);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (MyApplication.isDebug) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "ChangePassword", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception es) {
                    Log.wtf("Appservice ", "Error - ChangePassword : " + es.getMessage());
                }
            }
            Log.wtf("result", "result = " + result);
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
                    Toast.makeText(ActivityMowaziRegister.this, (MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage") : jsonobj.getString("EnglishMessage")), Toast.LENGTH_SHORT).show();

                    relogin();
                } else {
                    try {
                        Toast.makeText(ActivityMowaziRegister.this, (MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage") : jsonobj.getString("EnglishMessage")), Toast.LENGTH_SHORT).show();
                    }catch (Exception e) {

                        Toast.makeText(ActivityMowaziRegister.this, "please check your data entry", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
*/



    public void CreateDialogResponse(final Activity c, String message,Boolean isSuccess) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setCancelable(false)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    if(isSuccess)
                     relogin();
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();

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
            MyApplication.showDialog(ActivityMowaziRegister.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            // String url = "https://almowazi.com/TradingWebservice/WSTradingClient.asmx/SRV_SaveNewUserMowazi?";
            String url = MyApplication.parameter.getAlmowaziRegistrationLink().trim() + "?";
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
                            Toast.makeText(ActivityMowaziRegister.this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                result = ConnectionRequests.GET(url, ActivityMowaziRegister.this, parameters);

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "SRV_SaveNewUserMowazi", Toast.LENGTH_LONG).show();
                        Toast.makeText(ActivityMowaziRegister.this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
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
                  //  Toast.makeText(ActivityMowaziRegister.this, (MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage") : jsonobj.getString("EnglishMessage")), Toast.LENGTH_SHORT).show();
                   CreateDialogResponse(ActivityMowaziRegister.this,(MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage")+"\n"+(jsonobj.getString("ArabicMessage2").matches("null")?"":jsonobj.getString("ArabicMessage2")) : jsonobj.getString("EnglishMessage")+"\n"+(jsonobj.getString("EnglishMessage2").matches("null")?"":jsonobj.getString("EnglishMessage2"))),true);
                   // relogin();
                } else {
                    try {
                        CreateDialogResponse(ActivityMowaziRegister.this,(MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage")+"\n"+(jsonobj.getString("ArabicMessage2").matches("null")?"":jsonobj.getString("EnglishMessage2")) : jsonobj.getString("EnglishMessage")+"\n"+(jsonobj.getString("EnglishMessage2").matches("null")?"":jsonobj.getString("EnglishMessage2"))),false);

                       // Toast.makeText(ActivityMowaziRegister.this, (MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage") : jsonobj.getString("EnglishMessage")), Toast.LENGTH_SHORT).show();
                    }catch (Exception e) {

                        Toast.makeText(ActivityMowaziRegister.this, "please check your data entry", Toast.LENGTH_SHORT).show();
                    }
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    public class LoginTask extends AsyncTask<Void, Void, String> {

        String username = "", password = "";
        String random = Actions.getRandom();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = MyApplication.mshared.getString("cashed_username","");
            password = MyApplication.mshared.getString("cashed_password","");

            Log.wtf("login", "password = " + password);
            try {
                MyApplication.showDialog(ActivityMowaziRegister.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.Login.getValue();
            Log.wtf("login_password",Actions.MD5(Actions.MD5(password) + random));
            Log.wtf("login_random",random);
            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("Username").value(username)
                        .key("Password").value(Actions.MD5(Actions.MD5(password) + random))
                        .key("Random").value(random)
                        .key("Key").value(getString(R.string.beforekey))
                        .key("DeviceType").value(Actions.getApplicationType())
                        .key("VersionNumber").value(Actions.getVersionName(ActivityMowaziRegister.this))
                        .endObject();

                Log.wtf("login", "stringer = " + stringer);
                result = ConnectionRequests.POSTWCF(url, stringer);
                //Log.wtf("Login","result = " + result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //  load();
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
                MyApplication.currentUser = GlobalFunctions.GetUserInfo(result);
                MyApplication.editor.putString(getResources().getString(R.string.afterkey), MyApplication.currentUser.getKey()).apply();
                MyApplication.afterKey = MyApplication.currentUser.getKey();
                MyApplication.editor.putBoolean("fingerprintBlock", false).apply();
                if (MyApplication.currentUser.getStatus()==0||MyApplication.currentUser.getStatus()==52) {

                    Log.wtf("status_id",MyApplication.currentUser.getStatus()+"aaaaaaa");

                    for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
                        if (MyApplication.currentUser.getSubAccounts().get(i).isDefault()) {

                            MyApplication.selectedSubAccount = MyApplication.currentUser.getSubAccounts().get(i);
                            break;
                        }
                    }
                    MyApplication.currentUser.setUsername(username);
                    Actions.setLastUserId(ActivityMowaziRegister.this,MyApplication.currentUser.getId());

                    if (MyApplication.currentUser.getStatus() == 0) {
                        MyApplication.isLoggedIn=true;
                        MyApplication.isAutoLogin=false;
                        Actions.setLastMarketId(ActivityMowaziRegister.this, 3);
                        MyApplication.marketID ="3";
                        finish();


                    }else {
                        finish();
                    }
                }
                else {
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }
}
