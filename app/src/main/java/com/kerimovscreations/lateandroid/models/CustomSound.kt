package com.kerimovscreations.lateandroid.models

import io.realm.RealmObject

open class CustomSound(
        var soundFile: String = "",
        var lang: String = "",
        var value: Int = 0
) : RealmObject()