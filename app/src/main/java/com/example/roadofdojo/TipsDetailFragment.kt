package com.example.roadofdojo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TipsDetailFragment : Fragment() {

    companion object {
        // Key untuk Bundle argument
        const val ARG_TITLE = "tips_title"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil judul dari Bundle
        val tipsTitle = arguments?.getString(ARG_TITLE) ?: "Quick Tips"

        // Tampilkan judul di layout
        view.findViewById<TextView>(R.id.tvTipsTitle).text = tipsTitle

        // Update judul toolbar sesuai tips yang dipilih
        (activity as? QuickTipsActivity)?.supportActionBar?.title = tipsTitle
    }
}