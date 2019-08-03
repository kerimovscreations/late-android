package com.kerimovscreations.lateandroid.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.kerimovscreations.lateandroid.tools.LocaleHelper;

import static android.content.ContentValues.TAG;

public class GlobalApplication extends Application {

    public static LocaleHelper localeManager;

    @Override
    protected void attachBaseContext(Context base) {
        localeManager = new LocaleHelper(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.locale.getLanguage());
    }

}
