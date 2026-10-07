package cl.duoc.aquasample.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import cl.duoc.aquasample.model.Muestra
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.ui.components.EstadoChip
import cl.duoc.aquasample.ui.theme.Exito
import cl.duoc.aquasample.util.Reglas
import cl.duoc.aquasample.util.formatearDecimal
import cl.duoc.aquasample.util.formatearFecha
import cl.duoc.aquasample.viewmodel.Fabricas
import cl.duoc.aquasample.viewmodel.MuestrasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuestrasScreen(
    usuario: Usuario,
    onNueva: () -> Unit,
    onAbrir: (Long) -> Unit,
    onResumen: () -> Unit,
    onSalir: () -> Unit,
    viewModel: MuestrasViewModel = viewModel(factory = Fabricas.muestras(usuario))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Muestras")
                        Text("${usuario.codigo} · ${usuario.rol.etiqueta}", style = MaterialTheme.typography.bodySmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onResumen) { Icon(Icons.Default.BarChart, "Resumen") }
                    IconButton(onClick = onSalir) { Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión") }
                }
            )
        },
        floatingActionButton = {
            if (Reglas.puedeCrear(usuario)) {
                ExtendedFloatingActionButton(
                    onClick = onNueva,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Nueva muestra") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = state.texto,
                onValueChange = viewModel::onTextoChange,
                placeholder = { Text("Buscar centro, tren, línea u operador") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(state.estado == null, { viewModel.onEstadoChange(null) }, { Text("Todos") })
                }
                items(EstadoMuestra.entries) { e ->
                    FilterChip(state.estado == e, { viewModel.onEstadoChange(e) }, { Text(e.etiqueta) })
                }
            }

            when {
                state.cargando -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.muestras.isEmpty() -> Box(Modifier.fillMaxSize().padding(32.dp), Alignment.Center) {
                    Text("No hay muestras para mostrar.")
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.muestras, key = { it.id }) { m ->
                        MuestraCard(m, onClick = { onAbrir(m.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun MuestraCard(m: Muestra, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(m.centro, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                EstadoChip(m.estado)
            }
            Text("${m.tren} · ${m.linea} · tramo ${m.tramoCm} cm")
            Text(formatearFecha(m.fechaHora) + " · " + m.operador, style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${m.conteo} individuos · ${formatearDecimal(Reglas.densidadPorMetro(m.conteo, m.tramoCm))} ind/m",
                    Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                if (m.sincronizada) Icon(Icons.Default.CloudDone, "Sincronizada", tint = Exito)
            }
        }
    }
}
