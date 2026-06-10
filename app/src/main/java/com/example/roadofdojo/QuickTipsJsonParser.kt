package com.example.roadofdojo

import org.json.JSONArray

object QuickTipsJsonParser {
    fun parse(json: String): List<QuickTip> {
        val array = JSONArray(json)
        val tips = ArrayList<QuickTip>(array.length())
        for (index in 0 until array.length()) {
            val obj = array.getJSONObject(index)
            // Handle JSON null explicitly for 'video' to avoid the literal string "null"
            val videoValue = if (obj.isNull("video")) {
                null
            } else {
                obj.optString("video").trim().takeIf { it.isNotEmpty() && it.lowercase() != "null" }
            }

            tips.add(
                QuickTip(
                    id = obj.optString("id"),
                    title = obj.optString("title"),
                    content = obj.optString("content"),
                    sortOrder = obj.optInt("sort_order", index),
                    video = videoValue
                )
            )
        }
        return tips
    }
}

