package com.lara365.experience.models

data class EbookCompleteResponse(
    val success: Boolean,
    val message: String,
    val xp_earned: Int,
    val total_xp: Int
)