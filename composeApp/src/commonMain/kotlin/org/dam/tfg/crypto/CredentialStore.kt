package org.dam.tfg.crypto

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Credenciales descifradas listas para usar.
 */
data class StoredCredentials(
    val correo: String,
    val contrasenya: String  // en claro, solo vive en memoria
)

/**
 * Persiste y recupera las credenciales del usuario cifradas con AES-256-GCM.
 *
 * Uso:
 * ```
 * CredentialStore.init(settings)  // una sola vez al arrancar
 * CredentialStore.save(correo, contrasenya)
 * val creds = CredentialStore.load() // null si no hay nada guardado
 * CredentialStore.clear()
 */
object CredentialStore {

    // ── Claves de almacenamiento ──────────────────────────────────────────────

    private const val KEY_CORREO = "cred_correo"
    private const val KEY_CONTRASENYA = "cred_contrasenya_enc"
    private const val KEY_CRYPTO_KEY  = "cred_aes_key"

    // ── Estado interno ────────────────────────────────────────────────────────

    private val settings: Settings by lazy { Settings() }

    // ── API pública ───────────────────────────────────────────────────────────

    /**
     * Cifra y guarda las credenciales. Llama tras un login.
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun save(correo: String, contrasenya: String) {
        val keyBytes = getOrCreateKey()
        val contrasenyaCifrada = CryptoManager.encrypt(contrasenya, keyBytes)
        settings[KEY_CORREO] = correo
        settings[KEY_CONTRASENYA] = contrasenyaCifrada
    }

    /**
     * Carga y descifra las credenciales guardadas.
     * @return [StoredCredentials] o `null` si no hay nada guardado o falla el descifrado.
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun load(): StoredCredentials? {
        val correo = settings.getStringOrNull(KEY_CORREO)      ?: return null
        val contrasenyaCifrada = settings.getStringOrNull(KEY_CONTRASENYA) ?: return null
        val keyBytes = settings.getStringOrNull(KEY_CRYPTO_KEY)
            ?.let { Base64.decode(it) }
            ?: return null

        return try {
            val contrasenya = CryptoManager.decrypt(contrasenyaCifrada, keyBytes)
            StoredCredentials(correo, contrasenya)
        } catch (e: Exception) {
            clear()
            null
        }
    }

    /**
     * Elimina todas las credenciales guardadas.
     */
    fun clear() {
        settings.remove(KEY_CORREO)
        settings.remove(KEY_CONTRASENYA)
        // Nota: NO borramos KEY_CRYPTO_KEY deliberadamente.
        // Reutilizar la misma clave en el próximo save() es correcto y evita regenerarla.
    }

    // ── Privado ───────────────────────────────────────────────────────────────

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun getOrCreateKey(): ByteArray {
        val stored = settings.getStringOrNull(KEY_CRYPTO_KEY)
        if (stored != null) return Base64.decode(stored)

        val newKey = CryptoManager.generateKeyBytes()
        settings[KEY_CRYPTO_KEY] = Base64.encode(newKey)
        return newKey
    }
}