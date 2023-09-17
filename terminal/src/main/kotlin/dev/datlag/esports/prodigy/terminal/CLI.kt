package dev.datlag.esports.prodigy.terminal

import com.github.rvesse.airline.annotations.Cli
import com.github.rvesse.airline.help.cli.CliGlobalUsageGenerator
import dev.datlag.esports.prodigy.model.common.scopeCatching

@Cli(
    name = "eSports-Prodigy",
    description = "CommandLine interface for some eSports Prodigy in-app-features",
    commands = [ DXVKCommand::class ]
)
class CLI {
    companion object {
        fun initAndRun(args: Collection<String>, onError: () -> Unit) {
            val cmdResult = scopeCatching {
                val cli = com.github.rvesse.airline.Cli<CommandLine>(CLI::class.java)

                scopeCatching {
                    CliGlobalUsageGenerator<CommandLine>().apply {
                        usage(cli.metadata, System.out)
                    }
                }.getOrNull()

                cli.parse(*args.toTypedArray())
            }

            cmdResult.getOrNull()?.run() ?: run {
                cmdResult.exceptionOrNull()?.message?.let(::println)
                onError()
            }
        }
    }
}