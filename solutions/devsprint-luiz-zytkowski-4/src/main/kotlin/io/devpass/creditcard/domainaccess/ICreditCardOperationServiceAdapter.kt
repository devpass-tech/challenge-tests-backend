package io.devpass.creditcard.domainaccess

import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.operation.CreditCardCharge

interface ICreditCardOperationServiceAdapter {
    fun getById(creditCardOperationId: String): CreditCardOperation?
    fun charge(creditCardCharge: CreditCardCharge): List<CreditCardOperation>
    fun rollback(creditCardOperationId: String)
    fun listByPeriod(creditCardId: String, month: Int, year: Int): List<CreditCardOperation>
}
