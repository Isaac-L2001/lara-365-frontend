package com.lara365.experience.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lara365.experience.models.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(
    isLoading: Boolean,
    listaRutas: List<Route>,
    onRouteClick: (Route) -> Unit
) {
    // 1. ESTADO PARA EL FILTRO (null significa "Todos los municipios")
    var municipioSeleccionado by remember { mutableStateOf<String?>(null) }

    // Lista fija de los 9 municipios guaros en el formato del enum de Laravel
    val municipiosLara = listOf(
        Pair("todos", "Todos"),
        Pair("andres_eloy_blanco", "Andrés Eloy Blanco"),
        Pair("crespo", "Crespo"),
        Pair("iribarren", "Iribarren"),
        Pair("jimenez", "Jiménez"),
        Pair("moran", "Morán"),
        Pair("palavecino", "Palavecino"),
        Pair("simon_planas", "Simón Planas"),
        Pair("torres", "Torres"),
        Pair("urdaneta", "Urdaneta")
    )

    // 2. LÓGICA DE FILTRADO EN TIEMPO REAL (Reactiva)
    val rutasFiltradas = remember(municipioSeleccionado, listaRutas) {
        if (municipioSeleccionado == null || municipioSeleccionado == "todos") {
            listaRutas
        } else {
            listaRutas.filter { it.municipio == municipioSeleccionado }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF2C3E50))
        } else {
            // Estructura vertical fija: Arriba los filtros, abajo las tarjetas
            Column(modifier = Modifier.fillMaxSize()) {

                // 3. BARRA HORIZONTAL DE FILTROS (CHIPS)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono estético de filtro de primero
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = Color(0xFF2C3E50),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    municipiosLara.forEach { (slug, nombreVisible) ->
                        val esElSeleccionado = (municipioSeleccionado == slug) || (municipioSeleccionado == null && slug == "todos")

                        FilterChip(
                            selected = esElSeleccionado,
                            onClick = {
                                municipioSeleccionado = if (slug == "todos") null else slug
                            },
                            label = {
                                Text(
                                    text = nombreVisible,
                                    fontWeight = if (esElSeleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2C3E50), // Azul Oscuro Operativo
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // 4. EL LISTADO REACCIONA A LA LISTA FILTRADA
                if (rutasFiltradas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay rutas para este municipio",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(rutasFiltradas) { ruta ->
                            RouteCardItem(ruta = ruta, onRouteClick = onRouteClick)
                        }
                    }
                }
            }
        }
    }
}

// --- El resto del código (RouteCardItem, obtenerFormatoExperiencia, formatearMunicipio) se queda EXACTAMENTE IGUAL ---
@Composable
fun RouteCardItem(
    ruta: Route,
    onRouteClick: (Route) -> Unit
) {
    Card(
        onClick = { onRouteClick(ruta) },
        modifier = Modifier.fillMaxWidth().height(240.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ruta.imageUrl,
                contentDescription = "Imagen de ${ruta.nombreRuta}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 100f
                        )
                    )
            )

            val (textoExp, colorBadge) = obtenerFormatoExperiencia(ruta.tipoExperiencia)
            Surface(
                color = colorBadge,
                shape = RoundedCornerShape(bottomEnd = 14.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = textoExp,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = formatearMunicipio(ruta.municipio).uppercase(),
                    color = Color(0xFFFFB300),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Nombre de la Ruta
                Text(
                    text = ruta.nombreRuta,
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Schedule,
                            contentDescription = "Duración",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ruta.duracionEstimada,
                            color = Color.LightGray,
                            fontSize = 13.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = if (ruta.costo == 0.0) "Gratis" else "$${String.format("%.2f", ruta.costo)}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun obtenerFormatoExperiencia(tipo: String): Pair<String, Color> {
    return when (tipo) {
        "pildora_cultural" -> Pair("Píldora Cultural (2-4 h)", Color(0xFF2C3E50))
        "jornada_descubrimiento" -> Pair("Jornada de Descubrimiento (Full Day)", Color(0xFF2C3E50))
        else -> Pair("Experiencia Turística", Color.Gray)
    }
}

private fun formatearMunicipio(slug: String): String {
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