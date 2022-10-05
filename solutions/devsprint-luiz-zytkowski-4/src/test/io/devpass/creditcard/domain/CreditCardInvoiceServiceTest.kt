package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.ActionResponse
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.accountmanagement.Account
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardInvoiceServiceTest {

    @Test
    fun `Should successfully list operations by period`() {
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardReference = getRandomCreditCard()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every {
                getByPeriod(
                    creditCardId = "",
                    month = 1,
                    year = 2000
                )
            } returns creditCardInvoiceReference
        }
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        val result = creditCardInvoiceService.getByPeriod("", month = 1, year = 2000)
        Assertions.assertEquals(creditCardInvoiceReference, result)
    }


    @Test
    fun `Should return a BusinessRuleException when year is invalid`() {
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<BusinessRuleException> {
            creditCardInvoiceService.getByPeriod("", 12, -1)
        }
    }

    @Test
    fun `Should return a BusinessRuleException when month is invalid`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardOperationService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<BusinessRuleException> {
            creditCardOperationService.getByPeriod("", 13, 2000)
        }
    }

    @Test
    fun `Should return a BusinessRuleException when month is negative`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<BusinessRuleException> {
            creditCardInvoiceService.getByPeriod("", -1, 2000)
        }
    }

    @Test
    fun `Should return a EntityNotFoundException when credit card is null`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns null
        }
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceService.getByPeriod("", 12, 2000)
        }
    }

    @Test
    fun `Should successfully return a Credit Card Invoice`() {
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } returns creditCardInvoiceReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        val result = creditCardInvoiceService.getById("")
        assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `Should leak an exception when getById throws and exception himself`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } throws Exception("Forced exception for unit testing purposes")
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<Exception> {
            creditCardInvoiceService.getById("")
        }
    }

    @Test
    fun `Should successfully pay invoice`() {
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardOperationReference = getRandomCreditCardOperation()
        val creditCardReference = getRandomCreditCard()
        val accountReference = getRandomAccount()
        val actionResponseReference = getRandomActionResponse()
        val antiFraudGateway = mockk<IAccountManagementGateway> {
            every { getByCPF(any()) } returns accountReference
            every { withdraw(any()) } returns actionResponseReference
        }
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } returns creditCardInvoiceReference
            justRun { update(any()) }
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO> {
            every { create(any()) } returns creditCardOperationReference
        }
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
            justRun { update(any()) }

        }
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        creditCardInvoiceService.payInvoice("")
    }

    @Test
    fun `Should throw EntityNotFoundException if invoice not found`() {
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } returns null
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceService.payInvoice("")
        }
    }

    @Test
    fun `Should throw BusinessRuleException if invoice is already paid`() {
        val creditCardInvoiceReference = CreditCardInvoice(
            "",
            "",
            0,
            0,
            0.0,
            LocalDateTime.now(),
            LocalDateTime.now(),
        )
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } returns creditCardInvoiceReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<BusinessRuleException> {
            creditCardInvoiceService.payInvoice("")
        }
    }

    @Test
    fun `Should throw EntityNotFoundException if credit card not found`() {
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } returns creditCardInvoiceReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns null
        }
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceService.payInvoice("")
        }
    }

    @Test
    fun `Should throw BusinessRuleException if account does not have enough funds to pay the invoice`() {
        val accountReference = getRandomAccount()
        val creditCardReference = getRandomCreditCard()
        val creditCardInvoiceReference = CreditCardInvoice(
            "",
            "",
            0,
            0,
            10.0,
            LocalDateTime.now(),
            null
        )
        val creditCardInvoiceDAO = mockk<ICreditCardInvoiceDAO> {
            every { getInvoiceById(any()) } returns creditCardInvoiceReference
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>()
        val antiFraudGateway = mockk<IAccountManagementGateway> {
            every { getByCPF(any()) } returns accountReference
        }
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardInvoiceService =
            CreditCardInvoiceService(creditCardDAO, creditCardInvoiceDAO, creditCardOperationDAO, antiFraudGateway)
        assertThrows<BusinessRuleException> {
            creditCardInvoiceService.payInvoice("")
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

    private fun getRandomCreditCardInvoice(): CreditCardInvoice {
        return CreditCardInvoice(
            id = "",
            creditCard = "",
            month = 0,
            year = 0,
            value = 0.0,
            createdAt = LocalDateTime.now(),
            paidAt = null
        )
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

    private fun getRandomAccount(): Account {
        return Account(
            id = "",
            taxId = "",
            balance = 0.0
        )
    }

    private fun getRandomActionResponse(): ActionResponse {
        return ActionResponse(
            message = ""
        )
    }
}