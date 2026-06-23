package com.dstwrtv.app.util

object ArabicUtils {
    /**
     * Normalizes Arabic text for better search matching.
     * Replaces Aleppo/Alif variants, Taa Marbuta, Alif Maqsura, etc.
     */
    fun normalize(text: String): String {
        if (text.isBlank()) return ""
        
        return text
            .replace("[أإآ]".toRegex(), "ا")
            .replace("ة".toRegex(), "ه")
            .replace("ى".toRegex(), "ي")
            .replace("[ؤئ]".toRegex(), "ء")
            // Remove Harakat (short vowels)
            .replace("[\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652]".toRegex(), "")
            .lowercase()
            .trim()
    }

    /**
     * Matches two strings using normalized logic
     */
    fun matches(query: String, target: String): Boolean {
        if (query.isBlank()) return true
        val normalizedQuery = normalize(query)
        val normalizedTarget = normalize(target)
        return normalizedTarget.contains(normalizedQuery)
    }
}
