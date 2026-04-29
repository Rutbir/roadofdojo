package com.frahm.roadofdojo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private val logTag = "SupaOAuth"

    private lateinit var googleButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAuthenticated()) {
            Log.d(logTag, "User sudah authenticated, skip LoginActivity")
            goToMain()
            return
        }

        setContentView(R.layout.activity_login)

        googleButton = findViewById(R.id.btnGoogleSignIn)
        progressBar = findViewById(R.id.progressLogin)
        statusText = findViewById(R.id.tvLoginStatus)

        handleOAuthCallback(intent)

        googleButton.setOnClickListener {
            if (BuildConfig.SUPABASE_URL.isBlank() || BuildConfig.SUPABASE_ANON_KEY.isBlank()) {
                renderIdleState(
                    getString(
                        R.string.login_status_failed,
                        "SUPABASE_URL atau SUPABASE_ANON_KEY belum diisi di local.properties"
                    )
                )
                return@setOnClickListener
            }

            startSupabaseGoogleOAuth()
        }

        renderIdleState(getString(R.string.login_status_idle))
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun startSupabaseGoogleOAuth() {
        val authUri = Uri.parse("${BuildConfig.SUPABASE_URL}/auth/v1/authorize")
            .buildUpon()
            .appendQueryParameter("provider", "google")
            .appendQueryParameter("redirect_to", BuildConfig.APP_REDIRECT_URL)
            .build()

        Log.d(logTag, "Buka browser auth Supabase: $authUri")
        renderLoadingState()
        startActivity(Intent(Intent.ACTION_VIEW, authUri))
    }

    private fun handleOAuthCallback(callbackIntent: Intent?) {
        val callbackUri = callbackIntent?.data ?: return
        val expected = Uri.parse(BuildConfig.APP_REDIRECT_URL)

        val sameDestination = callbackUri.scheme == expected.scheme && callbackUri.host == expected.host
        if (!sameDestination) return

        val mergedParams = mutableMapOf<String, String>()
        mergedParams.putAll(readParams(callbackUri.query))
        mergedParams.putAll(readParams(callbackUri.fragment))

        val error = mergedParams["error_description"] ?: mergedParams["error"]
        if (!error.isNullOrBlank()) {
            Log.e(logTag, "OAuth callback error: $error")
            renderIdleState(getString(R.string.login_status_failed, error))
            return
        }

        val accessToken = mergedParams["access_token"]
        if (accessToken.isNullOrBlank()) {
            Log.e(logTag, "Callback diterima tapi access_token kosong. uri=$callbackUri")
            renderIdleState(getString(R.string.login_status_failed, "Callback tidak mengandung access_token"))
            return
        }

        getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(MainActivity.KEY_AUTHENTICATED, true)
            .apply()

        Log.d(logTag, "OAuth sukses, session flag disimpan")
        renderSuccessState()
        goToMain()
    }

    private fun readParams(raw: String?): Map<String, String> {
        if (raw.isNullOrBlank()) return emptyMap()
        val uri = Uri.parse("https://callback.local/?$raw")
        return uri.queryParameterNames.associateWith { key ->
            uri.getQueryParameter(key).orEmpty()
        }
    }

    private fun isAuthenticated(): Boolean {
        return getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
            .getBoolean(MainActivity.KEY_AUTHENTICATED, false)
    }

    private fun goToMain() {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        finish()
    }

    private fun renderLoadingState() {
        googleButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        statusText.text = getString(R.string.login_status_loading)
    }

    private fun renderSuccessState() {
        googleButton.isEnabled = true
        progressBar.visibility = View.GONE
        statusText.text = getString(R.string.login_status_success)
    }

    private fun renderIdleState(message: String) {
        googleButton.isEnabled = true
        progressBar.visibility = View.GONE
        statusText.text = message
    }
}

