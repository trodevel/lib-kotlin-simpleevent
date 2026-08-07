package com.trodevel.simpleevent

data class NotificationChannel(
    val channelId: String,
    val category: Category,
)

data class Conversations(
    val conversationId: String,
    val parentChannelId: String,
)
