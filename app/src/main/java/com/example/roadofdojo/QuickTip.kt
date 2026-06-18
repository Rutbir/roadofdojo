package com.example.roadofdojo

data class QuickTip(
    val id: String,
    val title: String,
    val description: String, // Tadi namanya 'content', diubah biar klop sama Adapter
    val sortOrder: Int,
    val category: String? = null, // Buat nangkep teks kategori (misal: DEFENSE)
    val imageUrl: String? = null, // Buat nangkep link thumbnail gambar dari Supabase
    val video: String? = null
)