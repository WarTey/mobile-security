package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        animCard()

        materialCardPatients.setOnClickListener { startActivity(Intent(this, PatientsActivity::class.java)) }
        materialCardVisits.setOnClickListener { startActivity(Intent(this, VisitsActivity::class.java)) }
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
}
