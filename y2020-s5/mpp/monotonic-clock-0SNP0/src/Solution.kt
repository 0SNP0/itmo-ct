/**
 * В теле класса решения разрешено использовать только переменные делегированные в класс RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author Селиванов Николай
 */
class Solution : MonotonicClock {
    private var c1 by RegularInt(0)
    private var c2 by RegularInt(0)
    private var c3 by RegularInt(0)
    private var c11 by RegularInt(0)
    private var c21 by RegularInt(0)

    override fun write(time: Time) {
        // write right-to-left
        c1 = time.d1
        c2 = time.d2
        c3 = time.d3
        c21 = time.d2
        c11 = time.d1
    }

    override fun read(): Time = // read left-to-right
        listOf(c11, c21, c3, c2, c1).let { (r11, r21, r3, r2, r1) ->
            Time(
                r1,
                if (r11 == r1) r2 else 0,
                if (r11 == r1 && r21 == r2) r3 else 0
            )
        }
}