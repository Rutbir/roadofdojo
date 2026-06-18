package com.example.roadofdojo

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
class BerandaActivity : AppCompatActivity() {

    private val repository = MovesRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        loadStreakData()
    }

    private fun loadStreakData() {
        val prefs = getSharedPreferences(AuthPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        val currentUserId = prefs.getString(AuthPrefs.KEY_USER_ID, "") ?: ""
        if (currentUserId.isBlank()) return

        lifecycleScope.launch {
            // Ambil dari Supabase
            val streakJson = repository.getStreakUser(currentUserId)
            var currentStreak = 0

            if (streakJson != null) {
                currentStreak = streakJson.optInt("current_streak", 0)
            }

            // Update Tulisan
            val txtStreakCount = findViewById<TextView>(R.id.txtStreakCount)
            txtStreakCount.text = "$currentStreak Hari Beruntun!"

            // Update 7 Bulatan Hari (S S R K J S M)
            val llStreakDays = findViewById<LinearLayout>(R.id.llStreakDaysContainer)
            if (llStreakDays != null) {
                // Maksimal 7 bulatan yang bisa menyala
                val maxActive = minOf(currentStreak, 7)

                for (i in 0 until 7) {
                    val card = llStreakDays.getChildAt(i) as? MaterialCardView
                    val tv = card?.getChildAt(0) as? TextView

                    if (i < maxActive) {
                        // Aktif: Bulatan warna Kuning
                        card?.setCardBackgroundColor(Color.parseColor("#FFD700"))
                        tv?.setTextColor(Color.parseColor("#121212"))
                    } else {
                        // Mati: Bulatan warna Abu-abu Transparan
                        card?.setCardBackgroundColor(Color.parseColor("#33FFFFFF"))
                        tv?.setTextColor(Color.parseColor("#888888"))
                    }
                }
            }
        }
    }

    private fun initViews() {
        // 1. CAROUSEL: Card Taekwondo → TaekwondoActivity (Animasi Zoom)
        val cardTaekwondo = findViewById<MaterialCardView>(R.id.cardTaekwondo)
        val imgTaekwondo = findViewById<ImageView>(R.id.imgTaekwondo)

        cardTaekwondo.setOnClickListener {
            val intent = Intent(this, TaekwondoActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imgTaekwondo,
                "animasi_gambar_bela_diri"
            )
            startActivity(intent, options.toBundle())
        }

        // 2. CAROUSEL: Card Boxing → BoxingActivity (Animasi Zoom)
        val cardBoxing = findViewById<MaterialCardView>(R.id.cardBoxing)
        val imgBoxing = findViewById<ImageView>(R.id.imgBoxing)

        cardBoxing.setOnClickListener {
            val intent = Intent(this, BoxingActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imgBoxing,
                "animasi_gambar_bela_diri"
            )
            startActivity(intent, options.toBundle())
        }

        // 3. JURUS AMAN SEMENTARA: Card Gerakan Favorit (Pakai Toast)
        val cardGerakanFavorit = findViewById<MaterialCardView>(R.id.cardGerakanFavorit)
        cardGerakanFavorit.setOnClickListener {
            val intent = Intent(this, FavoriteListActivity::class.java)
            startActivity(intent)
        }

        // 4. GRID MENU: Semua Teknik → BelaDiriActivity
        val btnMenuBelaDiri = findViewById<MaterialCardView>(R.id.btnMenuBelaDiri)
        btnMenuBelaDiri.setOnClickListener {
            val intent = Intent(this, BelaDiriActivity::class.java)
            startActivity(intent)
        }

        // 5. GRID MENU: Tips Harian → QuickTipsActivity
        val btnMenuTips = findViewById<MaterialCardView>(R.id.btnMenuTips)
        btnMenuTips.setOnClickListener {
            val intent = Intent(this, QuickTipsActivity::class.java)
            startActivity(intent)
        }
    }
}