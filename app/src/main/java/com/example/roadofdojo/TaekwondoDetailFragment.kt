package com.example.roadofdojo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class TaekwondoDetailFragment : Fragment() {

    companion object {
        const val ARG_NAMA  = "nama_gerakan"
        const val ARG_LEVEL = "level_gerakan"
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

        val namaGerakan = arguments?.getString(ARG_NAMA)  ?: "Gerakan"
        val level       = arguments?.getString(ARG_LEVEL) ?: "BEGINNER"

        // Isi layout
        view.findViewById<TextView>(R.id.tvGerakanTitle).text = namaGerakan
        view.findViewById<TextView>(R.id.tvLevel).text        = level

        // Update judul toolbar
        (activity as? TaekwondoActivity)?.setToolbarTitle(namaGerakan)
    }
}