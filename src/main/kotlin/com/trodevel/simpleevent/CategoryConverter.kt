package com.trodevel.simpleevent

import android.app.Notification

object CategoryConverter {
    fun fromString(category: String?): Category {
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
            else -> Category.OTHER
        }
    }
}
