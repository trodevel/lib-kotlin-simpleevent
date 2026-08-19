package com.trodevel.simpleevent

enum class Category {
    NONE,
    CALL,
    MESSAGE,
    EMAIL,
    EVENT,
    PROMO,
    ALARM,
    PROGRESS,
    SOCIAL,
    ERROR,
    TRANSPORT,
    SYSTEM,
    SERVICE,
    RECOMMENDATION,
    STATUS,
    REMINDER,
    LOCATION_SHARING,
    MISSED_CALL,
    NAVIGATION,
    STOPWATCH,
    VOICEMAIL,
    WORKOUT,
    OTHER;

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
