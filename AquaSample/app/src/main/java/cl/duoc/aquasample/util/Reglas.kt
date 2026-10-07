package cl.duoc.aquasample.util

import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Muestra
import cl.duoc.aquasample.model.Rol
import cl.duoc.aquasample.model.Usuario

/** Reglas de negocio: quién puede hacer qué y cálculos simples. */
object Reglas {

    fun puedeCrear(usuario: Usuario) = usuario.rol == Rol.OPERADOR

    /** El operador edita sus propias muestras mientras estén en borrador u observadas. */
    fun puedeEditar(usuario: Usuario, m: Muestra) =
        usuario.rol == Rol.OPERADOR && m.operador == usuario.codigo &&
            m.estado in listOf(EstadoMuestra.BORRADOR, EstadoMuestra.OBSERVADO)

    fun puedeEliminar(usuario: Usuario, m: Muestra) =
        usuario.rol == Rol.OPERADOR && m.operador == usuario.codigo &&
            m.estado == EstadoMuestra.BORRADOR

    fun puedeRevisar(usuario: Usuario, m: Muestra) =
        usuario.rol == Rol.SUPERVISOR &&
            m.estado in listOf(EstadoMuestra.PENDIENTE, EstadoMuestra.CORREGIDO)

    /** Una muestra observada que se reenvía pasa a CORREGIDO; si no, a PENDIENTE. */
    fun estadoAlEnviar(actual: EstadoMuestra) =
        if (actual == EstadoMuestra.OBSERVADO) EstadoMuestra.CORREGIDO else EstadoMuestra.PENDIENTE

    /** Qué registros ve cada rol. */
    fun esVisible(usuario: Usuario, m: Muestra) = when (usuario.rol) {
        Rol.OPERADOR -> m.operador == usuario.codigo
        Rol.SUPERVISOR -> m.estado != EstadoMuestra.BORRADOR
        Rol.ANALISTA -> m.estado == EstadoMuestra.VALIDADO
    }

    /** Individuos por metro lineal de cuerda. */
    fun densidadPorMetro(conteo: Int, tramoCm: Int): Double =
        if (tramoCm <= 0) 0.0 else conteo * 100.0 / tramoCm

    /** Filtra por texto (centro, tren, línea u operador) y por estado (null = todos). */
    fun filtrar(lista: List<Muestra>, texto: String, estado: EstadoMuestra?): List<Muestra> {
        val t = texto.trim().lowercase()
        return lista.filter { m ->
            (estado == null || m.estado == estado) &&
                (t.isEmpty() || listOf(m.centro, m.tren, m.linea, m.operador)
                    .any { it.lowercase().contains(t) })
        }
    }
}
