package fr.isen.guillaume.mobilesecurity
import android.util.Log
import android.widget.Toast
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec



class EncryptActivity() {
}

public fun encryptBytes(plainText:String, passwordString: String): String {
    var salt = ByteArray(256)
    var i:Int=0
    val charset = Charsets.UTF_8
    val plainTextBytes: ByteArray = plainText.toByteArray(charset)

    //Random salt for next step
    val random = SecureRandom()
    //val salt = ByteArray(256)
    random.nextBytes(salt)

    //PBKDF2 - derive la clé du mot de passe
    val passwordChar = passwordString.toCharArray() //Conversion du password en char[] array
    //clé de longueur 256
    //AES
    val pbKeySpec = PBEKeySpec(passwordChar, salt, 1324, 256) //1324 iterations
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded()
    val keySpec = SecretKeySpec(keyBytes, "AES")
    Log.d("KeyC",keySpec.toString())

    //Creation du vecteur d'initialisation pour l'AES
    val ivRandom = SecureRandom() //not caching previous seeded instance of SecureRandom
    val iv = ByteArray(16)
    ivRandom.nextBytes(iv)
    val ivSpec = IvParameterSpec(iv)

    //Encrypt
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    var encrypted = cipher.doFinal(plainTextBytes)

    var textMessage:String=""
    val cryptTab=ByteArray((encrypted.size)+(iv.size)+(salt.size))
    i=0
    while(i<16){
        cryptTab[i]=iv[i]
        textMessage+=cryptTab[i].toChar()
        i++
    }
    i=16
    var j:Int=0
    while(i<272){
        cryptTab[i]=salt[j]
        textMessage+=cryptTab[i].toChar()
        i++
        j++
    }
    i=272
    j=0
    while(i<cryptTab.size){
        cryptTab[i]=encrypted[j]
        textMessage+=cryptTab[i].toChar()
        i++
        j++
    }

    var test:String=String(cryptTab)
    return textMessage
}

public  fun decryptData(textCrypt: String, passwordString: String): String {
    var decrypted: ByteArray? = null
    var ivTab=ByteArray(16)
    var i:Int=0

    //Conversion du password en char[] array
    val passwordChar = passwordString.toCharArray()

    val charset = Charsets.UTF_8
    // initialisation du tableau qui va récupérer le chiffré + IV + sel
    val cryptArray = ByteArray(textCrypt.length)
    i=0
    while(i<cryptArray.size){
        cryptArray[i]=textCrypt[i].toByte()
        i++
    }

    //Récupération de l'IV
    i=0
    while(i < 16){
        ivTab[i]=cryptArray[i]
        i++
    }

    //Récupération du sel
    var saltTab=ByteArray(256)
    var j:Int=0
    while(i < 272){
        saltTab[j]=cryptArray[i]
        i++
        j++
    }

    //clé de longueur 256
    //AES
    val pbKeySpec = PBEKeySpec(passwordChar, saltTab, 1324, 256)
    val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
    val keySpec = SecretKeySpec(keyBytes, "AES")

    var msgCrypt=ByteArray((cryptArray.size)-272)
    //Récupération du chiffré
    i=272
    j=0
    while(i < cryptArray.size){
        msgCrypt[j]=cryptArray[i]
        i++
        j++
    }

    //Decrypt
    val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
    val ivSpec = IvParameterSpec(ivTab)
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    decrypted = cipher.doFinal(msgCrypt)

    //Conversion du message dechiffré : byteArray vers String
    var decryptText = String(decrypted)
    return decryptText
}

