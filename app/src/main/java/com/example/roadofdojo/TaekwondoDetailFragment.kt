package com.example.roadofdojo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class TaekwondoDetailFragment : Fragment() {

    companion object {
        const val ARG_NAMA  = "nama_gerakan"
        const val ARG_LEVEL = "level_gerakan"
        const val ARG_DESC  = "desc_gerakan" // Tambahan God-Tier: Deskripsi bisa dikirim dinamis
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_taekwondo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ambil Data (Dengan default value yang aman)
        val namaGerakan = arguments?.getString(ARG_NAMA)  ?: "Dollyo Chagi"
        val level       = arguments?.getString(ARG_LEVEL) ?: "BEGINNER"
        val desc        = arguments?.getString(ARG_DESC)  ?: "Instruksi langkah demi langkah akan tampil di sini. Putar pinggul dan lepaskan tendangan!"

        // 2. Hubungkan Komponen View dari XML
        val tvTitle = view.findViewById<TextView>(R.id.tvGerakanTitle)
        val tvLevel = view.findViewById<TextView>(R.id.tvLevel)
        val tvDesc  = view.findViewById<TextView>(R.id.tvGerakanDesc)
        val cardLevel = view.findViewById<MaterialCardView>(R.id.cardLevel)
        val btnSelesai = view.findViewById<MaterialButton>(R.id.btnSelesaiLatihan)
        val imgDemo = view.findViewById<ImageView>(R.id.imgGerakanDemo) // Area video/gambar

        // 3. Set Teks ke Layar
        tvTitle.text = namaGerakan
        tvLevel.text = level.uppercase()
        tvDesc.text  = desc

        // 4. LOGIC GOD-TIER: Ubah Warna Badge Otomatis Berdasarkan Tingkat Kesulitan
        when (level.uppercase()) {
            "BEGINNER" -> cardLevel.setCardBackgroundColor(Color.parseColor("#4CAF50")) // Hijau
            "INTERMEDIATE" -> cardLevel.setCardBackgroundColor(Color.parseColor("#FF9800")) // Orange
            "ADVANCED" -> cardLevel.setCardBackgroundColor(Color.parseColor("#F44336")) // Merah
            else -> cardLevel.setCardBackgroundColor(Color.parseColor("#FFD700")) // Emas default
        }

        // 5. Interaksi: Area Video (Placeholder)
        imgDemo.setOnClickListener {
            // Nanti bisa diganti intent buat muter video beneran
            Toast.makeText(requireContext(), "Memuat video tutorial $namaGerakan...", Toast.LENGTH_SHORT).show()
        }

        // 6. Interaksi: Tombol Selesai Latihan
        btnSelesai.setOnClickListener {
            // Di sini tempat yang pas buat naro logic nambahin "Daily Streak"
            Toast.makeText(requireContext(), "Mantap! Latihan $namaGerakan selesai.", Toast.LENGTH_SHORT).show()
        }
    }
}