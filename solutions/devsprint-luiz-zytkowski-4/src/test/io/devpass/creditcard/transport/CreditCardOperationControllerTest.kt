package io.devpass.creditcard.transport

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domainaccess.ICreditCardOperationServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardOperationController
import io.mockk.every
import io.mockk.justRun
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

    @Test
    fun `Should rollback an operation successfully`() {
        val creditCardOperationServiceAdapter = mockk<ICreditCardOperationServiceAdapter> {
            justRun { rollback("FAKE-OPERATION-ID") }
        }
        val creditCardOperationId = "FAKE-OPERATION-ID"
        val creditCardOperationController = CreditCardOperationController(creditCardOperationServiceAdapter)
        val result = creditCardOperationController.rollback("FAKE-OPERATION-ID")
        Assertions.assertEquals("Operation $creditCardOperationId was rolled back successfully", result)
    }

    @Test
    fun `Should leak and exception when rollback throws and exception himself`() {
        val creditCardOperationServiceAdapter = mockk<ICreditCardOperationServiceAdapter> {
            every { rollback("FAKE-OPERATION-ID") } throws EntityNotFoundException("Forced exception for unit testing purposes")
        }
        val creditCardOperationController = CreditCardOperationController(creditCardOperationServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardOperationController.rollback("FAKE-OPERATION-ID")
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

    @Test
    fun `Should successfully return a list of operations using listByPeriod method`() {
        val creditCardOperationReference = getRandomListByPeriod()
        val creditCardOperationServiceAdapter = mockk<ICreditCardOperationServiceAdapter> {
            every { listByPeriod(any(), any(), any()) } returns creditCardOperationReference
        }
        val creditCardOperationController = CreditCardOperationController(creditCardOperationServiceAdapter)
        val result = creditCardOperationController.listByPeriod("", 0, 0)
        Assertions.assertEquals(creditCardOperationReference, result)
    }

    private fun getRandomListByPeriod(): List<CreditCardOperation> {
        return listOf(
                CreditCardOperation(
                        id = "",
                        creditCard = "",
                        type = "",
                        value = 0.0,
                        month = 0,
                        year = 0,
                        description = "",
                        createdAt = LocalDateTime.now(),
                )
        )
    }
}