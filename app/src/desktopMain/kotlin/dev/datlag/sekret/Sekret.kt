package dev.datlag.sekret

class Sekret {

    companion object {
        init {
            NativeLoader.loadLibrary(Sekret::class.java.classLoader, "sekret")
        }
    }

    external fun testKey(it: String): String
}