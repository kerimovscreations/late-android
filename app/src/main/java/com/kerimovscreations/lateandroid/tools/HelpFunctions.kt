package com.kerimovscreations.lateandroid.tools

import android.content.Context
import android.os.Build
import androidx.work.Data
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.application.GlobalApplication
import com.kerimovscreations.lateandroid.enums.SoundType
import java.sql.Timestamp
import java.util.*

class HelpFunctions private constructor() {

    fun setUserLanguage(context: Context, language: String) {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(context.getString(R.string.display_language), language)
        editor.apply()
        GlobalApplication.localeManager!!.setNewLocale(context, language)
        if (language == "en") {
            setSoundType(context, SoundType.MALE_NORMAL.value)
        }
    }

    fun setSoundType(context: Context, soundType: Int) {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt(context.getString(R.string.sound_type), soundType)
        editor.apply()
    }

    fun getSoundType(context: Context): Int {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        return sharedPref.getInt(context.getString(R.string.sound_type), SoundType.MALE_NORMAL.value)
    }

    fun setTimerStartTimestamp(context: Context, timestamp: Long) {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(context.getString(R.string.timer_start_timestamp), timestamp)
        editor.apply()
    }

    fun setTimerDuration(context: Context, duration: Long) {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(context.getString(R.string.timer_duration), duration)
        editor.apply()
    }

    fun getTimerStartTimestamp(context: Context): Long {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        return sharedPref.getLong(context.getString(R.string.timer_start_timestamp), 0)
    }

    fun getTimerDuration(context: Context): Long {
        val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        return sharedPref.getLong(context.getString(R.string.timer_duration), 0)
    }

    val currentTimestamp: Long
        get() {
            val time = Date().time
            val ts = Timestamp(time)
            return ts.time
        }

    fun isRecentTimerActive(context: Context): Boolean {
        val startTimestamp = shared.getTimerStartTimestamp(context)
        if (startTimestamp == 0L) return false
        val duration = shared.getTimerDuration(context)
        val currentTimestamp = currentTimestamp
        return currentTimestamp - startTimestamp < duration
    }

    fun getNotificationData(context: Context, value: Int): Data {
        var resourceId = R.raw.en_male_mins_0_left
        var titleId = R.string.mins_0_left
        val language = GlobalApplication.localeManager!!.language
        val soundType = SoundType.values()[getSoundType(context)]
        when (value) {
            0 -> {
                titleId = R.string.mins_0_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_0_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_0_left
                        else -> R.raw.en_male_mins_0_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_0_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_0_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_0_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_0_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_0_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_0_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_0_left
                    }
                }
            }
            5 -> {
                titleId = R.string.mins_5_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_5_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_5_left
                        else -> R.raw.en_male_mins_5_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_5_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_5_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_5_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_5_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_5_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_5_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_5_left
                    }
                }
            }
            10 -> {
                titleId = R.string.mins_10_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_10_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_10_left
                        else -> R.raw.en_male_mins_10_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_10_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_10_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_10_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_10_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_10_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_10_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_10_left
                    }
                }
            }
            15 -> {
                titleId = R.string.mins_15_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_15_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_15_left
                        else -> R.raw.en_male_mins_15_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_15_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_15_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_15_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_15_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_15_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_15_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_15_left
                    }
                }
            }
            20 -> {
                titleId = R.string.mins_20_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_20_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_20_left
                        else -> R.raw.en_male_mins_20_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_20_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_20_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_20_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_20_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_20_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_20_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_20_left
                    }
                }
            }
            30 -> {
                titleId = R.string.mins_30_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_30_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_30_left
                        else -> R.raw.en_male_mins_30_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_30_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_30_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_30_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_30_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_30_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_30_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_30_left
                    }
                }
            }
            60 -> {
                titleId = R.string.mins_60_left
                if (language == "en") {
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.en_male_mins_60_left
                        SoundType.FEMALE_NORMAL -> R.raw.en_female_mins_60_left
                        else -> R.raw.en_male_mins_60_left
                    }
                } else {
                    // TODO: Change custom selection
                    resourceId = when (soundType) {
                        SoundType.MALE_NORMAL -> R.raw.ru_male_mins_60_left
                        SoundType.FEMALE_NORMAL -> R.raw.ru_female_mins_60_left
                        SoundType.MALE_FUNNY_1 -> R.raw.ru_male_fun_1_mins_60_left
                        SoundType.MALE_FUNNY_2 -> R.raw.ru_male_fun_2_mins_60_left
                        SoundType.FEMALE_FUNNY_1 -> R.raw.ru_female_fun_1_mins_60_left
                        SoundType.FEMALE_FUNNY_2 -> R.raw.ru_female_fun_1_mins_60_left
                        SoundType.CUSTOM -> R.raw.ru_female_fun_1_mins_60_left
                    }
                }
            }
        }
        return Data.Builder()
                .putString("TITLE", context.getString(titleId))
                .putInt("SOUND_ID", resourceId)
                .build()
    }

    companion object {
        var shared = HelpFunctions()

        @JvmStatic
        fun isAtLeastVersion(version: Int): Boolean {
            return Build.VERSION.SDK_INT >= version
        }
    }
}