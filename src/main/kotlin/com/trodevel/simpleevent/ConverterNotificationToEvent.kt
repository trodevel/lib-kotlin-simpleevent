package com.trodevel.simpleevent

import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import android.app.Notification
import android.os.Build
import androidx.annotation.RequiresApi
import android.app.Person as AndroidPerson
import androidx.core.app.Person as CompatPerson

object ConverterNotificationToEvent {
    fun convert(sbn: StatusBarNotification): EventBase {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: "No Title"
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: "No Content"
        val timestamp = System.currentTimeMillis()

        val conversationTitle = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
        var messagingPerson: Person? = null
        val people = mutableListOf<Person>()

        // Try to get more detailed messaging information using NotificationCompat
        val messagingStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(sbn.notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val androidPerson = extras.getParcelable<AndroidPerson>(Notification.EXTRA_MESSAGING_PERSON)
            androidPerson?.let { messagingPerson = it.toAppPerson() }

            val list = extras.getParcelableArrayList<AndroidPerson>(Notification.EXTRA_PEOPLE_LIST)
            list?.forEach { p ->
                val converted = p.toAppPerson()
                if (converted.name != "Unknown") {
                    people.add(converted)
                }
            }
        }

        // Fallback/Augment: Try MessagingStyle messages for names and participants
        messagingStyle?.messages?.forEach { msg ->
            val p = msg.person?.toAppPerson() ?: Person(
                name = msg.sender?.toString() ?: "Unknown",
                key = null,
                uri = null,
                isBot = false,
                isImportant = false
            )
            if (p.name != "Unknown") {
                people.add(p)
            }
        }

        // Fallback for messagingPerson if still null
        if (messagingPerson == null && messagingStyle != null) {
            val lastMsg = messagingStyle.messages.lastOrNull()
            if (lastMsg != null) {
                messagingPerson = lastMsg.person?.toAppPerson() ?: Person(
                    name = lastMsg.sender?.toString() ?: "Unknown",
                    key = null,
                    uri = null,
                    isBot = false,
                    isImportant = false
                )
            }
        }

        val isOneToOne = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            !extras.getBoolean(Notification.EXTRA_IS_GROUP_CONVERSATION)
        } else {
            true
        }
        val channel = initNotificationChannel(sbn)
        val conversations = initConversations(sbn)

        val base = DataEventBase(channel, conversations, packageName, title, text, sbn.key)

        return if (conversationTitle != null || messagingPerson != null || people.isNotEmpty()) {
            ExtendedEvent(
                timestamp = timestamp,
                base = base,
                isOneToOne = isOneToOne,
                people = people.distinctBy { it.key ?: it.uri ?: it.name }.filter { it.name != "Unknown" },
                messagingPerson = if (messagingPerson?.name == "Unknown") null else messagingPerson,
                conversationTitle = conversationTitle
            )
        } else {
            SimpleEvent(timestamp, base)
        }
    }

    private fun initNotificationChannel(sbn: StatusBarNotification): NotificationChannel {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sbn.notification.channelId ?: ""
        } else {
            ""
        }
        val categoryString = sbn.notification.category
        val category = CategoryConverter.fromString(categoryString)
        return NotificationChannel(channelId, category)
    }

    private fun initConversations(sbn: StatusBarNotification): Conversations {
        val conversationId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sbn.notification.shortcutId ?: ""
        } else {
            ""
        }
        return Conversations(conversationId, "")
    }

    fun toRemoveEvent(sbn: StatusBarNotification): RemoveEvent {
        return RemoveEvent(
            timestamp = System.currentTimeMillis(),
            key = sbn.key
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun AndroidPerson.toAppPerson(): Person {
        return Person(
            name = name?.toString() ?: "Unknown",
            key = key,
            uri = uri,
            isBot = isBot,
            isImportant = isImportant
        )
    }

    private fun CompatPerson.toAppPerson(): Person {
        return Person(
            name = name?.toString() ?: "Unknown",
            key = key,
            uri = uri,
            isBot = isBot,
            isImportant = isImportant
        )
    }
}
