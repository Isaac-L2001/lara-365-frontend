package com.lara365.experience.network

import android.content.Context
import android.util.Log
import com.lara365.experience.models.EbookCompleteResponse
import com.lara365.experience.models.EbookResponse

object EbookRepository {
    private const val TAG = "EBOOK_REPOSITORY"
    private const val PREFS_NAME = "LaraExperiencePrefs"
    private const val TOKEN_KEY = "auth_token"

    // 1. Cargar todos los módulos formativos
    suspend fun obtenerBiblioteca(context: Context): EbookResponse? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = sharedPref.getString(TOKEN_KEY, "") ?: ""

        if (token.isEmpty()) {
            Log.e(TAG, "No se encontró el token de autenticación")
            return null
        }

        return try {
            val respuesta = RetrofitClient.instance.obtenerEbooks("Bearer $token")
            if (respuesta.isSuccessful) {
                respuesta.body()
            } else {
                Log.e(TAG, "Error en el servidor al obtener ebooks: ${respuesta.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error de red al conectar a Laravel: ${e.message}")
            null
        }
    }

    // 2. Marcar un módulo como completado en Laravel y procesar la XP ganada
    suspend fun completarModulo(context: Context, moduloId: Int): EbookCompleteResponse? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val token = sharedPref.getString(TOKEN_KEY, "") ?: ""

        if (token.isEmpty()) return null

        return try {
            val respuesta = RetrofitClient.instance.completarModuloEbook("Bearer $token", moduloId)
            if (respuesta.isSuccessful && respuesta.body() != null) {
                val data = respuesta.body()!!

                // 💾 Actualizamos la XP global del usuario en local de inmediato
                with(sharedPref.edit()) {
                    putInt("user_xp", data.total_xp)
                    apply()
                }
                Log.d(TAG, "¡Módulo completado con éxito! Nueva XP local: ${data.total_xp}")
                data
            } else {
                Log.e(TAG, "Error en servidor al completar módulo: ${respuesta.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error de red al completar módulo: ${e.message}")
            null
        }
    }
}