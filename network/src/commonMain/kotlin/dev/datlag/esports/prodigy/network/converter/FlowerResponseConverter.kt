package dev.datlag.esports.prodigy.network.converter

import com.hadiyarajesh.flower_core.ApiResponse
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import de.jensklingenberg.ktorfit.upperBoundType
import dev.datlag.esports.prodigy.network.common.toCommonResponse
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FlowerResponseConverter : Converter.Factory {

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.ResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Flow::class) {
            if (typeData.typeInfo.upperBoundType()?.type == ApiResponse::class) {
                return object : Converter.ResponseConverter<HttpResponse, Any> {
                    override fun convert(getResponse: suspend () -> HttpResponse): Any {
                        return flow<ApiResponse<Any>> {
                            try {
                                val info = typeData.typeArgs.first().typeInfo
                                val kotlinType = info.upperBoundType() ?: throw IllegalArgumentException("Type must match Flow<ApiResponse<YourModel>>")

                                emit(
                                    ApiResponse.create(
                                        getResponse().toCommonResponse(kotlinType)
                                    )
                                )
                            } catch (e: Throwable) {
                                emit(ApiResponse.create(e))
                            }
                        }
                    }
                }
            }
        }
        return super.responseConverter(typeData, ktorfit)
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        return if (typeData.typeInfo.type == ApiResponse::class) {
            object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return try {
                        val info = typeData.typeArgs.first().typeInfo
                        ApiResponse.create(response.toCommonResponse(info))
                    } catch (e: Throwable) {
                        ApiResponse.create<Any>(e)
                    }
                }
            }
        } else {
            super.suspendResponseConverter(typeData, ktorfit)
        }
    }
}