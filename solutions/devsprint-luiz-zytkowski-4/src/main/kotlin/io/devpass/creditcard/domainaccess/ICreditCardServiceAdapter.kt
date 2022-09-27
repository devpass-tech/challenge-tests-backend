package io.devpass.creditcard.domainaccess

import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardCreation

interface ICreditCardServiceAdapter {
    fun findCreditCardById(creditCardId: String): CreditCard?
    fun requestCreation(creditCardCreation : CreditCardCreation) : CreditCard
}