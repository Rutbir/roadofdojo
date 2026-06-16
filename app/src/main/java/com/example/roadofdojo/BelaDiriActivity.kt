package com.example.roadofdojo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView

class BelaDiriActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bela_diri)

        // Setup Toolbar versi Material Design
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Bela Diri"
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Setup Card dan Gambar Taekwondo
        val cardTaekwondo = findViewById<MaterialCardView>(R.id.cardTaekwondo)
        val imgTaekwondo = findViewById<ImageView>(R.id.imgTaekwondo)

        cardTaekwondo.setOnClickListener {
            val intent = Intent(this, TaekwondoActivity::class.java)

            // JURUS GOD-TIER: Animasi Zoom (Shared Element)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imgTaekwondo,
                "animasi_gambar_bela_diri"
            )
            startActivity(intent, options.toBundle())
        }

        // Setup Card dan Gambar Boxing
        val cardBoxing = findViewById<MaterialCardView>(R.id.cardBoxing)
        val imgBoxing = findViewById<ImageView>(R.id.imgBoxing)

        cardBoxing.setOnClickListener {
            val intent = Intent(this, BoxingActivity::class.java)

            // JURUS GOD-TIER: Animasi Zoom (Shared Element)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                imgBoxing,
                "animasi_gambar_bela_diri"
            )
            startActivity(intent, options.toBundle())
        }
    }
}