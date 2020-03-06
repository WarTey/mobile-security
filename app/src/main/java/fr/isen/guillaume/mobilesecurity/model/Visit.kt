package fr.isen.guillaume.mobilesecurity.model

class Visit() {
    var id: String? = null
    var millis: Long? = null
    var actions: String? = null

    var patient = HashMap<String, String>()
    var visitor = HashMap<String, String>()

    constructor(
        pId: String,
        pMillis: Long,
        pActions: String,
        pPatient: HashMap<String, String>,
        pVisitor: HashMap<String, String>
    ) : this() {
        id = pId
        millis = pMillis
        actions = pActions
        patient.putAll(pPatient)
        visitor.putAll(pVisitor)
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