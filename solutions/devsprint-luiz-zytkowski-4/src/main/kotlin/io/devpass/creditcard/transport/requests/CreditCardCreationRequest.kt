package io.devpass.creditcard.transport.requests

import io.devpass.creditcard.domain.objects.CreditCardCreation

data class CreditCardCreationRequest(
    val taxId: String,
    val printedName: String,
) {
    fun toCreditCardCreation(): CreditCardCreation {
        return CreditCardCreation(taxId, printedName)
    }
}
