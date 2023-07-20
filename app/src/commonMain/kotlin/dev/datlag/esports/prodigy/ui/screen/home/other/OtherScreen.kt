package dev.datlag.esports.prodigy.ui.screen.home.other

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.datlag.esports.prodigy.SharedRes
import dev.datlag.esports.prodigy.other.Constants
import dev.datlag.esports.prodigy.ui.LocalCommonizer
import dev.datlag.esports.prodigy.ui.theme.LocalDarkMode
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun OtherScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val res = if (LocalDarkMode.current) {
                SharedRes.images.wip_dark
            } else {
                SharedRes.images.wip_light
            }

            Image(
                modifier = Modifier.fillMaxWidth(0.5F),
                painter = painterResource(res),
                contentDescription = "Welcome",
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth
            )

            val github = "GitHub"
            val textColor = MaterialTheme.colorScheme.onBackground
            val highlightColor = MaterialTheme.colorScheme.primary

            val annotatedString = buildAnnotatedString {
                withStyle(MaterialTheme.typography.bodyMedium.toParagraphStyle().copy(textAlign = TextAlign.Center)) {
                    withStyle(SpanStyle(color = textColor)) {
                        append("More games and features are in progress.")
                        appendLine()
                        append("Create requests, bug reports and other issues at ")
                    }
                    withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                        pushStringAnnotation(tag = github, annotation = github)
                        append(github)
                    }
                }
            }
            val commonizer = LocalCommonizer.current
            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let { span ->
                        if (span.tag == github) {
                            commonizer.openInBrowser(Constants.GITHUB_PROJECT)
                        }
                    }
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        commonizer.openInBrowser(Constants.GITHUB_PROJECT)
                    }
                ) {
                    Icon(
                        painter = painterResource(SharedRes.images.GitHub),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = "GitHub",
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {
                        commonizer.openInBrowser(Constants.GITHUB_SPONSOR)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = "Sponsor",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}