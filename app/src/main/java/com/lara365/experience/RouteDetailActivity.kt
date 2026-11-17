package com.lara365.experience

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.lara365.experience.models.Route
import com.lara365.experience.ui.RouteDetailScreen

class RouteDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperamos de forma segura el objeto Serializable que viaja en el Intent
        val ruta = intent.getSerializableExtra("EXTRA_ROUTE") as? Route

        if (ruta != null) {
            setContent {
                RouteDetailScreen(
                    ruta = ruta,
                    onBackClick = { finish() } // Para que la flecha de atrás cierre la actividad limpiamente
                )
            }
        } else {
            // Respaldo rápido por si ocurre un fallo catastrófico al desempaquetar
            finish()
        }
    }
}