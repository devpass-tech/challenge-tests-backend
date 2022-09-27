package io.devpass.creditcard.data.entities

import io.devpass.creditcard.domain.objects.CreditCardInvoice
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "credit_card_invoice")
data class CreditCardInvoiceEntity(
    @Id
    var id: String,
    var creditCard: String,
    var month: Int,
    var year: Int,
    var value: Double,
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var paidAt: LocalDateTime? = null,
) {
    fun toCreditCardInvoice(): CreditCardInvoice {
        return CreditCardInvoice(
            id = id,
            creditCard = creditCard,
            month = month,
            year = year,
            value = value,
            createdAt = createdAt,
            paidAt = paidAt
        )
    }
}