package com.andersen.iprospekta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.andersen.iprospekta.BuildConfig
import com.andersen.iprospekta.R

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_iprospekta),
            contentDescription = null,
            modifier = Modifier.size(110.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text("I-Prospekta", style = MaterialTheme.typography.titleLarge)
        Text(
            "Application professionnelle de prospection géologique",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                InfoLine("Développeur", "Andersen")
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
                InfoLine("Téléphone", "034 16 894 03")
                HorizontalDivider(Modifier.padding(vertical = 10.dp))
                InfoLine("Version", "${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})")
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}
