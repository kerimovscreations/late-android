package com.kerimovscreations.lateandroid.activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.dialogs.ReminderPickerDialogFragment;
import com.kerimovscreations.lateandroid.models.ReminderOption;
import com.kerimovscreations.lateandroid.tools.HelpFunctions;
import com.kerimovscreations.lateandroid.workers.NotifyWorker;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String workTag = "notificationWork";

    // Views

    @BindView(R.id.timer_circle)
    ImageView mTimerCircleImage;
    @BindView(R.id.btn_play)
    ImageView mPlayBtn;
    @BindView(R.id.timer_time_min)
    TextView mTimerTimeText;
    @BindView(R.id.timer_time_sec)
    TextView mTimerSecondText;

    // Variables

    private CountDownTimer timer;
    private long mTimerDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initVars();
    }

    void initVars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminders";
            String description = "Reminders for time periods of meetings";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("B", name, importance);
            channel.setDescription(description);

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(null, null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        checkRecentTimer();
    }

    void checkRecentTimer() {
        if (HelpFunctions.shared.isRecentTimerActive(this)) {
            mTimerDuration = HelpFunctions.shared.getTimerDuration(this);
            long timerTimestamp = HelpFunctions.shared.getTimerStartTimestamp(this);
            long currentTimestamp = HelpFunctions.shared.getCurrentTimestamp();
            long millisUntilFinished = timerTimestamp + mTimerDuration - currentTimestamp;
            startTimer(millisUntilFinished);
            startTimerCircleAnimation(millisUntilFinished);
        }
    }

    /* Click handlers */

    @OnClick(R.id.btn_play)
    void onPlay() {
        if (this.timer != null) {
            stopTimer();
            deleteReminders();
        } else {
            pickTime();
        }
    }

    /* Functions */

    void pickTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (timePicker, hours, minutes) -> pickReminders(hours * 60 + minutes),
                0, 0, true);

        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_title, null);
        timePickerDialog.setCustomTitle(view);
        timePickerDialog.show();
    }

    void pickReminders(int minutes) {
        if (minutes > 0) {
            ReminderPickerDialogFragment dialogFragment = new ReminderPickerDialogFragment(this, minutes);
            dialogFragment.setOnInteractionListener(options -> {
                mTimerDuration = minutes * 60000;

                setReminders(mTimerDuration, options);

                HelpFunctions.shared.setTimerStartTimestamp(this, HelpFunctions.shared.getCurrentTimestamp());
                HelpFunctions.shared.setTimerDuration(this, mTimerDuration);

                startTimer(mTimerDuration);
                startTimerCircleAnimation(mTimerDuration);
            });
            dialogFragment.show();
        }
    }

    void startTimer(long duration) {
        mPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop_white_24dp, null));

        timer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                mTimerTimeText.setText(String.format(Locale.getDefault(), "%d %02d",
                        millisUntilFinished / 3600000 % 24,
                        millisUntilFinished / 60000 % 60));
                mTimerSecondText.setText(String.format(Locale.getDefault(), "%02d",
                        millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                stopTimer();
            }
        };

        timer.start();
    }

    void startTimerCircleAnimation(long millisUntilFinished) {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(
                (float) ((double) (mTimerDuration - millisUntilFinished) / (double) mTimerDuration) * 178.0f,
                178.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(mTimerDuration);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        mTimerCircleImage.startAnimation(animSet);
    }

    @SuppressLint("SetTextI18n")
    void stopTimer() {
        this.timer.cancel();
        this.timer = null;
        this.mPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_arrow_white_24dp, null));
        mTimerTimeText.setText("0 00");
        mTimerSecondText.setText("00");
        mTimerCircleImage.clearAnimation();
        HelpFunctions.shared.setTimerStartTimestamp(this, 0);
    }

    void setReminders(long duration, ArrayList<ReminderOption> options) {
        for (ReminderOption option : options) {
            if (!option.isSelected()) {
                continue;
            }

            setNotification(duration, option.getValue());
        }
    }

    void deleteReminders() {
        WorkManager.getInstance().cancelAllWorkByTag(workTag);
    }

    void setNotification(long duration, int value) {
        Data inputData = HelpFunctions.shared.getNotificationData(this, value);

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                .setInitialDelay(duration - (value * 60000), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(workTag)
                .build();

        WorkManager.getInstance().enqueue(notificationWork);
    }
}

