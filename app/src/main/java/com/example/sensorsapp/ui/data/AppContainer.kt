package com.example.sensorsapp.ui.data

import android.content.Context
import com.example.sensorsapp.ui.room.ArchiveDatabase
import com.example.sensorsapp.ui.room.MeasurementsRepository
import com.example.sensorsapp.ui.room.OfflineMeasurementsRepository

interface AppContainer{
    val measurementsRepository: MeasurementsRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    override val measurementsRepository: MeasurementsRepository by lazy{
        OfflineMeasurementsRepository(ArchiveDatabase.getDatabase(context).measurementDao())
    }

}