package com.example.sensorsapp.ui


import android.hardware.Sensor
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.sensorsapp.ui.sensors.MySensorClass
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.seconds


data class DataFromSensor(
    var timestamp: Long = 0,
    var v0: Float = 0F,
//    val v1: Float,
//    val v2: Float
)

class MeasurementViewModel : ViewModel() {
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


    private var mSensor: Sensor? = null

    private var readDelay: Long = 500000000

    private var isRunning: Boolean = false




    fun startMeasuring(){
        if(::sensorManager.isInitialized && mSensor!=null){
            if(isRunning==false){

                collectedGravityData.clear()
                collectedMagneticData.clear()
                collectedGyroscopeData.clear()

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
            //mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

            sensorGravity = MySensorClass(ctx,Sensor.TYPE_GRAVITY,{values,timestamp ->
                gravityData.v0 = values[0]
                gravityData.timestamp = timestamp
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
            sensorGyroscope = MySensorClass(ctx,Sensor.TYPE_GYROSCOPE, { values, timestamp ->
                gyroscopeData.v0 = values[0]
                gyroscopeData.timestamp = timestamp
            })
            deviceSensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!)
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            deviceSensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!)
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
            deviceSensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!)

            sensorMagnetic = MySensorClass(ctx,Sensor.TYPE_MAGNETIC_FIELD,{values, timestamp ->
                magneticData.v0 = values[0]
                magneticData.timestamp = timestamp
            })
        }
    }

}

