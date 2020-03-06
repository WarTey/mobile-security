package fr.isen.guillaume.mobilesecurity.misc

import android.util.Base64
import java.nio.charset.Charset
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class Encryption {

    private var keySpec: SecretKeySpec

    companion object {
        // Size (in octets) of block length
        private const val SIZE = 16

        private var INSTANCE: Encryption? = null

        fun getInstance(): Encryption {
            synchronized(this) {
                if (INSTANCE == null)
                    INSTANCE = Encryption()

                return INSTANCE!!
            }
        }
    }

    init {
        // DEBUG clef depuis keystore
        val key = "SALUT LES LOULOU".toByteArray()
        keySpec = SecretKeySpec(key, "AES")
    }

    fun encrypt(plain: String): String {
        val plainBytes = plain.toByteArray()

        // Génération d'un IV
        // DEBUG salt ?
        val ivSpec = IvParameterSpec(ByteArray(SIZE).also { SecureRandom().nextBytes(it) })

        // Chiffrement
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            .also { it.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) }
        val encrypted = cipher.doFinal(plainBytes)

        // Ajout de l'IV au message
        val bytes = Arrays.copyOf(ivSpec.iv, SIZE + encrypted.size)
        System.arraycopy(encrypted, 0, bytes, SIZE, encrypted.size)

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedB64: String): String {
        val encryptedBytes = Base64.decode(encryptedB64, Base64.DEFAULT)

        // Récupération de l'IV
        val ivSpec = IvParameterSpec(encryptedBytes.copyOfRange(0, SIZE))

        // Déchiffrement
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            .also { it.init(Cipher.DECRYPT_MODE, keySpec, ivSpec) }
        return cipher.doFinal(encryptedBytes.copyOfRange(SIZE, encryptedBytes.size))
            .toString(Charset.defaultCharset())
    }
}