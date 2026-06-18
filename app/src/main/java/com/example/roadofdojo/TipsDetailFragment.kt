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
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.core.net.toUri

class TipsDetailFragment : Fragment() {

    companion object {
        const val ARG_TITLE = "tips_title"
        const val ARG_BODY = "tips_body"
        const val ARG_VIDEO = "tips_video"
    }

    private var tipVideoWebView: WebView? = null

    // Variabel buat nyimpen view pas layar dibikin fullscreen
    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

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

        val tipsTitle = arguments?.getString(ARG_TITLE) ?: "Quick Tips"
        val tipsBody = arguments?.getString(ARG_BODY) ?: ""
        val tipsVideo = arguments?.getString(ARG_VIDEO).orEmpty()

        view.findViewById<TextView>(R.id.tvTipsTitle).text = tipsTitle
        view.findViewById<TextView>(R.id.tvTipsBody).text = tipsBody

        val videoView = view.findViewById<WebView>(R.id.wvTipVideo)
        val nsvMainContent = view.findViewById<NestedScrollView>(R.id.nsvMainContent)
        val flFullscreenContainer = view.findViewById<FrameLayout>(R.id.flFullscreenContainer)

        tipVideoWebView = videoView

        if (tipsVideo.isNotBlank()) {
            videoView.visibility = View.VISIBLE
            videoView.settings.javaScriptEnabled = true
            videoView.settings.domStorageEnabled = true
            videoView.settings.loadsImagesAutomatically = true
            videoView.webViewClient = WebViewClient()

            // MAGIC FULLSCREEN DI SINI BRO!
            videoView.webChromeClient = object : WebChromeClient() {
                // Pas tombol fullscreen diklik
                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    if (customView != null) {
                        callback?.onCustomViewHidden()
                        return
                    }
                    customView = view
                    customViewCallback = callback

                    // Sembunyiin layout normal, tampilin layout fullscreen item
                    nsvMainContent.visibility = View.GONE
                    flFullscreenContainer.visibility = View.VISIBLE
                    flFullscreenContainer.addView(view)
                }

                // Pas tombol minimize / back diklik
                override fun onHideCustomView() {
                    super.onHideCustomView()
                    if (customView == null) return

                    // Copot video dari fullscreen, balikin UI normal
                    flFullscreenContainer.removeView(customView)
                    flFullscreenContainer.visibility = View.GONE
                    nsvMainContent.visibility = View.VISIBLE

                    customView = null
                    customViewCallback?.onCustomViewHidden()
                    customViewCallback = null
                }
            }

            videoView.loadDataWithBaseURL(
                "https://www.youtube-nocookie.com",
                buildYoutubeEmbedHtml(tipsVideo),
                "text/html",
                "utf-8",
                null
            )
        } else {
            videoView.visibility = View.GONE
        }

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
                  html, body { margin: 0; padding: 0; background: #000000; width: 100%; height: 100%; overflow: hidden; }
                  .wrap { position: relative; width: 100%; height: 100%; }
                  iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }
                </style>
              </head>
              <body>
                <div class="wrap">
                  <iframe src="$embedUrl" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>
                </div>
              </body>
            </html>
        """.trimIndent()
    }

    private fun extractYoutubeEmbedUrl(videoUrl: String): String {
        val videoId = when {
            videoUrl.contains("youtube.com/embed/") -> videoUrl.substringAfter("youtube.com/embed/").substringBefore('?').substringBefore('/')
            videoUrl.contains("youtu.be/") -> videoUrl.substringAfter("youtu.be/").substringBefore('?').substringBefore('/')
            videoUrl.contains("youtube.com/shorts/") -> videoUrl.substringAfter("youtube.com/shorts/").substringBefore('?').substringBefore('/')
            videoUrl.contains("watch") -> { val uri = videoUrl.toUri(); uri.getQueryParameter("v").orEmpty() }
            else -> ""
        }
        return if (videoId.isNotBlank()) {
            "https://www.youtube-nocookie.com/embed/$videoId?playsinline=1&rel=0&modestbranding=1&origin=https://www.youtube-nocookie.com"
        } else {
            videoUrl
        }
    }
}