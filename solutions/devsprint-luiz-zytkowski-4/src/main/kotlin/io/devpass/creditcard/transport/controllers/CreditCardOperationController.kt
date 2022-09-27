package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domainaccess.ICreditCardOperationServiceAdapter
import io.devpass.creditcard.transport.requests.CreditCardChargeRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("credit-card-operations")
class CreditCardOperationController(
    private val creditCardOperationServiceAdapter: ICreditCardOperationServiceAdapter,
) {

    @GetMapping
    fun listByPeriod(
        @RequestParam creditCardId: String,
        @RequestParam month: Int,
        @RequestParam year: Int,
    ): List<CreditCardOperation> {
        return creditCardOperationServiceAdapter.listByPeriod(creditCardId, month, year)
    }

    @GetMapping("/{creditCardOperationId}")
    fun getById(@PathVariable creditCardOperationId: String): CreditCardOperation {
        return creditCardOperationServiceAdapter.getById(creditCardOperationId)
            ?: throw EntityNotFoundException("Credit Card Operation Not found with ID: $creditCardOperationId")
    }

    @PostMapping("charge")
    fun charge(@RequestBody creditCardChargeRequest: CreditCardChargeRequest): List<CreditCardOperation> {
        return creditCardOperationServiceAdapter.charge(creditCardChargeRequest.toCreditCardCharge())
    }

    @PutMapping("rollback/{creditCardOperationId}")
    fun charge(@PathVariable creditCardOperationId: String): String {
        creditCardOperationServiceAdapter.rollback(creditCardOperationId)
        return "Operation $creditCardOperationId was rolled back successfully"
    }

}