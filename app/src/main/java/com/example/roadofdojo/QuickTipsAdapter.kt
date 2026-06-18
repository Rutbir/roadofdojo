package com.example.roadofdojo

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class QuickTipsAdapter(
    private val onItemClick: (QuickTip) -> Unit
) : RecyclerView.Adapter<QuickTipsAdapter.QuickTipsViewHolder>() {

    private val items = mutableListOf<QuickTip>()

    fun setItems(newItems: List<QuickTip>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickTipsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quick_tip, parent, false)
        return QuickTipsViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: QuickTipsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class QuickTipsViewHolder(
        itemView: View,
        private val onItemClick: (QuickTip) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        // Kenalin ID dari layout XML yang baru
        private val title: TextView = itemView.findViewById(R.id.tvTipTitle)
        private val description: TextView = itemView.findViewById(R.id.tvTipDescription)
        private val category: TextView = itemView.findViewById(R.id.tvTipCategory)
        private val thumbnail: ImageView = itemView.findViewById(R.id.imgTipThumbnail)

        fun bind(item: QuickTip) {
            // 1. Set teks
            title.text = item.title
            description.text = item.description // Udah ganti dari content ke description
            category.text = item.category ?: "TIPS"

            // 2. Set gambar default/kosongan biar bersih pas di-recycle
            thumbnail.setImageResource(R.drawable.quick_tips)

            // Pasang "KTP" URL di ImageView biar gambarnya gak ketuker
            thumbnail.tag = item.imageUrl

            // 3. Tarik gambar dari Supabase pakai Coroutines
            if (!item.imageUrl.isNullOrBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val url = URL(item.imageUrl)
                        val conn = (url.openConnection() as HttpURLConnection).apply {
                            connectTimeout = 10_000
                            readTimeout = 10_000
                            doInput = true
                        }
                        conn.connect()
                        val stream = conn.inputStream
                        val bitmap = BitmapFactory.decodeStream(stream)
                        stream.close()

                        // Balik ke UI Thread buat masang gambarnya
                        withContext(Dispatchers.Main) {
                            // Validasi: Cek apa KTP-nya masih sama?
                            if (thumbnail.tag == item.imageUrl && bitmap != null) {
                                thumbnail.setImageBitmap(bitmap)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Kalau error (misal ga ada sinyal / link mati), tetep pake gambar default
                    }
                }
            }

            // 4. Klik kartu buat navigasi
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}