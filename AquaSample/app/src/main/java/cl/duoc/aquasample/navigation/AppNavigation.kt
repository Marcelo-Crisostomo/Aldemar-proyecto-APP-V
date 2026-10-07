package cl.duoc.aquasample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.ui.screens.DetalleScreen
import cl.duoc.aquasample.ui.screens.LoginScreen
import cl.duoc.aquasample.ui.screens.MuestraFormScreen
import cl.duoc.aquasample.ui.screens.MuestrasScreen
import cl.duoc.aquasample.ui.screens.ResumenScreen
import cl.duoc.aquasample.viewmodel.AuthViewModel

/** Rutas de la app. form/0 significa "nueva muestra". */
object Rutas {
    const val LOGIN = "login"
    const val MUESTRAS = "muestras"
    const val FORM = "form/{id}"
    const val DETALLE = "detalle/{id}"
    const val RESUMEN = "resumen"
    fun form(id: Long) = "form/$id"
    fun detalle(id: Long) = "detalle/$id"
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    // AuthViewModel vive a nivel de Activity: guarda la sesión para todas las pantallas.
    val authViewModel: AuthViewModel = viewModel()
    val login by authViewModel.uiState.collectAsStateWithLifecycle()
    val usuario = login.usuario

    // Cuando el login es exitoso, vamos al listado y sacamos el login de la pila.
    LaunchedEffect(usuario) {
        if (usuario != null && nav.currentDestination?.route == Rutas.LOGIN) {
            nav.navigate(Rutas.MUESTRAS) { popUpTo(Rutas.LOGIN) { inclusive = true } }
        }
    }

    val idArg = listOf(navArgument("id") { type = NavType.LongType })

    NavHost(navController = nav, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) { LoginScreen(authViewModel) }

        composable(Rutas.MUESTRAS) {
            ConSesion(usuario, nav) { u ->
                MuestrasScreen(
                    usuario = u,
                    onNueva = { nav.navigate(Rutas.form(0)) },
                    onAbrir = { id -> nav.navigate(Rutas.detalle(id)) },
                    onResumen = { nav.navigate(Rutas.RESUMEN) },
                    onSalir = {
                        authViewModel.cerrarSesion()
                        nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } }
                    }
                )
            }
        }

        composable(Rutas.FORM, arguments = idArg) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            ConSesion(usuario, nav) { u -> MuestraFormScreen(u, id, onVolver = { nav.popBackStack() }) }
        }

        composable(Rutas.DETALLE, arguments = idArg) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            ConSesion(usuario, nav) { u ->
                DetalleScreen(u, id, onEditar = { nav.navigate(Rutas.form(it)) }, onVolver = { nav.popBackStack() })
            }
        }

        composable(Rutas.RESUMEN) {
            ConSesion(usuario, nav) { u -> ResumenScreen(u, onVolver = { nav.popBackStack() }) }
        }
    }
}

/** Muestra el contenido solo si hay sesión; si no, vuelve al login. */
@Composable
private fun ConSesion(usuario: Usuario?, nav: NavHostController, contenido: @Composable (Usuario) -> Unit) {
    if (usuario != null) {
        contenido(usuario)
    } else {
        LaunchedEffect(Unit) { nav.navigate(Rutas.LOGIN) { popUpTo(nav.graph.id) { inclusive = true } } }
    }
}
