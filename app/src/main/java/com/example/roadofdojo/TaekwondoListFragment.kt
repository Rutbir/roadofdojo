package com.example.roadofdojo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView // Import ini penting banget biar nggak error!

class TaekwondoListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_taekwondo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hapus pemanggilan setToolbarTitle karena UI kita udah pakai CollapsingToolbar otomatis

        // ===== BEGINNER =====
        view.findViewById<MaterialCardView>(R.id.itemBeginner1).setOnClickListener {
            navigateToDetail(
                "Roundhouse Kick",
                "BEGINNER",
                "Tendangan memutar horizontal yang menargetkan area pinggang atau kepala lawan dengan punggung kaki. Sangat cepat, efisien, dan mematikan."
            )
        }
        view.findViewById<MaterialCardView>(R.id.itemBeginner2).setOnClickListener {
            navigateToDetail(
                "Front Kick",
                "BEGINNER",
                "Tendangan dorongan lurus ke depan menggunakan bantalan telapak kaki (Ap Chagi). Cocok untuk menjaga jarak atau merusak kuda-kuda lawan."
            )
        }

        // ===== INTERMEDIATE =====
        view.findViewById<MaterialCardView>(R.id.itemIntermediate1).setOnClickListener {
            navigateToDetail(
                "Hook Kick",
                "INTERMEDIATE",
                "Tendangan tipuan yang awalnya terlihat meleset, lalu ditarik dengan cepat menggunakan tumit ke arah rahang atau pelipis lawan."
            )
        }
        view.findViewById<MaterialCardView>(R.id.itemIntermediate2).setOnClickListener {
            navigateToDetail(
                "Crescent Kick",
                "INTERMEDIATE",
                "Tendangan melengkung dari luar ke dalam atau sebaliknya. Sering digunakan untuk menangkis serangan atau menjatuhkan guard tangan lawan."
            )
        }

        // ===== ADVANCED =====
        view.findViewById<MaterialCardView>(R.id.itemAdvanced1).setOnClickListener {
            navigateToDetail(
                "Tornado Roundhouse",
                "ADVANCED",
                "Kombinasi putaran 360 derajat di udara untuk mengumpulkan momentum sebelum melepaskan tendangan Roundhouse yang sangat destruktif."
            )
        }
        view.findViewById<MaterialCardView>(R.id.itemAdvanced2).setOnClickListener {
            navigateToDetail(
                "Spinning Hook",
                "ADVANCED",
                "Tendangan berputar balik (back spin) dengan target kepala lawan. Membutuhkan kelenturan, keseimbangan ekstra tinggi, dan akurasi sempurna."
            )
        }
    }

    // Fungsi navigasi yang di-upgrade buat ngirim deskripsi sekaligus
    private fun navigateToDetail(namaGerakan: String, level: String, deskripsi: String) {
        val detailFragment = TaekwondoDetailFragment().apply {
            arguments = Bundle().apply {
                putString(TaekwondoDetailFragment.ARG_NAMA, namaGerakan)
                putString(TaekwondoDetailFragment.ARG_LEVEL, level)
                putString(TaekwondoDetailFragment.ARG_DESC, deskripsi) // Kirim deskripsi dinamis
            }
        }

        // Panggil fungsi loadFragment dari Activity Induk
        (activity as? TaekwondoActivity)?.loadFragment(detailFragment)
    }
}