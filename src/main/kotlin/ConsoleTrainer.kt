data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

fun Question.asConsoleString(): String {
    val variants: String = this.variants
        .mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return "${this.correctAnswer.original}\n$variants\n0 - Выход"
}

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
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
                while (true) {
                    val question = trainer.getNextQuestion() ?: break
                    println(question.asConsoleString())

                    val userAnswerInput = readln().toIntOrNull()
                    if (userAnswerInput == 0) break

                    if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                        println("Правильно!")
                    } else {
                        println("Неправильно! ${question.correctAnswer.original} - ${question.correctAnswer.translate}")
                    }
                }
                println("Вы выучили все слова")
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")
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