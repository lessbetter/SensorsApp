package com.example.sensorsapp.ui.data


import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensorsapp.ui.CreatingChartState
import com.example.sensorsapp.ui.MeasurementUiState
import com.example.sensorsapp.ui.ResultUiState
import com.example.sensorsapp.ui.SensorsUiState
import com.example.sensorsapp.ui.room.Measurement
import com.example.sensorsapp.ui.room.MeasurementsRepository
import com.example.sensorsapp.ui.sensors.MySensorClass
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource
import kotlin.time.toDuration

class MeasurementViewModel(private val measurementsRepository: MeasurementsRepository) : ViewModel() {

    var creatingChartState: CreatingChartState by mutableStateOf(CreatingChartState.Loading)
        private set

    private val _measurementUiState = MutableStateFlow(MeasurementUiState())
    val measurementUiState: StateFlow<MeasurementUiState> = _measurementUiState.asStateFlow()

    private val _resultUiState = MutableStateFlow(ResultUiState())
    val resultUiState: StateFlow<ResultUiState> = _resultUiState.asStateFlow()

    private val _sensorsUiState = MutableStateFlow(SensorsUiState())
    val sensorsUiState: StateFlow<SensorsUiState> = _sensorsUiState.asStateFlow()

    private lateinit var sensorManager: SensorManager

    private lateinit var sensorGravity: MySensorClass
    private lateinit var sensorGyroscope: MySensorClass
    private lateinit var sensorMagnetic: MySensorClass
    private lateinit var sensorAccelerometer: MySensorClass

    private var gravityData: DataFromSensor = DataFromSensor()
    private var gyroscopeData: DataFromSensor = DataFromSensor()
    private var magneticData: DataFromSensor = DataFromSensor()
    private var accelerometerData: DataFromSensor = DataFromSensor()

    var collectedGravityData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedGyroscopeData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedMagneticData: MutableList<DataFromSensor> = mutableListOf()
    private var collectedAccelerometerData: MutableList<DataFromSensor> = mutableListOf()

    var listOfSensors: MutableList<Sensors> = mutableListOf()

    var selectedSensors: MutableList<Int> = mutableListOf()

    val myStopWatch: StopWatch = StopWatch(this)

    var gravChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    var gyroChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    var magneChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()
    var acceChartModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    var collectedData: MeasurementData = MeasurementData()

    var timeOfMeasurement: String = ""


    private var mSensor: Sensor? = null

//    private var readDelay: Long = 500000000

    private var isRunning: Boolean = false

    var hasStarted: Boolean = false

    val timeSource = TimeSource.Monotonic

    var timeAdjustment: TimeSource.Monotonic.ValueTimeMark = timeSource.markNow()

    var stopMark: TimeSource.Monotonic.ValueTimeMark = timeSource.markNow()

    var pauseDuration: Duration = timeSource.markNow().minus(timeAdjustment)


    var hasRestarted = false

