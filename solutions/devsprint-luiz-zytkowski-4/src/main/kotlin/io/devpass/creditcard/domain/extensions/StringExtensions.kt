package io.devpass.creditcard.domain.extensions

fun String.validateAsCPF(): Boolean {
    if (this.all { it == this[0] } || this.length != 11 || this.isEmpty())
        return false

    val dig10: Char
    val dig11: Char
    var soma: Int
    var iterator: Int
    var result: Int
    var num: Int
    var peso: Int

    return try {
        soma = 0
        peso = 10
        iterator = 0
        while (iterator < 9) {
            num = (this[iterator].code - 48)
            soma += num * peso
            peso -= 1
            iterator++
        }
        result = 11 - soma % 11
        dig10 = if (result == 10 || result == 11) '0' else (result + 48).toChar()
        soma = 0
        peso = 11
        iterator = 0
        while (iterator < 10) {
            num = (this[iterator].code - 48)
            soma += num * peso
            peso -= 1
            iterator++
        }
        result = 11 - soma % 11
        dig11 = if (result == 10 || result == 11) '0' else (result + 48).toChar()

        dig10 == this[9] && dig11 == this[10]
    } catch (error: Exception) {
        false
    }
}