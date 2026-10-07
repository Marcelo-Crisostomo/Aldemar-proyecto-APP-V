package cl.duoc.aquasample.model

enum class Rol(val etiqueta: String) {
    OPERADOR("Operador de muestreo"),
    SUPERVISOR("Supervisor técnico"),
    ANALISTA("Analista de reportes")
}

data class Usuario(
    val codigo: String,
    val nombre: String,
    val clave: String,
    val rol: Rol
)
