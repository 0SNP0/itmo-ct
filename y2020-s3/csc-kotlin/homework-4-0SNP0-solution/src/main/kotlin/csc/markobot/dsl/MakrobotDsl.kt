package csc.markobot.dsl

import csc.markobot.api.*

@MakroBotDsl
class MakroBotCreate(val name: String) {
    var head: Head? = null
    var body: Body? = null
    var hands: Hands? = null
    var шасси: Chassis? = null

    fun getMakroBot() = MakroBot(name, head!!, body!!, hands!!, шасси!!)

    val гусеницы = Chassis.Caterpillar(0)
    val ноги = Chassis.Legs
    fun колеса(settings: WheelCreator.() -> Unit): Chassis.Wheel {
        return WheelCreator().apply(settings).getWheel()
    }
}

fun робот(name: String, settings: MakroBotCreate.() -> Unit) =
    MakroBotCreate(name).apply(settings).getMakroBot()