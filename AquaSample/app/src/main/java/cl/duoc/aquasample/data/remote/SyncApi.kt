package cl.duoc.aquasample.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/** Datos que se envían a la "planilla" remota. No incluye la foto, solo su referencia. */
data class MuestraDto(
    val idLocal: Long,
    val fechaHora: Long,
    val centro: String,
    val tren: String,
    val linea: String,
    val tramoCm: Int,
    val operador: String,
    val conteo: Int,
    val calibreMm: Double?,
    val foto: String?,
    val estado: String
)

/** Respuesta de la API: el servidor devuelve el id asignado. */
data class RespuestaSync(val id: Int)

interface SyncApi {
    // JSONPlaceholder acepta POST y responde con un id falso: ideal para simular.
    @POST("posts")
    suspend fun enviarMuestra(@Body muestra: MuestraDto): RespuestaSync
}

object RetrofitClient {
    // Reemplaza esta URL por tu propia API (por ejemplo Spring Boot) cuando la tengas.
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val api: SyncApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SyncApi::class.java)
    }
}
