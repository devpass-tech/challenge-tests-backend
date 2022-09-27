package io.devpass.creditcard.data

import io.devpass.creditcard.data.entities.CreditCardEntity
import io.devpass.creditcard.data.extensions.getOrNull
import io.devpass.creditcard.data.repositories.CreditCardRepository
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import java.util.UUID

class CreditCardDAO(
    private val creditCardRepository: CreditCardRepository
) : ICreditCardDAO {
    override fun getById(id: String): CreditCard? {
        return creditCardRepository.findById(id).getOrNull()?.toCreditCard()
    }

    override fun getByTaxId(taxId: String): CreditCard? {
        return creditCardRepository.findByTaxId(taxId).firstOrNull()?.toCreditCard()
    }

    override fun create(creditCard: CreditCard): CreditCard {
        return creditCardRepository.save(
            CreditCardEntity(
                id = UUID.randomUUID().toString(),
                owner = creditCard.owner,
                number = creditCard.number,
                securityCode = creditCard.securityCode,
                printedName = creditCard.printedName,
                creditLimit = creditCard.creditLimit,
                availableCreditLimit = creditCard.availableCreditLimit
            )
        ).toCreditCard()
    }

    override fun update(creditCard: CreditCard) {
        val creditCardEntity = creditCardRepository.findById(creditCard.id).getOrNull()
            ?: throw EntityNotFoundException("Credit card not found with ID ${creditCard.id}")
        creditCardEntity.availableCreditLimit = creditCard.availableCreditLimit
        creditCardRepository.save(creditCardEntity)
    }
}