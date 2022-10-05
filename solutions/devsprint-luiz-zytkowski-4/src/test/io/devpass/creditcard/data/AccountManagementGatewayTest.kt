package io.devpass.creditcard.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.Body
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import io.devpass.creditcard.data.http.response.DefaultHttpResponse
import io.devpass.creditcard.domain.exceptions.GatewayException
import io.devpass.creditcard.domain.objects.accountmanagement.Transaction
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.net.URL

class AccountManagementGatewayTest {
    private val originalClient = FuelManager.instance.client
    @AfterEach
    fun afterEach(){
        FuelManager.instance.client = originalClient
    }

    @Test
    fun `Should successfully process a transaction using withdraw method`(){
        val expectedResult = DefaultHttpResponse("")
        val json = jacksonObjectMapper().writeValueAsString(expectedResult)
        val body = mockk<Body>{
            every { toByteArray() } returns json.toByteArray()
            every { toStream() } returns toByteArray().inputStream()
        }
        val client = mockk<Client>{
            every { executeRequest(any()) } returns Response (
                    url = URL("http://devpass-unit-test.com"),
                    statusCode = HttpStatus.OK.value(),
                    responseMessage = "OK.",
                    body = body,
                    )
        }
        FuelManager.instance.client = client

        val accountManagementGateway = AccountManagementGateway("http://devpass-unit-test.com")
        val withdrawMethodSuccessResponse = accountManagementGateway.withdraw(Transaction("",10.0))
        Assertions.assertEquals(expectedResult.toActionResponse(), withdrawMethodSuccessResponse)
    }
    @Test
    fun `Should throw a GatewayException for unsuccessful transactions using withdraw method`(){
        val client = mockk<Client>{
            every { executeRequest(any()) } returns Response (
                    url = URL("http://devpass-unit-test.com"),
                    statusCode = HttpStatus.UNAUTHORIZED.value(),
                    responseMessage = "Unable to proceed your request - insufficient funds.",
            )
        }
        FuelManager.instance.client = client
        val expectedResult = GatewayException("")
        val accountManagementGateway = AccountManagementGateway("http://devpass-unit-test.com")
        val withdrawMethodErrorResponse = accountManagementGateway.withdraw(Transaction("", 0.0))
        Assertions.assertEquals(expectedResult, withdrawMethodErrorResponse)
    }
}