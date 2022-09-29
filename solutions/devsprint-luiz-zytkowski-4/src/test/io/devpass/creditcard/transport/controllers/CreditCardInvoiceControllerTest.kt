package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.exceptions.OwnedException
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreditCardInvoiceControllerTest {

    @Test
    fun `should pay invoice sucessfully`() {
        val creditCardId = "1234"
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { payInvoice(any()) } returns Unit
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.payInvoice(creditCardId)

        assertEquals("Invoice paid successfully", result)
    }

    @Test
    fun `should throw an Exception`() {
        val creditCardId = "1234"
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { payInvoice(any()) } throws OwnedException("OwnedException for unit testing purposes")
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)

        assertThrows<OwnedException> {
            creditCardInvoiceController.payInvoice(creditCardId)
        }
    }
}