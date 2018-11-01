package com.kerimovscreations.eventreminder.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kerimovscreations.eventreminder.R;
import com.kerimovscreations.eventreminder.adapters.EventListRVAdapter;
import com.kerimovscreations.eventreminder.dialogs.PickerDialogFragment;
import com.kerimovscreations.eventreminder.models.Event;
import com.kerimovscreations.eventreminder.viewModel.EventViewModel;
import com.kerimovscreations.eventreminder.workers.NotifyWorker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fabAdd)
    FloatingActionButton mFabAdd;
    @BindView(R.id.rvEventList)
    RecyclerView mRVEvents;

    private EventListRVAdapter adapter;
    private EventViewModel mEventViewModel;

    public static final String workTag = "notificationWork";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initVars();

//        MediaPlayer mp= MediaPlayer.create(getApplicationContext(), R.raw.mins_0_left);
//        mp.start();
    }

    void initVars() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Meeting reminder");

        adapter = new EventListRVAdapter(this);
        adapter.setOnItemClickListener(new EventListRVAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

            }

            @Override
            public void onLongClick(int position) {
                showDeleteEventDialog(position);
            }
        });

        mRVEvents.setAdapter(adapter);
        mRVEvents.setLayoutManager(new LinearLayoutManager(this));

        mFabAdd.setOnClickListener(view -> {
            showAddEventSheet();
        });

        mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);

        mEventViewModel.getAllEvents().observe(this, events -> {
            adapter.setEvents(events);
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminders";
            String description = "Reminders for time periods of meetings";
            int importance = NotificationManager.IMPORTANCE_LOW;
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
    }

    int calculateDelay(Event event) {
        DateFormat out = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        Log.e("ERR33", out.format(new Date()));
        Log.e("ERR33", out.format(event.getDateObj()));

        int result = (int) ((event.getDateObj().getTime() - (new Date()).getTime()) / 60000);

        Log.e("ERR33", String.valueOf(result));

        if (result < 0) {
            return 0;
        } else {
            return result;
        }
    }

    void showAddEventSheet() {
        final BottomSheetDialog bs = new BottomSheetDialog(this);
        View sheet = getLayoutInflater().inflate(R.layout.dialog_event_form, null);
        bs.setContentView(sheet);

//        TextInputEditText editText = sheet.findViewById(R.id.dialog_event_title);
        TextView dateText = sheet.findViewById(R.id.dialog_event_date_text);
        TextView durationText = sheet.findViewById(R.id.dialog_event_duration_text);
        final Date[] selectedDate = new Date[1];
        final int[] duration_mins = {0};

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timePicker, hours, mins) -> {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            now.set(Calendar.HOUR_OF_DAY, hours);
            now.set(Calendar.MINUTE, mins);
            selectedDate[0] = now.getTime();
            dateText.setText(hours + ":" + mins);
        }, 0, 0, true);


        sheet.findViewById(R.id.dialog_event_date_layout).setOnClickListener(view ->
                timePickerDialog.show());

        sheet.findViewById(R.id.dialog_event_duration_layout).setOnClickListener(view -> {
            PickerDialogFragment dialogFragment = new PickerDialogFragment();
            dialogFragment.setOnResultListener(duration -> {
                duration_mins[0] = (int) (duration / 60000);
                durationText.setText(duration_mins[0] + " minutes");
            });

            dialogFragment.show(getFragmentManager(), "dialog");
        });

        sheet.findViewById(R.id.dialog_event_submit_btn).setOnClickListener(v -> {
            if (selectedDate[0] != null
                    && duration_mins[0] >= 20) {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                Event event = new Event(df.format(selectedDate[0]), duration_mins[0]);
                Log.d("ERR12", df.format(selectedDate[0]));
                mEventViewModel.insert(event);
                notificationSet(event);
                bs.cancel();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please, fill all the fields",
                        Toast.LENGTH_LONG).show();
            }
        });

        bs.setCancelable(true);
        bs.show();
    }

    private void showDeleteEventDialog(int position) {
        final BottomSheetDialog bsh = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_delete_confirm, null);
        bsh.setContentView(sheetView);
        bsh.show();

        sheetView.findViewById(R.id.confirm_cancel_sure).setOnClickListener(view -> {
            mEventViewModel.delete(position);
            bsh.cancel();
        });
        sheetView.findViewById(R.id.confirm_cancel_cancel).setOnClickListener(view -> bsh.cancel());
    }

    void notificationSet(Event event) {
        Data inputData = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 0).build();

        // end
        int delayMSeconds = calculateDelay(event) + event.getDuration_mins();
        Log.e("ERR33", String.valueOf(delayMSeconds));
        if (delayMSeconds > 0) {
            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(delayMSeconds, TimeUnit.MINUTES)
                    .setInputData(inputData)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance().enqueue(notificationWork);
        }

        // 5 left
        Data inputData1 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 5).build();

        int delayMins1 = calculateDelay(event) + event.getDuration_mins() - 5;
        Log.e("ERR33", String.valueOf(delayMins1));
        if (delayMins1 > 0) {
            OneTimeWorkRequest notificationWork1 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(delayMins1, TimeUnit.MINUTES)
                    .setInputData(inputData1)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance().enqueue(notificationWork1);
        }

        // 10 left
        Data inputData2 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 10).build();

        int delayMins2 = calculateDelay(event) + event.getDuration_mins() - 10;
        Log.e("ERR33", String.valueOf(delayMins2));
        if (delayMins2 > 0) {
            OneTimeWorkRequest notificationWork2 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(delayMins2, TimeUnit.MINUTES)
                    .setInputData(inputData2)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance().enqueue(notificationWork2);
        }

        // 20 left
        Data inputData3 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 20).build();

        int delayMins3 = calculateDelay(event) + event.getDuration_mins() - 20;
        Log.e("ERR33", String.valueOf(delayMins3));
        if (delayMins3 > 0) {
            OneTimeWorkRequest notificationWork3 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(delayMins3, TimeUnit.MINUTES)
                    .setInputData(inputData3)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance().enqueue(notificationWork3);
        }

        // 30 left
        Data inputData4 = new Data.Builder().putInt("EVENT_ID", event.getId()).putInt("MILESTONE", 30).build();

        int delayMins4 = calculateDelay(event) + event.getDuration_mins() - 30;
        Log.e("ERR33", String.valueOf(delayMins4));
        if (delayMins4 > 0) {
            OneTimeWorkRequest notificationWork4 = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setInitialDelay(delayMins4, TimeUnit.MINUTES)
                    .setInputData(inputData4)
                    .addTag(workTag)
                    .build();

            WorkManager.getInstance().enqueue(notificationWork4);
        }
    }
}

