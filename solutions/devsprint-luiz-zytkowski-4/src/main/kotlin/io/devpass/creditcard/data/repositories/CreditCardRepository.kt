package io.devpass.creditcard.data.repositories

import io.devpass.creditcard.data.entities.CreditCardEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditCardRepository : CrudRepository<CreditCardEntity, String> {

    @Query("SELECT cce FROM CreditCardEntity cce WHERE cce.owner = ?1")
    fun findByTaxId(taxId : String) : List<CreditCardEntity>

}
