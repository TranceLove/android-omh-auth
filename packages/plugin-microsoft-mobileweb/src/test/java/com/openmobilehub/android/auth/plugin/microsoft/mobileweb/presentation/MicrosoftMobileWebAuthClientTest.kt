package com.openmobilehub.android.auth.plugin.microsoft.mobileweb.presentation

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openmobilehub.android.auth.core.common.mobileweb.presentation.redirect.RedirectActivity.Companion.CLIENT_ID
import com.openmobilehub.android.auth.core.common.mobileweb.presentation.redirect.RedirectActivity.Companion.SCOPES

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MicrosoftMobileWebAuthClientTest {

    @Test
    fun testDefaultScope() {
        val client = MicrosoftMobileWebAuthClient.Builder("123456")
            .build(ApplicationProvider.getApplicationContext())

        val intent = client.getLoginIntent()
        assertEquals("123456", intent.extras?.getString(CLIENT_ID))
        assertEquals("offline_access profile", intent.extras?.getString(SCOPES))
    }

    @Test
    fun testAddCustomScope() {
        var client = MicrosoftMobileWebAuthClient.Builder("123456")
            .addScope("Files.ReadWrite")
            .build(ApplicationProvider.getApplicationContext())

        var intent = client.getLoginIntent()
        assertEquals("123456", intent.extras?.getString(CLIENT_ID))
        assertEquals("offline_access profile Files.ReadWrite", intent.extras?.getString(SCOPES))

        client = MicrosoftMobileWebAuthClient.Builder("123456")
            .addScope("Files.ReadWrite")
            .addScope("User.Read")
            .build(ApplicationProvider.getApplicationContext())

        intent = client.getLoginIntent()
        assertEquals("123456", intent.extras?.getString(CLIENT_ID))
        assertEquals("offline_access profile Files.ReadWrite User.Read", intent.extras?.getString(SCOPES))
    }
}
