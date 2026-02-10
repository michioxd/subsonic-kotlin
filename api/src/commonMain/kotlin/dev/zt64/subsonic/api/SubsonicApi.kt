package dev.zt64.subsonic.api

import dev.zt64.subsonic.api.model.*
import io.ktor.utils.io.*
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Subsonic API
 */
public interface SubsonicApi {
    /**
     * Used to test connectivity with the server.
     */
    public suspend fun ping()

    /**
     * TODO
     *
     * @return
     */
    public suspend fun tokenInfo(): TokenInfo

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getLicense(): License

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getOpenSubsonicExtensions(): List<SubsonicExtension>

    /**
     * TODO
     *
     * @param username
     * @param password
     */
    public suspend fun changePassword(username: String, password: String)

    /**
     * TODO
     *
     * @param username
     * @param password
     * @param email
     * @param ldapAuthenticated
     * @param roles
     */
    public suspend fun createUser(
        username: String,
        password: String,
        email: String,
        ldapAuthenticated: Boolean = false,
        roles: List<Role> = emptyList()
    )

    /**
     * TODO
     *
     * @param username
     */
    public suspend fun deleteUser(username: String)

    /**
     * TODO
     *
     * @param username
     * @return
     */
    public suspend fun getUser(username: String): User

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getUsers(): List<User>

    /**
     * TODO
     *
     * @param username
     * @param password
     * @param email
     * @param ldapAuthenticated
     * @param roles
     */
    public suspend fun updateUser(
        username: String,
        password: String,
        email: String? = null,
        ldapAuthenticated: Boolean? = null,
        roles: List<Role>? = null
    )

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getMusicFolders(): List<MusicFolder>

    /**
     * TODO
     *
     * @param musicFolderId
     * @return
     */
    public suspend fun getIndexes(musicFolderId: String? = null): Indexes

    /**
     * TODO
     *
     */
    public suspend fun getMusicDirectory()

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getGenres(): List<Genre>

    /**
     * TODO
     *
     * @param folder
     * @return
     */
    public suspend fun getArtists(folder: String? = null): Artists

    /**
     * TODO
     *
     * @param id
     * @return
     */
    public suspend fun getArtist(id: String): Artist?

    /**
     * TODO
     *
     * @param id The album ID
     * @return
     */
    public suspend fun getAlbum(id: String): Album?

    /**
     * TODO
     *
     * @param id The song ID
     * @return
     */
    public suspend fun getSong(id: String): Song?

    /**
     * TODO
     *
     */
    public suspend fun getVideoInfo()

    /**
     * TODO
     *
     * @param id
     * @param maxSimilar
     * @param includeNotPresent
     * @return
     */
    public suspend fun getArtistInfo(
        id: String,
        maxSimilar: Int = 20,
        includeNotPresent: Boolean = false
    ): ArtistInfo

    public suspend fun getArtistInfo(
        artist: Artist,
        maxSimilar: Int = 20,
        includeNotPresent: Boolean = false
    ): ArtistInfo

    /**
     * TODO
     *
     * @param id
     * @param maxSimilar
     * @param includeNotPresent
     */
    public suspend fun getArtistInfo2(
        id: String,
        maxSimilar: Int = 20,
        includeNotPresent: Boolean = false
    )

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun getAlbumInfo(id: String): AlbumInfo

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun getAlbumInfo2(id: String): AlbumInfo

    /**
     * TODO
     *
     * @param id
     * @param count
     * @return
     */
    public suspend fun getSimilarSongs(id: String, count: Int = 50): List<Song>

    /**
     * TODO
     *
     * @param id
     * @param count
     * @return
     */
    public suspend fun getSimilarSongs2(id: String, count: Int = 50): List<Song>

    /**
     * Get top songs for an artist
     *
     * @return A list of top songs, or empty if the artist is missing or no songs found
     */
    public suspend fun getTopSongs(artist: String, count: Int = 50): List<Song>

    public suspend fun getTopSongs(artist: Artist, count: Int = 50): List<Song>

    /**
     * TODO
     *
     * @param type
     * @param size
     * @param offset
     * @return
     */
    public suspend fun getAlbums(type: AlbumListType, size: Int = 10, offset: Int = 0): List<Album>

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getAlbums2(type: AlbumListType, size: Int = 10, offset: Int = 0): List<Album>

    /**
     * TODO
     *
     * @param size
     * @param genre
     * @param fromYear
     * @param toYear
     * @return
     */
    public suspend fun getRandomSongs(
        size: Int = 10,
        genre: String? = null,
        fromYear: Int? = null,
        toYear: Int? = null
    ): List<Song>

