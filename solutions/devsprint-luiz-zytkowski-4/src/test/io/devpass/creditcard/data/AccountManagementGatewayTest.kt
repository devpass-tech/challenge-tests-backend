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
import io.devpass.creditcard.data.accountmanagement.response.AccountResponse
import io.devpass.creditcard.domain.exceptions.GatewayException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.net.URL

class AccountManagementGatewayTest {

    private val originalClient = FuelManager.instance.client

    @AfterEach
    fun afterEach() {
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
        val accountManagementGateway = AccountManagementGateway("http://devpass-unit-test.com")
        assertThrows<GatewayException> {
            accountManagementGateway.withdraw(Transaction("", 0.0))
        }
    }
    
    @Test
    fun `Should find account by Tax ID`() {
        val expectedResult = AccountResponse("", "", 0.0)
        val json = jacksonObjectMapper().writeValueAsString(expectedResult)
        val body = mockk<Body> {
            every { toByteArray() } returns json.toByteArray()
            every { toStream() } returns toByteArray().inputStream()
        }
        val client = mockk<Client> {
            every { executeRequest(any()) } returns Response(
                url = URL("http://devpass-account-management-gateway-test.com"),
                statusCode = HttpStatus.OK.value(),
                responseMessage = "OK",
                body = body,
            )
        }
        FuelManager.instance.client = client
        val accountManagementGateway = AccountManagementGateway("http://devpass-account-management-gateway-test.com")
        val accountResponse = accountManagementGateway.getByCPF("")
        assertEquals(expectedResult.toAccount(), accountResponse)
    }

    @Test
    fun `Should throw a GatewatException when account isn't found by Tax ID`() {
        val client = mockk<Client> {
            every { executeRequest(any()) } returns Response(
                url = URL("http://devpass-account-management-gateway-test.com"),
                statusCode = HttpStatus.BAD_REQUEST.value(),
                responseMessage = "Error finding account by Tax Id",
            )
        }
        FuelManager.instance.client = client
        val accountManagementGateway = AccountManagementGateway("http://devpass-account-management-gateway-test.com")
        assertThrows<GatewayException> {
            accountManagementGateway.getByCPF("")
        }
    }
}