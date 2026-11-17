package com.lara365.experience.models

data class EbookModule(
    val id: Int,
    val title: String,
    val description: String?,
    val category: String, // 'principal' o 'municipio'
    val pdf_url: String,   // URL completa que genera asset() en Laravel
    val xp_reward: Int,
    val is_completed: Boolean
)