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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andersen.iprospekta.R
import com.andersen.iprospekta.license.LicenseManager
import com.andersen.iprospekta.license.LicenseState

@Composable
fun ActivationScreen(
    licenseManager: LicenseManager,
    lastState: LicenseState,
    onActivated: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf(messageFor(lastState)) }
    var isError by remember { mutableStateOf(lastState !is LicenseState.NotActivated) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo_iprospekta),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text("I-Prospekta", style = MaterialTheme.typography.titleLarge)
        Text(
            "Prospection géologique de terrain",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        message?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    it,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Code d'activation") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                when (val st = licenseManager.activate(code)) {
                    is LicenseState.Active -> onActivated()
                    else -> {
                        isError = true
                        message = messageFor(st)
                    }
                }
            },
            enabled = code.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Activer la licence")
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Licence mensuelle. Contactez votre administrateur pour obtenir un code.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun messageFor(state: LicenseState): String? = when (state) {
    is LicenseState.NotActivated -> "Saisissez votre code d'activation pour démarrer."
    is LicenseState.Expired -> "Licence expirée le ${state.expiry}. Saisissez un nouveau code."
    is LicenseState.Invalid -> "Code invalide. Vérifiez la saisie."
    is LicenseState.ClockTampered -> "Horloge incohérente détectée. Revalidez votre licence."
    is LicenseState.Active -> null
}
