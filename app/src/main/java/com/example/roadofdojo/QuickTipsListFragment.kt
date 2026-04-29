package com.example.roadofdojo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView


class QuickTipsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quick_tips_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update judul toolbar saat fragment ini aktif
        (activity as? QuickTipsActivity)?.supportActionBar?.title = "Quick Tips"

        // Card Tips 1 → TipsDetailFragment dengan judul "Quick Tips 1"
        view.findViewById<CardView>(R.id.cardTips1).setOnClickListener {
            navigateToDetail("Quick Tips 1")
        }

        // Card Tips 2
        view.findViewById<CardView>(R.id.cardTips2).setOnClickListener {
            navigateToDetail("Quick Tips 2")
        }

        // Card Tips 3
        view.findViewById<CardView>(R.id.cardTips3).setOnClickListener {
            navigateToDetail("Quick Tips 3")
        }

        // Card Tips 4
        view.findViewById<CardView>(R.id.cardTips4).setOnClickListener {
            navigateToDetail("Quick Tips 4")
        }
    }

    private fun navigateToDetail(tipsTitle: String) {
        // Kirim judul ke fragment detail via Bundle
        val detailFragment = TipsDetailFragment().apply {
            arguments = Bundle().apply {
                putString(TipsDetailFragment.ARG_TITLE, tipsTitle)
            }
        }
        (activity as? QuickTipsActivity)?.loadFragment(detailFragment)
    }
}