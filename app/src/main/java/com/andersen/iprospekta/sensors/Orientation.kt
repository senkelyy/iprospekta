package com.andersen.iprospekta.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos

/**
 * Orientation de l'appareil, en degres.
 * - [azimuth]  : cap magnetique 0-360 (boussole)
 * - [pitch]    : tangage (inclinaison avant/arriere)
 * - [roll]     : roulis (inclinaison gauche/droite)
 * - [tilt]     : inclinaison totale par rapport a l'horizontale (clinometre)
 */
data class Orientation(
    val azimuth: Float = 0f,
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val available: Boolean = true
) {
    val tilt: Float
        get() {
            val p = Math.toRadians(pitch.toDouble())
            val r = Math.toRadians(roll.toDouble())
            val c = (cos(p) * cos(r)).coerceIn(-1.0, 1.0)
            return abs(Math.toDegrees(acos(c))).toFloat()
        }

    val cardinal: String
        get() = DIRECTIONS[(((azimuth + 22.5f) % 360f) / 45f).toInt().coerceIn(0, 7)]

    companion object {
        private val DIRECTIONS = arrayOf("N", "NE", "E", "SE", "S", "SO", "O", "NO")
    }
}

/**
 * Fournit l'orientation en temps reel via le capteur de vecteur de rotation.
 */
@Composable
fun rememberOrientation(): State<Orientation> {
    val context = LocalContext.current
    val state = remember { mutableStateOf(Orientation()) }

    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensor == null) {
            state.value = Orientation(available = false)
        }
        val rotation = FloatArray(9)
        val orientation = FloatArray(3)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotation, event.values)
                SensorManager.getOrientation(rotation, orientation)
                val az = ((Math.toDegrees(orientation[0].toDouble()).toFloat()) + 360f) % 360f
                state.value = Orientation(
                    azimuth = az,
                    pitch = Math.toDegrees(orientation[1].toDouble()).toFloat(),
                    roll = Math.toDegrees(orientation[2].toDouble()).toFloat(),
                    available = true
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (sensor != null) {
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sm.unregisterListener(listener) }
    }
    return state
}
