package org.dam.tfg.crypto

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal object CryptoManager {

    private val provider = CryptographyProvider.Default
    private val aesgcm = provider.get(AES.GCM)

    // ── Clave ─────────────────────────────────────────────────────────────────
    suspend fun generateKeyBytes(): ByteArray =
        aesgcm
            .keyGenerator(AES.Key.Size.B256)
            .generateKey().encodeToByteArray(AES.Key.Format.RAW)

    // ── Cifrado ───────────────────────────────────────────────────────────────
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun encrypt(plaintext: String, keyBytes: ByteArray): String {
        val key = decodeKey(keyBytes)
        val ciphertext = key.cipher().encrypt(plaintext.encodeToByteArray())
        return Base64.encode(ciphertext)
    }

    // ── Descifrado ────────────────────────────────────────────────────────────.
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun decrypt(cipherBase64: String, keyBytes: ByteArray): String {
        val key = decodeKey(keyBytes)
        val decrypted = key.cipher().decrypt(Base64.decode(cipherBase64))
        return decrypted.decodeToString()
    }

    // ── Privado ───────────────────────────────────────────────────────────────

    private suspend fun decodeKey(raw: ByteArray) =
        aesgcm.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, raw)
}