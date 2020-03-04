package fr.isen.guillaume.mobilesecurity.model

import android.net.Uri

class Patient() {
    var id: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var picture: Uri? = null

    constructor(pId: String, pFirstname: String, pLastname: String, pPicture: Uri?) : this() {
        id = pId
        firstname = pFirstname
        lastname = pLastname
        picture = pPicture
    }

    fun setIdAnd(pId: String): Patient {
        id = pId
        return this
    }
}