package com.kerimovscreations.lateandroid.activities

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.databinding.ActivityMainBinding
import com.kerimovscreations.lateandroid.dialogs.CustomSoundsDialogFragment
import com.kerimovscreations.lateandroid.dialogs.GuidelinesDialogFragment
import com.kerimovscreations.lateandroid.dialogs.ReminderPickerDialogFragment
import com.kerimovscreations.lateandroid.dialogs.SettingsDialogFragment
import com.kerimovscreations.lateandroid.enums.SoundType
import com.kerimovscreations.lateandroid.models.ReminderOption
import com.kerimovscreations.lateandroid.tools.BaseActivity
import com.kerimovscreations.lateandroid.tools.CircularSeekBar
import com.kerimovscreations.lateandroid.tools.HelpFunctions
import com.kerimovscreations.lateandroid.workers.NotifyWorker
import io.realm.Realm
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private var timer: CountDownTimer? = null
    private var mTimerDuration: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Realm.init(this)

        initVars()
    }

    private fun initVars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Reminders"
            val description = "Reminders for time periods of meetings"
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
                val minutes = binding.circularSeekBar.progress
                if (minutes > 5) {
                    pickReminders(minutes)
                } else {
                    Toast.makeText(this, getString(R.string.warning_min_time), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnSettings.setOnClickListener {
            val fragment = SettingsDialogFragment.newInstance()
            fragment.listener = object: SettingsDialogFragment.OnInteractionListener {
                override fun onCustomSoundPicker() {
                    promptCustomSoundsDialog()
                }
            }

            fragment.show(supportFragmentManager, "")
        }

        binding.btnInfo.setOnClickListener {
            val fragment = GuidelinesDialogFragment.newInstance()
            fragment.show(supportFragmentManager, "")
        }

        binding.circularSeekBar.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Int, fromUser: Boolean) {
                updateTimeText((progress * 60).toLong())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {

            }
        })
    }

    private fun checkRecentTimer() {
        if (HelpFunctions.shared.isRecentTimerActive(this)) {
            mTimerDuration = HelpFunctions.shared.getTimerDuration(this)
            val timerTimestamp = HelpFunctions.shared.getTimerStartTimestamp(this)
            val currentTimestamp = HelpFunctions.shared.currentTimestamp
            val millisUntilFinished = timerTimestamp + mTimerDuration - currentTimestamp
            startTimer(millisUntilFinished)
        }
    }

    /**
     * Functions
     */

    private fun pickReminders(minutes: Int) {
        val dialogFragment = ReminderPickerDialogFragment.newInstance(minutes)
        dialogFragment.setOnInteractionListener(object : ReminderPickerDialogFragment.OnInteractionListener {
            override fun onSubmit(options: ArrayList<ReminderOption>) {
                mTimerDuration = minutes * 60000.toLong()
                setReminders(mTimerDuration, options)
                HelpFunctions.shared.setTimerStartTimestamp(this@MainActivity, HelpFunctions.shared.currentTimestamp)
                HelpFunctions.shared.setTimerDuration(this@MainActivity, mTimerDuration)
                startTimer(mTimerDuration)
            }
        })

        dialogFragment.show(supportFragmentManager, "")
    }

    private fun startTimer(duration: Long) {
        binding.btnPlay.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_pause_white_24dp, null))
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
        binding.timerTimeMin.text = String.format(Locale.getDefault(), "%d %02d",
                seconds / 3600 % 24,
                seconds / 60 % 60)
        binding.timerTimeSec.text = String.format(Locale.getDefault(), "%02d",
                seconds % 60)

        if (shouldUpdateSeekBar) {
            binding.circularSeekBar.progress = (seconds / 60).toInt()
        }
    }

    @SuppressLint("SetTextI18n")
    fun stopTimer() {
        timer!!.cancel()
        timer = null
        binding.btnPlay.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_play_arrow_white_24dp, null))
        binding.timerTimeMin.text = "0 00"
        binding.timerTimeSec.text = "00"
        binding.circularSeekBar.progress = 0
        HelpFunctions.shared.setTimerStartTimestamp(this, 0)
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
        WorkManager.getInstance().cancelAllWorkByTag(workTag)
    }

    private fun setNotification(duration: Long, value: Int) {
        val inputData = HelpFunctions.shared.getNotificationData(this, value)
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                .setInitialDelay(duration - value * 60000, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(workTag)
                .build()
        WorkManager.getInstance().enqueue(notificationWork)
    }

    private fun promptCustomSoundsDialog() {
        val dialog = CustomSoundsDialogFragment.newInstance()

        dialog.listener = object: CustomSoundsDialogFragment.OnInteractionListener {
            override fun onSubmit() {
                HelpFunctions.shared.setSoundType(this@MainActivity, SoundType.CUSTOM.value)
            }
        }

        dialog.show(supportFragmentManager, "")
    }

    companion object {
        const val workTag = "notificationWork"
    }
}