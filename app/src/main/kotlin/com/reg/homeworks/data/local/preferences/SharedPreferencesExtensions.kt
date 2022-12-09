package com.task.homework_4.data.local.preferences

import android.content.SharedPreferences

fun SharedPreferences.put(key: String, value: Any?) =
    edit().apply {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            is Boolean -> putBoolean(key, value)
        }
    }.apply()