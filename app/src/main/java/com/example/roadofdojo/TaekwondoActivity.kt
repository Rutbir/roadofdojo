package com.example.roadofdojo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class TaekwondoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taekwondo)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Taekwondo"

        toolbar.setNavigationOnClickListener {
            // Jika ada fragment di back stack → kembali ke list
            // Jika tidak → keluar dari activity
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }

        // Tampilkan fragment list saat pertama kali
        if (savedInstanceState == null) {
            loadFragment(TaekwondoListFragment(), addToBackStack = false)
        }
    }

    // Fungsi helper load fragment
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)

        if (addToBackStack) transaction.addToBackStack(null)

        transaction.commit()
    }

    // Update judul toolbar dari fragment
    fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }
}