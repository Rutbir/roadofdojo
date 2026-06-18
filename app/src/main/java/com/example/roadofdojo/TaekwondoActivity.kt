package com.example.roadofdojo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar

class TaekwondoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taekwondo)

        // 1. Setup Toolbar God-Tier
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 2. Logic Tombol Back (Biar bisa back dari Detail ke List, bukan langsung keluar)
        toolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        // 3. Tampilkan Fragment List pas halaman pertama dibuka
        if (savedInstanceState == null) {
            loadFragment(TaekwondoListFragment())
        }
    }

    // FUNGSI INI YANG DIPANGGIL SAMA TaekwondoListFragment TADI
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            // Animasi transisi halus pas ganti fragment
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment)
            // Cek biar Fragment List pertama nggak usah masuk backstack
            .apply {
                if (fragment !is TaekwondoListFragment) {
                    addToBackStack(null)
                }
            }
            .commit()
    }

    // Fungsi biar Fragment bisa ganti judul Toolbar
    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}