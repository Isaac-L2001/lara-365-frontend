package com.lara365.experience.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lara365.experience.models.Establishment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    comercio: Establishment,
    onBackClick: () -> Unit,
    onScanClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FA) // Fondo gris texturizado premium
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(scrollState) // Scrolling fluido que mencionamos
        ) {
            // 1. IMAGEN DE CABECERA (Inmersiva)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = comercio.image_url,
                    contentDescription = "Portada de la ruta",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 32.dp,
                                bottomEnd = 32.dp
                            )
                        ), // Curvatura elegante en la base
                    contentScale = ContentScale.Crop
                )

                // Botón flotante para retroceder integrado en la imagen
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }
            }

            // 2. CONTENIDO DE INFORMACIÓN
            Column(modifier = Modifier.padding(24.dp)) {
                // Fila de Categoría y Descuento
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comercio.categoria.uppercase(),
                        color = Color(0xFF2C3E50),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = Color(0xFF4CAF50).copy(0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${comercio.porcentaje_descuento.toInt()}% OFF",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre del sitio
                Text(
                    text = comercio.nombre_comercio,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )

                Divider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    color = Color.LightGray.copy(0.5f)
                )

                // Sección Ubicación (Con icono como en tu captura)
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    title = "Ubicación",
                    subtitle = comercio.ubicacion
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sección Recompensa (Para la gamificación)
                InfoRow(
                    icon = Icons.Default.Star,
                    title = "Recompensa de Visita",
                    subtitle = "+150 XP de Explorador"
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 3. EL GRAN BOTÓN ROJO DE ESCANEO
                Button(
                    onClick = onScanClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ESCANEAR QR DEL SITIO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Componente reutilizable para las filas de info (Ubicación, XP, etc)
@Composable
fun InfoRow(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color(0xFF2C3E50), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2C3E50))
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
        }
    }
}