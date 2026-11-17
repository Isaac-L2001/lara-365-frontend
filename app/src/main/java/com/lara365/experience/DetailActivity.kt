package com.lara365.experience

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.lara365.experience.models.Establishment
import com.lara365.experience.ui.DetailScreen

// DetailActivity.kt
class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperamos el objeto enviado tal cual como lo tenías antes
        val establishment = intent.getSerializableExtra("EXTRA_ESTABLISHMENT") as? Establishment


        if (establishment != null) {
            setContent {
                DetailScreen(
                    comercio = establishment,
                    onBackClick = { finish() }, // Cierra esta actividad y vuelve al MainActivity
                    onScanClick = {
                        // Aquí conectarás tu lógica de escaneo que ya tenías funcional
                        Log.d("SCAN", "Iniciando escáner para el comercio: ${establishment.id}")
                        abrirEscanerQR(establishment.id)
                    }
                )
            }
        } else {
            // Respaldo rápido por si ocurre un fallo catastrófico al desempaquetar
            finish()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun abrirEscanerQR(establishmentId: Int) {
        val intent = Intent(this, ScannerActivity::class.java).apply {
            putExtra("EXPECTED_ID", establishmentId)
        }
        startActivity(intent)
    }
}