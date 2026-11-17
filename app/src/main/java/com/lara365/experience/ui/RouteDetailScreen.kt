package com.lara365.experience.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lara365.experience.models.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    ruta: Route,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF8F9FA) // Fondo gris texturizado premium
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState) // Scroll suave para pantallas pequeñas
        ) {
            // 1. CABECERA MULTIMEDIA INMERSIVA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = ruta.imageUrl,
                    contentDescription = "Portada de la ruta",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)), // Curvatura elegante en la base
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

            // 2. CUERPO DE DETALLES
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Municipio y Tipo de Experiencia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatearMunicipioOriginal(ruta.municipio).uppercase(),
                        color = Color(0xFFFFB300), // Oro larense
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = if (ruta.tipoExperiencia == "pildora_cultural") "Píldora Cultural" else "Full Day",
                        color = Color(0xFF2C3E50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título de la Ruta
                Text(
                    text = ruta.nombreRuta,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. TARJETA HORIZONTAL DE METADATOS (Tiempo y Costo)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Sección Duración
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = "Reloj", tint = Color(0xFF2C3E50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Duración", fontSize = 11.sp, color = Color.Gray)
                                Text(ruta.duracionEstimada, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Separador vertical estético
                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.LightGray))

                        // Sección Costo
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = "Dinero", tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Inversión", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = if (ruta.costo == 0.0) "Gratis" else "$${String.format("%.2f", ruta.costo)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. BLOQUE: PUNTO DE SALIDA
                InfoSectionBlock(
                    title = "Punto de Encuentro y Salida",
                    icon = Icons.Default.LocationOn,
                    iconColor = Color(0xFF2C3E50),
                    content = ruta.puntoSalida
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 5. BLOQUE: ITINERARIO COMPLETO
                InfoSectionBlock(
                    title = "Itinerario de la Experiencia",
                    icon = Icons.Default.Info,
                    iconColor = Color(0xFF2C3E50),
                    content = ruta.itinerario
                )
            }
        }
    }
}

// Composable modular interno para los bloques de texto informativos
@Composable
fun InfoSectionBlock(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    content: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C3E50)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            color = Color(0xFF4A4A4A),
            lineHeight = 22.sp
        )
    }
}

private fun formatearMunicipioOriginal(slug: String): String {
    return when (slug) {
        "andres_eloy_blanco" -> "Andrés Eloy Blanco"
        "simon_planas" -> "Simón Planas"
        "palavecino" -> "Palavecino"
        "iribarren" -> "Iribarren"
        "jimenez" -> "Jiménez"
        "moran" -> "Morán"
        "crespo" -> "Crespo"
        "torres" -> "Torres"
        "urdaneta" -> "Urdaneta"
        else -> slug.replaceFirstChar { it.uppercase() }
    }
}