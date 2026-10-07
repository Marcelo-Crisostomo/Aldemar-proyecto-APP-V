package cl.duoc.aquasample.model

/**
 * Datos FICTICIOS para el MVP académico.
 * No usar nombres, RUT ni datos reales de trabajadores o centros.
 */
object DatosDemo {

    val usuarios = listOf(
        Usuario("OP-01", "Operador Uno", "1234", Rol.OPERADOR),
        Usuario("OP-02", "Operador Dos", "1234", Rol.OPERADOR),
        Usuario("SUP-01", "Supervisor Uno", "1234", Rol.SUPERVISOR),
        Usuario("AN-01", "Analista Uno", "1234", Rol.ANALISTA)
    )

    val centros = listOf("Centro Bahía Norte", "Centro Isla Sur", "Centro Estero Azul")
    val trenes = listOf("Tren 1", "Tren 2", "Tren 3", "Tren 4")
    val lineas = (1..10).map { "Línea $it" }
}
