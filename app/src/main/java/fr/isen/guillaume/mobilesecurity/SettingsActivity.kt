package fr.isen.guillaume.mobilesecurity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.muddzdev.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnPassword.setOnClickListener { forgetPassword() }

    }

    private fun forgetPassword() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.email?.let { email ->
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
                if (it.isSuccessful)
                    StyleableToast.makeText(this, getString(R.string.email_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
                else
                    StyleableToast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
            }
        }
    }
}
