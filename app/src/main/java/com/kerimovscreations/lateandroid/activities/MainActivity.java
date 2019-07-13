package com.kerimovscreations.lateandroid.activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.tools.HelpFunctions;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

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
//                List<NotificationChannel> channelList = notificationManager.getNotificationChannels();

//                for (int i = 0; channelList != null && i < channelList.size(); i++) {
//                    notificationManager.deleteNotificationChannel(channelList.get(i).getId());
//                }

                notificationManager.createNotificationChannel(channel);
            }
        }

        checkRecentTimer();
    }

    void checkRecentTimer() {
        if(HelpFunctions.shared.isRecentTimerActive(this)) {
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
        Log.e("ERR33", String.valueOf(minutes));
        Log.e("ERR33", HelpFunctions.shared.getCurrentLanguageCode());

        mTimerDuration = minutes * 60000;

        HelpFunctions.shared.setTimerStartTimestamp(this, HelpFunctions.shared.getCurrentTimestamp());
        HelpFunctions.shared.setTimerDuration(this, mTimerDuration);

        startTimer(mTimerDuration);
        startTimerCircleAnimation(mTimerDuration);
    }

    void startTimer(long duration) {
        mPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop_white_24dp, null));

        timer = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                mTimerTimeText.setText(String.format(Locale.getDefault(), "%d %d",
                        millisUntilFinished / 3600000 % 24,
                        millisUntilFinished / 60000 % 60));
                mTimerSecondText.setText(String.format(Locale.getDefault(), "%d",
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
        deleteReminders();
        this.mPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_play_arrow_white_24dp, null));
        mTimerTimeText.setText("0 00");
        mTimerSecondText.setText("00");
        mTimerCircleImage.clearAnimation();
        HelpFunctions.shared.setTimerStartTimestamp(this, 0);
    }

    void deleteReminders() {

    }

//    int calculateDelay(Event event) {
//        DateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
//
//        Log.e("ERR33", out.format(new Date()));
//        Log.e("ERR33", out.format(event.getDateObj()));
//
//        int result = (int) ((event.getDateObj().getTime() - (new Date()).getTime()) / 60000);
//
//        Log.e("ERR33", String.valueOf(result));
//
//        if (result < 0) {
//            return 0;
//        } else {
//            return result;
//        }
//    }

    void showAddEventSheet() {

//        TextInputEditText editText = sheet.findViewById(R.id.dialog_event_title);
//        TextView dateText = sheet.findViewById(R.id.dialog_event_date_text);
//        TextView durationText = sheet.findViewById(R.id.dialog_event_duration_text);
//        final Date[] selectedDate = new Date[1];
//        final int[] duration_mins = {0};
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hours, mins) -> {
//            Calendar now = Calendar.getInstance();
//            now.setTime(new Date());
//            now.set(Calendar.HOUR_OF_DAY, hours);
//            now.set(Calendar.MINUTE, mins);
//            selectedDate[0] = now.getTime();
//            dateText.setText(hours + ":" + mins);
//        }, 0, 0, true);
//
//
//        sheet.findViewById(R.id.dialog_event_date_layout).setOnClickListener(view ->
//                timePickerDialog.show());
//
//        sheet.findViewById(R.id.dialog_event_duration_layout).setOnClickListener(view -> {
//            PickerDialogFragment dialogFragment = new PickerDialogFragment();
//            dialogFragment.setOnResultListener(duration -> {
//                duration_mins[0] = (int) (duration / 60000);
//                durationText.setText(duration_mins[0] + " minutes");
//            });
//
//            dialogFragment.show(getFragmentManager(), "dialog");
//        });
//
//        sheet.findViewById(R.id.dialog_event_submit_btn).setOnClickListener(v -> {
////            if (selectedDate[0] != null
////                    && duration_mins[0] >= 20) {
////                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
////
////                Event event = new Event(df.format(selectedDate[0]), duration_mins[0]);
////                Log.d("ERR12", df.format(selectedDate[0]));
////                mEventViewModel.insert(event);
////                notificationSet(event);
////                bs.cancel();
////            } else {
////                Toast.makeText(
////                        getApplicationContext(),
////                        "Please, fill all the fields",
////                        Toast.LENGTH_LONG).show();
////            }
//        });
//
//        bs.setCancelable(true);
//        bs.show();
    }

    private void showDeleteEventDialog(int position) {
//        final BottomSheetDialog bsh = new BottomSheetDialog(this);
//        View sheetView = getLayoutInflater().inflate(R.layout.dialog_delete_confirm, null);
//        bsh.setContentView(sheetView);
//        bsh.show();
//
//        sheetView.findViewById(R.id.confirm_cancel_sure).setOnClickListener(view -> {
//            bsh.cancel();
//        });
//        sheetView.findViewById(R.id.confirm_cancel_cancel).setOnClickListener(view -> bsh.cancel());
    }

//    void notificationSet(Event event) {
//        Data inputData = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 0).build();
//
//        // end
//        int delayMSeconds = calculateDelay(event) + event.getDuration_mins();
//        Log.e("ERR33", String.valueOf(delayMSeconds));
//        if (delayMSeconds > 0) {
//            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
//                    .setInitialDelay(delayMSeconds, TimeUnit.MINUTES)
//                    .setInputData(inputData)
//                    .addTag(workTag)
//                    .build();
//
//            WorkManager.getInstance().enqueue(notificationWork);
//        }
//
//        // 5 left
//        Data inputData1 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 5).build();
//
//        int delayMins1 = calculateDelay(event) + event.getDuration_mins() - 5;
//        Log.e("ERR33", String.valueOf(delayMins1));
//        if (delayMins1 > 0) {
//            OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
//                    .setInitialDelay(delayMins1, TimeUnit.MINUTES)
//                    .setInputData(inputData1)
//                    .addTag(workTag)
//                    .build();
//
//            WorkManager.getInstance().enqueue(notificationWork1);
//        }
//
//        // 10 left
//        Data inputData2 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 10).build();
//
//        int delayMins2 = calculateDelay(event) + event.getDuration_mins() - 10;
//        Log.e("ERR33", String.valueOf(delayMins2));
//        if (delayMins2 > 0) {
//            OneTimeWorkRequest notificationWork2 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
//                    .setInitialDelay(delayMins2, TimeUnit.MINUTES)
//                    .setInputData(inputData2)
//                    .addTag(workTag)
//                    .build();
//
//            WorkManager.getInstance().enqueue(notificationWork2);
//        }
//
//        // 20 left
//        Data inputData3 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 20).build();
//
//        int delayMins3 = calculateDelay(event) + event.getDuration_mins() - 20;
//        Log.e("ERR33", String.valueOf(delayMins3));
//        if (delayMins3 > 0) {
//            OneTimeWorkRequest notificationWork3 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
//                    .setInitialDelay(delayMins3, TimeUnit.MINUTES)
//                    .setInputData(inputData3)
//                    .addTag(workTag)
//                    .build();
//
//            WorkManager.getInstance().enqueue(notificationWork3);
//        }
//
//        // 30 left
//        Data inputData4 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 30).build();
//
//        int delayMins4 = calculateDelay(event) + event.getDuration_mins() - 30;
//        Log.e("ERR33", String.valueOf(delayMins4));
//        if (delayMins4 > 0) {
//            OneTimeWorkRequest notificationWork4 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
//                    .setInitialDelay(delayMins4, TimeUnit.MINUTES)
//                    .setInputData(inputData4)
//                    .addTag(workTag)
//                    .build();
//
//            WorkManager.getInstance().enqueue(notificationWork4);
//        }
//    }
}

