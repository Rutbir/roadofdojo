package com.example.roadofdojo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

        private val title: TextView = itemView.findViewById(R.id.tvTipTitle)

        fun bind(item: QuickTip) {
            title.text = item.title
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}

