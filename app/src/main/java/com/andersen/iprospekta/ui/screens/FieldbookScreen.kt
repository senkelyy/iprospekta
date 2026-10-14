package com.andersen.iprospekta.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.andersen.iprospekta.data.FieldPoint

@Composable
fun FieldbookScreen(
    points: List<FieldPoint>,
    onUpdate: (FieldPoint) -> Unit,
    onDelete: (Long) -> Unit
) {
    var editing by remember { mutableStateOf<FieldPoint?>(null) }

    if (points.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Aucun point.\nAjoutez-en depuis la carte (bouton + ou appui long).",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(points, key = { it.id }) { p ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "%.5f, %.5f  ·  %.0f m".format(p.latitude, p.longitude, p.altitude),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (p.lithologie.isNotBlank()) Text("Lithologie : ${p.lithologie}")
                        if (p.pendage != null && p.direction != null) {
                            Text(
                                "Structure : ${p.direction}° / ${p.pendage}°",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    IconButton(onClick = { editing = p }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Éditer")
                    }
                    IconButton(onClick = { onDelete(p.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Supprimer")
                    }
                }
            }
        }
    }

    editing?.let { point ->
        EditPointDialog(
            point = point,
            onDismiss = { editing = null },
            onSave = { onUpdate(it); editing = null }
        )
    }
}

@Composable
private fun EditPointDialog(
    point: FieldPoint,
    onDismiss: () -> Unit,
    onSave: (FieldPoint) -> Unit
) {
    var name by remember { mutableStateOf(point.name) }
    var lithologie by remember { mutableStateOf(point.lithologie) }
    var pendage by remember { mutableStateOf(point.pendage?.toString() ?: "") }
    var direction by remember { mutableStateOf(point.direction?.toString() ?: "") }
    var note by remember { mutableStateOf(point.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fiche d'affleurement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Nom") }, singleLine = true)
                OutlinedTextField(lithologie, { lithologie = it }, label = { Text("Lithologie") }, singleLine = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        direction, { direction = it.filter(Char::isDigit).take(3) },
                        label = { Text("Direction °") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        pendage, { pendage = it.filter(Char::isDigit).take(2) },
                        label = { Text("Pendage °") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(note, { note = it }, label = { Text("Note") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    point.copy(
                        name = name.ifBlank { point.name },
                        lithologie = lithologie,
                        pendage = pendage.toIntOrNull()?.coerceIn(0, 90),
                        direction = direction.toIntOrNull()?.coerceIn(0, 360),
                        note = note
                    )
                )
            }) { Text("Enregistrer") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
