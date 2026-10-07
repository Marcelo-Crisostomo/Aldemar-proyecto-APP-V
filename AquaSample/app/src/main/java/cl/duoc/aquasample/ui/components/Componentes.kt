package cl.duoc.aquasample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.ui.theme.Advertencia
import cl.duoc.aquasample.ui.theme.AzulMar
import cl.duoc.aquasample.ui.theme.ErrorRojo
import cl.duoc.aquasample.ui.theme.Exito
import cl.duoc.aquasample.ui.theme.Turquesa
import coil.compose.AsyncImage
import java.io.File

fun colorEstado(estado: EstadoMuestra): Color = when (estado) {
    EstadoMuestra.BORRADOR -> Color.Gray
    EstadoMuestra.PENDIENTE -> AzulMar
    EstadoMuestra.OBSERVADO -> ErrorRojo
    EstadoMuestra.CORREGIDO -> Advertencia
    EstadoMuestra.VALIDADO -> Exito
}

/** Etiqueta de color con el estado de revisión. */
@Composable
fun EstadoChip(estado: EstadoMuestra) {
    Surface(color = colorEstado(estado), shape = RoundedCornerShape(50)) {
        Text(
            estado.etiqueta,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Muestra la foto guardada o un marcador si aún no hay foto. */
@Composable
fun FotoMuestra(path: String?, alto: Dp = 200.dp) {
    val modifier = Modifier
        .fillMaxWidth()
        .height(alto)
        .clip(RoundedCornerShape(12.dp))
    if (path == null) {
        Box(modifier.background(Turquesa.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = AzulMar)
                Text("Sin fotografía", color = AzulMar)
            }
        }
    } else {
        AsyncImage(
            model = File(path),
            contentDescription = "Fotografía de la muestra",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

/** Fila "etiqueta: valor" para pantallas de detalle. */
@Composable
fun DatoFila(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(etiqueta, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, Modifier.weight(1.4f), fontWeight = FontWeight.SemiBold)
    }
}
