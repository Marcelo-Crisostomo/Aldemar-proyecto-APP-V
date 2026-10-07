package cl.duoc.aquasample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aquasample.data.repository.MuestraRepository
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Muestra
import cl.duoc.aquasample.util.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleUiState(
    val cargando: Boolean = true,
    val muestra: Muestra? = null,
    val errorComentario: String? = null,
    val mensaje: String? = null,
    val eliminada: Boolean = false
)

/** Detalle de una muestra + acciones de revisión del supervisor. */
class DetalleViewModel(
    private val repository: MuestraRepository,
    idMuestra: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observar(idMuestra).collect { m ->
                _uiState.update { it.copy(cargando = false, muestra = m) }
            }
        }
    }

    fun validar() = revisar(EstadoMuestra.VALIDADO, "", "Muestra validada")

    /** Devuelve true si el comentario es válido y la muestra quedó observada. */
    fun observar(comentario: String): Boolean {
        val error = Validador.comentarioRevision(comentario)
        _uiState.update { it.copy(errorComentario = error) }
        if (error != null) return false
        revisar(EstadoMuestra.OBSERVADO, comentario.trim(), "Muestra observada: el operador debe corregirla")
        return true
    }

    private fun revisar(estado: EstadoMuestra, comentario: String, mensaje: String) {
        val m = _uiState.value.muestra ?: return
        viewModelScope.launch {
            repository.cambiarEstado(m, estado, comentario)
            _uiState.update { it.copy(mensaje = mensaje) }
        }
    }

    fun eliminar() {
        val m = _uiState.value.muestra ?: return
        viewModelScope.launch {
            repository.eliminar(m)
            _uiState.update { it.copy(eliminada = true) }
        }
    }

    fun limpiarErrorComentario() = _uiState.update { it.copy(errorComentario = null) }
    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}
