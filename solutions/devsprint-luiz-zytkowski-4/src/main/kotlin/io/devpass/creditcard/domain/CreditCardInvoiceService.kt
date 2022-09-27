package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.CreditCardInvoice
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.CreditCardOperationTypes
import io.devpass.creditcard.domain.objects.accountmanagement.Transaction
import io.devpass.creditcard.domainaccess.ICreditCardInvoiceServiceAdapter
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime

class CreditCardInvoiceService(
    private val creditCardDAO: ICreditCardDAO,
    private val creditCardInvoiceDAO: ICreditCardInvoiceDAO,
    private val creditCardOperationDAO: ICreditCardOperationDAO,
    private val accountManagementGateway: IAccountManagementGateway,
) : ICreditCardInvoiceServiceAdapter {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getById(creditCardInvoiceId: String): CreditCardInvoice? {
        return creditCardInvoiceDAO.getInvoiceById(creditCardInvoiceId)
    }

    override fun getByPeriod(creditCardId: String, month: Int, year: Int): CreditCardInvoice? {
        if (year < 0)
            throw BusinessRuleException("You must provide a valid year to list operations")
        if (month < 1 || month > 12)
            throw BusinessRuleException("You must provide a valid month to list operations")
        if (creditCardDAO.getById(creditCardId) == null)
            throw EntityNotFoundException("Credit card not found with ID $creditCardId")
        return creditCardInvoiceDAO.getByPeriod(creditCardId, month, year)
    }

    override fun payInvoice(invoiceId: String) {
        val creditCardInvoice = getById(invoiceId)
            ?: throw EntityNotFoundException("Invoice not found with ID $invoiceId.")

        if (creditCardInvoice.paidAt != null)
            throw BusinessRuleException("Invoice is already paid.")

        val creditCard = creditCardDAO.getById(creditCardInvoice.creditCard)
            ?: throw EntityNotFoundException("Credit card not found with ID ${creditCardInvoice.creditCard}")

        val account = accountManagementGateway.getByCPF(creditCard.owner)

        if (account.balance < creditCardInvoice.value)
            throw BusinessRuleException("Account does not have enough funds to pay the invoice.")

        val actionResponse = accountManagementGateway.withdraw(Transaction(account.id, creditCardInvoice.value))
        logger.info(buildString {
            append("Withdrew ${creditCardInvoice.value} from account ${account.id} regarding payment of invoice ${creditCardInvoice.id}. ")
            append("Response: $actionResponse")
        })

        creditCard.availableCreditLimit += creditCardInvoice.value
        creditCardDAO.update(creditCard)

        creditCardOperationDAO.create(
            CreditCardOperation(
                id = "", // auto generated later
                creditCard = creditCard.id,
                type = CreditCardOperationTypes.INVOICE_PAYMENT,
                value = creditCardInvoice.value * -1,
                month = LocalDate.now().monthValue,
                year = LocalDate.now().year,
                description = "Payment of invoice #${creditCardInvoice.id}",
            )
        )

        creditCardInvoice.paidAt = LocalDateTime.now()
        creditCardInvoiceDAO.update(creditCardInvoice)
    }

    override fun generateInvoice(creditCardId: String): CreditCardInvoice {
        val creditCard = creditCardDAO.getById(creditCardId)
            ?: throw EntityNotFoundException("Credit card not found with ID $creditCardId")

        val currentMonth = LocalDate.now().monthValue
        val currentYear = LocalDate.now().year

        creditCardInvoiceDAO.getByPeriod(creditCardId, currentMonth, currentYear)?.also {
            throw BusinessRuleException("Invoice already generated for credit card ${creditCard.id} on period ${it.month}/${it.year}")
        }

        val processableOperations = creditCardOperationDAO.listByPeriod(creditCardId, currentMonth, currentYear).filter {
            it.type in listOf(CreditCardOperationTypes.CHARGE, CreditCardOperationTypes.ROLLBACK)
        }

        val invoiceValue = processableOperations.sumOf { it.value }

        val creditCardInvoice = if (invoiceValue > 0.0) {
            CreditCardInvoice(
                id = "",// will be auto-generated
                creditCard = creditCardId,
                month = currentMonth,
                year = currentYear,
                value = invoiceValue,
                createdAt = LocalDateTime.now(),
                paidAt = null,
            )
        } else { // Generate a paid invoice
            CreditCardInvoice(
                id = "",// will be auto-generated
                creditCard = creditCardId,
                month = currentMonth,
                year = currentYear,
                value = invoiceValue,
                createdAt = LocalDateTime.now(),
                paidAt = LocalDateTime.now(),
            ).also {
                logger.info("Invoice value was equal or lower than zero. Will generate a paid invoice...")
            }
        }

        return creditCardInvoiceDAO.create(creditCardInvoice)
    }
}