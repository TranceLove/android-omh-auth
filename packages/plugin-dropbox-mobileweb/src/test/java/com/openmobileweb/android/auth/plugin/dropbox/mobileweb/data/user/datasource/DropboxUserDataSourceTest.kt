package com.openmobileweb.android.auth.plugin.dropbox.mobileweb.data.user.datasource

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openmobilehub.android.auth.core.utils.EncryptedSharedPreferences.getEncryptedSharedPrefs
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.data.user.datasource.DropboxUserDataSource
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.utils.Constants
import com.openmobilehub.android.auth.plugin.dropbox.mobileweb.utils.Constants.PROVIDER_DROPBOX
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import com.openmobilehub.android.auth.test.FakeAndroidKeyStoreProvider


@RunWith(AndroidJUnit4::class)
class DropboxUserDataSourceTest {

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
        sharedPreferences = getEncryptedSharedPrefs(context, PROVIDER_DROPBOX)
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
                DropboxUserDataSource(sharedPreferences).handleIdToken(
                    token,
                    "test dropbox app id",
                )
                assertEquals(
                    "John",
                    sharedPreferences.getString(Constants.NAME_KEY, null),
                )
                assertEquals(
                    "Foobar",
                    sharedPreferences.getString(Constants.SURNAME_KEY, null),
                )
                assertEquals(
                    "test@test.com",
                    sharedPreferences.getString(Constants.EMAIL_KEY, null),
                )
                assertEquals(
                    "dbid:test dropbox account id",
                    sharedPreferences.getString(Constants.ID_KEY, null),
                )
                assertNull(sharedPreferences.getString(Constants.PICTURE_KEY, null))
            }
        }
    }
}
