package com.kerimovscreations.lateandroid.dialogs

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.LinearLayoutManager
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.activities.AudioRecorderActivity
import com.kerimovscreations.lateandroid.adapters.CustomSoundRVAdapter
import com.kerimovscreations.lateandroid.application.GlobalApplication
import com.kerimovscreations.lateandroid.databinding.DialogCustomSoundsBinding
import com.kerimovscreations.lateandroid.models.CustomSound
import com.kerimovscreations.lateandroid.models.ReminderOption
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where


class CustomSoundsDialogFragment : DialogFragment() {

    companion object {
        const val RC_AUDIO_PICK = 23
        const val RC_RECORD_AUDIO = 24
        const val RC_READ_EXTERNAL = 25

        fun newInstance() = CustomSoundsDialogFragment()
    }

    private lateinit var binding: DialogCustomSoundsBinding

    private lateinit var adapter: CustomSoundRVAdapter

    private var soundList = arrayListOf<ReminderOption>()

    private val realm = Realm.getDefaultInstance()

    private var pickedAudioForValue = -1

    private var mediaPlayer: MediaPlayer? = null
    private var playingIndex = -1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.dialog_custom_sounds, container, false)

        binding.btnSubmit.setOnClickListener {
            listener?.onSubmit()
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        adapter = CustomSoundRVAdapter(soundList)
        adapter.setOnInteractionListener(object : CustomSoundRVAdapter.OnInteractionListener {
            override fun onAction(index: Int) {
                if (soundList[index].soundFile.isEmpty()) {
                    promptSoundPickerDialog(index)
                } else {
                    playSound(index)
                }
            }

            override fun onEdit(index: Int) {
                promptSoundPickerDialog(index)
            }
        })

        binding.rvOptions.layoutManager = LinearLayoutManager(this.context!!)
        binding.rvOptions.adapter = adapter

        if (!hasExternalStoragePermission(this.activity!!)) {
            Toast.makeText(this.activity!!, getString(R.string.you_don_t_have_proper_permission), Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RC_READ_EXTERNAL)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        updateList()
    }

    private fun promptSoundPickerDialog(index: Int) {
        val dialog = CustomSoundPickerDialogFragment.newInstance()
        dialog.listener = object : CustomSoundPickerDialogFragment.OnInteractionListener {
            override fun onPickFile() {
                pickAudioFile(index)
            }

            override fun onRecordAudio() {
                toRecordAudio(index)
            }

        }
        dialog.show(childFragmentManager, "")
    }

    private fun updateList() {
        val milestones = arrayOf(0, 5, 10, 20, 15, 30, 60)

        val lang = GlobalApplication.localeManager!!.language

        soundList.clear()

        soundList.add(ReminderOption(getString(R.string.mins_0), 0))
        soundList.add(ReminderOption(getString(R.string.mins_5), 5))
        soundList.add(ReminderOption(getString(R.string.mins_10), 10))
        soundList.add(ReminderOption(getString(R.string.mins_15), 15))
        soundList.add(ReminderOption(getString(R.string.mins_20), 20))
        soundList.add(ReminderOption(getString(R.string.mins_30), 30))
        soundList.add(ReminderOption(getString(R.string.mins_60), 60))

        milestones.forEach { value ->
            val cachedSoundFile = realm.where<CustomSound>()
                    .equalTo("lang", lang)
                    .equalTo("value", value)
                    .findFirst()?.soundFile

            soundList.find { item -> item.value == value }?.soundFile = cachedSoundFile ?: ""
        }

        adapter.notifyDataSetChanged()
    }

    private fun pickAudioFile(index: Int) {
        val activityRef = this.activity ?: return

        this.pickedAudioForValue = soundList[index].value

        val videoIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        activityRef.startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), RC_AUDIO_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_AUDIO_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data ?: return
                val path2 = getAudioPath(uri) ?: return
                if (pickedAudioForValue < 0) {
                    return
                }

                realm.executeTransaction {
                    val lang = GlobalApplication.localeManager!!.language

                    val cachedSoundFile = realm.where<CustomSound>()
                            .equalTo("lang", lang)
                            .equalTo("value", pickedAudioForValue)
                            .findFirst()

                    cachedSoundFile?.let {
                        it.soundFile = path2
                    } ?: run {
                        val soundObj = realm.createObject<CustomSound>()
                        soundObj.soundFile = path2
                        soundObj.lang = lang
                        soundObj.value = pickedAudioForValue
                    }
                }

                updateList()
            }
        } else if (requestCode == RC_RECORD_AUDIO) {
            if (resultCode == Activity.RESULT_OK) {
                val path = data?.getStringExtra(AudioRecorderActivity.EXTRA_AUDIO_PATH) ?: return
                if (pickedAudioForValue < 0) {
                    return
                }

                realm.executeTransaction {
                    val lang = GlobalApplication.localeManager!!.language

                    val cachedSoundFile = realm.where<CustomSound>()
                            .equalTo("lang", lang)
                            .equalTo("value", pickedAudioForValue)
                            .findFirst()

                    cachedSoundFile?.let {
                        it.soundFile = path
                    } ?: run {
                        val soundObj = realm.createObject<CustomSound>()
                        soundObj.soundFile = path
                        soundObj.lang = lang
                        soundObj.value = pickedAudioForValue
                    }
                }

                updateList()
            }
        }
    }

    private fun getAudioPath(uri: Uri): String? {
        val activityRef = this.activity ?: return null

        val data = arrayOf(MediaStore.Audio.Media.DATA)
        val loader = CursorLoader(activityRef.applicationContext, uri, data, null, null, null)
        val cursor = loader.loadInBackground() ?: return null

        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    override fun dismiss() {
        stopSound()
        super.dismiss()
    }

    private fun stopSound() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun playSound(index: Int) {
        val activityRef = this.activity ?: return

        if (!hasExternalStoragePermission(activityRef)) {
            Toast.makeText(activityRef, getString(R.string.you_don_t_have_proper_permission), Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(activityRef, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    RC_READ_EXTERNAL)
            return
        }

        stopSound()
        if (playingIndex >= 0) {
            soundList[playingIndex].isPlaying = false
            adapter.notifyItemChanged(playingIndex)
            if (playingIndex == index) {
                return
            }
        }
        if (soundList[index].isPlaying) {
            soundList[index].isPlaying = false
            adapter.notifyItemChanged(index)
            return
        } else {
            playingIndex = index
            soundList[index].isPlaying = true
            adapter.notifyItemChanged(index)
        }

        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(soundList[index].soundFile)
        mediaPlayer?.prepare()

        mediaPlayer?.setOnCompletionListener {
            stopSound()
            soundList[index].isPlaying = false
            adapter.notifyItemChanged(index)
            playingIndex = -1
        }
        mediaPlayer?.start()
    }

    private fun hasExternalStoragePermission(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false
    }

    private fun toRecordAudio(index: Int) {
        val activityRef = this.activity ?: return

        this.pickedAudioForValue = soundList[index].value

        val intent = Intent(activityRef, AudioRecorderActivity::class.java)
        activityRef.startActivityForResult(intent, RC_RECORD_AUDIO)
    }

    interface OnInteractionListener {
        fun onSubmit()
    }

    var listener: OnInteractionListener? = null
}