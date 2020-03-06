package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fr.isen.guillaume.mobilesecurity.misc.Encryption
import fr.isen.guillaume.mobilesecurity.model.Patient
import fr.isen.guillaume.mobilesecurity.model.Visit
import fr.isen.guillaume.mobilesecurity.recycler.VisitsAdapter
import kotlinx.android.synthetic.main.activity_patient.*
import java.util.*
import kotlin.collections.ArrayList

class PatientActivity : AppCompatActivity() {

    private lateinit var crypto: Encryption

    private lateinit var db: FirebaseFirestore
    private var visitsQuery: Query? = null

    private lateinit var adapter: VisitsAdapter

    private var patient = Patient()

    private fun exitActivity() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        val patientReference = intent.getStringExtra("reference")
        if (patientReference == null)
            exitActivity()

        crypto = Encryption.getInstance()

        adapter = VisitsAdapter(ArrayList(), this, VisitsAdapter.VisitType.PATIENT_VISITOR)

        visitsRecycler.layoutManager = LinearLayoutManager(this)
        visitsRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        visitsRecycler.adapter = adapter

        // Database reference and general query
        db = FirebaseFirestore.getInstance()
        db.collection("patients").whereEqualTo("reference", crypto.encrypt(patientReference)).get()
            .addOnSuccessListener {
                if (it.size() > 0) {
                    val p = it.documents.first().toObject(Patient::class.java)

                    if (p != null) {
                        patient = crypto.iterateDecrypt(p)

                        txtFirstname.text =
                            resources.getString(R.string.patient_firstname, patient.firstname)
                        txtLastname.text = resources.getString(
                            R.string.patient_lastname, patient.lastname?.toUpperCase(
                                Locale.ROOT
                            )
                        )
                        txtReference.text =
                            resources.getString(R.string.patient_reference, patient.reference)


                        visitsQuery = db.collection("visits")
                            .whereEqualTo("patient.reference", crypto.encrypt(p.reference))
                            .orderBy("millis", Query.Direction.DESCENDING)
                        updateVisits()

                        return@addOnSuccessListener
                    }
                }
                exitActivity()
            }.addOnFailureListener {
                Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
                exitActivity()
            }

        // FAB add visit
        floatingButtonVisit.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddVisitActivity::class.java
                ).putExtra("reference", patient.reference)
            )
        }
    }

    private fun updateVisits() {
        startLoading()

        visitsQuery?.get()?.addOnSuccessListener {
            if (it.size() <= 0) {
                endLoading()
                return@addOnSuccessListener
            }

            it.map { doc -> crypto.iterateDecrypt(doc.toObject(Visit::class.java)) }.let { items ->
                adapter.addItems(ArrayList(items))
            }

            adapter.notifyDataSetChanged()
            endLoading()
        }?.addOnFailureListener {
            endLoading()
            Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
        }
    }

    private fun endLoading() {
        loading.visibility = View.GONE
    }

    private fun startLoading() {
        loading.visibility = View.VISIBLE
    }
}