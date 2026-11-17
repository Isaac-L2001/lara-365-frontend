package com.lara365.experience.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lara365.experience.DetailActivity
import com.lara365.experience.LaraExperienceCardActivity
import com.lara365.experience.models.Establishment
import com.lara365.experience.R
import com.lara365.experience.models.Route
import com.lara365.experience.models.EbookModule
import com.lara365.experience.models.GastronomiaResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithMenu(
    isLoadingComercios: Boolean,
    listaEstablecimientos: List<Establishment>,
    isLoadingRutas: Boolean,
    listaRutas: List<Route>,
    onRouteSectionSelected: () -> Unit, // Callback para indicarle al Activity que busque las rutas
    onRouteClick: (Route) -> Unit, // <-- NUEVO: Callback que subirá al Activity
    isLoadingEbooks: Boolean,
    listaEbooks: List<EbookModule>,
    onEbookSectionSelected: () -> Unit,
    onEbookClick: (EbookModule) -> Unit,
    isLoadingGastronomia: Boolean,
    listaGastronomia: List<GastronomiaResponse>,
    onGastronomiaSectionSelected: () -> Unit,
    userXpActual: Int,
    onLogoutSelected: () -> Unit
) {
    var pantallaActual by remember { mutableStateOf("comercios") } // "comercios" o "rutas"
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val contexto = LocalContext.current

    val sharedPref = contexto.getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
    val nombreUsuario = sharedPref.getString("user_name", "Usuario Lara") ?: "Usuario Lara"
    val rangoUsuario = sharedPref.getString("user_rank", "Baquiano Novato") ?: "Baquiano Novato"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(280.dp)
            ) {
                // --- CABECERA DEL MENÚ ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2C3E50)) // Tu azul corporativo
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .padding(horizontal = 20.dp, vertical = 24.dp), // Reducimos paddings para que sea más compacto
                    verticalAlignment = Alignment.CenterVertically // 🌟 Centra el avatar y los textos horizontalmente entre sí
                ) {
                    // Círculo del Avatar (Izquierda)
                    Box(
                        modifier = Modifier
                            .size(56.dp) // Un pelín más pequeño para cuidar el espacio
                            .background(Color.White.copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp)) // 🌟 Espacio entre el avatar y los textos

                    // Contenedor de Textos (Derecha)
                    Column(
                        modifier = Modifier.weight(1f) // Ocupa todo el espacio disponible a la derecha
                    ) {
                        Text(
                            text = nombreUsuario,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium, // Un tamaño un poco más compacto
                            fontWeight = FontWeight.Bold,
                            maxLines = 1 // Evita que si el nombre es muy largo deforme el menú
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = rangoUsuario,
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 🏛️ MÓDULOS DE CONTENIDO ---
                val modulos = listOf(
                    Triple("comercios", "Lara Shopping", Icons.Default.Storefront),
                    Triple("rutas", "Lara Conecta", Icons.Default.Map),
                    Triple("ebooks", "Así es Lara", Icons.Default.Book),
                    Triple("gastronomia", "Lara Gastronómica", Icons.Default.Restaurant)
                )

                modulos.forEach { (id, titulo, icono) ->
                    val esSeleccionado = pantallaActual == id

                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = titulo,
                                fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Medium,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        selected = esSeleccionado,
                        onClick = {
                            pantallaActual = id

                            when (id) {
                                "rutas" -> onRouteSectionSelected()
                                "ebooks" -> onEbookSectionSelected()
                                "gastronomia" -> onGastronomiaSectionSelected()
                            }

                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(icono, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFF2C3E50).copy(alpha = 0.12f),
                            selectedIconColor = Color(0xFF2C3E50),
                            selectedTextColor = Color(0xFF2C3E50),
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.DarkGray
                        )
                    )
                }

                // --- SECCIÓN DE SALIDA ---
                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyLarge) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutSelected()
                    },
                    // 🌟 CORRECCIÓN: Cambiado 'color' por 'tint' para el ícono de cerrar sesión
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFF2C3E50)) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedTextColor = Color.DarkGray,
                        unselectedContainerColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. El Logo de la Cámara de Turismo
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Encabezado Cámara de Turismo",
                                modifier = Modifier
                                    .height(35.dp),
                                contentScale = ContentScale.Fit
                            )

                            // 2. Un espacio de separación horizontal entre el logo y el texto
                            Spacer(modifier = Modifier.width(12.dp))

                            // 3. El Texto dinámico de la pantalla actual
                            Text(
                                text = when (pantallaActual) {
                                    "comercios" -> "Lara Shopping 365"
                                    "rutas" -> "Lara Conecta 365"
                                    "ebooks" -> "Así es Lara 365"
                                    "gastronomia" -> "Lara Gastronómica 365"
                                    else -> "Lara 365"
                                },
                                color = Color(0xFF2C3E50),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color(0xFF2C3E50))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FA))
                )
            }
        ) { paddingValores ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValores)
            ) {
                // CAMBIO DINÁMICO DE PANTALLAS
                when (pantallaActual) {
                    "comercios" -> {
                        if (isLoadingComercios) {
                            CircularProgressIndicator(color = Color(0xFFE53935), modifier = Modifier.align(Alignment.Center))
                        } else if (listaEstablecimientos.isEmpty()) {
                            Text(text = "No hay comercios disponibles", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        } else {
                            ComerciosList(
                                listaEstablecimientos = listaEstablecimientos,
                                contexto = contexto,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    "rutas" -> {
                        RoutesScreen(
                            isLoading = isLoadingRutas,
                            listaRutas = listaRutas,
                            onRouteClick = onRouteClick
                        )
                    }
                    "ebooks" -> {
                        AsiEsLaraScreen(
                            isLoading = isLoadingEbooks,
                            modules = listaEbooks,
                            onModuleClick = { modulo -> onEbookClick(modulo) }
                        )
                    }
                    "gastronomia" -> {
                        if (isLoadingGastronomia) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF2C3E50))
                            }
                        } else {
                            // Aquí llamamos al Composable de pestañas que creamos, pasándole la lista limpia
                            LaraGastronomicaScreen(listaElementos = listaGastronomia)
                        }
                    }
                }

                // 💳 EL BOTÓN FLOTANTE SÓLO SE MUESTRA EN EL MÓDULO DE COMERCIOS
                if (pantallaActual == "comercios") {
                    ExtendedFloatingActionButton(
                        onClick = {
                            val sharedPref = contexto.getSharedPreferences("LaraExperiencePrefs", Context.MODE_PRIVATE)
                            val nombreReal = sharedPref.getString("user_name", "Usuario Lara 365") ?: "Usuario Lara 365"
                            val serialReal = sharedPref.getString("serial_number", "1234567890123456") ?: "1234567890123456"
                            val rangoReal = sharedPref.getString("user_rank", "Explorador Larense") ?: "Explorador Larense"
                            val idReal = sharedPref.getInt("user_id", 0)

                            val intent = Intent(contexto, LaraExperienceCardActivity::class.java).apply {
                                putExtra("USER_NAME", nombreReal)
                                putExtra("TOTAL_XP", userXpActual)
                                putExtra("SERIAL_NUMBER", serialReal)
                                putExtra("USER_RANK", rangoReal)
                                putExtra("USER_ID", idReal)
                            }
                            contexto.startActivity(intent)
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 20.dp, bottom = 24.dp),
                        containerColor = Color(0xFF2C3E50),
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AccountBox, contentDescription = "Ver Mi Tarjeta")
                            Text(text = "Mi Tarjeta XP", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// Composable auxiliar interno perfectamente sincronizado con el Overscroll del Scaffold
@Composable
fun ComerciosList(
    listaEstablecimientos: List<Establishment>,
    contexto: Context,
    modifier: Modifier = Modifier // <-- Añadimos modificador por defecto
) {
    LazyColumn(
        // Acoplamos el modificador que viene del Box padre para que herede las propiedades elásticas del sistema
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(listaEstablecimientos) { comercio ->
            Card(
                onClick = {
                    val intent = Intent(contexto, DetailActivity::class.java).apply {
                        putExtra("EXTRA_ESTABLISHMENT", comercio)
                    }
                    contexto.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = comercio.image_url,
                        contentDescription = "Imagen de ${comercio.nombre_comercio}",
                        modifier = Modifier
                            .size(85.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = comercio.nombre_comercio,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = comercio.categoria, fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = comercio.ubicacion,
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = "${comercio.porcentaje_descuento.toInt()}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}