package com.kerimovscreations.lateandroid.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.databinding.DialogGuidelinesBinding

class GuidelinesDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = GuidelinesDialogFragment()
    }

    private lateinit var binding: DialogGuidelinesBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.dialog_guidelines, container, false)

        binding.btnSubmit.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}