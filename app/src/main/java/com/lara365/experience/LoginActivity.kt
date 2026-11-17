package com.lara365.experience

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.lara365.experience.network.ApiService
import com.lara365.experience.network.RetrofitClient
import com.lara365.experience.ui.LoginScreen
import com.lara365.experience.ui.RegisterScreen
import kotlinx.coroutines.launch
import java.io.IOException

class LoginActivity : ComponentActivity() {

    // Instancia de tu servicio de Retrofit (Asegúrate de que apunte a tu cliente configurado)
    private val apiService: ApiService by lazy {
        RetrofitClient.instance // O como tengas tu singleton de Retrofit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. VERIFICACIÓN RÁPIDA: ¿Ya inició sesión antes?
        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val token = sharedPref.getString("auth_token", null)
        if (token != null) {
            // Si el token existe, saltamos directo al Menú Principal y cerramos el Login
            irAlMenuPrincipal()
            return
        }

        // 2. SI NO TIENE SESIÓN, CARGAMOS EL FLUJO DE COMPOSE
        setContent {
            // Un estado sencillo para alternar entre pantallas: "login" o "register"
            var pantallaActual by remember { mutableStateOf("login") }

            if (pantallaActual == "login") {
                LoginScreen(
                    onLoginClick = { email, password ->
                        ejecutarLogin(email, password)
                    },
                    onNavigateToRegister = {
                        pantallaActual = "register" // Cambia la vista a Registro
                    }
                )
            } else {
                RegisterScreen(
                    onRegisterClick = { nombre, email, password ->
                        ejecutarRegistro(nombre, email, password)
                    },
                    onNavigateToLogin = {
                        pantallaActual = "login" // Regresa a la vista de Login
                    }
                )
            }
        }
    }

    // 3. LÓGICA DE PETICIÓN PARA EL LOGIN
    private fun ejecutarLogin(email: String, password: String) {
        // Ejecutamos en una corrutina porque Android no permite peticiones de red en el hilo principal
        lifecycleScope.launch {
            try {
                val campos = hashMapOf(
                    "email" to email.trim(),
                    "password" to password.trim(),
                    "device_name" to android.os.Build.MODEL // Envía el modelo del teléfono como device_name
                )

                val respuesta = apiService.login(campos)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val authData = respuesta.body()!!

                    // Extraemos el serial de la tarjeta si viene, si no, le ponemos un valor por defecto o vacío
                    val serialNumber = authData.card?.serial_number ?: "0000000000000000"

                    // Guardamos el token y datos clave localmente
                    guardarSesion(authData.token, authData.user.id, authData.user.name, authData.user.total_xp, serialNumber, authData.user.user_rank)

                    Toast.makeText(this@LoginActivity, "¡Bienvenido, ${authData.user.name}!", Toast.LENGTH_SHORT).show()
                    irAlMenuPrincipal()
                } else {
                    Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@LoginActivity, "Error de conexión. Revisa tu internet", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Ocurrió un error inesperado", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 4. LÓGICA DE PETICIÓN PARA EL REGISTRO
    private fun ejecutarRegistro(nombre: String, email: String, password: String) {
        lifecycleScope.launch {
            try {
                val campos = hashMapOf(
                    "name" to nombre,
                    "email" to email,
                    "password" to password,
                    "password_confirmation" to password, // Laravel pide la confirmación idéntica
                    "device_name" to android.os.Build.MODEL
                )

                val respuesta = apiService.register(campos)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val authData = respuesta.body()!!

                    // Extraemos el serial de la tarjeta si viene, si no, le ponemos un valor por defecto o vacío
                    val serialNumber = authData.card?.serial_number ?: "0000000000000000"

                    // Guardamos la sesión del usuario recién creado
                    guardarSesion(authData.token, authData.user.id, authData.user.name, authData.user.total_xp, serialNumber, authData.user.user_rank)

                    Toast.makeText(this@LoginActivity, "Registro exitoso. ¡Bienvenido!", Toast.LENGTH_SHORT).show()
                    irAlMenuPrincipal()
                } else {
                    Toast.makeText(this@LoginActivity, "Error en el registro. El correo ya podría estar en uso", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@LoginActivity, "Error de red", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Funciones utilitarias para la persistencia y navegación
        private fun guardarSesion(token: String, userId: Int, userName: String, totalXP: Int, serialNumber: String, userRank: String) {
        val sharedPref = getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("auth_token", token)
            putInt("user_id", userId)
            putString("user_name", userName)
            putInt("user_xp", totalXP)
            putString("serial_number", serialNumber)
            putString("user_rank", userRank)
            apply() // Guarda en segundo plano
        }
    }

    private fun irAlMenuPrincipal() {
        // Cambia "MenuPrincipalActivity::class.java" por el nombre real de tu Activity del menú
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Destruye el LoginActivity para que el usuario no pueda volver atrás con el botón físico
    }
}