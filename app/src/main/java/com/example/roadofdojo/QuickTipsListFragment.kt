package com.example.roadofdojo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class QuickTipsListFragment : Fragment() {

    private val repository = QuickTipsRepository()

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

        val progress = view.findViewById<ProgressBar>(R.id.progressTips)
        val emptyState = view.findViewById<View>(R.id.llEmptyState)
        val emptyText = view.findViewById<TextView>(R.id.tvTipsEmpty)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvQuickTips)
        
        // Menggunakan 2 kolom agar terlihat seperti grid kartu yang rapi
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val adapter = QuickTipsAdapter { tip ->
            navigateToDetail(tip.title, tip.content, tip.video)
        }
        recyclerView.adapter = adapter

        progress.visibility = View.VISIBLE
        emptyState.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val tips = repository.fetchTips()
                adapter.setItems(tips)
                emptyState.visibility = if (tips.isEmpty()) View.VISIBLE else View.GONE
            } catch (error: Exception) {
                emptyText.text = "Gagal memuat tips."
                emptyState.visibility = View.VISIBLE
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    private fun navigateToDetail(tipsTitle: String, tipsBody: String, videoUrl: String?) {
        // Kirim judul ke fragment detail via Bundle
        val detailFragment = TipsDetailFragment().apply {
            arguments = Bundle().apply {
                putString(TipsDetailFragment.ARG_TITLE, tipsTitle)
                putString(TipsDetailFragment.ARG_BODY, tipsBody)
                putString(TipsDetailFragment.ARG_VIDEO, videoUrl)
            }
        }
        (activity as? QuickTipsActivity)?.loadFragment(detailFragment)
    }
}
