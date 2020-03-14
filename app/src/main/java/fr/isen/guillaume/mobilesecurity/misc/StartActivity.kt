package fr.isen.guillaume.mobilesecurity.misc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import fr.isen.guillaume.mobilesecurity.HomeActivity
import fr.isen.guillaume.mobilesecurity.LoginActivity

class StartActivity {

    fun goToLogin(appCompatActivity: AppCompatActivity) {
        val intentLogin = Intent(appCompatActivity, LoginActivity::class.java)
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        appCompatActivity.startActivity(intentLogin)
        appCompatActivity.finish()
    }

    fun goToPhone(appCompatActivity: AppCompatActivity) {
        //val intentPhone = Intent(appCompatActivity, PhoneVerificationActivity::class.java)
        val intentPhone = Intent(appCompatActivity, HomeActivity::class.java)
        intentPhone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        appCompatActivity.startActivity(intentPhone)
        appCompatActivity.finish()
    }

    fun goToHome(appCompatActivity: AppCompatActivity) {
        val intentHome = Intent(appCompatActivity, HomeActivity::class.java)
        intentHome.putExtra("phone", true)
        intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        appCompatActivity.startActivity(intentHome)
        appCompatActivity.finish()
    }
}