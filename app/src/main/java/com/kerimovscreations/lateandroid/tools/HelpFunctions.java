package com.kerimovscreations.lateandroid.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kerimovscreations.lateandroid.R;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

public class HelpFunctions {

    public static HelpFunctions shared = new HelpFunctions();

    private HelpFunctions() {

    }

    public String getCurrentLanguageCode() {
        return Locale.getDefault().getLanguage();
    }

    public void setTimerStartTimestamp(Context context, long timestamp) {
        Log.e("ERR33", String.valueOf(timestamp));
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.timer_start_timestamp), timestamp);
        editor.apply();
    }

    public void setTimerDuration(Context context, long duration) {
        Log.e("ERR33", String.valueOf(duration));
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.timer_duration), duration);
        editor.apply();
    }

    public long getTimerStartTimestamp(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getLong(context.getString(R.string.timer_start_timestamp), 0);
    }

    public long getTimerDuration(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getLong(context.getString(R.string.timer_duration), 0);
    }

    public long getCurrentTimestamp() {
        long time = new Date().getTime();
        Timestamp ts = new Timestamp(time);
        return ts.getTime();
    }

    public boolean isRecentTimerActive(Context context) {
        long startTimestamp = HelpFunctions.shared.getTimerStartTimestamp(context);
        if (startTimestamp == 0)
            return false;

        long duration = HelpFunctions.shared.getTimerDuration(context);
        long currentTimestamp = getCurrentTimestamp();
        return (currentTimestamp - startTimestamp) < duration;
    }
}
