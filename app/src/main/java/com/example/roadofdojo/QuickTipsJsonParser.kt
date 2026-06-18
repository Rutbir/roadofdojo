package com.example.roadofdojo

import org.json.JSONArray

object QuickTipsJsonParser {
    fun parse(json: String): List<QuickTip> {
        val array = JSONArray(json)
        val tips = ArrayList<QuickTip>(array.length())
        for (index in 0 until array.length()) {
            val obj = array.getJSONObject(index)

            // Handle JSON null secara aman buat 'video'
            val videoValue = if (obj.isNull("video")) {
                null
            } else {
                obj.optString("video").trim().takeIf { it.isNotEmpty() && it.lowercase() != "null" }
            }

            // Handle JSON null secara aman buat 'image_url' (Thumbnail Gambar)
            // Asumsi nama kolom di Supabase lu adalah 'image_url'
            val imageUrlValue = if (obj.isNull("image_url")) {
                null
            } else {
                obj.optString("image_url").trim().takeIf { it.isNotEmpty() && it.lowercase() != "null" }
            }

            // Handle kategori, kalau kosong kita set default "TIPS" biar UI badge-nya tetep nyala
            val categoryValue = if (obj.isNull("category") || obj.optString("category").isBlank()) {
                "TIPS"
            } else {
                obj.optString("category").trim().uppercase()
            }

            tips.add(
                QuickTip(
                    id = obj.optString("id"),
                    title = obj.optString("title"),
                    description = obj.optString("content"), // Kolom 'content' dari DB dilempar ke 'description'
                    sortOrder = obj.optInt("sort_order", index),
                    category = categoryValue, // Masukin kategori ke sini
                    imageUrl = imageUrlValue, // Masukin link gambar ke sini
                    video = videoValue
                )
            )
        }
        return tips
    }
}