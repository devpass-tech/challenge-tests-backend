package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardEntity
import io.devpass.creditcard.data.entities.CreditCardInvoiceEntity
import io.devpass.creditcard.data.repositories.CreditCardInvoiceRepository
import io.devpass.creditcard.data.repositories.CreditCardRepository
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.Optional

class CreditCardInvoiceDAOTest {
    @Test
    fun `Should successfully return a CreditCardInvoice`() {
        val dateTime = LocalDateTime.now()
        val creditCardInvoice = getCreditCardInvoice(dateTime)
        val creditCardInvoiceEntity = getCreditCardInvoiceEntity(dateTime)
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { findById(any()) } returns Optional.of(creditCardInvoiceEntity)
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        val result = creditCardInvoiceDAO.getInvoiceById("")
        assertEquals(creditCardInvoice, result)
    }

    @Test
    fun `Should return null if there is no invoice with the id`() {
        val creditCardInvoiceEntity = null
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { findById(any()) } returns Optional.ofNullable(creditCardInvoiceEntity)
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        val result = creditCardInvoiceDAO.getInvoiceById("")
        assertEquals(null, result)
    }

    @Test
    fun `Should leak an exception when getInvoiceById throws an exception himself`() {
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { findById(any()) } throws Exception("Forced exception for unit testing purposes")
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        assertThrows<Exception> {
            creditCardInvoiceDAO.getInvoiceById("")
        }
    }

    private fun getCreditCardInvoiceEntity(dateTime: LocalDateTime): CreditCardInvoiceEntity {
        return CreditCardInvoiceEntity(
            id = "",
            creditCard = "",
            month = 0,
            year = 0,
            value = 0.0,
            createdAt = dateTime,
        )
    }

    private fun getCreditCardInvoice(dateTime: LocalDateTime): CreditCardInvoice {
        return CreditCardInvoice(
            id = "",
            creditCard = "",
            month = 0,
            year = 0,
            value = 0.0,
            createdAt = dateTime,
            paidAt = null,
        )
    }
}
