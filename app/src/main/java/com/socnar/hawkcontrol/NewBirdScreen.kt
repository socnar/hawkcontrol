package com.socnar.hawkcontrol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBirdScreen(
    onSave: (nombre: String, especie: String, sexo: String, anoNacimiento: String, modalidad: String) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var anoNacimiento by remember { mutableStateOf("") }
    var modalidad by remember { mutableStateOf(Modalidad.BAJO_VUELO.name) }

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nueva Ave", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = especie,
            onValueChange = { especie = it },
            label = { Text("Especie") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = sexo,
            onValueChange = { sexo = it },
            label = { Text("Sexo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = anoNacimiento,
            onValueChange = { anoNacimiento = it },
            label = { Text("Año de nacimiento") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = modalidad,
                onValueChange = {},
                readOnly = true,
                label = { Text("Modalidad") },
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
                Modalidad.values().forEach { mod ->
                    DropdownMenuItem(
                        text = { Text(mod.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            modalidad = mod.name
                            expanded = false
                        }
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    onSave(nombre, especie, sexo, anoNacimiento, modalidad)
                },
                enabled = nombre.isNotBlank() && especie.isNotBlank() && sexo.isNotBlank() && anoNacimiento.isNotBlank()
            ) {
                Text("Guardar")
            }
            OutlinedButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    }
}
