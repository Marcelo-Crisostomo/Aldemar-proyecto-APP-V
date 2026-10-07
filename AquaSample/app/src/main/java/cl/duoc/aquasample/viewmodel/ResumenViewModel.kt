package cl.duoc.aquasample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aquasample.data.repository.MuestraRepository
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.util.Reglas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estados posibles de la sincronización remota. */
sealed interface SyncEstado {
    data object Inactivo : SyncEstado
    data object Cargando : SyncEstado
    data class Exito(val enviadas: Int) : SyncEstado
    data class Error(val mensaje: String) : SyncEstado
}

data class ResumenUiState(
    val total: Int = 0,
    val porEstado: Map<EstadoMuestra, Int> = emptyMap(),
    val totalIndividuos: Int = 0,
    val densidadPromedio: Double = 0.0,
    val porSincronizar: Int = 0,
    val sync: SyncEstado = SyncEstado.Inactivo
)

class ResumenViewModel(
    private val repository: MuestraRepository,
    usuario: Usuario
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observarTodas().collect { todas ->
                val lista = todas.filter { Reglas.esVisible(usuario, it) }
                _uiState.update {
                    it.copy(
                        total = lista.size,
                        porEstado = lista.groupingBy { m -> m.estado }.eachCount(),
                        totalIndividuos = lista.sumOf { m -> m.conteo },
                        densidadPromedio = lista.map { m -> Reglas.densidadPorMetro(m.conteo, m.tramoCm) }
                            .average().takeIf { d -> !d.isNaN() } ?: 0.0,
                        porSincronizar = lista.count { m -> m.estado == EstadoMuestra.VALIDADO && !m.sincronizada }
                    )
                }
            }
        }
    }

    fun sincronizar() {
        _uiState.update { it.copy(sync = SyncEstado.Cargando) }
        viewModelScope.launch {
            val nuevo = try {
                SyncEstado.Exito(repository.sincronizarValidadas())
            } catch (e: Exception) {
                SyncEstado.Error("No se pudo sincronizar. Revisa tu conexión e intenta nuevamente.")
            }
            _uiState.update { it.copy(sync = nuevo) }
        }
    }
}
