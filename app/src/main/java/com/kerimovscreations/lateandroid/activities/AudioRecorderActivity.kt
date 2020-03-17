package com.kerimovscreations.lateandroid.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.databinding.ActivityAudioRecorderBinding
import com.kerimovscreations.lateandroid.tools.BaseActivity
import java.io.IOException

class AudioRecorderActivity : BaseActivity() {

    companion object {
        const val RC_AUDIO_RECORD = 53
        const val EXTRA_AUDIO_PATH = "audio_path"
    }

    private lateinit var binding: ActivityAudioRecorderBinding

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var tempSoundFile: String = ""

    private var isRecording = false
        set(value) {
            field = value
            updateBtns()

            if (!value) {
                timer?.cancel()
            }
        }

    private var isPlaying = false
        set(value) {
            field = value

            if (!value) {
                timer?.cancel()
            }

            updateBtns()
        }

    private var timer: CountDownTimer? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_recorder)

        binding.btnRecord.setOnTouchListener { view, motionEvent ->
            view.performClick()

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRecording()
                }
                MotionEvent.ACTION_UP -> {
                    stopRecording()
                }
                MotionEvent.ACTION_CANCEL -> {
                    stopRecording()
                }
            }

            true
        }

        binding.btnPlay.setOnClickListener {
            if (!isPlaying) {
                startPlaying()
            } else {
                stopPlaying()
            }
        }

        binding.btnSelect.setOnClickListener {
            if (tempSoundFile.isEmpty())
                return@setOnClickListener

            val intent = Intent()
            intent.putExtra(EXTRA_AUDIO_PATH, tempSoundFile)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()

        stopRecording()
        stopPlaying()
    }

    override fun onDestroy() {
        super.onDestroy()

        stopPlaying()
        stopRecording()
    }

    /**
     * UI
     */

    private fun updateBtns() {
        if (isRecording) {
            binding.btnPlay.alpha = 0.5f
            binding.btnPlay.isClickable = false
            binding.btnRecord.scaleX = 1.1f
            binding.btnRecord.scaleY = 1.1f
        } else {
            binding.btnPlay.alpha = 1.0f
            binding.btnPlay.isClickable = true
            binding.btnRecord.scaleX = 1.0f
            binding.btnRecord.scaleY = 1.0f
        }

        if (isPlaying) {
            binding.btnRecord.alpha = 0.5f
            binding.btnRecord.isClickable = false
            binding.btnPlay.scaleX = 1.1f
            binding.btnPlay.scaleY = 1.1f
            binding.btnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pause_white_24dp))
        } else {
            binding.btnRecord.alpha = 1.0f
            binding.btnRecord.isClickable = true
            binding.btnPlay.scaleX = 1.0f
            binding.btnPlay.scaleY = 1.0f
            binding.btnPlay.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_24dp))
        }
    }

    /**
     * Methods
     */

    private fun startPlaying() {
        if (tempSoundFile.isEmpty() || isPlaying) {
            return
        }

        isPlaying = true

        player = MediaPlayer().apply {
            try {
                setDataSource(tempSoundFile)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("APP", "prepare() failed")
            }
        }

        player?.duration?.let { duration ->

            timer = object : CountDownTimer(duration.toLong(), 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    binding.timerText.text = "0:${String.format("%02d", (duration / 1000) - (millisUntilFinished / 1000))}"
                }

                override fun onFinish() {
                    binding.timerText.text = "0:00"
                    stopPlaying()
                }
            }

            timer?.start()
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        isPlaying = false
        timer?.cancel()
        timer = null
    }

    private fun startRecording() {
        if (!hasAudioRecordPermission(this)) {
            Toast.makeText(this, getString(R.string.you_don_t_have_proper_permission), Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO),
                    RC_AUDIO_RECORD)
            return
        }

        tempSoundFile = getRandomFilePath(this)
        isRecording = true

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setOutputFile(tempSoundFile)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "prepare() failed")
            }

            start()
        }

        timer = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.timerText.text = "0:${String.format("%02d", 30 - (millisUntilFinished / 1000))}"
            }

            override fun onFinish() {
                binding.timerText.text = "0:00"
                stopRecording()
            }
        }

        timer?.start()
    }

    private fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        isRecording = false
        timer?.cancel()
        timer = null
    }

    private fun hasAudioRecordPermission(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false
    }

    private fun getRandomFilePath(context: Context): String {
        val filename = "generated-${System.currentTimeMillis()}.aac"
        val dirPath = getAppLocalStoragePath(context)

        return "${dirPath}/${filename}"
    }

    private fun getAppLocalStoragePath(context: Context): String {
        val dir = context.getExternalFilesDir(null)

        return if (dir == null) {
            ""
        } else {
            dir.absolutePath
        }
    }
}
