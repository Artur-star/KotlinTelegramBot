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
    @SerialName("chat")
    val chat: Chat
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long
)

fun main(args: Array<String>) {
    val botToken = args[0]
    var lastUpdateId = 0L
    val trainers = HashMap<Long, LearnWordsTrainer>()
    val json = Json {
        ignoreUnknownKeys = true
    }

    val tbs = TelegramBotService(json)
    val variantsAnswer: List<String> = listOf("AAA", "BBB", "CCC", "DDD")

    while (true) {
        Thread.sleep(2000)
        val responseString = tbs.getUpdates(botToken, lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, botToken, trainers, tbs, variantsAnswer) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    firstUpdate: Update,
    json: Json,
    botToken: String,
    trainers: HashMap<Long, LearnWordsTrainer>,
    tbs: TelegramBotService,
    variantsAnswer: List<String>,
) {
    val text = firstUpdate.message?.text
    val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id ?: return
    val data = firstUpdate.callbackQuery?.data ?: "Данных нет"

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    if (text?.lowercase() == TEXT_START) {
        tbs.sendMenu(json, botToken, chatId)
    }

    if (data.lowercase() == CLICKED_STATISTICS) {
        tbs.sendMessage(botToken, chatId, trainer.getStatistics().toString())
    }

    if (data.lowercase() == CLICKED_LEARN_WORDS) {
        tbs.checkNextQuestionAndSend(trainer, botToken, chatId, variantsAnswer)
    }

    if (data.startsWith(CALLBACK_DATA_ANSWER_PREFIX, true)) {
        val answerUser = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
        if (trainer.checkAnswer(answerUser)) {
            tbs.sendMessage(botToken, chatId, "Правильно!")
        } else {
            tbs.sendMessage(
                botToken,
                chatId,
                "${trainer.question?.correctAnswer?.original} - ${trainer.question?.correctAnswer?.translate}"
            )
        }
        tbs.checkNextQuestionAndSend(trainer, botToken, chatId, variantsAnswer)
    } else if (text?.lowercase() == TEXT_HELLO) {
        tbs.sendMessage(botToken, chatId, TEXT_HELLO)
    }

    if (data == RESET_ClICKED) {
        trainer.resetProgress()
        tbs.sendMessage(botToken, chatId, "Прогресс сброшен")
    }
}