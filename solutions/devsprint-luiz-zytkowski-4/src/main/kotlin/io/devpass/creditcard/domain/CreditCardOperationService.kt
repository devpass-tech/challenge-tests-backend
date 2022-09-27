package io.devpass.creditcard.domain

import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.dataaccess.ICreditCardInvoiceDAO
import io.devpass.creditcard.dataaccess.ICreditCardOperationDAO
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.EntityNotFoundException
import io.devpass.creditcard.domain.objects.ChargeablePeriod
import io.devpass.creditcard.domain.objects.CreditCardOperation
import io.devpass.creditcard.domain.objects.CreditCardOperationTypes
import io.devpass.creditcard.domain.objects.operation.CreditCardCharge
import io.devpass.creditcard.domainaccess.ICreditCardOperationServiceAdapter
import org.slf4j.LoggerFactory
import java.time.LocalDate

class CreditCardOperationService(
    private val creditCardDAO: ICreditCardDAO,
    private val creditCardInvoiceDAO: ICreditCardInvoiceDAO,
    private val creditCardOperationDAO: ICreditCardOperationDAO,
) : ICreditCardOperationServiceAdapter {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getById(creditCardOperationId: String): CreditCardOperation? {
        return creditCardOperationDAO.getOperationById(creditCardOperationId)
    }

    override fun charge(creditCardCharge: CreditCardCharge): List<CreditCardOperation> {
        if (creditCardCharge.value < 0.0)
            throw BusinessRuleException("You must provide a valid value to charge this credit card.")

        if (creditCardCharge.installments < 1 || creditCardCharge.installments > 12)
            throw BusinessRuleException("Installment number should be between 1 and 12")

        if (creditCardCharge.installments > 1 && creditCardCharge.value < 6.0)
            throw BusinessRuleException("Value of purchase must be at least R$ 6.00 when installment number is bigger than 1")

        val creditCard = creditCardDAO.getById(creditCardCharge.creditCardId)
            ?: throw EntityNotFoundException("Credit card not found with ID ${creditCardCharge.creditCardId}")

        if (creditCard.availableCreditLimit < creditCardCharge.value)
            throw BusinessRuleException("This credit card does not have enough limit for this purchase")

        val nextChargeablePeriod = getNextChargeableMonth(creditCard.id)
        val installments = generateInstallments(creditCardCharge, nextChargeablePeriod).map {
            creditCardOperationDAO.create(it)
        }
        logger.info("Generated installments $installments")

        creditCard.availableCreditLimit -= creditCardCharge.value
        creditCardDAO.update(creditCard)
        logger.info("Updated credit card limit of credit card ${creditCard.id}")

        return installments
    }

    override fun rollback(creditCardOperationId: String) {
        val creditCardOperation = creditCardOperationDAO.getOperationById(creditCardOperationId)
            ?: throw EntityNotFoundException("Operation not found with ID $creditCardOperationId")

        if (creditCardOperation.type != CreditCardOperationTypes.CHARGE)
            throw BusinessRuleException("You cannot rollback an operation that isn't of type ${CreditCardOperationTypes.CHARGE}")

        val creditCard = creditCardDAO.getById(creditCardOperation.creditCard)
            ?: throw EntityNotFoundException("Credit card not found with ID ${creditCardOperation.creditCard}")

        creditCardOperationDAO.create(
            CreditCardOperation(
                id = "", // will be auto-generated
                creditCard = creditCardOperation.creditCard,
                type = CreditCardOperationTypes.ROLLBACK,
                value = creditCardOperation.value * -1,
                month = LocalDate.now().monthValue,
                year = LocalDate.now().year,
                description = "Rollback of operation ${creditCardOperation.id}.",
            )
        )
        logger.info("Created rollback operation for operation $creditCardOperationId")

        creditCard.availableCreditLimit += creditCardOperation.value
        creditCardDAO.update(creditCard)

        logger.info("Updated available limit for credit card ${creditCard.id}")
    }

    override fun listByPeriod(creditCardId: String, month: Int, year: Int): List<CreditCardOperation> {
        if (year < 0)
            throw BusinessRuleException("You must provide a valid year to list operations")
        if (month < 1 || month > 12)
            throw BusinessRuleException("You must provide a valid month to list operations")
        if (creditCardDAO.getById(creditCardId) == null)
            throw EntityNotFoundException("Credit card not found with ID $creditCardId")
        return creditCardOperationDAO.listByPeriod(creditCardId, month, year)
    }

    private fun generateInstallments(creditCardCharge: CreditCardCharge, nextChargeablePeriod: ChargeablePeriod): List<CreditCardOperation> {
        var installmentDate = LocalDate.of(nextChargeablePeriod.year, nextChargeablePeriod.month, 1)
        val listOfCreditCardOperation = mutableListOf<CreditCardOperation>()
        val installmentValue = creditCardCharge.value / creditCardCharge.installments
        for (i in 1..creditCardCharge.installments) {
            listOfCreditCardOperation += CreditCardOperation(
                id = "", // will be auto-generated
                creditCard = creditCardCharge.creditCardId,
                type = CreditCardOperationTypes.CHARGE,
                value = installmentValue,
                month = installmentDate.monthValue,
                year = installmentDate.year,
                description = "${creditCardCharge.description} - $i/${creditCardCharge.installments}",
            )
            installmentDate = installmentDate.plusMonths(1)
        }
        return listOfCreditCardOperation
    }

    private fun getNextChargeableMonth(creditCardId: String): ChargeablePeriod {
        var referenceDate = LocalDate.now().withDayOfMonth(1)
        while (creditCardInvoiceDAO.getByPeriod(creditCardId, referenceDate.monthValue, referenceDate.year) != null) {
            referenceDate = referenceDate.plusMonths(1)
        }
        return ChargeablePeriod(referenceDate.monthValue, referenceDate.year)
    }
}