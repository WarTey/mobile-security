package fr.isen.guillaume.mobilesecurity.model

class Visit() {
    var id: String? = null
    var millis: Long? = null
    var visitor: Visitor? = null
    var actions: String? = null

    constructor(pId: String, pMillis: Long, pVisitor: Visitor, pActions: String) : this() {
        id = pId
        millis = pMillis
        visitor = pVisitor
        actions = pActions
    }

    fun setIdAnd(pId: String): Visit {
        id = pId
        return this
    }
}