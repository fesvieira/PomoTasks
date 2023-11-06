package com.fesvieira.pomotasks.helpers

val Int.formatToString get(): String {
    return if (this > 9) "$this" else "0$this"
}