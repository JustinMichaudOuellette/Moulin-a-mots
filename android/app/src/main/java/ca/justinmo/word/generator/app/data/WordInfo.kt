package ca.justinmo.word.generator.app.data

data class WordInfo(override val word: String, override val isFavorite: Boolean): IWordInfo {
    override val type: WordInfoType
        get() = WordInfoType.WORD
}