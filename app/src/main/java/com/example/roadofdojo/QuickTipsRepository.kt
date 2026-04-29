package com.example.roadofdojo

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuickTipsRepository {
    suspend fun fetchTips(): List<QuickTip> = withContext(Dispatchers.IO) {
        val url = URL(
            "${BuildConfig.SUPABASE_URL}/rest/v1/quick_tips" +
                "?select=id,title,content,sort_order&order=sort_order.asc"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
            setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            setRequestProperty("Accept", "application/json")
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

        QuickTipsJsonParser.parse(responseBody)
    }
}

