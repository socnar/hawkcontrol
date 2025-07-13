package com.socnar.hawkcontrol

data class Bird(
    val id: String, // UUID
    val nombre: String,
    val especie: String,
    val sexo: String,
    val anoNacimiento: Int,
    val modalidad: Modalidad
)

enum class Modalidad {
    BAJO_VUELO, ALTANERIA, VELOCIDAD
}

data class WeightEntry(
    val birdId: String,
    val fecha: String, // ISO 8601 (yyyy-MM-dd)
    val pesoAntesVolar: Float?,
    val comentario: String?,
    val altura: Int?, // Altanería
    val numCapturas: Int?, // Bajo vuelo
    val numLances: Int?, // Bajo vuelo
    val distancia: Int?, // Velocidad (metros)
    val tiempo: Float?, // Velocidad (segundos)
    val tipoVuelo: TipoVuelo?
)

enum class TipoVuelo {
    ENTRENAMIENTO, CAZA
}

