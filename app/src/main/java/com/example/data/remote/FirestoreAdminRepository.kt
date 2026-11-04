package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.data.model.ActivationKey
import com.example.data.model.AppUser
import com.example.data.model.PlanType
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreAdminRepository(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    // MANDATORY: Pass named database ID from firebase_applet_config.xml
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(
            context.getString(R.string.firestore_database_id)
        )
    }

    private val usersCollection = firestore.collection("users")
    private val keysCollection = firestore.collection("activation_keys")

    init {
        scope.launch {
            seedInitialFirestoreDataIfEmpty()
        }
    }

    private suspend fun seedInitialFirestoreDataIfEmpty() {
        try {
            val userSnap = usersCollection.limit(1).get().await()
            if (userSnap.isEmpty) {
                val initialUsers = listOf(
                    AppUser("u1", "Adil Hussain (Owner)", "adil@editor.ai", PlanType.ADMIN, 14, false, 9999, "Oct 2026", "ADIL-MASTER-2026"),
                    AppUser("u2", "Zara Vance", "zara.v@studio.com", PlanType.PRO_MONTHLY, 8, false, 40, "Nov 2026", null),
                    AppUser("u3", "Marcus Aurel", "marcus@creator.io", PlanType.FREE, 2, false, 2, "Nov 2026", null),
                    AppUser("u4", "Priya Sharma", "priya.s@filmmaker.in", PlanType.PRO_MONTHLY, 12, false, 50, "Nov 2026", "PRO-CREATOR-50"),
                    AppUser("u5", "Alex Chen", "chen.alex@vfx.net", PlanType.PRO_MONTHLY, 21, false, 100, "Nov 2026", "VIP-BOOST-100"),
                    AppUser("u6", "Kael Robotics", "kael@synth.ai", PlanType.FREE, 1, false, 2, "Nov 2026", null)
                )

                for (u in initialUsers) {
                    usersCollection.document(u.id).set(
                        mapOf(
                            "id" to u.id,
                            "name" to u.name,
                            "email" to u.email,
                            "plan" to u.plan.name,
                            "exportsToday" to u.exportsToday,
                            "allocatedLimit" to u.allocatedLimit,
                            "isBlocked" to u.isBlocked,
                            "joinedDate" to u.joinedDate,
                            "assignedKeyCode" to u.assignedKeyCode,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }

            val keySnap = keysCollection.limit(1).get().await()
            if (keySnap.isEmpty) {
                val defaultKeys = listOf(
                    ActivationKey("ADIL-MASTER-2026", PlanType.ADMIN, 9999, "Developer Master Key (Full Admin & Unlimited)", false, "Admin", null, "Lifetime"),
                    ActivationKey("ADIL-DEV-KEY", PlanType.ADMIN, 9999, "App Developer Key", false, "Admin", null, "Lifetime"),
                    ActivationKey("VIP-BOOST-100", PlanType.PRO_MONTHLY, 100, "VIP 100 Daily Exports Pass", false, "Admin", null, "1 Year"),
                    ActivationKey("PRO-CREATOR-50", PlanType.PRO_MONTHLY, 50, "Adil Pro 50 Daily Exports", false, "Admin", null, "30 Days")
                )

                for (k in defaultKeys) {
                    keysCollection.document(k.code).set(
                        mapOf(
                            "code" to k.code,
                            "planType" to k.planType.name,
                            "dailyExportLimit" to k.dailyExportLimit,
                            "label" to k.label,
                            "isUsed" to k.isUsed,
                            "createdBy" to k.createdBy,
                            "usedBy" to k.usedBy,
                            "expiryDuration" to k.expiryDuration,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            }
        } catch (_: Exception) {
            // Emulators or local fallbacks handled gracefully
        }
    }

    // Realtime observe registered users from Firestore
    fun observeUsers(): Flow<List<AppUser>> = usersCollection
        .snapshots()
        .map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.getString("id") ?: doc.id
                    val name = doc.getString("name") ?: "Unknown User"
                    val email = doc.getString("email") ?: ""
                    val planStr = doc.getString("plan") ?: "FREE"
                    val plan = try { PlanType.valueOf(planStr) } catch (_: Exception) { PlanType.FREE }
                    val exportsToday = (doc.get("exportsToday") as? Number)?.toInt() ?: 0
                    val allocatedLimit = (doc.get("allocatedLimit") as? Number)?.toInt() ?: 40
                    val isBlocked = doc.getBoolean("isBlocked") ?: false
                    val joinedDate = doc.getString("joinedDate") ?: "Nov 2026"
                    val assignedKeyCode = doc.getString("assignedKeyCode")
                    AppUser(id, name, email, plan, exportsToday, isBlocked, allocatedLimit, joinedDate, assignedKeyCode)
                } catch (_: Exception) {
                    null
                }
            }
        }
        .catch {
            // Emit fallback seed on error
            emit(
                listOf(
                    AppUser("u1", "Adil Hussain (Owner)", "adil@editor.ai", PlanType.ADMIN, 14, false, 9999, "Oct 2026", "ADIL-MASTER-2026"),
                    AppUser("u2", "Zara Vance", "zara.v@studio.com", PlanType.PRO_MONTHLY, 8, false, 40, "Nov 2026", null),
                    AppUser("u3", "Marcus Aurel", "marcus@creator.io", PlanType.FREE, 2, false, 2, "Nov 2026", null),
                    AppUser("u4", "Priya Sharma", "priya.s@filmmaker.in", PlanType.PRO_MONTHLY, 12, false, 50, "Nov 2026", "PRO-CREATOR-50"),
                    AppUser("u5", "Alex Chen", "chen.alex@vfx.net", PlanType.PRO_MONTHLY, 21, false, 100, "Nov 2026", "VIP-BOOST-100"),
                    AppUser("u6", "Kael Robotics", "kael@synth.ai", PlanType.FREE, 1, false, 2, "Nov 2026", null)
                )
            )
        }

    // Realtime observe custom license keys from Firestore
    fun observeActivationKeys(): Flow<List<ActivationKey>> = keysCollection
        .snapshots()
        .map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val code = doc.getString("code") ?: doc.id
                    val planStr = doc.getString("planType") ?: "PRO_MONTHLY"
                    val planType = try { PlanType.valueOf(planStr) } catch (_: Exception) { PlanType.PRO_MONTHLY }
                    val limit = (doc.get("dailyExportLimit") as? Number)?.toInt() ?: 40
                    val label = doc.getString("label") ?: "Custom Key"
                    val isUsed = doc.getBoolean("isUsed") ?: false
                    val createdBy = doc.getString("createdBy") ?: "Admin"
                    val usedBy = doc.getString("usedBy")
                    val expiryDuration = doc.getString("expiryDuration") ?: "Lifetime"
                    ActivationKey(code, planType, limit, label, isUsed, createdBy, usedBy, expiryDuration)
                } catch (_: Exception) {
                    null
                }
            }
        }
        .catch {
            emit(
                listOf(
                    ActivationKey("ADIL-MASTER-2026", PlanType.ADMIN, 9999, "Developer Master Key (Full Admin & Unlimited)", false, "Admin", null, "Lifetime"),
                    ActivationKey("ADIL-DEV-KEY", PlanType.ADMIN, 9999, "App Developer Key", false, "Admin", null, "Lifetime"),
                    ActivationKey("VIP-BOOST-100", PlanType.PRO_MONTHLY, 100, "VIP 100 Daily Exports Pass", false, "Admin", null, "1 Year"),
                    ActivationKey("PRO-CREATOR-50", PlanType.PRO_MONTHLY, 50, "Adil Pro 50 Daily Exports", false, "Admin", null, "30 Days")
                )
            )
        }

    // Save key to Firestore
    suspend fun saveKey(key: ActivationKey) {
        try {
            keysCollection.document(key.code).set(
                mapOf(
                    "code" to key.code,
                    "planType" to key.planType.name,
                    "dailyExportLimit" to key.dailyExportLimit,
                    "label" to key.label,
                    "isUsed" to key.isUsed,
                    "createdBy" to key.createdBy,
                    "usedBy" to key.usedBy,
                    "expiryDuration" to key.expiryDuration,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (_: Exception) {}
    }

    // Delete key from Firestore
    suspend fun deleteKey(code: String) {
        try {
            keysCollection.document(code).delete().await()
        } catch (_: Exception) {}
    }

    // Update user in Firestore
    suspend fun updateUser(user: AppUser) {
        try {
            usersCollection.document(user.id).update(
                mapOf(
                    "plan" to user.plan.name,
                    "allocatedLimit" to user.allocatedLimit,
                    "isBlocked" to user.isBlocked,
                    "assignedKeyCode" to user.assignedKeyCode,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (_: Exception) {}
    }

    // Redeem key in Firestore
    suspend fun redeemKey(code: String): Pair<Boolean, String> {
        return try {
            val doc = keysCollection.document(code.uppercase().trim()).get().await()
            if (!doc.exists()) {
                Pair(false, "Invalid key code in Firestore.")
            } else {
                val isUsed = doc.getBoolean("isUsed") ?: false
                if (isUsed && code != "ADIL-MASTER-2026" && code != "ADIL-DEV-KEY") {
                    Pair(false, "This key has already been redeemed.")
                } else {
                    if (code != "ADIL-MASTER-2026" && code != "ADIL-DEV-KEY") {
                        keysCollection.document(code).update("isUsed", true, "usedBy", "Current User").await()
                    }
                    val limit = (doc.get("dailyExportLimit") as? Number)?.toInt() ?: 40
                    val planStr = doc.getString("planType") ?: "PRO_MONTHLY"
                    Pair(true, "Key redeemed from Firestore! Plan: $planStr with $limit daily exports.")
                }
            }
        } catch (e: Exception) {
            Pair(false, "Error: ${e.message}")
        }
    }
}
