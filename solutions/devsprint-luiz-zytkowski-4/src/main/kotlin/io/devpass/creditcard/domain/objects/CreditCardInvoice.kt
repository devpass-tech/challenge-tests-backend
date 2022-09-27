package io.devpass.creditcard.domain.objects

import java.time.LocalDateTime

data class CreditCardInvoice(
    var id: String,
    var creditCard: String,
    var month: Int,
    var year: Int,
    var value: Double,
    var createdAt: LocalDateTime,
    var paidAt: LocalDateTime?
) 