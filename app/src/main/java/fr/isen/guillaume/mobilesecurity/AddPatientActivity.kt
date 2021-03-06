package fr.isen.guillaume.mobilesecurity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.guillaume.mobilesecurity.misc.Encryption
import fr.isen.guillaume.mobilesecurity.model.Patient
import kotlinx.android.synthetic.main.activity_add_patient.*


class AddPatientActivity : AppCompatActivity() {

    private lateinit var crypto: Encryption
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

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