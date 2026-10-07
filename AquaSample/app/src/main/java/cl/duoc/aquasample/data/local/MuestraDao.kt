package cl.duoc.aquasample.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.duoc.aquasample.model.Muestra
import kotlinx.coroutines.flow.Flow

@Dao
interface MuestraDao {

    @Query("SELECT * FROM muestras ORDER BY fechaHora DESC")
    fun observarTodas(): Flow<List<Muestra>>

    @Query("SELECT * FROM muestras WHERE id = :id")
    fun observarPorId(id: Long): Flow<Muestra?>

    @Query("SELECT * FROM muestras WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Muestra?

    @Query("SELECT * FROM muestras WHERE estado = 'VALIDADO' AND sincronizada = 0")
    suspend fun validadasSinSincronizar(): List<Muestra>

    @Insert
    suspend fun insertar(muestra: Muestra): Long

    @Update
    suspend fun actualizar(muestra: Muestra)

    @Delete
    suspend fun eliminar(muestra: Muestra)
}
