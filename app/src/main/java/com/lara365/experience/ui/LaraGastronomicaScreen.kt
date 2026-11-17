package com.lara365.experience.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.lara365.experience.models.GastronomiaResponse

@Composable
fun LaraGastronomicaScreen(listaElementos: List<GastronomiaResponse>) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Patrimonio", "Rutas", "Capacitación")

    // Filtramos la lista localmente según la pestaña seleccionada
    val tipoFiltro = when (tabIndex) {
        0 -> "recetario"
        1 -> "ruta"
        else -> "capacitacion"
    }
    val listaFiltrada = listaElementos.filter { it.tipo == tipoFiltro }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontWeight = FontWeight.Bold) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    selectedContentColor = Color(0xFF2C3E50),
                    unselectedContentColor = Color.Gray
                )
            }
        }

        if (listaFiltrada.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay elementos registrados en este pilar.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaFiltrada) { elemento ->
                    GastronomiaCard(elemento) // Tu misma tarjeta con los puntos XP
                }
            }
        }
    }
}

@Composable
fun GastronomiaCard(elemento: GastronomiaResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = elemento.imagen_url,
            contentDescription = elemento.titulo,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = elemento.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50),
                    modifier = Modifier.weight(1f)
                )
                // Insignia de Gamificación (Puntos XP)
                SuggestionChip(
                    onClick = { },
                    label = { Text("+${elemento.xp_puntos} XP", color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF27AE60))
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            elemento.ubicacion_o_autor?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = elemento.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
        }
    }
}