package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domainaccess.ICreditCardOperationServiceAdapter
import io.devpass.creditcard.transport.requests.CreditCardChargeRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CreditCardOperationControllerTest {

    @Test
    fun `should return a list of credit card operations`() {
        val request = CreditCardChargeRequest(
            creditCardId = "",
            value = 0.0,
            installments = 1,
            description = "",

            )
        val creditCardOperation = getRandomCreditCardOperation()
        val creditCardOperationService = mockk<ICreditCardOperationServiceAdapter> {
            every { charge(any()) } returns listOf(creditCardOperation)
        }

        val creditCardOperationController = CreditCardOperationController(creditCardOperationService)

        val result = creditCardOperationController.charge(request)

        Assertions.assertEquals(listOf(creditCardOperation), result)
    }

    private fun getRandomCreditCardOperation(): CreditCardOperation {
        return CreditCardOperation(
            id = "",
            creditCard = "",
            type = "",
            value = 0.0,
            month = 1,
            year = 2022,
            description = "",
            createdAt = LocalDateTime.now(),
        )
    }
}