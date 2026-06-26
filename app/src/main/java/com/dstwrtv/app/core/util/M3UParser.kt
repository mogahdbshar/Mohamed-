package com.dstwrtv.app.core.util

import com.dstwrtv.app.model.Channel

class M3UParser {

    fun parse(m3uContent: String, defaultCategory: String = "القنوات المضافة"): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = m3uContent.split("\n")
        
        var currentName = ""
        var currentLogo = ""
        var currentGroup = defaultCategory

        for (line in lines) {
            val trimmedLine = line.trim().replace("\r", "")
            if (trimmedLine.isEmpty()) continue

            if (trimmedLine.startsWith("#EXTINF:", ignoreCase = true)) {
                // Parse Name
                val lastCommaIndex = trimmedLine.lastIndexOf(',')
                currentName = if (lastCommaIndex != -1 && lastCommaIndex < trimmedLine.length - 1) {
                    trimmedLine.substring(lastCommaIndex + 1).trim()
                } else {
                    ""
                }

                // Parse Logo
                val logoMatch = Regex("""tvg-logo=["']([^"']+)["']""", RegexOption.IGNORE_CASE).find(trimmedLine)
                currentLogo = logoMatch?.groupValues?.get(1) ?: ""

                // Parse Group/Category
                val groupMatch = Regex("""group-title=["']([^"']+)["']""", RegexOption.IGNORE_CASE).find(trimmedLine)
                currentGroup = groupMatch?.groupValues?.get(1) ?: defaultCategory
            } else if (trimmedLine.startsWith("http", ignoreCase = true)) {
                val finalUrl = trimmedLine
                val finalName = if (currentName.isBlank()) {
                    val lastSlash = finalUrl.lastIndexOf('/')
                    if (lastSlash != -1 && lastSlash < finalUrl.length - 1) {
                        finalUrl.substring(lastSlash + 1)
                            .removeSuffix(".ts")
                            .removeSuffix(".m3u8")
                            .trim()
                    } else {
                        "قناة غير مسمى"
                    }
                } else {
                    currentName
                }

                channels.add(
                    Channel(
                        name = finalName,
                        url = finalUrl,
                        logo = currentLogo,
                        category = currentGroup
                    )
                )

                // Reset for next
                currentName = ""
                currentLogo = ""
                currentGroup = defaultCategory
            }
        }
        return channels
    }

    fun cleanCategory(category: String): String {
        return category
            .replace(Regex("(?i)باقة قنوات "), "")
            .replace(Regex("(?i)باقة القنوات "), "")
            .replace(Regex("(?i)قنوات "), "")
            .replace(Regex("(?i)باقة "), "")
            .replace("ال ال", " ال")
            .replace("الال", "ال")
            .replace("VIP", "")
            .replace("  ", " ")
            .trim()
            .replace(Regex("^الال"), "ال")
            .replace(Regex("^ال ال"), "ال")
    }
}
