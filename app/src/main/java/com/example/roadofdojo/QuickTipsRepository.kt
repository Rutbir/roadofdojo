package com.example.roadofdojo

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuickTipsRepository {
    suspend fun fetchTips(): List<QuickTip> = withContext(Dispatchers.IO) {
        // PERHATIKAN BAGIAN INI: Kita ubah jadi select=* biar SEMUA kolom ketarik
        val url = URL(
            "${BuildConfig.SUPABASE_URL}/rest/v1/quick_tips" +
                    "?select=*&order=sort_order.asc"
            // Pastikan nama tabelnya bener 'quick_tips', sesuaikan kalo beda
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

        QuickTipsJsonParser.parse(responseBody)
    }
}

