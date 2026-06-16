package com.example.roadofdojo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

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

        // Update judul toolbar
        (activity as? TaekwondoActivity)?.setToolbarTitle("Taekwondo")

        // ===== BEGINNER =====
        view.findViewById<LinearLayout>(R.id.itemBeginner1).setOnClickListener {
            navigateToDetail("Tendangan dua kaki", "BEGINNER")
        }
        view.findViewById<LinearLayout>(R.id.itemBeginner2).setOnClickListener {
            navigateToDetail("Salto belakang 120fps", "BEGINNER")
        }

        // ===== INTERMEDIATE =====
        view.findViewById<LinearLayout>(R.id.itemIntermediate1).setOnClickListener {
            navigateToDetail("Kuncian maut khas pati", "INTERMEDIATE")
        }
        view.findViewById<LinearLayout>(R.id.itemIntermediate2).setOnClickListener {
            navigateToDetail("Pancaran hayper kamehameha", "INTERMEDIATE")
        }

        // ===== ADVANCED =====
        view.findViewById<LinearLayout>(R.id.itemAdvanced1).setOnClickListener {
            navigateToDetail("Kuncian maut khas pati", "ADVANCED")
        }
        view.findViewById<LinearLayout>(R.id.itemAdvanced2).setOnClickListener {
            navigateToDetail("Pancaran hayper kamehameha", "ADVANCED")
        }
    }

    private fun navigateToDetail(namaGerakan: String, level: String) {
        val detailFragment = TaekwondoDetailFragment().apply {
            arguments = Bundle().apply {
                putString(TaekwondoDetailFragment.ARG_NAMA, namaGerakan)
                putString(TaekwondoDetailFragment.ARG_LEVEL, level)
            }
        }
        (activity as? TaekwondoActivity)?.loadFragment(detailFragment)
    }
}