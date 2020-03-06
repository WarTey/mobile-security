package fr.isen.guillaume.mobilesecurity.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class Pending(var email: String, var key: String, var status: String)