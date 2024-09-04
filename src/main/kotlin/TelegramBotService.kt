import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val TELEGRAM_BASE_URL = "https://api.telegram.org"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val RESET_ClICKED = "reset_clicked"
const val CLICKED_LEARN_WORDS: String = "learn_words_clicked"
const val CLICKED_STATISTICS: String = "statistics_clicked"

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

class TelegramBotService(private val json: Json) {

    fun getUpdates(botToken: String, updateId: Long): String {
        val urlGetUpdates = "$TELEGRAM_BASE_URL/bot$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newHttpClient()
        val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    private fun sendQuestion(botToken: String, chatId: Long, question: Question): String {
        val urlSendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(question.variants.mapIndexed { index, word ->
                    InlineKeyboard(text = word.translate, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index")
                })
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val client = HttpClient.newHttpClient()
        val httpRequest = HttpRequest.newBuilder(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMessage(botToken: String, chatId: Long, text: String): String {
        val urlSendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = text,
        )

        val requestBodyString = json.encodeToString(requestBody)

        val client: HttpClient = HttpClient.newHttpClient()
        val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(json: Json, botToken: String, chatId: Long): String {
        val urlSendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"

        val requestBody = SendMessageRequest(
            chatId,
            "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = "Learn words", callbackData = CLICKED_LEARN_WORDS),
                        InlineKeyboard(text = "Statistics", callbackData = CLICKED_STATISTICS)
                    ),
                    listOf(
                        InlineKeyboard(text = "Reset progress", callbackData = RESET_ClICKED),
                    )
                )
            )
        )

        val requestBodyString = json.encodeToString(requestBody)

        val client: HttpClient = HttpClient.newHttpClient()
        val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, botToken: String, chatId: Long) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            sendMessage(botToken, chatId, "Вы выучили все слова в базе")
        } else sendQuestion(botToken, chatId, question)
    }
}