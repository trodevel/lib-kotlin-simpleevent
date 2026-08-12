package com.trodevel.simpleevent

data class DataEventBase(
    val channel: NotificationChannel,
    val conversations: Conversations,
    val packageName: String,
    val title: String,
    val message: String,
    val key: String
)
