package com.trodevel.simpleevent

sealed class EventObject {
    abstract val timestamp: Long
}

data class RemoveEvent(
    override val timestamp: Long,
    val key: String
) : EventObject()

sealed class Event : EventObject() {
    abstract val base: EventBase
    abstract override val timestamp: Long
    val packageName: String get() = base.packageName
    val title: String get() = base.title
    val message: String get() = base.message
    val key: String get() = base.key
}

data class StandardEvent(
    override val timestamp: Long,
    override val base: EventBase
) : Event()

data class ExtendedEvent(
    override val timestamp: Long,
    override val base: EventBase,
    val isOneToOne: Boolean,
    val people: List<Person>,
    val messagingPerson: Person?,
    val conversationTitle: String?
) : Event()
