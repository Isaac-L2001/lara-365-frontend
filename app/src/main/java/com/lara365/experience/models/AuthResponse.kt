package com.lara365.experience.models

// 1. La respuesta global que viene de Laravel al loguearse o registrarse
data class AuthResponse(
    val token: String,
    val user: UserDto,
    val card: CardDto? = null // Puede ser nulo en el login si no lo configuramos ahí, pero viene seguro en el registro
)

// 2. El mapeo del usuario
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val total_xp: Int,
    val user_rank: String
)

// 3. El mapeo de la tarjeta que viene del backend
data class CardDto(
    val serial_number: String,
    val state: String,
    val qr_code: String
)