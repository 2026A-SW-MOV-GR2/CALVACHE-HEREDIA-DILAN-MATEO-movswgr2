package com.example.proyecto_red_seguridad

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_red_seguridad.security.DataStoreManager
import com.example.proyecto_red_seguridad.security.EncryptedPreferencesManager
import com.example.proyecto_red_seguridad.security.PlainPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SecurityViewModel(application: Application) : AndroidViewModel(application) {
    private val plainPrefs = PlainPreferencesManager(application)
    private val encryptedPrefs = EncryptedPreferencesManager(application)
    private val dataStorePrefs = DataStoreManager(application)

    val secretKey = MutableStateFlow("")
    val secretValue = MutableStateFlow("")
    val selectedStorage = MutableStateFlow("SharedPreferences")
    val message = MutableStateFlow("")

    fun saveSecret() {
        val key = secretKey.value
        val value = secretValue.value
        if (key.isBlank()) {
            message.value = "Por favor, ingresa una llave"
            return
        }

        viewModelScope.launch {
            when (selectedStorage.value) {
                "SharedPreferences" -> plainPrefs.saveSecret(key, value)
                "Encrypted" -> encryptedPrefs.saveSecret(key, value)
                "DataStore" -> dataStorePrefs.saveSecret(key, value)
            }
            secretValue.value = ""
            message.value = "Secreto guardado exitosamente"
        }
    }

    fun recoverSecret() {
        val key = secretKey.value
        if (key.isBlank()) {
            message.value = "Por favor, ingresa una llave"
            return
        }

        viewModelScope.launch {
            val result = when (selectedStorage.value) {
                "SharedPreferences" -> plainPrefs.getSecret(key)
                "Encrypted" -> encryptedPrefs.getSecret(key)
                "DataStore" -> dataStorePrefs.getSecret(key)
                else -> null
            }

            if (result != null) {
                secretValue.value = result
                message.value = "Secreto recuperado con éxito"
            } else {
                message.value = "Secreto no encontrado en este compartimento"
            }
        }
    }
}
