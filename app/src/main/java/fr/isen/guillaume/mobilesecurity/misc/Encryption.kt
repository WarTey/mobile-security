package fr.isen.guillaume.mobilesecurity.misc

import android.util.Base64
import java.nio.charset.Charset
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties


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

    fun encrypt(plain: String?): String? {
        if (plain == null)
            return null

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

    fun decrypt(encryptedB64: String?): String? {
        if (encryptedB64 == null)
            return null

        val encryptedBytes = Base64.decode(encryptedB64, Base64.DEFAULT)

        // Récupération de l'IV
        val ivSpec = IvParameterSpec(encryptedBytes.copyOfRange(0, SIZE))

        // Déchiffrement
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            .also { it.init(Cipher.DECRYPT_MODE, keySpec, ivSpec) }
        return cipher.doFinal(encryptedBytes.copyOfRange(SIZE, encryptedBytes.size))
            .toString(Charset.defaultCharset())
    }

    fun <T : Any> iterateEncrypt(obj: T): T {
        obj::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.classifier == String::class }
            .filterIsInstance<KMutableProperty<*>>().forEach {
                it.setter.call(obj, encrypt(it.getter.call(obj) as String))
            }

        return obj
    }

    fun <T : Any> iterateDecrypt(obj: T): T {
        obj::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.classifier == String::class }
            .filterIsInstance<KMutableProperty<*>>().forEach {
                it.setter.call(obj, decrypt(it.getter.call(obj) as String))
            }

        return obj
    }
}