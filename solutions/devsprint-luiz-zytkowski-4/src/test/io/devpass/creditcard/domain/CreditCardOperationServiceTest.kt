package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.CreditCardOperationTypes
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardOperationServiceTest {
    @Test
    fun `Should successfully rollback`() {
        val creditCardReference = getCreditCardRollback()
        val creditCardOperationReference = getCreditCardOperationRollback().copy(type = CreditCardOperationTypes.CHARGE )
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference

            every { update(any()) } just runs
        }
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { getOperationById(any())} returns creditCardOperationReference

            every { create(any()) } returns creditCardOperationReference
        }
        assertDoesNotThrow {
            CreditCardOperationService(
                    creditCardDAO,
                    creditCardInvoiceDAO,
                    creditCardOperationDAO,
            ).rollback("")
        }
    }

    @Test
    fun `Should leak and exception when rollback throws and exception himself`() {
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } throws EntityNotFoundException("Credit card not found")

            every { update(any()) } just runs
        }
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { getOperationById(any())} throws EntityNotFoundException("Operation not found with ID")

            every { create(any()) }  throws EntityNotFoundException("You cannot rollback an operation that isn't of type")
        }

        assertThrows<EntityNotFoundException> {
            CreditCardOperationService(
                    creditCardDAO,
                    creditCardInvoiceDAO,
                    creditCardOperationDAO,
            ).rollback("")
        }
    }
    @Test
    fun `Should successfully return a CreditCardOperationId`() {
        val creditCardReference = getRandomCreditCard()
        val creditCardOperationReference = getRandomCreditCardOperation()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { getOperationById(any()) } returns creditCardOperationReference
        }
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardOperationService =
                CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        val result = creditCardOperationService.getById("")
        Assertions.assertEquals(creditCardOperationReference, result)
    }

    @Test
    fun `Should leak and exception when findCreditCardById throws and exception himself`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { getOperationById(any()) } throws EntityNotFoundException("Forced exception for unit testing purposes")
        }
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } throws EntityNotFoundException("Forced exception for unit testing purposes")
        }
        val creditCardOperationService =
                CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<EntityNotFoundException> {
            creditCardOperationService.getById("")
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
    private fun getCreditCardRollback(): CreditCard {
        return CreditCard(
                id = "",
                owner = "",
                number = "",
                securityCode = "",
                printedName = "",
                creditLimit = 50.0,
                availableCreditLimit = 0.0,
        )
    }

    private fun getCreditCardOperationRollback(): CreditCardOperation {
        return CreditCardOperation(
                id = "",
                creditCard = "",
                type = "",
                value = 20.0,
                month = 0,
                year = 0,
                description = "",
                createdAt = LocalDateTime.now(),
        )
    }
}

