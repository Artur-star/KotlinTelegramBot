fun main(args: Array<String>) {
    val tbs = TelegramBotService()
    val botToken = args[0]
    var updateId = 0



    while (true) {
        Thread.sleep(2000)
        val updates = tbs.getUpdates(botToken, updateId)
        println(updates)

        val regexFindUpdateId = "\"update_id\":(.+?),\n\"message\"".toRegex()
        val regexFindText = "\"text\":\"(.+?)\"}".toRegex()
        val regexFindChatId = "\"chat\":\\{\"id\":(.+?),\"first_name\"".toRegex()
        val regexFindData = "\"data\":\"(.+?)\"".toRegex()

        val updateIdString = regexFindUpdateId.find(updates)?.groups?.get(1)?.value ?: continue

        val text = regexFindText.find(updates)?.groups?.get(1)?.value
        val chatIdString = regexFindChatId.find(updates)?.groups?.get(1)?.value
        val data = regexFindData.find(updates)?.groups?.get(1)?.value

        updateId = updateIdString.toInt() + 1
        val chatId = chatIdString?.toInt()

        if (text?.lowercase() == "hello" && chatId != null) {
            tbs.sendMessage(botToken, chatId, "Hello")
        }
        if (text?.lowercase() == "menu" && chatId != null) {
            tbs.sendMenu(botToken, chatId)
        }
        if (data?.lowercase() == "statistics_clicked" && chatId != null) {
            tbs.sendMessage(botToken, chatId, "Learned 10 words")
        }
    }
}

