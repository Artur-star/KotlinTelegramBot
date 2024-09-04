import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

data class Statistics(
    val learned: Int,
    val total: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val fileName: String = "dictionary.txt",
    private val learnAnswerQuestion: Int = 3,
    private val countOfQuestionWords: Int = 4
) {

    var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learned = dictionary.filter { it.correctAnswersCount >= learnAnswerQuestion }.size
        val total = dictionary.size
        val percent = (learned * 100) / total
        return Statistics(learned, total, percent)
    }

    fun getNextQuestion(): Question? {
        val listUnlearnedWords: List<Word> = dictionary.filter { it.correctAnswersCount < learnAnswerQuestion }
        if (listUnlearnedWords.isEmpty()) return null

        val questionWords: List<Word> = if (listUnlearnedWords.size < countOfQuestionWords) {
            val listLearnedWords: List<Word> = dictionary.filter { it.correctAnswersCount >= learnAnswerQuestion }
            listUnlearnedWords.shuffled().take(countOfQuestionWords) +
                    listLearnedWords.shuffled().take(countOfQuestionWords - listUnlearnedWords.size)
        } else {
            listUnlearnedWords.shuffled().take(countOfQuestionWords)
        }.shuffled()

        val correctAnswer: Word = questionWords.random()
        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId: Int = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else false
        } ?: false
    }

    private fun saveDictionary() {
        val file: File = File(fileName)
        file.writeText("")
        for (words in dictionary) {
            file.appendText("${words.original}|${words.translate}|${words.correctAnswersCount}\n")
        }
    }

    private fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        val file = File(fileName)
        if (!file.exists()) {
            File("dictionary.txt").copyTo(file)
        }

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

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}