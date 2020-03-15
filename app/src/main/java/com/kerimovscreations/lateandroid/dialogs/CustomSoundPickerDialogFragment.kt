package com.kerimovscreations.lateandroid.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.databinding.DialogCustomSoundPickerBinding

class CustomSoundPickerDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = CustomSoundPickerDialogFragment()
    }

    private lateinit var binding: DialogCustomSoundPickerBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {
            val bottomSheet =
                    dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_custom_sound_picker, container, false)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnPickMedia.setOnClickListener {
            dismiss()
            listener?.onPickFile()
        }

        binding.btnRecordSound.setOnClickListener {
            dismiss()
            listener?.onRecordAudio()
        }

        return binding.root
    }

    interface OnInteractionListener {
        fun onPickFile()
        fun onRecordAudio()
    }

    var listener: OnInteractionListener? = null
}