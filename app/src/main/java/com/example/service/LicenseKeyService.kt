package com.example.service

import android.content.Context
import com.example.R
import com.example.data.model.LicenseKey
import com.example.data.model.PlanType
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Service to generate alphanumeric license keys with associated expiry dates
 * and plan types (Free, Pro, Admin), persisting them to the 'licenseKeys' Firestore collection.
 */
class LicenseKeyService(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // Pass named database ID from firebase_applet_config.xml
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(
            context.getString(R.string.firestore_database_id)
        )
    }

    // Explicitly targeting the required 'licenseKeys' collection
    private val licenseKeysCollection by lazy {
        firestore.collection("licenseKeys")
    }

    private val secureRandom = SecureRandom()
    private val charPool: List<Char> = ('A'..'Z') + ('0'..'9')

    init {
        scope.launch {
            seedDefaultLicenseKeysIfEmpty()
        }
    }

    /**
     * Generates a high-entropy alphanumeric key string.
     * Example outputs:
     * - Free: ADIL-FRE-8K2M-9P4X
     * - Pro: ADIL-PRO-7N3Y-2W8Z
     * - Admin: ADIL-ADM-5Q1R-4T9V
     */
    fun generateAlphanumericKey(planType: PlanType, prefix: String = "ADIL"): String {
        val planTag = when (planType) {
            PlanType.FREE -> "FRE"
            PlanType.PRO_MONTHLY -> "PRO"
            PlanType.PRO_QUARTERLY -> "VIP"
            PlanType.ADMIN -> "ADM"
        }

        fun randomBlock(length: Int): String {
            return (1..length)
                .map { secureRandom.nextInt(charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }

        val part1 = randomBlock(4)
        val part2 = randomBlock(4)
        return "$prefix-$planTag-$part1-$part2"
    }

    /**
     * Calculates the expiry date string and timestamp in milliseconds based on validity days.
     * Passing -1 or 0 indicates Lifetime (Never expires).
     */
    fun calculateExpiry(validityDays: Int): Pair<String, Long> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return if (validityDays <= 0) {
            Pair("Lifetime (Never)", Long.MAX_VALUE)
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, validityDays)
            val expiryDate = calendar.time
            Pair(dateFormat.format(expiryDate), calendar.timeInMillis)
        }
    }

    /**
     * Generates and writes a new LicenseKey into the 'licenseKeys' Firestore collection.
     */
    suspend fun createLicenseKey(
        planType: PlanType,
        validityDays: Int = 30,
        customKeyString: String? = null,
        customLimit: Int? = null,
        label: String = "",
        createdBy: String = "Admin"
    ): Result<LicenseKey> {
        return try {
            val keyString = customKeyString?.trim()?.uppercase()
                ?.takeIf { it.isNotEmpty() }
                ?: generateAlphanumericKey(planType)

            val (expiryDateStr, expiresAtMs) = calculateExpiry(validityDays)
            val exportLimit = customLimit ?: when (planType) {
                PlanType.FREE -> 2
                PlanType.PRO_MONTHLY -> 40
                PlanType.PRO_QUARTERLY -> 100
                PlanType.ADMIN -> 9999
            }

            val licenseKey = LicenseKey(
                key = keyString,
                planType = planType,
                expiryDate = expiryDateStr,
                expiresAt = expiresAtMs,
                dailyExportLimit = exportLimit,
                label = label.ifBlank { "${planType.title} ($expiryDateStr)" },
                isUsed = false,
                usedBy = null,
                usedAt = null,
                createdBy = createdBy,
                createdAt = System.currentTimeMillis()
            )

            licenseKeysCollection.document(keyString).set(
                mapOf(
                    "key" to licenseKey.key,
                    "planType" to licenseKey.planType.name,
                    "dailyExportLimit" to licenseKey.dailyExportLimit,
                    "expiryDate" to licenseKey.expiryDate,
                    "expiresAt" to licenseKey.expiresAt,
                    "label" to licenseKey.label,
                    "isUsed" to licenseKey.isUsed,
                    "createdBy" to licenseKey.createdBy,
                    "usedBy" to licenseKey.usedBy,
                    "status" to licenseKey.statusText,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(licenseKey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Realtime snapshot flow observing the 'licenseKeys' collection.
     */
    fun observeLicenseKeys(): Flow<List<LicenseKey>> = licenseKeysCollection
        .snapshots()
        .map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val key = doc.getString("key") ?: doc.id
                    val planStr = doc.getString("planType") ?: "PRO_MONTHLY"
                    val planType = try {
                        PlanType.valueOf(planStr)
                    } catch (_: Exception) {
                        PlanType.PRO_MONTHLY
                    }
                    val limit = (doc.get("dailyExportLimit") as? Number)?.toInt() ?: planType.dailyExportLimit
                    val expiryDate = doc.getString("expiryDate") ?: "Lifetime (Never)"
                    val expiresAt = (doc.get("expiresAt") as? Number)?.toLong() ?: Long.MAX_VALUE
                    val label = doc.getString("label") ?: ""
                    val isUsed = doc.getBoolean("isUsed") ?: false
                    val usedBy = doc.getString("usedBy")
                    val usedAt = (doc.get("usedAt") as? Number)?.toLong()
                    val createdBy = doc.getString("createdBy") ?: "Admin"
                    val createdAt = (doc.get("createdAt") as? Number)?.toLong() ?: System.currentTimeMillis()

                    LicenseKey(
                        key = key,
                        planType = planType,
                        expiryDate = expiryDate,
                        expiresAt = expiresAt,
                        dailyExportLimit = limit,
                        label = label,
                        isUsed = isUsed,
                        usedBy = usedBy,
                        usedAt = usedAt,
                        createdBy = createdBy,
                        createdAt = createdAt
                    )
                } catch (_: Exception) {
                    null
                }
            }
        }
        .catch {
            // Provide sensible fallback seeds if offline
            emit(getFallbackLicenseKeys())
        }

    /**
     * Validates and redeems a license key from the 'licenseKeys' collection.
     */
    suspend fun redeemLicenseKey(keyInput: String, userId: String = "User"): Result<LicenseKey> {
        val cleanKey = keyInput.trim().uppercase()
        return try {
            val docRef = licenseKeysCollection.document(cleanKey)
            val snap = docRef.get().await()

            if (!snap.exists()) {
                // Check if it's one of the built-in developer master keys
                if (cleanKey == "ADIL-MASTER-2026" || cleanKey == "ADIL-DEV-KEY") {
                    val masterKey = LicenseKey(
                        key = cleanKey,
                        planType = PlanType.ADMIN,
                        expiryDate = "Lifetime (Never)",
                        expiresAt = Long.MAX_VALUE,
                        dailyExportLimit = 9999,
                        label = "Developer Master Key",
                        isUsed = false,
                        createdBy = "System"
                    )
                    return Result.success(masterKey)
                }
                return Result.failure(Exception("License key '$cleanKey' was not found in 'licenseKeys' collection."))
            }

            val isUsed = snap.getBoolean("isUsed") ?: false
            val expiresAt = (snap.get("expiresAt") as? Number)?.toLong() ?: Long.MAX_VALUE
            val planStr = snap.getString("planType") ?: "PRO_MONTHLY"
            val planType = try { PlanType.valueOf(planStr) } catch (_: Exception) { PlanType.PRO_MONTHLY }
            val limit = (snap.get("dailyExportLimit") as? Number)?.toInt() ?: planType.dailyExportLimit
            val expiryDate = snap.getString("expiryDate") ?: "Lifetime (Never)"

            if (isUsed && cleanKey != "ADIL-MASTER-2026" && cleanKey != "ADIL-DEV-KEY") {
                return Result.failure(Exception("This license key has already been redeemed."))
            }

            if (expiresAt in 1..System.currentTimeMillis()) {
                return Result.failure(Exception("This license key expired on $expiryDate."))
            }

            // Mark key as redeemed in Firestore
            docRef.update(
                mapOf(
                    "isUsed" to true,
                    "usedBy" to userId,
                    "usedAt" to System.currentTimeMillis(),
                    "status" to "REDEEMED",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            val redeemed = LicenseKey(
                key = cleanKey,
                planType = planType,
                expiryDate = expiryDate,
                expiresAt = expiresAt,
                dailyExportLimit = limit,
                isUsed = true,
                usedBy = userId
            )
            Result.success(redeemed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a license key from the 'licenseKeys' collection.
     */
    suspend fun deleteLicenseKey(key: String): Result<Unit> {
        return try {
            licenseKeysCollection.document(key.trim().uppercase()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun seedDefaultLicenseKeysIfEmpty() {
        try {
            val snap = licenseKeysCollection.limit(1).get().await()
            if (snap.isEmpty) {
                val seedKeys = getFallbackLicenseKeys()
                for (k in seedKeys) {
                    licenseKeysCollection.document(k.key).set(
                        mapOf(
                            "key" to k.key,
                            "planType" to k.planType.name,
                            "dailyExportLimit" to k.dailyExportLimit,
                            "expiryDate" to k.expiryDate,
                            "expiresAt" to k.expiresAt,
                            "label" to k.label,
                            "isUsed" to k.isUsed,
                            "createdBy" to k.createdBy,
                            "usedBy" to k.usedBy,
                            "status" to k.statusText,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (_: Exception) {}
    }

    private fun getFallbackLicenseKeys(): List<LicenseKey> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal30 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 30) }
        val cal365 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 365) }

        return listOf(
            LicenseKey(
                key = "ADIL-ADM-9X2K-4M8P",
                planType = PlanType.ADMIN,
                expiryDate = "Lifetime (Never)",
                expiresAt = Long.MAX_VALUE,
                dailyExportLimit = 9999,
                label = "Admin Developer Pass (Unlimited)",
                isUsed = false,
                createdBy = "Admin"
            ),
            LicenseKey(
                key = "ADIL-PRO-7B1N-3Q6Y",
                planType = PlanType.PRO_MONTHLY,
                expiryDate = dateFormat.format(cal30.time),
                expiresAt = cal30.timeInMillis,
                dailyExportLimit = 40,
                label = "Adil Pro Monthly (30 Days)",
                isUsed = false,
                createdBy = "Admin"
            ),
            LicenseKey(
                key = "ADIL-PRO-8W4T-2Z9K",
                planType = PlanType.PRO_MONTHLY,
                expiryDate = dateFormat.format(cal365.time),
                expiresAt = cal365.timeInMillis,
                dailyExportLimit = 100,
                label = "VIP Pro Annual (100 Daily Exports)",
                isUsed = false,
                createdBy = "Admin"
            ),
            LicenseKey(
                key = "ADIL-FRE-5C9L-1V3X",
                planType = PlanType.FREE,
                expiryDate = dateFormat.format(cal30.time),
                expiresAt = cal30.timeInMillis,
                dailyExportLimit = 2,
                label = "Free Starter Pass",
                isUsed = false,
                createdBy = "Admin"
            )
        )
    }
}
