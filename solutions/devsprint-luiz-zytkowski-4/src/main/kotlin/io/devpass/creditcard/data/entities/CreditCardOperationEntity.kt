package io.devpass.creditcard.data.entities

import io.devpass.creditcard.domain.objects.CreditCardOperation
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "credit_card_operation")
data class CreditCardOperationEntity(
    @Id
    var id: String,
    var creditCard: String,
    var type: String,
    var value: Double,
    var description: String,
    var month: Int,
    var year: Int,
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toCreditCardOperation(): CreditCardOperation {
        return CreditCardOperation(
            id = id,
            creditCard = creditCard,
            type = type,
            value = value,
            month = month,
            year = year,
            description = description,
            createdAt = createdAt,
        )
    }
}