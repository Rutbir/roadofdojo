package com.frahm.roadofdojo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button

    private val slides = listOf(
        OnboardingSlide(R.string.onboarding_title_1, R.string.onboarding_description_1),
        OnboardingSlide(R.string.onboarding_title_2, R.string.onboarding_description_2),
        OnboardingSlide(R.string.onboarding_title_3, R.string.onboarding_description_3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.vpOnboarding)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        val tabIndicator: TabLayout = findViewById(R.id.tabIndicator)

        viewPager.adapter = OnboardingAdapter(slides)

        TabLayoutMediator(tabIndicator, viewPager) { _, _ ->
            // Indicator is synced automatically by TabLayoutMediator.
        }.attach()

        btnSkip.setOnClickListener {
            completeOnboarding()
        }

        btnNext.setOnClickListener {
            val isLastPage = viewPager.currentItem == slides.lastIndex
            if (isLastPage) {
                completeOnboarding()
            } else {
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                btnNext.text = if (position == slides.lastIndex) {
                    getString(R.string.onboarding_cta_start)
                } else {
                    getString(R.string.onboarding_cta_next)
                }
            }
        })
    }

    private fun completeOnboarding() {
        getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(MainActivity.KEY_ONBOARDING_COMPLETED, true)
            .apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

