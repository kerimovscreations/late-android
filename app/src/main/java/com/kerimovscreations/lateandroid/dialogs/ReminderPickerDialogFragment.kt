package com.kerimovscreations.lateandroid.dialogs

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.adapters.ReminderOptionRecyclerViewAdapter
import com.kerimovscreations.lateandroid.databinding.DialogReminderPickerBinding
import com.kerimovscreations.lateandroid.models.ReminderOption
import com.kerimovscreations.lateandroid.tools.HelpFunctions
import java.util.*

class ReminderPickerDialogFragment : DialogFragment() {

    companion object {
        const val EXTRA_MINUTES = "minutes"
        fun newInstance(minutes: Int) = ReminderPickerDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_MINUTES, minutes)
            }
        }
    }

    private lateinit var binding: DialogReminderPickerBinding

    private var minutes: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var options: ArrayList<ReminderOption> = arrayListOf()
    private lateinit var adapter: ReminderOptionRecyclerViewAdapter
    private var playingIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        minutes = arguments?.getInt(EXTRA_MINUTES) ?: 0
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.dialog_reminder_picker, container, false)

        options.add(ReminderOption(getString(R.string.mins_0), 0))

        if (minutes > 5) {
            options.add(ReminderOption(getString(R.string.mins_5), 5))
        }
        if (minutes > 10) {
            options.add(ReminderOption(getString(R.string.mins_10), 10))
        }
        if (minutes > 15) {
            options.add(ReminderOption(getString(R.string.mins_15), 15))
        }
        if (minutes > 20) {
            options.add(ReminderOption(getString(R.string.mins_20), 20))
        }
        if (minutes > 30) {
            options.add(ReminderOption(getString(R.string.mins_30), 30))
        }
        if (minutes > 60) {
            options.add(ReminderOption(getString(R.string.mins_60), 60))
        }

        adapter = ReminderOptionRecyclerViewAdapter(this.context!!, options)

        adapter.setOnInteractionListener(object : ReminderOptionRecyclerViewAdapter.OnInteractionListener {
            override fun onPlay(index: Int) {
                playSound(index)
            }
        })

        binding.rvOptions.adapter = adapter

        binding.btnSubmit.setOnClickListener {
            if (mListener != null) {
                mListener!!.onSubmit(options)
            }
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun dismiss() {
        super.dismiss()
        stopSound()
    }

    private fun stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    private fun playSound(index: Int) {
        stopSound()
        if (playingIndex >= 0) {
            options[playingIndex].isPlaying = false
            adapter.notifyItemChanged(playingIndex)
            if (playingIndex == index) {
                return
            }
        }
        if (options[index].isPlaying) {
            options[index].isPlaying = false
            adapter.notifyItemChanged(index)
            return
        } else {
            playingIndex = index
            options[index].isPlaying = true
            adapter.notifyItemChanged(index)
        }
        val resourceId = HelpFunctions.shared.getNotificationData(context, options[index].value).getInt("SOUND_ID", R.raw.en_male_mins_0_left)

        mediaPlayer = MediaPlayer.create(this.context!!, resourceId)
        mediaPlayer?.setOnCompletionListener {
            stopSound()
            options[index].isPlaying = false
            adapter.notifyItemChanged(index)
            playingIndex = -1
        }
        mediaPlayer?.start()
    }

    interface OnInteractionListener {
        fun onSubmit(options: ArrayList<ReminderOption>)
    }

    private var mListener: OnInteractionListener? = null
    fun setOnInteractionListener(listener: OnInteractionListener) {
        mListener = listener
    }

}