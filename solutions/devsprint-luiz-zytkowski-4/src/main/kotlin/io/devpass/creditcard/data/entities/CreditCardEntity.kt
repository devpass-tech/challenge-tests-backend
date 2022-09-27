package io.devpass.creditcard.data.entities

import io.devpass.creditcard.domain.objects.CreditCard
import java.time.LocalDateTime
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "credit_card")
data class CreditCardEntity(
    @Id
    var id: String,
    var owner: String,
    var number: String,
    var securityCode: String,
    var printedName: String,
    var creditLimit: Double,
    var availableCreditLimit: Double,
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toCreditCard(): CreditCard {
        return CreditCard(
            id = id,
            owner = owner,
            number = number,
            securityCode = securityCode,
            printedName = printedName,
            creditLimit = creditLimit,
            availableCreditLimit = availableCreditLimit,
        )
    }
}

