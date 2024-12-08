package com.example.sensorsapp.ui.data


import android.hardware.Sensor
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensorsapp.ui.MeasurementUiState
import com.example.sensorsapp.ui.ResultUiState
import com.example.sensorsapp.ui.SensorsUiState
import com.example.sensorsapp.ui.sensors.MySensorClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.toDuration

class MeasurementViewModel : ViewModel() {

    private val _measurementUiState = MutableStateFlow(MeasurementUiState())
    val measurementUiState: StateFlow<MeasurementUiState> = _measurementUiState.asStateFlow()

    private val _resultUiState = MutableStateFlow(ResultUiState())
    val resultUiState: StateFlow<ResultUiState> = _resultUiState.asStateFlow()

    private val _sensorsUiState = MutableStateFlow(SensorsUiState())
    val sensorsUiState: StateFlow<SensorsUiState> = _sensorsUiState.asStateFlow()

    private lateinit var sensorManager: SensorManager

    private lateinit var sensorGyroscope: MySensorClass
    private lateinit var sensorGravity: MySensorClass
    private lateinit var sensorMagnetic: MySensorClass

    private var gravityData: DataFromSensor = DataFromSensor()
    private var magneticData: DataFromSensor = DataFromSensor()
    private var gyroscopeData: DataFromSensor = DataFromSensor()

    private var collectedGravityData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedMagneticData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedGyroscopeData: MutableList<DataFromSensor> = mutableListOf()

    var listOfSensors: MutableList<Sensors> = mutableListOf()

    var selectedSensors: MutableList<String> = mutableListOf()


    private var mSensor: Sensor? = null

    private var readDelay: Long = 500000000

    private var isRunning: Boolean = false

    private var hasStarted: Boolean = false

    val timeSource = TimeSource.Monotonic

    var timeAdjustment: TimeSource.Monotonic.ValueTimeMark = timeSource.markNow()

    var stopMark: TimeSource.Monotonic.ValueTimeMark = timeSource.markNow()

    var pauseDuration: Duration = timeSource.markNow().minus(timeAdjustment)


    var hasRestarted = false




    fun startMeasuring(){
        if(::sensorManager.isInitialized ){
            if(!isRunning){

                if(!hasStarted){
                    hasStarted = true
                    timeAdjustment = timeSource.markNow()
                }else{
                    hasRestarted = true
                    pauseDuration += timeSource.markNow().minus(stopMark)
                }

                _measurementUiState.update { currentState ->
                    currentState.copy(
                        isRunning = true
                    )
                }

                if(selectedSensors.contains(Sensor.STRING_TYPE_GYROSCOPE))sensorGyroscope.startListening()
                if(selectedSensors.contains(Sensor.STRING_TYPE_GRAVITY))sensorGravity.startListening()
                if(selectedSensors.contains(Sensor.STRING_TYPE_MAGNETIC_FIELD))sensorMagnetic.startListening()

                isRunning=true

                viewModelScope.launch { readData() }
            }
            else {
                stopMark = timeSource.markNow()
                _measurementUiState.update { currentState ->
                    currentState.copy(
                        isRunning = false
                    )
                }
                isRunning=false

                for(data in collectedGravityData){
                    Log.d("Collected Gravity Data","val: ${data.v0} at: ${data.timeMark}")
                }
                for(data in collectedMagneticData){
                    Log.d("Collected Magnetic Data","val: ${data.v0} at: ${data.timeMark}")
                }
                for(data in collectedGyroscopeData){
                    Log.d("Collected Gyroscope Data","val: ${data.v0} at: ${data.timeMark}")
                }
                sensorGyroscope.stopListening()
                sensorGravity.stopListening()
                sensorMagnetic.stopListening()
            }

        }else
            return
    }

    fun stopRunning(){
        if(hasStarted==true){
            hasStarted = false
            hasRestarted = false
            pauseDuration = 0.toDuration(DurationUnit.NANOSECONDS)
            Log.d("Stopped measuring","TRUE")
            isRunning = false
            _measurementUiState.update { currentState ->
                currentState.copy(
                    isRunning = false
                )
            }
            for(data in collectedGravityData){
                Log.d("Collected Gravity Data","val: ${data.v0} at: ${data.timeMark}")
            }
            for(data in collectedMagneticData){
                Log.d("Collected Magnetic Data","val: ${data.v0} at: ${data.timeMark}")
            }
            for(data in collectedGyroscopeData){
                Log.d("Collected Gyroscope Data","val: ${data.v0} at: ${data.timeMark}")
            }
            sensorGyroscope.stopListening()
            sensorGravity.stopListening()
            sensorMagnetic.stopListening()
            collectedGravityData.clear()
            collectedMagneticData.clear()
            collectedGyroscopeData.clear()
        }
    }

