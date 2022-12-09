package com.task.homework_4.data.local.preferences

import android.content.SharedPreferences
import com.task.homework_4.data.local.preferences.Constants.IS_AUTHENTICATED
import com.task.homework_4.data.local.preferences.Constants.VERIFICATION_ID

class PreferencesManager(private val sharedPreferences: SharedPreferences) {

    var verificationId: String?
        get() = sharedPreferences.getString(VERIFICATION_ID, "")
        set(value) = sharedPreferences.put(VERIFICATION_ID, value)

    var isAuthenticated: Boolean
        get() = sharedPreferences.getBoolean(IS_AUTHENTICATED, false)
        set(value) = sharedPreferences.put(IS_AUTHENTICATED, value)
}