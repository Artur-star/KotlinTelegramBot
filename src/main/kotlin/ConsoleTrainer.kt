import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

const val MAX_ANSWER_COUNT = 3

fun main() {
    val dictionary: MutableList<Word> = mutableListOf()
    val file = File("dictionary.txt")
    file.writeText("hello|привет\ndog|собака\ncat|кошка\npen|ручка\n")

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
    println(
        "Меню: \n" +
                "1 – Учить слова\n" +
                "2 – Статистика\n" +
                "0 – Выход"
    )
    println("Выберите пункт меню: ")
    var input = readln().toIntOrNull()

    while (true) {
        var enterCorrectAnswer = ""
        when (input) {
            0 -> {
                println("Нажали 0")
                break
            }

            1 -> {
                println("Нажали 1")
                while (enterCorrectAnswer != "0") {
                    val listUnlearnedWords: MutableList<Word> =
                        dictionary.filter { it.correctAnswersCount < MAX_ANSWER_COUNT }.toMutableList()

                    if (listUnlearnedWords.isEmpty()) {
                        println("Вы выучили все слова")
                        break
                    } else {
                        val jumbledUnlearnedWords = listUnlearnedWords.shuffled().take(4)
                        val word = jumbledUnlearnedWords[0]
                        val translate = word.translate
                        val original = word.original
                        println(original)
                        jumbledUnlearnedWords.forEach { println(it.translate) }
                        enterCorrectAnswer = readln()
                        if (enterCorrectAnswer == translate) {
                            listUnlearnedWords.set(
                                listUnlearnedWords.indexOf(word),
                                Word(word.original, word.translate, word.correctAnswersCount++)
                            )
                            println("Right answer")
                        }
                    }
                }
            }

            2 -> {
                val filterDictionary = dictionary.filter { it.correctAnswersCount >= MAX_ANSWER_COUNT }
                println("Выучено ${filterDictionary.size} из ${dictionary.size} слов | ${(filterDictionary.size * 100) / dictionary.size}")
            }

            else -> println("Ошибка ввода данных")
        }
        println(
            "Меню: \n" +
                    "1 – Учить слова\n" +
                    "2 – Статистика\n" +
                    "0 – Выход"
        )
        input = readln().toIntOrNull()
    }
}