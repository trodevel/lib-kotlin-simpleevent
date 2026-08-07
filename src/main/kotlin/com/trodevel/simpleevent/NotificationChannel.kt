package com.trodevel.simpleevent

data class NotificationChannel(
    val channelId: String,
    val category: Int,
    val conversationId: String?,
    val parentChannelId: String?,
)
