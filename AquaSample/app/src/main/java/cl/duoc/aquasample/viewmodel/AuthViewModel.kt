package cl.duoc.aquasample.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.aquasample.model.DatosDemo
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.util.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginUiState(
    val codigo: String = "",
    val clave: String = "",
    val errorCodigo: String? = null,
    val errorClave: String? = null,
    val error: String? = null,
    val usuario: Usuario? = null      // != null significa sesión iniciada
)

/** Maneja el login y guarda el usuario de la sesión para toda la app. */
class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onCodigoChange(valor: String) =
        _uiState.update { it.copy(codigo = valor.uppercase().trim(), errorCodigo = null, error = null) }

    fun onClaveChange(valor: String) =
        _uiState.update { it.copy(clave = valor, errorClave = null, error = null) }

    fun ingresar() {
        val s = _uiState.value
        val errorCodigo = Validador.requerido(s.codigo, "Ingresa tu código de usuario")
        val errorClave = Validador.requerido(s.clave, "Ingresa tu clave")
        if (errorCodigo != null || errorClave != null) {
            _uiState.update { it.copy(errorCodigo = errorCodigo, errorClave = errorClave) }
            return
        }
        val usuario = DatosDemo.usuarios.find { it.codigo == s.codigo && it.clave == s.clave }
        _uiState.update {
            if (usuario == null) it.copy(error = "Código o clave incorrectos")
            else it.copy(usuario = usuario, clave = "")
        }
    }

    fun cerrarSesion() = _uiState.update { LoginUiState() }
}
