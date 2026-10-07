package cl.duoc.aquasample.model

enum class EstadoMuestra(val etiqueta: String) {
    BORRADOR("Borrador"),
    PENDIENTE("Pendiente"),
    OBSERVADO("Observado"),
    CORREGIDO("Corregido"),
    VALIDADO("Validado")
}
