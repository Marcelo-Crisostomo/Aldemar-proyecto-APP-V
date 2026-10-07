package cl.duoc.aquasample.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Texto de error bajo un campo (solo si existe error). */
private fun textoError(error: String?): (@Composable () -> Unit)? =
    error?.let { { Text(it) } }

/** Campo de texto con mensaje de error. */
@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    numerico: Boolean = false,
    lineas: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = textoError(error),
        singleLine = lineas == 1,
        minLines = lineas,
        keyboardOptions = if (numerico) KeyboardOptions(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default,
        modifier = modifier.fillMaxWidth()
    )
}

/** Lista desplegable para elegir un valor de un catálogo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDesplegable(
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    etiqueta: String,
    error: String?
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            isError = error != null,
            supportingText = textoError(error),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandido) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = { onSeleccion(opcion); expandido = false }
                )
            }
        }
    }
}

/**
 * Contador táctil para el conteo en mesa: el operador presiona "+1" por cada chorito.
 * También puede escribir el número directamente.
 */
@Composable
fun Contador(
    valor: String,
    onCambio: (String) -> Unit,
    onSumar: (Int) -> Unit,
    error: String?
) {
    Column {
        Text("Conteo de individuos", style = MaterialTheme.typography.titleSmall)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(onClick = { onSumar(-1) }, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Default.Remove, contentDescription = "Restar uno")
            }
            OutlinedTextField(
                value = valor,
                onValueChange = onCambio,
                isError = error != null,
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(120.dp)
            )
            Button(onClick = { onSumar(1) }, modifier = Modifier.weight(1f).height(56.dp)) {
                Text("+1", fontSize = 22.sp)
            }
            Button(onClick = { onSumar(10) }, modifier = Modifier.height(56.dp)) {
                Text("+10")
            }
        }
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
