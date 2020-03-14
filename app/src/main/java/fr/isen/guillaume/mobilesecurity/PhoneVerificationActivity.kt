package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.lib.AntiDebug
import fr.isen.guillaume.mobilesecurity.lib.Emulator
import fr.isen.guillaume.mobilesecurity.lib.Monkey
import fr.isen.guillaume.mobilesecurity.lib.Runtime
import fr.isen.guillaume.mobilesecurity.misc.StartActivity
import fr.isen.guillaume.mobilesecurity.misc.Verification
import kotlinx.android.synthetic.main.activity_phone_verification.*
import kotlinx.android.synthetic.main.activity_phone_verification.btnSend
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.system.exitProcess

class PhoneVerificationActivity : AppCompatActivity() {

    private var code = "null"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)

        if (Verification().isRooted(this) || Verification().isEmulator() || Runtime().isHooked() || AntiDebug().isDebugged() || Monkey().isUserAMonkey() || Emulator().isQEmuEnvDetected())
            exitProcess(0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified) Verification().checkPending(
            FirebaseAuth.getInstance(), this)
        else StartActivity().goToLogin(this)

        if (!firebaseUser?.phoneNumber.isNullOrEmpty()) {
            changeDisplay()
            sendCode(firebaseUser)
        } else
            btnSend.setOnClickListener { sendCode(firebaseUser) }
        btnNoCode.setOnClickListener { drawPhone(firebaseUser) }
        btnContinue.setOnClickListener { verifCode() }
        btnBack.setOnClickListener { leave() }
    }

    private fun leave() {
        FirebaseAuth.getInstance().signOut()
        val intentLogin = Intent(this, LoginActivity::class.java)
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentLogin)
        finish()
    }

    private fun changeDisplay() {
        inputPhone.visibility = View.GONE
        btnSend.visibility = View.GONE
        pinView.visibility = View.VISIBLE
        btnContinue.visibility = View.VISIBLE
        btnNoCode.visibility = View.VISIBLE
    }

    private fun verifCode() {
        if (code != "null") {
            val phoneAuthCredential = PhoneAuthProvider.getCredential(code, pinView.value)
            FirebaseAuth.getInstance().currentUser?.linkWithCredential(phoneAuthCredential)?.addOnCompleteListener {
                if (it.isSuccessful)
                    StartActivity().goToHome(this)
                else
                    StyleableToast.makeText(this, getString(R.string.code_error), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
            }
        }
    }

    private fun sendCode(firebaseUser: FirebaseUser?) {
        if (isPhone() || !firebaseUser?.phoneNumber.isNullOrEmpty()) {
            val phoneNumber = if (firebaseUser?.phoneNumber.isNullOrEmpty()) txtPhone.text.toString() else firebaseUser?.phoneNumber
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber.toString(), 60, TimeUnit.SECONDS, this, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    StartActivity().goToHome(this@PhoneVerificationActivity)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    StyleableToast.makeText(this@PhoneVerificationActivity, getString(R.string.code_failed), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    code = p0

                    StyleableToast.makeText(this@PhoneVerificationActivity, getString(R.string.code_sent), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
                    drawPinCode(firebaseUser)
                }
            })
        } else
            showPhoneError()
    }

    private fun drawPhone(firebaseUser: FirebaseUser?) {
        if (firebaseUser?.phoneNumber.isNullOrEmpty()) {
            pinView.visibility = View.GONE
            btnContinue.visibility = View.GONE
            btnNoCode.visibility = View.GONE
            Handler().postDelayed({
                inputPhone.visibility = View.VISIBLE
                btnSend.visibility = View.VISIBLE
            }, 500)
        } else
            sendCode(firebaseUser)
    }

    private fun drawPinCode(firebaseUser: FirebaseUser?) {
        if (firebaseUser?.phoneNumber.isNullOrEmpty()) {
            inputPhone.visibility = View.GONE
            btnSend.visibility = View.GONE
            Handler().postDelayed({
                pinView.visibility = View.VISIBLE
                btnContinue.visibility = View.VISIBLE
                btnNoCode.visibility = View.VISIBLE
            }, 500)
        }
    }

    private fun isPhone(): Boolean {
        return Pattern.compile(PHONE_REGEX).matcher(txtPhone.text.toString()).matches()
    }

    private fun showPhoneError() {
        inputPhone.error = getString(R.string.not_phone_number)
    }

    companion object {
        private const val PHONE_REGEX = "(?:(?:\\+)33{0,3}(?:\\[\\s.-]{0,3})?)[1-9](?:(?:[\\s.-]?\\d{2}){4}|\\d{2}(?:[\\s.-]?\\d{3}){2})"
    }
}
