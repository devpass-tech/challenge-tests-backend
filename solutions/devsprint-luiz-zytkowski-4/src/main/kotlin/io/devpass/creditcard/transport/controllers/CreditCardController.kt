package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domainaccess.ICreditCardServiceAdapter
import io.devpass.creditcard.transport.requests.CreditCardCreationRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("credit-cards")
class CreditCardController(
    private val creditCardServiceAdapter: ICreditCardServiceAdapter,
) {

    @GetMapping("/{creditCardId}")
    fun findCreditCard(@PathVariable creditCardId: String): CreditCard {
        return creditCardServiceAdapter.findCreditCardById(creditCardId)
            ?: throw EntityNotFoundException("CreditCard Not found with number: $creditCardId")
    }

    @PostMapping
    fun requestCreditCard(@RequestBody creditCardCreationRequest: CreditCardCreationRequest): CreditCard {
        return creditCardServiceAdapter.requestCreation(creditCardCreationRequest.toCreditCardCreation())
    }
}
