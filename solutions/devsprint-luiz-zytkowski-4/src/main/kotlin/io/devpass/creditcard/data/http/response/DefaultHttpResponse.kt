package io.devpass.creditcard.data.http.response

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import io.devpass.creditcard.domain.objects.ActionResponse

data class DefaultHttpResponse(
    val message: String,
) {
    fun toActionResponse(): ActionResponse {
        return ActionResponse(
            message
        )
    }

    companion object {

        fun fromFuelError(
            fuelError: FuelError?,
            fallBackMessage: String = "Unhandled exception.",
        ): DefaultHttpResponse {
            if (fuelError == null) return DefaultHttpResponse(fallBackMessage)
            return try {
                jacksonObjectMapper().readValue(fuelError.errorData, DefaultHttpResponse::class.java)
            } catch (e: Exception) {
                DefaultHttpResponse(fallBackMessage)
            }
        }

    }
}
