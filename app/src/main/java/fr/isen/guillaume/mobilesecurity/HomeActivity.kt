package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import fr.isen.guillaume.mobilesecurity.lib.AntiDebug
import fr.isen.guillaume.mobilesecurity.lib.Emulator
import fr.isen.guillaume.mobilesecurity.lib.Monkey
import fr.isen.guillaume.mobilesecurity.lib.Runtime
import fr.isen.guillaume.mobilesecurity.misc.Encryption
import fr.isen.guillaume.mobilesecurity.misc.StartActivity
import fr.isen.guillaume.mobilesecurity.misc.Verification
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (Verification().isRooted(this) || Verification().isEmulator() || Runtime().isHooked() || AntiDebug().isDebugged() || Monkey().isUserAMonkey() || Emulator().isQEmuEnvDetected())
            exitProcess(0)

        /*val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified && intent.getBooleanExtra("phone", false)) checkPending(FirebaseAuth.getInstance())
        else goToLogin()*/

        // Encryption init
        Encryption.getInstance()

        animCard()

        materialCardPatients.setOnClickListener { startActivity(Intent(this, PatientsActivity::class.java)) }
        materialCardVisits.setOnClickListener { startActivity(Intent(this, VisitsActivity::class.java)) }
        materialCardSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        btnLogout.setOnClickListener { logout() }
    }

    private fun animCard() {
        materialCardPatients.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_one))
        materialCardVisits.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_one))
        materialCardSettings.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_two))
        btnLogout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_three))
    }

    private fun checkPending(firebaseAuth: FirebaseAuth) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        val pendingRef = firebaseAuth.currentUser?.email?.let { firestore.collection("pending").document(it) }

        pendingRef?.get()?.addOnSuccessListener {
            if (it.data?.get("status").toString() != "InProgress")
                txtUsername.text = firebaseAuth.currentUser?.displayName
        }
    }

    private fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null)
            firebaseAuth.signOut()
        StartActivity().goToLogin(this)
    }
}
