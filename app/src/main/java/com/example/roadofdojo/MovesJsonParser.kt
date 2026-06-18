package com.example.roadofdojo

import org.json.JSONArray

object MovesJsonParser {
    fun parse(json: String): List<Move> {
        val array = JSONArray(json)
        val moves = ArrayList<Move>(array.length())
        for (index in 0 until array.length()) {
            val obj = array.getJSONObject(index)

            val categoryId = if (obj.isNull("category_id")) null else obj.optInt("category_id")

            // Handle possible JSON nulls for string fields
            val moveName = if (obj.isNull("move_name")) null else obj.optString("move_name").takeIf { it.isNotBlank() }
            val title = if (obj.isNull("title")) null else obj.optString("title").takeIf { it.isNotBlank() }
            val description = if (obj.isNull("description")) null else obj.optString("description").takeIf { it.isNotBlank() }
            val level = if (obj.isNull("level")) null else obj.optString("level").takeIf { it.isNotBlank() }
            val youtube = if (obj.isNull("youtube_url")) null else obj.optString("youtube_url").takeIf { it.isNotBlank() }
            val image = if (obj.isNull("image_url")) null else obj.optString("image_url").takeIf { it.isNotBlank() }

            moves.add(
                Move(
                    moveId = obj.optString("move_id"),
                    categoryId = categoryId,
                    moveName = moveName,
                    title = title,
                    description = description,
                    level = level,
                    youtubeUrl = youtube,
                    imageUrl = image
                )
            )
        }
        return moves
    }
}