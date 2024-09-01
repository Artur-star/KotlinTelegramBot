import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
)

fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId = 0L

    val json = Json{
        ignoreUnknownKeys = true
    }

//    val regexFindUpdateId = "\"update_id\":(.+?),\n".toRegex()
//    val regexFindText = "\"text\":\"([^\"]*)\"".toRegex()
//    val regexFindChatId = "\"chat\":\\{\"id\":(.+?),\"first_name\"".toRegex()
//    val regexFindData = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()
    val tbs = TelegramBotService()
    var question: Question? = null

    while (true) {
        Thread.sleep(2000)
        val responseString = tbs.getUpdates(botToken, lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1
        val message = firstUpdate.message?.text

        val text = regexFindText.find(responseString)?.groups?.get(1)?.value
        val chatIdString = regexFindChatId.find(responseString)?.groups?.get(1)?.value
        val data = regexFindData.find(responseString)?.groups?.get(1)?.value ?: "Данных нет"

        val chatId = chatIdString?.toInt()

        val updateIdString = regexFindUpdateId.find(responseString)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        lastUpdateId = updateIdString + 1

        if (text?.lowercase() == "/start" && chatId != null) {
            tbs.sendMenu(botToken, chatId)
        }

        if (data.lowercase() == CLICKED_STATISTICS && chatId != null) {
            tbs.sendMessage(botToken, chatId, trainer.getStatistics().toString())
        }

        if (data.lowercase() == CLICKED_LEARN_WORDS && chatId != null) {
            question = tbs.checkNextQuestionAndSend(trainer, botToken, chatId)
        }

        if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX, true) && chatId != null) {
            val answerUser = data.substringAfter("answer_").toInt()
            if (trainer.checkAnswer(answerUser)) {
                tbs.sendMessage(botToken, chatId, "Правильно!")
            } else tbs.sendMessage(botToken, chatId, question?.correctAnswer?.translate ?: continue)
            question = tbs.checkNextQuestionAndSend(trainer, botToken, chatId)
        } else if (text?.lowercase() == "hello" && chatId != null) {
            tbs.sendMessage(botToken, chatId, "Hello")
        }
    }
}

