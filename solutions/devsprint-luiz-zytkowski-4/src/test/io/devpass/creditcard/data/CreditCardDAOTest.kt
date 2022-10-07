package io.devpass.creditcard.data

import io.devpass.creditcard.data.repositories.CreditCardRepository
import io.devpass.creditcard.data.entities.CreditCardEntity
import io.devpass.creditcard.domain.objects.CreditCard
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class CreditCardDAOTest {

    @Test
    fun `Should successfully return a CreditCard`() {
        val creditCardReference = getCreditCard()
        val creditCardEntity = getListOfCreditCardEntity()
        val creditCardRepository = mockk<CreditCardRepository> {
            every { findByTaxId("") } returns creditCardEntity
        }
        val creditCardDAO = CreditCardDAO(creditCardRepository)
        val result = creditCardDAO.getByTaxId("")
        assertEquals(creditCardReference, result)
    }

    @Test
    fun `Should return null if the list is empty`() {
        val creditCardEntity = listOf<CreditCardEntity>()
        val creditCardRepository = mockk<CreditCardRepository> {
            every { findByTaxId("") } returns creditCardEntity
        }
        val creditCardDAO = CreditCardDAO(creditCardRepository)
        val result = creditCardDAO.getByTaxId("")
        assertEquals(null, result)
    }

    @Test
    fun `Should leak an exception when getByTaxId throws an exception himself`() {
        val creditCardRepository = mockk<CreditCardRepository> {
            every { findByTaxId(any()) } throws Exception("Forced exception for unit testing purposes")
        }
        val creditCardDAO = CreditCardDAO(creditCardRepository)
        assertThrows<Exception> {
            creditCardDAO.getByTaxId("")
        }
    }

    @Test
    fun `Should successfully find a CreditCard by ID`() {
        val creditCardReference = getCreditCard()
        val creditCardRepository = mockk<CreditCardRepository> {
            every { findById(any()) } returns creditCardReference
        }
        val creditCardDAO = CreditCardDAO(creditCardRepository)
        val result = creditCardDAO.getById("")
        assertEquals(creditCardReference, result)
    }

    private fun getListOfCreditCardEntity(): List<CreditCardEntity> {
        return listOf(
            CreditCardEntity(
                id = "",
                owner = "",
                number = "",
                securityCode = "",
                printedName = "",
                creditLimit = 0.0,
                availableCreditLimit = 0.0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )
        )
    }

    private fun getCreditCard(): CreditCard {
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