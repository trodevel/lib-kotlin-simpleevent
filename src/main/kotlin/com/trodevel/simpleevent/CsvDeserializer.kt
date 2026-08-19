package com.trodevel.simpleevent

import org.json.JSONArray
import org.json.JSONObject

class CsvDeserializer(private val separator: String) {

    private val legacyParser = LegacyCsvDeserializer(separator)

    fun toObject(line: String): Event? {
        val parts = line.split(separator)
        if (parts.isEmpty()) return null

        return try {
            val version = parts[0].toIntOrNull()
            if (version != null && version in 0..100) {
                parseNewFormat(version, parts)
            } else {
                legacyParser.toObject(parts)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseNewFormat(version: Int, parts: List<String>): Event? {
        if (parts.size < 4) return null
        val type = parts[1].toIntOrNull() ?: return null
        val timestamp = parts[2].toLongOrNull() ?: return null

        return when (type) {
            1 -> parseSimpleEvent(version, timestamp, parts)
            2 -> parseExtendedEvent(version, timestamp, parts)
            3 -> parseRemoveEvent(version, timestamp, parts)
            else -> null
        }
    }

    private fun parseSimpleEvent(version: Int, timestamp: Long, parts: List<String>): SimpleEvent? {
        return when (version) {
            1 -> parseSimpleEvent_1(timestamp, parts)
            2 -> parseSimpleEvent_2(timestamp, parts)
            3 -> parseSimpleEvent_3(timestamp, parts)
            else -> null
        }
    }

    private fun parseSimpleEvent_1(timestamp: Long, parts: List<String>): SimpleEvent? {
        if (parts.size < 7) return null
        val base = DataEventBase(
            channel = NotificationChannel("", CategoryExt(Category.OTHER, "")),
            conversations = Conversations("", ""),
            key = unescape(parts[3]),
            packageName = unescape(parts[4]),
            title = unescape(parts[5]),
            message = unescape(parts[6])
        )
        return SimpleEvent(timestamp, base)
    }

    private fun parseSimpleEvent_2(timestamp: Long, parts: List<String>): SimpleEvent? {
        val base = parseDataEventBase_2(parts) ?: return null
        return SimpleEvent(timestamp, base)
    }

    private fun parseSimpleEvent_3(timestamp: Long, parts: List<String>): SimpleEvent? {
        val base = parseDataEventBase_3(parts) ?: return null
        return SimpleEvent(timestamp, base)
    }

    private fun parseExtendedEvent(version: Int, timestamp: Long, parts: List<String>): ExtendedEvent? {
        return when (version) {
            1 -> parseExtendedEvent_1(timestamp, parts)
            2 -> parseExtendedEvent_2(timestamp, parts)
            3 -> parseExtendedEvent_3(timestamp, parts)
            else -> null
        }
    }

    private fun parseExtendedEvent_1(timestamp: Long, parts: List<String>): ExtendedEvent? {
        if (parts.size < 7) return null
        val base = DataEventBase(
            channel = NotificationChannel("", CategoryExt(Category.OTHER, "")),
            conversations = Conversations("", ""),
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
        return ExtendedEvent(timestamp, base, false, people, messagingPerson, conversationTitle.takeIf { it.isNotEmpty() })
    }

    private fun parseExtendedEvent_2(timestamp: Long, parts: List<String>): ExtendedEvent? {
        val base = parseDataEventBase_2(parts) ?: return null
        val isOneToOne = parts.getOrNull(11)?.toBoolean() ?: false
        val peopleStr = if (parts.size > 12) unescape(parts[12]) else ""
        val messagingPersonStr = if (parts.size > 13) unescape(parts[13]) else ""
        val conversationTitle = if (parts.size > 14) unescape(parts[14]) else ""

        val people = parsePeopleJson(peopleStr)
        val messagingPerson = parsePersonJson(messagingPersonStr)
        return ExtendedEvent(timestamp, base, isOneToOne, people, messagingPerson, conversationTitle.takeIf { it.isNotEmpty() })
    }

    private fun parseExtendedEvent_3(timestamp: Long, parts: List<String>): ExtendedEvent? {
        val base = parseDataEventBase_3(parts) ?: return null
        val isOneToOne = parts.getOrNull(12)?.toBoolean() ?: false
        val peopleStr = if (parts.size > 13) unescape(parts[13]) else ""
        val messagingPersonStr = if (parts.size > 14) unescape(parts[14]) else ""
        val conversationTitle = if (parts.size > 15) unescape(parts[15]) else ""

        val people = parsePeopleJson(peopleStr)
        val messagingPerson = parsePersonJson(messagingPersonStr)
        return ExtendedEvent(timestamp, base, isOneToOne, people, messagingPerson, conversationTitle.takeIf { it.isNotEmpty() })
    }

    private fun parseDataEventBase_2(parts: List<String>): DataEventBase? {
        if (parts.size < 11) return null
        return DataEventBase(
            key = unescape(parts[3]),
            channel = NotificationChannel(
                channelId = unescape(parts[4]),
                category = parseCategoryExt_2(parts[5])
            ),
            conversations = Conversations(
                conversationId = unescape(parts[6]),
                parentChannelId = unescape(parts[7])
            ),
            packageName = unescape(parts[8]),
            title = unescape(parts[9]),
            message = unescape(parts[10])
        )
    }

    private fun parseDataEventBase_3(parts: List<String>): DataEventBase? {
        if (parts.size < 12) return null
        return DataEventBase(
            key = unescape(parts[3]),
            channel = NotificationChannel(
                channelId = unescape(parts[4]),
                category = parseCategoryExt_3(parts[5], parts[6])
            ),
            conversations = Conversations(
                conversationId = unescape(parts[7]),
                parentChannelId = unescape(parts[8])
            ),
            packageName = unescape(parts[9]),
            title = unescape(parts[10]),
            message = unescape(parts[11])
        )
    }

    private fun parseCategoryExt_2(categoryPart: String): CategoryExt {
        val category = try { Category.valueOf(unescape(categoryPart)) } catch (e: Exception) { Category.OTHER }
        return CategoryExt(category, "")
    }

    private fun parseCategoryExt_3(categoryPart: String, categoryStrPart: String): CategoryExt {
        val categoryId = categoryPart.toIntOrNull() ?: 100
        val category = Category.fromInt(categoryId)
        return CategoryExt(category, unescape(categoryStrPart))
    }

    private fun parseRemoveEvent(version: Int, timestamp: Long, parts: List<String>): RemoveEvent? {
        if (version == 1)
            return parseRemoveEvent_1(timestamp, parts)
        return null
    }

    private fun parseRemoveEvent_1(timestamp: Long, parts: List<String>): RemoveEvent? {
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
