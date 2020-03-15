package com.kerimovscreations.lateandroid.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.kerimovscreations.lateandroid.R
import java.util.*

class LocaleHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    fun setLocale(c: Context): Context {
        return updateResources(c, language)
    }

    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(language)
        return updateResources(c, language)
    }

    val language: String
        get() = prefs.getString(SELECTED_LANGUAGE, LANGUAGE_ENGLISH) ?: LANGUAGE_ENGLISH

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) {
        prefs.edit().putString(SELECTED_LANGUAGE, language).commit()
    }

    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }

    companion object {
        private const val SELECTED_LANGUAGE = "Locale.FingerPrintAuthHelper.Selected.Language"
        const val LANGUAGE_ENGLISH = "en"
    }

}