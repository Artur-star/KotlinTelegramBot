fun main(args: Array<String>) {

    val tbs = TelegramBotService()
    val botToken = args[0]
    var updateId = 0

    val regexFindUpdateId = "\"update_id\":(.+?),\n".toRegex()
    val regexFindText = "\"text\":\"([^\"]*)\"".toRegex()
    val regexFindChatId = "\"chat\":\\{\"id\":(.+?),\"first_name\"".toRegex()
    val regexFindData = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()
    val question: Question = trainer.getNextQuestion() ?: run {
        "Вы выучили все слова в базе"
        return
    }
    while (true) {
        Thread.sleep(2000)
        val updates = tbs.getUpdates(botToken, updateId)
        println(updates)

        val text = regexFindText.find(updates)?.groups?.get(1)?.value
        val chatIdString = regexFindChatId.find(updates)?.groups?.get(1)?.value
        val data = regexFindData.find(updates)?.groups?.get(1)?.value

        val chatId = chatIdString?.toInt()

        val updateIdString = regexFindUpdateId.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        updateId = updateIdString + 1

        if (text?.lowercase() == "hello" && chatId != null) {
            tbs.sendMessage(botToken, chatId, "Hello")
        }
        if (text?.lowercase() == "/start" && chatId != null) {
            tbs.sendMenu(botToken, chatId)
        }

        if (data?.lowercase() == CLICKED_STATISTICS && chatId != null) {
            tbs.sendMessage(botToken, chatId, trainer.getStatistics().toString())
        }

        if (data?.lowercase() == CLICKED_LEARN_WORDS && chatId != null) {
            tbs.sendQuestion(botToken, chatId, question)
        }
    }
}

