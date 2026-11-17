package com.lara365.experience

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.lara365.experience.network.ApiService
import com.lara365.experience.network.RetrofitClient
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.lara365.experience.ui.LaraExperienceCard

class LaraExperienceCardActivity : ComponentActivity() {

    private val apiService: ApiService by lazy { RetrofitClient.instance }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nameInicial = intent.getStringExtra("USER_NAME") ?: "Usuario Lara 365"
        val xpInicial = intent.getIntExtra("TOTAL_XP", 0)
        val serialInicial = intent.getStringExtra("SERIAL_NUMBER") ?: "1234567890123456"
        val rankInicial = intent.getStringExtra("USER_RANK") ?: "Explorador"
        val id = intent.getIntExtra("USER_ID", 0)

        setContent {
            var xpActual by remember { mutableStateOf(xpInicial) }
            var rangoActual by remember { mutableStateOf(rankInicial) }
            var cargando by remember { mutableStateOf(true) }

            // Usamos el scope nativo de LaunchedEffect
            LaunchedEffect(Unit) {
                try {
                    val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
                    val token = sharedPref.getString("auth_token", "") ?: ""

                    if (token.isNotEmpty()) {
                        // Ahora la petición se ejecutará en el hilo correcto sin cancelarse
                        val respuesta = apiService.obtenerPerfil("Bearer $token", id)

                        if (respuesta.isSuccessful && respuesta.body() != null) {
                            val datosActualizados = respuesta.body()!!
                            android.util.Log.d("TARJETA_DEBUG", "XP que llegó de Laravel: ${datosActualizados.user.total_xp}")

                            xpActual = datosActualizados.user.total_xp
                            rangoActual = datosActualizados.user.user_rank

                            with(sharedPref.edit()) {
                                putInt("user_xp", xpActual)
                                putString("user_rank", rangoActual)
                                apply()
                            }
                        } else {
                            android.util.Log.e("TARJETA_DEBUG", "Error en servidor: ${respuesta.code()}")
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TARJETA_DEBUG", "Excepción de red: ${e.message}")
                    e.printStackTrace()
                } finally {
                    cargando = false
                }
            }

            LaraExperienceCard(
                userName = nameInicial,
                totalXP = xpActual,
                serialNumber = serialInicial,
                userRank = rangoActual,
                userId = id,
                onBackClick = { finish() }
            )
        }
    }
}