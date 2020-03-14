package fr.isen.guillaume.mobilesecurity.misc

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.scottyab.rootbeer.RootBeer

class Verification {

    fun isRooted(context: Context): Boolean {
        val rootBeer = RootBeer(context)
        return rootBeer.isRooted || rootBeer.isRootedWithoutBusyBoxCheck
    }

    fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic")
    }

    fun checkPending(firebaseAuth: FirebaseAuth, appCompatActivity: AppCompatActivity) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
        val pendingRef = firebaseAuth.currentUser?.email?.let { firestore.collection("pending").document(it) }

        pendingRef?.get()?.addOnSuccessListener {
            if (it.data?.get("status").toString() == "InProgress")
                StartActivity().goToLogin(appCompatActivity)
        }
    }
}