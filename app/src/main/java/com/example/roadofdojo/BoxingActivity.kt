package com.example.roadofdojo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar

class BoxingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boxing)

        // 1. UPGRADE GOD-TIER: Wajib pakai MaterialToolbar biar sinkron dengan UI XML baru
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Aktifkan tombol panah back di sudut kiri atas
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Boxing"

        // 2. Logic Tombol Back Cerdas (Cek apakah lagi di Detail atau di List)
        toolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                // Kalau lagi buka detail gerakan, kembali ke list
                supportFragmentManager.popBackStack()
            } else {
                // Kalau lagi di list, kembali ke Beranda utama
                onBackPressedDispatcher.onBackPressed()
            }
        }

        // 3. Muat Fragment pertama kali (List Gerakan) tanpa masuk ke backstack
        if (savedInstanceState == null) {
            loadFragment(BoxingListFragment(), addToBackStack = false)
        }
        val openDetail = intent.getBooleanExtra("OPEN_DETAIL", false)
        if (openDetail) {
            val detailFragment = TaekwondoDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(TaekwondoDetailFragment.ARG_MOVE_ID, intent.getStringExtra("MOVE_ID"))
                    putString(TaekwondoDetailFragment.ARG_NAMA, intent.getStringExtra("NAMA_GERAKAN"))
                    putString(TaekwondoDetailFragment.ARG_LEVEL, intent.getStringExtra("LEVEL_GERAKAN"))
                    putString(TaekwondoDetailFragment.ARG_DESC, intent.getStringExtra("DESC_GERAKAN"))
                    putString(TaekwondoDetailFragment.ARG_VIDEO, intent.getStringExtra("VIDEO_URL"))
                    putString(TaekwondoDetailFragment.ARG_IMAGE, intent.getStringExtra("IMAGE_URL"))
                }
            }
            // Gunakan fungsi loadFragment milik Activity kamu
            loadFragment(detailFragment)
        }
    }

    // FUNGSI NAVIGASI DENGAN ANIMASI PREMIUM
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

            // JURUS GOD-TIER: Animasi transisi antar layar ala aplikasi flagship
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    // Fungsi tambahan jika Fragment butuh mengubah judul Toolbar secara dinamis
    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}