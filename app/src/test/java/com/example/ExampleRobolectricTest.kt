package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AdrLocalDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ADR Levha", appName)
  }

  @Test
  fun `test kemler 33 decoding`() {
    val result = AdrLocalDatabase.decodeKemlerCode("33")
    assertEquals("33", result.code)
    assertEquals(false, result.isWaterReactive)
    assertTrue(result.fullDescription.contains("alevlenir", ignoreCase = true))
  }

  @Test
  fun `test water reactive X338 decoding`() {
    val result = AdrLocalDatabase.decodeKemlerCode("X338")
    assertEquals("X338", result.code)
    assertEquals(true, result.isWaterReactive)
  }

  @Test
  fun `test UN 1203 substance lookup`() {
    val substance = AdrLocalDatabase.findSubstanceByUn("1203")
    assertNotNull(substance)
    assertTrue(substance!!.nameTr.contains("BENZİN"))
    assertEquals("Sınıf 3", substance.adrClass)
  }
}
