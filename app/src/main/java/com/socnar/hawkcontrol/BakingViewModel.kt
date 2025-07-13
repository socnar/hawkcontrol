package com.socnar.hawkcontrol

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID

// DataStore delegate fuera de la clase
val Context.dataStore by preferencesDataStore(name = "settings")

sealed class SettingsEvent {
    object RequestImport : SettingsEvent()
    object RequestExport : SettingsEvent()
    data class ShowMessage(val message: String) : SettingsEvent()
}

class BakingViewModel(app: Application) : AndroidViewModel(app) {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _settingsEvent = Channel<SettingsEvent>()
    val settingsEvent = _settingsEvent.receiveAsFlow()

    private val _birds = MutableStateFlow<List<Bird>>(emptyList())
    val birds: StateFlow<List<Bird>> = _birds

    private val birdsFileName = "birds.csv"

    private val _weights = MutableStateFlow<List<WeightEntry>>(emptyList())
    val weights: StateFlow<List<WeightEntry>> = _weights
    private val weightsFileName = "weights.csv"

    init {
        // Cargar preferencia de modo oscuro
        viewModelScope.launch {
            val prefs = app.dataStore.data
            prefs.collect { pref ->
                _isDarkMode.value = pref[booleanPreferencesKey("dark_mode")] ?: false
            }
        }
        // Cargar aves desde CSV al iniciar
        viewModelScope.launch(Dispatchers.IO) {
            loadBirdsFromCsv()
        }
        // Cargar registros de peso desde CSV al iniciar
        viewModelScope.launch(Dispatchers.IO) {
            loadWeightsFromCsv()
        }
    }

    private suspend fun loadBirdsFromCsv() {
        val loaded = CsvUtils.loadBirds(getApplication(), birdsFileName)
        _birds.value = loaded
    }

    private suspend fun loadWeightsFromCsv() {
        val loaded = CsvUtils.loadWeights(getApplication(), weightsFileName)
        _weights.value = loaded
    }

    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[booleanPreferencesKey("dark_mode")] = enabled
            }
        }
    }

    fun onImportCsvClicked() {
        viewModelScope.launch { _settingsEvent.send(SettingsEvent.RequestImport) }
    }

    fun onExportCsvClicked() {
        viewModelScope.launch { _settingsEvent.send(SettingsEvent.RequestExport) }
    }

    fun importCsvFromUri(uri: Uri) {
        viewModelScope.launch {
            val ok = CsvUtils.importCsv(getApplication(), uri, "birds.csv")
            if (ok) {
                // Recargar los datos después de importar
                val loaded = CsvUtils.loadBirds(getApplication(), birdsFileName)
                _birds.value = loaded
            }
            _settingsEvent.send(SettingsEvent.ShowMessage(if (ok) "Importación exitosa" else "Error al importar"))
        }
    }

    fun exportCsvToUri(uri: Uri) {
        viewModelScope.launch {
            val ok = CsvUtils.exportCsv(getApplication(), "birds.csv", uri)
            _settingsEvent.send(SettingsEvent.ShowMessage(if (ok) "Exportación exitosa" else "Error al exportar"))
        }
    }

    fun deleteBird(birdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = _birds.value.filterNot { it.id == birdId }
            _birds.value = updated
            CsvUtils.saveBirds(getApplication(), updated, birdsFileName)
        }
    }

    fun addBird(nombre: String, especie: String, sexo: String, anoNacimiento: String, modalidad: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newBird = Bird(
                id = UUID.randomUUID().toString(),
                nombre = nombre,
                especie = especie,
                sexo = sexo,
                anoNacimiento = anoNacimiento.toIntOrNull() ?: 0,
                modalidad = Modalidad.valueOf(modalidad)
            )
            val updated = _birds.value + newBird
            _birds.value = updated
            CsvUtils.saveBirds(getApplication(), updated, birdsFileName)
        }
    }

    fun addWeightEntry(
        birdId: String,
        fecha: String,
        pesoAntesVolar: String?,
        tipoVuelo: String?,
        comentario: String?,
        altura: String?,
        numCapturas: String?,
        numLances: String?,
        distancia: String?,
        tiempo: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newEntry = WeightEntry(
                birdId = birdId,
                fecha = fecha,
                pesoAntesVolar = pesoAntesVolar?.toFloatOrNull(),
                comentario = comentario,
                altura = altura?.toIntOrNull(),
                numCapturas = numCapturas?.toIntOrNull(),
                numLances = numLances?.toIntOrNull(),
                distancia = distancia?.toIntOrNull(),
                tiempo = tiempo?.toFloatOrNull(),
                tipoVuelo = tipoVuelo?.let { TipoVuelo.valueOf(it) }
            )
            val updated = _weights.value + newEntry
            _weights.value = updated
            CsvUtils.saveWeights(getApplication(), updated, weightsFileName)
        }
    }

    fun getWeightsForBird(birdId: String?): List<WeightEntry> {
        if (birdId == null) return emptyList()
        return _weights.value.filter { it.birdId == birdId }
    }
}