import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import com.socnar.hawkcontrol.Bird
import com.socnar.hawkcontrol.Modalidad
import com.socnar.hawkcontrol.WeightEntry
import com.socnar.hawkcontrol.TipoVuelo

object CsvUtils {
    // Importa un archivo CSV y lo convierte en una lista de listas de cadenas
    fun importCsv(context: Context, fileName: String): List<List<String>> {
        val result = mutableListOf<List<String>>()
        try {
            val inputStream = context.openFileInput(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.use { r ->
                r.forEachLine { line ->
                    // Divide cada línea por comas y agrega a la lista resultante
                    result.add(line.split(","))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    // Exporta una lista de listas de cadenas a un archivo CSV
    fun exportCsv(context: Context, fileName: String, data: List<List<String>>) {
        try {
            val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = OutputStreamWriter(outputStream)
            writer.use { w ->
                data.forEach { row ->
                    // Une cada fila en una cadena separada por comas
                    w.write(row.joinToString(",") + "\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Exporta un archivo CSV local a un URI externo (por ejemplo, SAF)
    fun exportCsv(context: Context, localFileName: String, exportUri: Uri): Boolean {
        return try {
            val input = context.openFileInput(localFileName)
            val output = context.contentResolver.openOutputStream(exportUri)
            if (output != null) {
                input.copyTo(output)
                output.flush()
                output.close()
            }
            input.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Importa un archivo CSV externo (por ejemplo, SAF) y lo guarda localmente
    fun importCsv(context: Context, importUri: Uri, localFileName: String): Boolean {
        return try {
            val input = context.contentResolver.openInputStream(importUri)
            val output = context.openFileOutput(localFileName, Context.MODE_PRIVATE)
            if (input != null) {
                input.copyTo(output)
                output.flush()
                output.close()
            }
            input?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadBirds(context: Context, fileName: String): List<Bird> {
        val birds = mutableListOf<Bird>()
        try {
            val inputStream = context.openFileInput(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 6) {
                        birds.add(
                            Bird(
                                id = parts[0],
                                nombre = parts[1],
                                especie = parts[2],
                                sexo = parts[3],
                                anoNacimiento = parts[4].toIntOrNull() ?: 0,
                                modalidad = Modalidad.valueOf(parts[5])
                            )
                        )
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            // Si el archivo no existe, simplemente retorna la lista vacía
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return birds
    }

    fun saveBirds(context: Context, birds: List<Bird>, fileName: String) {
        try {
            val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = OutputStreamWriter(outputStream)
            writer.use { w ->
                w.write("id,nombre,especie,sexo,anoNacimiento,modalidad\n")
                birds.forEach { bird ->
                    w.write("${bird.id},${bird.nombre},${bird.especie},${bird.sexo},${bird.anoNacimiento},${bird.modalidad}\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadWeights(context: Context, fileName: String): List<WeightEntry> {
        val weights = mutableListOf<WeightEntry>()
        try {
            val inputStream = context.openFileInput(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.useLines { lines ->
                lines.drop(1).forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 10) {
                        weights.add(
                            WeightEntry(
                                birdId = parts[0],
                                fecha = parts[1],
                                pesoAntesVolar = parts[2].toFloatOrNull(),
                                comentario = parts[3].ifBlank { null },
                                altura = parts[4].toIntOrNull(),
                                numCapturas = parts[5].toIntOrNull(),
                                numLances = parts[6].toIntOrNull(),
                                distancia = parts[7].toIntOrNull(),
                                tiempo = parts[8].toFloatOrNull(),
                                tipoVuelo = parts[9].ifBlank { null }?.let { TipoVuelo.valueOf(it) }
                            )
                        )
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            // Si el archivo no existe, retorna vacío
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return weights
    }

    fun saveWeights(context: Context, weights: List<WeightEntry>, fileName: String) {
        try {
            val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer = OutputStreamWriter(outputStream)
            writer.use { w ->
                w.write("birdId,fecha,pesoAntesVolar,comentario,altura,numCapturas,numLances,distancia,tiempo,tipoVuelo\n")
                weights.forEach { wEntry ->
                    w.write("${wEntry.birdId},${wEntry.fecha},${wEntry.pesoAntesVolar ?: ""},${wEntry.comentario ?: ""},${wEntry.altura ?: ""},${wEntry.numCapturas ?: ""},${wEntry.numLances ?: ""},${wEntry.distancia ?: ""},${wEntry.tiempo ?: ""},${wEntry.tipoVuelo ?: ""}\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
