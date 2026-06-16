package com.example.roadofdojo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri


class TipsDetailFragment : Fragment() {

    companion object {
        // Key untuk Bundle argument
        const val ARG_TITLE = "tips_title"
        const val ARG_BODY = "tips_body"
        const val ARG_VIDEO = "tips_video"
    }

    private var tipVideoWebView: WebView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tips_detail, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil judul dan konten dari Bundle
        val tipsTitle = arguments?.getString(ARG_TITLE) ?: "Quick Tips"
        val tipsBody = arguments?.getString(ARG_BODY) ?: ""
        val tipsVideo = arguments?.getString(ARG_VIDEO).orEmpty()

        // Tampilkan judul dan konten di layout
        view.findViewById<TextView>(R.id.tvTipsTitle).text = tipsTitle
        view.findViewById<TextView>(R.id.tvTipsBody).text = tipsBody

        val videoLabel = view.findViewById<TextView>(R.id.tvVideoLabel)
        val videoView = view.findViewById<WebView>(R.id.wvTipVideo)
        tipVideoWebView = videoView

        if (tipsVideo.isNotBlank()) {
            videoLabel.visibility = View.VISIBLE
            videoView.visibility = View.VISIBLE
            videoView.settings.javaScriptEnabled = true
            videoView.settings.domStorageEnabled = true
            videoView.settings.loadsImagesAutomatically = true
            videoView.webViewClient = WebViewClient()
            videoView.loadDataWithBaseURL(
                "https://www.youtube-nocookie.com",
                buildYoutubeEmbedHtml(tipsVideo),
                "text/html",
                "utf-8",
                null
            )
        } else {
            videoLabel.visibility = View.GONE
            videoView.visibility = View.GONE
        }

        // Update judul toolbar sesuai tips yang dipilih
        (activity as? QuickTipsActivity)?.supportActionBar?.title = tipsTitle
    }

    override fun onDestroyView() {
        tipVideoWebView?.apply {
            stopLoading()
            loadUrl("about:blank")
            destroy()
        }
        tipVideoWebView = null
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
}