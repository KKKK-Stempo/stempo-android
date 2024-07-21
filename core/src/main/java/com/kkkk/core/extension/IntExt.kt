package com.kkkk.core.extension

import android.content.Context
import android.util.TypedValue
import java.text.DecimalFormat

fun Int.dpToPx(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

fun Int.setNumberForm(): String {
    val decimal = DecimalFormat("#,###")
    return decimal.format(this)
}

fun Int.setOverThousand(): String =
    if (this < 1000) {
        this.toString()
    } else {
        "999+"
    }
