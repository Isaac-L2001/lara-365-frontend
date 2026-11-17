package com.lara365.experience.ui

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lara365.experience.models.EbookModule
import com.lara365.experience.network.EbookRepository
import kotlinx.coroutines.launch

class AsiEsLaraViewModel : ViewModel() {

    // Lista mutable de módulos expuesta como lectura para Compose
    private val _modulesState = mutableStateOf<List<EbookModule>>(emptyList())
    val modulesState: State<List<EbookModule>> = _modulesState

    // Estado de carga de la pantalla
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // Mensaje de error o éxito para mostrar notificaciones (Toast / Snackbar)
    private val _mensaje = mutableStateOf<String?>(null)
    val mensaje: State<String?> = _mensaje

    // Función para limpiar el mensaje una vez mostrado
    fun limpiarMensaje() { _mensaje.value = null }

    // Cargar la biblioteca desde el backend de Laravel
    fun cargarEbooks(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val resultado = EbookRepository.obtenerBiblioteca(context) // Llama al método del repositorio
            if (resultado != null && resultado.success) {
                _modulesState.value = resultado.modules
            } else {
                _mensaje.value = "No se pudieron cargar los módulos formativos."
            }
            _isLoading.value = false
        }
    }

    // Registrar la lectura del módulo y actualizar el estado visual en caliente
    fun leerModulo(context: Context, moduloId: Int) {
        viewModelScope.launch {
            val resultado = EbookRepository.completarModulo(context, moduloId)
            if (resultado != null && resultado.success) {
                _mensaje.value = resultado.message

                // 🔥 TRUCO MÁGICO EN CALIENTE: Actualizamos la lista local en memoria de Compose
                // para que el check verde de la tarjeta se pinte de inmediato sin recargar toda la pantalla desde el servidor.
                _modulesState.value = _modulesState.value.map { modulo ->
                    if (modulo.id == moduloId) {
                        modulo.copy(is_completed = true)
                    } else {
                        modulo
                    }
                }
            } else {
                _mensaje.value = "Error al registrar la lectura del módulo."
            }
        }
    }
}