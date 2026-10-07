package cl.duoc.aquasample.util

/**
 * Validaciones del formulario. Cada función devuelve null si el valor es válido,
 * o un mensaje de error para mostrar en la UI.
 */
object Validador {

    const val TRAMO_MIN = 1
    const val TRAMO_MAX = 200
    const val CONTEO_MAX = 5000
    const val OBS_MAX = 300

    /** Campo obligatorio: el mensaje se escribe completo para que suene natural ("Selecciona el centro"). */
    fun requerido(valor: String, mensaje: String): String? =
        if (valor.isBlank()) mensaje else null

    fun tramo(texto: String): String? {
        val n = texto.trim().toIntOrNull() ?: return "Ingresa el largo del tramo en cm"
        return if (n !in TRAMO_MIN..TRAMO_MAX) "El tramo debe estar entre $TRAMO_MIN y $TRAMO_MAX cm" else null
    }

    /** Al enviar a revisión el conteo es obligatorio y mayor a 0; en borrador puede quedar vacío. */
    fun conteo(texto: String, obligatorio: Boolean): String? {
        if (texto.isBlank()) return if (obligatorio) "Ingresa el conteo de individuos" else null
        val n = texto.trim().toIntOrNull() ?: return "El conteo debe ser un número entero"
        return when {
            n < 0 || n > CONTEO_MAX -> "El conteo debe estar entre 0 y $CONTEO_MAX"
            obligatorio && n == 0 -> "El conteo debe ser mayor a 0 para enviar"
            else -> null
        }
    }

    /** Calibre opcional en milímetros. Acepta coma o punto decimal. */
    fun calibre(texto: String): String? {
        if (texto.isBlank()) return null
        val n = texto.trim().replace(',', '.').toDoubleOrNull() ?: return "Calibre inválido"
        return if (n <= 0 || n > 150) "El calibre debe estar entre 0 y 150 mm" else null
    }

    fun observaciones(texto: String): String? =
        if (texto.length > OBS_MAX) "Máximo $OBS_MAX caracteres" else null

    fun foto(path: String?, obligatoria: Boolean): String? =
        if (obligatoria && path.isNullOrBlank()) "Debes adjuntar una fotografía de la muestra" else null

    fun comentarioRevision(texto: String): String? =
        if (texto.trim().length < 5) "Explica la observación (mínimo 5 caracteres)" else null
}
