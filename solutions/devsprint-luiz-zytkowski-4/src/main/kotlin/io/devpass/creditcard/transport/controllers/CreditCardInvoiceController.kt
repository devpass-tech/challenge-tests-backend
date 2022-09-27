package io.devpass.creditcard.transport.controllers

import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import io.devpass.creditcard.transport.requests.InvoiceCreationRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("credit-card-invoices")
class CreditCardInvoiceController(
    private val creditCardInvoiceServiceAdapter: ICreditCardInvoiceServiceAdapter
) {
    @GetMapping("/{creditCardInvoiceId}")
    fun getById(@PathVariable creditCardInvoiceId: String): CreditCardInvoice {
        return creditCardInvoiceServiceAdapter.getById(creditCardInvoiceId)
            ?: throw EntityNotFoundException("Credit Card Invoice Not found with ID: $creditCardInvoiceId")
    }

    @PostMapping
    fun generateInvoice(@RequestBody invoiceCreationRequest: InvoiceCreationRequest): CreditCardInvoice {
        return creditCardInvoiceServiceAdapter.generateInvoice(invoiceCreationRequest.creditCardId)
    }

    @PutMapping("/pay/{creditCardInvoiceId}")
    fun payInvoice(@PathVariable creditCardInvoiceId: String): String {
        creditCardInvoiceServiceAdapter.payInvoice(creditCardInvoiceId)
        return "Invoice paid successfully"
    }

    @GetMapping
    fun getByPeriod(
        @RequestParam creditCardId: String,
        @RequestParam month: Int,
        @RequestParam year: Int,
    ): CreditCardInvoice {
        return creditCardInvoiceServiceAdapter.getByPeriod(creditCardId, month, year)
            ?: throw EntityNotFoundException("Invoice not found with given parameters")
    }
}
