package com.example.roadofdojo

import android.annotation.SuppressLint
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class BoxingDetailFragment : Fragment() {

    companion object {
        const val ARG_NAMA  = "nama_gerakan"
        const val ARG_LEVEL = "level_gerakan"
        const val ARG_DESC  = "desc_gerakan"
        const val ARG_VIDEO = "video_url"
        const val ARG_IMAGE = "image_url"
    }

    private var moveVideoWebView: WebView? = null

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

        // 1. Ambil data dari Bundle/Arguments
        val namaGerakan = arguments?.getString(ARG_NAMA)  ?: "Jab"
        val level       = arguments?.getString(ARG_LEVEL) ?: "BEGINNER"
        val desc        = arguments?.getString(ARG_DESC)  ?: "Instruksi langkah demi langkah akan tampil di sini. Jaga guard tangan tetap di depan wajah."
        val videoUrl    = arguments?.getString(ARG_VIDEO).orEmpty()
        val imageUrl    = arguments?.getString(ARG_IMAGE).orEmpty()

        // 2. Set judul Toolbar (opsional jika dipanggil dari BoxingActivity)
        try {
            // Uncomment baris di bawah ini jika BoxingActivity punya method setToolbarTitle
            // (activity as? BoxingActivity)?.setToolbarTitle(namaGerakan)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Inisialisasi Views
        val tvTitle = view.findViewById<TextView>(R.id.tvGerakanTitle)
        val tvLevel = view.findViewById<TextView>(R.id.tvLevel)
        val tvDesc  = view.findViewById<TextView>(R.id.tvGerakanDesc)
        val cardLevel = view.findViewById<MaterialCardView>(R.id.cardLevel)
        val btnSelesai = view.findViewById<MaterialButton>(R.id.btnSelesaiLatihan)

        // Views untuk pemutar video YouTube
        val overlay = view.findViewById<View>(R.id.viewOverlay)
        val playBtn = view.findViewById<ImageView>(R.id.ivPlayOverlay)
        val videoView = view.findViewById<WebView>(R.id.wvGerakanVideo)
        moveVideoWebView = videoView

        // Amankan imgGerakanDemo sebagai nullable karena di XML sebelumnya tidak ada
        val imgDemo = view.findViewById<ImageView?>(R.id.imgGerakanDemo)

        // 4. Masukkan data ke dalam UI
        tvTitle.text = namaGerakan
        tvLevel.text = level.uppercase()
        tvDesc.text  = desc

        // Ganti warna badge otomatis sesuai tingkat kesulitan
        when (level.uppercase()) {
            "BEGINNER" -> cardLevel.setCardBackgroundColor(Color.parseColor("#4CAF50")) // Hijau
            "INTERMEDIATE" -> cardLevel.setCardBackgroundColor(Color.parseColor("#FF9800")) // Orange
            "ADVANCED" -> cardLevel.setCardBackgroundColor(Color.parseColor("#F44336")) // Merah
            else -> cardLevel.setCardBackgroundColor(Color.parseColor("#FFD700")) // Emas
        }

        // Load gambar dari Internet jika URL tersedia
        if (imageUrl.isNotBlank() && imgDemo != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                loadImageInto(imgDemo, imageUrl)
            }
        }

        // 5. Setup fitur memutar Video Tutorial (YouTube Embed)
        val playVideoAction = View.OnClickListener {
            if (videoUrl.isNotBlank()) {
                // Sembunyikan cover overlay
                overlay?.visibility = View.GONE
                playBtn?.visibility = View.GONE
                imgDemo?.visibility = View.GONE

                // Tampilkan dan mainkan video
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

        // Trigger video diputar jika tombol play atau background-nya diklik
        overlay?.setOnClickListener(playVideoAction)
        playBtn?.setOnClickListener(playVideoAction)
        imgDemo?.setOnClickListener(playVideoAction)

        // 6. Tombol Selesai
        btnSelesai.setOnClickListener {
            Toast.makeText(requireContext(), "Mantap! Latihan $namaGerakan selesai.", Toast.LENGTH_SHORT).show()
            // Nantinya di sini kamu bisa tambahin logika update progress/XP user
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

    // Fungsi untuk membuat frame HTML pemutar YouTube
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

    // Fungsi untuk mendapatkan ID YouTube dari berbagai format link
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

    // Fungsi suspend untuk download dan pasang gambar (Coroutines)
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