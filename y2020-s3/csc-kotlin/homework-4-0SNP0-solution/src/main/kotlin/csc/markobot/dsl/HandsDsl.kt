package csc.markobot.dsl

import csc.markobot.api.*

@MakroBotDsl
class HandsCreator : Materiable() {
    val очень_легкая = LoadClass.VeryLight
    val легкая = LoadClass.Light
    val средняя = LoadClass.Medium
    val тяжелая = LoadClass.Heavy
    val очень_тяжелая = LoadClass.VeryHeavy
    val громадная = LoadClass.Enormous

    var нагрузка: Pair<LoadClass, LoadClass>? = null

    fun getHands(): Hands {
        setMaterial()
        return Hands(material!!, нагрузка!!.first, нагрузка!!.second)
    }
}

fun MakroBotCreate.руки(settings: HandsCreator.() -> Unit) {
    this.hands = HandsCreator().apply(settings).getHands()
}