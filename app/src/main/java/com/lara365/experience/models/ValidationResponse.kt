package com.lara365.experience.models

import com.google.gson.annotations.SerializedName

data class ValidationResponse(
    @SerializedName("status") val status: String,
    @SerializedName("nombre") val nombreEstablecimiento: String,
    @SerializedName("puntos_ganados") val puntosGanados: Double,
    @SerializedName("puntos_totales") val puntosTotales: Double
)