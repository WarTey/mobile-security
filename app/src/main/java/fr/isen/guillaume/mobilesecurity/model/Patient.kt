package fr.isen.guillaume.mobilesecurity.model

import android.net.Uri

class Patient() {
    var id: String? = null

    var reference: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var picture: Uri? = null

    constructor(pId: String, pReference: String, pFirstname: String, pLastname: String, pPicture: Uri?) : this() {
        id = pId
        reference = pReference
        firstname = pFirstname
        lastname = pLastname
        picture = pPicture
    }

    fun setIdAnd(pId: String): Patient {
        id = pId
        return this
    }

    fun toMap(): HashMap<String, Comparable<*>?> {
        return hashMapOf(
            "reference" to reference,
            "firstname" to firstname,
            "lastname" to lastname,
            "picture" to picture
        )
    }

    companion object {
        fun isValid(map: HashMap<String, Comparable<*>?>?): Boolean {
            if (map == null)
                return false
            return checkField(map["reference"]) && checkField(map["firstname"]) && checkField(map["lastname"] != null)
        }

        private fun checkField(field: Comparable<*>?): Boolean {
            return field != null && field != ""
        }
    }
}