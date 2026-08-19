package com.trodevel.simpleevent

import android.app.Notification

object CategoryConverter {
    fun fromString(category: String?): Category {
        if (category.isNullOrEmpty()) return Category.NONE
        return when (category) {
            Notification.CATEGORY_CALL -> Category.CALL
            Notification.CATEGORY_MESSAGE -> Category.MESSAGE
            Notification.CATEGORY_EMAIL -> Category.EMAIL
            Notification.CATEGORY_EVENT -> Category.EVENT
            Notification.CATEGORY_PROMO -> Category.PROMO
            Notification.CATEGORY_ALARM -> Category.ALARM
            Notification.CATEGORY_PROGRESS -> Category.PROGRESS
            Notification.CATEGORY_SOCIAL -> Category.SOCIAL
            Notification.CATEGORY_ERROR -> Category.ERROR
            Notification.CATEGORY_TRANSPORT -> Category.TRANSPORT
            Notification.CATEGORY_SYSTEM -> Category.SYSTEM
            Notification.CATEGORY_SERVICE -> Category.SERVICE
            Notification.CATEGORY_RECOMMENDATION -> Category.RECOMMENDATION
            Notification.CATEGORY_STATUS -> Category.STATUS
            Notification.CATEGORY_REMINDER -> Category.REMINDER
            Notification.CATEGORY_LOCATION_SHARING -> Category.LOCATION_SHARING
            Notification.CATEGORY_MISSED_CALL -> Category.MISSED_CALL
            Notification.CATEGORY_NAVIGATION -> Category.NAVIGATION
            Notification.CATEGORY_STOPWATCH -> Category.STOPWATCH
            Notification.CATEGORY_VOICEMAIL -> Category.VOICEMAIL
            Notification.CATEGORY_WORKOUT -> Category.WORKOUT
            else -> Category.OTHER
        }
    }
}
