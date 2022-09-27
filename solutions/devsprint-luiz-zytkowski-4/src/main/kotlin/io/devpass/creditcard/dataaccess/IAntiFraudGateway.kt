package io.devpass.creditcard.dataaccess

import io.devpass.creditcard.domain.objects.antifraud.CreditCardEligibility

interface IAntiFraudGateway {
    fun creditCardEligibility(CPF: String): CreditCardEligibility
}