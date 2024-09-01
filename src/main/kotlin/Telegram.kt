fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0

    val regexFindUpdateId = "\"update_id\":(.+?),\n".toRegex()
    val regexFindText = "\"text\":\"([^\"]*)\"".toRegex()
    val regexFindChatId = "\"chat\":\\{\"id\":(.+?),\"first_name\"".toRegex()
    val regexFindData = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()
    val tbs = TelegramBotService()
    var question: Question? = null

    while (true) {
        Thread.sleep(2000)
        val updates = tbs.getUpdates(botToken, updateId)
        println(updates)

        val text = regexFindText.find(updates)?.groups?.get(1)?.value
        val chatIdString = regexFindChatId.find(updates)?.groups?.get(1)?.value
        val data = regexFindData.find(updates)?.groups?.get(1)?.value ?: "Данных нет"

        val chatId = chatIdString?.toInt()

        val updateIdString = regexFindUpdateId.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        updateId = updateIdString + 1

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

