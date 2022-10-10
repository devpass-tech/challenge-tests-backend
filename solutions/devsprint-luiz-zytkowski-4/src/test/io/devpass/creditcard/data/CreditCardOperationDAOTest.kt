package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardOperationEntity
import io.devpass.creditcard.data.repositories.CreditCardOperationRepository
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CreditCardOperationDAOTest {

    @Test
    fun `should call getOperationById method and return a CreditCardOperation`() {
        val creditCardOperationReference = getCreditCardOperationEntity()
        val creditCardOperationRepository = mockk<CreditCardOperationRepository>() {
            every { findById(any()) } returns Optional.of(creditCardOperationReference)
        }

        val creditCardOperation = CreditCardOperationDAO(
            creditCardOperationRepository
        )
        val result = creditCardOperation.getOperationById("")
        assertEquals(creditCardOperationReference.toCreditCardOperation(), result)
    }

    @Test
    fun `Should return null when repository findById returns an empty optional`() {
        val creditCardOperationRepository = mockk<CreditCardOperationRepository> {
            every { findById(any()) } returns Optional.ofNullable(null)
        }

        val creditCardOperation = CreditCardOperationDAO(
            creditCardOperationRepository
        )
        val result = creditCardOperation.getOperationById("")
        assertEquals(null, result)
    }

    @Test
    fun `Should successfully return a CreditCardOperation`() {
        val getListCreditCardOperationReference = listOf<CreditCardOperationEntity>()
        val creditCardOperationRepository = mockk<CreditCardOperationRepository> {
            every { listByPeriod(any(), any(), any()) } returns getListCreditCardOperationReference
        }
        val creditCardOperationDAO = CreditCardOperationDAO(creditCardOperationRepository)
        val result = creditCardOperationDAO.listByPeriod("", month = 0, 0)
        assertEquals(getListCreditCardOperationReference, result)
    }

    @Test
    fun `Should leak an exception when listByPeriod throws an exception himself`() {
        val creditCardOperationRepository = mockk<CreditCardOperationRepository> {
            every { listByPeriod(any(), any(), any()) } throws Exception("Periods not found")
        }
        val creditCardOperationDAO = CreditCardOperationDAO(creditCardOperationRepository)
        assertThrows<Exception> {
            creditCardOperationDAO.listByPeriod("", 14, 0)
        }
    }

    @Test
    fun `Should Successfully create a CreditCardOperation`() {
        val creditCardOperationEntityReference = getCreditCardOperationEntity()
        val creditCardOperationReference = creditCardOperationEntityReference.toCreditCardOperation()

        val creditCardOperationRepository = mockk<CreditCardOperationRepository>() {
            every { save(any()) } returns creditCardOperationEntityReference
        }

        val creditCardOperationDAO = CreditCardOperationDAO(creditCardOperationRepository)
        val result = creditCardOperationDAO.create(creditCardOperationReference)

        assertEquals(creditCardOperationReference, result)
    }

    @Test
    fun `Should leak an exception when create throws an exception himself`() {
        val creditCardOperationReference = getCreditCardOperation()

        val creditCardOperationRepository = mockk<CreditCardOperationRepository>() {
            every { save(any()) } throws Exception()
        }

        val creditCardOperationDAO = CreditCardOperationDAO(creditCardOperationRepository)

        assertThrows<Exception> { creditCardOperationDAO.create(creditCardOperationReference) }
    }

    private fun getCreditCardOperationEntity(): CreditCardOperationEntity {
        return CreditCardOperationEntity(
            id = "",
            creditCard = "",
            type = "",
            value = 10.0,
            month = 2,
            year = 2022,
            description = "",
            createdAt = LocalDateTime.now(),
        )
    }

    private fun getCreditCardOperation(): CreditCardOperation {
        return CreditCardOperation(
            id = "",
            creditCard = "",
            type = "",
            value = 0.0,
            month = 0,
            year = 0,
            description = "",
        )
    }
}