package com.kerimovscreations.lateandroid.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.tools.HelpFunctions;
import com.kerimovscreations.lateandroid.tools.LocaleHelper;

import java.util.Objects;

public class SettingsDialogFragment extends Dialog implements
        android.view.View.OnClickListener {

    private Context mContext;
    private TextView mLanguageTitle;
    private TextView mLanguageText;
    private TextView mSoundTitle;
    private TextView mSoundText;
    private TextView mTitle;

    public SettingsDialogFragment(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.language_view).setOnClickListener(this);
        findViewById(R.id.sound_view).setOnClickListener(this);

        mTitle = findViewById(R.id.title);
        mLanguageTitle = findViewById(R.id.language_title);
        mLanguageText = findViewById(R.id.language_text);
        mSoundTitle = findViewById(R.id.sound_title);
        mSoundText = findViewById(R.id.sound_text);
    }

    private void updateTexts() {
        mTitle.setText(R.string.settings);
        mLanguageTitle.setText(R.string.language);
        mLanguageText.setText(R.string.current_language);
        mSoundTitle.setText(R.string.sound);
        mSoundText.setText(R.string.male);
    }

    /* Click handlers */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                dismiss();
                break;
            case R.id.language_view:
                onLanguage();
                break;
            case R.id.sound_view:
                onSound();
                break;
            default:
                break;
        }
    }

    private void onLanguage() {
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        CharSequence[] items = new CharSequence[]{"English", "Русский"};
        final int[] selectedOption = {-1};

        if (LocaleHelper.getLanguage(mContext).equals("en")) {
            selectedOption[0] = 0;
        } else if (LocaleHelper.getLanguage(mContext).equals("ru")) {
            selectedOption[0] = 1;
        }

        adb.setSingleChoiceItems(items, selectedOption[0], (dialog, which) -> selectedOption[0] = which);
        adb.setPositiveButton(mContext.getString(R.string.save), (dialog, which) -> {
            switch (selectedOption[0]) {
                case 0:
                    LocaleHelper.setLocale(mContext, "en");
                    HelpFunctions.shared.setUserLanguage(mContext, "en");
                    updateTexts();
                    break;
                case 1:
                    LocaleHelper.setLocale(mContext, "ru");
                    HelpFunctions.shared.setUserLanguage(mContext, "ru");
                    updateTexts();
                    break;
                default:
                    break;
            }
        });
        adb.setNegativeButton(mContext.getString(R.string.cancel), null);
        adb.setTitle(mContext.getString(R.string.select_language));
        adb.show();
    }

    private void onSound() {

    }
}
