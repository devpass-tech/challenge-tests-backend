package io.devpass.creditcard.data.extensions

import java.util.Optional

fun <T> Optional<T>.getOrNull(): T? {
    return if (this.isPresent) this.get() else null
}