package dev.datlag.esports.prodigy.network.fetcher

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import it.skrape.fetcher.BlockingFetcher
import it.skrape.fetcher.Domain
import it.skrape.fetcher.Expires
import it.skrape.fetcher.Result
import kotlinx.coroutines.runBlocking

class KtorFetcher(val client: HttpClient) : BlockingFetcher<HttpRequestBuilder> {

    override fun fetch(request: HttpRequestBuilder): Result = runBlocking {
        with(client.request(request)) {
            Result(
                responseBody = bodyAsText(),
                responseStatus = Result.Status(status.value, status.description),
                contentType = contentType()?.toString(),
                headers = headers.toMap().mapValues { it.value.firstOrNull().orEmpty() },
                baseUri = request.url.toString(),
                cookies = (setCookie().map { cookie ->
                    it.skrape.fetcher.Cookie(
                        name = cookie.name,
                        value = cookie.value,
                        maxAge = cookie.maxAge,
                        expires = cookie.expires?.toHttpDate()?.let { Expires.Date(it) } ?: Expires.Session,
                        domain = Domain(cookie.domain ?: String(), true),
                        path = cookie.path ?: String(),
                        secure = cookie.secure,
                        httpOnly = cookie.httpOnly
                    )
                })
            )
        }
    }

    override val requestBuilder: HttpRequestBuilder
        get() = HttpRequestBuilder()
}