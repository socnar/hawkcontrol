package com.socnar.hawkcontrol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Dummy data for birds (replace with real data from ViewModel)
data class BirdListItem(val id: String, val nombre: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    birds: List<BirdListItem>,
    onAddBird: () -> Unit,
    onBirdClick: (BirdListItem) -> Unit,
    onSettingsClick: () -> Unit,
    onDeleteBird: (BirdListItem) -> Unit
) {
    var birdToDelete by remember { mutableStateOf<BirdListItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Aves") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBird) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Ave")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(birds) { bird ->
                ListItem(
                    headlineContent = { Text(bird.nombre) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBirdClick(bird) },
                    trailingContent = {
                        IconButton(onClick = { birdToDelete = bird }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar Ave")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
        if (birdToDelete != null) {
            AlertDialog(
                onDismissRequest = { birdToDelete = null },
                title = { Text("Eliminar ave") },
                text = { Text("¿Seguro que deseas eliminar a '${birdToDelete?.nombre}'?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteBird(birdToDelete!!)
                        birdToDelete = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { birdToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
