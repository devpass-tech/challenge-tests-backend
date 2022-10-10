package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domainaccess.ICreditCardServiceAdapter
import io.devpass.creditcard.transport.requests.CreditCardCreationRequest
import io.mockk.every
import io.mockk.mockk
import org.hibernate.TransactionException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class CreditCardControllerTest {

    @Test
    fun `Should successfully return a CreditCard`() {
        val creditCardReference = getRandomCreditCard()
        val creditCardServiceAdapter = mockk<ICreditCardServiceAdapter> {
            every { findCreditCardById(any()) } returns creditCardReference
        }
        val creditCardController = CreditCardController(creditCardServiceAdapter)
        val result = creditCardController.findCreditCard("")
        Assertions.assertEquals(creditCardReference, result)
    }

    @Test
    fun `Should raise an EntityNotFoundException when findCreditCardById returns null`() {
        val creditCardServiceAdapter = mockk<ICreditCardServiceAdapter> {
            every { findCreditCardById(any()) } returns null
        }
        val creditCardController = CreditCardController(creditCardServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardController.findCreditCard("")
        }
    }

    @Test
    fun `Should leak and exception whem findCreditCardById throws and exception himself`() {
        val creditCardServiceAdapter = mockk<ICreditCardServiceAdapter> {
            every { findCreditCardById(any()) } throws TransactionException("Forced exception for unit testing purposes")
        }
        val creditCardController = CreditCardController(creditCardServiceAdapter)
        assertThrows<TransactionException> {
            creditCardController.findCreditCard("")
        }
    }

    @Test
    fun `Should request a credit card`() {
        val creditCardCreationRequest = CreditCardCreationRequest(taxId = "", printedName = "")
        val randomCreditCardReference = getRandomCreditCard()
        val creditCardServiceAdapter = mockk<ICreditCardServiceAdapter> {
            every { requestCreation(any()) } returns randomCreditCardReference
        }
        val creditCardController = CreditCardController(creditCardServiceAdapter)
        val result = creditCardController.requestCreditCard(creditCardCreationRequest)
        Assertions.assertEquals(randomCreditCardReference, result)
    }

    @Test
    fun `Should raise an exception`() {
        val creditCardCreationRequest = CreditCardCreationRequest(taxId = "", printedName = "")
        val creditCardServiceAdapter = mockk<ICreditCardServiceAdapter> {
            every { requestCreation(any()) } throws Exception("Throw Exception for testing")
        }
        val creditCardController = CreditCardController(creditCardServiceAdapter)
        assertThrows<Exception> {
            creditCardController.requestCreditCard(creditCardCreationRequest)
        }
    }

    private fun getRandomCreditCard(): CreditCard {
        return CreditCard(
            id = "",
            owner = "",
            number = "",
            securityCode = "",
            printedName = "",
            creditLimit = 0.0,
            availableCreditLimit = 0.0,
        )
    }
}