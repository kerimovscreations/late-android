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
import com.kerimovscreations.lateandroid.application.GlobalApplication;
import com.kerimovscreations.lateandroid.enums.SoundType;
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
    private TextView mSubmitBtn;

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
        mSubmitBtn = findViewById(R.id.btn_submit);
        updateTexts();
    }

    private void updateTexts() {
        mTitle.setText(mContext.getString(R.string.settings));
        mLanguageTitle.setText(mContext.getString(R.string.language));
        mLanguageText.setText(mContext.getString(R.string.current_language));
        mSoundTitle.setText(mContext.getString(R.string.sound));

        SoundType soundType = SoundType.values()[HelpFunctions.shared.getSoundType(mContext)];

        int resId;
        switch (soundType) {
            case MALE_NORMAL:
                resId = R.string.male;
                break;
            case FEMALE_NORMAL:
                resId = R.string.female;
                break;
            case MALE_FUNNY_1:
                resId = R.string.male_fun_1;
                break;
            case FEMALE_FUNNY_1:
                resId = R.string.female_fun_1;
                break;
            case MALE_FUNNY_2:
                resId = R.string.male_fun_2;
                break;
            case FEMALE_FUNNY_2:
                resId = R.string.female_fun_2;
                break;
            default:
                resId = R.string.male;
        }

        mSoundText.setText(mContext.getString(resId));

        mSubmitBtn.setText(mContext.getString(R.string.submit));
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
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext, R.style.AppThemeAlertDialog);
        CharSequence[] items = new CharSequence[]{"English", "Русский"};
        final int[] selectedOption = {-1};

        if (GlobalApplication.localeManager.getLanguage().equals("en")) {
            selectedOption[0] = 0;
        } else if (GlobalApplication.localeManager.getLanguage().equals("ru")) {
            selectedOption[0] = 1;
        } else {
            selectedOption[0] = 0;
        }

        adb.setSingleChoiceItems(items, selectedOption[0], (dialog, which) -> selectedOption[0] = which);
        adb.setPositiveButton(mContext.getString(R.string.save), (dialog, which) -> {
            switch (selectedOption[0]) {
                case 0:
                    GlobalApplication.localeManager.setNewLocale(mContext, "en");
                    HelpFunctions.shared.setUserLanguage(mContext, "en");
                    updateTexts();
                    break;
                case 1:
                    GlobalApplication.localeManager.setNewLocale(mContext, "ru");
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
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext, R.style.AppThemeAlertDialog);
        CharSequence[] items;

        if (GlobalApplication.localeManager.getLanguage().equals("en")) {
            items = new CharSequence[]{mContext.getString(R.string.male), mContext.getString(R.string.female)};
        } else {
            items = new CharSequence[]{mContext.getString(R.string.male), mContext.getString(R.string.female),
                    mContext.getString(R.string.male_fun_1), mContext.getString(R.string.female_fun_1),
                    mContext.getString(R.string.male_fun_2), mContext.getString(R.string.female_fun_2)};
        }

        final int[] selectedOption = {-1};

        SoundType soundType = SoundType.values()[HelpFunctions.shared.getSoundType(mContext)];
        selectedOption[0] = soundType.getValue();

        adb.setSingleChoiceItems(items, selectedOption[0], (dialog, which) -> selectedOption[0] = which);
        adb.setPositiveButton(mContext.getString(R.string.save), (dialog, which) -> {
            HelpFunctions.shared.setSoundType(mContext, selectedOption[0]);
            updateTexts();
        });
        adb.setNegativeButton(mContext.getString(R.string.cancel), null);
        adb.setTitle(mContext.getString(R.string.select_sound));
        adb.show();
    }
}
