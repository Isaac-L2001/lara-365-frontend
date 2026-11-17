package com.lara365.experience.models

import java.io.Serializable

data class Establishment(
    val id: Int,
    val nombre_comercio: String,
    val categoria: String,
    val porcentaje_descuento: Double,
    val ubicacion: String,
    val image_url: String,
    val modulo: String,
    val es_punto_interes: Int // Laravel suele enviar los booleanos como 1 o 0
) : Serializable