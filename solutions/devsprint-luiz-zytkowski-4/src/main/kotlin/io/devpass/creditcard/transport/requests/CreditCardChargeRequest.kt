package io.devpass.creditcard.transport.requests

import io.devpass.creditcard.domain.objects.operation.CreditCardCharge

data class CreditCardChargeRequest(
    val creditCardId: String,
    val value: Double,
    val installments: Int,
    val description: String,
) {
    fun toCreditCardCharge(): CreditCardCharge {
        return CreditCardCharge(
            creditCardId,
            value,
            installments,
            description,
        )
    }
}
