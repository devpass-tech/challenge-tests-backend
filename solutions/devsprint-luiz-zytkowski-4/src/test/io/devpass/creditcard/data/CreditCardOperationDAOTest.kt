package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardOperationEntity
import io.devpass.creditcard.data.repositories.CreditCardOperationRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreditCardOperationDAOTest {
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
}