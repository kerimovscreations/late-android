package com.kerimovscreations.lateandroid.workers

import android.Manifest
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.activities.MainActivity

class NotifyWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        triggerNotification()
        return Result.success()
    }

    private fun triggerNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val eventId = inputData.getInt("EVENT_ID", 1)

        val customSoundUrl = (inputData.getString("SOUND_URL") ?: "")

        val sound = if (customSoundUrl.isNotEmpty()) {
            if (hasExternalStoragePermission(applicationContext)) {
                Uri.parse(customSoundUrl)
            } else {
                val soundId = R.raw.en_male_mins_0_left
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + soundId)
            }
        } else {
            val soundId = inputData.getInt("SOUND_ID", R.raw.en_male_mins_0_left)
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + soundId)
        }

        val title = inputData.getString("TITLE")
        val mBuilder = NotificationCompat.Builder(applicationContext, "B")
            .setSmallIcon(R.drawable.ic_stat_icon)
            .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .setSound(null)
            .setContentTitle("LATE")
            .setContentText(title)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        try {
            val r = RingtoneManager.getRingtone(applicationContext, sound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(eventId, mBuilder.build())
    }

    private fun hasExternalStoragePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false
    }
}