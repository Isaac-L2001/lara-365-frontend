package com.lara365.experience.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Route(
    val id: Int,
    @SerializedName("nombre_ruta") val nombreRuta: String,
    @SerializedName("tipo_experiencia") val tipoExperiencia: String, // 'pildora_cultural' o 'jornada_descubrimiento'
    val municipio: String, // 'iribarren', 'andres_eloy_blanco', etc.
    val itinerario: String,
    @SerializedName("punto_salida") val puntoSalida: String,
    @SerializedName("duracion_estimada") val duracionEstimada: String,
    val costo: Double,
    @SerializedName("image_url") val imageUrl: String?
) : Serializable