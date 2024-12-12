package com.example.sensorsapp.ui.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Measurement::class], version = 1, exportSchema = false)
abstract class ArchiveDatabase: RoomDatabase() {

    abstract fun measurementDao(): MeasurementDao
    companion object{
        @Volatile
        private var Instance: ArchiveDatabase? = null

        fun getDatabase(context: Context): ArchiveDatabase{
            return Instance ?: synchronized(this){
                Room.databaseBuilder(context,ArchiveDatabase::class.java,"archive_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}