package io.devpass.creditcard.domain
import io.devpass.creditcard.dataaccess.*
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
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
}