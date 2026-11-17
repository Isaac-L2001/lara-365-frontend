package com.lara365.experience.ui // Asegúrate de que sea tu paquete real

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun LaraExperienceCard(
    userName: String,
    totalXP: Int,
    serialNumber: String,
    userRank: String,
    userId: Int,
    onBackClick: () -> Unit
) {
    val qrBitmap = remember(userId) {
        generarCodigoQR(texto = "LARA365-USER-$userId")
    }

    // 💳 Para agrupar el string del serial de 4 en 4 separados por un espacio
    val serialFormateado = remember(serialNumber) {
        serialNumber.replace("(.{4})".toRegex(), "$1 ").trim()
    }

    // LÓGICA DE GAMIFICACIÓN EN EL FRONT:
    val datosSiguienteNivel = remember(totalXP) {
        when {
            totalXP < 100 -> Pair("Baquiano Larense", 100)
            totalXP < 300 -> Pair("Guaro Crepuscular", 300)
            totalXP < 600 -> Pair("Embajador del Sol", 600)
            else -> Pair("¡Nivel Máximo!", 600)
        }
    }

    val siguienteRango = datosSiguienteNivel.first
    val xpRequerida = datosSiguienteNivel.second

    val xpFaltante = xpRequerida - totalXP

    // 1. ENVOLVEMOS TODO EN UN BOX PADRE
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        // 2. TU DISEÑO ORIGINAL SE MANTIENE EXACTAMENTE IGUAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                // Aumentamos el padding superior para que la tarjeta no choque con el botón flotante
                .padding(start = 16.dp, end = 16.dp, top = 80.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Tarjeta de Membresía (Diseño Crepúsculo Larense)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFBDC3C7), // Plata claro
                                    Color(0xFF95A5A6), // Gris titanio
                                    Color(0xFF2C3E50)  // Remate en tu azul corporativo
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Lara Experience Card",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.TopStart)
                    )

                    Text(
                        text = serialFormateado,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(text = userName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "XP: $totalXP", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        Text(text = "Rango: $userRank", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 👑 NUEVO: TEXTO MOTIVACIONAL DE PROGRESO DEBAJO DE LA TARJETA
            if (totalXP < 600) {
                Text(
                    text = "Te faltan $xpFaltante XP para ascender a $siguienteRango",
                    color = Color(0xFF2C3E50),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Text(
                    text = "¡Felicidades! Eres un Leyenda en Lara 365",
                    color = Color(0xFFE53935),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenedor del QR
            Text(
                text = "Pase de Usuario Único",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.size(220.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Código QR del Turista",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(text = "Error al generar QR", color = Color.Red, fontSize = 12.sp)
                    }
                }
            }
        }

        // 3. NUEVO: BOTÓN FLOTANTE DE RETORNO (Capa Superior)
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding() // Respeta la barra de notificaciones de forma limpia
                .padding(start = 16.dp, top = 16.dp)
                .size(40.dp)
                .background(
                    color = Color(0xFF2C3E50).copy(alpha = 0.15f), // Azul oscuro muy sutil traslúcido
                    shape = RoundedCornerShape(50)
                )
                .align(Alignment.TopStart) // Lo anclamos arriba a la izquierda
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                tint = Color(0xFF2C3E50) // El mismo azul corporativo para que resalte en el fondo gris
            )
        }
    }
}

// Se mantiene tu función original generarCodigoQR...
fun generarCodigoQR(texto: String, size: Int = 500): Bitmap? {
    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(texto, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val colorNegro = Color.Black.toArgb()
        val colorBlanco = Color.White.toArgb()
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) colorNegro else colorBlanco)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}