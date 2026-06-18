package com.example.roadofdojo

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class FavoriteListActivity : AppCompatActivity() {

    private val repository = MovesRepository()
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_list)

        // Tombol Back
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Ambil User ID dari sesi aktif
        val prefs = getSharedPreferences(AuthPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getString(AuthPrefs.KEY_USER_ID, "") ?: ""

        if (currentUserId.isBlank()) {
            Toast.makeText(this, "Sesi login tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadFavorites()
    }

    private fun loadFavorites() {
        val container = findViewById<LinearLayout>(R.id.llFavoriteContainer)
        val tvEmpty = findViewById<TextView>(R.id.tvEmptyState)

        lifecycleScope.launch {
            try {
                // Tembak supabase
                val moves = repository.getFavoriteMovesUser(currentUserId)

                if (moves.isEmpty()) {
                    container.visibility = View.GONE
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    container.visibility = View.VISIBLE
                    tvEmpty.visibility = View.GONE
                    populateSection(container, moves)
                }

            } catch (e: Exception) {
                Toast.makeText(this@FavoriteListActivity, "Gagal memuat favorit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateSection(container: LinearLayout, moves: List<Move>) {
        container.removeAllViews()
        val itemHeight = dpToPx(140)
        val chunks = moves.chunked(2)

        for ((rowIndex, pair) in chunks.withIndex()) {
            val row = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    itemHeight
                ).apply {
                    if (rowIndex > 0) topMargin = dpToPx(8)
                }
                orientation = LinearLayout.HORIZONTAL
            }

            for ((i, move) in pair.withIndex()) {
                val itemView = layoutInflater.inflate(R.layout.item_move, row, false)

                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    if (i == 0) marginEnd = dpToPx(4) else marginStart = dpToPx(4)
                }
                itemView.layoutParams = lp

                val tvTitle = itemView.findViewById<TextView>(R.id.tvMoveTitle)
                val img = itemView.findViewById<ImageView>(R.id.imgMove)

                tvTitle.text = move.moveName ?: move.title ?: "Unnamed"

                // Click item: Opsional, arahkan ke detailnya
                itemView.setOnClickListener {
                    // Cek ini gerakan Taekwondo (1) atau Boxing (2)
                    val intent = if (move.categoryId == 1) {
                        android.content.Intent(this@FavoriteListActivity, TaekwondoActivity::class.java)
                    } else {
                        android.content.Intent(this@FavoriteListActivity, BoxingActivity::class.java)
                    }

                    // Bawa semua data yang dibutuhkan Fragment Detail
                    intent.putExtra("MOVE_ID", move.moveId)
                    intent.putExtra("NAMA_GERAKAN", tvTitle.text.toString())
                    intent.putExtra("LEVEL_GERAKAN", move.level ?: "BEGINNER")
                    intent.putExtra("DESC_GERAKAN", move.description ?: "")
                    intent.putExtra("VIDEO_URL", move.youtubeUrl ?: "")
                    intent.putExtra("IMAGE_URL", move.imageUrl ?: "")

                    // Flag khusus biar Activity tahu dia harus langsung buka halaman Detail, bukan List
                    intent.putExtra("OPEN_DETAIL", true)

                    startActivity(intent)

                }

                if (!move.imageUrl.isNullOrBlank()) {
                    lifecycleScope.launch {
                        loadImageInto(img, move.imageUrl)
                    }
                } else {
                    // Beri default image kalau kosong
                    img.setBackgroundColor(Color.parseColor("#333333"))
                }

                row.addView(itemView)
            }

            if (pair.size == 1) {
                val spacer = View(this)
                spacer.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                row.addView(spacer)
            }

            container.addView(row)
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()

    private suspend fun loadImageInto(imageView: ImageView, urlStr: String) {
        try {
            val bitmap = withContext(Dispatchers.IO) {
                val url = URL(urlStr)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10_000
                    readTimeout = 10_000
                    doInput = true
                }
                conn.connect()
                val stream = conn.inputStream
                val bmp = BitmapFactory.decodeStream(stream)
                stream.close()
                bmp
            }
            withContext(Dispatchers.Main) {
                bitmap?.let { imageView.setImageBitmap(it) }
            }
        } catch (_: Exception) { }
    }
}