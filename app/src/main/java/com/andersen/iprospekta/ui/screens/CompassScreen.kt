package com.andersen.iprospekta.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andersen.iprospekta.sensors.rememberOrientation
import com.andersen.iprospekta.ui.theme.IpGreen500
import com.andersen.iprospekta.ui.theme.IpGrey300
import com.andersen.iprospekta.ui.theme.IpOrange500
import com.andersen.iprospekta.ui.theme.IpWhite
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun CompassScreen() {
    val orientation by rememberOrientation()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!orientation.available) {
            Text(
                "Capteur d'orientation indisponible sur cet appareil.",
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(16.dp))
        }

        Canvas(modifier = Modifier.size(260.dp)) {
            val r = min(size.width, size.height) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Cadran
            drawCircle(color = IpGrey300, radius = r, center = center, style = Stroke(width = 6f))
            drawCircle(color = IpGreen500, radius = 10f, center = center)

            // Rose des vents (rotation inverse du cap)
            rotate(degrees = -orientation.azimuth, pivot = center) {
                // Aiguille Nord (orange)
                val north = Path().apply {
                    moveTo(center.x, center.y - r + 18f)
                    lineTo(center.x - 22f, center.y)
                    lineTo(center.x + 22f, center.y)
                    close()
                }
                drawPath(north, color = IpOrange500)
                // Aiguille Sud (blanc)
                val south = Path().apply {
                    moveTo(center.x, center.y + r - 18f)
                    lineTo(center.x - 22f, center.y)
                    lineTo(center.x + 22f, center.y)
                    close()
                }
                drawPath(south, color = IpWhite)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "${orientation.azimuth.roundToInt()}°  ${orientation.cardinal}",
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Cap magnétique",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Clinomètre — inclinaison : ${orientation.tilt.roundToInt()}°",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            "Tangage ${orientation.pitch.roundToInt()}°   ·   Roulis ${orientation.roll.roundToInt()}°",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
