import java.io.File

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0
)

fun main() {
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