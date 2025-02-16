package com.kerimovscreations.lateandroid.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.models.ReminderOption
import com.kerimovscreations.lateandroid.databinding.ActivityMainBinding
import com.kerimovscreations.lateandroid.dialogs.GuidelinesDialogFragment
import com.kerimovscreations.lateandroid.dialogs.ReminderPickerDialogFragment
import com.kerimovscreations.lateandroid.dialogs.SettingsDialogFragment
import com.kerimovscreations.lateandroid.tools.BaseActivity
import com.kerimovscreations.lateandroid.tools.CircularSeekBar
import com.kerimovscreations.lateandroid.tools.HelpFunctions
import com.kerimovscreations.lateandroid.workers.NotifyWorker
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private var timer: CountDownTimer? = null
    private var mTimerDuration: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initVars()
    }

    private fun initVars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Reminders"
            val description = "Audio reminders based on user settings"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("B", name, importance)
            channel.description = description
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(null, null)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        checkRecentTimer()

        binding.btnPlay.setOnClickListener {
            if (timer != null) {
                stopTimer()
                deleteReminders()
            } else {
                if (hasNotificationPermission(this).not()) {
                    Toast.makeText(
                        this,
                        getString(R.string.warning_no_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    requestPushNotificationPermission()
                    return@setOnClickListener
                }

                val minutes = binding.circularSeekBar.progress
                if (minutes > 5) {
                    pickReminders(minutes)
                } else {
                    Toast.makeText(this, getString(R.string.warning_min_time), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.btnSettings.setOnClickListener {
            val fragment = SettingsDialogFragment.newInstance()
            fragment.show(supportFragmentManager, "settings")
        }

        binding.btnInfo.setOnClickListener {
            val fragment = GuidelinesDialogFragment.newInstance()
            fragment.show(supportFragmentManager, "guidelines")
        }

        binding.circularSeekBar.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                updateTimeText((progress * 60).toLong())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
    }

    /**
     * Functions
     */

    private fun checkRecentTimer() {
        if (HelpFunctions.shared.isRecentTimerActive(this)) {
            mTimerDuration = HelpFunctions.shared.getTimerDuration(this)
            val timerTimestamp = HelpFunctions.shared.getTimerStartTimestamp(this)
            val currentTimestamp = HelpFunctions.shared.currentTimestamp
            val millisUntilFinished = timerTimestamp + mTimerDuration - currentTimestamp
            startTimer(millisUntilFinished)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPushNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    private fun pickReminders(minutes: Int) {
        val dialogFragment = ReminderPickerDialogFragment.newInstance(minutes)
        dialogFragment.setOnInteractionListener(object :
            ReminderPickerDialogFragment.OnInteractionListener {
            override fun onSubmit(options: ArrayList<ReminderOption>) {
                mTimerDuration = minutes * 60000.toLong()
                setReminders(mTimerDuration, options)
                HelpFunctions.shared.setTimerStartTimestamp(
                    this@MainActivity,
                    HelpFunctions.shared.currentTimestamp
                )
                HelpFunctions.shared.setTimerDuration(this@MainActivity, mTimerDuration)
                startTimer(mTimerDuration)
            }
        })

        dialogFragment.show(supportFragmentManager, "")
    }

    private fun startTimer(duration: Long) {
        binding.btnPlay.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_stop_white_24dp,
                null
            )
        )
        binding.circularSeekBar.isTouchEnabled = false
        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimeText(millisUntilFinished / 1000, true)
            }

            override fun onFinish() {
                stopTimer()
            }
        }
        timer?.start()
    }

    private fun updateTimeText(seconds: Long, shouldUpdateSeekBar: Boolean = false) {
        binding.timerTimeMin.text = String.format(
            Locale.getDefault(), "%d %02d",
            seconds / 3600 % 24,
            seconds / 60 % 60
        )
        binding.timerTimeSec.text = String.format(
            Locale.getDefault(), "%02d",
            seconds % 60
        )

        if (shouldUpdateSeekBar) {
            binding.circularSeekBar.progress = (seconds / 60).toInt()
        }
    }

    @SuppressLint("SetTextI18n")
    fun stopTimer() {
        timer?.cancel()
        timer = null
        binding.btnPlay.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_play_arrow_white_24dp,
                null
            )
        )
        binding.timerTimeMin.text = "0 00"
        binding.timerTimeSec.text = "00"
        binding.circularSeekBar.progress = 0
        HelpFunctions.shared.setTimerStartTimestamp(this, 0)
        binding.circularSeekBar.isTouchEnabled = true
    }

    private fun setReminders(duration: Long, options: ArrayList<ReminderOption>) {
        for (option in options) {
            if (!option.isSelected) {
                continue
            }
            setNotification(duration, option.value)
        }
    }

    private fun deleteReminders() {
        WorkManager.getInstance(this).cancelAllWorkByTag(TAG_WORK)
    }

    private fun setNotification(duration: Long, value: Int) {
        val inputData = HelpFunctions.shared.getNotificationData(this, value)
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
            .setInitialDelay(duration - value * 60000, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag(TAG_WORK)
            .build()
        WorkManager.getInstance(this).enqueue(notificationWork)
    }

    companion object {
        const val TAG_WORK = "notificationWork"
    }
}