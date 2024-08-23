import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

const val TELEGRAM_BASE_URL = "https://api.telegram.org"

class TelegramBotService {
    fun getUpdates(botToken: String, updateId: Int): String {
        val urlGetUpdates = "$TELEGRAM_BASE_URL/bot$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newHttpClient()
        val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(botToken: String, chatId: Int, text: String) : String {
        val encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
        val urlSendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val client: HttpClient = HttpClient.newHttpClient()
        val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(botToken: String, chatId: Int) : String {
        val urlSendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "learn_words_clicked"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "statistics_clicked"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()

        val client: HttpClient = HttpClient.newHttpClient()
        val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}