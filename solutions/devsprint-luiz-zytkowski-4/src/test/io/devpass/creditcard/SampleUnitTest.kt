package io.devpass.creditcard

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domainaccess.ICreditCardServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardController
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SampleUnitTest {

    @Test
    fun `I Should run nicely`() {
        val creditCardServiceAdapter = mockk<ICreditCardServiceAdapter> {
            every { findCreditCardById(any()) } returns null
        }
        val creditCardController = CreditCardController(creditCardServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardController.findCreditCard("testId")
        }
    }

}