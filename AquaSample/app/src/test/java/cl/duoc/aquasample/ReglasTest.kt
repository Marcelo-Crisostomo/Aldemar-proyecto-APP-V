package cl.duoc.aquasample

import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Muestra
import cl.duoc.aquasample.model.Rol
import cl.duoc.aquasample.model.Usuario
import cl.duoc.aquasample.util.Reglas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasTest {

    private val operador = Usuario("OP-01", "Op", "1234", Rol.OPERADOR)
    private val supervisor = Usuario("SUP-01", "Sup", "1234", Rol.SUPERVISOR)

    private fun muestra(estado: EstadoMuestra, linea: String = "Línea 1") = Muestra(
        fechaHora = 0, centro = "Centro Isla Sur", tren = "Tren 1", linea = linea,
        tramoCm = 20, operador = "OP-01", conteo = 150, estado = estado
    )

    @Test
    fun `densidad por metro se calcula desde el tramo`() {
        assertEquals(750.0, Reglas.densidadPorMetro(150, 20), 0.001)
        assertEquals(0.0, Reglas.densidadPorMetro(150, 0), 0.001)
    }

    @Test
    fun `muestra observada reenviada pasa a corregida`() {
        assertEquals(EstadoMuestra.CORREGIDO, Reglas.estadoAlEnviar(EstadoMuestra.OBSERVADO))
        assertEquals(EstadoMuestra.PENDIENTE, Reglas.estadoAlEnviar(EstadoMuestra.BORRADOR))
    }

    @Test
    fun `permisos por rol y estado`() {
        assertTrue(Reglas.puedeEditar(operador, muestra(EstadoMuestra.BORRADOR)))
        assertFalse(Reglas.puedeEditar(operador, muestra(EstadoMuestra.VALIDADO)))
        assertTrue(Reglas.puedeRevisar(supervisor, muestra(EstadoMuestra.PENDIENTE)))
        assertFalse(Reglas.puedeRevisar(operador, muestra(EstadoMuestra.PENDIENTE)))
        assertFalse(Reglas.esVisible(supervisor, muestra(EstadoMuestra.BORRADOR)))
    }

    @Test
    fun `filtrar por texto y estado`() {
        val lista = listOf(
            muestra(EstadoMuestra.PENDIENTE, "Línea 1"),
            muestra(EstadoMuestra.VALIDADO, "Línea 2")
        )
        assertEquals(1, Reglas.filtrar(lista, "línea 2", null).size)
        assertEquals(1, Reglas.filtrar(lista, "", EstadoMuestra.PENDIENTE).size)
        assertEquals(2, Reglas.filtrar(lista, "isla", null).size)
    }
}
