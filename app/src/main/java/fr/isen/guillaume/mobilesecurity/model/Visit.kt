package fr.isen.guillaume.mobilesecurity.model

class Visit() {
    var millis: Long? = null
    var visitor: Visitor? = null
    var actions: String? = null

    constructor(pMillis: Long, pVisitor: Visitor, pActions: String) : this() {
        millis = pMillis
        visitor = pVisitor
        actions = pActions
    }
}