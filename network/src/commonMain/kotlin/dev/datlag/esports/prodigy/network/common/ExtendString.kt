package dev.datlag.esports.prodigy.network.common

import dev.datlag.esports.prodigy.model.common.scopeCatching
import kotlinx.datetime.*
import kotlin.math.abs
import kotlin.time.Duration.Companion.days

internal fun String.parseToEpochSeconds(timezone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return (scopeCatching {
        this.toInstant()
    }.getOrNull() ?: scopeCatching {
        this.toLocalDateTime().toInstant(timezone)
    }.getOrNull() ?: scopeCatching {
        this.toLocalDate().atStartOfDayIn(timezone)
    }.getOrNull() ?: scopeCatching {
        this.toLocalTime().atDate(Clock.System.todayIn(timezone)).toInstant(timezone)
    }.getOrNull())?.epochSeconds ?: when {
        this.equals("now", true) -> Clock.System.now().epochSeconds
        this.equals("today", true) -> Clock.System.todayIn(timezone).atStartOfDayIn(timezone).epochSeconds
        this.equals("yesterday", true) -> Clock.System.todayIn(timezone).minus(1, DateTimeUnit.DAY).atStartOfDayIn(timezone).epochSeconds
        else -> {
            val now = Clock.System.now()
            var edit = Clock.System.now()

            val yearRegex = "((\\d+) year(s)?)".toRegex(RegexOption.IGNORE_CASE)
            val monthRegex = "((\\d+) month(s)?)".toRegex(RegexOption.IGNORE_CASE)
            val weekRegex = "((\\d+) week(s)?)".toRegex(RegexOption.IGNORE_CASE)
            val dayRegex = "((\\d+) day(s)?)".toRegex(RegexOption.IGNORE_CASE)
            val hourRegex = "((\\d+) hour(s)?)".toRegex(RegexOption.IGNORE_CASE)
            val minuteRegex = "((\\d+) minute(s)?)".toRegex(RegexOption.IGNORE_CASE)

            yearRegex.find(this)?.let { result ->
                result.groups[1]?.value?.ifBlank { null }?.toIntOrNull()?.let {
                    edit = edit.minus(it, DateTimeUnit.YEAR, timezone)
                }
            }
            monthRegex.find(this)?.let { result ->
                result.groups[1]?.value?.ifBlank { null }?.toIntOrNull()?.let {
                    edit = edit.minus(it, DateTimeUnit.MONTH, timezone)
                }
            }
            weekRegex.find(this)?.let { result ->
                result.groups[1]?.value?.ifBlank { null }?.toIntOrNull()?.let {
                    edit = edit.minus(it, DateTimeUnit.WEEK, timezone)
                }
            }
            dayRegex.find(this)?.let { result ->
                result.groups[1]?.value?.ifBlank { null }?.toIntOrNull()?.let {
                    edit = edit.minus(it, DateTimeUnit.DAY, timezone)
                }
            }
            hourRegex.find(this)?.let { result ->
                result.groups[1]?.value?.ifBlank { null }?.toIntOrNull()?.let {
                    edit = edit.minus(it, DateTimeUnit.HOUR, timezone)
                }
            }
            minuteRegex.find(this)?.let { result ->
                result.groups[1]?.value?.ifBlank { null }?.toIntOrNull()?.let {
                    edit = edit.minus(it, DateTimeUnit.MINUTE, timezone)
                }
            }

            if (abs(edit.until(now, DateTimeUnit.MINUTE, timezone)) > 1 || abs(now.until(edit, DateTimeUnit.MINUTE, timezone)) > 1) {
                edit.epochSeconds
            } else {
                0L
            }
        }
    }
}