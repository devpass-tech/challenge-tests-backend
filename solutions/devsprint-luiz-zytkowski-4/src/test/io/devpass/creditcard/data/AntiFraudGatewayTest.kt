package io.devpass.creditcard.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.Body
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import io.devpass.creditcard.domain.exceptions.OwnedException
import io.devpass.creditcard.domain.objects.antifraud.CreditCardEligibility
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.net.URL

class AntiFraudGatewayTest {

    @Test
    fun `Should get creditCardEligibility for the informed CPF`() {
        val expectedResult = CreditCardEligibility(true, 1.0)
        val json = jacksonObjectMapper().writeValueAsString(expectedResult)
        val body = mockk<Body> {
            every { toByteArray() } returns json.toByteArray()
            every { toStream() } returns toByteArray().inputStream()
        }
        val client = mockk<Client> {
            every { executeRequest(any()) } returns Response(
                url = URL("http://devpass-antifraud-gateway-test.com"),
                statusCode = HttpStatus.OK.value(),
                responseMessage = "OK",
                body = body,
            )
        }
        FuelManager.instance.client = client
        val antiFraudGateway = AntiFraudGateway("http://devpass-antifraud-gateway-test.com")
        val creditcardEligibilyResponse = antiFraudGateway.creditCardEligibility("")
        Assertions.assertEquals(expectedResult, creditcardEligibilyResponse)
    }

    @Test
    fun `Should throw an OwnedException when creditCardEligibity isn't avaiable for the informed CPF`() {
        val client = mockk<Client> {
            every { executeRequest(any()) } returns Response(
                url = URL("http://devpass-antifraud-gateway-test.com"),
                statusCode = HttpStatus.BAD_REQUEST.value(),
                responseMessage = "Error getting eligibility for informed CPF",
            )
        }
        FuelManager.instance.client = client
        val antiFraudGateway = AntiFraudGateway("http://devpass-antifraud-gateway-test.com")
        assertThrows<OwnedException> {
            antiFraudGateway.creditCardEligibility("")
        }
    }
}
