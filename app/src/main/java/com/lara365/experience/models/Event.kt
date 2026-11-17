package com.lara365.experience.models

data class Event(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val lugar: String,
    val fecha: String,
    val hora: String,
    val imagen_url: String
)