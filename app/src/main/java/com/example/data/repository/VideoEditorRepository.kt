package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.model.ActivationKey
import com.example.data.model.LicenseKey
import com.example.data.model.AppUser
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrackItem
import com.example.data.model.BrollItem
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.PlanType
import com.example.data.model.Project
import com.example.data.model.Resolution
import com.example.data.model.SubscriptionState
import com.example.data.model.TransitionType
import com.example.data.model.VideoClip
import com.example.data.model.VideoFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class VideoEditorRepository(
    private val projectDao: ProjectDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    val allProjects: Flow<List<Project>> = projectDao.getAllProjects()

    private val _subscriptionState = MutableStateFlow(
        SubscriptionState(
            planType = PlanType.PRO_MONTHLY,
            exportsUsedToday = 2,
            maxExportsDaily = 40,
            priceInr = 50,
            renewalDateText = "Dec 03, 2026",
            isActive = true,
            isAdmin = false
        )
    )
    val subscriptionState = _subscriptionState.asStateFlow()

    private val _adminUsers = MutableStateFlow(
        listOf(
            AppUser("u1", "Adil Hussain (Owner)", "adil@editor.ai", PlanType.ADMIN, 14, false),
            AppUser("u2", "Zara Vance", "zara.v@studio.com", PlanType.PRO_MONTHLY, 8, false),
            AppUser("u3", "Marcus Aurel", "marcus@creator.io", PlanType.FREE, 2, false),
            AppUser("u4", "Priya Sharma", "priya.s@filmmaker.in", PlanType.PRO_MONTHLY, 12, false),
            AppUser("u5", "Alex Chen", "chen.alex@vfx.net", PlanType.PRO_MONTHLY, 21, false),
            AppUser("u6", "Kael Robotics", "kael@synth.ai", PlanType.FREE, 1, false)
        )
    )
    val adminUsers = _adminUsers.asStateFlow()

    init {
        scope.launch {
            seedDefaultProjectsIfEmpty()
        }
    }

    private suspend fun seedDefaultProjectsIfEmpty() {
        // Create initial default project mirroring the user reference UI (VeoStudio spaceship / neon corridor)
        val defaultProjectId = "proj_veo_spaceship"
        val existing = projectDao.getProjectById(defaultProjectId)
        if (existing == null) {
            val project1 = Project(
                id = defaultProjectId,
                name = "Spaceship Odyssey - Ep 1",
                description = "INT. SPACESHIP - DAY. Captain Zara in neon hyper-corridor",
                durationMs = 95000L, // 1m 35s matching screenshot timecode 01:35:30
                aspectRatio = AspectRatio.RATIO_16_9,
                resolution = Resolution.RES_1080P,
                thumbnailKey = "ref_editor_preview"
            )
            projectDao.insertProject(project1)

            // Clips
            val clips = listOf(
                VideoClip(
                    id = "clip_1",
                    projectId = defaultProjectId,
                    title = "Scene 1: Zara Cockpit",
                    startMs = 0L,
                    durationMs = 24000L,
                    sourceDurationMs = 24000L,
                    filter = VideoFilter.CYBERPUNK,
                    transitionIn = TransitionType.NONE,
                    hueAccent = 0xFF8B5CF6,
                    visualPrompt = "Captain Zara looking through navigation displays in purple neon cockpit"
                ),
                VideoClip(
                    id = "clip_2",
                    projectId = defaultProjectId,
                    title = "Scene 2: Neon Corridor",
                    startMs = 24000L,
                    durationMs = 38000L,
                    sourceDurationMs = 38000L,
                    filter = VideoFilter.CINEMATIC,
                    transitionIn = TransitionType.CROSSFADE,
                    hueAccent = 0xFF06B6D4,
                    visualPrompt = "Futuristic hyper-drive corridor bathed in magenta and cyan neon lighting"
                ),
                VideoClip(
                    id = "clip_3",
                    projectId = defaultProjectId,
                    title = "Scene 3: Kael Encounter",
                    startMs = 62000L,
                    durationMs = 33000L,
                    sourceDurationMs = 33000L,
                    filter = VideoFilter.WARM,
                    transitionIn = TransitionType.GLITCH_WARP,
                    hueAccent = 0xFFEC4899,
                    visualPrompt = "Robot companion and co-pilot preparing jump drive sequence"
                )
            )
            projectDao.insertClips(clips)

            // Audio track
            val audioTracks = listOf(
                AudioTrackItem(
                    id = "audio_bgm_1",
                    projectId = defaultProjectId,
                    title = "Synthwave Pulse 120BPM",
                    startMs = 0L,
                    durationMs = 95000L,
                    volume = 0.75f,
                    isVoiceOver = false
                ),
                AudioTrackItem(
                    id = "audio_vo_1",
                    projectId = defaultProjectId,
                    title = "Voice-Over: Zara Log",
                    startMs = 6000L,
                    durationMs = 45000L,
                    volume = 0.95f,
                    isVoiceOver = true
                )
            )
            projectDao.insertAudioTracks(audioTracks)

            // B-Roll / AI Layers (matches "AI Preview 1", "AI Layer 1", "AI Layer 2", "AI Layer 3" in screenshot)
            val brolls = listOf(
                BrollItem(
                    id = "broll_ai_preview_1",
                    projectId = defaultProjectId,
                    title = "AI Preview 1 (Spaceship Thrust)",
                    category = "Sci-Fi",
                    startMs = 26000L,
                    durationMs = 28000L,
                    layerIndex = 1
                ),
                BrollItem(
                    id = "broll_ai_layer_1",
                    projectId = defaultProjectId,
                    title = "AI Layer 1 (Warp Graphics)",
                    category = "VFX",
                    startMs = 28000L,
                    durationMs = 24000L,
                    layerIndex = 2
                ),
                BrollItem(
                    id = "broll_ai_layer_3",
                    projectId = defaultProjectId,
                    title = "AI Layer 3 (Cyber HUD)",
                    category = "Tech",
                    startMs = 52000L,
                    durationMs = 22000L,
                    layerIndex = 2
                ),
                BrollItem(
                    id = "broll_ai_layer_2",
                    projectId = defaultProjectId,
                    title = "AI Layer 2 (Energy Particles)",
                    category = "Cyberpunk",
                    startMs = 66000L,
                    durationMs = 25000L,
                    layerIndex = 1
                )
            )
            projectDao.insertBrollItems(brolls)

            // Captions (3D animated style)
            val captions = listOf(
                CaptionItem(
                    id = "cap_1",
                    projectId = defaultProjectId,
                    text = "CAPTAIN ZARA: JUMP DRIVE INITIATED",
                    startMs = 2000L,
                    durationMs = 12000L,
                    style = CaptionStyle.NEON_3D,
                    highlightWord = "JUMP"
                ),
                CaptionItem(
                    id = "cap_2",
                    projectId = defaultProjectId,
                    text = "SYSTEMS ONLINE. NAVIGATION LOCKED.",
                    startMs = 16000L,
                    durationMs = 14000L,
                    style = CaptionStyle.CYBER_OUTLINE,
                    highlightWord = "ONLINE"
                ),
                CaptionItem(
                    id = "cap_3",
                    projectId = defaultProjectId,
                    text = "PREPARE FOR HYPER-WARP TRANSITION",
                    startMs = 32000L,
                    durationMs = 18000L,
                    style = CaptionStyle.NEON_3D,
                    highlightWord = "HYPER-WARP"
                )
            )
            projectDao.insertCaptions(captions)

            // Second project: Neon Cyberpunk Reel (9:16 vertical)
            val project2 = Project(
                id = "proj_cyber_reel",
                name = "ADIL Editor City",
                description = "9:16 Vertical Reel with high-energy cuts and 3D glowing captions",
                durationMs = 30000L,
                aspectRatio = AspectRatio.RATIO_9_16,
                resolution = Resolution.RES_1080P,
                thumbnailKey = "ref_editor_preview"
            )
            projectDao.insertProject(project2)
        }
    }

    fun getClipsForProject(projectId: String): Flow<List<VideoClip>> =
        projectDao.getClipsForProject(projectId)

    fun getAudioForProject(projectId: String): Flow<List<AudioTrackItem>> =
        projectDao.getAudioForProject(projectId)

    fun getBrollForProject(projectId: String): Flow<List<BrollItem>> =
        projectDao.getBrollForProject(projectId)

    fun getCaptionsForProject(projectId: String): Flow<List<CaptionItem>> =
        projectDao.getCaptionsForProject(projectId)

    suspend fun getProjectById(projectId: String): Project? =
        projectDao.getProjectById(projectId)

    suspend fun createNewProject(
        name: String,
        aspectRatio: AspectRatio = AspectRatio.RATIO_16_9,
        resolution: Resolution = Resolution.RES_1080P
    ): Project {
        val newProj = Project(
            id = "proj_" + UUID.randomUUID().toString().take(8),
            name = name,
            aspectRatio = aspectRatio,
            resolution = resolution,
            durationMs = 60000L,
            lastModified = System.currentTimeMillis()
        )
        projectDao.insertProject(newProj)

        // Seed 2 default clips for instant editing
        val clip1 = VideoClip(
            id = UUID.randomUUID().toString(),
            projectId = newProj.id,
            title = "Scene 1 (Intro)",
            startMs = 0L,
            durationMs = 28000L,
            sourceDurationMs = 28000L,
            filter = VideoFilter.CYBERPUNK
        )
        val clip2 = VideoClip(
            id = UUID.randomUUID().toString(),
            projectId = newProj.id,
            title = "Scene 2 (Main Action)",
            startMs = 28000L,
            durationMs = 32000L,
            sourceDurationMs = 32000L,
            filter = VideoFilter.CINEMATIC
        )
        projectDao.insertClips(listOf(clip1, clip2))

        val audio = AudioTrackItem(
            id = UUID.randomUUID().toString(),
            projectId = newProj.id,
            title = "Cinematic Ambient Track",
            startMs = 0L,
            durationMs = 60000L,
            volume = 0.8f
        )
        projectDao.insertAudioTrack(audio)

        return newProj
    }

    suspend fun updateProject(project: Project) {
        projectDao.updateProject(project.copy(lastModified = System.currentTimeMillis()))
    }

    suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project)
    }

    // Clip operations
    suspend fun insertClip(clip: VideoClip) = projectDao.insertClip(clip)
    suspend fun updateClip(clip: VideoClip) = projectDao.updateClip(clip)
    suspend fun deleteClip(clipId: String) = projectDao.deleteClip(clipId)

    // Audio operations
    suspend fun insertAudioTrack(audio: AudioTrackItem) = projectDao.insertAudioTrack(audio)
    suspend fun deleteAudioTrack(trackId: String) = projectDao.deleteAudioTrack(trackId)

    // B-Roll operations
    suspend fun insertBrollItem(item: BrollItem) = projectDao.insertBrollItem(item)
    suspend fun deleteBrollItem(itemId: String) = projectDao.deleteBrollItem(itemId)

    // Caption operations
    suspend fun insertCaption(caption: CaptionItem) = projectDao.insertCaption(caption)
    suspend fun deleteCaption(captionId: String) = projectDao.deleteCaption(captionId)

    // Subscription & Usage Quota
    fun recordExportUsed(): Boolean {
        val current = _subscriptionState.value
        if (current.exportsUsedToday >= current.maxExportsDaily && current.planType != PlanType.ADMIN) {
            return false // Quota reached
        }
        _subscriptionState.value = current.copy(
            exportsUsedToday = current.exportsUsedToday + 1
        )
        return true
    }

    fun upgradePlan(planType: PlanType) {
        val current = _subscriptionState.value
        _subscriptionState.value = current.copy(
            planType = planType,
            maxExportsDaily = planType.dailyExportLimit,
            priceInr = planType.priceInr,
            isAdmin = planType == PlanType.ADMIN
        )
    }

    private val _activationKeys = MutableStateFlow(
        listOf(
            ActivationKey(
                code = "ADIL-MASTER-2026",
                planType = PlanType.ADMIN,
                dailyExportLimit = 9999,
                label = "Developer Master Key (Full Admin & Unlimited Exports)"
            ),
            ActivationKey(
                code = "ADIL-DEV-KEY",
                planType = PlanType.ADMIN,
                dailyExportLimit = 9999,
                label = "App Developer Key"
            ),
            ActivationKey(
                code = "VIP-BOOST-100",
                planType = PlanType.PRO_MONTHLY,
                dailyExportLimit = 100,
                label = "VIP 100 Daily Exports Pass"
            ),
            ActivationKey(
                code = "PRO-CREATOR-50",
                planType = PlanType.PRO_MONTHLY,
                dailyExportLimit = 50,
                label = "Adil Pro 50 Daily Exports"
            )
        )
    )
    val activationKeys = _activationKeys.asStateFlow()

    fun generateActivationKey(
        code: String,
        planType: PlanType,
        dailyExportLimit: Int,
        label: String
    ): ActivationKey {
        val newKey = ActivationKey(
            code = code.uppercase().trim(),
            planType = planType,
            dailyExportLimit = dailyExportLimit,
            label = label
        )
        _activationKeys.value = listOf(newKey) + _activationKeys.value
        return newKey
    }

    fun redeemActivationKey(inputCode: String): Pair<Boolean, String> {
        val clean = inputCode.uppercase().trim()
        val foundKey = _activationKeys.value.find { it.code == clean }

        if (foundKey == null) {
            return Pair(false, "Invalid key code. Please check with the app developer/admin.")
        }

        if (foundKey.isUsed && foundKey.code != "ADIL-MASTER-2026" && foundKey.code != "ADIL-DEV-KEY") {
            return Pair(false, "This key has already been redeemed.")
        }

        // Mark as used if not reusable master developer key
        if (foundKey.code != "ADIL-MASTER-2026" && foundKey.code != "ADIL-DEV-KEY") {
            _activationKeys.value = _activationKeys.value.map {
                if (it.code == clean) it.copy(isUsed = true, usedBy = "Current User") else it
            }
        }

        // Update user subscription state
        val current = _subscriptionState.value
        _subscriptionState.value = current.copy(
            planType = foundKey.planType,
            maxExportsDaily = foundKey.dailyExportLimit,
            isAdmin = foundKey.planType == PlanType.ADMIN,
            isActive = true
        )

        val msg = if (foundKey.planType == PlanType.ADMIN) {
            "Developer Master Key Activated! Admin status and Unlimited exports unlocked."
        } else {
            "Activation Successful! ${foundKey.label} activated with ${foundKey.dailyExportLimit} daily exports."
        }

        return Pair(true, msg)
    }

    fun applyLicenseKey(key: LicenseKey): String {
        val current = _subscriptionState.value
        _subscriptionState.value = current.copy(
            planType = key.planType,
            maxExportsDaily = key.dailyExportLimit,
            isAdmin = key.planType == PlanType.ADMIN,
            renewalDateText = key.expiryDate,
            isActive = true
        )
        return "License Key '${key.key}' Activated! Plan: ${key.planType.title} (${key.dailyExportLimit} daily exports, Expiry: ${key.expiryDate})"
    }

    fun toggleUserBlock(userId: String) {
        _adminUsers.value = _adminUsers.value.map { user ->
            if (user.id == userId) user.copy(isBlocked = !user.isBlocked) else user
        }
    }

    fun updateUserPlan(userId: String, plan: PlanType) {
        _adminUsers.value = _adminUsers.value.map { user ->
            if (user.id == userId) user.copy(
                plan = plan,
                allocatedLimit = if (plan == PlanType.ADMIN) 9999 else if (plan == PlanType.PRO_MONTHLY) 40 else 2
            ) else user
        }
    }

    fun updateUserQuota(userId: String, newLimit: Int) {
        _adminUsers.value = _adminUsers.value.map { user ->
            if (user.id == userId) user.copy(allocatedLimit = newLimit) else user
        }
    }

    fun revokeActivationKey(code: String) {
        _activationKeys.value = _activationKeys.value.filter { it.code != code }
    }

    fun assignKeyToUser(userId: String, keyCode: String) {
        val key = _activationKeys.value.find { it.code == keyCode } ?: return
        _adminUsers.value = _adminUsers.value.map { user ->
            if (user.id == userId) {
                user.copy(
                    plan = key.planType,
                    allocatedLimit = key.dailyExportLimit,
                    assignedKeyCode = key.code
                )
            } else user
        }
        _activationKeys.value = _activationKeys.value.map {
            if (it.code == keyCode) it.copy(isUsed = true, usedBy = "User #$userId") else it
        }
    }
}
