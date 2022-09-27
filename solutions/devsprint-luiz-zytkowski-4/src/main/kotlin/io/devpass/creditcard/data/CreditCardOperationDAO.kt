package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardOperationEntity
import io.devpass.creditcard.data.extensions.getOrNull
import io.devpass.creditcard.data.repositories.CreditCardOperationRepository
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.objects.CreditCardOperation
import java.util.UUID

class CreditCardOperationDAO(
    private val creditCardOperationRepository: CreditCardOperationRepository,
) : ICreditCardOperationDAO {
    override fun getOperationById(id: String): CreditCardOperation? {
        return creditCardOperationRepository.findById(id).getOrNull()?.toCreditCardOperation()
    }

    override fun create(creditCardOperation: CreditCardOperation): CreditCardOperation {
        return creditCardOperationRepository.save(
            CreditCardOperationEntity(
                id = UUID.randomUUID().toString(),
                creditCard = creditCardOperation.creditCard,
                type = creditCardOperation.type,
                value = creditCardOperation.value,
                month = creditCardOperation.month,
                year = creditCardOperation.year,
                description = creditCardOperation.description,
            )
        ).toCreditCardOperation()
    }

    override fun listByPeriod(creditCardId: String, month: Int, year: Int): List<CreditCardOperation> {
        return creditCardOperationRepository.listByPeriod(creditCardId, month, year).map { it.toCreditCardOperation() }
    }
}