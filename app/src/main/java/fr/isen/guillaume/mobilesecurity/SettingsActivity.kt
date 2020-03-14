package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.lib.AntiDebug
import fr.isen.guillaume.mobilesecurity.lib.Emulator
import fr.isen.guillaume.mobilesecurity.lib.Monkey
import fr.isen.guillaume.mobilesecurity.lib.Runtime
import fr.isen.guillaume.mobilesecurity.misc.StartActivity
import fr.isen.guillaume.mobilesecurity.misc.Verification
import kotlinx.android.synthetic.main.activity_settings.*
import kotlin.system.exitProcess

class SettingsActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (Verification().isRooted(this) || Verification().isEmulator() || Runtime().isHooked() || AntiDebug().isDebugged() || Monkey().isUserAMonkey() || Emulator().isQEmuEnvDetected())
            exitProcess(0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified) Verification().checkPending(
            FirebaseAuth.getInstance(), this)
        else StartActivity().goToLogin(this)

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
