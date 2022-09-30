package transport

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import io.devpass.creditcard.transport.controllers.CreditCardInvoiceController
import io.devpass.creditcard.transport.requests.InvoiceCreationRequest
import io.mockk.every
import io.mockk.mockk
import org.hibernate.TransactionException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

internal class CreditCardInvoiceControllerTest {

    @Test
    fun `Should successfully return a CreditCardInvoiceId`(){
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getById(any()) } returns getRandomCreditCardInvoice()
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.getById("")
        Assertions.assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `Should raise an EntityNotFoundException when CreditCardInvoice not found with ID`(){
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getById(any()) } returns null
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceController.getById("")
        }
    }

    @Test
    fun `Should successfully generateInvoice`(){
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { generateInvoice(any()) } returns getRandomCreditCardInvoice()
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.generateInvoice(InvoiceCreationRequest(creditCardId = ""))
        Assertions.assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `Should successfully when an invoice is paid`(){
        val payInvoiceStringReference = "Invoice paid successfully"
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { payInvoice(any()) } returns Unit
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.payInvoice(String())
        Assertions.assertEquals(payInvoiceStringReference, result)
    }

    @Test
    fun `Should successfully return a period`(){
        val creditCardInvoiceReference = getRandomCreditCardInvoice()
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getByPeriod(any(), any(), any()) } returns getRandomCreditCardInvoice()
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        val result = creditCardInvoiceController.getByPeriod("", month = 1, year = 1)
        Assertions.assertEquals(creditCardInvoiceReference, result)
    }

    @Test
    fun `Should raise an EntityNotFoundException when getByPeriod returns null`(){
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getByPeriod(any(), any(), any()) } returns null
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<EntityNotFoundException> {
            creditCardInvoiceController.getByPeriod("", 1, 0)
        }
    }

    @Test
    fun `Should raise an exception when getByPeriod throws an exception`(){
        val creditCardInvoiceServiceAdapter = mockk<ICreditCardInvoiceServiceAdapter> {
            every { getByPeriod(any(), any(), any()) } throws TransactionException("Error")
        }
        val creditCardInvoiceController = CreditCardInvoiceController(creditCardInvoiceServiceAdapter)
        assertThrows<TransactionException> {
            creditCardInvoiceController.getByPeriod("", 1, 0)
        }
    }

    private fun getRandomCreditCardInvoice(): CreditCardInvoice {
        return CreditCardInvoice(
                id = "",
                creditCard = "",
                month = 1,
                year = 1,
                value = 0.0,
                paidAt = LocalDateTime.MAX,
                createdAt = LocalDateTime.MAX
        )
    }
}