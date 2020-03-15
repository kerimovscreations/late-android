package com.kerimovscreations.lateandroid.tools

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        supportFragmentManager.fragments.forEach {
            it.onActivityResult(requestCode, resultCode, data)
        }
    }
}