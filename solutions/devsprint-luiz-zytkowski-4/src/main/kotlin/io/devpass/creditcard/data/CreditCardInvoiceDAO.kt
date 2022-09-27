package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardInvoiceEntity
import io.devpass.creditcard.data.extensions.getOrNull
import io.devpass.creditcard.data.repositories.CreditCardInvoiceRepository
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import java.util.UUID

class CreditCardInvoiceDAO(
    private val creditCardInvoiceRepository: CreditCardInvoiceRepository,
) : ICreditCardInvoiceDAO {
    override fun getInvoiceById(id: String): CreditCardInvoice? {
        return creditCardInvoiceRepository.findById(id).getOrNull()?.toCreditCardInvoice()
    }

    override fun getByPeriod(creditCardId: String, month: Int, year: Int): CreditCardInvoice? {
        return creditCardInvoiceRepository.getByPeriod(creditCardId, month, year).let {
            if (it.isEmpty()) null else it.first().toCreditCardInvoice()
        }
    }

    override fun create(creditCardInvoice: CreditCardInvoice): CreditCardInvoice {
        return creditCardInvoiceRepository.save(
            CreditCardInvoiceEntity(
                id = UUID.randomUUID().toString(),
                creditCard = creditCardInvoice.creditCard,
                month = creditCardInvoice.month,
                year = creditCardInvoice.year,
                value = creditCardInvoice.value,
            )
        ).toCreditCardInvoice()
    }

    override fun update(creditCardInvoice: CreditCardInvoice) {
        val creditCardInvoiceEntity = creditCardInvoiceRepository.findById(creditCardInvoice.id).getOrNull()
            ?: throw EntityNotFoundException("Invoice not found with ID ${creditCardInvoice.id}")
        creditCardInvoiceEntity.paidAt = creditCardInvoice.paidAt
        creditCardInvoiceRepository.save(creditCardInvoiceEntity)
    }
}