package dev.datlag.esports.prodigy.common

import android.os.Parcel
import android.os.Parcelable

fun Parcelable.getSizeInBytes(): Int {
    val parcel = Parcel.obtain()

    try {
        parcel.writeParcelable(this, 0)
        return parcel.marshall().size
    } finally {
        parcel.recycle()
    }
}