package com.trodevel.simpleevent

enum class Category(val id: Int) {
    NONE(0),
    CALL(1),
    MESSAGE(2),
    EMAIL(3),
    EVENT(4),
    PROMO(5),
    ALARM(6),
    PROGRESS(7),
    SOCIAL(8),
    ERROR(9),
    TRANSPORT(10),
    SYSTEM(11),
    SERVICE(12),
    RECOMMENDATION(13),
    STATUS(14),
    REMINDER(15),
    LOCATION_SHARING(16),
    MISSED_CALL(17),
    NAVIGATION(18),
    STOPWATCH(19),
    VOICEMAIL(20),
    WORKOUT(21),
    OTHER(100);

    companion object {
        fun fromInt(id: Int): Category {
            return values().find { it.id == id } ?: OTHER
        }
    }

    fun getIcon(): String {
        return when (this) {
            NONE -> ""
            CALL -> "📞"
            MESSAGE -> "💬"
            EMAIL -> "📧"
            EVENT -> "📅"
            PROMO -> "🏷️"
            ALARM -> "⏰"
            PROGRESS -> "⏳"
            SOCIAL -> "👥"
            ERROR -> "⚠️"
            TRANSPORT -> "🚗"
            SYSTEM -> "⚙️"
            SERVICE -> "🛠️"
            RECOMMENDATION -> "💡"
            STATUS -> "ℹ️"
            REMINDER -> "🔔"
            LOCATION_SHARING -> "📍"
            MISSED_CALL -> "📵"
            NAVIGATION -> "🧭"
            STOPWATCH -> "⏱️"
            VOICEMAIL -> "📼"
            WORKOUT -> "🏃"
            OTHER -> "📁"
        }
    }
}
