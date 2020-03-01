package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initLayout()

        btnNew.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }

    private fun initLayout() {
        Handler().postDelayed({
            constraintLayoutInput.visibility = View.VISIBLE
            constraintLayoutConnection.visibility = View.VISIBLE
        }, 2500)
    }
}
