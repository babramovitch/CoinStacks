package com.nebulights.coinstacks.Extensions

import android.content.res.Resources

inline fun <T : Any> T?.notNull(f: (it: T) -> Unit) {
    if (this != null) f(this)
}

fun String.isNumber(): Boolean {
    return try {
        this.toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()
fun ByteArray.toHex(): String {
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
