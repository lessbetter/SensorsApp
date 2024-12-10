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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
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

    var collectedGravityData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedMagneticData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedGyroscopeData: MutableList<DataFromSensor> = mutableListOf()

    var listOfSensors: MutableList<Sensors> = mutableListOf()

    var selectedSensors: MutableList<String> = mutableListOf()

    val myStopWatch: StopWatch = StopWatch(this)

    var gravChartModel: CartesianChartModelProducer = CartesianChartModelProducer()
    var magneChartProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    var gyroChartProducer: CartesianChartModelProducer = CartesianChartModelProducer()


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
                    resetData()
                }else{
                    hasRestarted = true
                    pauseDuration += timeSource.markNow().minus(stopMark)
                }
                myStopWatch.start()

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

                myStopWatch.pause()

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
            myStopWatch.reset()
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

            sensorGravity = MySensorClass(ctx,Sensor.TYPE_GRAVITY,{values ->
                gravityData.v0 = values[0]
                //gravityData.timestamp = timestamp
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_GYROSCOPE,false))

            sensorGyroscope = MySensorClass(ctx,Sensor.TYPE_GYROSCOPE, { values->
                gyroscopeData.v0 = values[0]
                //gyroscopeData.timestamp = timestamp
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_ACCELEROMETER,false))

        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_MAGNETIC_FIELD,false))

            sensorMagnetic = MySensorClass(ctx,Sensor.TYPE_MAGNETIC_FIELD,{values ->
                magneticData.v0 = values[0]
                //magneticData.timestamp = timestamp
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
        val gravData: MutableList<Float> = mutableListOf()
        val timeList: MutableList<Float> = mutableListOf()
        val gyroData: MutableList<Float> = mutableListOf()
        for (value in collectedGravityData){
            val v0Rounded = value.v0.toBigDecimal().setScale(3,RoundingMode.UP).toFloat()
            gravData.add(v0Rounded)
            val rounded = value.timeMark!!.toDouble(DurationUnit.SECONDS).toBigDecimal().setScale(3,RoundingMode.UP).toFloat()
            timeList.add(rounded)
        }
        for(value in collectedGyroscopeData){
            gyroData.add(value.v0.toBigDecimal().setScale(3,RoundingMode.UP).toFloat())
        }
        _resultUiState.update { currentState ->
            currentState.copy(
                gravAxis = gravData,
                timeAxis = timeList,
                gyroAxis = gyroData,
            )
        }
        viewModelScope.launch {
            if(selectedSensors.contains(Sensor.STRING_TYPE_GRAVITY)){
                gravChartModel.runTransaction {
                    lineSeries{ series(x=timeList.map{it},y=gravData.map{it}) }
                }
            }
            if(selectedSensors.contains(Sensor.STRING_TYPE_MAGNETIC_FIELD)){
                magneChartProducer.runTransaction {
                    //lineSeries{ series(resultUiState.value.timeAxis,resultUiState.value.magnAxis) }
                }
            }
            if(selectedSensors.contains(Sensor.STRING_TYPE_GYROSCOPE)){
                gyroChartProducer.runTransaction {
                    lineSeries{ series(timeList,gyroData) }
                }
            }
        }
    }

    fun resetData(){
        _resultUiState.update { currentState ->
            currentState.copy(
                gravAxis = mutableListOf(),
                timeAxis = mutableListOf(),
                gyroAxis = mutableListOf()
            )
        }
    }

    fun updateTimeText(formattedTime: String){
        _measurementUiState.update { currentState ->
            currentState.copy(formattedTime = formattedTime)
        }
    }

}

