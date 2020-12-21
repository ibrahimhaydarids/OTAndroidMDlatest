package com.ids.fixot;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;

import java.util.Locale;

/**
 * Created by Amal on 7/18/2017.
 */

public class LocalUtils {

    private static Locale sLocale;

    public static void setLocale(Locale locale) {
        sLocale = locale;
        if (sLocale != null) {
            Locale.setDefault(sLocale);
        }
    }

    public static void updateConfig(ContextThemeWrapper wrapper) {
        if (sLocale != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = new Configuration();
            configuration.setLocale(sLocale);
           try{
               if(MyApplication.mshared.getInt("font_size", 1)==3)
                 configuration.fontScale=1.24f;
              else if(MyApplication.mshared.getInt("font_size", 1)==2)
                   configuration.fontScale=1.12f;
               else
                   configuration.fontScale=1.0f;
           }catch (Exception e){}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
            }
            wrapper.applyOverrideConfiguration(configuration);
        }
    }

    public static void updateConfig(Application app, Configuration configuration) {
        if (sLocale != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //Wrapping the configuration to avoid Activity endless loop
            Configuration config = new Configuration(configuration);
            // We must use the now-deprecated config.locale and res.updateConfiguration here,
            // because the replacements aren't available till API level 24 and 17 respectively.
            config.locale = sLocale;
              try{
               if(MyApplication.mshared.getInt("font_size", 1)==3)
                 configuration.fontScale=1.3f;
               else if(MyApplication.mshared.getInt("font_size", 1)==2)
                   configuration.fontScale=1.15f;
               else
                   configuration.fontScale=1.0f;

           }catch (Exception e){}
            Resources res = app.getBaseContext().getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }
}