package dev.datlag.esports.prodigy.ui.screen.user

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
actual fun UserScreen(component: UserComponent) {

    Button(
        onClick = {
            component.back()
        }
    ) {
        Text(
            text = "Go Back"
        )
    }

}