package dev.datlag.esports.prodigy.other

object Constants {

    const val LINUX_DARK_MODE_CMD = "gsettings get org.gnome.desktop.interface color-scheme"
    const val LINUX_DARK_MODE_LEGACY_CMD = "gsettings get org.gnome.desktop.interface gtk-theme"
    const val LINUX_THEME_MONITOR_CMD = "gsettings monitor org.gnome.desktop.interface color-scheme"
    const val LINUX_THEME_MONITOR_LEGACY_CMD = "gsettings monitor org.gnome.desktop.interface gtk-theme"

    const val WIN_THEME_REGISTRY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize"
    const val WIN_THEME_REGISTRY_VALUE = "AppsUseLightTheme"

}