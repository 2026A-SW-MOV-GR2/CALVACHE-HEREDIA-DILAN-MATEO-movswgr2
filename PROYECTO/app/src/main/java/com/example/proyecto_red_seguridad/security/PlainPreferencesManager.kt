package com.example.proyecto_red_seguridad.security

import android.content.Context
import android.content.SharedPreferences

class PlainPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PlainSecrets", Context.MODE_PRIVATE)

    fun saveSecret(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getSecret(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}
