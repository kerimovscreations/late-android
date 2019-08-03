package com.kerimovscreations.lateandroid.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

import androidx.work.Data;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.application.GlobalApplication;
import com.kerimovscreations.lateandroid.enums.SoundType;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.os.Build.VERSION_CODES.P;
import static com.kerimovscreations.lateandroid.enums.SoundType.MALE_NORMAL;

public class HelpFunctions {

    public static HelpFunctions shared = new HelpFunctions();

    private HelpFunctions() {

    }

    public void setUserLanguage(Context context, String language) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.display_language), language);
        editor.apply();
        GlobalApplication.localeManager.setNewLocale(context, language);

        if (language.equals("en")) {
            setSoundType(context, MALE_NORMAL.getValue());
        }
    }

    public String getUserLanguage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.display_language), "en");
    }

    public void setSoundType(Context context, int soundType) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(context.getString(R.string.sound_type), soundType);
        editor.apply();
    }

    public int getSoundType(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getInt(context.getString(R.string.sound_type), MALE_NORMAL.getValue());
    }

    public void setTimerStartTimestamp(Context context, long timestamp) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.timer_start_timestamp), timestamp);
        editor.apply();
    }

    public void setTimerDuration(Context context, long duration) {
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

    public static Object getPrivateField(String className, String fieldName, Object object) {
        try {
            Class c = Class.forName(className);
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(object);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static String getTitleCache() {
        // https://developer.android.com/about/versions/pie/restrictions-non-sdk-interfaces
        if (isAtLeastVersion(P)) return "Can't access title cache\nstarting from API 28";
        Object o = HelpFunctions.getPrivateField("android.app.ApplicationPackageManager", "sStringCache", null);
        Map<?, WeakReference<CharSequence>> cache = (Map<?, WeakReference<CharSequence>>) o;
        if (cache == null) return "";

        StringBuilder builder = new StringBuilder("Cache:").append("\n");
        for (Map.Entry<?, WeakReference<CharSequence>> e : cache.entrySet()) {
            CharSequence title = e.getValue().get();
            if (title != null) {
                builder.append(title).append("\n");
            }
        }
        return builder.toString();
    }

    public static Resources getTopLevelResources(Activity a) {
        try {
            return a.getPackageManager().getResourcesForApplication(a.getApplicationInfo());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAtLeastVersion(int version) {
        return Build.VERSION.SDK_INT >= version;
    }

    public Data getNotificationData(Context context, int value) {

        int resourceId = R.raw.en_male_mins_0_left;
        int titleId = R.string.mins_0_left;
        String language = getUserLanguage(context);
        SoundType soundType = SoundType.values()[getSoundType(context)];

        switch (value) {
            case 0:
                titleId = R.string.mins_0_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_0_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_0_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_0_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_0_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_0_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_0_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_0_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_0_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_0_left;
                            break;
                    }
                }
                break;
            case 5:
                titleId = R.string.mins_5_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_5_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_5_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_5_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_5_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_5_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_5_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_5_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_5_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_5_left;
                            break;
                    }
                }
                break;
            case 10:
                titleId = R.string.mins_10_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_10_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_10_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_10_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_10_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_10_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_10_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_10_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_10_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_10_left;
                            break;
                    }
                }
                break;
            case 15:
                titleId = R.string.mins_15_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_15_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_15_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_15_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_15_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_15_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_15_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_15_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_15_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_15_left;
                            break;
                    }
                }
                break;
            case 20:
                titleId = R.string.mins_20_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_20_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_20_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_20_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_20_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_20_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_20_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_20_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_20_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_20_left;
                            break;
                    }
                }
                break;
            case 30:
                titleId = R.string.mins_30_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_30_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_30_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_30_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_30_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_30_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_30_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_30_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_30_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_30_left;
                            break;
                    }
                }
                break;
            case 60:
                titleId = R.string.mins_60_left;
                if (language.equals("en")) {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.en_male_mins_60_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.en_female_mins_60_left;
                            break;
                        default:
                            resourceId = R.raw.en_male_mins_60_left;
                    }
                } else {
                    switch (soundType) {
                        case MALE_NORMAL:
                            resourceId = R.raw.ru_male_mins_60_left;
                            break;
                        case FEMALE_NORMAL:
                            resourceId = R.raw.ru_female_mins_60_left;
                            break;
                        case MALE_FUNNY_1:
                            resourceId = R.raw.ru_male_fun_1_mins_60_left;
                            break;
                        case MALE_FUNNY_2:
                            resourceId = R.raw.ru_male_fun_2_mins_60_left;
                            break;
                        case FEMALE_FUNNY_1:
                            resourceId = R.raw.ru_female_fun_1_mins_60_left;
                            break;
                        case FEMALE_FUNNY_2:
                            resourceId = R.raw.ru_female_fun_1_mins_60_left;
                            break;
                    }
                }
                break;
        }

        return new Data.Builder()
                .putString("TITLE", context.getString(titleId))
                .putInt("SOUND_ID", resourceId)
                .build();
    }
}
