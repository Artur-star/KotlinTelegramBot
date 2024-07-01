import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0
)

fun main() {
    println(
        "Меню: \n" +
                "1 – Учить слова\n" +
                "2 – Статистика\n" +
                "0 – Выход"
    )
    println("Выберите пункт меню: ")
    var input = readln().toIntOrNull()
    while (input in 0..2) {
        when (input) {
            0 -> println("Нажали 0")

            1 -> println("Нажали 1")

            2 -> println("Нажали 2")

            else -> println("Ошибка ввода данных")
        }
        input = readln().toIntOrNull()
    }
    val dictionary: MutableList<Word> = mutableListOf()
    val file = File("dictionary.txt")
    file.writeText("hello|привет|1\ndog|собака|2\ncat|кошка|3\n")

    file.forEachLine { line ->
        val parts = line.split("|")
        val original = parts[0]
        val translate = parts[1]
        val correctAnswersCount = parts.getOrNull(2)?.toIntOrNull() ?: 0

        val word = Word(original, translate, correctAnswersCount)
        dictionary.add(word)
    }

    dictionary.forEach { word ->
        println("Original: ${word.original}, Translate: ${word.translate}, Correct Answers: ${word.correctAnswersCount}")
    }
}