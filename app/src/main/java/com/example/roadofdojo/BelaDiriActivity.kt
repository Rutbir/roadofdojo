package com.example.roadofdojo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import android.content.Intent

class BelaDiriActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bela_diri)

        // Setup Toolbar dengan tombol back
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bela Diri"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Card Taekwondo → TaekwondoActivity
        val cardTaekwondo = findViewById<CardView>(R.id.cardTaekwondo)
        cardTaekwondo.setOnClickListener {
            val intent = Intent(this, TaekwondoActivity::class.java)
            startActivity(intent)
        }

        // Card Boxing → BoxingActivity
        val cardBoxing = findViewById<CardView>(R.id.cardBoxing)
        cardBoxing.setOnClickListener {
            val intent = Intent(this, BoxingActivity::class.java)
            startActivity(intent)
        }
    }
}
