package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardInvoiceEntity
import io.devpass.creditcard.data.repositories.CreditCardInvoiceRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CreditCardInvoiceDAOTest {

    @Test
    fun `should call create method and return a CreditCardInvoice`(){
        val creditCardInvoiceEntity = getRandomCreditCardInvoiceEntity()

        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository>(){
            every { save(any()) } returns creditCardInvoiceEntity
        }

        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)

        val result = creditCardInvoiceDAO.create(creditCardInvoice = creditCardInvoiceEntity.toCreditCardInvoice())

        Assertions.assertEquals(creditCardInvoiceEntity.toCreditCardInvoice(), result)
    }

    fun getRandomCreditCardInvoiceEntity() : CreditCardInvoiceEntity{
        return CreditCardInvoiceEntity(
            id = "",
            creditCard = "",
            month = 1,
            year = 2022,
            value = 10.0,
            createdAt = LocalDateTime.now(),
            paidAt = null,
        )
    }
}