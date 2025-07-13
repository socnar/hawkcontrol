package com.socnar.hawkcontrol

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    viewModel: BakingViewModel = viewModel(),
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onBack: () -> Unit // Nuevo parámetro para manejar el back
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modo oscuro")
            Spacer(Modifier.width(16.dp))
            Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggle)
        }
        Button(onClick = onImportClick, modifier = Modifier.fillMaxWidth()) {
            Text("Importar CSV")
        }
        Button(onClick = onExportClick, modifier = Modifier.fillMaxWidth()) {
            Text("Exportar CSV")
        }
    }
}
