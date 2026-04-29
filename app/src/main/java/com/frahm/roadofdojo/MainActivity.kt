package com.frahm.roadofdojo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val onboardingCompleted = preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        val authenticated = preferences.getBoolean(KEY_AUTHENTICATED, false)

        if (!onboardingCompleted) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        if (!authenticated) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // User sudah lolos onboarding + login, tampilkan halaman utama.
        setContentView(R.layout.activity_main)
    }

    companion object {
        const val PREFS_NAME = "road_of_dojo_prefs"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_AUTHENTICATED = "authenticated"
    }
}