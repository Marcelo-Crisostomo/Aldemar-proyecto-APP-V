package cl.duoc.aquasample.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.aquasample.AquaSampleApp
import cl.duoc.aquasample.data.repository.MuestraRepository
import cl.duoc.aquasample.model.Usuario

/**
 * Fábricas para crear ViewModels que reciben parámetros (repositorio, usuario, id).
 * Así evitamos usar una librería de inyección de dependencias.
 */
object Fabricas {

    private fun CreationExtras.repo(): MuestraRepository =
        (this[APPLICATION_KEY] as AquaSampleApp).repository

    fun muestras(usuario: Usuario): ViewModelProvider.Factory = viewModelFactory {
        initializer { MuestrasViewModel(repo(), usuario) }
    }

    fun formulario(usuario: Usuario, id: Long): ViewModelProvider.Factory = viewModelFactory {
        initializer { MuestraFormViewModel(repo(), usuario, id) }
    }

    fun detalle(id: Long): ViewModelProvider.Factory = viewModelFactory {
        initializer { DetalleViewModel(repo(), id) }
    }

    fun resumen(usuario: Usuario): ViewModelProvider.Factory = viewModelFactory {
        initializer { ResumenViewModel(repo(), usuario) }
    }
}
