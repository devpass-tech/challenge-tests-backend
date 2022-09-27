package io.devpass.creditcard.data.repositories

import io.devpass.creditcard.data.entities.CreditCardInvoiceEntity
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditCardInvoiceRepository : CrudRepository<CreditCardInvoiceEntity, String> {

    @Query("SELECT ccie FROM CreditCardInvoiceEntity ccie WHERE ccie.creditCard = ?1 AND ccie.month = ?2 AND ccie.year = ?3")
    fun getByPeriod(creditCardId: String, month: Int, year: Int): List<CreditCardInvoiceEntity>

}