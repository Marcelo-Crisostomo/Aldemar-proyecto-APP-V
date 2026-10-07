package cl.duoc.aquasample.ui.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aquasample.model.DatosDemo
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.ui.components.CampoDesplegable
import cl.duoc.aquasample.ui.components.CampoTexto
import cl.duoc.aquasample.ui.components.Contador
import cl.duoc.aquasample.ui.components.FotoMuestra
import cl.duoc.aquasample.util.Fotos
import cl.duoc.aquasample.util.formatearFecha
import cl.duoc.aquasample.viewmodel.Fabricas
import cl.duoc.aquasample.viewmodel.MuestraFormViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuestraFormScreen(
    usuario: Usuario,
    idMuestra: Long,
    onVolver: () -> Unit,
    viewModel: MuestraFormViewModel = viewModel(factory = Fabricas.formulario(usuario, idMuestra))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    // Ruta del archivo que estamos esperando de la cámara (sobrevive a la rotación).
    var fotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val path = fotoPendiente
        if (ok && path != null) viewModel.fotoCapturada(path) else path?.let { File(it).delete() }
        fotoPendiente = null
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scope.launch {
            try {
                val archivo = withContext(Dispatchers.IO) { Fotos.copiarDesdeGaleria(context, uri) }
                viewModel.fotoCapturada(archivo.path)
            } catch (e: Exception) {
                snackbar.showSnackbar("No se pudo cargar la imagen")
            }
        }
    }

    // Efectos: volver al guardar y mostrar mensajes.
    LaunchedEffect(state.guardado) { if (state.guardado) onVolver() }
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { snackbar.showSnackbar(it); viewModel.mensajeMostrado() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (idMuestra == 0L) "Nueva muestra" else "Editar muestra") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        val e = state.errores
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Comentario del supervisor si la muestra fue observada
            if (state.comentarioSupervisor.isNotBlank()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text("Observación del supervisor: ${state.comentarioSupervisor}", Modifier.padding(12.dp))
                }
            }
            Text("Fecha: ${formatearFecha(state.fechaHora)} · Operador: ${state.operador}")

            CampoDesplegable(state.centro, DatosDemo.centros, { viewModel.actualizar { copy(centro = it) } }, "Centro", e.centro)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(Modifier.weight(1f)) {
                    CampoDesplegable(state.tren, DatosDemo.trenes, { viewModel.actualizar { copy(tren = it) } }, "Tren", e.tren)
                }
                Column(Modifier.weight(1f)) {
                    CampoDesplegable(state.linea, DatosDemo.lineas, { viewModel.actualizar { copy(linea = it) } }, "Línea", e.linea)
                }
            }
            CampoTexto(state.tramo, { viewModel.actualizar { copy(tramo = it) } }, "Tramo muestreado (cm)", e.tramo, numerico = true)

            Contador(
                valor = state.conteo,
                onCambio = { viewModel.actualizar { copy(conteo = it) } },
                onSumar = viewModel::sumarConteo,
                error = e.conteo
            )

            CampoTexto(state.calibre, { viewModel.actualizar { copy(calibre = it) } }, "Calibre promedio mm (opcional)", e.calibre, numerico = true)
            CampoTexto(state.observaciones, { viewModel.actualizar { copy(observaciones = it) } }, "Observaciones", e.observaciones, lineas = 3)

            // Fotografía
            Text("Fotografía de la muestra", style = MaterialTheme.typography.titleSmall)
            FotoMuestra(state.fotoPath)
            e.foto?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    try {
                        val archivo = Fotos.nuevoArchivo(context)
                        fotoPendiente = archivo.path
                        camara.launch(Fotos.uriPara(context, archivo))
                    } catch (ex: ActivityNotFoundException) {
                        scope.launch { snackbar.showSnackbar("No hay una app de cámara disponible") }
                    }
                }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PhotoCamera, null); Text(" Cámara")
                }
                OutlinedButton(onClick = {
                    galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.PhotoLibrary, null); Text(" Galería")
                }
            }

            // Acciones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(onClick = { viewModel.guardar(enviar = false) }, modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Guardar")
                }
                Button(onClick = { viewModel.guardar(enviar = true) }, modifier = Modifier.weight(1f).height(52.dp)) {
                    Text("Enviar a revisión")
                }
            }
        }
    }
}