    /**
     * TODO
     *
     * @param count
     * @param genre
     * @param musicFolderId
     * @return
     */
    public suspend fun getSongs(
        count: Int = 10,
        genre: Genre? = null,
        musicFolderId: String
    ): List<Song>

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getNowPlaying(): List<NowPlayingEntry>

    /**
     * TODO
     *
     * @param musicFolder
     */
    public suspend fun getStarred(musicFolder: MusicFolder? = null): Starred

    /**
     * TODO
     *
     * @param musicFolder
     */
    public suspend fun getStarred2(musicFolder: MusicFolder? = null): Starred

    /**
     * TODO
     *
     * @param query
     * @param artistCount
     * @param artistOffset
     * @param albumCount
     * @param albumOffset
     * @param songCount
     * @param songOffset
     * @param musicFolderId
     */
    public suspend fun search(
        query: String,
        artistCount: Int = 20,
        artistOffset: Int = 0,
        albumCount: Int = 20,
        albumOffset: Int = 0,
        songCount: Int = 20,
        songOffset: Int = 0,
        musicFolderId: Int? = null
    ): SearchResult

    /**
     * TODO
     *
     * @param query
     */
    public suspend fun search3(
        query: String,
        artistCount: Int = 20,
        artistOffset: Int = 0,
        albumCount: Int = 20,
        albumOffset: Int = 0,
        songCount: Int = 20,
        songOffset: Int = 0,
        musicFolderId: Int? = null
    ): SearchResult

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getPlaylists(): List<Playlist>

    /**
     * TODO
     *
     * @param id
     * @return
     */
    public suspend fun getPlaylist(id: String): Playlist

    /**
     * TODO
     *
     * @param name
     * @param songIds
     */
    public suspend fun createPlaylist(name: String, songIds: List<String>): Playlist

    /**
     * TODO
     *
     * @param name
     * @param songs
     */
    public suspend fun createPlaylistFromSongs(name: String, songs: List<Song>): Playlist

    /**
     * TODO
     *
     * @param id
     * @param name
     * @param comment
     * @param public
     * @param songIdsToAdd
     * @param songIndicesToRemove
     */
    public suspend fun updatePlaylist(
        id: String,
        name: String? = null,
        comment: String? = null,
        public: Boolean? = null,
        songIdsToAdd: List<String> = emptyList(),
        songIndicesToRemove: List<Int> = emptyList()
    )

    // public suspend fun updatePlaylist(
    //     id: String,
    //     name: String? = null,
    //     comment: String? = null,
    //     public: Boolean? = null,
    //     songsToAdd: List<Song> = emptyList(),
    //     songIndicesToRemove: List<Int> = emptyList()
    // )

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun deletePlaylist(id: String)

    /**
     * TODO
     *
     * @param id
     * @param maxBitRate
     * @param format
     * @return
     */
    public suspend fun stream(
        id: String,
        maxBitRate: Int = 0,
        format: String? = null
    ): ByteReadChannel

    public fun getStreamUrl(id: String, maxBitRate: Int = 0, format: String? = null): String

    /**
     * TODO
     *
     * @param id
     * @return
     */
    public suspend fun download(id: String): ByteArray

    /**
     * TODO
     *
     * @param id
     * @param bitRate
     * @param audioTrack
     * @return
     */
    public suspend fun hls(id: String, bitRate: Int? = null, audioTrack: String? = null): ByteArray

    /**
     * TODO
     *
     * @param videoId
     * @param format
     * @return
     */
    public suspend fun getCaptions(videoId: String, format: String): List<String>

    /**
     * TODO
     *
     * @param id
     * @param size
     * @return
     */
    public suspend fun getCoverArt(id: String, size: String? = null): ByteArray

    public fun getCoverArtUrl(id: String, size: String? = null, auth: Boolean = true): String

    /**
     * TODO
     *
     * @param artist
     * @param title
     * @return
     */
    public suspend fun getLyrics(artist: String, title: String): Lyrics

    /**
     * TODO
     *
     * @param id
     * @return
     */
    public suspend fun getLyrics(id: String): StructuredLyrics

    /**
     * TODO
     *
     * @param song
     * @return
     */
    public suspend fun getLyrics(song: Song): StructuredLyrics

    /**
     * TODO
     *
     * @param username
     * @return
     */
    public suspend fun getAvatar(username: String): ByteArray

    public fun getAvatarUrl(username: String, auth: Boolean = true): String

    /**
     * Star an item
     *
     * @param id
     */
    public suspend fun star(vararg id: String)

