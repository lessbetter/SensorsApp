package com.example.sensorsapp.ui.screens.measurement

import android.hardware.Sensor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensorsapp.ui.CreatingChartState
import com.example.sensorsapp.ui.data.MeasurementData
import com.example.sensorsapp.ui.room.Measurement
import com.example.sensorsapp.ui.room.MeasurementsRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MeasurementDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    measurementsRepository: MeasurementsRepository
) : ViewModel(){
    var creatingChartState: CreatingChartState by mutableStateOf(CreatingChartState.Loading)
        private set

//    val selectedSensors: MutableList<Int> = mutableListOf()
    val acceChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    val magneChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    val gyroChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    val gravChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    private val itemId: Int = checkNotNull(savedStateHandle[MeasurementDetailsDestination.itemIdArg])

    val uiState: StateFlow<MeasurementDetailsUiState> =
        measurementsRepository.getMeasurementStream(itemId)
            .filterNotNull()
            .map {
                MeasurementDetailsUiState(measurementDetails = it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MeasurementDetailsUiState(
                    measurementDetails = Measurement(
                        title = "",
                        data = MeasurementData(),
                        date = ""
                    )
                )
            )



    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


    fun toAxis(){
        val selectedSensors: MutableList<Int> = uiState.value.measurementDetails.data.listOfSensors
        val gravData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_GRAVITY)
        val gyroData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_GYROSCOPE)
        val magneData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_MAGNETIC_FIELD)
        val acceData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_ACCELEROMETER)
        val timeList: MutableList<Float> = uiState.value.measurementDetails.data.timeTable
        val defaultDispatcher = Dispatchers.Default
        viewModelScope.launch (defaultDispatcher){
            val job1 = viewModelScope.launch(defaultDispatcher){
                if(selectedSensors.contains(Sensor.TYPE_GRAVITY)){
                    gravChartModelProducer.runTransaction {
                        lineSeries{
                            series(x=timeList.map{it},y=gravData.map{it[0]})
                            series(x=timeList.map{it},y=gravData.map{it[1]})
                            series(x=timeList.map{it},y=gravData.map{it[2]})
                        }
                    }
                    delay(2000L)
                }
            }
            val job2=viewModelScope.launch(defaultDispatcher){
                if(selectedSensors.contains(Sensor.TYPE_GYROSCOPE)){
                    gyroChartModelProducer.runTransaction {
                        lineSeries{
                            series(timeList,gyroData.map{it[0]})
                            series(timeList,gyroData.map{it[1]})
                            series(timeList,gyroData.map{it[2]})
                        }
                    }
                    delay(2000L)
                }
            }

            val job3=viewModelScope.launch(defaultDispatcher){
                if(selectedSensors.contains(Sensor.TYPE_MAGNETIC_FIELD)){
                    magneChartModelProducer.runTransaction {
                        lineSeries {
                            series(timeList,magneData.map{it[0]})
                            series(timeList,magneData.map{it[1]})
                            series(timeList,magneData.map{it[2]})
                        }
                    }
                    delay(2000L)
                }
            }
            val job4=viewModelScope.launch(defaultDispatcher){
                if(selectedSensors.contains(Sensor.TYPE_ACCELEROMETER)){
                    acceChartModelProducer.runTransaction {
                        lineSeries {
                            series(timeList,acceData.map{it[0]})
                            series(timeList,acceData.map{it[1]})
                            series(timeList,acceData.map{it[2]})
                        }
//                        Log.d("creatingChart: ","finished")
                    }
                    delay(2000L)
                }
            }
            job1.join()
            job2.join()
            job3.join()
            job4.join()
        }
        creatingChartState = CreatingChartState.Success
    }

    private fun populateDataList(sensorType: Int): MutableList<List<Float>> {
        val listOfData: MutableList<List<Float>> = mutableListOf()
        when(sensorType){
            Sensor.TYPE_GRAVITY -> {
                for(value in uiState.value.measurementDetails.data.sensorsData){
                    if(value.sensorType==Sensor.TYPE_GRAVITY){
                        listOfData.addAll(value.values)
                        return listOfData
                    }
                }
            }
            Sensor.TYPE_GYROSCOPE -> {
                for(value in uiState.value.measurementDetails.data.sensorsData){
                    if(value.sensorType==Sensor.TYPE_GYROSCOPE){
                        listOfData.addAll(value.values)
                        return listOfData
                    }
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                for(value in uiState.value.measurementDetails.data.sensorsData){
                    if(value.sensorType==Sensor.TYPE_MAGNETIC_FIELD){
                        listOfData.addAll(value.values)
                        return listOfData
                    }
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                for(value in uiState.value.measurementDetails.data.sensorsData){
                    if(value.sensorType==Sensor.TYPE_ACCELEROMETER){
                        listOfData.addAll(value.values)
                        return listOfData
                    }
                }
            }
        }
        return listOfData
    }

}

data class MeasurementDetailsUiState(
    val outOfStock: Boolean = true,
    val measurementDetails: Measurement,
    val chartState: CreatingChartState = CreatingChartState.Loading
)