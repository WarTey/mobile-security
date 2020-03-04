package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.guillaume.mobilesecurity.model.Patient
import fr.isen.guillaume.mobilesecurity.recycler.PatientsAdapter
import kotlinx.android.synthetic.main.activity_patients.*

class PatientsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patients)

        val patients = ArrayList<Patient>()
        patients.add(Patient("Jean", "Dupont", null))

        recyclerPatients.layoutManager = LinearLayoutManager(this)
        recyclerPatients.adapter = PatientsAdapter(patients, this)

        floatingButtonPatients.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddPatientActivity::class.java
                )
            )
        }
    }
}