package fr.isen.guillaume.mobilesecurity.misc

import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties


class Encryption {

    private var keySpec: SecretKeySpec? = null

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
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val email = currentUser.email

            if (email != null) {
                FirebaseFirestore.getInstance().collection("pending").document(email).get().addOnSuccessListener {

                    val keyStore = KeyStore.getInstance("AndroidKeyStore")
                    keyStore.load(null)

                    val keys = keyStore.getEntry("ProjectMobileSecurity", null) as KeyStore.PrivateKeyEntry
                    val keyAdmin = it.data?.get("sym").toString()
                    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
                    cipher.init(Cipher.DECRYPT_MODE, keys.privateKey)
                    Log.e("dd", Base64.decode(keyAdmin, Base64.DEFAULT)!!.contentToString())
                    Log.e("dd", Base64.decode(keyAdmin, Base64.DEFAULT).size.toString())
                    val keyAES = cipher.doFinal(Base64.decode(keyAdmin, Base64.DEFAULT))

                    keySpec = SecretKeySpec(keyAES, "AES")
                }
            }
        }
    }

    fun encrypt(plain: String?): String? {
        if (plain == null || keySpec == null)
            return null

        val plainBytes = plain.toByteArray()

        // Génération d'un IV
        // DEBUG salt ?
        val ivSpec = IvParameterSpec(ByteArray(SIZE))//.also { SecureRandom().nextBytes(it) })

        // Chiffrement
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding").also { it.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) }
        val encrypted = cipher.doFinal(plainBytes)

        // Ajout de l'IV au message
        //val bytes = Arrays.copyOf(ivSpec.iv, SIZE + encrypted.size)
        //System.arraycopy(encrypted, 0, bytes, SIZE, encrypted.size)

        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(encryptedB64: String?): String? {
        if (encryptedB64 == null || keySpec == null)
            return null

        val encryptedBytes = Base64.decode(encryptedB64, Base64.DEFAULT)

        // Récupération de l'IV
        val ivSpec = IvParameterSpec(ByteArray((SIZE)))//encryptedBytes.copyOfRange(0, SIZE))

        // Déchiffrement
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            .also { it.init(Cipher.DECRYPT_MODE, keySpec, ivSpec) }
        return cipher.doFinal(encryptedBytes/*.copyOfRange(SIZE, encryptedBytes.size)*/)
            .toString(Charset.defaultCharset())
    }

    fun <T : Any> iterateEncrypt(obj: T): T {
        obj::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.classifier == String::class }
            .filterIsInstance<KMutableProperty<*>>().forEach {
                it.setter.call(obj, encrypt(it.getter.call(obj) as String))
            }

        obj::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.classifier == HashMap::class }
            .filterIsInstance<KMutableProperty<*>>().forEach {
                val map = it.getter.call(obj) as HashMap<String, String>
                map.entries.forEach { field ->
                    map[field.key] = encrypt(field.value)!!
                }
            }

        return obj
    }

    fun <T : Any> iterateDecrypt(obj: T): T {
        obj::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.classifier == String::class }
            .filterIsInstance<KMutableProperty<*>>().forEach {
                it.setter.call(obj, decrypt(it.getter.call(obj) as String))
            }

        obj::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.classifier == HashMap::class }
            .filterIsInstance<KMutableProperty<*>>().forEach {
                val map = it.getter.call(obj) as HashMap<String, String>
                map.entries.forEach { field ->
                    map.set(field.key, decrypt(field.value)!!)
                }
            }

        return obj
    }
}