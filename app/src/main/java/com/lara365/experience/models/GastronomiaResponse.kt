package com.lara365.experience.models

data class GastronomiaResponse(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: String, // 'recetario', 'ruta', 'capacitacion'
    val ubicacion_o_autor: String?,
    val imagen_url: String?,
    val xp_puntos: Int
)