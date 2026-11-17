package com.lara365.experience.models

data class ApiResponse<T>(
    val success: Boolean,
    val data: T
)