package dev.datlag.esports.prodigy.network.scraper

import dev.datlag.esports.prodigy.model.common.scopeCatching
import dev.datlag.esports.prodigy.model.common.suspendCatching
import dev.datlag.esports.prodigy.model.hltv.Country
import dev.datlag.esports.prodigy.model.hltv.Home
import dev.datlag.esports.prodigy.model.hltv.News
import dev.datlag.esports.prodigy.model.hltv.Team
import dev.datlag.esports.prodigy.network.common.findFirstOrNull
import dev.datlag.esports.prodigy.network.common.parseToEpochSeconds
import dev.datlag.esports.prodigy.network.fetcher.KtorFetcher
import io.ktor.client.*
import io.ktor.client.request.*
import it.skrape.core.document
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape

object HLTVScraper {

    suspend fun scrapeHome(client: HttpClient): Result<Home> {
        return skrape(KtorFetcher(client)) {
            request {
                url("https://www.hltv.org")
            }
            response {
                if (responseStatus.code != 200) {
                    Result.failure(Exception(responseStatus.message))
                } else {
                    val contentCol = document.findFirstOrNull(".colCon .contentCol")
                    val leftCol = document.findFirstOrNull(".colCon .leftCol")
                    val teamEntries = leftCol?.findAll(".rank") ?: emptyList()

                    val hero = scopeCatching {
                        val heroCon = contentCol?.findFirst(".hero-con") ?: return@scopeCatching null
                        val link = heroCon.findFirst("a")
                        val (img, href) = (link.eachImage.values.firstOrNull() ?: String()) to (link.eachHref.firstOrNull() ?: String())
                        if (img.isBlank() || href.isBlank()) {
                            return@scopeCatching null
                        }
                        Home.Hero(
                            img = img,
                            href = href
                        )
                    }.getOrNull()

                    val teams = teamEntries.mapIndexedNotNull { index, teamElement ->
                        scopeCatching {
                            val rankText = teamElement.findFirstOrNull(".rankNum")?.text
                                ?: teamElement.findFirstOrNull("a")?.text
                            val iconLight = teamElement.findFirstOrNull(".day-only")?.eachSrc?.firstOrNull()?.ifBlank { null }
                            val iconDark = teamElement.findFirstOrNull(".night-only")?.eachSrc?.firstOrNull()?.ifBlank { null }
                            val fallbackImage = teamElement.findFirstOrNull("img")?.eachSrc?.firstOrNull()?.ifBlank { null }
                            val teamInfo = teamElement.findLast("a")

                            Home.Team(
                                ranking = rankText?.toIntOrNull() ?: (index + 1),
                                imgLight = iconLight ?: fallbackImage ?: iconDark ?: String(),
                                imgDark = iconDark ?: iconLight ?: fallbackImage ?: String(),
                                name = teamInfo.text,
                                href = teamInfo.eachHref.firstOrNull() ?: String()
                            )
                        }.getOrNull()
                    }

                    Result.success(Home(hero, teams))
                }
            }
        }
    }

    suspend fun scrapeNews(client: HttpClient): Result<List<News>> {
        return skrape(KtorFetcher(client)) {
            request {
                url("https://www.hltv.org/news/archive")
            }
            response {
                if (responseStatus.code != 200) {
                    Result.failure(Exception(responseStatus.message))
                } else {
                    Result.success(
                        document.findAll(".article").map { element ->
                            val newsFlag = element.findFirst(".newsflag")
                            News(
                                link = element.eachHref.first(),
                                title = element.findFirst(".newstext").text,
                                date = element.findFirst(".newsrecent").text.parseToEpochSeconds(),
                                country = Country(
                                    name = newsFlag.attribute("alt"),
                                    code = newsFlag.attribute("src").split('/').last().split('.').first()
                                )
                            )
                        }
                    )
                }
            }
        }
    }

    suspend fun scrapeTeam(href: String, id: Number, client: HttpClient): Result<Team> {
        var targetHref = href
        if (targetHref.startsWith('/')) {
            targetHref = targetHref.substring(1)
        }
        if (targetHref.startsWith("team", true)) {
            targetHref = targetHref.substringAfter('/')
        }

        return skrape(KtorFetcher(client)) {
            request {
                url("https://www.hltv.org/team/$targetHref")
            }
            response {
                if (responseStatus.code != 200) {
                    Result.failure(Exception(responseStatus.message))
                } else {
                    val name = document.findFirst(".profile-team-name").text
                    val logoSrc = document.findFirst(".teamLogo").eachSrc.firstOrNull()
                    val logo = if (logoSrc?.contains("placeholder.svg", true) == true) null else logoSrc

                    val facebook = scopeCatching {
                        document.findAll(".facebook").firstOrNull()?.parent?.eachHref?.firstOrNull()
                    }.getOrNull()
                    val twitter = scopeCatching {
                        document.findAll(".twitter").firstOrNull()?.parent?.eachHref?.firstOrNull()
                    }.getOrNull()
                    val instagram = scopeCatching {
                        document.findAll(".instagram").firstOrNull()?.parent?.eachHref?.firstOrNull()
                    }.getOrNull()
                    val rank = document.findFirst(".profile-team-stat .right").text.replace("#", "").toIntOrNull() ?: 0
                    val players = document.findAll(".players-table tbody tr").map { element ->
                        val playerName = element.findFirst(".playersBox-playernick-image .playersBox-playernick .text-ellipsis").text
                        val playerId = element.findFirst(".playersBox-playernick-image").eachHref.firstOrNull()?.split('/')?.get(1)?.toIntOrNull() ?: 0
                        val timeOnTeam = element.findByIndex(2, "td").text.trim()
                        val mapsPlayed = element.findByIndex(3, "td").text.trim().toIntOrNull() ?: -1
                        val type = element.findFirst(".player-status").text.trim()
                        val img = element.findFirstOrNull(".playersBox-img-wrapper img")?.eachSrc?.firstNotNullOfOrNull {
                            it.ifBlank { null }
                        }
                        val flagElement = element.findFirstOrNull(".playersBox-playernick .flag")
                        val country = flagElement?.let {
                            Country(
                                name = it.attribute("alt"),
                                code = it.eachSrc.firstOrNull()?.split('/')?.last()?.split('.')?.first() ?: String()
                            )
                        }

                        Team.Player(
                            type = Team.Player.Type.byLabel(type),
                            id = playerId,
                            name = playerName,
                            timeOnTeam = timeOnTeam,
                            mapsPlayed = mapsPlayed,
                            image = img,
                            country = country
                        )
                    }
                    val country = Country(
                        name = document.findFirst(".team-country .flag").attribute("alt"),
                        code = document.findFirst(".team-country .flag").eachSrc.firstOrNull()?.split('/')?.last()?.split('.')?.first() ?: String()
                    )

                    Result.success(
                        Team(
                            id = id,
                            name = name,
                            logo = logo,
                            socials = Team.Socials(
                                facebook = facebook,
                                twitter = twitter,
                                instagram = instagram
                            ),
                            country = country,
                            rank = rank,
                            players = players,
                            rankingDevelopment = emptyList(),
                            news = emptyList()
                        )
                    )
                }
            }
        }
    }
}