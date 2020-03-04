package fr.isen.guillaume.mobilesecurity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fr.isen.guillaume.mobilesecurity.model.Visit
import fr.isen.guillaume.mobilesecurity.recycler.VisitsAdapter
import kotlinx.android.synthetic.main.activity_visits.*

class VisitsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var visitsQuery: Query

    private lateinit var adapter: VisitsAdapter

    private var modeLoaded: Int = 0
    private var lastId: String = ""

    companion object {
        private const val LIMIT_LOAD: Long = 10L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visits)

        adapter = VisitsAdapter(ArrayList(), this)

        recyclerVisits.layoutManager = LinearLayoutManager(this)
        recyclerVisits.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerVisits.adapter = adapter

        db = FirebaseFirestore.getInstance()
        visitsQuery =
            db.collection("visits").orderBy("millis", Query.Direction.DESCENDING).limit(LIMIT_LOAD)
        getMine(false)

        // La re-sélection n'a pas d'effet
        bottomNavigationBackup.setOnNavigationItemReselectedListener { }
        // À la sélection de l'autre mode
        bottomNavigationBackup.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener updateVisits(it.itemId)
        }
    }


    private fun getMine(replace: Boolean) {
        loading.visibility = View.VISIBLE

        visitsQuery.whereEqualTo("visitor.name", "pascal").get().addOnSuccessListener {
            val list = ArrayList<Visit>()

            for (document in it) {
                val obj = document.toObject(Visit::class.java)
                obj.id = document.id

                lastId = document.id
                list.add(obj)
            }

            if (replace)
                adapter.updateItems(list)
            else
                adapter.addItems(list)

            adapter.notifyDataSetChanged()
            loading.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAll(replace: Boolean) {
        loading.visibility = View.VISIBLE

        visitsQuery.get().addOnSuccessListener {
            val list = ArrayList<Visit>()

            for (document in it) {
                val obj = document.toObject(Visit::class.java)
                obj.id = document.id

                lastId = document.id
                list.add(obj)
            }

            if (replace)
                adapter.updateItems(list)
            else
                adapter.addItems(list)

            adapter.notifyDataSetChanged()
            loading.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateVisits(id: Int): Boolean {
        if (id == modeLoaded)
            return false

        modeLoaded = id
        if (id == R.id.itemMe)
            getMine(true)
        else
            getAll(true)

        return true
    }
}