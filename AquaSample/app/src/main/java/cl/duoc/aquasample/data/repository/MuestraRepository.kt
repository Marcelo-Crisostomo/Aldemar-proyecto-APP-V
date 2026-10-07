package cl.duoc.aquasample.data.repository

import cl.duoc.aquasample.data.local.MuestraDao
import cl.duoc.aquasample.data.remote.MuestraDto
import cl.duoc.aquasample.data.remote.SyncApi
import cl.duoc.aquasample.model.EstadoMuestra
import cl.duoc.aquasample.model.Muestra
import java.io.File

/** Punto único de acceso a los datos: Room (local) y Retrofit (remoto). */
class MuestraRepository(
    private val dao: MuestraDao,
    private val api: SyncApi
) {
    fun observarTodas() = dao.observarTodas()
    fun observar(id: Long) = dao.observarPorId(id)
    suspend fun obtener(id: Long) = dao.obtenerPorId(id)

    suspend fun guardar(muestra: Muestra): Long =
        if (muestra.id == 0L) dao.insertar(muestra) else {
            dao.actualizar(muestra); muestra.id
        }

    suspend fun eliminar(muestra: Muestra) {
        muestra.fotoPath?.let { File(it).delete() }
        dao.eliminar(muestra)
    }

    suspend fun cambiarEstado(muestra: Muestra, estado: EstadoMuestra, comentario: String) =
        dao.actualizar(muestra.copy(estado = estado, comentarioSupervisor = comentario))

    /** Envía las muestras validadas pendientes. Devuelve cuántas se sincronizaron. */
    suspend fun sincronizarValidadas(): Int {
        val pendientes = dao.validadasSinSincronizar()
        pendientes.forEach { m ->
            api.enviarMuestra(
                MuestraDto(
                    m.id, m.fechaHora, m.centro, m.tren, m.linea, m.tramoCm,
                    m.operador, m.conteo, m.calibreMm, m.fotoPath?.let { File(it).name },
                    m.estado.name
                )
            )
            dao.actualizar(m.copy(sincronizada = true))
        }
        return pendientes.size
    }
}
