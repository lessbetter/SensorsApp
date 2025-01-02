package com.example.sensorsapp.ui.screens.measurement

import android.hardware.Sensor
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensorsapp.NavigationDestination
import com.example.sensorsapp.R
import com.example.sensorsapp.ui.AppViewModelProvider
import com.example.sensorsapp.ui.CreatingChartState
import com.example.sensorsapp.ui.room.Measurement
import com.example.sensorsapp.ui.screens.LoadingScreen
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
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
import com.patrykandpatrick.vico.core.common.HorizontalLegend
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object MeasurementDetailsDestination : NavigationDestination {
    override val route = "item_details"
    override val titleRes = R.string.measurement_detail_title
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MeasurementDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val chartState = viewModel.creatingChartState



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
                if (uiState.measurementDetails.date.isBlank()) {
                    Text("")
                } else {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    val dateTime = LocalDateTime.parse(uiState.measurementDetails.date)
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Measured: ${dateTime.format(formatter)}"
                    )
                }
            }
        }
    ) { innerPadding ->
        MeasurementDetailsBody(
            uiState.measurementDetails,
            modifier,
            innerPadding,
            chartState,
            viewModel,
            uiState
        )


    }

}

@Composable
fun MeasurementDetailsBody(
    measurement: Measurement,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    chartState: CreatingChartState,
    viewModel: MeasurementDetailsViewModel,
    uiState: MeasurementDetailsUiState
) {
//    if (measurement.title.isBlank()) {
//        Text(
//            text = "stringResource(R.string.no_item_description)",
//            textAlign = TextAlign.Center,
//            style = MaterialTheme.typography.titleLarge,
//        )
//    } else {
        viewModel.toAxis()
        when (chartState) {
            is CreatingChartState.Success -> ResultScreen(
                modifier,
                viewModel,
                contentPadding,
                uiState
            )

            is CreatingChartState.Loading -> LoadingScreen(modifier.padding(contentPadding))
            is CreatingChartState.Error -> Unit
        }
    //}

}

@Composable
fun ResultScreen(
    modifier: Modifier,
    viewModel: MeasurementDetailsViewModel,
    contentPadding: PaddingValues,
    uiState: MeasurementDetailsUiState
) {


    val gravModel = viewModel.gravChartModelProducer
    val gyroModel = viewModel.gyroChartModelProducer
    val magneModel = viewModel.magneChartModelProducer
    val acceModel = viewModel.acceChartModelProducer

    val sensorsList = uiState.measurementDetails.data.listOfSensors

    LazyColumn(modifier = modifier.padding(contentPadding)) {
        item {
            if (sensorsList.contains(Sensor.TYPE_GRAVITY)) {
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Red)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Blue)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Green)
                                    )
                                ),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(
                            titleComponent = TextComponent(),
                            title = "Gravity"
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time"
                        ),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    gravModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    modifier = Modifier.padding(top = 40.dp),
                    placeholder = {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                )
            }
            if (sensorsList.contains(Sensor.TYPE_GYROSCOPE)) {
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Red)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Blue)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Green)
                                    )
                                ),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(
                            titleComponent = TextComponent(),
                            title = "Tilt"
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time"
                        ),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    gyroModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    placeholder = {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }

                )
            }
            if (sensorsList.contains(Sensor.TYPE_MAGNETIC_FIELD)) {
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Red)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Blue)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Green)
                                    )
                                ),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(
                            titleComponent = TextComponent(),
                            title = "Geomagnetic field strength"
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time"
                        ),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    magneModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    placeholder = {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }

                )
            }
            if (sensorsList.contains(Sensor.TYPE_ACCELEROMETER)) {
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Red)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Blue)
                                    )
                                ),
                                LineCartesianLayer.rememberLine(
                                    fill = LineCartesianLayer.LineFill.single(
                                        fill(Color.Green)
                                    )
                                ),
                            )
                        ),
                        startAxis = VerticalAxis.rememberStart(
                            titleComponent = TextComponent(),
                            title = "Acceleration"
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            titleComponent = TextComponent(),
                            title = "Time"
                        ),
                        getXStep = { 0.5 },
                        legend = horizontalLegend()
                    ),
                    acceModel,
                    scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),
                    zoomState = rememberVicoZoomState(false, initialZoom = Zoom.x(100.0)),
                    placeholder = {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }

                )
            }
        }

    }

}

@Composable
fun LoadingScreen(modifier: Modifier) {
    ConstraintLayout(modifier = modifier) {
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