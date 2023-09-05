package csc.markobot.dsl

import csc.markobot.api.*

infix fun Chassis.Caterpillar.шириной(width: Int) = Chassis.Caterpillar(width)

@MakroBotDsl
class WheelCreator {
    var диаметр = 0
    var количество = 0
    fun getWheel() = Chassis.Wheel(количество, диаметр)
}