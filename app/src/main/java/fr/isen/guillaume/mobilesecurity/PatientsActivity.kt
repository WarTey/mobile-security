package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fr.isen.guillaume.mobilesecurity.model.Patient
import fr.isen.guillaume.mobilesecurity.recycler.PatientsAdapter
import kotlinx.android.synthetic.main.activity_patients.*

class PatientsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var patientsQuery: Query

    private lateinit var adapter: PatientsAdapter

    private var lastId: DocumentSnapshot? = null
    private var isLoading: Boolean = false

    companion object {
        private const val LIMIT_LOAD: Long = 10L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patients)

        adapter = PatientsAdapter(ArrayList(), this)

        recyclerPatients.layoutManager = LinearLayoutManager(this)
        recyclerPatients.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerPatients.adapter = adapter

        // Database reference and general query
        db = FirebaseFirestore.getInstance()
        patientsQuery = db.collection("patients").orderBy("lastname").limit(LIMIT_LOAD)

        // Load first patients
        updatePatients()

        // FAB to add a patient
        floatingButtonPatients.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddPatientActivity::class.java
                )
            )
        }

        // (Almost) infinite scroll
        recyclerPatients.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1))
                    updatePatients()
            }
        })
    }

    private fun updatePatients(): Boolean {
        if (!isLoading) {
            startLoading()

            patientsQuery.run {
                // Pagination if needed
                return@run if (lastId == null) this else this.startAfter(lastId!!)
            }.get().addOnSuccessListener {
                if (it.size() <= 0) {
                    endLoading()
                    return@addOnSuccessListener
                }

                lastId = it.documents.last()
                it.map { doc -> doc.toObject(Patient::class.java).setIdAnd(doc.id) }.let { items ->
                    adapter.addItems(ArrayList(items))
                }

                adapter.notifyDataSetChanged()
                endLoading()
            }.addOnFailureListener {
                Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
            }

            return true
        }

        return false
    }

    private fun endLoading() {
        isLoading = false
        loading.visibility = View.GONE
    }

    private fun startLoading() {
        isLoading = true
        loading.visibility = View.VISIBLE
    }
}