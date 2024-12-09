package com.example.sensorsapp.ui.screens

import android.hardware.Sensor
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.sensorsapp.ui.ResultUiState
import com.example.sensorsapp.ui.data.MeasurementViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ResultScreen(
    modifier: Modifier,
    viewModel: MeasurementViewModel
){
    val modelProducer = remember{CartesianChartModelProducer()}
    val resultUiState by viewModel.resultUiState.collectAsState()

    val sensorsList = viewModel.selectedSensors

    LaunchedEffect(Unit){
        withContext(Dispatchers.Default){
            modelProducer.runTransaction {
                lineSeries{ series(resultUiState.timeAxis,resultUiState.yAxis) }
                if(sensorsList.contains(Sensor.STRING_TYPE_GYROSCOPE)){
                    lineSeries { series(resultUiState.gyroAxis,resultUiState.yAxis) }
                }
            }
        }

    }
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(titleComponent = TextComponent(),title = "Gravity"),
            bottomAxis =HorizontalAxis.rememberBottom(titleComponent = TextComponent(),title = "Time"),
        ),
        modelProducer,
        scrollState = rememberVicoScrollState(true, Scroll.Absolute.Start),

    )
}