package dev.datlag.esports.prodigy.terminal.restriction

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ExistingFiles(val extraFlag: Int = EXISTING_FILES_FLAG_NONE)