package dev.datlag.sekret

import kotlinx.cinterop.CPointer
import dev.datlag.sekret.JNIEnvVar
import dev.datlag.sekret.jclass
import dev.datlag.sekret.jstring

@CName("Java_dev_datlag_sekret_Sekret_test")
fun test(env: CPointer<JNIEnvVar>, clazz: jclass, it: jint): jint {
    initRuntimeIfNeeded()
    Platform.isMemoryLeakCheckerActive = false

    return it + 1
}