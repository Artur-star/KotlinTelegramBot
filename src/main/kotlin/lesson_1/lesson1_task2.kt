package lesson_1

import java.io.File

fun main() {
    val file = File("words.txt")
    file.writeText("hello привет\ndog собака\ncat кошка")

    file.readLines().forEach { println(it) }
}