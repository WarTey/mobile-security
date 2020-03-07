package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import fr.isen.guillaume.mobilesecurity.misc.Encryption
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified && intent.getBooleanExtra("phone", false)) checkPending(FirebaseAuth.getInstance())
        else goToLogin()

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

    private fun goToLogin() {
        val intentLogin = Intent(this, LoginActivity::class.java)
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentLogin)
        finish()
    }

    private fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null)
            firebaseAuth.signOut()
        goToLogin()
    }
}
