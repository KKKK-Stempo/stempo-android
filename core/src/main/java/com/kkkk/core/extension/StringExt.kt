package com.kkkk.core.extension

import java.text.BreakIterator

fun String?.isJsonObject(): Boolean = this?.startsWith("{") == true && this.endsWith("}")

fun String?.isJsonArray(): Boolean = this?.startsWith("[") == true && this.endsWith("]")

fun String.getGraphemeLength(): Int {
    val breakIterator: BreakIterator = BreakIterator.getCharacterInstance()

    breakIterator.setText(this)

    var count = 0
    while (breakIterator.next() != BreakIterator.DONE) {
        count++
    }

    return count
}

fun String.breakLines(): String {
    return this.replace(" ", "\u00A0")
}
