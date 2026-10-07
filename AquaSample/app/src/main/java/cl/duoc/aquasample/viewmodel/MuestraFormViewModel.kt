package cl.duoc.aquasample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.aquasample.data.repository.MuestraRepository
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Muestra
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.util.Reglas
import cl.duoc.aquasample.util.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormErrores(
    val centro: String? = null,
    val tren: String? = null,
    val linea: String? = null,
    val tramo: String? = null,
    val conteo: String? = null,
    val calibre: String? = null,
    val observaciones: String? = null,
    val foto: String? = null
) {
    fun hayErrores() =
        listOf(centro, tren, linea, tramo, conteo, calibre, observaciones, foto).any { it != null }
}

/** Los campos numéricos se guardan como texto para poder validar lo que escribe el usuario. */
data class FormUiState(
    val id: Long = 0,
    val fechaHora: Long = System.currentTimeMillis(),
    val operador: String = "",
    val centro: String = "",
    val tren: String = "",
    val linea: String = "",
    val tramo: String = "20",
    val conteo: String = "0",
    val calibre: String = "",
    val observaciones: String = "",
    val fotoPath: String? = null,
    val estado: EstadoMuestra = EstadoMuestra.BORRADOR,
    val comentarioSupervisor: String = "",
    val errores: FormErrores = FormErrores(),
    val guardado: Boolean = false,
    val mensaje: String? = null
)

class MuestraFormViewModel(
    private val repository: MuestraRepository,
    usuario: Usuario,
    idMuestra: Long          // 0 = nueva muestra
) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState(operador = usuario.codigo))
    val uiState = _uiState.asStateFlow()

    init {
        if (idMuestra != 0L) viewModelScope.launch {
            repository.obtener(idMuestra)?.let { m ->
                _uiState.value = FormUiState(
                    id = m.id, fechaHora = m.fechaHora, operador = m.operador,
                    centro = m.centro, tren = m.tren, linea = m.linea,
                    tramo = m.tramoCm.toString(), conteo = m.conteo.toString(),
                    calibre = m.calibreMm?.toString() ?: "", observaciones = m.observaciones,
                    fotoPath = m.fotoPath, estado = m.estado,
                    comentarioSupervisor = m.comentarioSupervisor
                )
            }
        }
    }

    /** Actualiza cualquier campo: viewModel.actualizar { copy(centro = "X") } */
    fun actualizar(cambio: FormUiState.() -> FormUiState) = _uiState.update { it.cambio() }

    fun sumarConteo(delta: Int) = _uiState.update {
        val actual = it.conteo.toIntOrNull() ?: 0
        it.copy(conteo = (actual + delta).coerceIn(0, Validador.CONTEO_MAX).toString())
    }

    fun fotoCapturada(path: String) = _uiState.update {
        it.copy(fotoPath = path, errores = it.errores.copy(foto = null))
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    /** enviar = false guarda como borrador; enviar = true exige todos los datos y la foto. */
    fun guardar(enviar: Boolean) {
        val s = _uiState.value
        val errores = FormErrores(
            centro = Validador.requerido(s.centro, "Selecciona el centro"),
            tren = Validador.requerido(s.tren, "Selecciona el tren"),
            linea = Validador.requerido(s.linea, "Selecciona la línea"),
            tramo = Validador.tramo(s.tramo),
            conteo = Validador.conteo(s.conteo, obligatorio = enviar),
            calibre = Validador.calibre(s.calibre),
            observaciones = Validador.observaciones(s.observaciones),
            foto = Validador.foto(s.fotoPath, obligatoria = enviar)
        )
        if (errores.hayErrores()) {
            _uiState.update { it.copy(errores = errores, mensaje = "Revisa los campos marcados") }
            return
        }
        val muestra = Muestra(
            id = s.id, fechaHora = s.fechaHora, centro = s.centro, tren = s.tren,
            linea = s.linea, tramoCm = s.tramo.trim().toInt(), operador = s.operador,
            conteo = s.conteo.toIntOrNull() ?: 0,
            calibreMm = s.calibre.trim().replace(',', '.').toDoubleOrNull(),
            fotoPath = s.fotoPath, observaciones = s.observaciones.trim(),
            estado = if (enviar) Reglas.estadoAlEnviar(s.estado) else s.estado,
            comentarioSupervisor = s.comentarioSupervisor
        )
        viewModelScope.launch {
            repository.guardar(muestra)
            _uiState.update { it.copy(errores = FormErrores(), guardado = true) }
        }
    }
}
