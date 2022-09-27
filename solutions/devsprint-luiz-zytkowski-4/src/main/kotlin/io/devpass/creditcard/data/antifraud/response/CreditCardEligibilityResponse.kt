package io.devpass.creditcard.data.antifraud.response

import io.devpass.creditcard.domain.objects.antifraud.CreditCardEligibility

data class CreditCardEligibilityResponse(
    val shouldHaveCreditCard: Boolean,
    val proposedLimit: Double?,
) {
    fun toCreditCardEligibility(): CreditCardEligibility {
        return CreditCardEligibility(
            shouldHaveCreditCard = shouldHaveCreditCard,
            proposedLimit = proposedLimit
        )
    }
}