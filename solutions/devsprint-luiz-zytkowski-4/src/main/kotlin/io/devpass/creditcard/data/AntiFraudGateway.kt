package io.devpass.creditcard.data

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf
import io.devpass.creditcard.data.antifraud.response.CreditCardEligibilityResponse
import io.devpass.creditcard.dataaccess.IAntiFraudGateway
import io.devpass.creditcard.domain.exceptions.OwnedException
import io.devpass.creditcard.domain.objects.antifraud.CreditCardEligibility


class AntiFraudGateway(
    private val baseUrl: String,
) : IAntiFraudGateway {
    override fun creditCardEligibility(CPF: String): CreditCardEligibility {
        val (_, result, response) = Fuel.get("$baseUrl/anti-fraud/credit-card-eligibility/$CPF")
            .responseObject<CreditCardEligibilityResponse>(jacksonDeserializerOf())
        return if (result.isSuccessful) {
            response.get().toCreditCardEligibility()
        } else throw OwnedException("Erro ao obter eligibilidade do CPF: $CPF")
    }
}