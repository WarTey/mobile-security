package fr.isen.guillaume.mobilesecurity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import fr.isen.guillaume.mobilesecurity.misc.FormattedTime
import fr.isen.guillaume.mobilesecurity.model.Visit
import fr.isen.guillaume.mobilesecurity.model.Visitor
import kotlinx.android.synthetic.main.activity_add_visit.*

class AddVisitActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var addQuery: CollectionReference

    private var visit: HashMap<String, Comparable<*>?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visit)

        db = FirebaseFirestore.getInstance()
        addQuery = db.collection("patients")

        btnAdd.setOnClickListener {
            addVisit()
        }
    }

    private fun addVisit() {
        /*
        val r = Visit("", FormattedTime.parse(get(txtDate)), ref, "NON")
        /*visit = hashMapOf(
            "firstname" to get(txtPatient),
            "actions" to get(txtActions),
            "visitor" to ref?.getField(),
            "millis" to FormattedTime.parse(get(txtDate))
        )
        visit["visitor"] = hashMapOf(

        )*/

        if (visit != null && Visit.isValid(visit)) {
            addQuery.add(visit!!).addOnSuccessListener {
                Toast.makeText(this, "Ajouté !", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Champ(s) invalide(s)", Toast.LENGTH_SHORT).show()
        }*/
    }

    private fun get(par: TextInputEditText): String {
        return par.text.toString()
    }
}