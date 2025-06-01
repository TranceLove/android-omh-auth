package com.openmobilehub.android.auth.plugin.microsoft.mobileweb.data.user.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences.getEncryptedSharedPrefs
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.utils.Constants
import com.openmobilehub.android.auth.plugin.microsoft.mobileweb.utils.Constants.PROVIDER_MICROSOFT
import com.openmobilehub.android.auth.test.FakeAndroidKeyStoreProvider

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MicrosoftUserDataSourceTest {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        @BeforeClass
        @JvmStatic
        fun bootstrap() {
            FakeAndroidKeyStoreProvider.setup()
        }
    }

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        sharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_MICROSOFT)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun testParseIdToken() {
        val token = String(javaClass.classLoader.getResourceAsStream("idtoken.txt").readBytes())
        runBlocking {
            launch {
                MicrosoftUserDataSource(sharedPreferences).handleIdToken(
                    token,
                    "12345678-90ab-cdef-1234-567890abcdef",
                )
                assertEquals(
                    "Abe",
                    sharedPreferences.getString(Constants.NAME_KEY, null),
                )
                assertEquals(
                    "Lincoln",
                    sharedPreferences.getString(Constants.SURNAME_KEY, null),
                )
                assertEquals(
                    "AbeLi@test.com",
                    sharedPreferences.getString(Constants.EMAIL_KEY, null),
                )
                assertEquals(
                    "00000000-0000-0000-0000-000000000001",
                    sharedPreferences.getString(Constants.ID_KEY, null),
                )
                assertEquals(
                    "https://graph.microsoft.com/v1.0/me/photo/\$value",
                    sharedPreferences.getString(Constants.PICTURE_KEY, null),
                )
            }
        }
    }
}
