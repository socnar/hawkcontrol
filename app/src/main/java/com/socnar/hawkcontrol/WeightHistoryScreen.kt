package com.socnar.hawkcontrol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightHistoryScreen(
    bird: Bird,
    weights: List<WeightEntry>,
    onAddWeight: () -> Unit,
    onShowGraphs: () -> Unit,
    onBack: () -> Unit,
    onWeightEntryClick: (WeightEntry) -> Unit // Nuevo parámetro
) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial de ${bird.nombre}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Button(
                        onClick = onShowGraphs,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text("Gráficas", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWeight,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Peso")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fecha", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Peso", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Comentario", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    when (bird.modalidad) {
                        Modalidad.ALTANERIA -> Text("Altura", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Modalidad.BAJO_VUELO -> Text("Capturas", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Modalidad.VELOCIDAD -> Text("Tiempo", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
            Divider()
            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Más espacio entre filas
            ) {
                items(weights) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.dp, RoundedCornerShape(12.dp))
                            .clickable { onWeightEntryClick(entry) }, // Hace la card clickable
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 8.dp), // Más padding interno
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val fechaCorta = try {
                                val partes = entry.fecha.split("/")
                                if (partes.size == 3) "${partes[0]}/${partes[1]}/${partes[2].takeLast(2)}" else entry.fecha
                            } catch (e: Exception) { entry.fecha }
                            Text(fechaCorta, modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text(entry.pesoAntesVolar?.toString() ?: "", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text(entry.comentario ?: "", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            when (bird.modalidad) {
                                Modalidad.ALTANERIA -> Text(entry.altura?.toString() ?: "", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                Modalidad.BAJO_VUELO -> Text(entry.numCapturas?.toString() ?: "", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                Modalidad.VELOCIDAD -> Text(entry.tiempo?.toString() ?: "", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
