package com.trodevel.simpleevent

import java.text.SimpleDateFormat
import java.util.*

class CsvSerializer(
    private val mustExportTsAsDateTime: Boolean,
    private val separator: String = ","
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun toCsvHeader(): String {
        val timestampHeader = if (mustExportTsAsDateTime) "DATE${separator}TIME" else "Timestamp"
        return "VERSION${separator}TYPE${separator}$timestampHeader${separator}Key${separator}packageName${separator}Title${separator}Message${separator}channelId${separator}category${separator}conversationId${separator}parentChannelId${separator}isOneToOne${separator}people${separator}messaging_person${separator}conversation_title\n"
    }

    private fun dateToString(timestamp: Long): String {
        if (mustExportTsAsDateTime) {
            val date = Date(timestamp)
            return "${dateFormat.format(date)}${separator}${timeFormat.format(date)}"
        }
        return "$timestamp"
    }

    private fun baseToCsv(base: EventBase): String {
        return "\"${sanitize(base.key)}\"$separator\"${sanitize(base.channel.channelId)}\"$separator${base.channel.category}$separator\"${sanitize(base.conversations.conversationId)}\"$separator\"${sanitize(base.conversations.parentChannelId)}\"$separator\"${sanitize(base.packageName)}\"$separator\"${sanitize(base.title)}\"$separator\"${sanitize(base.message)}\""
    }

    fun toString(event: EventObject): String {
        val timestamp = dateToString(event.timestamp)

        return when (event) {
            is StandardEvent -> standardEventToCsv(timestamp, baseToCsv(event.base))
            is ExtendedEvent -> extendedEventToCsv(timestamp, baseToCsv(event.base), event)
            is RemoveEvent -> removeEventToCsv(timestamp, event.key)
        }
    }

    private fun standardEventToCsv(timestamp: String, basePart: String): String {
        return "2${separator}1${separator}${timestamp}${separator}${basePart}\n"
    }

    private fun extendedEventToCsv(timestamp: String, basePart: String, event: ExtendedEvent): String {
        val peopleJson = personListToJson(event.people)
        val messagingPersonJson = event.messagingPerson?.let { personToJson(it).toString() } ?: ""
        val extendedPart = "${event.isOneToOne}$separator\"${sanitize(peopleJson)}\"$separator\"${sanitize(messagingPersonJson)}\"$separator\"${sanitize(event.conversationTitle ?: "")}\""
        return "2${separator}2${separator}${timestamp}${separator}${basePart}${separator}${extendedPart}\n"
    }

    private fun removeEventToCsv(timestamp: String, key: String): String {
        return "1${separator}3${separator}${timestamp}${separator}\"${sanitize(key)}\"\n"
    }

    private fun personToJson(person: Person): org.json.JSONObject {
        return org.json.JSONObject().apply {
            put("name", person.name)
            put("key", person.key ?: org.json.JSONObject.NULL)
            put("uri", person.uri ?: org.json.JSONObject.NULL)
            put("isBot", person.isBot)
            put("isImportant", person.isImportant)
        }
    }

    private fun personListToJson(people: List<Person>): String {
        val array = org.json.JSONArray()
        people.forEach { array.put(personToJson(it)) }
        return array.toString()
    }

    private fun sanitize(input: String): String {
        return input
            .replace("\n", " ")
            .replace("\r", " ")
            .replace("\"", "\"\"")
    }
}
