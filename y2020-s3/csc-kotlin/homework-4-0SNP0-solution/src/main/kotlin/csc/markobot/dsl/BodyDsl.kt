package csc.markobot.dsl

import csc.markobot.api.*

@MakroBotDsl
class BodyCreator : Materiable() {
    var strings = listOf<String>()

    fun getBody(): Body {
        setMaterial()
        return Body(material!!, strings)
    }

    fun надпись(text: StringsBlock.() -> Unit) {
        this.strings = StringsBlock().apply(text).strings
    }
}

fun MakroBotCreate.туловище(settings: BodyCreator.() -> Unit) {
    this.body = BodyCreator().apply(settings).getBody()
}

@MakroBotDsl
class StringsBlock {
    val strings = mutableListOf<String>()
    operator fun String.unaryPlus() = strings.add(this)
}