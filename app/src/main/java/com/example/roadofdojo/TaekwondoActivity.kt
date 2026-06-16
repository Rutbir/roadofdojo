package com.example.roadofdojo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class TaekwondoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taekwondo)

        // 1. UPDATE GOD-TIER: Panggil MaterialToolbar, bukan Toolbar biasa
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 2. Tampilkan tombol panah "Back"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 3. PENTING: Matiin title bawaan dari ActionBar
        // Karena judul "Taekwondo" udah di-handle otomatis sama efek CollapsingToolbar di XML
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 4. Fungsi tombol Back (Otomatis jalanin animasi reverse kalau lu pake Shared Element)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}