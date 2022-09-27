package io.devpass.creditcard.domainaccess

import io.devpass.creditcard.domain.objects.CreditCardInvoice

interface ICreditCardInvoiceServiceAdapter {
    fun getById(creditCardInvoiceId: String): CreditCardInvoice?
    fun getByPeriod(creditCardId: String, month: Int, year: Int): CreditCardInvoice?
    fun payInvoice(invoiceId: String)
    fun generateInvoice(creditCardId: String): CreditCardInvoice
}