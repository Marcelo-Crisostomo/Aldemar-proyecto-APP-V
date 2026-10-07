package cl.duoc.aquasample.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Rol
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.ui.components.DatoFila
import cl.duoc.aquasample.ui.components.EstadoChip
import cl.duoc.aquasample.ui.theme.Exito
import cl.duoc.aquasample.util.formatearDecimal
import cl.duoc.aquasample.viewmodel.Fabricas
import cl.duoc.aquasample.viewmodel.ResumenViewModel
import cl.duoc.aquasample.viewmodel.SyncEstado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    usuario: Usuario,
    onVolver: () -> Unit,
    viewModel: ResumenViewModel = viewModel(factory = Fabricas.resumen(usuario))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Indicadores", fontWeight = FontWeight.Bold)
                    DatoFila("Muestras", state.total.toString())
                    DatoFila("Individuos contados", state.totalIndividuos.toString())
                    DatoFila("Densidad promedio", "${formatearDecimal(state.densidadPromedio)} ind/m")
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Por estado de revisión", fontWeight = FontWeight.Bold)
                    EstadoMuestra.entries.forEach { e ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { EstadoChip(e) }
                            Text((state.porEstado[e] ?: 0).toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Sincronización simulada con la "planilla" remota (solo supervisor y analista)
            if (usuario.rol != Rol.OPERADOR) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sincronización", fontWeight = FontWeight.Bold)
                        Text("Muestras validadas por enviar: ${state.porSincronizar}")
                        Button(
                            onClick = viewModel::sincronizar,
                            enabled = state.porSincronizar > 0 && state.sync != SyncEstado.Cargando,
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) { Text("Sincronizar validadas") }

                        when (val s = state.sync) {
                            SyncEstado.Inactivo -> Unit
                            SyncEstado.Cargando -> Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(Modifier.size(20.dp)); Text("  Enviando…")
                            }
                            is SyncEstado.Exito -> Text("✔ ${s.enviadas} muestra(s) sincronizada(s)", color = Exito)
                            is SyncEstado.Error -> Text(s.mensaje, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            Text(
                "MVP académico con datos ficticios. Los resultados no reemplazan el criterio técnico.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
