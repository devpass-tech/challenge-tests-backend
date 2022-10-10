package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardInvoiceEntity
import io.devpass.creditcard.data.repositories.CreditCardInvoiceRepository
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


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

    @Test
    fun `Should return CreditCardInvoice`() {
        val creditCardInvoiceEntityList = getListOfCreditCardInvoiceEntity()
        val creditCardInvoiceReference = creditCardInvoiceEntityList.first().toCreditCardInvoice()

        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { getByPeriod(any(), any(), any()) } returns creditCardInvoiceEntityList
        }

        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        val result = creditCardInvoiceDAO.getByPeriod("", 0, 0)

        assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `Should return null if return an empty list fro Repository`() {
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { getByPeriod(any(), any(), any()) } returns emptyList()
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        val result = creditCardInvoiceDAO.getByPeriod("", 0, 0)

        assertNull(result)
    }

    @Test
    fun `Should leak an exception when getByPeriod throws an exception himself`() {
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { getByPeriod(any(), any(), any()) } throws Exception()
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        assertThrows<Exception> {
            creditCardInvoiceDAO.getByPeriod("", 0, 0)
        }
    }

    @Test
    fun `should call create method and return a CreditCardInvoice`() {
        val creditCardInvoiceEntity = getRandomCreditCardInvoiceEntity()

        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository>() {
            every { save(any()) } returns creditCardInvoiceEntity
        }

        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)

        val result = creditCardInvoiceDAO.create(creditCardInvoice = creditCardInvoiceEntity.toCreditCardInvoice())

        assertEquals(creditCardInvoiceEntity.toCreditCardInvoice(), result)
    }

    @Test
    fun `Should sucessfully update a CreditCardInvoice`() {
        val creditCardInvoiceEntityReference = getCreditCardInvoiceEntity(LocalDateTime.now())
        val creditCardInvoiceReference = getCreditCardInvoice(LocalDateTime.now())
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository>() {
            every { findById(any()) } returns Optional.of(creditCardInvoiceEntityReference)
            every { (save(creditCardInvoiceEntityReference)) } returns creditCardInvoiceEntityReference
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        creditCardInvoiceDAO.update(creditCardInvoiceReference)
        verify { creditCardInvoiceRepository.save(any()) }
    }

    @Test
    fun `Should throw an exception when the invoice ID isn't found`() {
        val creditCardInvoiceEntity = null
        val creditCardInvoiceReference = getCreditCardInvoice(LocalDateTime.now())
        val creditCardInvoiceRepository = mockk<CreditCardInvoiceRepository> {
            every { findById(any()) } returns Optional.ofNullable(creditCardInvoiceEntity)
        }
        val creditCardInvoiceDAO = CreditCardInvoiceDAO(creditCardInvoiceRepository)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceDAO.update(creditCardInvoiceReference)
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

    private fun getListOfCreditCardInvoiceEntity(): List<CreditCardInvoiceEntity> {
        return listOf(
            CreditCardInvoiceEntity(
                id = "",
                creditCard = "",
                month = 0,
                year = 0,
                value = 0.0
            )
        )
    }

    private fun getRandomCreditCardInvoiceEntity(): CreditCardInvoiceEntity {
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
