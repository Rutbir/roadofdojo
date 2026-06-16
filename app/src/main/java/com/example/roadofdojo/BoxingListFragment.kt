package com.example.roadofdojo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment

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

        (activity as? BoxingActivity)?.setToolbarTitle("Boxing")

        // ===== BEGINNER =====
        view.findViewById<LinearLayout>(R.id.itemBeginner1).setOnClickListener {
            navigateToDetail("Jab dasar", "BEGINNER")
        }
        view.findViewById<LinearLayout>(R.id.itemBeginner2).setOnClickListener {
            navigateToDetail("Cross pukulan lurus", "BEGINNER")
        }

        // ===== INTERMEDIATE =====
        view.findViewById<LinearLayout>(R.id.itemIntermediate1).setOnClickListener {
            navigateToDetail("Hook kiri dan kanan", "INTERMEDIATE")
        }
        view.findViewById<LinearLayout>(R.id.itemIntermediate2).setOnClickListener {
            navigateToDetail("Uppercut kombinasi", "INTERMEDIATE")
        }

        // ===== ADVANCED =====
        view.findViewById<LinearLayout>(R.id.itemAdvanced1).setOnClickListener {
            navigateToDetail("Combo 10 pukulan", "ADVANCED")
        }
        view.findViewById<LinearLayout>(R.id.itemAdvanced2).setOnClickListener {
            navigateToDetail("Slip and counter", "ADVANCED")
        }
    }

    private fun navigateToDetail(namaGerakan: String, level: String) {
        val detailFragment = BoxingDetailFragment().apply {
            arguments = Bundle().apply {
                putString(BoxingDetailFragment.ARG_NAMA, namaGerakan)
                putString(BoxingDetailFragment.ARG_LEVEL, level)
            }
        }
        (activity as? BoxingActivity)?.loadFragment(detailFragment)
    }
}