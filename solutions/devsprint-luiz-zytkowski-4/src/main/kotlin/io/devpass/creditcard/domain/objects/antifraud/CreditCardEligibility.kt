package io.devpass.creditcard.domain.objects.antifraud

data class CreditCardEligibility(
    val shouldHaveCreditCard: Boolean,
    val proposedLimit: Double?
)