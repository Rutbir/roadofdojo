package com.example.roadofdojo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView // Wajib diganti ke MaterialCardView!

class BoxingListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_boxing_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hapus (activity as? BoxingActivity)?.setToolbarTitle("Boxing") biar gak crash dengan UI baru!

        // ===== BEGINNER =====
        view.findViewById<MaterialCardView>(R.id.itemBeginner1).setOnClickListener {
            navigateToDetail(
                "Jab Dasar",
                "BEGINNER",
                "Pukulan lurus ke depan menggunakan tangan terdepan (lead hand). Kunci untuk mengukur jarak, membuka pertahanan lawan, dan memulai kombinasi mematikan."
            )
        }
        view.findViewById<MaterialCardView>(R.id.itemBeginner2).setOnClickListener {
            navigateToDetail(
                "Cross Pukulan Lurus",
                "BEGINNER",
                "Pukulan lurus bertenaga penuh dari tangan belakang (rear hand). Mengandalkan putaran pinggul dan bahu untuk menghasilkan daya hancur maksimal."
            )
        }

        // ===== INTERMEDIATE =====
        view.findViewById<MaterialCardView>(R.id.itemIntermediate1).setOnClickListener {
            navigateToDetail(
                "Hook Kiri dan Kanan",
                "INTERMEDIATE",
                "Pukulan melingkar jarak dekat hingga menengah yang menargetkan sisi rahang atau rusuk lawan. Sangat efektif untuk menembus guard (pertahanan) rapat."
            )
        }
        view.findViewById<MaterialCardView>(R.id.itemIntermediate2).setOnClickListener {
            navigateToDetail(
                "Uppercut Kombinasi",
                "INTERMEDIATE",
                "Rangkaian pukulan vertikal mematikan dari bawah ke atas. Menargetkan dagu atau ulu hati, sangat ampuh saat pertarungan jarak dekat (infighting)."
            )
        }

        // ===== ADVANCED =====
        view.findViewById<MaterialCardView>(R.id.itemAdvanced1).setOnClickListener {
            navigateToDetail(
                "Combo 10 Pukulan",
                "ADVANCED",
                "Rangkaian kombinasi pukulan beruntun tingkat tinggi (volume punching) untuk menekan lawan secara brutal, menguras stamina, dan mencari celah KO."
            )
        }
        view.findViewById<MaterialCardView>(R.id.itemAdvanced2).setOnClickListener {
            navigateToDetail(
                "Slip and Counter",
                "ADVANCED",
                "Teknik elakan tingkat mahir. Menggeser kepala dari garis serangan lawan (slip) dan secara bersamaan membalas dengan pukulan telak (counter) saat pertahanan lawan terbuka."
            )
        }
    }

    // Fungsi navigasi yang di-upgrade buat ngirim deskripsi dinamis
    private fun navigateToDetail(namaGerakan: String, level: String, deskripsi: String) {
        val detailFragment = BoxingDetailFragment().apply {
            arguments = Bundle().apply {
                putString(BoxingDetailFragment.ARG_NAMA, namaGerakan)
                putString(BoxingDetailFragment.ARG_LEVEL, level)
                putString(BoxingDetailFragment.ARG_DESC, deskripsi) // Data deskripsi dikirim ke Detail
            }
        }

        // Load Fragment dengan aman melalui Activity Induk
        (activity as? BoxingActivity)?.loadFragment(detailFragment)
    }
}