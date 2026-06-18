package com.example.roadofdojo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast // Wajib ditambahin nih bro buat pop-up sementara
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.card.MaterialCardView

class BerandaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        initViews()
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