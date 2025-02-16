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
    ): View {
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
        binding.languageText.text = when (GlobalApplication.localeManager!!.language) {
            "en" -> "English"
            "ru" -> "Русский"
            else -> "English"
        }
        binding.soundTitle.text = getString(R.string.sound)
        val soundType =
            SoundType.entries.getOrElse(
                HelpFunctions.shared.getSoundType(this.requireContext()),
                { SoundType.MALE_NORMAL })
        val resId: Int = when (soundType) {
            SoundType.MALE_NORMAL -> R.string.male
            SoundType.FEMALE_NORMAL -> R.string.female
            SoundType.MALE_FUNNY_1 -> R.string.male_fun_1
            SoundType.FEMALE_FUNNY_1 -> R.string.female_fun_1
            SoundType.MALE_FUNNY_2 -> R.string.male_fun_2
            SoundType.FEMALE_FUNNY_2 -> R.string.female_fun_2
        }

        binding.soundText.text = getString(resId)
        binding.btnSubmit.text = getString(R.string.submit)
    }

    private fun onLanguage() {
        val adb = AlertDialog.Builder(this.requireContext(), R.style.AppThemeAlertDialog)
        val items = arrayOf<CharSequence>("English", "Русский")
        var selectedOption = when (GlobalApplication.localeManager!!.language) {
            "en" -> {
                0
            }

            "ru" -> {
                1
            }

            else -> {
                0
            }
        }
        adb.setSingleChoiceItems(
            items,
            selectedOption
        ) { _: DialogInterface?, which: Int -> selectedOption = which }
        adb.setPositiveButton(getString(R.string.save)) { _: DialogInterface?, _: Int ->
            when (selectedOption) {
                0 -> {
                    GlobalApplication.localeManager!!.setNewLocale(this.requireContext(), "en")
                    HelpFunctions.shared.setUserLanguage(this.requireContext(), "en")
                    HelpFunctions.shared.setSoundType(this.requireContext(), SoundType.MALE_NORMAL)
                    updateTexts()
                }

                1 -> {
                    GlobalApplication.localeManager!!.setNewLocale(this.requireContext(), "ru")
                    HelpFunctions.shared.setUserLanguage(this.requireContext(), "ru")
                    HelpFunctions.shared.setSoundType(this.requireContext(), SoundType.MALE_NORMAL)
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
        val adb = AlertDialog.Builder(this.requireContext(), R.style.AppThemeAlertDialog)
        val language = GlobalApplication.localeManager!!.language
        val items: Array<Pair<SoundType, CharSequence>> = if (language == "en") {
            arrayOf(
                Pair(SoundType.MALE_NORMAL, getString(R.string.male)),
                Pair(SoundType.FEMALE_NORMAL, getString(R.string.female)),
            )
        } else {
            arrayOf(
                Pair(SoundType.MALE_NORMAL, getString(R.string.male)),
                Pair(SoundType.FEMALE_NORMAL, getString(R.string.female)),
                Pair(SoundType.MALE_FUNNY_1, getString(R.string.male_fun_1)),
                Pair(SoundType.FEMALE_FUNNY_1, getString(R.string.female_fun_1)),
                Pair(SoundType.MALE_FUNNY_2, getString(R.string.male_fun_2)),
                Pair(SoundType.FEMALE_FUNNY_2, getString(R.string.female_fun_2)),
            )
        }
        var selectedOption: Int
        val soundType = SoundType.entries.getOrElse(
            HelpFunctions.shared.getSoundType(this.requireContext()),
            { SoundType.MALE_NORMAL })
        selectedOption = items.firstOrNull { it.first == soundType }?.let { items.indexOf(it) } ?: 0
        adb.setSingleChoiceItems(
            items.map { it.second }.toTypedArray(),
            selectedOption
        ) { _: DialogInterface?, which: Int -> selectedOption = which }
        adb.setPositiveButton(getString(R.string.save)) { _: DialogInterface?, _: Int ->
            HelpFunctions.shared.setSoundType(
                this.requireContext(),
                items[selectedOption].first
            )
            updateTexts()
        }
        adb.setNegativeButton(getString(R.string.cancel), null)
        adb.setTitle(getString(R.string.select_sound))
        adb.show()
    }
}