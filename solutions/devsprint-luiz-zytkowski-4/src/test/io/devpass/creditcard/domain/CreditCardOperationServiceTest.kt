package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardOperationServiceTest {

    @Test
    fun `Should successfully list operations by period`() {
        val creditCardOperationReference = getRandomCreditCardOperations()
        val creditCardReference = getRandomCreditCard()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every {
                listByPeriod(
                    creditCardId = "",
                    month = 1,
                    year = 2000
                )
            } returns creditCardOperationReference
        }
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        val result = creditCardOperationService.listByPeriod("", month = 1, year = 2000)
        assertEquals(creditCardOperationReference, result)
    }

    @Test
    fun `Should return a BusinessRuleException when year is invalid`() {
        val creditCardReference = getRandomCreditCard()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every {
                listByPeriod(
                    creditCardId = "",
                    month = 12,
                    year = 0
                )
            } throws BusinessRuleException("You must provide a valid year to list operations")
        }
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<BusinessRuleException> {
            creditCardOperationService.listByPeriod("", 12, 0)
        }
    }

    @Test
    fun `Should return a BusinessRuleException when month is invalid`() {
        val creditCardReference = getRandomCreditCard()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every {
                listByPeriod(
                    creditCardId = "",
                    month = 13,
                    year = 2000
                )
            } throws BusinessRuleException("You must provide a valid month to list operations")
        }
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<BusinessRuleException> {
            creditCardOperationService.listByPeriod("", 13, 2000)
        }
    }

    @Test
    fun `Should return a EntityNotFoundException when credit card is null`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) }  throws EntityNotFoundException("Credit card not found with ID")
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every {
                listByPeriod(
                    creditCardId = "",
                    month = 12,
                    year = 2000
                )
            } throws EntityNotFoundException("Credit card not found with ID")
        }
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<EntityNotFoundException> {
            creditCardOperationService.listByPeriod("", 12, 2000)
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

    private fun getRandomCreditCardOperations(): List<CreditCardOperation> {
        return listOf(
            CreditCardOperation(
                id = "",
                creditCard = "",
                type = "",
                value = 0.0,
                month = 0,
                year = 0,
                description = "",
                createdAt = LocalDateTime.now()
            )
        )
    }

}