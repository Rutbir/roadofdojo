package com.example.roadofdojo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class BoxingDetailFragment : Fragment() {

    companion object {
        const val ARG_MOVE_ID = "move_id" // PENTING: ID gerakan dari database
        const val ARG_NAMA  = "nama_gerakan"
        const val ARG_LEVEL = "level_gerakan"
        const val ARG_DESC  = "desc_gerakan"
        const val ARG_VIDEO = "video_url"
        const val ARG_IMAGE = "image_url"
    }

    private var moveVideoWebView: WebView? = null

    // Inisialisasi Repository buat manggil Supabase
    private val repository = MovesRepository()
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_boxing_detail, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil User ID dari SharedPreferences
        val prefs = requireContext().getSharedPreferences(AuthPrefs.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getString(AuthPrefs.KEY_USER_ID, "") ?: ""

        // 1. Ambil data dari Bundle/Arguments
        val moveId      = arguments?.getString(ARG_MOVE_ID) ?: ""
        val namaGerakan = arguments?.getString(ARG_NAMA)  ?: "Jab"
        val level       = arguments?.getString(ARG_LEVEL) ?: "BEGINNER"
        val desc        = arguments?.getString(ARG_DESC)  ?: "Instruksi langkah demi langkah akan tampil di sini. Jaga guard tangan tetap di depan wajah."
        val videoUrl    = arguments?.getString(ARG_VIDEO).orEmpty()
        val imageUrl    = arguments?.getString(ARG_IMAGE).orEmpty()

        // 2. Set judul Toolbar (opsional jika dipanggil dari BoxingActivity)
        try {
            (activity as? BoxingActivity)?.setToolbarTitle(namaGerakan)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Inisialisasi Views
        val tvTitle = view.findViewById<TextView>(R.id.tvGerakanTitle)
        val tvLevel = view.findViewById<TextView>(R.id.tvLevel)
        val tvDesc  = view.findViewById<TextView>(R.id.tvGerakanDesc)
        val cardLevel = view.findViewById<MaterialCardView>(R.id.cardLevel)
        val btnSelesai = view.findViewById<MaterialButton>(R.id.btnSelesaiLatihan)

        // Deklarasi Komponen Favorit yang sebelumnya tertinggal
        val ivFavorite = view.findViewById<ImageView>(R.id.ivFavorite)
        var isFavorited = false

        // Komponen UI untuk Status Evaluasi
        val cardStatus = view.findViewById<MaterialCardView>(R.id.cardStatus)
        val tvStatusText = view.findViewById<TextView>(R.id.tvStatusText)

        // Views untuk pemutar video YouTube
        val overlay = view.findViewById<View>(R.id.viewOverlay)
        val playBtn = view.findViewById<ImageView>(R.id.ivPlayOverlay)
        val videoView = view.findViewById<WebView>(R.id.wvGerakanVideo)
        moveVideoWebView = videoView
        val imgDemo = view.findViewById<ImageView?>(R.id.imgGerakanDemo)

        // 4. Masukkan data ke dalam UI
        tvTitle.text = namaGerakan
        tvLevel.text = level.uppercase()
        tvDesc.text  = desc

        // 5. Cek status evaluasi dari Supabase saat layar dibuka
        if (moveId.isNotBlank()) {
            viewLifecycleOwner.lifecycleScope.launch {
                val refleksi = repository.getRefleksiUser(currentUserId, moveId)
                if (refleksi != null) {
                    updateUIRefleksi(refleksi.note, cardStatus, tvStatusText)
                }
            }
        }

        // 6. Ganti warna badge otomatis sesuai tingkat kesulitan
        when (level.uppercase()) {
            "BEGINNER" -> cardLevel.setCardBackgroundColor(Color.parseColor("#4CAF50"))
            "INTERMEDIATE" -> cardLevel.setCardBackgroundColor(Color.parseColor("#FF9800"))
            "ADVANCED" -> cardLevel.setCardBackgroundColor(Color.parseColor("#F44336"))
            else -> cardLevel.setCardBackgroundColor(Color.parseColor("#FFD700"))
        }

        // 7. Load gambar dari Internet jika URL tersedia
        if (imageUrl.isNotBlank() && imgDemo != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                loadImageInto(imgDemo, imageUrl)
            }
        }

        // 8. Setup fitur memutar Video Tutorial (YouTube Embed)
        val playVideoAction = View.OnClickListener {
            if (videoUrl.isNotBlank()) {
                overlay?.visibility = View.GONE
                playBtn?.visibility = View.GONE
                imgDemo?.visibility = View.GONE

                videoView?.visibility = View.VISIBLE
                videoView?.settings?.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadsImagesAutomatically = true
                }
                videoView?.webViewClient = WebViewClient()
                videoView?.loadDataWithBaseURL(
                    "https://www.youtube-nocookie.com",
                    buildYoutubeEmbedHtml(videoUrl),
                    "text/html",
                    "utf-8",
                    null
                )
            } else {
                Toast.makeText(requireContext(), "Video tutorial $namaGerakan belum tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        overlay?.setOnClickListener(playVideoAction)
        playBtn?.setOnClickListener(playVideoAction)
        imgDemo?.setOnClickListener(playVideoAction)

        // 9. Tombol Selesai -> Memicu Pop-Up Evaluasi
        btnSelesai.setOnClickListener {
            if (moveId.isNotBlank()) {
                tampilkanDialogEvaluasi(moveId, namaGerakan, cardStatus, tvStatusText)
            } else {
                Toast.makeText(requireContext(), "Error: ID Gerakan tidak ditemukan!", Toast.LENGTH_SHORT).show()
            }
        }

        // ==========================================
        // 10. FITUR FAVORIT
        // ==========================================
        fun updateFavoriteIcon(status: Boolean) {
            if (status) {
                ivFavorite.setImageResource(android.R.drawable.btn_star_big_on)
                ivFavorite.setColorFilter(Color.parseColor("#FF5252"))
            } else {
                ivFavorite.setImageResource(android.R.drawable.btn_star_big_off)
                ivFavorite.setColorFilter(Color.parseColor("#FFFFFF"))
            }
        }

        if (moveId.isNotBlank()) {
            viewLifecycleOwner.lifecycleScope.launch {
                isFavorited = repository.cekIsFavorite(currentUserId, moveId)
                updateFavoriteIcon(isFavorited)
            }
        }

        ivFavorite.setOnClickListener {
            if (moveId.isBlank()) return@setOnClickListener

            it.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()

            isFavorited = !isFavorited
            updateFavoriteIcon(isFavorited)

            viewLifecycleOwner.lifecycleScope.launch {
                val sukses = if (isFavorited) {
                    repository.tambahFavorite(currentUserId, moveId)
                } else {
                    repository.hapusFavorite(currentUserId, moveId)
                }

                if (!sukses) {
                    isFavorited = !isFavorited
                    updateFavoriteIcon(isFavorited)
                    Toast.makeText(requireContext(), "Gagal mengupdate favorit", Toast.LENGTH_SHORT).show()
                } else {
                    val pesan = if (isFavorited) "Ditambahkan ke Favorit" else "Dihapus dari Favorit"
                    Toast.makeText(requireContext(), pesan, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi untuk memunculkan Bottom Sheet Dialog & Simpan Evaluasi ke Supabase
    private fun tampilkanDialogEvaluasi(
        moveId: String,
        namaGerakan: String,
        cardStatus: MaterialCardView,
        tvStatusText: TextView
    ) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_evaluasi, null)
        bottomSheetDialog.setContentView(view)

        val btnKurang = view.findViewById<MaterialButton>(R.id.btnEvalKurang)
        val btnLumayan = view.findViewById<MaterialButton>(R.id.btnEvalLumayan)
        val btnMantap = view.findViewById<MaterialButton>(R.id.btnEvalMantap)

        // Helper untuk menyimpan ke DB sekaligus memperbarui status card di UI
        fun simpanDanUpdate(note: String, pesanToast: String) {
            Toast.makeText(requireContext(), pesanToast, Toast.LENGTH_SHORT).show()
            viewLifecycleOwner.lifecycleScope.launch {
                repository.simpanRefleksi(currentUserId, moveId, note)
                repository.updateStreak(currentUserId)
                updateUIRefleksi(note, cardStatus, tvStatusText)
            }
            bottomSheetDialog.dismiss()
        }

        btnKurang.setOnClickListener {
            simpanDanUpdate("Masih Kaku", "Tetap semangat! Latihan terus $namaGerakan.")
        }

        btnLumayan.setOnClickListener {
            simpanDanUpdate("Lumayan", "Nice! Dikit lagi $namaGerakan lu sempurna.")
        }

        btnMantap.setOnClickListener {
            simpanDanUpdate("GG Banget", "GG Banget! Lu udah nguasain $namaGerakan.")
        }

        bottomSheetDialog.show()
    }

    // Fungsi untuk ganti warna Card Status sesuai hasil evaluasi
    private fun updateUIRefleksi(note: String, cardStatus: MaterialCardView, tvStatusText: TextView) {
        cardStatus.visibility = View.VISIBLE
        tvStatusText.text = "Status: $note"

        when (note) {
            "Masih Kaku" -> {
                cardStatus.setCardBackgroundColor("#33FF5252".toColorInt())
                cardStatus.strokeColor = "#FF5252".toColorInt()
                tvStatusText.setTextColor("#FF5252".toColorInt())
            }
            "Lumayan" -> {
                cardStatus.setCardBackgroundColor("#33FF9800".toColorInt())
                cardStatus.strokeColor = "#FF9800".toColorInt()
                tvStatusText.setTextColor("#FF9800".toColorInt())
            }
            "GG Banget" -> {
                cardStatus.setCardBackgroundColor("#33FFD700".toColorInt())
                cardStatus.strokeColor = "#FFD700".toColorInt()
                tvStatusText.setTextColor("#FFD700".toColorInt())
            }
        }
    }

    override fun onDestroyView() {
        moveVideoWebView?.apply {
            stopLoading()
            loadUrl("about:blank")
            destroy()
        }
        moveVideoWebView = null
        super.onDestroyView()
    }

    private fun buildYoutubeEmbedHtml(videoUrl: String): String {
        val embedUrl = extractYoutubeEmbedUrl(videoUrl)
        return """
            <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <style>
                  html, body {
                    margin: 0;
                    padding: 0;
                    background: #000000;
                    width: 100%;
                    height: 100%;
                    overflow: hidden;
                  }
                  .wrap {
                    position: relative;
                    width: 100%;
                    height: 100%;
                  }
                  iframe {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    border: 0;
                  }
                </style>
              </head>
              <body>
                <div class="wrap">
                  <iframe
                    src="$embedUrl"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                  </iframe>
                </div>
              </body>
            </html>
        """.trimIndent()
    }

    private fun extractYoutubeEmbedUrl(videoUrl: String): String {
        val videoId = when {
            videoUrl.contains("youtube.com/embed/") -> videoUrl.substringAfter("youtube.com/embed/")
                .substringBefore('?')
                .substringBefore('/')

            videoUrl.contains("youtu.be/") -> videoUrl.substringAfter("youtu.be/")
                .substringBefore('?')
                .substringBefore('/')

            videoUrl.contains("youtube.com/shorts/") -> videoUrl.substringAfter("youtube.com/shorts/")
                .substringBefore('?')
                .substringBefore('/')

            videoUrl.contains("watch") -> {
                val uri = videoUrl.toUri()
                uri.getQueryParameter("v").orEmpty()
            }

            else -> ""
        }

        return if (videoId.isNotBlank()) {
            "https://www.youtube-nocookie.com/embed/$videoId?playsinline=1&rel=0&modestbranding=1&origin=https://www.youtube-nocookie.com"
        } else {
            videoUrl
        }
    }

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
                bitmap?.let { imageView.setImageBitmap(it) }
            }
        } catch (_: Exception) {
            // Abaikan jika error, biarkan menggunakan gambar default
        }
    }
}