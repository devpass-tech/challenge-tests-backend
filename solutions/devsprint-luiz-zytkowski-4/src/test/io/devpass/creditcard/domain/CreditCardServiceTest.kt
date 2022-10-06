package io.devpass.creditcard.domain


import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.dataaccess.IAntiFraudGateway
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.InvalidDataException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardCreation
import io.devpass.creditcard.domain.objects.accountmanagement.Account
import io.devpass.creditcard.domain.objects.antifraud.CreditCardEligibility
import io.mockk.every
import io.mockk.mockk
import org.hibernate.TransactionException
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun `Should create a Credit Card sucessfully`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"
        val creditCardReference = getCreditCard(validTaxId, printedName)
        val accountReference = getAccount(validTaxId)

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns null
            every { create(any()) } returns creditCardReference
        }

        val antiFraudGateway = mockk<IAntiFraudGateway> {
            every { creditCardEligibility(validTaxId) } returns CreditCardEligibility(true, 10.0)
        }

        val accountManagementGateway = mockk<IAccountManagementGateway> {
            every { createAccount(any()) } returns accountReference
        }

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertEquals(creditCardReference, creditCardService.requestCreation(creditCardCreation))
    }

    @Test
    fun `Should leak an Exception when repository create function failed`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"
        val accountReference = getAccount(validTaxId)

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns null
            every { create(any()) } throws Exception()
        }

        val antiFraudGateway = mockk<IAntiFraudGateway> {
            every { creditCardEligibility(validTaxId) } returns CreditCardEligibility(true, 10.0)
        }

        val accountManagementGateway = mockk<IAccountManagementGateway> {
            every { createAccount(any()) } returns accountReference
        }

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<Exception> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should leak an Exception when fail to create an Account`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns null
        }

        val antiFraudGateway = mockk<IAntiFraudGateway> {
            every { creditCardEligibility(validTaxId) } returns CreditCardEligibility(true, 10.0)
        }

        val accountManagementGateway = mockk<IAccountManagementGateway> {
            every { createAccount(any()) } throws Exception()
        }

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<Exception> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should throw an InvalidDataException when return null for proposed limit`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns null
        }

        val antiFraudGateway = mockk<IAntiFraudGateway> {
            every { creditCardEligibility(validTaxId) } returns CreditCardEligibility(true, null)
        }

        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<InvalidDataException> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should throw an BusinessRuleException when CreditCardEligibility return false`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns null
        }

        val antiFraudGateway = mockk<IAntiFraudGateway> {
            every { creditCardEligibility(validTaxId) } returns CreditCardEligibility(false, 10.0)
        }

        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<BusinessRuleException> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should leak an Exception when failed to create a CreditCardEligibility`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns null
        }

        val antiFraudGateway = mockk<IAntiFraudGateway> {
            every { creditCardEligibility(validTaxId) } throws Exception()
        }

        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<Exception> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should throw a BusinessRuleException when CPF already owns a credit card`() {
        val validTaxId = "71190024063"
        val printedName = "JOAO A LOPES"
        val creditCardReference = getCreditCard(validTaxId, printedName)

        val creditCardDAO = mockk<ICreditCardDAO> {
            every { getByTaxId(validTaxId) } returns creditCardReference
        }

        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<BusinessRuleException> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should throw a BusinessRuleException when printed name is greater than 100`() {
        val validTaxId = "71190024063"
        val printedName =
            "Praesent in mauris eu tortor porttitor accumsan. Mauris suscipit, ligula sit amet pharetra semper, nibh ante cursus purus"

        val creditCardDAO = mockk<ICreditCardDAO>()
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<BusinessRuleException> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should throw a BusinessRuleException when printed name is blank`() {
        val validTaxId = "71190024063"
        val printedName = ""

        val creditCardDAO = mockk<ICreditCardDAO>()
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<BusinessRuleException> { creditCardService.requestCreation(creditCardCreation) }
    }

    @Test
    fun `Should throw a BusinessRuleException when CPF is invalid`() {
        val validTaxId = "12345678900"
        val printedName = "JOAO A LOPES"

        val creditCardDAO = mockk<ICreditCardDAO>()
        val antiFraudGateway = mockk<IAntiFraudGateway>()
        val accountManagementGateway = mockk<IAccountManagementGateway>()

        val creditCardService = CreditCardService(creditCardDAO, antiFraudGateway, accountManagementGateway)
        val creditCardCreation = CreditCardCreation(validTaxId, printedName)

        assertThrows<BusinessRuleException> { creditCardService.requestCreation(creditCardCreation) }
    }

    private fun getAccount(taxID: String): Account =
        Account(id = "", taxId = taxID, balance = 0.0)

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

    private fun getCreditCard(taxID: String, printedName: String): CreditCard {
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
}


