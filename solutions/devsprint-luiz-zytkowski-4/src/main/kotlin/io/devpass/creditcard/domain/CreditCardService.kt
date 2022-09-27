package io.devpass.creditcard.domain

import io.devpass.creditcard.data.accountmanagement.request.AccountCreationRequest
import io.devpass.creditcard.dataaccess.IAccountManagementGateway
import io.devpass.creditcard.dataaccess.IAntiFraudGateway
import io.devpass.creditcard.dataaccess.ICreditCardDAO
import io.devpass.creditcard.domain.accountmanagement.AccountCreation
import io.devpass.creditcard.domain.creditcard.CreditCardNumberGenerator
import io.devpass.creditcard.domain.exceptions.BusinessRuleException
import io.devpass.creditcard.domain.exceptions.InvalidDataException
import io.devpass.creditcard.domain.extensions.validateAsCPF
import io.devpass.creditcard.domain.objects.CreditCard
import io.devpass.creditcard.domain.objects.CreditCardCreation
import io.devpass.creditcard.domainaccess.ICreditCardServiceAdapter
import org.slf4j.LoggerFactory

class CreditCardService(
    private val creditCardDAO: ICreditCardDAO,
    private val antiFraudGateway: IAntiFraudGateway,
    private val accountManagementGateway: IAccountManagementGateway,
) : ICreditCardServiceAdapter {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun findCreditCardById(creditCardId: String): CreditCard? {
        return creditCardDAO.getById(creditCardId)
    }

    override fun requestCreation(creditCardCreation: CreditCardCreation): CreditCard {
        if (!creditCardCreation.taxId.validateAsCPF())
            throw BusinessRuleException("Provided CPF is invalid.")
        if (creditCardCreation.printedName.trim().isBlank())
            throw BusinessRuleException("The name to be printed on the credit card must be provided")
        if (creditCardCreation.printedName.trim().length > 100)
            throw BusinessRuleException("The name to be printed on the credit card cannot exceed 100 characters.")

        creditCardDAO.getByTaxId(creditCardCreation.taxId)?.also {
            throw BusinessRuleException("This CPF already owns a credit card.")
        }

        val creditCardEligibility = antiFraudGateway.creditCardEligibility(creditCardCreation.taxId)
        logger.info("Credit card eligibility for tax id ${creditCardCreation.taxId}: $creditCardEligibility")

        if (!creditCardEligibility.shouldHaveCreditCard)
            throw BusinessRuleException("Unfortunately, we cannot provide you with a credit card at the moment.")

        val creditLimit = creditCardEligibility.proposedLimit
            ?: throw InvalidDataException("anti-fraud returned null proposed limit on a valid eligibility request")

        val account = accountManagementGateway.createAccount(AccountCreation(creditCardCreation.taxId))
        logger.info("Account created: $account")

        val creditCard = CreditCard(
            id = "", // will be auto-generated
            owner = creditCardCreation.taxId,
            number = CreditCardNumberGenerator.generateCreditCardNumber("124578", 12),
            securityCode = CreditCardNumberGenerator.generateCreditCardSecurityNumber(3),
            printedName = creditCardCreation.printedName,
            creditLimit = creditLimit,
            availableCreditLimit = creditLimit,
        )
        return creditCardDAO.create(creditCard).also {
            logger.info("Created credit card: $it")
        }
    }

}