    fun getSensors(ctx: Context){
        sensorManager = ctx.getSystemService(SENSOR_SERVICE) as SensorManager


        if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)!=null){
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            listOfSensors.add(Sensors(sensorType = Sensor.TYPE_GRAVITY))

            sensorGravity = MySensorClass(ctx,Sensor.TYPE_GRAVITY) { values ->
                gravityData.values = values
            }
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!=null){
            listOfSensors.add(Sensors(sensorType = Sensor.TYPE_GYROSCOPE))

            sensorGyroscope = MySensorClass(ctx,Sensor.TYPE_GYROSCOPE, { values->
                gyroscopeData.values = values
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!=null){
            listOfSensors.add(Sensors(sensorType = Sensor.TYPE_MAGNETIC_FIELD))

            sensorMagnetic = MySensorClass(ctx,Sensor.TYPE_MAGNETIC_FIELD,{values ->
                magneticData.values = values
            })
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!=null){
            listOfSensors.add(Sensors(sensorType = Sensor.TYPE_ACCELEROMETER))

            sensorAccelerometer = MySensorClass(ctx,Sensor.TYPE_ACCELEROMETER,{values ->
                accelerometerData.values = values
            })
        }


        _sensorsUiState.update { currentState ->
            currentState.copy(
                listOfSensors = listOfSensors
            )
        }
    }


    fun startMeasuring(){
        if(::sensorManager.isInitialized ){
            if(!isRunning){

                _measurementUiState.update { currentState ->
                    currentState.copy(
                        isRunning = true
                    )
                }

                if(selectedSensors.contains(Sensor.TYPE_GYROSCOPE))sensorGyroscope.startListening()
                if(selectedSensors.contains(Sensor.TYPE_GRAVITY))sensorGravity.startListening()
                if(selectedSensors.contains(Sensor.TYPE_MAGNETIC_FIELD))sensorMagnetic.startListening()
                if(selectedSensors.contains(Sensor.TYPE_ACCELEROMETER))sensorAccelerometer.startListening()


                isRunning=true

                viewModelScope.launch {
                    if(selectedSensors.contains(Sensor.TYPE_GYROSCOPE)){
                        sensorGyroscope.startListening()
                        while (gyroscopeData.values==null){
                            delay(1)
                        }
                    }
                    if(selectedSensors.contains(Sensor.TYPE_GRAVITY)) {
                        sensorGravity.startListening()
                        while (gravityData.values==null){
                            delay(1)
                        }
                    }
                    if(selectedSensors.contains(Sensor.TYPE_MAGNETIC_FIELD)) {
                        sensorMagnetic.startListening()
                        while (magneticData.values==null){
                            delay(1)
                        }
                    }
                    if(selectedSensors.contains(Sensor.TYPE_ACCELEROMETER)) {
                        sensorAccelerometer.startListening()
                        while (accelerometerData.values==null){
                            delay(1)
                        }
                    }
                    if(!hasStarted){
                        hasStarted = true
                        resetData()
                        timeAdjustment = timeSource.markNow()
                    }else{
                        hasRestarted = true
                        pauseDuration += timeSource.markNow().minus(stopMark)
                    }
                    readData()

                }
                myStopWatch.start()
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

                sensorGyroscope.stopListening()
                sensorGravity.stopListening()
                sensorMagnetic.stopListening()
                sensorAccelerometer.startListening()
            }

        }else
            return
    }

    fun stopRunning(){
        if(hasStarted==true){
            hasStarted = false
            hasRestarted = false
            pauseDuration = 0.toDuration(DurationUnit.NANOSECONDS)

            isRunning = false
            _measurementUiState.update { currentState ->
                currentState.copy(
                    isRunning = false
                )
            }
            myStopWatch.reset()

            sensorGyroscope.stopListening()
            sensorGravity.stopListening()
            sensorMagnetic.stopListening()
            sensorAccelerometer.stopListening()
            collectedGravityData.clear()
            collectedMagneticData.clear()
            collectedGyroscopeData.clear()
            collectedAccelerometerData.clear()
        }
    }

    suspend fun readData(){

        while(isRunning){

            var timeMark = timeSource.markNow().minus(timeAdjustment)
            if(hasRestarted){
                timeMark -= pauseDuration
            }
            collectedGravityData.add(DataFromSensor(timeMark,gravityData.sensorType,gravityData.values))
            collectedMagneticData.add(DataFromSensor(timeMark,magneticData.sensorType,magneticData.values))
            collectedGyroscopeData.add(DataFromSensor(timeMark,gyroscopeData.sensorType,gyroscopeData.values))
            collectedAccelerometerData.add(DataFromSensor(timeMark,accelerometerData.sensorType,accelerometerData.values))


            delay(500)
        }
    }





    fun onCheckedUpdate(type: Int,selected: Boolean){
//        var tempList = sensorsUiState.value.listOfSensors
        when(type){
            Sensor.TYPE_GRAVITY -> _sensorsUiState.update { currentState -> currentState.copy(isGravityChecked = selected) }
            Sensor.TYPE_GYROSCOPE -> _sensorsUiState.update { it.copy(isGyroscopeChecked = selected) }
            Sensor.TYPE_MAGNETIC_FIELD -> _sensorsUiState.update { it.copy(isMagneticChecked = selected) }
            Sensor.TYPE_ACCELEROMETER -> _sensorsUiState.update { it.copy(isAccelerometerChecked = selected) }
        }
    }

    fun setLocalList(){
        selectedSensors.clear()
        if(sensorsUiState.value.isGravityChecked) selectedSensors.add(Sensor.TYPE_GRAVITY)
        if(sensorsUiState.value.isGyroscopeChecked) selectedSensors.add(Sensor.TYPE_GYROSCOPE)
        if(sensorsUiState.value.isMagneticChecked) selectedSensors.add(Sensor.TYPE_MAGNETIC_FIELD)
        if(sensorsUiState.value.isAccelerometerChecked) selectedSensors.add(Sensor.TYPE_ACCELEROMETER)

    }

    fun toAxis(){

        val defaultDispatcher = Dispatchers.Default
        viewModelScope.launch(defaultDispatcher) {
            collectedData = MeasurementData()
            val gravData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_GRAVITY)
            val gyroData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_GYROSCOPE)
            val magneData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_MAGNETIC_FIELD)
            val acceData: MutableList<List<Float>> = populateDataList(Sensor.TYPE_ACCELEROMETER)
            val timeList: MutableList<Float> = mutableListOf()

            for (value in collectedGravityData){
                val rounded = value.timeMark!!.toDouble(DurationUnit.SECONDS).toBigDecimal().setScale(4,RoundingMode.UP).toFloat()
                timeList.add(rounded)
            }
            collectedData.timeTable.addAll(timeList)
            creatingChartState = CreatingChartState.Loading
            viewModelScope.launch(defaultDispatcher) {

                val job1=viewModelScope.launch(defaultDispatcher){
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
                            Log.d("creatingChart: ","finished")
                        }
                        delay(2000L)
                    }

                }
                job1.join()
                job2.join()
                job3.join()
                job4.join()
                //delay(2000L)
            }.join()
            creatingChartState = CreatingChartState.Success
            //this.coroutineContext.job.invokeOnCompletion{
            stopRunning()
            //}
            Log.d("Loading: ","finished")
            //creatingChartState = CreatingChartState.Success

        }



    }

    fun saveTime(){
        timeOfMeasurement = LocalDateTime.now().toString()
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

    private fun populateDataList(sensorType: Int): MutableList<List<Float>>{
        val listOfData: MutableList<List<Float>> = mutableListOf()
        val sensorData = SensorData()
        when(sensorType){
            Sensor.TYPE_GRAVITY ->{
                if(selectedSensors.contains(sensorType)){
                    sensorData.sensorType = sensorType
                    for (value in collectedGravityData){
                        listOfData.add(value.values!!)
                    }
                }
            }
            Sensor.TYPE_GYROSCOPE ->{
                if(selectedSensors.contains(sensorType)){
                    sensorData.sensorType = sensorType
                    for (value in collectedGyroscopeData){
                        listOfData.add(value.values!!)
                    }
                }
            }
            Sensor.TYPE_MAGNETIC_FIELD ->{
                if(selectedSensors.contains(sensorType)){
                    sensorData.sensorType = sensorType
                    for (value in collectedMagneticData){
                        listOfData.add(value.values!!)
                    }
                }
            }
            Sensor.TYPE_ACCELEROMETER ->{
                if(selectedSensors.contains(sensorType)){
                    sensorData.sensorType = sensorType
                    for (value in collectedAccelerometerData){
                        listOfData.add(value.values!!)
                    }
                }
            }
        }
        sensorData.values.addAll(listOfData)
        if(sensorData.sensorType!=-2){
            collectedData.sensorsData.add(SensorData(sensorData.sensorType,sensorData.values))
        }
        return listOfData
    }

    private fun validateData(): Boolean {
        val localList = collectedData.sensorsData
        for(value in localList){
            if(value.sensorType==-2){
                localList.remove(value)
            }
        }
        return localList.isNotEmpty()
    }

    suspend fun saveData(name: String){
        if(validateData()){
            collectedData.listOfSensors.addAll(selectedSensors)
            measurementsRepository.insertMeasurement(Measurement(title = name, data = collectedData, date = timeOfMeasurement))
        }
    }

    fun resetSelectedSensors(){
        _sensorsUiState.update { currentState ->
            currentState.copy(
                isGravityChecked = false,
                isGyroscopeChecked = false,
                isMagneticChecked = false,
                isAccelerometerChecked = false
            )
        }
    }

}

