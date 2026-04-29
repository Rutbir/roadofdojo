package com.example.roadofdojo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// File: OnboardingAdapter.kt
// Tujuan: Adapter RecyclerView untuk menampilkan daftar slide onboarding.
// Penjelasan singkat (ID):
// - Adapter menghubungkan data model `OnboardingSlide` ke layout item XML
//   (`item_onboarding_slide.xml`) sehingga RecyclerView bisa menampilkan tiap
//   slide (judul + deskripsi).
// - RecyclerView menggunakan ViewHolder pattern: buat ViewHolder saat perlu,
//   bind data ke ViewHolder saat item muncul di layar.
// Pseudocode global:
// - class OnboardingAdapter(slides: List<OnboardingSlide>) : Adapter
//   - onCreateViewHolder(parent, viewType): inflate layout, return ViewHolder
//   - onBindViewHolder(holder, position): holder.bind(slides[position])
//   - getItemCount(): return slides.size

class OnboardingAdapter(
    private val slides: List<OnboardingSlide>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        // Buat LayoutInflater dari konteks parent.
        // Pseudocode:
        // inflater = LayoutInflater.from(parent.context)
        val inflater = LayoutInflater.from(parent.context)

        // Inflate layout item untuk satu slide.
        // Parameter `parent` digunakan agar layout params bekerja benar di RecyclerView.
        // Pseudocode: view = inflater.inflate(R.layout.item_onboarding_slide, parent, false)
        val view = inflater.inflate(R.layout.item_onboarding_slide, parent, false)

        // Bungkus view yang sudah di-inflate ke dalam ViewHolder.
        // Pseudocode: return OnboardingViewHolder(view)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        // Ambil model slide pada posisi yang diminta lalu beritahu ViewHolder untuk bind data.
        // Pseudocode: slide = slides[position]; holder.bind(slide)
        holder.bind(slides[position])
    }

    // getItemCount: kembalikan jumlah slide yang ada di data source.
    // Pseudocode: return slides.size
    override fun getItemCount(): Int = slides.size

    class OnboardingViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        // Cari view yang ada di layout item (item_onboarding_slide.xml).
        // `tvSlideTitle` dan `tvSlideDescription` diasumsikan ada di file XML.
        private val title: TextView = itemView.findViewById(R.id.tvSlideTitle)
        private val description: TextView = itemView.findViewById(R.id.tvSlideDescription)

        // Bind data model ke view. Method ini dipanggil dari onBindViewHolder.
        // Pseudocode:
        // - menerima parameter `slide: OnboardingSlide`
        // - ambil resource id dari slide (slide.titleResId / slide.descriptionResId)
        // - panggil title.setText(resId) dan description.setText(resId)
        // Note: jika model menyimpan String langsung (bukan resId), pakai setText(string)
        fun bind(slide: OnboardingSlide) {
            // Set teks judul menggunakan resource id dari model
            title.setText(slide.titleResId)

            // Set teks deskripsi menggunakan resource id dari model
            description.setText(slide.descriptionResId)
        }
    }
}


