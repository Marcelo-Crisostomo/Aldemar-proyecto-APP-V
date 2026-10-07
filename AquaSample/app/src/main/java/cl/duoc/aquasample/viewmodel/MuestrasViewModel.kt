package cl.duoc.aquasample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aquasample.data.repository.MuestraRepository
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Muestra
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.util.Reglas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MuestrasUiState(
    val cargando: Boolean = true,
    val muestras: List<Muestra> = emptyList(),
    val texto: String = "",
    val estado: EstadoMuestra? = null      // null = todos los estados
)

/** Historial de muestras con búsqueda y filtro por estado. */
class MuestrasViewModel(
    repository: MuestraRepository,
    private val usuario: Usuario
) : ViewModel() {

    private val _uiState = MutableStateFlow(MuestrasUiState())
    val uiState = _uiState.asStateFlow()

    private var visibles: List<Muestra> = emptyList()

    init {
        // Room emite una nueva lista cada vez que cambia la tabla.
        viewModelScope.launch {
            repository.observarTodas().collect { todas ->
                visibles = todas.filter { Reglas.esVisible(usuario, it) }
                aplicarFiltros()
            }
        }
    }

    fun onTextoChange(texto: String) {
        _uiState.update { it.copy(texto = texto) }
        aplicarFiltros()
    }

    fun onEstadoChange(estado: EstadoMuestra?) {
        _uiState.update { it.copy(estado = estado) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() = _uiState.update {
        it.copy(cargando = false, muestras = Reglas.filtrar(visibles, it.texto, it.estado))
    }
}
