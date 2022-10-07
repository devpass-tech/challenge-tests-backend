package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.CreditCardOperationTypes
import io.devpass.creditcard.domain.objects.operation.CreditCardCharge
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.CreditCardOperationTypes
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardOperationServiceTest {

    @Test
    fun `should execute charge method and return a list of credit card operations`() {

        val creditCardChargeReference = getRandomCreditCardCharge()
        val creditCardOperation = getRandomCreditCardOperation()

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns getRandomCreditCard()
            every { update(any()) } just Runs
        }

        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every {
                getByPeriod(
                    any(),
                    any(),
                    any()
                )
            } returns CreditCardInvoice(
                id = "",
                creditCard = "",
                month = 1,
                year = 2022,
                value = 100.0,
                createdAt = LocalDateTime.now(),
                paidAt = LocalDateTime.now(),
            ) andThen (null)
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { create(any()) } returns creditCardOperation
        }

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardInvoiceDAO,
            creditCardOperationDAO,
        )

        val result = creditCardOperationService.charge(creditCardChargeReference)

        Assertions.assertEquals(listOf(creditCardOperation), result)

    }

    @Test
    fun `should throw a BusinessRuleException when credit card charge value is less than 0`() {
        val creditCardChargeReference = getRandomCreditCardCharge().copy(value = -1.0)

        val creditCardDAO = mockk<ICreditCardDAO>()

        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()

        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardInvoiceDAO,
            creditCardOperationDAO,
        )

        assertThrows<BusinessRuleException> { creditCardOperationService.charge(creditCardChargeReference) }
    }

    @Test
    fun `should throw a BusinessRuleException when credit card charge installments is less than 1 or greater than 12`() {
        val creditCardChargeReference = getRandomCreditCardCharge().copy(installments = 13)
        val creditCardChargeReferenceTwo = getRandomCreditCardCharge().copy(installments = 0)

        val creditCardDAO = mockk<ICreditCardDAO>()

        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()

        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardInvoiceDAO,
            creditCardOperationDAO,
        )

        assertThrows<BusinessRuleException> {
            creditCardOperationService.charge(creditCardChargeReference)
            creditCardOperationService.charge(creditCardChargeReferenceTwo)
        }
    }
    
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
    fun `Should leak an exception when the operation has not valid ID`() {
        val creditCardDAO = mockk<ICreditCardDAO> ()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { getOperationById(any())} returns null
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
    fun `Should leak an exception when the credit card has not valid ID`() {
        val creditCardOperationReference = getCreditCardOperationRollback().copy(type = CreditCardOperationTypes.CHARGE )
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns null
        }
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>{
            every { getOperationById(any())} returns creditCardOperationReference
            every { create(any()) } returns creditCardOperationReference
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
    fun `Should return a BusinessRuleException when operation not is a type charge`() {
        val creditCardOperationReference = getCreditCardOperationRollback().copy(type = CreditCardOperationTypes.INVOICE_PAYMENT )
        val creditCardDAO = mockk<ICreditCardDAO> ()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>{
            every { getOperationById(any())} returns creditCardOperationReference
        }
        assertThrows<BusinessRuleException> {
            CreditCardOperationService(
                    creditCardDAO,
                    creditCardInvoiceDAO,
                    creditCardOperationDAO,
            ).rollback("")
        }
    }

    @Test
    fun `should throw a BusinessRuleException when installments if greater than 1 and credit card charge value is less than 6`() {

        val creditCardChargeReference = getRandomCreditCardCharge().copy(installments = 3, value = 5.0)

        val creditCardDAO = mockk<ICreditCardDAO>()

        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()

        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardInvoiceDAO,
            creditCardOperationDAO,
        )

        assertThrows<BusinessRuleException> {
            creditCardOperationService.charge(creditCardChargeReference)
        }
    }
    
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
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> ()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> ()
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<BusinessRuleException> {
            creditCardOperationService.listByPeriod("", 12, -1)
        }
    }

    @Test
    fun `Should return a BusinessRuleException when month is invalid`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<BusinessRuleException> {
            creditCardOperationService.listByPeriod("", 13, 2000)
        }
    }

    @Test
    fun `should throw a BusinessRuleException when availabe credit limit is less than the credit card charge value`() {
        val creditCardCharge = getRandomCreditCardCharge().copy(value = 500.0)
        val creditCard = getRandomCreditCard().copy(availableCreditLimit = 50.0)

        val creditCardDAO = mockk<ICreditCardDAO>() {
            every { getById(any()) } returns creditCard
        }
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardInvoiceDAO,
            creditCardOperationDAO,
        )

        assertThrows<BusinessRuleException> { creditCardOperationService.charge(creditCardCharge) }
    }

    @Test
    fun `should throw an EntityNotFoundException when credit card not found`() {
        val creditCardCharge = getRandomCreditCardCharge()

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns null
        }
        val creditCardIvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardIvoiceDAO,
            creditCardOperationDAO,
        )

        assertThrows<EntityNotFoundException> { creditCardOperationService.charge(creditCardCharge) }
    }
    
    @Test
    fun `Should return a BusinessRuleException when month is negative`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val creditCardOperationService =
            CreditCardOperationService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO)
        assertThrows<BusinessRuleException> {
            creditCardOperationService.listByPeriod("", -1, 2000)
        }
    }

    @Test
    fun `Should return a EntityNotFoundException when credit card is null`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) }  returns null
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
        assertEquals(creditCardOperationReference, result)
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
            creditLimit = 1000.0,
            availableCreditLimit = 1000.0,
        )
    }

    private fun getRandomCreditCardCharge(): CreditCardCharge {
        return CreditCardCharge(
            creditCardId = "1",
            value = 5.0,
            installments = 1,
            description = " "
        )
    }

    private fun getRandomCreditCardInvoice(): CreditCardInvoice {
        return CreditCardInvoice(
            id = "",
            creditCard = "",
            month = 1,
            year = 2022,
            value = 100.0,
            createdAt = LocalDateTime.now(),
            paidAt = LocalDateTime.now(),
        )
    }

    private fun getRandomCreditCardOperation(): CreditCardOperation {
        return CreditCardOperation(
            id = "",
            creditCard = "",
            type = CreditCardOperationTypes.CHARGE,
            value = 10.0,
            month = 1,
            year = 2,
            description = "",
            createdAt = LocalDateTime.now(),
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