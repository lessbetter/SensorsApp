package com.example.sensorsapp.ui.screens

import android.hardware.Sensor
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensorsapp.ui.AppViewModelProvider
import com.example.sensorsapp.ui.CreatingChartState
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.example.sensorsapp.ui.data.SetNameViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.HorizontalLegend
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    chartState: CreatingChartState,
    viewModel: MeasurementViewModel,
    modifier: Modifier,
    onSaveButtonClicked: (name: String) -> Unit,
    setNameViewModel: SetNameViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Results")
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                FloatingActionButton(
                    onClick = { setNameViewModel.showBottomSheet() }
                ) {
                    Icon(Icons.Filled.Add,"Add data to database")
                }
            }
        }
    ) {
        innerPadding ->
        when(chartState){
            is CreatingChartState.Success -> ResultScreen(modifier,viewModel,innerPadding)
            is CreatingChartState.Loading -> LoadingScreen(modifier.padding(innerPadding))
            is CreatingChartState.Error -> Unit
        }
        if (setNameViewModel.showBottomSheetState) {
            //setNameViewModel.updateName("")
            ModalBottomSheet(
                onDismissRequest = {
                    setNameViewModel.resetState()
                },
                sheetState = sheetState
            ) {
                OutlinedTextField(
                    value = setNameViewModel.name,
                    onValueChange = { name -> setNameViewModel.updateName(name) },
                    isError = setNameViewModel.isEmpty,
                    label = {
                        if(setNameViewModel.isEmpty){
                            Text("Name cannot be empty")
                        }else{
                            Text("Set a name for this measurement")
                        }
                    }
                    /*...*/
                )
                // Sheet content
                Button(onClick = {
                    if(setNameViewModel.name.isBlank()){
                        setNameViewModel.setError(true)
                    }else{
                        setNameViewModel.setError(false)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                setNameViewModel.resetState()
                            }
                            onSaveButtonClicked.invoke(setNameViewModel.name)
                        }
                    }


                }) {
                    Text("Save data")
                }
            }
        }

    }
}

@Composable
fun ResultScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel,
    contentPadding: PaddingValues
){
    Log.d("Loading: ","Enterred screen")

    val gravModel = viewModel.gravChartModelProducer
    val gyroModel = viewModel.gyroChartModelProducer
    val magneModel = viewModel.magneChartModelProducer
    val acceModel = viewModel.acceChartModelProducer

    val sensorsList = viewModel.selectedSensors

//    LaunchedEffect(Unit){
//        withContext(Dispatchers.Default){
//            modelProducer.runTransaction {
//                lineSeries{ series(resultUiState.timeAxis,resultUiState.gravAxis) }
////                if(sensorsList.contains(Sensor.STRING_TYPE_GYROSCOPE)){
////                    lineSeries { series(resultUiState.gyroAxis,resultUiState.gravAxis) }
////                }
//            }
//        }
//
//    }
    LazyColumn (modifier = modifier.padding(contentPadding)){
        item {
            if(sensorsList.contains(Sensor.TYPE_GRAVITY)){
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Gravity"),
                        bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time",labelRotationDegrees = 45f),
                        //getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    gravModel,
                    scrollState = rememberVicoScrollState(false),
                    zoomState = rememberVicoZoomState(false,Zoom.Content),
                    modifier = Modifier.padding(top = 40.dp),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )}
                )
            }
            if(sensorsList.contains(Sensor.TYPE_GYROSCOPE)){
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Tilt"),
                        bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    gyroModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )}

                )
            }
            if(sensorsList.contains(Sensor.TYPE_MAGNETIC_FIELD)){
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Geomagnetic field strength"),
                        bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    magneModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )}

                )
            }
            if(sensorsList.contains(Sensor.TYPE_ACCELEROMETER)){
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Red))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Blue))),
                                LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(Color.Green))),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Acceleration"),
                        bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    acceModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )}

                )
            }
        }

    }

}

@Composable
private fun horizontalLegend(): HorizontalLegend<CartesianMeasuringContext, CartesianDrawingContext> =
    rememberHorizontalLegend(
        items = {
            add(
                element = LegendItem(
                    icon = ShapeComponent(fill = fill(Color.Red)),
                    labelComponent = TextComponent(),
                    label = "X Axis"
                )
            )
            add(
                element = LegendItem(
                    icon = ShapeComponent(fill = fill(Color.Blue)),
                    labelComponent = TextComponent(),
                    label = "Y Axis"
                )
            )
            add(
                element = LegendItem(
                    icon = ShapeComponent(fill = fill(Color.Green)),
                    labelComponent = TextComponent(),
                    label = "Z Axis"
                )
            )
        },
        padding = Insets(startDp = 10f)
    )

@Composable
fun LoadingScreen(modifier: Modifier){
    ConstraintLayout(modifier = modifier){
        val loader = createRef()
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .constrainAs(loader) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}