package io.devpass.creditcard.dataaccess

import io.devpass.creditcard.domain.objects.CreditCardInvoice

interface ICreditCardInvoiceDAO {
    fun getInvoiceById(id: String): CreditCardInvoice?
    fun getByPeriod(creditCardId: String, month: Int, year: Int): CreditCardInvoice?
    fun update(creditCardInvoice: CreditCardInvoice)
    fun create(creditCardInvoice: CreditCardInvoice): CreditCardInvoice
}