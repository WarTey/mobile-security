package fr.isen.guillaume.mobilesecurity

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
import fr.isen.guillaume.mobilesecurity.model.Visit
import fr.isen.guillaume.mobilesecurity.recycler.VisitsAdapter
import kotlinx.android.synthetic.main.activity_visits.*

class VisitsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var visitsQuery: Query

    private lateinit var adapter: VisitsAdapter

    private var modeLoaded: Int = 0
    private var lastId: DocumentSnapshot? = null
    private var isLoading: Boolean = false

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

        // Database reference and general query
        db = FirebaseFirestore.getInstance()
        visitsQuery =
            db.collection("visits").orderBy("millis", Query.Direction.DESCENDING).limit(LIMIT_LOAD)

        // Load first visits
        modeLoaded = bottomNavigationBackup.selectedItemId
        updateVisits(modeLoaded, false)

        // Re-select has no effect
        bottomNavigationBackup.setOnNavigationItemReselectedListener { }
        // Mode selection
        bottomNavigationBackup.setOnNavigationItemSelectedListener {
            if (it.itemId == modeLoaded)
                return@setOnNavigationItemSelectedListener false

            lastId = null
            modeLoaded = it.itemId

            recyclerVisits.scrollToPosition(0)
            return@setOnNavigationItemSelectedListener updateVisits(it.itemId, true)
        }

        // (Almost) infinite scroll
        recyclerVisits.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1))
                    updateVisits(modeLoaded, false)
            }
        })
    }


    private fun execQuery(query: Query, replace: Boolean) {
        query.get().addOnSuccessListener {
            if (it.size() <= 0) {
                endLoading()
                return@addOnSuccessListener
            }

            lastId = it.documents.last()
            it.map { doc -> doc.toObject(Visit::class.java).setIdAnd(doc.id) }.let { items ->
                if (replace)
                    adapter.updateItems(ArrayList(items))
                else
                    adapter.addItems(ArrayList(items))
            }

            adapter.notifyDataSetChanged()
            endLoading()
        }.addOnFailureListener {
            Toast.makeText(this, "Erreur BDD", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateVisits(id: Int, replace: Boolean): Boolean {
        if (!isLoading) {
            startLoading()

            execQuery(visitsQuery.run {
                // Mine or All
                // DEBUG: get visitor name
                return@run if (id == R.id.itemMe) whereEqualTo("visitor.name", "pascal") else this
            }.run {
                // Pagination if needed
                return@run if (lastId == null) this else this.startAfter(lastId!!)
            }, replace)

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