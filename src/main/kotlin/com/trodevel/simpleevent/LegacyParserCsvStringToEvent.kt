package com.trodevel.simpleevent

import org.json.JSONArray
import org.json.JSONObject

class LegacyParserCsvStringToEvent(private val separator: String) {

    fun parse(parts: List<String>): EventObject? {
        if (parts.size < 2) return null
        val secondColInt = parts[1].toIntOrNull()
        return if (secondColInt != null && secondColInt in 1..3) {
            parseNewFormat(secondColInt, parts)
        } else {
            parseOldFormat(parts)
        }
    }

    private fun parseNewFormat(type: Int, parts: List<String>): EventObject? {
        return when (type) {
            1 -> parseStandardEvent(parts)
            2 -> parseLegacyExtendedEvent(parts)
            3 -> parseExtendedEvent(parts)
            else -> null
        }
    }

    private fun parseOldFormat(parts: List<String>): StandardEvent? {
        if (parts.size < 4) return null
        val timestamp = parts[0].toLongOrNull() ?: return null
        val title = unescape(parts[2])
        val base = EventBase(
            packageName = unescape(parts[1]),
            title = title,
            message = unescape(parts.subList(3, parts.size).joinToString(separator)),
            key = title
        )
        return StandardEvent(timestamp, base)
    }

    private fun parseStandardEvent(parts: List<String>): StandardEvent? {
        if (parts.size < 5) return null
        val timestamp = parts[0].toLongOrNull() ?: return null
        val title = unescape(parts[3])
        val base = EventBase(
            packageName = unescape(parts[2]),
            title = title,
            message = unescape(parts[4]),
            key = title
        )
        return StandardEvent(timestamp, base)
    }

    private fun parseLegacyExtendedEvent(parts: List<String>): ExtendedEvent? {
        if (parts.size < 5) return null
        val timestamp = parts[0].toLongOrNull() ?: return null
        val title = unescape(parts[3])
        val base = EventBase(
            packageName = unescape(parts[2]),
            title = title,
            message = unescape(parts[4]),
            key = title
        )
        val peopleStr = if (parts.size > 5) unescape(parts[5]) else ""
        val messagingPersonStr = if (parts.size > 6) unescape(parts[6]) else ""
        val conversationTitle = if (parts.size > 7) unescape(parts[7]) else ""

        val people = if (peopleStr.isNotEmpty()) {
            peopleStr.split(",").map { Person(it.trim(), null, null, false, false) }
        } else emptyList()
        val messagingPerson = if (messagingPersonStr.isNotEmpty()) {
            Person(messagingPersonStr, null, null, false, false)
        } else null

        return ExtendedEvent(timestamp, base, people, messagingPerson, conversationTitle.takeIf { it.isNotEmpty() })
    }

    private fun parseExtendedEvent(parts: List<String>): ExtendedEvent? {
        if (parts.size < 5) return null
        val timestamp = parts[0].toLongOrNull() ?: return null
        val title = unescape(parts[3])
        val base = EventBase(
            packageName = unescape(parts[2]),
            title = title,
            message = unescape(parts[4]),
            key = title
        )
        val peopleStr = if (parts.size > 5) unescape(parts[5]) else ""
        val messagingPersonStr = if (parts.size > 6) unescape(parts[6]) else ""
        val conversationTitle = if (parts.size > 7) unescape(parts[7]) else ""

        val people = parsePeopleJson(peopleStr)
        val messagingPerson = parsePersonJson(messagingPersonStr)
        return ExtendedEvent(timestamp, base, people, messagingPerson, conversationTitle.takeIf { it.isNotEmpty() } )
    }

    private fun parsePersonJson(jsonStr: String): Person? {
        if (jsonStr.isEmpty()) return null
        return try {
            val json = JSONObject(jsonStr)
            Person(
                name = json.getString("name"),
                key = if (json.has("key") && !json.isNull("key")) json.getString("key") else null,
                uri = if (json.has("uri") && !json.isNull("uri")) json.getString("uri") else null,
                isBot = json.optBoolean("isBot", false),
                isImportant = json.optBoolean("isImportant", false)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parsePeopleJson(jsonStr: String): List<Person> {
        if (jsonStr.isEmpty()) return emptyList()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Person>()
            for (i in 0 until array.length()) {
                val json = array.getJSONObject(i)
                list.add(Person(
                    name = json.getString("name"),
                    key = if (json.has("key") && !json.isNull("key")) json.getString("key") else null,
                    uri = if (json.has("uri") && !json.isNull("uri")) json.getString("uri") else null,
                    isBot = json.optBoolean("isBot", false),
                    isImportant = json.optBoolean("isImportant", false)
                ))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun unescape(input: String): String {
        return input.removeSurrounding("\"").replace("\"\"", "\"")
    }
}
