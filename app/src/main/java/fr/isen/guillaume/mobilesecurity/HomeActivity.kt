package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) txtUsername.text = firebaseUser.displayName
        else goToLogin()

        animCard()

        materialCardPatients.setOnClickListener { startActivity(Intent(this, PatientsActivity::class.java)) }
        materialCardVisits.setOnClickListener { startActivity(Intent(this, VisitsActivity::class.java)) }

        btnLogout.setOnClickListener { logout() }
    }

    private fun animCard() {
        materialCardPatients.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_one))
        materialOldPatients.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_one))
        materialCardVisits.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_two))
        materialCardMail.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_two))
        materialCardTokens.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_three))
        materialCardSettings.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_three))
        btnLogout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translation_y_four))
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
