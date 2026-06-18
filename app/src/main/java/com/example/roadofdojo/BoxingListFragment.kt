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

class BoxingListFragment : Fragment() {

    private val repository = MovesRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_boxing_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set judul Toolbar pakai fungsi dari Activity
        (activity as? BoxingActivity)?.setToolbarTitle("Boxing")

        val beginnerContainer = view.findViewById<LinearLayout>(R.id.llBeginnerContainer)
        val intermediateContainer = view.findViewById<LinearLayout>(R.id.llIntermediateContainer)
        val advancedContainer = view.findViewById<LinearLayout>(R.id.llAdvancedContainer)

        // Ambil data gerakan dari repository secara asynchronous
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val moves = repository.fetchMovesByCategory(2) // Boxing category_id = 2

                // Kelompokkan berdasarkan level kesulitan
                val grouped = moves.groupBy { it.level?.trim()?.uppercase() ?: "UNKNOWN" }

                // Masukkan data ke masing-masing section UI
                populateSection(beginnerContainer, grouped["BEGINNER"].orEmpty())
                populateSection(intermediateContainer, grouped["INTERMEDIATE"].orEmpty())
                populateSection(advancedContainer, grouped["ADVANCED"].orEmpty())

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal memuat gerakan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Fungsi untuk menyusun card gerakan secara dinamis (2 item per baris)
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

        // Potong list menjadi grup berisi 2 item untuk dibuat grid manual
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
                // Pastikan kamu punya file res/layout/item_move.xml
                val itemView = layoutInflater.inflate(R.layout.item_move, row, false)

                val lp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                    if (i == 0) marginEnd = dpToPx(4) else marginStart = dpToPx(4)
                }
                itemView.layoutParams = lp

                val tvTitle = itemView.findViewById<TextView>(R.id.tvMoveTitle)
                val img = itemView.findViewById<ImageView>(R.id.imgMove)

                tvTitle.text = move.moveName ?: move.title ?: "Unnamed"

                // Aksi saat card gerakan diklik, lempar semua data ke Detail Fragment
                itemView.setOnClickListener {
                    navigateToDetail(
                        namaGerakan = tvTitle.text.toString(),
                        level = move.level ?: "BEGINNER",
                        deskripsi = move.description ?: "",
                        videoUrl = move.youtubeUrl ?: "",
                        imageUrl = move.imageUrl ?: ""
                    )
                }

                // Load gambar cover dari URL atau pakai default
                if (!move.imageUrl.isNullOrBlank()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        loadImageInto(img, move.imageUrl)
                    }
                } else {
                    img.setImageResource(R.drawable.boxing) // Pastikan gambar ini ada di res/drawable
                }

                row.addView(itemView)
            }

            // Kalau jumlah item ganjil, tambahkan spacer kosong biar ukurannya pas
            if (pair.size == 1) {
                val spacer = View(requireContext())
                spacer.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                row.addView(spacer)
            }

            container.addView(row)
        }
    }

    // Utilitas konversi dp ke pixel untuk layouting dinamis
    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()

    // Fungsi suspend untuk download gambar cover gerakan
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
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.boxing)
                }
            }
        } catch (_: Exception) {
            withContext(Dispatchers.Main) {
                imageView.setImageResource(R.drawable.boxing)
            }
        }
    }

    // Bawa parameter lengkap ke BoxingDetailFragment
    private fun navigateToDetail(namaGerakan: String, level: String, deskripsi: String, videoUrl: String, imageUrl: String) {
        val detailFragment = BoxingDetailFragment().apply {
            arguments = Bundle().apply {
                putString(BoxingDetailFragment.ARG_NAMA, namaGerakan)
                putString(BoxingDetailFragment.ARG_LEVEL, level)
                putString(BoxingDetailFragment.ARG_DESC, deskripsi)
                putString(BoxingDetailFragment.ARG_VIDEO, videoUrl)
                putString(BoxingDetailFragment.ARG_IMAGE, imageUrl)
            }
        }
        (activity as? BoxingActivity)?.loadFragment(detailFragment)
    }
}