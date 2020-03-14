package fr.isen.guillaume.mobilesecurity

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.guillaume.mobilesecurity.lib.AntiDebug
import fr.isen.guillaume.mobilesecurity.lib.Emulator
import fr.isen.guillaume.mobilesecurity.lib.Monkey
import fr.isen.guillaume.mobilesecurity.lib.Runtime
import fr.isen.guillaume.mobilesecurity.misc.Encryption
import fr.isen.guillaume.mobilesecurity.misc.FormattedTime
import fr.isen.guillaume.mobilesecurity.misc.StartActivity
import fr.isen.guillaume.mobilesecurity.misc.Verification
import fr.isen.guillaume.mobilesecurity.model.Visit
import kotlinx.android.synthetic.main.activity_add_visit.*
import java.util.*
import kotlin.system.exitProcess

class AddVisitActivity : AppCompatActivity() {

    private lateinit var crypto: Encryption
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visit)

        if (Verification().isRooted(this) || Verification().isEmulator() || Runtime().isHooked() || AntiDebug().isDebugged() || Monkey().isUserAMonkey() || Emulator().isQEmuEnvDetected())
            exitProcess(0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified) Verification().checkPending(FirebaseAuth.getInstance(), this)
        else StartActivity().goToLogin(this)

        crypto = Encryption.getInstance()

        // Si l'utilisateur n'est pas authentifié
        auth = FirebaseAuth.getInstance()
        auth.currentUser.let {
            if (it == null)
                finish()
        }

        // Si une référence patient est passée
        if (intent.hasExtra("reference")) {
            // On pré-remplie le champ que l'on verrouille
            txtReference.setText(intent.getStringExtra("reference"))
            txtReference.isEnabled = false
        }

        // Référence à Firestore
        db = FirebaseFirestore.getInstance()

        // Listeners sur la validation de l'ajout
        btnAdd.setOnClickListener {
            addVisit()
        }

        // Listeners sur le champ "Date"
        txtDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.show(supportFragmentManager, "Date de la visite")
            picker.addOnPositiveButtonClickListener {
                if (isValidDate(it))
                    txtDate.setText(FormattedTime.dayMonthYear(it))
                else
                    Toast.makeText(this, "Date invalide", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidDate(date: Long): Boolean {
        // Si la date est antérieure à aujourd'hui
        return if (date <= Calendar.getInstance().timeInMillis)
            true
        else {
            Toast.makeText(this, "Date invalide", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun addVisit() {
        // Visite à ajouter
        val visit = Visit(
            "", FormattedTime.parse(get(txtDate), true), get(txtActions),
            hashMapOf( // Patient
                "reference" to get(txtReference)
            ), hashMapOf( // Visitor
                "id" to auth.currentUser!!.uid,
                "name" to auth.currentUser!!.displayName!!
            )
        )

        if (true) {
            db.collection("patients").whereEqualTo("reference", crypto.encrypt(get(txtReference))).get().addOnSuccessListener {
                if (it.size() <= 0) {
                    Toast.makeText(this, "Erreur BDD : patient inexistant ?", Toast.LENGTH_SHORT)
                        .show()
                    return@addOnSuccessListener
                }

                // Création du document
                val doc = db.collection("visits").document()

                // Chiffrement de la visite
                it.documents.first().let {f ->
                    visit.patient["name"] =
                        crypto.decrypt(f["firstname"].toString()) + " " + crypto.decrypt(f["lastname"].toString())
                }
                visit.setIdAnd(doc.id)

                // Ajout de la visite
                doc.set(crypto.iterateEncrypt(visit)).addOnSuccessListener {
                    Toast.makeText(this, "Ajouté !", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Erreur BDD : patient inexistant ?", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Champ(s) invalide(s)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun get(par: TextInputEditText): String {
        return par.text.toString()
    }
}