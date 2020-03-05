package fr.isen.guillaume.mobilesecurity.recycler

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.R
import fr.isen.guillaume.mobilesecurity.model.Pending
import kotlinx.android.synthetic.main.recyclerview_pending.view.*

class PendingViewHolder(itemView: View, context: Context, pendingMode: Boolean) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            if (pendingMode) {
                val message = context.getString(R.string.registration_accepted)
                MaterialAlertDialogBuilder(context).setTitle("Acceptez-vous l'inscription ?").setPositiveButton("Administrateur")
                { _, _ -> makeRequest("admin", context, message) }.setNegativeButton("Utilisateur")
                { _, _ -> makeRequest("user", context, message) }.show()
            } else {
                MaterialAlertDialogBuilder(context).setTitle("Désactiver l'inscription ?").setPositiveButton("Oui")
                { _, _ -> makeRequest("InProgress", context, context.getString(R.string.pending_register)) }.show()
            }
        }
    }

    private fun makeRequest(type: String, context: Context, message: String) {
        val firestore = FirebaseFirestore.getInstance()
        val pendingRef = firestore.collection("pending").document(itemView.txtEmail.text.toString())

        pendingRef.update("status", type).addOnSuccessListener {
            StyleableToast.makeText(context, context.getString(R.string.registration_accepted), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
        }.addOnFailureListener {
            StyleableToast.makeText(context, context.getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
        }
    }

    fun bindPending(pending: Pending) {
        itemView.txtEmail.text = pending.email
    }
}