package com.andersen.iprospekta

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.andersen.iprospekta.data.PointRepository
import com.andersen.iprospekta.license.LicenseManager
import com.andersen.iprospekta.license.LicenseState
import com.andersen.iprospekta.ui.screens.AboutScreen
import com.andersen.iprospekta.ui.screens.ActivationScreen
import com.andersen.iprospekta.ui.screens.CompassScreen
import com.andersen.iprospekta.ui.screens.FieldbookScreen
import com.andersen.iprospekta.ui.screens.MapScreen
import com.andersen.iprospekta.ui.theme.IProspektaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IProspektaTheme {
                AppRoot()
            }
        }
    }
}

private enum class Tab(val label: String, val icon: ImageVector) {
    MAP("Carte", Icons.Filled.Map),
    FIELDBOOK("Carnet", Icons.Filled.MenuBook),
    COMPASS("Boussole", Icons.Filled.Explore),
    ABOUT("À propos", Icons.Filled.Info)
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    val licenseManager = remember { LicenseManager(context) }
    var licenseState by remember { mutableStateOf<LicenseState>(licenseManager.currentState()) }

    when (val st = licenseState) {
        is LicenseState.Active -> MainApp(st, context)
        else -> ActivationScreen(
            licenseManager = licenseManager,
            lastState = st,
            onActivated = { licenseState = licenseManager.currentState() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainApp(active: LicenseState.Active, context: Context) {
    val repo = remember { PointRepository(context) }
    var points by remember { mutableStateOf(repo.load()) }
    var tab by rememberSaveable { mutableStateOf(Tab.MAP) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("I-Prospekta") },
                actions = { ExpiryChip(active.daysLeft) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                Tab.MAP -> MapScreen(points = points, onAddPoint = { points = repo.add(it) })
                Tab.FIELDBOOK -> FieldbookScreen(
                    points = points,
                    onUpdate = { points = repo.update(it) },
                    onDelete = { points = repo.delete(it) }
                )
                Tab.COMPASS -> CompassScreen()
                Tab.ABOUT -> AboutScreen()
            }
        }
    }
}

@Composable
private fun ExpiryChip(daysLeft: Long) {
    val warn = daysLeft <= 5
    AssistChip(
        onClick = {},
        label = { Text(if (daysLeft >= 0) "$daysLeft j" else "expirée") },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = if (warn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary,
            containerColor = if (warn) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
        )
    )
}
