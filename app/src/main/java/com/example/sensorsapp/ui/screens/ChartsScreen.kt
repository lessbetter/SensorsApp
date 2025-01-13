package com.example.sensorsapp.ui.screens

import android.content.Intent
import android.hardware.Sensor
import android.net.Uri
import android.provider.DocumentsContract
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
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
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.fixed
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shadow
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.HorizontalLegend
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    FloatingActionButton(
                        onClick = { setNameViewModel.showBottomSheet() }

                    ) {
                        Icon(Icons.Filled.Add,"Add data to database")
                    }
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
                sheetState = sheetState,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
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
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
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
                        Text("Save to database")
                    }
//                    Button(onClick = {
//                        if(setNameViewModel.name.isBlank()){
//                            setNameViewModel.setError(true)
//                        }else{
//                            setNameViewModel.setError(false)
//                            scope.launch { sheetState.hide() }.invokeOnCompletion {
//                                if (!sheetState.isVisible) {
//                                    setNameViewModel.resetState()
//                                }
//                                viewModel.saveToFile(setNameViewModel.name)
//                            }
//                        }
//
//
//                    }) {
//                        Text("Save to csv file")
//                    }
                }
                // Sheet content

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
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

    val sensorsList = viewModel.selectedSensors

    val labelBackgroundShape = markerCorneredShape(CorneredShape.Corner.Rounded)
    val labelBackground =
        rememberShapeComponent(
            fill = fill(MaterialTheme.colorScheme.surfaceContainer),
            shape = labelBackgroundShape,
            shadow =
            shadow(radius = 4f.dp, y = 2f.dp),
        )
    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            padding = insets(8.dp, 4.dp),
            background = labelBackground,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )

    LazyColumn (modifier = modifier.padding(contentPadding)){
        item {
            if(sensorsList.contains(Sensor.TYPE_GRAVITY)){
                Log.d("Drawing: ","started")
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
                        bottomAxis =HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time",
                            valueFormatter = {_,value,_ ->
                                LocalTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault()).format(formatter)
                            }),
                        getXStep = { 500.0 },
                        legend = horizontalLegend(),
                        marker = DefaultCartesianMarker(label)
                    ),
                    gravModel,
                    scrollState = rememberVicoScrollState(true),
                    zoomState = rememberVicoZoomState(true),
                    modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )},

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
                        bottomAxis =HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time",
                            valueFormatter = {_,value,_ ->
                                LocalTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault()).format(formatter)
                            }),
                        getXStep = { 500.0 },
                        legend = horizontalLegend(),
                        marker = DefaultCartesianMarker(label)
                    ),
                    gyroModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(true),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )},
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                        bottomAxis =HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time",
                            valueFormatter = {_,value,_ ->
                            LocalTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault()).format(formatter)
                        }),
                        getXStep = { 500.0 },
                        legend = horizontalLegend(),
                        marker = DefaultCartesianMarker(label)
                    ),
                    magneModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(true),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )},
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                        bottomAxis =HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time",
                            valueFormatter = {_,value,_ ->
                                LocalTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault()).format(formatter)
                            }),
                        getXStep = { 500.0 },
                        legend = horizontalLegend(),
                        marker = DefaultCartesianMarker(label)
                    ),
                    acceModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(true),
                    placeholder = {CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )},
                    modifier = Modifier.padding(horizontal = 16.dp)
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