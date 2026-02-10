package dev.zt64.subsonic.api

import dev.zt64.subsonic.api.model.*
import dev.zt64.subsonic.client.ArrayUnwrapSerializer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf
import kotlin.time.Instant

internal class SubsonicApiImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val authParams: Map<String, String>
) : SubsonicApi {
    private val json = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(List::class) { args ->
                ArrayUnwrapSerializer(args[0])
            }
        }
    }

    private fun buildUrl(
        endpoint: String,
        params: Map<String, String?> = emptyMap(),
        includeAuth: Boolean = true
    ): String {
        return URLBuilder(baseUrl).apply {
            path(endpoint)
            if (includeAuth) {
                authParams.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }
            params.forEach { (key, value) ->
                if (value != null) {
                    parameters.append(key, value)
                }
            }
        }.buildString()
    }

    private suspend inline fun <reified T : Any> getSubsonicResponse(
        endPoint: String,
        builder: HttpRequestBuilder.() -> Unit = {},
        serializer: KSerializer<SubsonicResponse<T>> = serializer<SubsonicResponse<T>>()
    ): SubsonicResponse<T> {
        val res = httpClient
            .get(endPoint, builder)

        when (res.status) {
            HttpStatusCode.NotFound -> {
                throw SubsonicException("Host is down, or endpoint '$endPoint' is not implemented")
            }

            HttpStatusCode.Gone -> {
                throw SubsonicException("Endpoint '$endPoint' will not be implemented")
            }

            HttpStatusCode.NotImplemented -> {
                throw SubsonicException("Endpoint '$endPoint' is not implemented")
            }
        }

        return json.decodeFromJsonElement(serializer, res.body<JsonObject>()["subsonic-response"]!!)
    }

    @Throws(SubsonicException::class, CancellationException::class)
    private suspend inline fun <reified T : Any> get(
        endPoint: String,
        dataSerializer: KSerializer<T> = serializerFor<T>(json.serializersModule),
        builder: HttpRequestBuilder.() -> Unit = {}
    ): T {
        val responseSerializer = SubsonicResponse.serializer(dataSerializer)

        return when (val response = getSubsonicResponse(endPoint, builder, responseSerializer)) {
            is SubsonicResponse.Error -> {
                if (response.error.code == 70) {
                    // 70 means data not found
                    // TODO: Return null or throw
                }

                throw SubsonicException(
                    code = response.error.code,
                    message = response.error.message
                )
            }

            is SubsonicResponse.Success<T> -> {
                response.data
            }

            is SubsonicResponse.Empty -> {
                error("Expected data but received empty response")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> serializerFor(module: SerializersModule): KSerializer<T> {
        val type = typeOf<T>()
        return if (type.classifier == List::class) {
            val elementSerializer = json.serializersModule.serializer(type.arguments[0].type!!)
            ArrayUnwrapSerializer(elementSerializer)
        } else {
            module.serializer(type)
        } as KSerializer<T>
    }

    private suspend inline fun getNullable(
        endPoint: String,
        builder: HttpRequestBuilder.() -> Unit = {}
    ) {
        when (val response = getSubsonicResponse<Unit>(endPoint, builder)) {
            is SubsonicResponse.Error -> error(
                "Subsonic API error ${response.error.code}: ${response.error.message}"
            )

            is SubsonicResponse.Success<Unit> -> error("Unexpected response")

            is SubsonicResponse.Empty -> return
        }
    }

    private suspend inline fun reqBytes(
        endPoint: String,
        builder: HttpRequestBuilder.() -> Unit = {}
    ): ByteArray {
        return httpClient.get(endPoint, builder).bodyAsBytes()
    }

    override suspend fun ping() {
        return getNullable("ping.view")
    }

    override suspend fun tokenInfo(): TokenInfo {
        return get("tokenInfo.view")
    }

    override suspend fun getLicense(): License {
        return get("getLicense.view")
    }

    override suspend fun getOpenSubsonicExtensions(): List<SubsonicExtension> {
        return get("getOpenSubsonicExtensions.view")
    }

    override suspend fun changePassword(username: String, password: String) {
        getNullable("changePassword.view") {
            parameter("username", username)
            parameter("password", password)
        }
    }

    override suspend fun createUser(
        username: String,
        password: String,
        email: String,
        ldapAuthenticated: Boolean,
        roles: List<Role>
    ) {
        getNullable("createUser.view") {
            parameter("username", username)
            parameter("password", password)
            parameter("email", email)
            parameter("ldapAuthenticated", ldapAuthenticated)

            // TODO: Roles
        }
    }

    override suspend fun deleteUser(username: String) {
        getNullable("deleteUser.view") {
            parameter("username", username)
        }
    }

    override suspend fun getUser(username: String): User {
        return get("getUser.view") {
            parameter("username", username)
        }
    }

    override suspend fun getUsers(): List<User> {
        return get("getUsers.view")
    }

    override suspend fun updateUser(
        username: String,
        password: String,
        email: String?,
        ldapAuthenticated: Boolean?,
        roles: List<Role>?
    ) {
        getNullable("updateUser.view") {
            parameter("username", username)
            parameter("password", password)
            parameter("email", email)
            parameter("ldapAuthenticated", ldapAuthenticated)

            if (!roles.isNullOrEmpty()) {
                if (Role.ADMIN in roles) parameter("adminRole", true)
                if (Role.SETTINGS in roles) parameter("settingsRole", true)
                if (Role.STREAM in roles) parameter("streamRole", true)

                // TODO: Add other roles
            }
        }
    }

    override suspend fun getMusicFolders(): List<MusicFolder> {
        return get("getMusicFolders.view")
    }

    override suspend fun getIndexes(musicFolderId: String?): Indexes {
        return get("getIndexes.view")
    }

    override suspend fun getMusicDirectory() {
        return get("getMusicDirectory.view")
    }

    override suspend fun getGenres(): List<Genre> = get("getGenres.view")

    override suspend fun getArtists(folder: String?): Artists {
        return get("getArtists.view") {
            parameter("musicFolderId", folder)
        }
    }

    override suspend fun getArtist(id: String): Artist {
        return get("getArtist.view") {
            parameter("id", id)
        }
    }

    override suspend fun getAlbum(id: String): Album {
        return get("getAlbum.view") {
            parameter("id", id)
        }
    }

    override suspend fun getSong(id: String): Song {
        return get("getSong.view") {
            parameter("id", id)
        }
    }

    override suspend fun getVideoInfo() {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistInfo(
        id: String,
        maxSimilar: Int,
        includeNotPresent: Boolean
    ): ArtistInfo {
        return get("getArtistInfo.view") {
            parameter("id", id)
            parameter("count", maxSimilar)
            parameter("includeNotPresent", includeNotPresent)
        }
    }

    override suspend fun getArtistInfo(
        artist: Artist,
        maxSimilar: Int,
        includeNotPresent: Boolean
    ): ArtistInfo {
        return getArtistInfo(artist.id, maxSimilar, includeNotPresent)
    }

    override suspend fun getArtistInfo2(id: String, maxSimilar: Int, includeNotPresent: Boolean) {
        return get("getArtistInfo.view") {
            parameter("id", id)
            parameter("count", maxSimilar)
            parameter("includeNotPresent", includeNotPresent)
        }
    }

    override suspend fun getAlbumInfo(id: String): AlbumInfo {
        return get("getAlbumInfo.view") {
            parameter("id", id)
        }
    }

    override suspend fun getAlbumInfo2(id: String): AlbumInfo {
        return get("getAlbumInfo2.view") {
            parameter("id", id)
        }
    }

    override suspend fun getSimilarSongs(id: String, count: Int): List<Song> {
        return get("getSimilarSongs.view") {
            parameter("id", id)
        }
    }

    override suspend fun getSimilarSongs2(id: String, count: Int): List<Song> {
        return get("getSimilarSongs2.view") {
            parameter("id", id)
        }
    }

    override suspend fun getTopSongs(artist: String, count: Int): List<Song> {
        return get("getTopSongs.view") {
            parameter("artist", artist)
            parameter("count", count)
        }
    }

    override suspend fun getTopSongs(artist: Artist, count: Int): List<Song> {
        return getTopSongs(artist.name, count)
    }

    override suspend fun getAlbums(type: AlbumListType, size: Int, offset: Int): List<Album> {
        return get("getAlbumList.view") {
            parameter("type", type.value)
            parameter("size", size)
            parameter("offset", offset)

            when (type) {
                is AlbumListType.ByYear -> {
                    parameter("fromYear", type.fromYear)
                    parameter("toYear", type.toYear)
                }

                is AlbumListType.ByGenre -> {
                    parameter("genre", type.genre)
                }

                else -> {}
            }
        }
    }

    override suspend fun getAlbums2(type: AlbumListType, size: Int, offset: Int): List<Album> {
        return get("getAlbumList2.view") {
            parameter("type", type.value)
            parameter("size", size)
            parameter("offset", offset)

            when (type) {
                is AlbumListType.ByYear -> {
                    parameter("fromYear", type.fromYear)
                    parameter("toYear", type.toYear)
                }

                is AlbumListType.ByGenre -> {
                    parameter("genre", type.genre)
                }

                else -> {}
            }
        }
    }

    override suspend fun getRandomSongs(
        size: Int,
        genre: String?,
        fromYear: Int?,
        toYear: Int?
    ): List<Song> {
        return get("getRandomSongs.view") {
            parameter("size", size)
            parameter("genre", genre)
            parameter("fromYear", fromYear)
            parameter("toYear", toYear)
        }
    }

    override suspend fun getSongs(count: Int, genre: Genre?, musicFolderId: String): List<Song> {
        return get("getSongsByGenre.view") {
            parameter("count", count)
            parameter("genre", genre)
            parameter("musicFolderId", musicFolderId)
        }
    }

    override suspend fun getNowPlaying(): List<NowPlayingEntry> {
        return get("nowPlaying.view")
    }

    override suspend fun getStarred(musicFolder: MusicFolder?): Starred {
        return get("getStarred.view") {
            parameter("musicFolderId", musicFolder?.id)
        }
    }

    override suspend fun getStarred2(musicFolder: MusicFolder?): Starred {
        return get("getStarred2.view") {
            parameter("musicFolderId", musicFolder?.id)
        }
    }

    override suspend fun search(
        query: String,
        artistCount: Int,
        artistOffset: Int,
        albumCount: Int,
        albumOffset: Int,
        songCount: Int,
        songOffset: Int,
        musicFolderId: Int?
    ): SearchResult {
        return get("search2.view") {
            parameter("query", query)
            parameter("artistCount", artistCount)
            parameter("artistOffset", artistOffset)
            parameter("albumCount", albumCount)
            parameter("albumOffset", albumOffset)
            parameter("songCount", songCount)
            parameter("songOffset", songOffset)
            parameter("musicFolderId", musicFolderId)
        }
    }

    override suspend fun search3(
        query: String,
        artistCount: Int,
        artistOffset: Int,
        albumCount: Int,
        albumOffset: Int,
        songCount: Int,
        songOffset: Int,
        musicFolderId: Int?
    ): SearchResult {
        return get("search3.view") {
            parameter("query", query)
            parameter("artistCount", artistCount)
            parameter("artistOffset", artistOffset)
            parameter("albumCount", albumCount)
            parameter("albumOffset", albumOffset)
            parameter("songCount", songCount)
            parameter("songOffset", songOffset)
            parameter("musicFolderId", musicFolderId)
        }
    }

    override suspend fun getPlaylists(): List<Playlist> = get("getPlaylists.view")

    override suspend fun getPlaylist(id: String): Playlist {
        return get("getPlaylist.view") {
            parameter("id", id)
        }
    }

    override suspend fun createPlaylist(name: String, songIds: List<String>): Playlist {
        return get("createPlaylist.view") {
            parameter("name", name)
            songIds.forEach { id ->
                parameter("songId", id)
            }
        }
    }

    override suspend fun createPlaylistFromSongs(name: String, songs: List<Song>): Playlist {
        return createPlaylist(name, songs.map(Song::id))
    }

    override suspend fun updatePlaylist(
        id: String,
        name: String?,
        comment: String?,
        public: Boolean?,
        songIdsToAdd: List<String>,
        songIndicesToRemove: List<Int>
    ) {
        getNullable("updatePlaylist.view") {
            parameter("id", id)
            parameter("name", name)
            parameter("comment", comment)
            parameter("public", public)
            parameter("songIdToAdd", songIdsToAdd)
            parameter("songIndexToRemove", songIndicesToRemove)
        }
    }

    override suspend fun deletePlaylist(id: String) {
        getNullable("deletePlaylist.view") {
            parameter("id", id)
        }
    }

    override suspend fun stream(id: String, maxBitRate: Int, format: String?): ByteReadChannel {
        return httpClient.get("stream.view") {
            parameter("id", id)
        }.bodyAsChannel()
    }

    override fun getStreamUrl(id: String, maxBitRate: Int, format: String?): String {
        return buildUrl(
            "rest/stream.view",
            buildMap {
                put("id", id)
                if (maxBitRate > 0) put("maxBitRate", maxBitRate.toString())
                if (format != null) put("format", format)
            }
        )
    }

    override suspend fun download(id: String): ByteArray {
        // TODO: parse using subsonic response which is used for errors
        return httpClient.get("download.view") {
            parameter("id", id)
        }.bodyAsBytes()
    }

    override suspend fun hls(id: String, bitRate: Int?, audioTrack: String?): ByteArray {
        return reqBytes("hls.view") {
            parameter("id", id)
            parameter("bitrate", bitRate)
            parameter("audioTrack", audioTrack)
        }
    }

    override suspend fun getCaptions(videoId: String, format: String): List<String> {
        return get("getCaptions.view") {
            parameter("id", videoId)
            parameter("format", format)
        }
    }

    override suspend fun getCoverArt(id: String, size: String?): ByteArray {
        return reqBytes("getCoverArt.view") {
            parameter("id", id)
            parameter("size", size)
        }
    }

    override fun getCoverArtUrl(id: String, size: String?, auth: Boolean): String {
        return buildUrl(
            "rest/getCoverArt.view",
            buildMap {
                put("id", id)
                if (size != null) put("size", size)
            },
            includeAuth = auth
        )
    }

    override suspend fun getLyrics(artist: String, title: String): Lyrics {
        return get("getLyrics.view") {
            parameter("artist", artist)
            parameter("title", title)
        }
    }

    override suspend fun getLyrics(id: String): StructuredLyrics {
        return get("getLyricsBySongId.view") {
            parameter("id", id)
        }
    }

    override suspend fun getLyrics(song: Song): StructuredLyrics = getLyrics(song.id)

    override suspend fun getAvatar(username: String): ByteArray {
        return httpClient.get("getAvatar.view") {
            parameter("username", username)
        }.bodyAsBytes()
    }

    override fun getAvatarUrl(username: String, auth: Boolean): String {
        return buildUrl(
            "rest/getAvatar.view",
            mapOf("username" to username),
            includeAuth = auth
        )
    }

    override suspend fun star(vararg id: String) {
        require(id.isNotEmpty())

        getNullable("star.view") {
            parameter("id", id.toList())
        }
    }

    override suspend fun star(vararg item: Resource) {
        star(*item.map { it.id }.toTypedArray())
    }

    override suspend fun unstar(vararg id: String) {
        require(id.isNotEmpty())

        return getNullable("unstar.view") {
            parameter("id", id.toList())
        }
    }

    override suspend fun unstar(vararg items: Resource) {
        unstar(*items.map { it.id }.toTypedArray())
    }

    override suspend fun setRating(id: String, rating: Int) {
        getNullable("setRating.view") {
            parameter("id", id)
            parameter("rating", rating)
        }
    }

    override suspend fun scrobble(id: String, time: Instant, submission: Boolean) {
        getNullable("scrobble.view") {
            parameter("id", id)
            parameter("time", time)
            parameter("submission", submission)
        }
    }

    override suspend fun getShares(): List<Share> {
        return get("getShares.view")
    }

    override suspend fun createShare(
        entries: List<String>,
        description: String?,
        expires: Instant?
    ): Share {
        return get<List<Share>>("createShare.view") {
            entries.forEach { id ->
                parameter("id", id)
            }
            parameter("description", description)
            parameter("expires", expires)
        }.single()
    }

    override suspend fun updateShare(id: String, description: String?, expires: Instant?) {
        getNullable("updateShare.view") {
            parameter("id", id)
            parameter("description", description)
            parameter("expires", expires)
        }
    }

    override suspend fun deleteShare(id: String) {
        getNullable("deleteShare.view") {
            parameter("id", id)
        }
    }

    override suspend fun getPodcasts(includeEpisodes: Boolean): List<Podcast> {
        return get("getPodcasts.view") {
            parameter("includeEpisodes", includeEpisodes)
        }
    }

    override suspend fun getNewestPodcasts(): List<Podcast> {
        return get("getNewestPodcasts.view") {}
    }

    override suspend fun getPodcastEpisode(id: String) {
        return get("getPodcastEpisode.view") {}
    }

    override suspend fun refreshPodcasts() {
        getNullable("refreshPodcasts.view")
    }

    override suspend fun createPodcastChannel(url: String) {
        getNullable("createPodcastChannel.view") {
            parameter("url", url)
        }
    }

    override suspend fun deletePodcastChannel(id: String) {
        getNullable("deletePodcastChannel.view") {
            parameter("id", id)
        }
    }

    override suspend fun deletePodcastEpisode(id: String) {
        getNullable("deletePodcastEpisode.view") {
            parameter("id", id)
        }
    }

    override suspend fun downloadPodcastEpisode(id: String) {
        return get("downloadPodcastEpisode.view") {
            parameter("id", id)
        }
    }

    override suspend fun jukeboxControl(action: JukeboxAction, gain: Float) {
        return get("jukeboxControl.view") {
            parameter("action", action)

            when (action) {
                is JukeboxAction.Skip, is JukeboxAction.Skip -> {
                    parameter("index", action.index)
                }

                is JukeboxAction.Add -> {
                    parameter("id", action.id)
                }

                is JukeboxAction.Set -> {
                    parameter("id", action.id)
                }

                is JukeboxAction.SetGain -> {
                    parameter("gain", action.gain)
                }

                else -> {}
            }
        }
    }

    override suspend fun getInternetRadioStations(): List<InternetRadioStation> {
        return get("getInternetRadioStations.view")
    }

    override suspend fun createInternetRadioStation(
        streamUrl: String,
        name: String,
        homepageUrl: String?
    ) {
        return getNullable("createInternetRadioStation.view") {
            parameter("streamUrl", streamUrl)
            parameter("name", name)
            parameter("homepageUrl", homepageUrl)
        }
    }

    override suspend fun updateInternetRadioStation(
        id: String,
        streamUrl: String,
        name: String,
        homepageUrl: String?
    ) {
        getNullable("updateInternetRadioStation.view") {
            parameter("id", id)
            parameter("streamUrl", streamUrl)
            parameter("name", name)
            parameter("homepageUrl", homepageUrl)
        }
    }

    override suspend fun deleteInternetRadioStation(id: String) {
        getNullable("deleteRadioStation.view") {
            parameter("id", id)
        }
    }

    override suspend fun getChatMessages(): List<ChatMessage> {
        return get("getChatMessages.view")
    }

    override suspend fun addChatMessage(message: String) {
        getNullable("addChatMessage.view") {
            parameter("message", message)
        }
    }

    override suspend fun getBookmarks(): List<Bookmark<Resource>> {
        return get("getBookmarks.view")
    }

    override suspend fun createBookmark(id: String, position: Long, comment: String?) {
        return getNullable("createBookmark.view") {
            parameter("id", id)
            parameter("position", position)
            parameter("comment", comment)
        }
    }

    override suspend fun deleteBookmark(id: String) {
        getNullable("deleteBookmark.view") {
            parameter("id", id)
        }
    }

    override suspend fun getPlayQueue(): PlayQueue {
        return get("getPlayQueue.view")
    }

    override suspend fun getPlayQueueByIndex() {
        return get("getPlayQueueByIndex.view")
    }

    override suspend fun savePlayQueue(id: Long?, currentId: Long, position: Int) {
        getNullable("savePlayQueueByIndex") {
            parameter("id", id)
            parameter("current", currentId)
            parameter("position", position)
        }
    }

    override suspend fun savePlayQueue(id: String?, currentIndex: Int?, position: Long?) {
        getNullable("savePlayQueueByIndex") {
            parameter("id", id)
            parameter("currentIndex", currentIndex)
            parameter("position", position)
        }
    }

    override suspend fun getScanStatus(): ScanStatus {
        return get("getScanStatus.view")
    }

    override suspend fun startScan(): ScanStatus {
        return get("startScan.view")
    }

    override suspend fun getTranscodeDecision(id: String, mediaType: MediaType): TranscodeDecision {
        return get("getTranscodeDecision.view") {
            parameter("id", id)
            parameter("mediaType", mediaType)
        }
    }

    override suspend fun getTranscodeStream(
        id: String,
        mediaType: MediaType,
        offset: Int,
        params: String
    ): ByteArray {
        return reqBytes("getTranscodeStream.view") {
            parameter("id", id)
            parameter("mediaType", mediaType)
            parameter("offset", offset)
            parameter("transcodeParams", params)
        }
    }

    override suspend fun getTranscodeStream(
        id: String,
        mediaType: MediaType,
        offset: Int
    ): ByteArray {
        val decision = get<TranscodeDecision>("getTranscodeDecision.view") {
            parameter("id", id)
            parameter("mediaType", mediaType)
        }

        return reqBytes("getTranscodeStream.view") {
            parameter("id", id)
            parameter("mediaType", mediaType)
            parameter("offset", offset)
            parameter("transcodeParams", decision.transcodeParams)
        }
    }
}