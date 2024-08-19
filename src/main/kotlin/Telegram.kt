fun main(args: Array<String>) {
    val tbs = TelegramBotService()
    val botToken = args[0]
    var updateId = 0
    var chatId = 0

    while (true) {
        Thread.sleep(2000)
        val updates = tbs.getUpdates(botToken, updateId)

        val regexFindUpdateId = "\"update_id\":(.+?),\n\"message\"".toRegex()
        val regexFindText = "\"text\":\"(.+?)\"}".toRegex()
        val regexFindChatId = "\"chat\":\\{\"id\":(.+?),\"first_name\"".toRegex()

        val updateIdString = regexFindUpdateId.find(updates)?.groups?.get(1)?.value ?: continue
        val text = regexFindText.find(updates)?.groups?.get(1)?.value ?: continue
        val chatIdString = regexFindChatId.find(updates)?.groups?.get(1)?.value ?: continue

        updateId = updateIdString.toInt() + 1
        chatId = chatIdString.toInt()
        tbs.sendMessage(botToken, chatId, text)
    }
}

