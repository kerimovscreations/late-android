package com.kerimovscreations.lateandroid.dialogs

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.kerimovscreations.lateandroid.R
import com.kerimovscreations.lateandroid.application.GlobalApplication
import com.kerimovscreations.lateandroid.databinding.DialogSettingsBinding
import com.kerimovscreations.lateandroid.enums.SoundType
import com.kerimovscreations.lateandroid.tools.HelpFunctions

class SettingsDialogFragment : DialogFragment() {

    companion object {
        fun newInstance() = SettingsDialogFragment()
    }

    private lateinit var binding: DialogSettingsBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding =
                DataBindingUtil.inflate(inflater, R.layout.dialog_settings, container, false)

        binding.btnSubmit.setOnClickListener {
            dismiss()
        }

        binding.languageView.setOnClickListener {
            onLanguage()
        }

        binding.soundView.setOnClickListener {
            onSound()
        }

        updateTexts()

        return binding.root
    }

    private fun updateTexts() {
        binding.title.text = getString(R.string.settings)
        binding.languageTitle.text = getString(R.string.language)
        binding.languageText.text = getString(R.string.current_language)
        binding.soundTitle.text = getString(R.string.sound)
        val soundType = SoundType.values()[HelpFunctions.shared.getSoundType(this.context!!)]
        val resId: Int
        resId = when (soundType) {
            SoundType.MALE_NORMAL -> R.string.male
            SoundType.FEMALE_NORMAL -> R.string.female
            SoundType.MALE_FUNNY_1 -> R.string.male_fun_1
            SoundType.FEMALE_FUNNY_1 -> R.string.female_fun_1
            SoundType.MALE_FUNNY_2 -> R.string.male_fun_2
            SoundType.FEMALE_FUNNY_2 -> R.string.female_fun_2
            else -> R.string.male
        }
        binding.soundText.text = getString(resId)
        binding.btnSubmit.text = getString(R.string.submit)
    }

    private fun onLanguage() {
        val adb = AlertDialog.Builder(this.context!!, R.style.AppThemeAlertDialog)
        val items = arrayOf<CharSequence>("English", "Русский")
        val selectedOption = intArrayOf(-1)
        when (GlobalApplication.localeManager!!.language) {
            "en" -> {
                selectedOption[0] = 0
            }
            "ru" -> {
                selectedOption[0] = 1
            }
            else -> {
                selectedOption[0] = 0
            }
        }
        adb.setSingleChoiceItems(items, selectedOption[0]) { _: DialogInterface?, which: Int -> selectedOption[0] = which }
        adb.setPositiveButton(getString(R.string.save)) { _: DialogInterface?, _: Int ->
            when (selectedOption[0]) {
                0 -> {
                    GlobalApplication.localeManager!!.setNewLocale(this.context!!, "en")
                    HelpFunctions.shared.setUserLanguage(this.context!!, "en")
                    updateTexts()
                }
                1 -> {
                    GlobalApplication.localeManager!!.setNewLocale(this.context!!, "ru")
                    HelpFunctions.shared.setUserLanguage(this.context!!, "ru")
                    updateTexts()
                }
                else -> {
                }
            }
        }
        adb.setNegativeButton(getString(R.string.cancel), null)
        adb.setTitle(getString(R.string.select_language))
        adb.show()
    }

    private fun onSound() {
        val adb = AlertDialog.Builder(this.context!!, R.style.AppThemeAlertDialog)
        val items: Array<CharSequence> = if (GlobalApplication.localeManager!!.language == "en") {
            arrayOf(getString(R.string.male), getString(R.string.female))
        } else {
            arrayOf(getString(R.string.male), getString(R.string.female),
                    getString(R.string.male_fun_1), getString(R.string.female_fun_1),
                    getString(R.string.male_fun_2), getString(R.string.female_fun_2))
        }
        val selectedOption = intArrayOf(-1)
        val soundType = SoundType.values()[HelpFunctions.shared.getSoundType(this.context!!)]
        selectedOption[0] = soundType.value
        adb.setSingleChoiceItems(items, selectedOption[0]) { _: DialogInterface?, which: Int -> selectedOption[0] = which }
        adb.setPositiveButton(getString(R.string.save)) { _: DialogInterface?, _: Int ->
            HelpFunctions.shared.setSoundType(this.context!!, selectedOption[0])
            updateTexts()
        }
        adb.setNegativeButton(getString(R.string.cancel), null)
        adb.setTitle(getString(R.string.select_sound))
        adb.show()
    }

}