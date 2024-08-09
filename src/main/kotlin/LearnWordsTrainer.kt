import java.io.File

const val MAX_NUMBER_CORRECT_ANSWERS = 3
const val COUNT_ANSWERS = 4

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswersCount >= MAX_NUMBER_CORRECT_ANSWERS }.size
        val total = dictionary.size
        val percent = (learned * 100) / total
        return Statistics(learned, total, percent)
    }

    fun getNextQuestion(): Question? {
        val listUnlearnedWords: List<Word> = dictionary.filter { it.correctAnswersCount < MAX_NUMBER_CORRECT_ANSWERS }
        if (listUnlearnedWords.isEmpty()) return null
        val jumbledUnlearnedWords: List<Word> = listUnlearnedWords.shuffled().take(COUNT_ANSWERS)
        val correctAnswer: Word = jumbledUnlearnedWords.random()
        question = Question(
            variants = jumbledUnlearnedWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId: Int = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }

    private fun saveDictionary(listWords: List<Word>) {
        val file: File = File("dictionary.txt")
        file.writeText("")
        for (words in listWords) {
            file.appendText("${words.original}|${words.translate}|${words.correctAnswersCount}\n")
        }
    }

    private fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()

        val file = File("dictionary.txt")

        file.forEachLine { line ->
            val parts = line.split("|")
            val original = parts[0]
            val translate = parts[1]
            val correctAnswersCount = parts.getOrNull(2)?.toIntOrNull() ?: 0

            val word = Word(original, translate, correctAnswersCount)
            dictionary.add(word)
        }
        return dictionary
    }
}