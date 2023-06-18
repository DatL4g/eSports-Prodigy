package dev.datlag.esports.prodigy.ui.screen.home.info.device.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.netguru.multiplatform.charts.ChartAnimation
import com.netguru.multiplatform.charts.line.LineChart
import com.netguru.multiplatform.charts.line.LineChartData
import com.netguru.multiplatform.charts.line.LineChartPoint
import com.netguru.multiplatform.charts.line.LineChartSeries
import dev.datlag.esports.prodigy.model.common.asList
import dev.datlag.esports.prodigy.ui.theme.SchemeTheme

@Composable
fun UnsupportedGameChart(modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().height(300.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LineChart(
                lineChartData = LineChartData(
                    series = LineChartSeries(
                        dataName = "Unknown Data",
                        lineColor = SchemeTheme.colorScheme.primary,
                        listOfPoints = (0..10).map { point ->
                            LineChartPoint(
                                x = point.toLong(),
                                y = (1..15).random().toFloat()
                            )
                        }
                    ).asList()
                ),
                modifier = Modifier.fillMaxSize(),
                animation = ChartAnimation.Disabled,
                xAxisLabel = { },
                yAxisLabel = { },
                overlayHeaderLabel = { },
                overlayEnabled = false
            )
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5F)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "User Statistics",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "currently not supported",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = {
                            onClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = "Learn more",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}