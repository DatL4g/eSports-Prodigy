@file:Suppress("NewApi")

package dev.datlag.esports.prodigy

actual fun getPackageName(): String {
    // requires class in root directory
    val clazz = AppIO::class

    return clazz.java.packageName.ifBlank { null } ?: run {
        var cutPackage = (clazz.qualifiedName ?: clazz.java.canonicalName).substringBeforeLast(clazz.simpleName ?: clazz.java.simpleName)

        if (cutPackage.startsWith('.')) {
            cutPackage = cutPackage.substring(1)
        }
        if (cutPackage.endsWith('.')) {
            cutPackage = cutPackage.substringBeforeLast('.')
        }

        cutPackage
    }
}