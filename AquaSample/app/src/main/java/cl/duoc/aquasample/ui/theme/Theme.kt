package cl.duoc.aquasample.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta pequeña inspirada en el mar
val AzulMar = Color(0xFF0B4F6C)     // primario
val Turquesa = Color(0xFF20A39E)    // secundario
val Fondo = Color(0xFFF4F7F9)
val Superficie = Color(0xFFFFFFFF)
val Exito = Color(0xFF2E7D32)
val Advertencia = Color(0xFFED8C00)
val ErrorRojo = Color(0xFFC62828)

private val Colores = lightColorScheme(
    primary = AzulMar,
    onPrimary = Color.White,
    secondary = Turquesa,
    onSecondary = Color.White,
    background = Fondo,
    surface = Superficie,
    error = ErrorRojo
)

@Composable
fun AquaSampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, content = content)
}
