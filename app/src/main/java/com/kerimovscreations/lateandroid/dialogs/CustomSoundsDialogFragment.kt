package com.kerimovscreations.lateandroid.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.adapters.CustomSoundRVAdapter
import com.kerimovscreations.lateandroid.application.GlobalApplication
import com.kerimovscreations.lateandroid.databinding.DialogCustomSoundsBinding
import com.kerimovscreations.lateandroid.models.CustomSound
import com.kerimovscreations.lateandroid.models.ReminderOption
import io.realm.Realm
import io.realm.kotlin.where

class CustomSoundsDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = CustomSoundsDialogFragment().apply {
        }
    }

    private lateinit var binding: DialogCustomSoundsBinding

    private lateinit var adapter: CustomSoundRVAdapter

    private var soundList = arrayListOf<ReminderOption>()

    private val realm = Realm.getDefaultInstance()

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
                val dialog = CustomSoundPickerDialogFragment.newInstance()
                dialog.show(childFragmentManager, "")
            }
        })

        binding.rvOptions.layoutManager = LinearLayoutManager(this.context!!)
        binding.rvOptions.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        updateList()
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

    interface OnInteractionListener {
        fun onSubmit()
    }

    var listener: OnInteractionListener? = null
}