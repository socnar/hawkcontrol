package com.socnar.hawkcontrol

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryScreen(
    bird: Bird,
    weights: List<WeightEntry>,
    onAddWeight: () -> Unit,
    onShowGraphs: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de ${bird.nombre}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Button(onClick = onShowGraphs) { Text("Gráficas") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddWeight) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Peso")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fecha", modifier = Modifier.weight(1f))
                Text("Peso", modifier = Modifier.weight(1f))
                Text("Comentario", modifier = Modifier.weight(1f))
                when (bird.modalidad) {
                    Modalidad.ALTANERIA -> Text("Altura", modifier = Modifier.weight(1f))
                    Modalidad.BAJO_VUELO -> Text("Capturas", modifier = Modifier.weight(1f))
                    Modalidad.VELOCIDAD -> Text("Tiempo", modifier = Modifier.weight(1f))
                }
            }
            Divider()
            LazyColumn(Modifier.weight(1f)) {
                items(weights) { entry ->
                    Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(entry.fecha, modifier = Modifier.weight(1f))
                        Text(entry.pesoAntesVolar?.toString() ?: "", modifier = Modifier.weight(1f))
                        Text(entry.comentario ?: "", modifier = Modifier.weight(1f))
                        when (bird.modalidad) {
                            Modalidad.ALTANERIA -> Text(entry.altura?.toString() ?: "", modifier = Modifier.weight(1f))
                            Modalidad.BAJO_VUELO -> Text(entry.numCapturas?.toString() ?: "", modifier = Modifier.weight(1f))
                            Modalidad.VELOCIDAD -> Text(entry.tiempo?.toString() ?: "", modifier = Modifier.weight(1f))
                        }
                    }
                    Divider()
                }
            }
        }
    }
}
