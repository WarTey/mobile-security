package fr.isen.guillaume.mobilesecurity.recycler

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muddzdev.styleabletoast.StyleableToast
import fr.isen.guillaume.mobilesecurity.R
import fr.isen.guillaume.mobilesecurity.model.Pending
import kotlinx.android.synthetic.main.recyclerview_pending.view.*
import java.lang.StringBuilder
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

class PendingViewHolder(itemView: View, context: Context, pendingMode: Boolean) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.setOnClickListener {
            if (pendingMode) {
                MaterialAlertDialogBuilder(context).setTitle("Acceptez-vous l'inscription ?").setPositiveButton("Administrateur")
                { _, _ -> setKey("admin", context) }.setNegativeButton("Utilisateur")
                { _, _ -> setKey("user", context) }.show()
            } else {
                MaterialAlertDialogBuilder(context).setTitle("Désactiver l'inscription ?").setPositiveButton("Oui")
                { _, _ -> makeUpdate("InProgress", context, "null") }.show()
            }
        }
    }

    private fun makeUpdate(type: String, context: Context, key: String) {
        val firestore = FirebaseFirestore.getInstance()
        val pendingRef = firestore.collection("pending").document(itemView.txtEmail.text.toString())

        pendingRef.update("key", key,"status", type).addOnSuccessListener {
            StyleableToast.makeText(context, context.getString(R.string.registration_accepted), Toast.LENGTH_LONG, R.style.StyleToastSuccess).show()
        }.addOnFailureListener {
            StyleableToast.makeText(context, context.getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
        }
    }

    private fun setKey(type: String, context: Context) {
        val firestore = FirebaseFirestore.getInstance()
        val pendingRef = firestore.collection("pending").document(itemView.txtEmail.text.toString())

        pendingRef.get().addOnSuccessListener { user ->
            val adminRef = firestore.collection("pending").document(FirebaseAuth.getInstance().currentUser?.email.toString())
            adminRef.get().addOnSuccessListener {
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                val keyPair = keyStore.getEntry("ProjectMobileSecurity", null) as KeyStore.PrivateKeyEntry

                val keyAdmin = it.data?.get("key").toString()
                val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
                cipher.init(Cipher.DECRYPT_MODE, keyPair.privateKey)
                val keyAES = cipher.doFinal(Base64.decode(keyAdmin, Base64.DEFAULT))

                val keyUser = user.data?.get("key").toString()
                val publicKey = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(Base64.decode(keyUser, Base64.DEFAULT)))
                cipher.init(Cipher.ENCRYPT_MODE, publicKey)
                val keyAESCipher = cipher.doFinal(keyAES)

                makeUpdate(type, context, Base64.encodeToString(keyAESCipher, Base64.DEFAULT))
            }.addOnFailureListener {
                StyleableToast.makeText(context, context.getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
            }
        }.addOnFailureListener {
            StyleableToast.makeText(context, context.getString(R.string.error_registration), Toast.LENGTH_LONG, R.style.StyleToastFail).show()
        }
    }

    fun bindPending(pending: Pending) {
        itemView.txtEmail.text = pending.email
    }
}