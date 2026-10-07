package cl.duoc.aquasample.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.ui.components.DatoFila
import cl.duoc.aquasample.ui.components.EstadoChip
import cl.duoc.aquasample.ui.components.FotoMuestra
import cl.duoc.aquasample.ui.theme.Exito
import cl.duoc.aquasample.util.Reglas
import cl.duoc.aquasample.util.formatearDecimal
import cl.duoc.aquasample.util.formatearFecha
import cl.duoc.aquasample.viewmodel.DetalleViewModel
import cl.duoc.aquasample.viewmodel.Fabricas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    usuario: Usuario,
    idMuestra: Long,
    onEditar: (Long) -> Unit,
    onVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel(factory = Fabricas.detalle(idMuestra))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var dialogoObservar by remember { mutableStateOf(false) }
    var dialogoEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(state.eliminada) { if (state.eliminada) onVolver() }
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { snackbar.showSnackbar(it); viewModel.mensajeMostrado() }
    }

    val m = state.muestra
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de muestra") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                },
                actions = {
                    if (m != null && Reglas.puedeEditar(usuario, m)) {
                        IconButton(onClick = { onEditar(m.id) }) { Icon(Icons.Default.Edit, "Editar") }
                    }
                    if (m != null && Reglas.puedeEliminar(usuario, m)) {
                        IconButton(onClick = { dialogoEliminar = true }) { Icon(Icons.Default.Delete, "Eliminar") }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (m == null) {
            Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                if (state.cargando) CircularProgressIndicator() else Text("La muestra no existe")
            }
            return@Scaffold
        }
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FotoMuestra(m.fotoPath, alto = 240.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.centro, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge)
                EstadoChip(m.estado)
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    DatoFila("Fecha y hora", formatearFecha(m.fechaHora))
                    DatoFila("Tren / Línea", "${m.tren} / ${m.linea}")
                    DatoFila("Tramo", "${m.tramoCm} cm")
                    DatoFila("Operador", m.operador)
                    DatoFila("Conteo", "${m.conteo} individuos")
                    DatoFila("Densidad", "${formatearDecimal(Reglas.densidadPorMetro(m.conteo, m.tramoCm))} ind/m")
                    DatoFila("Calibre prom.", m.calibreMm?.let { "${formatearDecimal(it)} mm" } ?: "—")
                    DatoFila("Sincronizada", if (m.sincronizada) "Sí" else "No")
                }
            }
            if (m.observaciones.isNotBlank()) {
                Text("Observaciones", fontWeight = FontWeight.SemiBold)
                Text(m.observaciones)
            }
            if (m.comentarioSupervisor.isNotBlank()) {
                Text("Comentario del supervisor", fontWeight = FontWeight.SemiBold)
                Text(m.comentarioSupervisor, color = MaterialTheme.colorScheme.error)
            }

            // Acciones de revisión (solo supervisor y estados revisables)
            if (Reglas.puedeRevisar(usuario, m)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(
                        onClick = { dialogoObservar = true },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) { Text("Observar") }
                    Button(
                        onClick = viewModel::validar,
                        colors = ButtonDefaults.buttonColors(containerColor = Exito),
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) { Text("Validar") }
                }
            }
        }
    }

    if (dialogoObservar) {
        var comentario by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { dialogoObservar = false; viewModel.limpiarErrorComentario() },
            title = { Text("Observar muestra") },
            text = {
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("¿Qué debe corregir el operador?") },
                    isError = state.errorComentario != null,
                    supportingText = state.errorComentario?.let { { Text(it) } }
                )
            },
            confirmButton = {
                TextButton(onClick = { if (viewModel.observar(comentario)) dialogoObservar = false }) {
                    Text("Enviar observación")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogoObservar = false; viewModel.limpiarErrorComentario() }) { Text("Cancelar") }
            }
        )
    }

    if (dialogoEliminar) {
        AlertDialog(
            onDismissRequest = { dialogoEliminar = false },
            title = { Text("Eliminar borrador") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { dialogoEliminar = false; viewModel.eliminar() }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { dialogoEliminar = false }) { Text("Cancelar") } }
        )
    }
}
