package dev.datlag.esports.prodigy.other

expect class Commonizer {

    fun openInBrowser(url: String, error: String = String()): Result<Any>

    fun restartApp()
}