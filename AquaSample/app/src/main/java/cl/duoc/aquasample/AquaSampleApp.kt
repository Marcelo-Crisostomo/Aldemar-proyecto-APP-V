package cl.duoc.aquasample

import android.app.Application
import cl.duoc.aquasample.data.local.AppDatabase
import cl.duoc.aquasample.data.remote.RetrofitClient
import cl.duoc.aquasample.data.repository.MuestraRepository

/** Crea una sola vez la base de datos y el repositorio para toda la app. */
class AquaSampleApp : Application() {
    val repository: MuestraRepository by lazy {
        MuestraRepository(AppDatabase.crear(this).muestraDao(), RetrofitClient.api)
    }
}
