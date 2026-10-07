package cl.duoc.aquasample.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Manejo de archivos de fotografía en el almacenamiento interno de la app. */
object Fotos {

    private fun carpeta(context: Context) = File(context.filesDir, "fotos").apply { mkdirs() }

    fun nuevoArchivo(context: Context): File =
        File(carpeta(context), "muestra_${System.currentTimeMillis()}.jpg")

    /** Uri "content://" que la app de cámara puede usar para escribir la foto. */
    fun uriPara(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    /** Copia una imagen elegida en la galería a nuestra carpeta (así no perdemos el acceso). */
    fun copiarDesdeGaleria(context: Context, uri: Uri): File {
        val destino = nuevoArchivo(context)
        context.contentResolver.openInputStream(uri).use { entrada ->
            requireNotNull(entrada) { "No se pudo abrir la imagen" }
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        return destino
    }
}
