package com.example.sensorsapp.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_NORMAL


class MySensorClass(
    private val context: Context,
//    private val sensorFeature: String,
    private val sensorType: Int,
    private val sendData: (List<Float>) -> Unit
) : SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null

    fun startListening() {
        if(!::mSensorManager.isInitialized && mSensor == null){
            mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            mSensor = mSensorManager.getDefaultSensor(sensorType)
        }
        mSensor?.let{
            mSensorManager.registerListener(this, mSensor, SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        if(::mSensorManager.isInitialized && mSensor != null){
            mSensorManager.unregisterListener(this)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent) {
        //val value = event.values[0]
        sendData(event.values.toList())
    }
}