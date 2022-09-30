package io.devpass.creditcard.domain


import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.dataaccess.IAntiFraudGateway
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domainaccess.ICreditCardServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardController
import io.mockk.every
import io.mockk.mockk
import org.hibernate.TransactionException
import org.junit.jupiter.api.Assertions
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
        Assertions.assertEquals(creditCardReference, result)
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


