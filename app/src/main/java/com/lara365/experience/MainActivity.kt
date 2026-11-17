package com.lara365.experience

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.lara365.experience.models.Establishment
import com.lara365.experience.models.Route
import com.lara365.experience.models.EbookModule
import com.lara365.experience.models.GastronomiaResponse
import com.lara365.experience.network.RetrofitClient
import com.lara365.experience.ui.MainScreenWithMenu
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : ComponentActivity() {

    // Estados de datos mutables controlados por el Activity
    private var listaEstablecimientos = mutableStateListOf<Establishment>()
    private var isLoadingComercios = mutableStateOf(false)

    private var listaRutas = mutableStateListOf<Route>()
    private var isLoadingRutas = mutableStateOf(false)

    // Módulo de Ebooks ("Así es Lara")
    private var listaEbooks = mutableStateListOf<EbookModule>()

    private var isLoadingEbooks = mutableStateOf(false)

    private var listaGastronomia = mutableStateListOf<GastronomiaResponse>()

    private var isLoadingGastronomia = mutableStateOf(false)

    private var userXpState = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        userXpState.value = sharedPref.getInt("user_xp", 0)

        loadEstablishments() // Carga inicial por defecto
        setContent {
            MainScreenWithMenu(
                isLoadingComercios = isLoadingComercios.value,
                listaEstablecimientos = listaEstablecimientos,
                isLoadingRutas = isLoadingRutas.value,
                listaRutas = listaRutas,
                onRouteSectionSelected = { loadRoutes() }, // Se ejecuta cuando el usuario toca "Rutas" en el menú
                onRouteClick = { rutaSeleccionada ->
                    val intent = Intent(this, RouteDetailActivity::class.java).apply {
                        putExtra("EXTRA_ROUTE", rutaSeleccionada)
                    }
                    startActivity(intent)
                },
                isLoadingEbooks = isLoadingEbooks.value,
                listaEbooks = listaEbooks,
                onEbookSectionSelected = { loadEbooks() }, // Disparar la carga al tocar el módulo en el menú
                onEbookClick = { modulo -> completarEbook(modulo.id) }, // Simular lectura al pulsar la tarjeta
                isLoadingGastronomia = isLoadingGastronomia.value,
                listaGastronomia = listaGastronomia,
                onGastronomiaSectionSelected = { loadGastronomia() },
                userXpActual = userXpState.value,
                onLogoutSelected = { cerrarSesion() }
            )
        }
    }

    // 🏬 PETICIÓN 1: COMERCIOS (LARA SHOPPING EXPERIENCE)
    private fun loadEstablishments() {
        isLoadingComercios.value = true
        RetrofitClient.instance.getEstablishments(modulo = null).enqueue(object : retrofit2.Callback<com.lara365.experience.models.ApiResponse<List<Establishment>>> {
            override fun onResponse(
                call: retrofit2.Call<com.lara365.experience.models.ApiResponse<List<Establishment>>>,
                response: retrofit2.Response<com.lara365.experience.models.ApiResponse<List<Establishment>>>
            ) {
                isLoadingComercios.value = false
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    listaEstablecimientos.clear()
                    listaEstablecimientos.addAll(list)
                } else {
                    Toast.makeText(this@MainActivity, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<com.lara365.experience.models.ApiResponse<List<Establishment>>>, t: Throwable) {
                isLoadingComercios.value = false
                Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }

    // 🗺️ PETICIÓN 2: RUTAS TURÍSTICAS (LARA CONECTA 365)
    private fun loadRoutes() {
        // Si ya cargamos las rutas una vez, evitamos pegarle al servidor innecesariamente
        if (listaRutas.isNotEmpty()) return

        isLoadingRutas.value = true
        lifecycleScope.launch {
            try {
                // Al usar "suspend fun", la respuesta se asigna directamente de manera síncrona visual, pero asíncrona por detrás
                val respuesta = RetrofitClient.instance.getRoutes()
                listaRutas.clear()
                listaRutas.addAll(respuesta.data)
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Error de red. Revisa tu internet", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "No se pudieron obtener las rutas", Toast.LENGTH_SHORT).show()
            } finally {
                isLoadingRutas.value = false
            }
        }
    }

    // 📚 PETICIÓN 3: OBTENER BIBLIOTECA DE EBOOKS
    private fun loadEbooks() {
        if (listaEbooks.isNotEmpty()) return

        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", "") ?: ""

        if (token.isEmpty()) return

        isLoadingEbooks.value = true
        lifecycleScope.launch {
            try {
                // Usamos el método que ya tienes en tu ApiService
                val respuesta = RetrofitClient.instance.obtenerEbooks("Bearer $token")

                // 🛠️ VALIDACIÓN DE RETROFIT CORRECTA
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val ebookResponse = respuesta.body()!!
                    listaEbooks.clear()
                    listaEbooks.addAll(ebookResponse.modules) // Asegúrate de que en tu EbookResponse se llame 'modules'
                } else {
                    Toast.makeText(this@MainActivity, "Error del servidor al cargar módulos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Error de red al cargar ebooks", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "No se pudieron obtener los módulos", Toast.LENGTH_SHORT).show()
            } finally {
                isLoadingEbooks.value = false
            }
        }
    }

    // PETICIÓN 4: COMPLETAR MÓDULO FORMATIVO
    private fun completarEbook(moduloId: Int) {
        // 🕵️‍♂️ CANDADO EN ANDROID: Buscamos el módulo en la lista local antes de ir a Laravel
        val moduloLocal = listaEbooks.find { it.id == moduloId }

        // Si el módulo ya tiene el check verde (is_completed == true), frenamos el click de una vez
        if (moduloLocal != null && moduloLocal.is_completed) {
            Toast.makeText(this, "¡Ya completaste esta lectura! Busca otro módulo para seguir ganando XP.", Toast.LENGTH_SHORT).show()
            return // Rompe la función aquí y no le pega a Laravel
        }

        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", "") ?: ""

        if (token.isEmpty()) return

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.instance.completarModuloEbook("Bearer $token", moduloId)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val data = respuesta.body()!!
                    Toast.makeText(this@MainActivity, data.message, Toast.LENGTH_LONG).show()

                    with(sharedPref.edit()) {
                        putInt("user_xp", data.total_xp)
                        apply()
                    }

                    userXpState.value = data.total_xp

                    val index = listaEbooks.indexOfFirst { it.id == moduloId }
                    if (index != -1) {
                        val moduloActualizado = listaEbooks[index].copy(is_completed = true)
                        listaEbooks[index] = moduloActualizado
                    }
                } else {
                    // Si el servidor responde un 400 o un error controlado, intentamos alertar al usuario
                    Toast.makeText(this@MainActivity, "Módulo ya registrado o ID inválido", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al registrar la lectura", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // PETICIÓN 5: LARA GASTRONÓMICA
    private fun loadGastronomia() {
        // Si ya cargamos las rutas una vez, evitamos pegarle al servidor innecesariamente
        if (listaGastronomia.isNotEmpty()) return

        isLoadingGastronomia.value = true
        lifecycleScope.launch {
            try {
                // Al usar "suspend fun", la respuesta se asigna directamente de manera síncrona visual, pero asíncrona por detrás
                val respuesta = RetrofitClient.instance.getGastronomia()
                listaGastronomia.clear()
                listaGastronomia.addAll(respuesta)
            } catch (e: IOException) {
                Toast.makeText(this@MainActivity, "Error de red. Revisa tu internet", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "No se pudo obtener la gastronomía", Toast.LENGTH_SHORT).show()
            } finally {
                isLoadingGastronomia.value = false
            }
        }
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        if (token != null) {
            lifecycleScope.launch {
                try {
                    val tokenFormateado = "Bearer $token"
                    RetrofitClient.instance.logout(tokenFormateado)
                    limpiarDatosLocalesYSalir()
                } catch (e: IOException) {
                    Toast.makeText(this@MainActivity, "Cierre de sesión local", Toast.LENGTH_SHORT).show()
                    limpiarDatosLocalesYSalir()
                }
            }
        } else {
            limpiarDatosLocalesYSalir()
        }
    }

    private fun limpiarDatosLocalesYSalir() {
        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}