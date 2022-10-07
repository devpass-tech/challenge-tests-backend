package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
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
import java.time.LocalDateTime

class CreditCardOperationServiceTest {

    // CENARIO 1 - EXECUTAR O MÉTODO CHARGE E RETORNAR UMA LISTA DE CREDIT CARD OP
    // CENARIO 2 - CREDITCARDCHARGE VALUE < 0.0
    // CENARIO 3 - CREDIT CARD CHARGE INSTALLMENTS < 1 || > 12
    // CERARIO 4 - CREDIT CARD CHARGE INSTALLMENTS > 1 && VALUE <6.0
    //  CENARIO 5 -  available credit limit < charge value

    @Test
    fun `should execute charge method and return a list of credit card operations`(){

        val creditCardChargeReference = getRandomCreditCardCharge()
        val creditCardOperation = getRandomCreditCardOperation()

        val creditCardDAO = mockk<ICreditCardDAO>{
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
                year =  2022,
                value = 100.0,
                createdAt = LocalDateTime.now(),
                paidAt = LocalDateTime.now(),
            ) andThen (null)
        }
        val creditCardOperationDAO = mockk<ICreditCardOperationDAO>{
            every {create(any())} returns creditCardOperation
        }

        val creditCardOperationService = CreditCardOperationService(
            creditCardDAO,
            creditCardInvoiceDAO,
            creditCardOperationDAO,
        )

        val result = creditCardOperationService.charge(creditCardChargeReference)

        Assertions.assertEquals(listOf(creditCardOperation), result)

    }

    private fun getRandomCreditCard() : CreditCard {
        return CreditCard(
            id = "",
            owner = "",
            number = "",
            securityCode =  "",
            printedName = "",
            creditLimit = 1000.0,
            availableCreditLimit = 1000.0,
        )
    }

    private fun getRandomCreditCardCharge() : CreditCardCharge {
        return CreditCardCharge(
            creditCardId = "1",
            value = 5.0,
            installments = 1,
            description = " "
        )
    }

    private fun getRandomCreditCardInvoice() : CreditCardInvoice {
        return CreditCardInvoice(
            id = "",
            creditCard = "",
            month = 1,
            year =  2022,
            value = 100.0,
            createdAt = LocalDateTime.now(),
            paidAt = LocalDateTime.now(),
        )
    }

    private fun getRandomCreditCardOperation() : CreditCardOperation{
        return CreditCardOperation(
            id = "",
            creditCard = "",
            type = CreditCardOperationTypes.CHARGE,
            value = 10.0,
            month = 1,
            year = 2,
            description = "",
            createdAt =  LocalDateTime.now(),
        )
    }
}