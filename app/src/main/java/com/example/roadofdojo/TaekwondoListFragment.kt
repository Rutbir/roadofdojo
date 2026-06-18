package com.example.roadofdojo

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class TaekwondoListFragment : Fragment() {

    private val repository = MovesRepository()

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

        // Dynamic: load moves from Supabase (category_id = 1 for Taekwondo)
        val beginnerContainer = view.findViewById<LinearLayout>(R.id.llBeginnerContainer)
        val intermediateContainer = view.findViewById<LinearLayout>(R.id.llIntermediateContainer)
        val advancedContainer = view.findViewById<LinearLayout>(R.id.llAdvancedContainer)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val moves = repository.fetchMovesByCategory(1)

                val grouped = moves.groupBy { it.level?.trim()?.uppercase() ?: "UNKNOWN" }

                populateSection(beginnerContainer, grouped["BEGINNER"].orEmpty())
                populateSection(intermediateContainer, grouped["INTERMEDIATE"].orEmpty())
                populateSection(advancedContainer, grouped["ADVANCED"].orEmpty())

            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    Toast.makeText(
                        requireContext(),
                        "Gagal memuat: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Populate a section container with move cards in 2-column rows
    private fun populateSection(container: LinearLayout, moves: List<Move>) {
        container.removeAllViews()

        if (moves.isEmpty()) {
            val empty = TextView(requireContext()).apply {
                text = "Belum ada gerakan"
                setTextColor(Color.parseColor("#666666"))
            }
            container.addView(empty)
            return
        }

        val itemHeight = dpToPx(140)

        val chunks = moves.chunked(2)
        for ((rowIndex, pair) in chunks.withIndex()) {
            val row = LinearLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    itemHeight
                ).apply {
                    if (rowIndex > 0) topMargin = dpToPx(8)
                }
                orientation = LinearLayout.HORIZONTAL
            }

            for ((i, move) in pair.withIndex()) {
                val itemView = layoutInflater.inflate(R.layout.item_move, row, false)

                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    if (i == 0) marginEnd = dpToPx(4) else marginStart = dpToPx(4)
                }
                itemView.layoutParams = lp

                val tvTitle = itemView.findViewById<TextView>(R.id.tvMoveTitle)
                val img = itemView.findViewById<ImageView>(R.id.imgMove)

                tvTitle.text = move.moveName ?: move.title ?: "Unnamed"

                // DI SINI PERUBAHANNYA: Kita ambil move.moveId dan lempar ke fungsi navigasi
                itemView.setOnClickListener {
                    navigateToDetail(
                        moveId = move.moveId, // <-- Gunakan moveId sesuai data class Move
                        namaGerakan = tvTitle.text.toString(),
                        level = move.level ?: "BEGINNER",
                        deskripsi = move.description ?: "",
                        videoUrl = move.youtubeUrl ?: "",
                        imageUrl = move.imageUrl ?: ""
                    )
                }

                if (!move.imageUrl.isNullOrBlank()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        loadImageInto(img, move.imageUrl)
                    }
                } else {
                    img.setImageResource(R.drawable.taekwondo)
                }

                row.addView(itemView)
            }

            // If only one item in row, add spacer to keep two-column layout
            if (pair.size == 1) {
                val spacer = View(requireContext())
                spacer.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                row.addView(spacer)
            }

            container.addView(row)
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()

    private suspend fun loadImageInto(imageView: ImageView, urlStr: String) {
        try {
            val bitmap = withContext(Dispatchers.IO) {
                val url = URL(urlStr)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10_000
                    readTimeout = 10_000
                    doInput = true
                }
                conn.connect()
                val stream = conn.inputStream
                val bmp = BitmapFactory.decodeStream(stream)
                stream.close()
                bmp
            }
            withContext(Dispatchers.Main) {
                if (bitmap != null) imageView.setImageBitmap(bitmap) else imageView.setImageResource(R.drawable.taekwondo)
            }
        } catch (_: Exception) {
            withContext(Dispatchers.Main) {
                imageView.setImageResource(R.drawable.taekwondo)
            }
        }
    }

    // FUNGSI NAVIGASI DI-UPGRADE: Sekarang menerima parameter moveId dan memasukkannya ke Bundle
    private fun navigateToDetail(moveId: String, namaGerakan: String, level: String, deskripsi: String, videoUrl: String, imageUrl: String) {
        val detailFragment = TaekwondoDetailFragment().apply {
            arguments = Bundle().apply {
                putString(TaekwondoDetailFragment.ARG_MOVE_ID, moveId) // <-- Bundle nangkep ID-nya di sini
                putString(TaekwondoDetailFragment.ARG_NAMA, namaGerakan)
                putString(TaekwondoDetailFragment.ARG_LEVEL, level)
                putString(TaekwondoDetailFragment.ARG_DESC, deskripsi)
                putString(TaekwondoDetailFragment.ARG_VIDEO, videoUrl)
                putString(TaekwondoDetailFragment.ARG_IMAGE, imageUrl)
            }
        }
        (activity as? TaekwondoActivity)?.loadFragment(detailFragment)
    }
}