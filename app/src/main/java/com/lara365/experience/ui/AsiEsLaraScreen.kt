package com.lara365.experience.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lara365.experience.models.EbookModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsiEsLaraScreen(
    isLoading: Boolean,
    modules: List<EbookModule>,
    onModuleClick: (EbookModule) -> Unit
) {
    val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Fondo gris claro limpio
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2C3E50)
                )
            } else {
                // Separamos la lista en memoria usando las categorías que sembramos
                val principales = modules.filter { it.category == "principal" }
                val municipales = modules.filter { it.category == "municipio" }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SECCIÓN 1: Módulos Principales
                    if (principales.isNotEmpty()) {
                        item {
                            Text(
                                text = "Módulos Principales",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(principales) { modulo ->
                            EbookCardItem(modulo = modulo, onModuleClick = { onModuleClick(modulo) })
                        }
                    }

                    // SECCIÓN 2: Módulos Municipales (Municipios 1 al 9)
                    if (municipales.isNotEmpty()) {
                        item {
                            Text(
                                text = "Capacitación por Municipios",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333),
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(municipales) { modulo ->
                            EbookCardItem(modulo = modulo, onModuleClick = { onModuleClick(modulo) })
                        }
                    }
                }
            }
        }
    }


@Composable
fun EbookCardItem(
    modulo: EbookModule,
    onModuleClick: (EbookModule) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onModuleClick(modulo) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono representativo del libro digital
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = "Ebook Icon",
                tint = if (modulo.is_completed) Color(0xFF4CAF50) else Color(0xFF757575),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Textos informativos del módulo
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = modulo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                modulo.description?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color(0xFF616161),
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Etiqueta de la recompensa de XP
                Text(
                    text = "+${modulo.xp_reward} XP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C3E50)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Indicador de estado: si ya está completado, pinta un check verde interactivo
            if (modulo.is_completed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completado",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}