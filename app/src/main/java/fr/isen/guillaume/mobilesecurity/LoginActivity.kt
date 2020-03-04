package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.muddzdev.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) goToPhone()

        initLayout()

        btnNew.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        btnForget.setOnClickListener { forgetPassword() }
        btnSend.setOnClickListener { login() }
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
                        goToPhone()
                    else
                        viewEmailCheckError()
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

    private fun viewEmailCheckError() {
        resetInputError()
        StyleableToast.makeText(this, getString(R.string.check_email), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
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
        val intentPhone = Intent(this, PhoneVerificationActivity::class.java)
        intentPhone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentPhone)
        finish()
    }
}
