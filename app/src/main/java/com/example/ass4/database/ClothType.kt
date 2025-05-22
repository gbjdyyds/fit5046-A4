package com.example.ass4.database

enum class ClothType {
    CAP,
    TOP,
    BOTTOM,
    SHOES;

    companion object {
        fun fromString(value: String): ClothType {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                TOP // return top type by default 
            }
        }
    }
} 