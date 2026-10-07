package cl.duoc.aquasample.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Una muestra de choritos tomada de un tramo de línea de cultivo. */
@Entity(tableName = "muestras")
data class Muestra(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fechaHora: Long,              // milisegundos (System.currentTimeMillis)
    val centro: String,
    val tren: String,
    val linea: String,
    val tramoCm: Int,
    val operador: String,             // código ficticio del operador
    val conteo: Int,
    val calibreMm: Double? = null,    // opcional
    val fotoPath: String? = null,     // ruta del archivo en almacenamiento interno
    val observaciones: String = "",
    val estado: EstadoMuestra = EstadoMuestra.BORRADOR,
    val comentarioSupervisor: String = "",
    val sincronizada: Boolean = false
)
