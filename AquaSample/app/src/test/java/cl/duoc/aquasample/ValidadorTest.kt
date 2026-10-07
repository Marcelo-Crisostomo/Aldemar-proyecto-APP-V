package cl.duoc.aquasample

import cl.duoc.aquasample.util.Validador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorTest {

    @Test
    fun `tramo fuera de rango entrega error`() {
        assertNotNull(Validador.tramo("0"))
        assertNotNull(Validador.tramo("500"))
        assertNotNull(Validador.tramo("abc"))
        assertNull(Validador.tramo("20"))
    }

    @Test
    fun `conteo vacio se permite en borrador pero no al enviar`() {
        assertNull(Validador.conteo("", obligatorio = false))
        assertEquals("Ingresa el conteo de individuos", Validador.conteo("", obligatorio = true))
        assertNotNull(Validador.conteo("0", obligatorio = true))
        assertNull(Validador.conteo("152", obligatorio = true))
    }

    @Test
    fun `calibre es opcional y acepta coma decimal`() {
        assertNull(Validador.calibre(""))
        assertNull(Validador.calibre("35,5"))
        assertNotNull(Validador.calibre("-3"))
    }

    @Test
    fun `foto es obligatoria solo al enviar a revision`() {
        assertNull(Validador.foto(null, obligatoria = false))
        assertNotNull(Validador.foto(null, obligatoria = true))
        assertNull(Validador.foto("/data/fotos/m.jpg", obligatoria = true))
    }
}
