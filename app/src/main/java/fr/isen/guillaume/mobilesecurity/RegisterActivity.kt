package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.model.Pending
import kotlinx.android.synthetic.main.activity_register.*
import java.security.KeyPairGenerator
import java.util.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) goToLogin()

        btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        if (isEmail() && isSamePassword() && !isPasswordTooShort() && !txtName.text.isNullOrEmpty()) {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.createUserWithEmailAndPassword(txtUsername.text.toString(), txtPassword.text.toString()).addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val userProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(txtName.text.toString()).build()
                    firebaseAuth.currentUser?.updateProfile(userProfileChangeRequest)
                    sendRequest(firebaseAuth)
                } else
                    viewRegisterError()
            }
        } else
            viewBadInput()
    }

    private fun isPasswordTooShort(): Boolean {
        return txtPassword.text.toString().length < 13
    }

    private fun isEmail(): Boolean {
        return Pattern.compile(EMAIL_REGEX).matcher(txtUsername.text.toString()).matches()
    }

    private fun isSamePassword(): Boolean {
        return txtPassword.text.toString() == txtConfirmPassword.text.toString()
    }

    private fun resetInputError() {
        inputName.error = null
        inputUsername.error = null
        inputPassword.error = null
        inputConfirmPassword.error = null
    }

    private fun viewBadInput() {
        resetInputError()

        if (txtName.text.isNullOrEmpty())
            inputName.error = getString(R.string.empty_input)

        if (!isEmail())
            inputUsername.error = getString(R.string.incorrect_email)

        if (isPasswordTooShort())
            inputPassword.error = getString(R.string.too_short_password)

        if (!isSamePassword() || txtConfirmPassword.text.isNullOrEmpty())
            inputConfirmPassword.error = getString(R.string.incorrect_password)
    }

    private fun viewRegisterError() {
        resetInputError()
        StyleableToast.makeText(this, getString(R.string.register_error), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
    }

    private fun sendRequest(firebaseAuth: FirebaseAuth) {
        val firestore = FirebaseFirestore.getInstance()
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(3072)

        val pendingRef = firestore.collection("pending").document()
        val pending = firebaseAuth.currentUser?.email?.let { Pending(it, Arrays.toString(keyPairGenerator.genKeyPair().private.encoded), "InProgress") }

        pending?.let {
            pendingRef.set(it).addOnSuccessListener {
                StyleableToast.makeText(this, getString(R.string.registration_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
                firebaseAuth.signOut()
                goToLogin()
            }.addOnFailureListener {
                StyleableToast.makeText(this, getString(R.string.registration_not_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
            }
        }
    }

    private fun goToLogin() {
        val intentLogin = Intent(this, LoginActivity::class.java)
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentLogin)
        finish()
    }

    companion object {
        private const val EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"
    }
}
