package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.model.Pending
import fr.isen.guillaume.mobilesecurity.recycler.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_pending_mode.*

class PendingModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_mode)

        initRecycler()
    }

    private fun initRecycler() {
        val firestore = FirebaseFirestore.getInstance()
        val pendingRef = firestore.collection("pending")
        val pendingQuery = pendingRef.whereEqualTo("status", "InProgress").orderBy("email", Query.Direction.ASCENDING)
        val pending = ArrayList<Pending>()

        pendingQuery.get().addOnSuccessListener {
            fillPending(it, pending)
            recyclerPending.layoutManager = LinearLayoutManager(this)
            recyclerPending.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            recyclerPending.adapter = RecyclerAdapter(pending, this, true)
        }.addOnFailureListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            StyleableToast.makeText(this, getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
        }

        pendingRef.addSnapshotListener { _, _ ->
            pendingQuery.get().addOnSuccessListener {
                pending.clear()
                fillPending(it, pending)
                recyclerPending.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun fillPending(it: QuerySnapshot, pending: ArrayList<Pending>) {
        for (document in it)
            if (document.data["status"] == "InProgress")
                pending.add(Pending(document.data["email"] as String, document.data["key"] as String, document.data["status"] as String))
    }
}
