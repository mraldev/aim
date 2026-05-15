package org.dam.tfg.crypto

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

object ObjectStore {

    private val settings: Settings by lazy { Settings() }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun <T> save(key: String, value: T, serializer: KSerializer<T>) {
        val encoded = json.encodeToString(serializer, value)
        settings[key] = encoded
    }

    fun <T> load(key: String, serializer: KSerializer<T>): T? {
        val raw = settings.getStringOrNull(key) ?: return null
        return try {
            json.decodeFromString(serializer, raw)
        } catch (e: Exception) {
            null
        }
    }

    fun remove(key: String) {
        settings.remove(key)
    }
}
