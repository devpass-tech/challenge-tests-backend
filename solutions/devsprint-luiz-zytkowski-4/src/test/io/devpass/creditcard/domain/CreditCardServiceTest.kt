package io.devpass.creditcard.domain


import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.dataaccess.IAntiFraudGateway
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardCreation
import io.mockk.every
import io.mockk.mockk
import org.hibernate.TransactionException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class CreditCardServiceTest {

    @Test
    fun `Should successfully return a CreditCardId`() {
        val creditCardReference = getRandomCreditCard()
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } returns creditCardReference
        }
        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val result = creditCardService.findCreditCardById("")
        assertEquals(creditCardReference, result)
    }


    @Test
    fun `Should leak and exception when findCreditCardById throws and exception himself`() {
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getById(any()) } throws TransactionException("Forced exception for unit testing purposes")
        }
        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        assertThrows<TransactionException> {
            creditCardService.findCreditCardById("")
        }
    }

    @Test
    fun `Should validate CPF sucessfully`() {
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)

        val validTaxId = "00761779477"
        val printedName = "Livio"
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        val exception = assertThrows<Exception> {
            creditCardService.requestCreation(creditCardCreation)
        }
        assertNotEquals(BusinessRuleException("Provided CPF is invalid.").message, exception.message)

    }

    @Test
    fun `Should throw an Exception when validate CPF`() {
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()
        val creditCardDAO = mockk<ICreditCardDAO>()
        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)

        val taxId = "12345678900"
        val printedName = "Livio"
        val creditCardCreation = CreditCardCreation(taxId, printedName)

        val exception = assertThrows<BusinessRuleException> {
            creditCardService.requestCreation(creditCardCreation)
        }
        assertEquals(BusinessRuleException("Provided CPF is invalid.").message, exception.message)

    }

    private fun getCreditCardWithOwnerAndPrintedName(taxID: String, printedName: String): CreditCard {
        return CreditCard(
            id = "",
            owner = taxID,
            number = "",
            securityCode = "",
            printedName = printedName,
            creditLimit = 0.0,
            availableCreditLimit = 0.0,
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


