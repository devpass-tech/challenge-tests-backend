package io.devpass.creditcard.dataaccess

import io.devpass.creditcard.domain.accountmanagement.AccountCreation
import io.devpass.creditcard.domain.objects.ActionResponse
import io.devpass.creditcard.domain.objects.accountmanagement.Account
import io.devpass.creditcard.domain.objects.accountmanagement.Transaction

interface IAccountManagementGateway {
    fun getByCPF(CPF: String): Account
    fun getAccountById(accountId : String) : Account
    fun createAccount(accountCreation: AccountCreation) : Account
    fun withdraw(transaction: Transaction) : ActionResponse
}
