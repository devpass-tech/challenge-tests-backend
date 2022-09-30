package io.devpass.creditcard.transport

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domainaccess.ICreditCardOperationServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardOperationController
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardOperationControllerTest {

    @Test
    fun `Should get credit card operation by ID`() {
        val creditCardOperationReference = getRandomCreditCardOperation()
        val creditCardOperationServiceAdapter = mockk<ICreditCardOperationServiceAdapter> {
            every { getById(any()) } returns creditCardOperationReference
        }
        val creditCardOperationController = CreditCardOperationController(creditCardOperationServiceAdapter)
        val result = creditCardOperationController.getById(creditCardOperationId = "")
        Assertions.assertEquals(creditCardOperationReference, result)
    }

    @Test
    fun `Should throw an EntityNotFoundException when getById method returns null`() {
        val creditCardOperationServiceAdapter = mockk<ICreditCardOperationServiceAdapter> {
            every { getById(any()) } returns null
        }
        val creditCardOperationController = CreditCardOperationController(creditCardOperationServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardOperationController.getById(creditCardOperationId = "")
        }
    }

    @Test
    fun `Should leak an exception when getById method throws an exception`() {
        val creditCardOperationServiceAdapter = mockk<ICreditCardOperationServiceAdapter> {
            every { getById(any()) } throws Exception("Throws Exception for testing")
        }
        val creditCardOperationController = CreditCardOperationController(creditCardOperationServiceAdapter)
        assertThrows<Exception> {
            creditCardOperationController.getById(creditCardOperationId = "")
        }
    }

    private fun getRandomCreditCardOperation(): CreditCardOperation {
        return CreditCardOperation(
            id = "",
            creditCard = "",
            type = "",
            value = 0.0,
            month = 0,
            year = 0,
            description = "",
            createdAt = LocalDateTime.now(),
        )
    }
}