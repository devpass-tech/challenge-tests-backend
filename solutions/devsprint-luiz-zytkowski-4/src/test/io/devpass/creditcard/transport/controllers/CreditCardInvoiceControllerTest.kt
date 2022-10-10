package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.exceptions.OwnedException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import io.devpass.creditcard.transport.requests.InvoiceCreationRequest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.time.LocalDateTime
import org.hibernate.TransactionException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreditCardInvoiceControllerTest {

    @Test
    fun `Should successfully return a period`() {
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getByPeriod(any(), any(), any()) } returns creditCardInvoiceReference
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.getByPeriod("", month = 1, year = 1)
        assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `Should raise an EntityNotFoundException when getByPeriod returns null`() {
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getByPeriod(any(), any(), any()) } returns null
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceController.getByPeriod("", 1, 0)
        }
    }

    @Test
    fun `Should raise an exception when getByPeriod throws an exception`() {
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getByPeriod(any(), any(), any()) } throws TransactionException("Error")
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<TransactionException> {
            creditCardInvoiceController.getByPeriod("", 1, 0)
        }
    }

    @Test
    fun `should pay invoice sucessfully`() {
        val creditCardId = "1234"
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { payInvoice(any()) } just runs
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

    @Test
    fun `should throw an EntityNotFoundException when getById method returns null`() {
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getById(any()) } returns null
        }

        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)

        assertThrows<EntityNotFoundException> {
            creditCardInvoiceController.getById(creditCardInvoiceId = "")
        }
    }

    @Test
    fun `should get credit card invoice by ID`() {
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getById(any()) } returns creditCardInvoiceReference
        }

        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)

        val result = creditCardInvoiceController.getById(creditCardInvoiceId = "")
        assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `should leak an exception when getById method throws an exception`() {
        val creditCarInvoiceServicedapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getById(any()) } throws EntityNotFoundException("Forced exception for unit testing purposes")
        }

        val creditCardInvoiceController = CreditCardInvoiceController(creditCarInvoiceServicedapter)

        assertThrows<EntityNotFoundException> {
            creditCardInvoiceController.getById(creditCardInvoiceId = "")
        }
    }

    @Test
    fun `Should generate an invoice`() {
        val invoiceCreationRequest = InvoiceCreationRequest(creditCardId = "")
        val invoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = (mockk<ICreditCardInvoiceServiceAdapter> {
            every { generateInvoice(any()) } returns invoiceReference
        })
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.generateInvoice(invoiceCreationRequest)
        assertEquals(invoiceReference, result)
    }

    @Test
    fun `Should throw Exception`() {
        val invoiceCreationRequest = InvoiceCreationRequest(creditCardId = "")
        val creditCardInvoiceServiceAdapter = (mockk<ICreditCardInvoiceServiceAdapter> {
            every { generateInvoice(any()) } throws Exception("Throw Exception for testing")
        })
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<Exception> {
            creditCardInvoiceController.generateInvoice(invoiceCreationRequest)
        }
    }

    private fun getRandomCreditCardInvoice(): CreditCardInvoice {
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