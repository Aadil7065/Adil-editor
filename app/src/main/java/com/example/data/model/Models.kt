package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AspectRatio(val label: String, val widthRatio: Float, val heightRatio: Float) {
    RATIO_16_9("16:9", 16f, 9f),
    RATIO_9_16("9:16 (Shorts/Reels)", 9f, 16f),
    RATIO_1_1("1:1 (Square)", 1f, 1f),
    RATIO_4_5("4:5 (Portrait)", 4f, 5f)
}

enum class Resolution(val label: String, val width: Int, val height: Int) {
    RES_720P("720p HD", 1280, 720),
    RES_1080P("1080p Full HD", 1920, 1080),
    RES_4K("4K Ultra HD", 3840, 2160)
}

enum class VideoFilter(val displayName: String, val colorMatrixDescription: String) {
    NONE("Original", "Natural colors"),
    CINEMATIC("Cinematic", "Teal shadows & warm orange highlights"),
    CYBERPUNK("Cyberpunk", "Electric neon magenta and cyan contrast"),
    NOIR("Film Noir", "High contrast dramatic monochrome"),
    VIBRANT("Vibrant HDR", "Saturated punchy rich colors"),
    WARM("Golden Hour", "Soft warm sunset glow"),
    RETRO("Retro 90s", "Grainy VHS nostalgia with chromatic aberration"),
    GLITCH("Acid Glitch", "RGB shift and digital distortion")
}

enum class TransitionType(val displayName: String) {
    NONE("None"),
    CROSSFADE("Crossfade"),
    GLITCH_WARP("Glitch Warp"),
    ZOOM_SPIN("Zoom Spin"),
    DIP_TO_BLACK("Dip to Black"),
    CYBER_SLIDE("Cyber Slide"),
    NEON_WHIP("Neon Whip")
}

enum class CaptionStyle(val displayName: String, val has3DEffect: Boolean) {
    NEON_3D("3D Neon Glow", true),
    BOLD_IMPACT("Bold Impact", false),
    KARAOKE_POP("Karaoke Pop", false),
    MINIMAL_GLASS("Minimal Glass", false),
    CYBER_OUTLINE("Cyberpunk Extrude", true)
}

enum class PlanType(val title: String, val priceInr: Int, val dailyExportLimit: Int, val durationText: String = "Monthly") {
    FREE("Free Plan", 0, 2, "Forever"),
    PRO_MONTHLY("Adil Pro Monthly", 50, 40, "1 Month"),
    PRO_QUARTERLY("Adil VIP 3 Months", 109, 100, "3 Months (90 Days)"),
    ADMIN("Admin Plan", 0, 9999, "Lifetime")
}

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val durationMs: Long = 95000L, // default ~1m 35s
    val aspectRatio: AspectRatio = AspectRatio.RATIO_16_9,
    val resolution: Resolution = Resolution.RES_1080P,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val thumbnailKey: String = "ref_editor_preview",
    val exportCount: Int = 0
)

@Entity(tableName = "video_clips")
data class VideoClip(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val startMs: Long,
    val durationMs: Long,
    val sourceDurationMs: Long,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val filter: VideoFilter = VideoFilter.NONE,
    val transitionIn: TransitionType = TransitionType.NONE,
    val rotationDeg: Float = 0f,
    val hueAccent: Long = 0xFF8B5CF6,
    val visualPrompt: String = "Spaceship interior corridor with neon lighting"
)

@Entity(tableName = "audio_tracks")
data class AudioTrackItem(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val startMs: Long,
    val durationMs: Long,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false,
    val isVoiceOver: Boolean = false,
    val noiseReduction: Boolean = true
)

@Entity(tableName = "broll_items")
data class BrollItem(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val category: String, // "Sci-Fi", "Tech", "Cyberpunk", "Cinematic", "Urban"
    val startMs: Long,
    val durationMs: Long,
    val prompt: String = "",
    val layerIndex: Int = 1 // 1 for AI Layer 1, 2 for AI Layer 2
)

@Entity(tableName = "caption_items")
data class CaptionItem(
    @PrimaryKey val id: String,
    val projectId: String,
    val text: String,
    val startMs: Long,
    val durationMs: Long,
    val style: CaptionStyle = CaptionStyle.NEON_3D,
    val highlightWord: String = ""
)

data class VoiceCommandResult(
    val transcript: String,
    val detectedIntent: String,
    val proposedChanges: List<String>,
    val executableAction: String,
    val isDestructive: Boolean = false,
    val confidence: Float = 0.95f
)