    suspend fun readData(){
        while(isRunning){

            if(timeSource!=null){
                var timeMark = timeSource.markNow().minus(timeAdjustment)
                if(hasRestarted){
                    timeMark -= pauseDuration
                }
                collectedGravityData.add(DataFromSensor(timeMark,gravityData.timestamp,gravityData.v0))
                collectedMagneticData.add(DataFromSensor(timeMark,magneticData.timestamp,magneticData.v0))
                collectedGyroscopeData.add(DataFromSensor(timeMark,gyroscopeData.timestamp,gyroscopeData.v0))
            }


            Log.d("Collecting Test","collected")
            delay(500)
        }
    }


    fun getSensors(ctx: Context){
        sensorManager = ctx.getSystemService(SENSOR_SERVICE) as SensorManager


        if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!=null){
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_GRAVITY,false))

            sensorGravity = MySensorClass(ctx,Sensor.TYPE_GRAVITY,{values,timestamp ->
                gravityData.v0 = values[0]
                gravityData.timestamp = timestamp
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_GYROSCOPE,false))

            sensorGyroscope = MySensorClass(ctx,Sensor.TYPE_GYROSCOPE, { values, timestamp ->
                gyroscopeData.v0 = values[0]
                gyroscopeData.timestamp = timestamp
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_ACCELEROMETER,false))

        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_MAGNETIC_FIELD,false))

            sensorMagnetic = MySensorClass(ctx,Sensor.TYPE_MAGNETIC_FIELD,{values, timestamp ->
                magneticData.v0 = values[0]
                magneticData.timestamp = timestamp
            })
        }

        _sensorsUiState.update { currentState ->
            currentState.copy(
                listOfSensors = listOfSensors
            )
        }
    }


    fun onCheckedUpdate(name: String,selected: Boolean){
//        var tempList = sensorsUiState.value.listOfSensors
        when(name){
            Sensor.STRING_TYPE_GRAVITY -> _sensorsUiState.update { currentState -> currentState.copy(isGravityChecked = selected) }
            Sensor.STRING_TYPE_MAGNETIC_FIELD -> _sensorsUiState.update { it.copy(isMagneticChecked = selected) }
            Sensor.STRING_TYPE_GYROSCOPE -> _sensorsUiState.update { it.copy(isGyroscopeChecked = selected) }
        }
    }

    fun setLocalList(){
        //listOfSensors = mutableListOf()
        if(sensorsUiState.value.isGravityChecked) selectedSensors.add(Sensor.STRING_TYPE_GRAVITY)
        if(sensorsUiState.value.isGyroscopeChecked) selectedSensors.add(Sensor.STRING_TYPE_GYROSCOPE)
        if(sensorsUiState.value.isMagneticChecked) selectedSensors.add(Sensor.STRING_TYPE_MAGNETIC_FIELD)
        if(sensorsUiState.value.isAccelerometerChecked) selectedSensors.add(Sensor.STRING_TYPE_ACCELEROMETER)

    }

//    fun updateSensorsList(sensors: MutableList<Sensors>, element: Sensors): MutableList<Sensors>{
//        sensors.add(element)
//        return sensors
//    }

    fun toAxis(){
        val valList: MutableList<Float> = mutableListOf()
        val timeList: MutableList<Long> = mutableListOf()
        val gyroData: MutableList<Float> = mutableListOf()
        for (value in collectedGravityData){
            valList.add(value.v0)
            timeList.add(value.timeMark!!.inWholeSeconds)
        }
        for(value in collectedGyroscopeData){
            gyroData.add(value.v0)
        }
        _resultUiState.update { currentState ->
            currentState.copy(
                yAxis = valList,
                xAxis = timeList,
                gyroAxis = gyroData,
            )
        }
    }

}

