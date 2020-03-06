package fr.isen.guillaume.mobilesecurity.model

import android.net.Uri

class Patient() {
    var id: String? = null

    var reference: String? = null
    var firstname: String? = null
    var lastname: String? = null

    var pathology: String? = null
    var treatment: String? = null

    var picture: Uri? = null

    constructor(
        pId: String,
        pReference: String,
        pFirstname: String,
        pLastname: String,
        pPathology: String,
        pTreatment: String,
        pPicture: Uri?
    ) : this() {
        id = pId
        reference = pReference
        firstname = pFirstname
        lastname = pLastname
        pathology = pPathology
        treatment = pTreatment
        picture = pPicture
    }

    fun setIdAnd(pId: String): Patient {
        id = pId
        return this
    }


    companion object {
        fun isValid(map: HashMap<String, Comparable<*>?>?): Boolean {
            if (map == null)
                return false
            return checkField(map["reference"]) && checkField(map["firstname"]) && checkField(map["lastname"])
        }

        private fun checkField(field: Comparable<*>?): Boolean {
            return field != null && field != ""
        }
    }
}