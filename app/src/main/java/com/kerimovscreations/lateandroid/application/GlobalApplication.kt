package com.kerimovscreations.lateandroid.application

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.kerimovscreations.lateandroid.tools.LocaleHelper

class GlobalApplication : Application() {

    override fun attachBaseContext(base: Context) {
        localeManager = LocaleHelper(base)
        super.attachBaseContext(localeManager!!.setLocale(base))
        Log.d(ContentValues.TAG, "attachBaseContext")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeManager!!.setLocale(this)
        Log.d(ContentValues.TAG, "onConfigurationChanged: " + newConfig.locale.language)
    }

    companion object {
        @JvmField
        var localeManager: LocaleHelper? = null
    }
}