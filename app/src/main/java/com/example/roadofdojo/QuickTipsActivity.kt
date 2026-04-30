package com.example.roadofdojo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class QuickTipsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_tips)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Quick Tips"

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
            loadFragment(QuickTipsListFragment(), addToBackStack = false)
        }

        if (savedInstanceState == null) {
            loadFragment(QuickTipsListFragment(), addToBackStack = false)
        }
    }

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)

        if (addToBackStack) transaction.addToBackStack(null)

        transaction.commit()
    }

    // Fungsi helper untuk load fragment
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)

        if (addToBackStack) transaction.addToBackStack(null)

        transaction.commit()
    }
}
