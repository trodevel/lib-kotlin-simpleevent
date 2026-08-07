package com.trodevel.simpleevent

enum class Category {
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
    OTHER;

    fun getIcon(): String {
        return when (this) {
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
            OTHER -> "📁"
        }
    }
}
