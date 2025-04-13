package notas.backend

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class GeminiAPIClient(private val apiKey: String) {

    private val client = OkHttpClient()
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    fun generateText(prompt: String, callback: (String?) -> Unit) {
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            """
                {
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": "$prompt"
                                }
                            ]
                        }
                    ]
                }
            """.trimIndent()
        )

        val request = Request.Builder()
            .url("$baseUrl?key=$apiKey")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Error de conexión: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val json = JSONObject(responseBody)
                        val text = json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text")
                        callback(text)
                    } catch (e: Exception) {
                        callback("Error al procesar la respuesta: ${e.message}")
                    }
                } else {
                    callback("Error de la API: ${response.code} - ${responseBody ?: "Sin respuesta"}")
                }
            }
        })
    }
}