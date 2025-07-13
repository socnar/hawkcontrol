package com.socnar.hawkcontrol

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.socnar.hawkcontrol.ui.theme.HawkControlTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: BakingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return BakingViewModel(application) as T
                }
            })
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            var showSettings by remember { mutableStateOf(false) }
            var showAddBird by remember { mutableStateOf(false) }
            var selectedBird by remember { mutableStateOf<BirdListItem?>(null) }
            var selectedWeightEntry by remember { mutableStateOf<WeightEntry?>(null) }
            // Dummy list for now, replace with ViewModel data
            val birds by viewModel.birds.collectAsState()

            // Launchers para SAF
            val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let { viewModel.importCsvFromUri(it) }
            }
            val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
                uri?.let { viewModel.exportCsvToUri(it) }
            }

            // Escuchar eventos del ViewModel
            LaunchedEffect(Unit) {
                viewModel.settingsEvent.collectLatest { event ->
                    when (event) {
                        is SettingsEvent.RequestImport -> importLauncher.launch(arrayOf("text/csv"))
                        is SettingsEvent.RequestExport -> exportLauncher.launch("birds.csv")
                        is SettingsEvent.ShowMessage -> {
                            // Puedes mostrar un Toast o Snackbar aquí
                        }
                    }
                }
            }

            HawkControlTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    when {
                        showSettings -> SettingsScreen(
                            viewModel = viewModel,
                            onImportClick = { viewModel.onImportCsvClicked() },
                            onExportClick = { viewModel.onExportCsvClicked() },
                            isDarkMode = isDarkMode,
                            onDarkModeToggle = { viewModel.setDarkMode(it) },
                            onBack = { showSettings = false }
                        )
                        else -> when {
                            showAddBird -> NewBirdScreen(
                                onSave = { nombre, especie, sexo, anoNacimiento, modalidad ->
                                    viewModel.addBird(nombre, especie, sexo, anoNacimiento, modalidad)
                                    showAddBird = false
                                },
                                onCancel = { showAddBird = false }
                            )
                            selectedBird != null -> {
                                val bird = birds.find { it.id == selectedBird!!.id }
                                val weights = viewModel.getWeightsForBird(bird?.id)
                                var showNewWeight by remember { mutableStateOf(false) }
                                var showGraphs by remember { mutableStateOf(false) }
                                if (showNewWeight) {
                                    NewWeightEntryScreen(
                                        bird = bird!!,
                                        onSave = { fecha, pesoAntesVolar, tipoVuelo, comentario, altura, numCapturas, numLances, distancia, tiempo ->
                                            viewModel.addWeightEntry(
                                                birdId = bird.id,
                                                fecha = fecha,
                                                pesoAntesVolar = pesoAntesVolar,
                                                tipoVuelo = tipoVuelo,
                                                comentario = comentario,
                                                altura = altura,
                                                numCapturas = numCapturas,
                                                numLances = numLances,
                                                distancia = distancia,
                                                tiempo = tiempo
                                            )
                                            showNewWeight = false
                                        },
                                        onCancel = { showNewWeight = false }
                                    )
                                } else if (selectedWeightEntry != null) {
                                    ViewWeightEntryScreen(
                                        bird = bird!!,
                                        entry = selectedWeightEntry!!,
                                        onBack = { selectedWeightEntry = null }
                                    )
                                } else {
                                    WeightHistoryScreen(
                                        bird = bird!!,
                                        weights = weights,
                                        onAddWeight = { showNewWeight = true },
                                        onShowGraphs = { showGraphs = true },
                                        onBack = { selectedBird = null },
                                        onWeightEntryClick = { entry ->
                                            selectedWeightEntry = entry
                                        }
                                    )
                                    if (showGraphs) {
                                        GraphsScreen(
                                            bird = bird!!,
                                            weights = weights,
                                            onBack = { showGraphs = false }
                                        )
                                    }
                                }
                            }
                            else -> HomeScreen(
                                birds = birds.map { BirdListItem(it.id, it.nombre) },
                                onAddBird = { showAddBird = true },
                                onBirdClick = { selectedBird = it },
                                onSettingsClick = { showSettings = true },
                                onDeleteBird = { bird ->
                                    viewModel.deleteBird(bird.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}