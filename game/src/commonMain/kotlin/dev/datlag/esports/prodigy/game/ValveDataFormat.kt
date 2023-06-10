package dev.datlag.esports.prodigy.game

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Convert Valve Data Format (vdf) to JSON.
 *
 * This got some tricky and hardly understandable regular expressions, which depend on each other with some string manipulation.
 *
 * A custom serialization decoder would be better probably, but this is way easier and faster implemented.
 *
 * @author Jeff Retz (DatLag)
 * @since 2022
 */
open class ValveDataFormat internal constructor(val json: Json) {

    fun toJsonString(value: String): String {
        if (isValidJson(value)) {
            return value
        }

        val keyValuePairsWithComma = "{${value.substringAfter('{')}".replace(VDF_ALL_ENDING_WITH_COMMA.toRegex()) {
            "${it.value},"
        }
        val keyValuePairsWithColon = keyValuePairsWithComma.replace(VDF_ALL_ENDING_WITH_COLON.toRegex()) {
            "${it.value}:"
        }
        val closingParenthesisWithComma = keyValuePairsWithColon.replace(VDF_ALL_ENDING_WITH_PARENTHESIS.toRegex()) {
            // the last match result is the end of the whole JSON and should not add a comma ','
            if (it.next() == null) {
                it.value
            } else {
                "${it.value},"
            }
        }
        return closingParenthesisWithComma
    }

    private fun isValidJson(value: String): Boolean {
        return try {
            json.parseToJsonElement(value)
            true
        } catch (ignored: SerializationException) {
            false
        }
    }

    inline fun <reified T> decodeFromString(value: String): T {
        return json.decodeFromString(toJsonString(value))
    }

    class Builder internal constructor(vdf: ValveDataFormat) {
        var json: Json = vdf.json
    }

    companion object Default : ValveDataFormat(Json {
        ignoreUnknownKeys = true
        isLenient = true
    }) {
        /**
         * Match entries that need a comma ',' at the end.
         *
         * This matches key -> value based entries only, like: "key"    "value"
         *
         * Appending a comma results to: "key"    "value",
         */
        private const val VDF_ALL_ENDING_WITH_COMMA = "\"\\S+\"\\s+\"(\\S|[ ])*\"(?!(\\s+)?})"

        /**
         * Match entries that need a colon ':' at the end.
         *
         * This only works after appending the comma from [VDF_ALL_ENDING_WITH_COMMA] and matches the key of key - value pairs, like: "key"    "value",
         *
         * Appending a colon results to: "key":    "value",
         */
        private const val VDF_ALL_ENDING_WITH_COLON = "\"(\\S|[ ])*\"(?!([,]|\\s+(}|])))"

        /**
         * Match entries that need a comma ',' at the end.
         * This should be done last, but doesn't really matter (I guess).
         *
         * This matches parenthesis '} ]' to fix the JSON formatting (object/list separation), like: { "key1"    "value" } { "key2"    "value" }
         *
         * Appending a comma (if it's not the last entry) results to: { "key1"    "value" }, { "key2"    "value" }
         */
        private const val VDF_ALL_ENDING_WITH_PARENTHESIS = "(}|])(?!([,]|\\s+(}|])))"
    }
}

fun ValveDataFormat(from: ValveDataFormat = ValveDataFormat, builder: ValveDataFormat.Builder.() -> Unit): ValveDataFormat {
    val vdf = ValveDataFormat.Builder(from)
    vdf.builder()
    return ValveDataFormat(vdf.json)
}