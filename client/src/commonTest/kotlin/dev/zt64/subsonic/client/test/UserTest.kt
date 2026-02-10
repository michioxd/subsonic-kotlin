package dev.zt64.subsonic.client.test

import dev.zt64.subsonic.client.SubsonicClient
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

// user interaction related tests
class UserTest {
    @Test
    fun testGetUser() = runTest {
        testEndpoint(
            endpoint = "getUser.view",
            response = """
                "user": {
                  "folder": [
                      1,
                      3
                  ],
                  "username": "sindre",
                  "email": "sindre@activeobjects.no",
                  "scrobblingEnabled": "true",
                  "adminRole": "false",
                  "settingsRole": "true",
                  "downloadRole": "true",
                  "uploadRole": "false",
                  "playlistRole": "true",
                  "coverArtRole": "true",
                  "commentRole": "true",
                  "podcastRole": "true",
                  "streamRole": "true",
                  "jukeboxRole": "true",
                  "shareRole": "false"
                }
            """.trimIndent()
        ) { getUser("123") }
    }

    @Test
    fun testDeleteUser() = runTest {
        testEndpoint(
            endpoint = "deleteUser.view",
            response = ""
        ) { deleteUser(USERNAME) }
    }

    @Test
    fun testUpdateUser() = runTest {
        testEndpoint(
            endpoint = "updateUser.view"
        ) { updateUser(USERNAME, PASSWORD, EMAIL, false) }
    }

    @Test
    fun testGetUsers() = runTest {
        testEndpoint(
            endpoint = "getUsers.view",
            response = """
                "users": {
                  "user": [
                    {
                      "folder": [
                          1,
                          3
                      ],
                      "username": "sindre",
                      "email": "sindre@activeobjects.no",
                      "scrobblingEnabled": "true",
                      "adminRole": "false",
                      "settingsRole": "true",
                      "downloadRole": "true",
                      "uploadRole": "false",
                      "playlistRole": "true",
                      "coverArtRole": "true",
                      "commentRole": "true",
                      "podcastRole": "true",
                      "streamRole": "true",
                      "jukeboxRole": "true",
                      "shareRole": "false"
                    }
                  ]
                }
            """.trimIndent()
        ) { getUsers() }
    }

    @Test
    fun testCreateUser() = runTest {
        testEndpoint(
            endpoint = "createUser.view"
        ) { createUser(USERNAME, PASSWORD, EMAIL) }
    }

    @Test
    fun testChangePassword() = runTest {
        testEndpoint(
            endpoint = "changePassword.view"
        ) { changePassword(USERNAME, PASSWORD) }
    }

    // Chat
    @Test
    fun testGetChatMessages() = runTest {
        testEndpoint(
            endpoint = "getChatMessages.view",
            response = """
                "chatMessages": {
                  "chatMessage": [
                    {
                      "username": "admin",
                      "time": 1678943707000,
                      "message": "Hello World"
                    }
                  ]
                }
            """.trimIndent()
        ) { getChatMessages() }
    }

    @Test
    fun testAddChatMessage() = runTest {
        testEndpoint(
            endpoint = "addChatMessage.view"
        ) { addChatMessage("Test message") }
    }

    // Shares
    @Test
    fun testGetShares() = runTest {
        testEndpoint(
            endpoint = "getShares.view",
            response = """
                
            """.trimIndent(),
            call = SubsonicClient::getShares
        )
    }

    @Test
    fun testCreateShare() = runTest {
        testEndpoint(
            endpoint = "createShare.view"
        ) {
            createShare(listOf("Wa5fzmngg4VgscnxP1c05u"), "test", Clock.System.now())
        }
    }

    @Test
    fun testUpdateShare() = runTest {
        val share = testEndpoint(
            endpoint = "createShare.view"
        ) {
            createShare(listOf("Wa5fzmngg4VgscnxP1c05u"), "test", Clock.System.now())
        }!!

        testEndpoint(
            endpoint = "updateShare.view"
        ) { updateShare(share.id, expires = Clock.System.now() + 5.days) }
    }

    @Test
    fun testDeleteShare() = runTest {
        val share = testEndpoint(
            endpoint = "createShare.view"
        ) {
            createShare(listOf("Wa5fzmngg4VgscnxP1c05u"), "test", Clock.System.now())
        }!!

        testEndpoint("deleteShare.view") {
            deleteShare(share.id)
        }
    }

    @Test
    fun testStar() = runTest {
        testEndpoint("star.view") { star("abc") }
    }

    @Test
    fun testUnstar() = runTest {
        testEndpoint("unstar.view") { unstar("abc") }
    }

    @Test
    fun testGetBookmarks() = runTest {
        testEndpoint("getBookmarks.view") {
            getBookmarks()
        }
    }

    @Test
    fun testBookmark() = runTest {
        testEndpoint("createBookmark.view") {
            createBookmark("abc", position = 0)
        }

        testEndpoint("deleteBookmark.view") {
            deleteBookmark("abc")
        }
    }
}