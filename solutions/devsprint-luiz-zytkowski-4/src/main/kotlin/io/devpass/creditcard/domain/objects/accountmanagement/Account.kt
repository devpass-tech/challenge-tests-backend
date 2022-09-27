package io.devpass.creditcard.domain.objects.accountmanagement

data class Account(
    val id: String,
    val taxId: String,
    val balance: Double,
)
