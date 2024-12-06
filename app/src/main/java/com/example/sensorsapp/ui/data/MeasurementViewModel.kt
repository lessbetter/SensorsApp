package com.example.sensorsapp.ui.data


import android.hardware.Sensor
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.sensorsapp.ui.MeasurementUiState
import com.example.sensorsapp.ui.SensorsUiState
import com.example.sensorsapp.ui.sensors.MySensorClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MeasurementViewModel : ViewModel() {
    private val _measurementUiState = MutableStateFlow(MeasurementUiState())
    val measurementUiState: StateFlow<MeasurementUiState> = _measurementUiState.asStateFlow()
    private val _sensorsUiState = MutableStateFlow(SensorsUiState(isChecked = { setListChecked(it) }))
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


    private var mSensor: Sensor? = null

    private var readDelay: Long = 500000000

    private var isRunning: Boolean = false




    fun startMeasuring(){
        if(::sensorManager.isInitialized && mSensor!=null){
            if(isRunning==false){


                sensorGyroscope.startListening()
                sensorGravity.startListening()
                sensorMagnetic.startListening()

                isRunning=true
            }
            else if (isRunning==true){
                isRunning=false
                //sensorManager.unregisterListener(testListener,mSensor)
                for(data in collectedGravityData){
                    Log.d("Collected Gravity Data","val: ${data.v0} at: ${data.timestamp}")
                }
                for(data in collectedMagneticData){
                    Log.d("Collected Magnetic Data","val: ${data.v0} at: ${data.timestamp}")
                }
                for(data in collectedGyroscopeData){
                    Log.d("Collected Gyroscope Data","val: ${data.v0} at: ${data.timestamp}")
                }
                sensorGyroscope.stopListening()
                sensorGravity.stopListening()
                sensorMagnetic.stopListening()
            }

        }else
            return
    }

    suspend fun readData(){
        while(isRunning){
            delay(500)

            collectedGravityData.add(DataFromSensor(gravityData.timestamp,gravityData.v0))
            collectedMagneticData.add(DataFromSensor(magneticData.timestamp,magneticData.v0))
            collectedGyroscopeData.add(DataFromSensor(gyroscopeData.timestamp,gyroscopeData.v0))
            Log.d("Collecting Test","collected")
        }
    }


    fun getSensors(ctx: Context){
        sensorManager = ctx.getSystemService(SENSOR_SERVICE) as SensorManager
        val deviceSensors: MutableList<Sensor> = mutableListOf()




        if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!=null){
            //sensorsUiState.value.listOfSensors.add(Sensors(Sensor.STRING_TYPE_GRAVITY,false))
            _sensorsUiState.update { currentState ->
                currentState.copy(
                    listOfSensors = updateSensorsList(currentState.listOfSensors,Sensors(Sensor.STRING_TYPE_GRAVITY,false))
                )
            }
            //mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
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
            deviceSensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!)
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_ACCELEROMETER,false))

            deviceSensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!)
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
            listOfSensors.add(Sensors(Sensor.STRING_TYPE_MAGNETIC_FIELD,false))

            deviceSensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!)

            sensorMagnetic = MySensorClass(ctx,Sensor.TYPE_MAGNETIC_FIELD,{values, timestamp ->
                magneticData.v0 = values[0]
                magneticData.timestamp = timestamp
            })
        }
    }

    fun resetData(){
        isRunning = false
        collectedGravityData.clear()
        collectedMagneticData.clear()
        collectedGyroscopeData.clear()
    }

    fun onCheckedUpdate(name: String,selected: Boolean){
        var tempList = sensorsUiState.value.listOfSensors
        when(name){
            Sensor.STRING_TYPE_GRAVITY -> _sensorsUiState.update { currentState -> currentState.copy(isGravityChecked = selected) }
        }
//        for (sensor in tempList){
//            if(sensor.sensorName==name){
//                sensor.isSelected = selected
//                _sensorsUiState.update { currentState ->
//                    currentState.copy (
//                        listOfSensors = tempList
//                    )
//                }
//                break
//            }
//        }
    }

    fun setListChecked(name: String): Boolean{
        for (sensor in sensorsUiState.value.listOfSensors){
            if(sensor.sensorName==name){
                return sensor.isSelected
            }
        }
        return false
    }

    fun updateSensorsList(sensors: MutableList<Sensors>, element: Sensors): MutableList<Sensors>{
        sensors.add(element)
        return sensors
    }

}

