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
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
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
                title = { Text("Listado de Aves", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)) },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBird,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Ave")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(birds) { bird ->
                val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val cardColor = if (isDark) com.socnar.hawkcontrol.ui.theme.LighterGray else MaterialTheme.colorScheme.surface
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .shadow(1.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    ListItem(
                        headlineContent = { Text(bird.nombre, fontWeight = FontWeight.Bold) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBirdClick(bird) },
                        trailingContent = {
                            IconButton(onClick = { birdToDelete = bird }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar Ave")
                            }
                        }
                    )
                }
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
