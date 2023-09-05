package csc.markobot.dsl

import csc.markobot.api.*

@MakroBotDsl
data class MaterialCreator(var thickness: Int = 0, val create: (Int) -> Material) {
    fun getMaterial() = create(thickness)

    infix fun толщиной(thickness: Int) {
        this.thickness = thickness
    }
}

open class Materiable {
    open var material: Material? = null

    open var пластик = MaterialCreator { Plastic(it) }
    open var металл = MaterialCreator { Metal(it) }

    open fun setMaterial() {
        if (пластик.thickness != 0) {
            material = пластик.getMaterial()
        }
        if (металл.thickness != 0) {
            material = металл.getMaterial()
        }
    }
}