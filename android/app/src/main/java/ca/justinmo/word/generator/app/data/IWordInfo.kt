package ca.justinmo.word.generator.app.data

interface IWordInfo {
    val type: WordInfoType
    val isFavorite: Boolean
    val word: String
}