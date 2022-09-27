package io.devpass.creditcard.domain.creditcard

import java.util.Random

object CreditCardNumberGenerator {
    fun generateCreditCardSecurityNumber(length: Int) : String {
        val builder = StringBuilder()
        builder.append(generateRandomNumber(length))
        return builder.toString()
    }

    fun generateCreditCardNumber(bin: String, length: Int): String {
        val randomNumberLength = length - (bin.length + 1)
        val builder = StringBuilder(bin)
        builder.append(generateRandomNumber(randomNumberLength))

        val checkDigit = getCheckDigit(builder.toString())
        builder.append(checkDigit)
        return builder.toString()
    }

    private val randomNumber = Random(System.currentTimeMillis())

    private fun generateRandomNumber(randomNumberLength: Int) : String{
        val builder = StringBuilder()
        for (i in 0 until randomNumberLength) {
            val digit = randomNumber.nextInt(10)
            builder.append(digit)
        }
        return builder.toString()
    }

    private fun getCheckDigit(number: String): Int {
        var sum = 0
        for (i in 0 until number.length) {
            var digit = number.substring(i, i + 1).toInt()
            if (i % 2 == 0) {
                digit = digit * 2
                if (digit > 9) {
                    digit = digit / 10 + digit % 10
                }
            }
            sum += digit
        }

        val mod = sum % 10
        return if (mod == 0) 0 else 10 - mod
    }
}