enum class ExportStatus {
    IDLE, PREPARING, PROCESSING_FRAMES, MIXING_AUDIO, ENCODING_H264, COMPLETED, CANCELLED, FAILED
}

data class ExportJob(
    val id: String,
    val projectId: String,
    val projectName: String,
    val status: ExportStatus = ExportStatus.IDLE,
    val progress: Float = 0f,
    val currentStepText: String = "",
    val resolution: Resolution = Resolution.RES_1080P,
    val fps: Int = 60,
    val outputSizeBytes: Long = 0L,
    val outputPath: String = ""
)

data class SubscriptionState(
    val planType: PlanType = PlanType.PRO_MONTHLY,
    val exportsUsedToday: Int = 1,
    val maxExportsDaily: Int = 40,
    val priceInr: Int = 50,
    val renewalDateText: String = "Dec 03, 2026",
    val isActive: Boolean = true,
    val isAdmin: Boolean = false
)

data class WithdrawalRecord(
    val id: String,
    val amountInr: Double,
    val method: String, // "UPI" or "PAYTM"
    val targetAccount: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED"
)

data class AdminMetrics(
    val totalUsers: Int = 14820,
    val proUsers: Int = 6410,
    val freeUsers: Int = 8410,
    val exportsToday: Int = 2845,
    val storageGb: Float = 418.5f,
    val aiTokensUsedK: Float = 182.4f,
    val activeSubscriptionPriceInr: Int = 50,
    val adminWalletBalanceInr: Double = 34850.0,
    val totalSubscriptionRevenueInr: Double = 68770.0,
    val withdrawnRevenueInr: Double = 33920.0,
    val systemStatusText: String = "All AI & Rendering Engines Operational",
    val recentWithdrawals: List<WithdrawalRecord> = listOf(
        WithdrawalRecord("w1", 12500.0, "UPI", "adil@oksbi", System.currentTimeMillis() - 86400000L * 2, "COMPLETED"),
        WithdrawalRecord("w2", 8400.0, "PAYTM", "+91 9876543210", System.currentTimeMillis() - 86400000L * 5, "COMPLETED"),
        WithdrawalRecord("w3", 13020.0, "UPI", "adad88569@upi", System.currentTimeMillis() - 86400000L * 10, "COMPLETED")
    ),
    val recentLogs: List<String> = listOf(
        "[INFO 18:42] AI Voice Engine parsed 410 instructions with 98.4% accuracy",
        "[INFO 18:45] Cloud H.264 pipeline rendered 142 jobs at average 2.4s latency",
        "[PAYOUT 18:49] ₹12,500 transferred to Admin UPI (adil@oksbi) successfully",
        "[SECURITY 18:50] Firebase App Check verified 1,290 client sessions",
        "[PAYMENT 18:52] ₹109 3-Months VIP subscription processed for user #8410"
    )
)

data class AppUser(
    val id: String,
    val name: String,
    val email: String,
    val plan: PlanType,
    val exportsToday: Int,
    val isBlocked: Boolean = false,
    val allocatedLimit: Int = 40,
    val joinedDate: String = "Nov 2026",
    val assignedKeyCode: String? = null
)

data class PromptScene(
    val sceneNumber: Int,
    val heading: String,
    val actionScript: String,
    val dialogue: String,
    val characterName: String,
    val estimatedDurationMs: Long = 12000L,
    val visualStyle: String = "ADIL Editor City"
)

data class ActivationKey(
    val code: String,
    val planType: PlanType,
    val dailyExportLimit: Int,
    val label: String,
    val isUsed: Boolean = false,
    val createdBy: String = "Admin",
    val usedBy: String? = null,
    val expiryDuration: String = "Lifetime",
    val createdAt: Long = System.currentTimeMillis()
)

data class LicenseKey(
    val key: String,
    val planType: PlanType,
    val expiryDate: String,
    val expiresAt: Long,
    val dailyExportLimit: Int = planType.dailyExportLimit,
    val label: String = "",
    val isUsed: Boolean = false,
    val usedBy: String? = null,
    val usedAt: Long? = null,
    val createdBy: String = "Admin",
    val createdAt: Long = System.currentTimeMillis()
) {
    val isExpired: Boolean
        get() = expiresAt in 1..System.currentTimeMillis()

    val statusText: String
        get() = when {
            isUsed -> "REDEEMED"
            isExpired -> "EXPIRED"
            else -> "ACTIVE"
        }
}

