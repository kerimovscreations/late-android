package com.kerimovscreations.lateandroid.application;

import android.app.Application;
import android.content.Context;

import com.kerimovscreations.lateandroid.tools.HelpFunctions;
import com.kerimovscreations.lateandroid.tools.LocaleHelper;

public class GlobalApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, HelpFunctions.shared.getUserLanguage(base)));
    }

}
