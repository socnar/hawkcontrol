package com.socnar.hawkcontrol

import android.app.DatePickerDialog
import java.util.Calendar
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWeightEntryScreen(
    bird: Bird,
    onSave: (
        fecha: String,
        pesoAntesVolar: String,
        tipoVuelo: String?,
        comentario: String?,
        altura: String?,
        numCapturas: String?,
        numLances: String?,
        distancia: String?,
        tiempo: String?
    ) -> Unit,
    onCancel: () -> Unit
) {
    BackHandler(onBack = onCancel)
    // Obtener la fecha de hoy compatible con Android API < 26
    val today = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }
    var fecha by remember { mutableStateOf(today) }
    var pesoAntesVolar by remember { mutableStateOf("") }
    var tipoVuelo by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var numCapturas by remember { mutableStateOf("") }
    var numLances by remember { mutableStateOf("") }
    var distancia by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                fecha = formatted
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Nuevo registro de peso", style = MaterialTheme.typography.headlineMedium)
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (DD/MM/YYYY)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    readOnly = false
                )
                OutlinedTextField(
                    value = pesoAntesVolar,
                    onValueChange = { pesoAntesVolar = it },
                    label = { Text("Peso antes de volar (gramos)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                var expandedTipoVuelo by remember { mutableStateOf(false) }
                if ((bird.modalidad == Modalidad.ALTANERIA || bird.modalidad == Modalidad.VELOCIDAD)) {
                    ExposedDropdownMenuBox(
                        expanded = expandedTipoVuelo,
                        onExpandedChange = { expandedTipoVuelo = it }
                    ) {
                        OutlinedTextField(
                            value = tipoVuelo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de vuelo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { expandedTipoVuelo = !expandedTipoVuelo },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipoVuelo)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            singleLine = true,
                            enabled = true
                        )
                        DropdownMenu(
                            expanded = expandedTipoVuelo,
                            onDismissRequest = { expandedTipoVuelo = false }
                        ) {
                            listOf("ENTRENAMIENTO", "CAZA").forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.capitalize()) },
                                    onClick = {
                                        tipoVuelo = tipo
                                        expandedTipoVuelo = false
                                    }
                                )
                            }
                        }
                    }
                }
                if (bird.modalidad == Modalidad.ALTANERIA) {
                    OutlinedTextField(
                        value = altura,
                        onValueChange = { altura = it },
                        label = { Text("Altura (metros)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                if (bird.modalidad == Modalidad.BAJO_VUELO) {
                    OutlinedTextField(
                        value = numCapturas,
                        onValueChange = { numCapturas = it },
                        label = { Text("Nº de capturas") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                if (bird.modalidad == Modalidad.VELOCIDAD) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = distancia,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Distancia") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .clickable { expanded = !expanded },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            singleLine = true,
                            enabled = true
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("200", "300", "400").forEach { dist ->
                                DropdownMenuItem(
                                    text = { Text("${dist}m") },
                                    onClick = {
                                        distancia = dist
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = tiempo,
                        onValueChange = { tiempo = it },
                        label = { Text("Tiempo (segundos, admite decimales)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (bird.modalidad == Modalidad.BAJO_VUELO) {
                    OutlinedTextField(
                        value = numLances,
                        onValueChange = { numLances = it },
                        label = { Text("Nº de lances") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    onSave(
                        fecha,
                        pesoAntesVolar,
                        tipoVuelo.takeIf { it.isNotBlank() },
                        comentario.takeIf { it.isNotBlank() },
                        altura.takeIf { it.isNotBlank() },
                        numCapturas.takeIf { it.isNotBlank() },
                        numLances.takeIf { it.isNotBlank() },
                        distancia.takeIf { it.isNotBlank() },
                        tiempo.takeIf { it.isNotBlank() }
                    )
                },
                enabled = fecha.isNotBlank() && pesoAntesVolar.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar")
            }
            OutlinedButton(onClick = onCancel, shape = RoundedCornerShape(12.dp)) {
                Text("Cancelar")
            }
        }
    }
}
