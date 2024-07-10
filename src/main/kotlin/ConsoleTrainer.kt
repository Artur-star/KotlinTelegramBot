import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

const val MAX_NUMBER_CORRECT_ANSWERS = 3
const val COUNT_ANSWERS = 4

fun main() {
    val dictionary: MutableList<Word> = mutableListOf()
    val file = File("dictionary.txt")

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
        when (input) {
            0 -> {
                println("Нажали 0")
                break
            }

            1 -> {
                println("Нажали 1")
                while (true) {
                    val listUnlearnedWords: MutableList<Word> =
                        dictionary.filter { it.correctAnswersCount < MAX_NUMBER_CORRECT_ANSWERS }.toMutableList()

                    if (listUnlearnedWords.isNotEmpty()) {
                        val jumbledUnlearnedWords = listUnlearnedWords.shuffled().take(COUNT_ANSWERS)
                        val word = jumbledUnlearnedWords[0]
                        println(word.original)
                        jumbledUnlearnedWords.forEach { println(it.translate) }
                        val enterCorrectAnswer = readln()
                        if (enterCorrectAnswer == word.translate) {
                            listUnlearnedWords.set(
                                listUnlearnedWords.indexOf(word),
                                Word(word.original, word.translate, word.correctAnswersCount++)
                            )
                            println("Right answer")
                        } else if (enterCorrectAnswer == "0") break
                    } else break
                }
                println("Вы выучили все слова")
            }

            2 -> {
                val filterDictionary = dictionary.filter { it.correctAnswersCount >= MAX_NUMBER_CORRECT_ANSWERS }
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