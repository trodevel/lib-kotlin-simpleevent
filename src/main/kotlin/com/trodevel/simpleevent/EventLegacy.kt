package com.trodevel.simpleevent

data class LegacyEvent(
    val timestamp: Long,
    val packageName: String,
    val title: String,
    val message: String
)

data class LegacyExtendedEvent(
    val timestamp: Long,
    val packageName: String,
    val title: String,
    val message: String,
    val people: String,
    val messagingPerson: String,
    val conversationTitle: String
)