    /**
     * Star
     *
     * @param item
     */
    public suspend fun star(vararg item: Resource)

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun unstar(vararg id: String)

    /**
     * TODO
     *
     * @param items
     */
    public suspend fun unstar(vararg items: Resource)

    /**
     * TODO
     *
     * @param id
     * @param rating
     */
    public suspend fun setRating(id: String, rating: Int)

    /**
     * TODO
     *
     * @param id
     * @param time
     * @param submission
     */
    public suspend fun scrobble(
        id: String,
        time: Instant = Clock.System.now(),
        submission: Boolean = true
    )

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getShares(): List<Share>

    /**
     * TODO
     *
     * @param entries
     * @param description
     * @param expires
     */
    public suspend fun createShare(
        entries: List<String>,
        description: String? = null,
        expires: Instant? = null
    ): Share

    /**
     * TODO
     *
     * @param id
     * @param description
     * @param expires
     */
    public suspend fun updateShare(
        id: String,
        description: String? = null,
        expires: Instant? = null
    )

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun deleteShare(id: String)

    /**
     * TODO
     *
     * @param includeEpisodes
     * @return
     */
    public suspend fun getPodcasts(includeEpisodes: Boolean = true): List<Podcast>

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getNewestPodcasts(): List<Podcast>

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun getPodcastEpisode(id: String)

    /**
     * TODO
     *
     */
    public suspend fun refreshPodcasts()

    /**
     * TODO
     *
     * @param url
     */
    public suspend fun createPodcastChannel(url: String)

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun deletePodcastChannel(id: String)

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun deletePodcastEpisode(id: String)

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun downloadPodcastEpisode(id: String)

    /**
     * TODO
     *
     * @param action
     * @param gain
     */
    public suspend fun jukeboxControl(action: JukeboxAction, gain: Float)

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getInternetRadioStations(): List<InternetRadioStation>

    /**
     * TODO
     *
     * @param streamUrl
     * @param name
     * @param homepageUrl
     */
    public suspend fun createInternetRadioStation(
        streamUrl: String,
        name: String,
        homepageUrl: String? = null
    )

    /**
     * TODO
     *
     * @param id
     * @param streamUrl
     * @param name
     * @param homepageUrl
     */
    public suspend fun updateInternetRadioStation(
        id: String,
        streamUrl: String,
        name: String,
        homepageUrl: String? = null
    )

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun deleteInternetRadioStation(id: String)

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getChatMessages(): List<ChatMessage>

    /**
     * Add a message to the chat log
     *
     * @param message The message content
     */
    public suspend fun addChatMessage(message: String)

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getBookmarks(): List<Bookmark<Resource>>

    /**
     * Create or update a bookmark.
     *
     * @param id The ID of the media file to bookmark
     * @param position The position within the media file in milliseconds
     * @param comment An additional comment
     */
    public suspend fun createBookmark(id: String, position: Long, comment: String? = null)

    /**
     * TODO
     *
     * @param id
     */
    public suspend fun deleteBookmark(id: String)

    // -- Play queue --

    /**
     * TODO
     *
     * @return
     */
    public suspend fun getPlayQueue(): PlayQueue

    /**
     * TODO
     *
     */
    public suspend fun getPlayQueueByIndex()

    /**
     * TODO
     *
     * @param id
     * @param currentId
     * @param position
     */
    public suspend fun savePlayQueue(id: Long? = null, currentId: Long, position: Int = 0)

    /**
     * TODO
     *
     * @param id
     * @param currentIndex
     * @param position
     */
    public suspend fun savePlayQueue(
        id: String? = null,
        currentIndex: Int? = null,
        position: Long? = null
    )

    /**
     * Check the current of an active scan.
     */
    public suspend fun getScanStatus(): ScanStatus

    /**
     * Start a scan of the media library.
     */
    public suspend fun startScan(): ScanStatus

    /**
     * TODO
     *
     * @param id
     * @param mediaType
     * @return
     */
    public suspend fun getTranscodeDecision(id: String, mediaType: MediaType): TranscodeDecision

    /**
     * TODO
     *
     * @param id
     * @param mediaType
     * @param offset
     * @param params
     * @return
     */
    public suspend fun getTranscodeStream(
        id: String,
        mediaType: MediaType,
        offset: Int = 0,
        params: String
    ): ByteArray

    /**
     * Get a transcoded media stream
     *
     * @param id The ID of the media to be transcoded
     * @param mediaType
     * @param offset
     */
    public suspend fun getTranscodeStream(
        id: String,
        mediaType: MediaType,
        offset: Int = 0
    ): ByteArray
}