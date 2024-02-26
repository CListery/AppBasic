@file:Suppress("unused")
@file:JvmName("ExtCharSequence")

package com.kotlin

fun String.Companion.safeFormat(format: String, vararg args: Any?): String {
    val safeArgs = args.map { it.safeGet { "" } }.toTypedArray()
    return String.format(format, *safeArgs)
}

fun <T : CharSequence> Array<out T?>.filterNotEmpty(): List<T> {
    return filterNotNull().filterNotTo(arrayListOf()) { it.isEmpty() }
}

fun CharSequence.filterNumber(): String {
    var number = ""
    forEach { c ->
        if(c.isDigit()) {
            number += c
        }
    }
    return number
}

fun CharSequence.decodeUnicodeString(): String {
    if(isBlank()) {
        return ""
    }
    var pos = 0
    val sb = StringBuilder()
    fun readEscapeCharacter(): Char {
        return when(val escaped = get(pos++)) {
            'u'  -> {
                if(pos + 4 > length) {
                    throw IllegalArgumentException("Unterminated escape sequence: $this")
                }
                val hex = substring(pos, pos + 4)
                pos += 4
                hex.toInt(16).toChar()
            }
            
            't'  -> '\t'
            'b'  -> '\b'
            'n'  -> '\n'
            'r'  -> '\r'
            'f'  -> '\u000c'
            else -> escaped
        }
    }
    while(pos < length) {
        val c = get(pos++)
        if(c == '\\') {
            if(pos == length) {
                break
            }
            val escapeCharacter = readEscapeCharacter()
            sb.append(escapeCharacter)
        } else {
            sb.append(c)
        }
    }
    return sb.toString()
}

fun CharSequence.encodeUnicodeString(): String {
    if(isBlank()) {
        return ""
    }
    var pos = 0
    val sb = StringBuilder()
    
    while(pos < length) {
        val c = get(pos++)
        when {
            c == '/'     -> {
                sb.append('\\').append('/')
            }
            
            c.code < 128 -> {
                sb.append(c)
            }
            
            else         -> {
                sb.append("\\u").append(String.format("%04x", c.code))
            }
        }
    }
    
    return sb.toString()
}
