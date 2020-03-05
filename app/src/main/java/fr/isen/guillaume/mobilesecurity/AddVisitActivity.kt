package fr.isen.guillaume.mobilesecurity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.guillaume.mobilesecurity.misc.FormattedTime
import fr.isen.guillaume.mobilesecurity.model.Visit
import kotlinx.android.synthetic.main.activity_add_visit.*
import java.util.*

class AddVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var addQuery: CollectionReference

    private lateinit var userUid: String

    private var visit: HashMap<String, Comparable<*>?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visit)

        FirebaseAuth.getInstance().currentUser.let {
            if (it == null) {
                Log.d("oups", "aie")
                finish()
            }
            else
                userUid = it.uid
        }

        db = FirebaseFirestore.getInstance()
        addQuery = db.collection("patients")

        btnAdd.setOnClickListener {
            addVisit()
        }

        txtDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.show(supportFragmentManager, "Date de la visite")
            picker.addOnPositiveButtonClickListener {
                if (isValidDate(it))
                    txtDate.setText(FormattedTime.dayMonthYear(it))
            }
        }
    }

    private fun isValidDate(date: Long): Boolean {
        return if (date <= Calendar.getInstance().timeInMillis)
            true
        else {
            Toast.makeText(this, "Date invalide", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun addVisit() {
        /*visit = hashMapOf(
            "firstname" to get(txtPatient),
            "actions" to get(txtActions),
            "visitor" to ref?.getField(),
            "millis" to FormattedTime.parse(get(txtDate))
        )
        visit["visitor"] = hashMapOf(

        )*/


        if (true) {
            val reference = db.collection("visits").document()

            val r = Visit(reference.id, FormattedTime.parse(get(txtDate)), get(txtReference), hashMapOf(
                "name" to "",
                "reference" to "Fontes" // DEBUG
            ), hashMapOf(
                "name" to userUid // DEBUG
            ))

            reference.set(r).addOnSuccessListener {
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