import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

fun main() {

    val responseString = """{
    "ok": true,
    "result": [
        {
            "update_id": 861534652,
            "message": {
                "message_id": 616,
                "from": {
                    "id": 1034923700,
                    "is_bot": false,
                    "first_name": "Artur",
                    "last_name": "Knyazev",
                    "username": "KnyazevArt",
                    "language_code": "ru"
                },
                "chat": {
                    "id": 1034923700,
                    "first_name": "Artur",
                    "last_name": "Knyazev",
                    "username": "KnyazevArt",
                    "type": "private"
                },
                "date": 1725214341,
                "text": "/start",
                "entities": [
                    {
                        "offset": 0,
                        "length": 6,
                        "type": "bot_command"
                    }
                ]
            }
        }
    ]
}""".trimIndent()

    val wordObject = json.decodeFromString<Response>(
        responseString
    )
    println(wordObject)
}