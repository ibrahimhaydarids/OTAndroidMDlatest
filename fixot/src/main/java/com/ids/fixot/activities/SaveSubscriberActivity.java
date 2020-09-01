package com.ids.fixot.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;

public class SaveSubscriberActivity extends AppCompatActivity {

    SaveSubscriber mSaveSubscriber;
    LoadSubscriber mLoadSubsriber;
    RelativeLayout rootLayout;
    Toolbar myToolbar;
    TextView tvEmail, tvInvestorId, tvMobile;
    EditText etEmail, etInvestorId,etMobile;
    Button btSave;
    LinearLayout loading;
    public SaveSubscriberActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        setContentView(R.layout.activity_save_subscriber);
        Actions.initializeBugsTracking(this);
        findViews();
        Actions.initializeToolBar(getString(R.string.save_subscriber), SaveSubscriberActivity.this);
        Actions.overrideFonts(this, rootLayout, false);
        setListeners();
    }


    private void findViews(){
        rootLayout = findViewById(R.id.rootLayout);

        myToolbar = findViewById(R.id.my_toolbar);

        tvEmail = findViewById(R.id.tvEmail);
        tvInvestorId = findViewById(R.id.tvInvestorId);
        tvMobile = findViewById(R.id.tvPhone);

        etEmail = findViewById(R.id.etEmail);
        etInvestorId = findViewById(R.id.etInvestorId);
        etMobile = findViewById(R.id.etMobileNumber);
        btSave = findViewById(R.id.btSave);
        loading=findViewById(R.id.loading);

    }

    private void setListeners(){
        btSave.setOnClickListener(v->{
            if(/*etEmail.getText().toString().isEmpty() || */etMobile.getText().toString().isEmpty() || etInvestorId.getText().toString().isEmpty())
                Actions.CreateDialog(this,getString(R.string.check_empty_fields),false,false);
        /*    else if(!Actions.isValidEmail(etEmail.getText().toString()))
                Actions.CreateDialog(this,getString(R.string.valid_email),false,false);
            */else {
                mSaveSubscriber = new SaveSubscriber();
                mSaveSubscriber.executeOnExecutor(MyApplication.threadPoolExecutor);
            }

        });
    }

    public void back(View v) {
        View view = this.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
    }


    private class SaveSubscriber extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);


        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.SaveSubscriber.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();


            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("Email").value(""/*etEmail.getText().toString()+""*/)
                        .key("InvestorID").value(etInvestorId.getText().toString())
                        .key("ID").value("0")
                        .key("key").value(getString(R.string.beforekey)+"")
                        .key("Phone").value(etMobile.getText().toString())
                        .key("Lang").value(MyApplication.lang)

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
                Log.wtf("json_stringer",stringer.toString());
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
            try{ Log.wtf("result_subscription",result);}catch (Exception e){}
            JSONObject object = null;
            try {
                object = new JSONObject(result);
                String message = "";
                message = MyApplication.lang == MyApplication.ARABIC?object.getString("MessageAr"):object.getString("MessageEn");

                String status=object.getString("Status");
                if (status.equals("0")) {

                    int subscriberId=object.getInt("ObjectID");
                    startActivity(new Intent(SaveSubscriberActivity.this,ActivityPinCode.class).putExtra("subscriber_id",subscriberId));

                } else {
                   Actions.CreateDialog(SaveSubscriberActivity.this,message,false,false);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.SaveSubscriber.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

        }
    }



    private class LoadSubscriber extends AsyncTask<Void, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute(); }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.LoadSubscriber.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("deviceId", MyApplication.mshared.getInt("deviceId",0)+"");
            parameters.put("InvestorId", MyApplication.currentUser.getInvestorId()+"");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));

            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                MyApplication.userSubscriber = GlobalFunctions.GetSubscriber(result);

            } catch (Exception e) {
                e.printStackTrace();

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(MyApplication.isSubscriber){
               // showSubDialog(activity);
            }
        }
    }


}
