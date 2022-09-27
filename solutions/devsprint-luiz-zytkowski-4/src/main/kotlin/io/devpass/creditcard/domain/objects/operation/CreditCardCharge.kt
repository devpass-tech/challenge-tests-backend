package io.devpass.creditcard.domain.objects.operation

data class CreditCardCharge(
    val creditCardId: String,
    val value: Double,
    val installments: Int,
    val description: String,
)
