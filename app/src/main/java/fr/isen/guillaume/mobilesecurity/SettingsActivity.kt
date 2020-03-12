package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muddzdev.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setView()

        btnPassword.setOnClickListener { forgetPassword() }
        btnRegister.setOnClickListener { startActivity(Intent(this, PendingModeActivity::class.java)) }
        btnRemove.setOnClickListener { startActivity(Intent(this, RemoveUsersActivity::class.java)) }
    }

    private fun setView() {
        val firestore = FirebaseFirestore.getInstance()
        val pendingRef = FirebaseAuth.getInstance().currentUser?.email?.let { firestore.collection("pending").document(it) }

        pendingRef?.get()?.addOnSuccessListener {
            if (it.data?.get("status").toString() == "admin") {
                btnRegister.visibility = View.VISIBLE
                btnRemove.visibility = View.VISIBLE
            }
        }
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
