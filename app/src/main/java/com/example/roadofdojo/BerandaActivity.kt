package com.example.roadofdojo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.cardview.widget.CardView
import android.content.Intent

class BerandaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        initViews()
    }

    private fun initViews() {
        // Card Bela Diri → BelaDiriActivity
        val cardBelaDiri = findViewById<CardView>(R.id.cardBelaDiri)
        cardBelaDiri.setOnClickListener {
            val intent = Intent(this, BelaDiriActivity::class.java)
            startActivity(intent)
        }

        // Card Quick Tips → QuickTipsActivity
        val cardQuickTips = findViewById<CardView>(R.id.cardQuickTips)
        cardQuickTips.setOnClickListener {
            val intent = Intent(this, QuickTipsActivity::class.java)
            startActivity(intent)
        }

        // Card Favorit → FavoritActivity
        val cardFavorit = findViewById<CardView>(R.id.cardFavorit)
        cardFavorit.setOnClickListener {
            val intent = Intent(this, FavoritActivity::class.java)
            startActivity(intent)
        }
    }
}
