package csc.markobot.dsl

import csc.markobot.api.Eye
import csc.markobot.api.LampEye
import csc.markobot.api.LedEye

fun HeadCreator.глаза(eyes: MutableList<Eye>.() -> Unit) {
    this.eyes = mutableListOf<Eye>().apply(eyes)
}

@MakroBotDsl
class EyesCreator {
    var количество = 0
    var яркость = 0

    fun getEyes(create: (Int) -> Eye): MutableList<Eye> {
        val list = mutableListOf<Eye>()
        repeat(количество) {
            list.add(create(яркость))
        }
        return list
    }
}

fun MutableList<Eye>.eyesList(settings: EyesCreator.() -> Unit, create: (Int) -> Eye) {
    this.addAll(EyesCreator().apply(settings).getEyes(create))
}

fun MutableList<Eye>.лампы(settings: EyesCreator.() -> Unit) = this.eyesList(settings) { LampEye(it) }
fun MutableList<Eye>.диоды(settings: EyesCreator.() -> Unit) = this.eyesList(settings) { LedEye(it) }
