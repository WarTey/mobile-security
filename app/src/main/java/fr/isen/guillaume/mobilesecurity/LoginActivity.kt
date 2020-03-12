package fr.isen.guillaume.mobilesecurity

import android.R.attr.publicKey
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.model.Pending
import kotlinx.android.synthetic.main.activity_login.*
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class LoginActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*Log.e("f", "coucou")

        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()

        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder("ProjectMobileSecurity", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setUserAuthenticationRequired(false)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build())
        val keyPair = keyPairGenerator.genKeyPair()

        val keyGen: KeyGenerator = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey: SecretKey = keyGen.generateKey()

        val encryptCipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)

        val cipherText: ByteArray = encryptCipher.doFinal(secretKey.encoded)

        val pendingRef = firestore.collection("pending").document("guillaume.blanc-de-lanaute@isen.yncrea.fr")
        Log.e("a", keyPair.public.encoded!!.contentToString())
        Log.e("a", keyPair.public.encoded.size.toString())
        val test = Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT)
        Log.e("b", test)
        Log.e("b", test.length.toString())
        Log.e("c", Base64.decode(test, Base64.DEFAULT)!!.contentToString())
        Log.e("c", Base64.decode(test, Base64.DEFAULT)!!.contentToString().length.toString())
        Log.e("d", Base64.encode(keyPair.public.encoded, Base64.DEFAULT)!!.contentToString())
        Log.e("d", Base64.encode(keyPair.public.encoded, Base64.DEFAULT).size.toString())
        val pending = Pending("guillaume.blanc-de-lanaute@isen.yncrea.fr", Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT), Base64.encodeToString(cipherText, Base64.DEFAULT), "admin")

        pending.let {
            pendingRef.set(it).addOnSuccessListener {
                StyleableToast.makeText(this, getString(R.string.registration_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
            }.addOnFailureListener {
                StyleableToast.makeText(this, getString(R.string.registration_not_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
            }
        }*/

        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isEmailVerified == true && !isEmulator())
            checkPending(firebaseAuth)

        initLayout()

        btnNew.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        btnForget.setOnClickListener { forgetPassword() }
        btnSend.setOnClickListener { login() }
    }

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic")
    }

    private fun initLayout() {
        Handler().postDelayed({
            constraintLayoutInput.visibility = View.VISIBLE
            constraintLayoutConnection.visibility = View.VISIBLE
        }, 2500)
    }

    private fun login() {
        if (!isEmptyInput()) {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signInWithEmailAndPassword(txtUsername.text.toString(), txtPassword.text.toString()).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val isEmailVerified = firebaseAuth.currentUser?.isEmailVerified
                    if (isEmailVerified != null && isEmailVerified)
                        checkPending(firebaseAuth)
                    else
                        viewEmailCheckError(firebaseAuth)
                } else
                    viewLoginError()
            }
        } else
            viewBadInput()
    }

    private fun isEmptyInput(): Boolean {
        return txtUsername.text.isNullOrEmpty() || txtPassword.text.isNullOrEmpty()
    }

    private fun resetInputError() {
        inputUsername.error = null
        inputPassword.error = null
    }

    private fun viewLoginError() {
        resetInputError()
        StyleableToast.makeText(this, getString(R.string.connection_error_message), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
    }

    private fun verifyEmail(firebaseAuth: FirebaseAuth) {
        firebaseAuth.currentUser?.sendEmailVerification()
        StyleableToast.makeText(this, getString(R.string.check_email), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
    }

    private fun viewEmailCheckError(firebaseAuth: FirebaseAuth) {
        resetInputError()
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        val pendingRef = firebaseAuth.currentUser?.email?.let { firestore.collection("pending").document(it) }

        pendingRef?.get()?.addOnSuccessListener {
            if (it.data?.get("status") == "InProgress")
                StyleableToast.makeText(this, getString(R.string.error_status), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
            else if (it.data?.get("status") == "user" || it.data?.get("status") == "admin") {
                verifyEmail(firebaseAuth)
                StyleableToast.makeText(this, getString(R.string.check_email), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
            }
        }?.addOnFailureListener {
            StyleableToast.makeText(this, getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
        }
    }

    private fun checkPending(firebaseAuth: FirebaseAuth) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        val pendingRef = firebaseAuth.currentUser?.email?.let { firestore.collection("pending").document(it) }

        pendingRef?.get()?.addOnSuccessListener {
            if (it.data?.get("status").toString() != "InProgress")
                goToPhone()
        }
    }

    private fun viewBadInput() {
        resetInputError()

        if (txtUsername.text.isNullOrEmpty())
            inputUsername.error = getString(R.string.empty_input)

        if (txtPassword.text.isNullOrEmpty())
            inputPassword.error = getString(R.string.empty_input)
    }

    private fun forgetPassword() {
        if (txtUsername.text.isNullOrEmpty())
            inputUsername.error = getString(R.string.empty_input)
        else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(txtUsername.text.toString()).addOnCompleteListener(this) {
                if (it.isSuccessful)
                    StyleableToast.makeText(this, getString(R.string.email_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
                else
                    StyleableToast.makeText(this, getString(R.string.incorrect_email), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
            }
        }
    }

    private fun goToPhone() {
        //val intentPhone = Intent(this, PhoneVerificationActivity::class.java)
        val intentPhone = Intent(this, HomeActivity::class.java)
        intentPhone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentPhone)
        finish()
    }
}
