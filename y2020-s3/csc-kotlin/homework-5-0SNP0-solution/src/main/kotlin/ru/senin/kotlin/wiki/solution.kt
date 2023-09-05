package ru.senin.kotlin.wiki

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import java.io.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.log10

const val STAT_LIMIT = 300

val endl = System.lineSeparator()

fun solve(parameters: Parameters) {
    val output = fileOutput(parameters.output)
    val maxTimeout = parameters.inputs.sumOf { it.length() }
    multiParse(parameters.inputs.map { bz2Input(it) }, parameters.threads, output, maxTimeout)
    output.close()
}

fun bz2Input(file: File) = BZip2CompressorInputStream(BufferedInputStream(FileInputStream(file)))
fun fileOutput(file: String) = BufferedWriter(OutputStreamWriter(FileOutputStream(file), "utf-8"))

fun multiParse(inputSet: List<InputStream>, threads: Int, output: Writer, maxTimeoutMs: Long = Long.MAX_VALUE) {
    val stat = Statistics()

    val threadPool = Executors.newFixedThreadPool(threads)
    threadPool.invokeAll(inputSet.map {
        Callable { parse(it, stat) }
    }, maxTimeoutMs, TimeUnit.MILLISECONDS)
    threadPool.shutdown()

    val titleStat = stat.title.toWordCountSet()
    val textStat = stat.text.toWordCountSet()

    output.write("Топ-$STAT_LIMIT слов в заголовках статей:$endl")
    printStat(titleStat, output, limit = STAT_LIMIT)

    output.write("${endl}Топ-$STAT_LIMIT слов в статьях:$endl")
    printStat(textStat, output, limit = STAT_LIMIT)

    output.write("${endl}Распределение статей по размеру:$endl")
    printStat(stat.bytes, output)

    output.write("${endl}Распределение статей по времени:$endl")
    printStat(stat.years, output)
}

private fun Map<String, Int>.toWordCountSet() =
    entries.mapTo(sortedSetOf(statComparator)) { WordCount(it.value, it.key) }

fun printStat(set: Set<WordCount>, writer: Writer, limit: Int = set.size) {
    set.asSequence().take(limit).forEach { (k, v) ->
        writer.write("$k $v$endl")
    }
}

fun printStat(map: SortedMap<Int, Int>, writer: Writer) {
    if (map.isEmpty()) return

    for (i in map.firstKey()..map.lastKey()) {
        writer.write("$i ${map.getOrDefault(i, 0)}$endl")
    }
}

data class WordCount(val count: Int, val word: String)

val statComparator = compareByDescending<WordCount> { it.count }.thenBy { it.word }

data class Statistics(
    val title: MutableMap<String, Int> = mutableMapOf(),
    val text: MutableMap<String, Int> = mutableMapOf(),
    val bytes: SortedMap<Int, Int> = sortedMapOf(),
    val years: SortedMap<Int, Int> = sortedMapOf()
) {
    fun addPage(stat: PageStat) {
        if (!stat.isCorrect()) return

        synchronized(this) {
            stat.title?.forEach { (k, v) -> title.merge(k, v) }
            stat.text?.forEach { (k, v) -> text.merge(k, v) }
            stat.bytes?.let { bytes.merge(log10(it.toDouble()).toInt()) }
            stat.year?.let { years.merge(it) }
        }
    }
}

fun <K> MutableMap<K, Int>.merge(key: K, value: Int = 1) =
    this.merge(key, value) { a, b -> a + b }
