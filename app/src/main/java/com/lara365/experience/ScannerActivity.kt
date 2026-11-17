package com.lara365.experience

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.lara365.experience.network.RetrofitClient
import com.lara365.experience.ui.ScannerScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@androidx.camera.core.ExperimentalGetImage
class ScannerActivity : ComponentActivity() {

    // Estados reactivos globales compartidos con Compose
    private var isProcessing = false
    private var isLoadingState = mutableStateOf(false)

    // Estados para controlar la tarjeta flotante de resultado final
    private var showResultCard = mutableStateOf(false)
    private var resultTitle = mutableStateOf("")
    private var resultMessage = mutableStateOf("")
    private var resultCardColor = mutableStateOf(Color(0xFF4CAF50)) // Verde por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expectedId = intent.getIntExtra("EXPECTED_ID", -1)

        // Verificamos permisos al arrancar usando Compose nativo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanner(expectedId)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val expectedId = intent.getIntExtra("EXPECTED_ID", -1)
        if (isGranted) {
            startScanner(expectedId)
        } else {
            Toast.makeText(this, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startScanner(expectedId: Int) {
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {

                // 1. LLAMAMOS AL SCREEN QUE CONSTRUISTE CON LA CÁMARA
                ScannerScreen(
                    targetEstablishmentId = expectedId,
                    onQrScanned = { rawValue ->
                        val cleanValue = rawValue.trim()
                        Log.d(
                            "DEBUG_SCANNER",
                            "Leído del QR: '$cleanValue' | Esperado: '$expectedId'"
                        )

                        if (cleanValue == expectedId.toString() && !isProcessing) {
                            isProcessing = true
                            vibrarTelefono()

                            // Activamos el cargando nativo en Compose
                            isLoadingState.value = true
                            enviarDatosAlServidor(cleanValue)
                        } else if (!isProcessing) {
                            vibrarTelefono()
                            Toast.makeText(
                                this@ScannerActivity,
                                "QR inválido para este establecimiento",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    onCloseClick = { finish() }
                )

                // 2. NUESTRO LOADING OVERLAY (Tu función composable adaptada)
                LoadingOverlay(isLoading = isLoadingState.value)

                // 3. TARJETA DE RESULTADO FINAL (Reemplazo del viejo Card XML)
                if (showResultCard.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = resultTitle.value,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = resultCardColor.value
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = resultMessage.value,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun enviarDatosAlServidor(codigo: String) {
        lifecycleScope.launch {
            try {
                // Tu validarTarjeta de Laravel (pasando código y ID fijo o dinámico de usuario)
                val response = RetrofitClient.instance.validarTarjeta(codigo, 1)
                withContext(Dispatchers.Main) {
                    isLoadingState.value = false
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        actualizarInterfazExito(
                            data.nombreEstablecimiento,
                            data.puntosGanados,
                            data.puntosTotales
                        )
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        actualizarInterfazError("Servidor dice: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoadingState.value = false
                    actualizarInterfazError("Error de conexión con el servidor")
                }
            }
        }
    }

    private fun actualizarInterfazExito(nombre: String, ganados: Double, totales: Double) {
        resultCardColor.value = Color(0xFF4CAF50) // Verde Éxito
        resultTitle.value = "¡Puntos Sumados!"
        resultMessage.value = "Establecimiento: $nombre\nGanaste: ${ganados.toInt()} pts\nTotal acumulado: ${totales.toInt()} pts"
        showResultCard.value = true

        Handler(Looper.getMainLooper()).postDelayed({ resetearInterfaz(debeCerrar = true) }, 4000)
    }

    private fun actualizarInterfazError(mensaje: String) {
        resultCardColor.value = Color(0xFFE53935)
        resultTitle.value = "Error de Validación"
        resultMessage.value = mensaje
        showResultCard.value = true

        Handler(Looper.getMainLooper()).postDelayed({ resetearInterfaz(debeCerrar = false) }, 4000)
    }

    private fun resetearInterfaz(debeCerrar: Boolean) {
        showResultCard.value = false
        isProcessing = false
        if (debeCerrar) {
            finish() // Cerramos el escáner automáticamente si todo fue un éxito
        }
    }

    private fun vibrarTelefono() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(150, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(150)
        }
    }

    @Composable
    fun LoadingOverlay(isLoading: Boolean) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFFE53935))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Validando en Lara 365...", color = Color.Black)
                    }
                }
            }
        }
    }
}