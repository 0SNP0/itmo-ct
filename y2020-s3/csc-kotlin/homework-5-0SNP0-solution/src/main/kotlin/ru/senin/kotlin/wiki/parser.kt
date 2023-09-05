package ru.senin.kotlin.wiki

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

const val WORD_MIN = 3
const val DATE_LEN = 20

object RusLetters {
    private val ranges = listOf('А'..'Я', 'а'..'я')
    operator fun contains(ch: Char) = ranges.any { ch in it }
}

fun parse(input: InputStream, stat: Statistics = Statistics()) {
    val parser = SAXParserFactory.newInstance().newSAXParser()
    val handler = WikiHandler(stat)
    parser.parse(input, handler)
}

data class PageStat(
    var title: HashMap<String, Int>? = null,
    var text: HashMap<String, Int>? = null,
    var bytes: Int? = null,
    var year: Int? = null
) {
    fun isCorrect() = title != null && text != null && bytes != null && year != null
}

enum class Tag(val needContent: Boolean) {
    TITLE(true), TIMESTAMP(true), TEXT(true),
    PAGE(false), OTHER(false);

    companion object {
        fun of(tags: List<String>) = when (tags) {
            listOf("mediawiki", "page") -> PAGE
            listOf("mediawiki", "page", "title") -> TITLE
            listOf("mediawiki", "page", "revision", "timestamp") -> TIMESTAMP
            listOf("mediawiki", "page", "revision", "text") -> TEXT
            else -> OTHER
        }
    }
}

class WikiHandler(private val allStat: Statistics = Statistics()) : DefaultHandler() {
    private var pageStat = PageStat()
    private val tags = mutableListOf<String>()
    private val currentTag
        get() = tags.lastOrNull() ?: ""
    private var tagContentBuffer = StringBuffer()

    override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
        requireNotNull(qName)

        tags += qName
        when (Tag.of(tags)) {
            Tag.TITLE -> {
                pageStat.title = HashMap()
                tagContentBuffer = StringBuffer()
            }
            Tag.TEXT -> {
                pageStat.bytes = attributes?.getValue("bytes")?.toInt()
                pageStat.text = HashMap()
                tagContentBuffer = StringBuffer()
            }
            Tag.TIMESTAMP -> tagContentBuffer = StringBuffer()
            Tag.PAGE, Tag.OTHER -> Unit
        }
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        require(qName == currentTag) { "not well-formed xml" }

        when (Tag.of(tags)) {
            Tag.PAGE -> {
                allStat.addPage(pageStat)
                pageStat = PageStat()
            }
            Tag.TITLE -> findRusWords { pageStat.title?.merge(it) }
            Tag.TEXT -> findRusWords { pageStat.text?.merge(it) }
            Tag.TIMESTAMP -> pageStat.year = getYear()
            Tag.OTHER -> Unit
        }

        require(tags.isNotEmpty()) { "not well-formed xml" }
        tags.removeLast()
    }

    override fun endDocument() {
        require(tags.isEmpty()) { "not well-formed xml" }
    }

    override fun characters(ch: CharArray?, start: Int, length: Int) {
        if (ch == null) return

        if (Tag.of(tags).needContent) {
            tagContentBuffer.append(String(ch, start, length))
        }
    }

    private fun getYear(): Int {
        require(tagContentBuffer.length == DATE_LEN) {
            "not correct timestamp - expected length: $DATE_LEN, but got ${tagContentBuffer.length}"
        }
        return tagContentBuffer.substring(0, 4).toInt()
    }

    private fun findRusWords(add: (String) -> Unit) {
        val content = tagContentBuffer.toString()
        var start = 0

        for (pos in 0..content.length) {
            if (pos < content.length && content[pos] in RusLetters) continue

            if (pos - start >= WORD_MIN) {
                add(content.substring(start, pos).lowercase())
            }
            start = pos + 1
        }
    }
}