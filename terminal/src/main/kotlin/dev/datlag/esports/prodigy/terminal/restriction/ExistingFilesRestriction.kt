package dev.datlag.esports.prodigy.terminal.restriction

import com.github.rvesse.airline.help.sections.HelpFormat
import com.github.rvesse.airline.help.sections.HelpHint
import com.github.rvesse.airline.model.ArgumentsMetadata
import com.github.rvesse.airline.model.OptionMetadata
import com.github.rvesse.airline.parser.ParseState
import com.github.rvesse.airline.parser.errors.ParseRestrictionViolatedException
import com.github.rvesse.airline.restrictions.AbstractCommonRestriction
import dev.datlag.esports.prodigy.model.common.canReadSafely
import dev.datlag.esports.prodigy.model.common.canWriteSafely
import dev.datlag.esports.prodigy.model.common.existsSafely
import java.io.File

class ExistingFilesRestriction(private val flag: Int) : AbstractCommonRestriction(), HelpHint {

    private fun validate(optionTitle: String, value: Any?) {
        val exception = ParseRestrictionViolatedException(
            "Option '%s' must be valid file path(s) but got '%s'",
            optionTitle,
            value.toString()
        )

        fun validateFiles(files: List<File>) {
            if (files.isEmpty()) {
                throw exception
            } else {
                val valid = when {
                    flag >= EXISTING_FILES_FLAG_WRITE -> {
                        files.any { it.canWriteSafely() }
                    }
                    flag == EXISTING_FILES_FLAG_READ -> {
                        files.any { it.canReadSafely() }
                    }
                    else -> true
                }
                if (!valid) {
                    throw exception
                }
            }
        }

        when {
            value is Array<*> && value.isArrayOf<String>() -> {
                val paths = value as Array<String>
                val files = paths.map {
                    File(it)
                }.filter { it.existsSafely() }

                validateFiles(files)
            }
            value is Collection<*> -> {
                val paths = value as? Collection<String> ?: throw exception
                val files = paths.map {
                    File(it)
                }.filter { it.existsSafely() }

                validateFiles(files)
            }
            value is String -> {
                val files = listOf(File(value)).filter {
                    it.existsSafely()
                }

                validateFiles(files)
            }
            else -> throw exception
        }
    }

    override fun <T : Any?> postValidate(state: ParseState<T>?, arguments: ArgumentsMetadata?, value: Any?) {
        super.postValidate(state, arguments, value)

        validate(arguments?.title?.firstOrNull() ?: String(), value)
    }

    override fun <T : Any?> postValidate(state: ParseState<T>?, option: OptionMetadata?, value: Any?) {
        super.postValidate(state, option, value)

        validate(option?.titles?.firstOrNull() ?: String(), value)
    }

    override fun getPreamble(): String? {
        return null
    }

    override fun getFormat(): HelpFormat {
        return HelpFormat.LIST
    }

    override fun numContentBlocks(): Int {
        return 1
    }

    override fun getContentBlock(blockNumber: Int): Array<String> {
        return arrayOf("This options value must be an array of valid file paths")
    }
}