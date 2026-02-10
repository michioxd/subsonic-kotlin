package dev.zt64.subsonic.client.test

import dev.zt64.subsonic.api.Salt
import kotlin.test.Test
import kotlin.test.assertEquals

class SaltTest {
    @Test
    fun testGenerateSalt() {
        val salt = Salt.generateSalt()
        assertEquals(6, salt.length)
    }

    @Test
    fun testPassword() {
        val salt = "c19b2d"
        val expected = "dfe3effde948b7bfdcc0000cf742f0a8"

        val (saltedPassword) = Salt.saltPassword(PASSWORD, salt)
        assertEquals(expected, saltedPassword)
    }
}