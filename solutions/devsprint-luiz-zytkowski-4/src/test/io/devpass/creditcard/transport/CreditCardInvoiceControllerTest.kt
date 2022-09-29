package io.devpass.creditcard.transport

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardInvoiceController
import io.devpass.creditcard.transport.requests.InvoiceCreationRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardInvoiceControllerTest() {

    @Test
    fun `Should generate an invoice`() {
        val invoiceCreationRequest = InvoiceCreationRequest(creditCardId = "")
        val invoiceReference = mockCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = (mockk<ICreditCardInvoiceServiceAdapter> {
            every { generateInvoice(any()) } returns invoiceReference
        })
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.generateInvoice(invoiceCreationRequest)
        assertEquals(invoiceReference, result)
    }

    @Test
    fun `Should throw EntityNotFoundException`() {
        val invoiceCreationRequest = InvoiceCreationRequest(creditCardId = "")
        val creditCardInvoiceServiceAdapter = (mockk<ICreditCardInvoiceServiceAdapter> {
            every { generateInvoice(any()) } throws Exception("Throw Exception for testing")
        })
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<Exception> {
            creditCardInvoiceController.generateInvoice(invoiceCreationRequest)
        }
    }

    private fun mockCreditCardInvoice(): CreditCardInvoice {
        return CreditCardInvoice(
            id = "",
            creditCard = "",
            month = 0,
            year = 0,
            value = 0.0,
            createdAt = LocalDateTime.now(),
            paidAt = null,
        )
    }
}