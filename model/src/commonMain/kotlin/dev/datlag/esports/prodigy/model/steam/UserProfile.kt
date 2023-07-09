package dev.datlag.esports.prodigy.model.steam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import nl.adaptivity.xmlutil.serialization.XmlCData
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
@SerialName("profile")
@XmlElement
data class UserProfile(
    @XmlElement @XmlCData @SerialName("avatarIcon") val avatarIcon: String?,
    @XmlElement @XmlCData @SerialName("avatarMedium") val avatarMedium: String?,
    @XmlElement @XmlCData @SerialName("avatarFull") val avatarFull: String?
) {

    @Transient
    val avatar: String? = avatarFull?.ifBlank { null } ?: avatarMedium?.ifBlank { null } ?: avatarIcon
}
