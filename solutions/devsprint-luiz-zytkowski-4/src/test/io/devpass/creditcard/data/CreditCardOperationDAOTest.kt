package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardOperationEntity
import io.devpass.creditcard.data.repositories.CreditCardOperationRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.Optional

class CreditCardOperationDAOTest {

    @Test
    fun `should call getOperationById method and return a CreditCardOperation`(){
        val creditCardOperationReference = getCreditCardOperationEntity()
        val creditCardOperationRepository = mockk<CreditCardOperationRepository>(){
            every { findById(any()) } returns Optional.of(creditCardOperationReference)
        }

        val creditCardOperation = CreditCardOperationDAO(
            creditCardOperationRepository
        )
        val result = creditCardOperation.getOperationById("")
        Assertions.assertEquals(creditCardOperationReference.toCreditCardOperation(), result)
    }

    fun getCreditCardOperationEntity() : CreditCardOperationEntity{
        return CreditCardOperationEntity(
            id = "",
            creditCard = "",
            type = "",
            value = 10.0,
            month = 2,
            year = 2022,
            description = "",
            createdAt = LocalDateTime.now()
        )
    }
}