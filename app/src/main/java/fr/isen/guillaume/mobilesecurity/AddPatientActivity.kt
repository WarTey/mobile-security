package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.guillaume.mobilesecurity.lib.AntiDebug
import fr.isen.guillaume.mobilesecurity.lib.Emulator
import fr.isen.guillaume.mobilesecurity.lib.Monkey
import fr.isen.guillaume.mobilesecurity.lib.Runtime
import fr.isen.guillaume.mobilesecurity.misc.Encryption
import fr.isen.guillaume.mobilesecurity.misc.StartActivity
import fr.isen.guillaume.mobilesecurity.misc.Verification
import fr.isen.guillaume.mobilesecurity.model.Patient
import kotlinx.android.synthetic.main.activity_add_patient.*
import kotlin.system.exitProcess

class AddPatientActivity : AppCompatActivity() {

    private lateinit var crypto: Encryption
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        if (Verification().isRooted(this) || Verification().isEmulator() || Runtime().isHooked() || AntiDebug().isDebugged() || Monkey().isUserAMonkey() || Emulator().isQEmuEnvDetected())
            exitProcess(0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified) Verification().checkPending(FirebaseAuth.getInstance(), this)
        else StartActivity().goToLogin(this)

        crypto = Encryption.getInstance()
        // Référence Firestore
        db = FirebaseFirestore.getInstance()

        // Listeners d'ajout de patient
        btnAdd.setOnClickListener {
            addPatient()
        }
    }

    private fun addPatient() {
        // Patient à ajouter
        val patient = Patient("", get(txtReference), get(txtFirstname), get(txtLastname), get(txtPathology), get(txtTreatment), null)

        if (true) {//patient != null && patient.isValid()) {
            // Création du document
            val doc = db.collection("patients").document()

            // Chiffrement du patient
            patient.setIdAnd(doc.id)

            // Ajout du patient
            doc.set(crypto.iterateEncrypt(patient)).addOnSuccessListener {
                Toast.makeText(this, "Ajouté !", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Champ(s) invalide(s)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun get(par: TextInputEditText): String {
        return par.text.toString()
    }
}