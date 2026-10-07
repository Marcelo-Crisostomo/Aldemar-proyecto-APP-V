package cl.duoc.aquasample.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.aquasample.model.Muestra

@Database(entities = [Muestra::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun muestraDao(): MuestraDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "aquasample.db").build()
    }
}
