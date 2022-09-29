package io.devpass.creditcard.transport

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardInvoiceController
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardInvoiceControllerTest {

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
            every { getById(any()) } returns getRandomCreditCardInvoice()
        }

        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)

        val result = creditCardInvoiceController.getById(creditCardInvoiceId = "")
        Assertions.assertEquals(creditCardInvoiceReference, result)
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

    private fun getRandomCreditCardInvoice(): CreditCardInvoice {
        return CreditCardInvoice(
            id = "",
            creditCard = "",
            month = 1,
            year = 2002,
            value = 1000.0,
            createdAt = LocalDateTime.now(),
            paidAt = LocalDateTime.now(),
        )
    }
}