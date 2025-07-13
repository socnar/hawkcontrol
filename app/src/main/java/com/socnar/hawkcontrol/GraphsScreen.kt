package com.socnar.hawkcontrol

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.IntOffset
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphsScreen(
    bird: Bird,
    weights: List<WeightEntry>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gráficas de ${bird.nombre}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Peso vs Fecha (todas las modalidades)
            Text("Peso vs Fecha", style = MaterialTheme.typography.titleMedium)
            val pesoPoints = weights.takeLast(180).mapIndexed { idx, entry ->
                entry.pesoAntesVolar ?: 0f
            }
            SimpleLineChart(
                points = pesoPoints,
                modifier = Modifier.height(180.dp).fillMaxWidth(),
                ejeYLabel = "Peso (g)",
                ejeXLabel = "Fecha"
            )
            when (bird.modalidad) {
                Modalidad.ALTANERIA -> {
                    val alturaPoints = weights.takeLast(180).mapIndexed { idx, entry ->
                        entry.altura?.toFloat() ?: 0f
                    }
                    Text("Altura vs Fecha", style = MaterialTheme.typography.titleMedium)
                    SimpleLineChart(
                        points = alturaPoints,
                        modifier = Modifier.height(180.dp).fillMaxWidth(),
                        ejeYLabel = "Altura (m)",
                        ejeXLabel = "Fecha"
                    )
                }
                Modalidad.BAJO_VUELO -> {
                    val lancesPoints = weights.takeLast(180).mapIndexed { idx, entry ->
                        entry.numLances?.toFloat() ?: 0f
                    }
                    Text("Lances vs Fecha", style = MaterialTheme.typography.titleMedium)
                    SimpleLineChart(
                        points = lancesPoints,
                        modifier = Modifier.height(180.dp).fillMaxWidth(),
                        ejeYLabel = "Lances",
                        ejeXLabel = "Fecha"
                    )
                    val capturasPoints = weights.takeLast(180).mapIndexed { idx, entry ->
                        entry.numCapturas?.toFloat() ?: 0f
                    }
                    Text("Capturas vs Fecha", style = MaterialTheme.typography.titleMedium)
                    SimpleLineChart(
                        points = capturasPoints,
                        modifier = Modifier.height(180.dp).fillMaxWidth(),
                        ejeYLabel = "Capturas",
                        ejeXLabel = "Fecha"
                    )
                }
                Modalidad.VELOCIDAD -> {
                    listOf(200, 300, 400).forEach { dist ->
                        val tiempoPoints = weights.takeLast(180).filter { it.distancia == dist }
                            .mapIndexed { idx, entry ->
                                entry.tiempo ?: 0f
                            }
                        Text("${dist}m: Tiempo vs Fecha", style = MaterialTheme.typography.titleMedium)
                        SimpleLineChart(
                            points = tiempoPoints,
                            modifier = Modifier.height(180.dp).fillMaxWidth(),
                            ejeYLabel = "Tiempo (s)",
                            ejeXLabel = "Fecha"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleLineChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    ejeYLabel: String = "",
    ejeXLabel: String = "",
    fechas: List<String>? = null // Opcional: lista de fechas para mostrar en el tooltip
) {
    if (points.isEmpty()) return
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var tapOffset by remember { mutableStateOf<Offset?>(null) }
    Box(modifier = modifier.pointerInput(points) {
        detectTapGestures { offset ->
            val chartWidth = size.width - 40f
            val x = offset.x - 40f
            val idx = (x / chartWidth * (points.size - 1)).roundToInt().coerceIn(0, points.size - 1)
            selectedIndex = idx
            tapOffset = offset
        }
    }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val maxY = points.maxOrNull() ?: 1f
            val minY = points.minOrNull() ?: 0f
            // Eje Y
            drawLine(
                color = Color.Gray,
                start = Offset(40f, 0f),
                end = Offset(40f, size.height),
                strokeWidth = 2f
            )
            // Etiquetas del eje Y (5 divisiones)
            val numLabels = 5
            for (i in 0..numLabels) {
                val value = minY + (maxY - minY) * i / numLabels
                val denom = (maxY - minY)
                val scale = if (denom != 0f) (size.height - 40f) / denom else 1f
                val y = size.height - 20f - (value - minY) * scale
                drawIntoCanvas { canvas -> // Use the canvas from drawIntoCanvas
                    val text = String.format(Locale.getDefault(), "%.1f", value)
                    val paint = Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 28f
                        // You can also set other Paint properties here if needed
                        // e.g., textAlign = Paint.Align.LEFT
                    }
                    // Draw text using the canvas from drawIntoCanvas
                    canvas.nativeCanvas.drawText(text, 0f, y + 5f, paint)
                }
            }
            // Eje X
            drawLine(
                color = Color.Gray,
                start = Offset(40f, size.height - 20f),
                end = Offset(size.width, size.height - 20f),
                strokeWidth = 2f
            )
            // Línea de datos
            val pathPoints = points.mapIndexed { i, y ->
                val denom = (maxY - minY)
                val scale = if (denom != 0f) (size.height - 40f) / denom else 1f
                Offset(
                    40f + i * ((size.width - 40f) / (points.size - 1).coerceAtLeast(1)),
                    size.height - 20f - (y - minY) * scale
                )
            }
            for (i in 0 until pathPoints.size - 1) {
                drawLine(
                    color = Color.Blue,
                    start = pathPoints[i],
                    end = pathPoints[i + 1],
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
            // Punto y tooltip seleccionado
            selectedIndex?.let { idx ->
                if (idx in pathPoints.indices) {
                    val pt = pathPoints[idx]
                    drawCircle(
                        color = Color.Red,
                        radius = 10f,
                        center = pt
                    )
                }
            }
        }
        // Etiqueta eje Y
        if (ejeYLabel.isNotEmpty()) {
            Text(
                ejeYLabel,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 0.dp, top = 8.dp)
                    .graphicsLayer(rotationZ = -90f)
            )
        }
        // Etiqueta eje X
        if (ejeXLabel.isNotEmpty()) {
            Text(
                ejeXLabel,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 0.dp)
            )
        }
        // Tooltip
        selectedIndex?.let { idx ->
            if (idx in points.indices) {
                val valor = points[idx]
                val fecha = fechas?.getOrNull(idx) ?: ""
                val texto = if (fecha.isNotEmpty()) "${valor} | $fecha" else "${valor}"
                val pt = if (idx < points.size) {
                    val maxY = points.maxOrNull() ?: 1f
                    val minY = points.minOrNull() ?: 0f
                    val denom = (maxY - minY)
                    val scale = if (denom != 0f) (180f - 40f) / denom else 1f
                    Offset(40f + idx * ((180f - 40f) / (points.size - 1).coerceAtLeast(1)), 180f - 20f - (valor - minY) * scale)
                } else Offset(0f, 0f)
                Box(
                    modifier = Modifier.offset {
                        val x = ((tapOffset?.x ?: pt.x) + 12f).toInt()
                        val y = ((tapOffset?.y ?: pt.y) - 32f).toInt()
                        IntOffset(x, y)
                    }
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        shape = MaterialTheme.shapes.small,
                        shadowElevation = 2.dp
                    ) {
                        Text(texto, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}
