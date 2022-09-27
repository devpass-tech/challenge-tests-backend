package io.devpass.creditcard.dataaccess

import io.devpass.creditcard.domain.objects.CreditCardOperation

interface ICreditCardOperationDAO {
    fun getOperationById(id: String): CreditCardOperation?
    fun create(creditCardOperation: CreditCardOperation) : CreditCardOperation
    fun listByPeriod(creditCardId : String, month : Int, year : Int) : List<CreditCardOperation>
}