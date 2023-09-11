package dev.datlag.esports.prodigy.ui.screen.home.rocketleague

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import dev.datlag.esports.prodigy.common.lifecycle.collectAsStateWithLifecycle
import dev.datlag.esports.prodigy.model.state.RequestState

@Composable
fun RocketLeagueView(component: RocketLeagueComponent) {
    val currentRequestState by component.eventsRequestState.collectAsStateWithLifecycle(RequestState.Loading)

    when (currentRequestState) {
        RequestState.Loading -> Text(text = "Loading events")
        is RequestState.Success -> {
            val events = (currentRequestState as RequestState.Success).events
            LazyColumn {
                items(events) { event ->
                    Text(text = event.name)
                }
            }
        }
        is RequestState.Error -> {
            Column {
                Text(text = "Error loading events")
                Text(text = (currentRequestState as RequestState.Error).msg)
                Button(
                    onClick = {
                        component.retryLoadingEvents()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(text = "Retry")
                }
            }
        }
    }
}