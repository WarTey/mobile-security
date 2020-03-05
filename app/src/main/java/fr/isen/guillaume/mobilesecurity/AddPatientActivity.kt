package fr.isen.guillaume.mobilesecurity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.guillaume.mobilesecurity.model.Patient
import kotlinx.android.synthetic.main.activity_add_patient.*
import java.util.*


class AddPatientActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var addQuery: CollectionReference

    private var patient: HashMap<String, Comparable<*>?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        db = FirebaseFirestore.getInstance()
        addQuery = db.collection("patients")

        btnAdd.setOnClickListener {
            addPatient()
        }
    }

    private fun addPatient() {
        patient = hashMapOf(
            "firstname" to get(txtFirstname),
            "lastname" to get(txtLastname)
        )

        if (patient != null && Patient.isValid(patient)) {
            addQuery.add(patient!!).addOnSuccessListener {
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