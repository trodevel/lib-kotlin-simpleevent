package com.trodevel.simpleevent

data class CategoryExt(
    val category: Category,
    val categoryStr: String
) {
    fun getIconOrStr(): String {
        return if (category != Category.OTHER) {
            category.getIcon()
        } else {
            categoryStr
        }
    }
}
