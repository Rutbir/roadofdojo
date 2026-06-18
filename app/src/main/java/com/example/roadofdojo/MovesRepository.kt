package com.example.roadofdojo

import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
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

    // Fungsi untuk narik status refleksi sebelumnya (Pakai HTTP URL Connection)
    suspend fun getRefleksiUser(userId: String, moveId: String): Refleksi? = withContext(Dispatchers.IO) {
        // PROTEKSI 1: Pastikan string yang masuk adalah UUID yang valid
        try {
            java.util.UUID.fromString(userId)
            java.util.UUID.fromString(moveId)
        } catch (e: IllegalArgumentException) {
            println("Error getRefleksi: userId atau moveId bukan format UUID valid")
            return@withContext null
        }

        try {
            val url = URL(
                "${BuildConfig.SUPABASE_URL}/rest/v1/refleksi" +
                        "?user_id=eq.$userId&move_id=eq.$moveId&select=*"
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
            if (responseCode in 200..299) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }

                val jsonArray = org.json.JSONArray(responseBody)
                if (jsonArray.length() > 0) {
                    val jsonObj = jsonArray.getJSONObject(0)
                    return@withContext Refleksi(
                        refleksi_id = jsonObj.optString("refleksi_id", null),
                        created_at = jsonObj.optString("created_at", null),
                        user_id = jsonObj.getString("user_id"),
                        move_id = jsonObj.getString("move_id"),
                        note = jsonObj.getString("note")
                    )
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Fungsi untuk nyimpen/update evaluasi (Pakai HTTP URL Connection)
    suspend fun simpanRefleksi(userId: String, moveId: String, note: String) = withContext(Dispatchers.IO) {
        // PROTEKSI 2: Pastikan string yang masuk adalah UUID yang valid
        try {
            java.util.UUID.fromString(userId)
            java.util.UUID.fromString(moveId)
        } catch (e: IllegalArgumentException) {
            println("Error simpanRefleksi: userId atau moveId bukan format UUID valid")
            return@withContext
        }

        try {
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/refleksi")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "resolution=merge-duplicates")
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            // Dikirim sebagai String, Supabase yang bakal nge-cast jadi UUID
            val jsonBody = org.json.JSONObject().apply {
                put("user_id", userId)
                put("move_id", moveId)
                put("note", note)
            }

            connection.outputStream.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            connection.outputStream.flush()
            connection.outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                println("Error simpan refleksi: $responseCode - $errorBody")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}