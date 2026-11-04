package com.example

import com.example.data.model.ActivationKey
import com.example.data.model.PlanType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun activationKey_validation() {
    val key = ActivationKey(
      code = "ADIL-MASTER-2026",
      planType = PlanType.ADMIN,
      dailyExportLimit = 9999,
      label = "Developer Master Key"
    )
    assertEquals("ADIL-MASTER-2026", key.code)
    assertEquals(PlanType.ADMIN, key.planType)
    assertEquals(9999, key.dailyExportLimit)
  }

  @Test
  fun customKey_allocation() {
    val key = ActivationKey(
      code = "VIP-500",
      planType = PlanType.PRO_MONTHLY,
      dailyExportLimit = 500,
      label = "VIP Pass"
    )
    assertEquals(500, key.dailyExportLimit)
  }

  @Test
  fun licenseKey_alphanumeric_and_expiry_validation() {
    val key = com.example.data.model.LicenseKey(
      key = "ADIL-PRO-8K9M-2X7A",
      planType = PlanType.PRO_MONTHLY,
      expiryDate = "2026-12-03",
      expiresAt = System.currentTimeMillis() + 86400000L * 30,
      dailyExportLimit = 40,
      label = "Adil Pro Monthly"
    )

    assertEquals("ADIL-PRO-8K9M-2X7A", key.key)
    assertEquals(PlanType.PRO_MONTHLY, key.planType)
    assertEquals(40, key.dailyExportLimit)
    assertEquals("ACTIVE", key.statusText)
    assertTrue(key.key.matches(Regex("^[A-Z0-9-]+$")))
  }

  @Test
  fun licenseKey_all_plans_supported() {
    val plans = listOf(PlanType.FREE, PlanType.PRO_MONTHLY, PlanType.ADMIN)
    assertEquals(3, plans.size)
    assertEquals("Free Plan", PlanType.FREE.title)
    assertEquals("Adil Pro Monthly", PlanType.PRO_MONTHLY.title)
    assertEquals("Admin Plan", PlanType.ADMIN.title)
  }
}

