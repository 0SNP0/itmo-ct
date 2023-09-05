package csc.markobot.dsl

import csc.markobot.api.*

@MakroBotDsl
class HeadCreator : Materiable() {
    var eyes = listOf<Eye>()
    var mouth = Mouth(null)

    fun getHead(): Head {
        setMaterial()
        return Head(material!!, eyes, mouth)
    }
}

fun MakroBotCreate.голова(settings: HeadCreator.() -> Unit) {
    this.head = HeadCreator().apply(settings).getHead()
}