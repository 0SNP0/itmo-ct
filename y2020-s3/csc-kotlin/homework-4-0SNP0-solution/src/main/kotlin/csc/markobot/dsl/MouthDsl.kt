package csc.markobot.dsl

import csc.markobot.api.*

@MakroBotDsl
class MouthCreator {
    var speaker: Speaker? = null
}

@MakroBotDsl
class SpeakerCreator {
    var мощность = 0

    fun getSpeaker() = Speaker(мощность)
}

fun MouthCreator.динамик(settings: SpeakerCreator.() -> Unit) {
    this.speaker = SpeakerCreator().apply(settings).getSpeaker()
}

fun HeadCreator.рот(settings: MouthCreator.() -> Unit) {
    this.mouth = Mouth(MouthCreator().apply(settings).speaker)
}