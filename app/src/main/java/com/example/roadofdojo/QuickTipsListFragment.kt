package com.example.roadofdojo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


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


        }


        }
    }

        // Kirim judul ke fragment detail via Bundle
        val detailFragment = TipsDetailFragment().apply {
            arguments = Bundle().apply {
                putString(TipsDetailFragment.ARG_TITLE, tipsTitle)
            }
        }
        (activity as? QuickTipsActivity)?.loadFragment(detailFragment)
    }
}