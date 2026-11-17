package com.lara365.experience.models

import com.google.gson.annotations.SerializedName

data class CardResponse (
    @SerializedName("nombre") val nombre: String,
    @SerializedName("puntos_totales") val puntosTotales: Double,
    @SerializedName("estado") val estado: String
)