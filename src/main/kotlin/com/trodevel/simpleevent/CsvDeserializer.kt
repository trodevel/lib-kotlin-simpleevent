package com.trodevel.simpleevent

import org.json.JSONArray
import org.json.JSONObject

class CsvDeserializer(private val separator: String) {

    private val legacyParser = LegacyCsvDeserializer(separator)

    fun toObject(line: String): EventObject? {
        val parts = line.split(separator)
        if (parts.isEmpty()) return null

        return try {
            val firstColInt = parts[0].toIntOrNull()
            if (firstColInt != null && firstColInt in 0..100) {
                parseNewFormat(parts)
            } else {
                legacyParser.toObject(parts)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseNewFormat(parts: List<String>): EventObject? {
        if (parts.size < 4) return null
        val type = parts[1].toIntOrNull() ?: return null
        val timestamp = parts[2].toLongOrNull() ?: return null

        return when (type) {
            1 -> parseStandardEventNew(timestamp, parts)
            2 -> parseExtendedEventNew(timestamp, parts)
            3 -> parseRemoveEventNew(timestamp, parts)
            else -> null
        }
    }

    private fun parseStandardEventNew(timestamp: Long, parts: List<String>): StandardEvent? {
        if (parts.size < 7) return null
        val base = EventBase(
            key = unescape(parts[3]),
            packageName = unescape(parts[4]),
            title = unescape(parts[5]),
            message = unescape(parts[6])
        )
        return StandardEvent(timestamp, base)
    }

    private fun parseExtendedEventNew(timestamp: Long, parts: List<String>): ExtendedEvent? {
        if (parts.size < 7) return null
        val base = EventBase(
            key = unescape(parts[3]),
            packageName = unescape(parts[4]),
            title = unescape(parts[5]),
            message = unescape(parts[6])
        )
        val peopleStr = if (parts.size > 7) unescape(parts[7]) else ""
        val messagingPersonStr = if (parts.size > 8) unescape(parts[8]) else ""
        val conversationTitle = if (parts.size > 9) unescape(parts[9]) else ""

        val people = parsePeopleJson(peopleStr)
        val messagingPerson = parsePersonJson(messagingPersonStr)
        return ExtendedEvent(timestamp, base, people, messagingPerson, conversationTitle.takeIf { it.isNotEmpty() })
    }

    private fun parseRemoveEventNew(timestamp: Long, parts: List<String>): RemoveEvent? {
        if (parts.size < 4) return null
        return RemoveEvent(timestamp, unescape(parts[3]))
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
