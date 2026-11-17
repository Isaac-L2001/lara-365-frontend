package com.lara365.experience.ui

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@ExperimentalGetImage
@Composable
fun ScannerScreen(
    targetEstablishmentId: Int,
    onQrScanned: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var isScanningActive by remember { mutableStateOf(true) }
    var isFlashOn by remember { mutableStateOf(false) }

    // 1. OJO: Cambiamos el estado de Compose por una referencia tradicional de Kotlin
    // Usamos una celda mutable ordinaria para que los listeners nativos la lean sin activar las alarmas de Compose
    val cameraReference = remember { mutableStateOf<Camera?>(null) }

    val frameColor = Color.White

    Box(modifier = Modifier.fillMaxSize()) {

        // CÁMARA DE FONDO
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // 2. CONFIGURAMOS EL ZOOM USANDO LA REFERENCIA TRADICIONAL
                    val scaleGestureDetector = ScaleGestureDetector(ctx,
                        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                            override fun onScale(detector: ScaleGestureDetector): Boolean {
                                // Accedemos al valor interno sin invocar código Composable
                                cameraReference.value?.let { camera ->
                                    val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 1f
                                    camera.cameraControl.setZoomRatio(currentZoomRatio * detector.scaleFactor)
                                }
                                return true
                            }
                        })

                    // 3. CONFIGURAMOS EL ENFOQUE POR TOQUE
                    setOnTouchListener { view, event ->
                        scaleGestureDetector.onTouchEvent(event)

                        if (event.action == MotionEvent.ACTION_UP) {
                            view.performClick()

                            cameraReference.value?.let { camera ->
                                val factory = meteringPointFactory
                                val point = factory.createPoint(event.x, event.y)
                                val action = FocusMeteringAction.Builder(point).build()
                                camera.cameraControl.startFocusAndMetering(action)
                            }
                        }
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val barcodeScanner = BarcodeScanning.getClient()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && isScanningActive) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        val rawValue = barcode.rawValue
                                        if (rawValue != null) {
                                            isScanningActive = false
                                            onQrScanned(rawValue)
                                            break
                                        }
                                    }
                                }
                                .addOnCompleteListener { imageProxy.close() }
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        // 4. Guardamos la cámara en nuestra referencia ordinaria (.value)
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                        cameraReference.value = camera
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // MARCO ESTILO GOOGLE LENS (Mantenemos tu diseño limpio)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val boxSize = 240.dp.toPx()
            val strokeWidth = 3.dp.toPx()
            val cornerLength = 25.dp.toPx()

            val left = (width - boxSize) / 2
            val top = (height - boxSize) / 2
            val right = left + boxSize
            val bottom = top + boxSize

            drawHorizontalLine(left, left + cornerLength, top, strokeWidth, frameColor)
            drawVerticalLine(left, top, top + cornerLength, strokeWidth, frameColor)
            drawHorizontalLine(right - cornerLength, right, top, strokeWidth, frameColor)
            drawVerticalLine(right, top, top + cornerLength, strokeWidth, frameColor)
            drawHorizontalLine(left, left + cornerLength, bottom, strokeWidth, frameColor)
            drawVerticalLine(left, bottom - cornerLength, bottom, strokeWidth, frameColor)
            drawHorizontalLine(right - cornerLength, right, bottom, strokeWidth, frameColor)
            drawVerticalLine(right, bottom - cornerLength, bottom, strokeWidth, frameColor)
        }

        // BOTÓN DE LINTERNA (Esquina superior izquierda)
        IconButton(
            onClick = {
                cameraReference.value?.let { camera ->
                    if (camera.cameraInfo.hasFlashUnit()) {
                        isFlashOn = !isFlashOn
                        camera.cameraControl.enableTorch(isFlashOn)
                    }
                }
            },
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Linterna",
                tint = Color.White
            )
        }
    }
}

// Funciones de extensión para limpiar el dibujo en el Canvas
fun DrawScope.drawHorizontalLine(xStart: Float, xEnd: Float, y: Float, strokeWidth: Float, color: Color) {
    drawLine(color = color, start = Offset(xStart, y), end = Offset(xEnd, y), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}
fun DrawScope.drawVerticalLine(x: Float, yStart: Float, yEnd: Float, strokeWidth: Float, color: Color) {
    drawLine(color = color, start = Offset(x, yStart), end = Offset(x, yEnd), strokeWidth = strokeWidth, cap = StrokeCap.Round)
}