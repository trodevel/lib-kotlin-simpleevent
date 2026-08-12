package com.trodevel.simpleevent

sealed class Event {
    abstract val timestamp: Long
}

data class RemoveEvent(
    override val timestamp: Long,
    val key: String
) : Event()

sealed class EventBase : Event() {
    abstract val base: DataEventBase
    abstract override val timestamp: Long
    val packageName: String get() = base.packageName
    val title: String get() = base.title
    val message: String get() = base.message
    val key: String get() = base.key
}

data class SimpleEvent(
    override val timestamp: Long,
    override val base: DataEventBase
) : EventBase()

data class ExtendedEvent(
    override val timestamp: Long,
    override val base: DataEventBase,
    val isOneToOne: Boolean,
    val people: List<Person>,
    val messagingPerson: Person?,
    val conversationTitle: String?
) : EventBase()
