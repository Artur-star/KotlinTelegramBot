import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"

    //создание HTTP клиента
    val client: HttpClient = HttpClient.newHttpClient()

    //создание запроса
    val httpRequest: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()

    //отправка запроса и получение ответа
    val response: HttpResponse<String> = client.send(httpRequest, HttpResponse.BodyHandlers.ofString())

    println(response.body())
}