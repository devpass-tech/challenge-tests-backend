package io.devpass.creditcard.data

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import com.github.kittinunf.fuel.jackson.objectBody
import io.devpass.creditcard.data.accountmanagement.response.AccountResponse
import io.devpass.creditcard.data.http.response.DefaultHttpResponse
import io.devpass.creditcard.data.accountmanagement.request.AccountCreationRequest
import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.domain.accountmanagement.AccountCreation
import io.devpass.creditcard.domain.exceptions.GatewayException
import io.devpass.creditcard.domain.objects.accountmanagement.Account
import io.devpass.creditcard.domain.objects.accountmanagement.Transaction
import io.devpass.creditcard.domain.objects.ActionResponse


class AccountManagementGateway(
    private val baseUrl: String,
) : IAccountManagementGateway {

    override fun getByCPF(CPF: String): Account {
        val (_, result, response) = Fuel
            .get("$baseUrl/account-management/balance-by-tax-id/$CPF")
            .responseObject<AccountResponse>(jacksonDeserializerOf())
        return if (result.isSuccessful) {
            response.get().toAccount()
        } else {
            val errorMessage = DefaultHttpResponse.fromFuelError(response.component2()).message
            throw GatewayException("Error finding account by Tax Id. Message: $errorMessage")
        }
    }

    override fun withdraw(transaction: Transaction): ActionResponse {
        val (_, result, response) = Fuel.put("$baseUrl/account-management/withdraw").objectBody(transaction)
            .responseObject<DefaultHttpResponse>(jacksonDeserializerOf())
        return if (result.isSuccessful) {
            response.get().toActionResponse()
        } else {
            val errorMessage = DefaultHttpResponse.fromFuelError(response.component2()).message
            throw GatewayException("Error removing account balance. Message: $errorMessage")
        }
    }

    override fun createAccount(accountCreation: AccountCreation): Account {
        val (_, result, response) = Fuel
            .post("$baseUrl/account-management/create")
            .objectBody(AccountCreationRequest(taxId = accountCreation.taxId))
            .responseObject<AccountResponse>(jacksonDeserializerOf())
        return if (result.isSuccessful) {
            response.get().toAccount()
        } else {
            val errorMessage = DefaultHttpResponse.fromFuelError(response.component2()).message
            throw GatewayException("Error creating account. Message: $errorMessage")
        }
    }

    override fun getAccountById(accountId: String): Account {
        val (_, result, response) = Fuel
            .get("$baseUrl/account-management/balance/$accountId")
            .responseObject<AccountResponse>(jacksonDeserializerOf())
        return if (result.isSuccessful) {
            response.get().toAccount()
        } else {
            val errorMessage = DefaultHttpResponse.fromFuelError(response.component2()).message
            throw GatewayException("Error finding account by ID. Message: $errorMessage")
        }
    }
}