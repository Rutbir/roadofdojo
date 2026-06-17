package com.example.roadofdojo

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovesRepository {
    suspend fun fetchMovesByCategory(categoryId: Int): List<Move> = withContext(Dispatchers.IO) {
        val url = URL(
            "${BuildConfig.SUPABASE_URL}/rest/v1/moves" +
                    "?select=move_id,category_id,move_name,title,description,level,youtube_url,image_url" +
                    "&category_id=eq.$categoryId"
        )

        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
            setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            setRequestProperty("Accept", "application/json")
            connectTimeout = 15_000
            readTimeout = 15_000
        }

        val responseCode = connection.responseCode
        val responseBody = (if (responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        })?.bufferedReader()?.use { it.readText() }.orEmpty()

        if (responseCode !in 200..299) {
            throw IOException("Supabase error $responseCode: $responseBody")
        }

        MovesJsonParser.parse(responseBody)
    }
}