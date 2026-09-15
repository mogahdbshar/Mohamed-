package com.dstwrtv.app.streaming.domain.metadata

/**
 * Third-party attribution required by metadata providers used by the app.
 * Keeping this in the domain layer lets the About/Legal UI expose the same
 * information without coupling the UI to individual network clients.
 */
data class MetadataAttribution(
    val providerId: String,
    val name: String,
    val attributionText: String,
    val licenseText: String? = null,
    val website: String? = null
)

object MetadataAttributions {
    val tvMaze = MetadataAttribution(
        providerId = "tvmaze",
        name = "TVMaze",
        attributionText = "TV data provided by TVMaze.",
        licenseText = "CC BY-SA 4.0",
        website = "https://www.tvmaze.com/"
    )

    val cinemeta = MetadataAttribution(
        providerId = "cinemeta",
        name = "Cinemeta",
        attributionText = "Metadata provided through the Cinemeta service.",
        website = "https://www.stremio.com/"
    )

    val all: List<MetadataAttribution> = listOf(cinemeta, tvMaze)
}
