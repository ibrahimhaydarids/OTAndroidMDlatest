package com.ids.fixot.activities;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.interfaces.spItemListener;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Amal on 4/6/2017.
 */

public class SettingsActivity extends AppCompatActivity implements MarketStatusListener, spItemListener {

    RelativeLayout rlLayout, changePassword, changePin, layoutFingerPrint, layoutVersionNumber, layoutPushNotification;
    Switch switchNot, switchFingerprint, switchDarckTheme;
    ImageView ivArrow, ivArrowPin;
    RadioButton rbArabic, rbEnglish;
    TextView tvVersionNumber, tvChangeFont;
    String versionNumber = "";
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    RelativeLayout layoutThemes;
    private BroadcastReceiver receiver;
    Spinner spInstrumentsTop;
    LinearLayout linearSettings;

    Button btNormalFont,btLargeFont,btLargerFont;

    public SettingsActivity() {
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
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_setting);
        Actions.initializeBugsTracking(this);

        findViews();
        setAppFonts();
        setListeners();

        Actions.initializeToolBar(getString(R.string.settings), SettingsActivity.this);
        Actions.overrideFonts(this, rlLayout, false);
        Actions.showHideFooter(this);

        rbArabic.setTypeface(MyApplication.droidbold);

        try {
            spInstrumentsTop = (Spinner) findViewById(R.id.spInstrumentTop);
            if (BuildConfig.Enable_Markets)
                spInstrumentsTop.setVisibility(View.VISIBLE);
            else
                spInstrumentsTop.setVisibility(View.GONE);
            spInstrumentsTop.setVisibility(View.GONE);
            //  Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
            Log.wtf("exception", e.toString());
        }

