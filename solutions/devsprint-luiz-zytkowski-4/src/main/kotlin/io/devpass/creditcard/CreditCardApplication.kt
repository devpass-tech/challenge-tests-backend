package io.devpass.creditcard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreditCardApplication

fun main(args: Array<String>) {
	runApplication<CreditCardApplication>(*args)
}
