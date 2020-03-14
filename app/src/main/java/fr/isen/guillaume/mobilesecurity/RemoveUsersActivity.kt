package fr.isen.guillaume.mobilesecurity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.lib.AntiDebug
import fr.isen.guillaume.mobilesecurity.lib.Emulator
import fr.isen.guillaume.mobilesecurity.lib.Monkey
import fr.isen.guillaume.mobilesecurity.lib.Runtime
import fr.isen.guillaume.mobilesecurity.misc.StartActivity
import fr.isen.guillaume.mobilesecurity.misc.Verification
import fr.isen.guillaume.mobilesecurity.model.Pending
import fr.isen.guillaume.mobilesecurity.recycler.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_pending_mode.*
import kotlin.system.exitProcess

class RemoveUsersActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_mode)
        txtDashboard.text = getString(R.string.remove_register)

        if (Verification().isRooted(this) || Verification().isEmulator() || Runtime().isHooked() || AntiDebug().isDebugged() || Monkey().isUserAMonkey() || Emulator().isQEmuEnvDetected())
            exitProcess(0)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified) Verification().checkPending(
            FirebaseAuth.getInstance(), this)
        else StartActivity().goToLogin(this)

        initRecycler()
    }

    private fun initRecycler() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        val pendingRef = firestore.collection("pending")
        val pending = ArrayList<Pending>()

        pendingRef.get().addOnSuccessListener {
            fillPending(it, pending)
            recyclerPending.layoutManager = LinearLayoutManager(this)
            recyclerPending.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            recyclerPending.adapter = RecyclerAdapter(pending, this, false)
        }.addOnFailureListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            StyleableToast.makeText(this, getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
        }

        pendingRef.addSnapshotListener { _, _ ->
            pendingRef.get().addOnSuccessListener {
                pending.clear()
                fillPending(it, pending)
                recyclerPending.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun fillPending(it: QuerySnapshot, pending: ArrayList<Pending>) {
        for (document in it)
            if ((document.data["status"] == "user" || document.data["status"] == "admin") && document.data["email"].toString() != FirebaseAuth.getInstance().currentUser?.email)
                pending.add(Pending(document.data["email"] as String, document.data["key"] as String, document.data["sym"] as String, document.data["status"] as String))
    }
}