        setBackgrounds();
    }

    private void findViews() {
        btNormalFont= findViewById(R.id.btNormalFont);
        btLargeFont= findViewById(R.id.btLargeFont);
        btLargerFont= findViewById(R.id.btLargerFont);

        ivArrow = findViewById(R.id.ivArrow);
        linearSettings=findViewById(R.id.linearSettings);
        ivArrowPin = findViewById(R.id.ivArrowPin);
        tvVersionNumber = findViewById(R.id.tvVersionNumber);
        layoutVersionNumber = findViewById(R.id.layoutVersionNumber);
        layoutFingerPrint = findViewById(R.id.layoutFingerPrint);
        layoutPushNotification = findViewById(R.id.layoutPushNotification);
        changePassword = findViewById(R.id.changePassword);
        changePin = findViewById(R.id.changePin);
        changePin.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePinActivity.class));
        });

        changePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
        //changePin.setVisibility(View.VISIBLE);
        changePin.setVisibility(MyApplication.mshared.getBoolean("ShowChangePIN", false) ? View.VISIBLE : View.GONE);


        if (BuildConfig.Enable_Markets) {
           // layoutPushNotification.setVisibility(View.VISIBLE);
            layoutPushNotification.setVisibility(MyApplication.mshared.getBoolean("EnableNotification", false) ? View.VISIBLE : View.GONE);
        }
        else
            layoutPushNotification.setVisibility(View.GONE);
        rlLayout = findViewById(R.id.rlLayout);
        switchNot = findViewById(R.id.switchNot);
        switchFingerprint = findViewById(R.id.switchFingerprint);
        switchDarckTheme = findViewById(R.id.switchDarckTheme);
        rbArabic = findViewById(R.id.rbArabic);
        rbEnglish = findViewById(R.id.rbEnglish);
        layoutThemes = findViewById(R.id.layoutThemes);
        tvChangeFont = findViewById(R.id.tvChangeFont);

        changePassword.setVisibility(View.VISIBLE);
        ivArrow.setRotation(MyApplication.lang == MyApplication.ENGLISH ? 180f : 0f);
        ivArrowPin.setRotation(MyApplication.lang == MyApplication.ENGLISH ? 180f : 0f);
        try {

            versionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " (" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ")";

            tvVersionNumber.setText(versionNumber);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        switchFingerprint.setChecked(MyApplication.mshared.getBoolean(getResources().getString(R.string.allow_finger_print), false));
        switchDarckTheme.setChecked(!MyApplication.mshared.getBoolean(getResources().getString(R.string.normal_theme), true));

//        layoutThemes.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            try {
                if (fingerprintManager.isHardwareDetected()) {

                    if (fingerprintManager.hasEnrolledFingerprints()) {

                        if (keyguardManager.isKeyguardSecure()) {
                            //check if allowed and Remembered
                            layoutFingerPrint.setVisibility(View.VISIBLE);
                            layoutVersionNumber.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorLight));
                            layoutVersionNumber.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, MyApplication.mshared.getBoolean(SettingsActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));

                        } else {

                            layoutFingerPrint.setVisibility(View.GONE);
                        }
                    }
                } else {

                    layoutFingerPrint.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                layoutFingerPrint.setVisibility(View.GONE);
            }
        } else {
            layoutFingerPrint.setVisibility(View.GONE);
        }

        tvChangeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //   Toast.makeText(getApplicationContext(),"asdasdas",Toast.LENGTH_LONG).show();
    }



    private void setBackgrounds(){

        final int childCount = linearSettings.getChildCount();
        int count=0;
        for (int i = 0; i < childCount; i++) {
            View v = linearSettings.getChildAt(i);
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

    private void setListeners() {

        if (MyApplication.lang == MyApplication.ARABIC)
            rbArabic.setChecked(true);
        else
            rbEnglish.setChecked(true);


        rbArabic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                MyApplication.instruments.clear();
                Actions.setLocal(MyApplication.ARABIC, SettingsActivity.this);
                MyApplication.lang = MyApplication.ARABIC;
                MyApplication.editor.putInt("lang", MyApplication.ARABIC).apply();
                LocalUtils.setLocale(new Locale("ar"));
                LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

                Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);

                SettingsActivity.this.recreate();

            }
        });

        rbEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                MyApplication.instruments.clear();
                Actions.setLocal(MyApplication.ENGLISH, SettingsActivity.this);
                MyApplication.lang = MyApplication.ENGLISH;
                MyApplication.editor.putInt("lang", MyApplication.ENGLISH).apply();
                LocalUtils.setLocale(new Locale("en"));
                LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

                Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);

                SettingsActivity.this.recreate();
            }
        });
        layoutPushNotification.setOnClickListener(view ->
                // switchNot.performClick()
                startActivity(new Intent(SettingsActivity.this, NotificationSettingsActivity.class))
        );

        switchFingerprint.setOnClickListener(v -> {

            Log.wtf("Clicked on switch", "now");
            if (switchFingerprint.isChecked()) {
                MyApplication.editor.putBoolean(getResources().getString(R.string.allow_finger_print), true).apply();
            } else {

                MyApplication.editor.putBoolean(getResources().getString(R.string.allow_finger_print), false).apply();
            }
        });
        layoutFingerPrint.setOnClickListener(view -> switchFingerprint.performClick());


        switchDarckTheme.setOnClickListener(v -> {
            Log.wtf("Clicked on switchDarckTheme", "now");
            if (switchDarckTheme.isChecked()) {
                MyApplication.editor.putBoolean(getResources().getString(R.string.normal_theme), false).apply();
            } else {
                MyApplication.editor.putBoolean(getResources().getString(R.string.normal_theme), true).apply();
            }
            SettingsActivity.this.recreate();
        });
    }

    public void back(View v) {
        startActivity(new Intent(this,MoreActivity.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Actions.setActivityTheme(this);
        //finish();
        //startActivity(getIntent());
        try {
            Actions.setSpinnerTop(this, spInstrumentsTop, this);
        } catch (Exception e) {
        }

        Actions.checkSession(this);
        //Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);

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

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }



    private void setAppFonts(){
        btNormalFont.setOnClickListener(v->{
               setTabs(1);
               MyApplication.editor.putInt("font_size", 1).apply();
               finish();
               startActivity(getIntent());
        });

        btLargeFont.setOnClickListener(v->{
               setTabs(2);
               MyApplication.editor.putInt("font_size", 2).apply();
               finish();
               startActivity(getIntent());

        });

        btLargerFont.setOnClickListener(v->{
               setTabs(3);
               MyApplication.editor.putInt("font_size", 3).apply();
               finish();
               startActivity(getIntent());
        });


       setTabs(MyApplication.mshared.getInt("font_size", 1));



        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        String toastMsg;
        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                toastMsg = "Large screen";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                toastMsg = "Normal screen";

                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                btLargerFont.setVisibility(View.GONE);
                toastMsg = "Small screen";

                break;
            default:
                toastMsg = "Screen size is neither large, normal or small";
        }
        //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();


    }

    private void setTabs(int size){
        if(size==1){
            btNormalFont.setBackgroundResource(R.drawable.rounded_light);
            btLargeFont.setBackgroundResource(0);
            btLargerFont.setBackgroundResource(0);
        }else if(size == 2){
            btLargeFont.setBackgroundResource(R.drawable.rounded_light);
            btNormalFont.setBackgroundResource(0);
            btLargerFont.setBackgroundResource(0);
        }else if(size == 3){
            btLargerFont.setBackgroundResource(R.drawable.rounded_light);
            btLargeFont.setBackgroundResource(0);
            btNormalFont.setBackgroundResource(0);
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MoreActivity.class));
        finishAffinity();
       // super.onBackPressed();
    }
}
