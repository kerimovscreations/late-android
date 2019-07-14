package com.kerimovscreations.lateandroid.workers;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.kerimovscreations.lateandroid.R;
import com.kerimovscreations.lateandroid.activities.MainActivity;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotifyWorker extends Worker {

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {
        triggerNotification();

        return Worker.Result.success();
    }

    private void triggerNotification() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        final int DBEventID = getInputData().getInt("EVENT_ID", 1);

        int soundId = getInputData().getInt("SOUND_ID", R.raw.en_male_mins_0_left);
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + soundId);
        String title = getInputData().getString("TITLE");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "B")
                .setSmallIcon(R.drawable.ic_stat_icon)
                .setSound(null)
                .setContentTitle("LATE")
                .setContentText(title)
                .setVibrate(new long[] {0, 250, 250, 250})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        try {
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), sound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationManager.notify(DBEventID, mBuilder.build());
    }
}
