package fr.isen.guillaume.mobilesecurity.model

import com.google.firebase.firestore.DocumentReference

class Visit() {
    var id: String? = null
    var millis: Long? = null
    var actions: String? = null

    lateinit var visitor: DocumentReference
    lateinit var patient: DocumentReference

    constructor(pId: String, pMillis: Long, pActions: String) : this() {
        id = pId
        millis = pMillis
        actions = pActions
    }

    fun setIdAnd(pId: String): Visit {
        id = pId
        return this
    }

    companion object {
        fun isValid(map: HashMap<String, Comparable<*>?>?): Boolean {
            if (map == null)
                return false
            return checkField(map["millis"]) && checkField(map["actions"])
        }

        private fun checkField(field: Comparable<*>?): Boolean {
            return field != null && field != ""
        }
    }
}