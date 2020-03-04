package fr.isen.guillaume.mobilesecurity.model

import android.net.Uri

class Patient() {
    var firstname: String? = null
    var lastname: String? = null
    var picture: Uri? = null

    constructor(pFirstname: String, pLastname: String, pPicture: Uri?) : this() {
        firstname = pFirstname
        lastname = pLastname
        picture = pPicture
    }
}