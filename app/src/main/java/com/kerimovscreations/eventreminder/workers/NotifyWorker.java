package com.kerimovscreations.eventreminder.workers;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.kerimovscreations.eventreminder.R;
import com.kerimovscreations.eventreminder.activities.MainActivity;

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

        return Worker.Result.SUCCESS;
    }

    void triggerNotification() {
        System.out.println("triggerNotification");

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        final int DBEventID = getInputData().getInt("EVENT_ID", 1);
        final int milestone = getInputData().getInt("MILESTONE", 0);

        Uri sound;

        switch (milestone) {
            case 0:
                sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.mins_0_left);
                break;
            case 5:
                sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.mins_5_left);
                break;
            case 10:
                sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.mins_10_left);
                break;
            case 20:
                sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.mins_20_left);
                break;
            case 30:
                sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.mins_30_left);
                break;
            default:
                sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.mins_0_left);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "B")
                .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                .setSound(null)
                .setContentTitle("It's time")
                .setContentText("It's time to complete meeting")
                .setPriority(NotificationCompat.PRIORITY_LOW)
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
