package com.socnar.hawkcontrol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewWeightEntryScreen(
    bird: Bird,
    entry: WeightEntry,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle de registro",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Card(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val fechaCorta = try {
                    val partes = entry.fecha.split("/")
                    if (partes.size == 3) "${partes[0]}/${partes[1]}/${partes[2].takeLast(2)}" else entry.fecha
                } catch (e: Exception) { entry.fecha }
                Text("Fecha: $fechaCorta", fontSize = 16.sp)
                Text("Peso antes de volar: ${entry.pesoAntesVolar ?: "-"} g", fontSize = 16.sp)
                if (entry.tipoVuelo != null) Text("Tipo de vuelo: ${entry.tipoVuelo}", fontSize = 16.sp)
                if (entry.comentario != null) Text("Comentario: ${entry.comentario}", fontSize = 16.sp)
                when (bird.modalidad) {
                    Modalidad.ALTANERIA -> {
                        Text("Altura: ${entry.altura ?: "-"} m", fontSize = 16.sp)
                    }
                    Modalidad.BAJO_VUELO -> {
                        Text("Nº de capturas: ${entry.numCapturas ?: "-"}", fontSize = 16.sp)
                        Text("Nº de lances: ${entry.numLances ?: "-"}", fontSize = 16.sp)
                    }
                    Modalidad.VELOCIDAD -> {
                        Text("Distancia: ${entry.distancia ?: "-"} m", fontSize = 16.sp)
                        Text("Tiempo: ${entry.tiempo ?: "-"} s", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
