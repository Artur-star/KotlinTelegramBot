package lesson_1

import java.io.File

fun main() {
    val file = File("words.txt")
    file.writeText("hello ������\ndog ������\ncat �����")

    file.readLines().forEach { println(it) }
}