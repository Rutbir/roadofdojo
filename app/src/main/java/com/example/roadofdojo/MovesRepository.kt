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

    // Fungsi untuk narik status refleksi terbaru (Pakai HTTP URL Connection)
    suspend fun getRefleksiUser(userId: String, moveId: String): Refleksi? = withContext(Dispatchers.IO) {
        // PROTEKSI: Pastikan string yang masuk adalah UUID yang valid
        try {
            UUID.fromString(userId)
            UUID.fromString(moveId)
        } catch (e: IllegalArgumentException) {
            println("Error getRefleksi: userId atau moveId bukan format UUID valid")
            return@withContext null
        }

        try {
            // PERBAIKAN: Gunakan 1 URL saja.
            // Tambahkan order=created_at.desc untuk ambil yang paling baru.
            // Tambahkan limit=1 untuk menghemat kuota data karena kita cuma butuh 1 data terakhir.
            val url = URL(
                "${BuildConfig.SUPABASE_URL}/rest/v1/refleksi" +
                        "?user_id=eq.$userId&move_id=eq.$moveId&select=*&order=created_at.desc&limit=1"
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

                val jsonArray = JSONArray(responseBody)
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
            } else {
                println("Error getRefleksi: Supabase me-return response code $responseCode")
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Fungsi untuk nyimpen/update evaluasi (Pakai HTTP URL Connection)
    suspend fun simpanRefleksi(userId: String, moveId: String, note: String) = withContext(Dispatchers.IO) {
        // PROTEKSI: Pastikan string yang masuk adalah UUID yang valid
        try {
            UUID.fromString(userId)
            UUID.fromString(moveId)
        } catch (e: IllegalArgumentException) {
            println("Error simpanRefleksi: userId atau moveId bukan format UUID valid")
            return@withContext
        }

        try {
            // PERBAIKAN: Tambahkan parameter on_conflict
            // Supaya Supabase tahu dia harus me-replace data jika kombinasi user_id & move_id sudah ada
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/refleksi?on_conflict=user_id,move_id")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                // Header ini akan melakukan UPSERT (Insert kalau baru, Update kalau sudah ada)
                setRequestProperty("Prefer", "resolution=merge-duplicates")
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            val jsonBody = JSONObject().apply {
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

    suspend fun cekIsFavorite(userId: String, moveId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/favorites?user_id=eq.$userId&move_id=eq.$moveId&select=id")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            if (connection.responseCode in 200..299) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = org.json.JSONArray(responseBody)
                return@withContext jsonArray.length() > 0 // Kalau datanya > 0, berarti udah di-favorit
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        false
    }

    // 2. Tambah ke Favorit (Insert)
    suspend fun tambahFavorite(userId: String, moveId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/favorites")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val jsonBody = org.json.JSONObject().apply {
                put("user_id", userId)
                put("move_id", moveId)
            }

            connection.outputStream.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            connection.outputStream.flush()
            connection.outputStream.close()

            return@withContext connection.responseCode in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    // 3. Hapus dari Favorit (Delete)
    suspend fun hapusFavorite(userId: String, moveId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Delete berdasarkan kombinasi user_id dan move_id
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/favorites?user_id=eq.$userId&move_id=eq.$moveId")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            }
            return@withContext connection.responseCode in 200..299
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    // 4. Ambil Daftar Gerakan Favorit User
    suspend fun getFavoriteMovesUser(userId: String): List<Move> = withContext(Dispatchers.IO) {
        val moveIds = mutableListOf<String>()

        // Step 1: Ambil move_id apa saja yang di-favoritkan user ini
        try {
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/favorites?user_id=eq.$userId&select=move_id")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            if (connection.responseCode in 200..299) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = org.json.JSONArray(responseBody)
                for (i in 0 until jsonArray.length()) {
                    moveIds.add(jsonArray.getJSONObject(i).getString("move_id"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }

        // Kalau belum punya favorit satupun, langsung return list kosong
        if (moveIds.isEmpty()) return@withContext emptyList()

        // Step 2: Ambil detail gerakan dari tabel moves berdasarkan move_id yang didapat
        try {
            // Ubah list ["id1", "id2"] jadi string "id1,id2" biar bisa masuk ke parameter URL Supabase "in.()"
            val idsStr = moveIds.joinToString(",")
            val urlMoves = URL("${BuildConfig.SUPABASE_URL}/rest/v1/moves?move_id=in.($idsStr)&select=move_id,category_id,move_name,title,description,level,youtube_url,image_url")
            val connMoves = (urlMoves.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            if (connMoves.responseCode in 200..299) {
                val responseBody = connMoves.inputStream.bufferedReader().use { it.readText() }
                // Pakai parser yang udah ada buat mapping json ke List<Move>
                return@withContext MovesJsonParser.parse(responseBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext emptyList()
    }
    suspend fun getStreakUser(userId: String): org.json.JSONObject? = withContext(Dispatchers.IO) {
        try {
            val url = URL("${BuildConfig.SUPABASE_URL}/rest/v1/streaks?user_id=eq.$userId&select=*")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15_000
                readTimeout = 15_000
            }

            if (connection.responseCode in 200..299) {
                val responseBody = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = org.json.JSONArray(responseBody)
                if (jsonArray.length() > 0) {
                    return@withContext jsonArray.getJSONObject(0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    // 2. Kalkulasi dan Update Streak setelah latihan selesai
    suspend fun updateStreak(userId: String) = withContext(Dispatchers.IO) {
        try {
            val streakData = getStreakUser(userId)
            val today = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Jakarta"))

            var currentStreak = 1
            var longestStreak = 1
            var isNew = true
            var streaksId = java.util.UUID.randomUUID().toString()

            // Jika user sudah punya record streak sebelumnya, lakukan kalkulasi tanggal
            if (streakData != null) {
                isNew = false
                streaksId = streakData.optString("streaks_id", streaksId)
                val lastDateStr = streakData.optString("last_activity_streak", "")
                currentStreak = streakData.optInt("current_streak", 0)
                longestStreak = streakData.optInt("longest_streak", 0)

                if (lastDateStr.isNotEmpty()) {
                    val lastDate = java.time.LocalDate.parse(lastDateStr)
                    val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastDate, today)

                    when {
                        daysBetween == 0L -> return@withContext // Hari ini sudah latihan, tidak perlu update
                        daysBetween == 1L -> currentStreak += 1 // Latihan beruntun dari kemarin
                        else -> currentStreak = 1 // Bolos, reset streak kembali ke 1
                    }
                }
            }

            // Update rekor longest streak jika terpecahkan
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak
            }

            // Siapkan payload JSON
            val jsonBody = org.json.JSONObject().apply {
                if (isNew) put("streaks_id", streaksId) // Insert ID kalau baru
                put("user_id", userId)
                put("current_streak", currentStreak)
                put("longest_streak", longestStreak)
                put("last_activity_streak", today.toString()) // Format YYYY-MM-DD
            }

            // Atur URL dan Method (POST untuk baru, PATCH untuk update row lama)
            val targetUrl = if (isNew) {
                URL("${BuildConfig.SUPABASE_URL}/rest/v1/streaks")
            } else {
                URL("${BuildConfig.SUPABASE_URL}/rest/v1/streaks?user_id=eq.$userId")
            }
            val targetMethod = if (isNew) "POST" else "PATCH"

            val connection = (targetUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = targetMethod
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            connection.outputStream.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            connection.outputStream.flush()
            connection.outputStream.close()

            val code = connection.responseCode
            if (code !in 200..299) {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                println("ERROR UPDATE STREAK: $code - $errorBody")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}