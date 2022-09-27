package io.devpass.creditcard.dataaccess

import io.devpass.creditcard.domain.objects.CreditCard

interface ICreditCardDAO {
    fun getById(id: String) : CreditCard?
    fun getByTaxId(taxId : String) : CreditCard?
    fun create(creditCard: CreditCard) : CreditCard
    fun update(creditCard: CreditCard)
}