package com.example.engine

import com.example.data.model.AudioTrackItem
import com.example.data.model.BrollItem
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.Project
import com.example.data.model.TransitionType
import com.example.data.model.VideoClip
import com.example.data.model.VideoFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

data class TimelineSnapshot(
    val clips: List<VideoClip>,
    val audioTracks: List<AudioTrackItem>,
    val brollItems: List<BrollItem>,
    val captions: List<CaptionItem>
)

class TimelineEngine(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playheadMs = MutableStateFlow(2000L) // Start around 00:00:02 like in the screenshot
    val playheadMs = _playheadMs.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>("clip_1")
    val selectedClipId = _selectedClipId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5x to 3.0x
    val timelineZoom = _timelineZoom.asStateFlow()

    private val _isRecordingVoiceover = MutableStateFlow(false)
    val isRecordingVoiceover = _isRecordingVoiceover.asStateFlow()

    // Undo / Redo history
    private val undoStack = mutableListOf<TimelineSnapshot>()
    private val redoStack = mutableListOf<TimelineSnapshot>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo = _canRedo.asStateFlow()

    private var playbackTickerJob: Job? = null

    fun play() {
        if (_isPlaying.value) return
        _isPlaying.value = true
        startPlaybackTicker()
    }

    fun pause() {
        _isPlaying.value = false
        playbackTickerJob?.cancel()
        playbackTickerJob = null
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun seekTo(timeMs: Long, totalDurationMs: Long) {
        val clamped = timeMs.coerceIn(0L, totalDurationMs.coerceAtLeast(1000L))
        _playheadMs.value = clamped
    }

    fun rewind5s(totalDurationMs: Long) {
        seekTo(_playheadMs.value - 5000L, totalDurationMs)
    }

    fun forward5s(totalDurationMs: Long) {
        seekTo(_playheadMs.value + 5000L, totalDurationMs)
    }

    fun selectClip(clipId: String?) {
        _selectedClipId.value = clipId
    }

    fun setZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.5f)
    }

    fun zoomIn() {
        setZoom(_timelineZoom.value + 0.25f)
    }

    fun zoomOut() {
        setZoom(_timelineZoom.value - 0.25f)
    }

    private fun startPlaybackTicker() {
        playbackTickerJob?.cancel()
        playbackTickerJob = scope.launch {
            while (isActive && _isPlaying.value) {
                delay(50)
                val current = _playheadMs.value
                val next = current + 50
                // Loop back if reaching 95 seconds or end
                if (next > 95000L) {
                    _playheadMs.value = 0L
                } else {
                    _playheadMs.value = next
                }
            }
        }
    }

    fun pushUndoState(snapshot: TimelineSnapshot) {
        undoStack.add(snapshot)
        redoStack.clear()
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = false
    }

    fun undo(currentSnapshot: TimelineSnapshot): TimelineSnapshot? {
        if (undoStack.isEmpty()) return null
        val previous = undoStack.removeAt(undoStack.lastIndex)
        redoStack.add(currentSnapshot)
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
        return previous
    }

    fun redo(currentSnapshot: TimelineSnapshot): TimelineSnapshot? {
        if (redoStack.isEmpty()) return null
        val next = redoStack.removeAt(redoStack.lastIndex)
        undoStack.add(currentSnapshot)
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
        return next
    }

    // Editing Operations
    fun splitClipAtPlayhead(
        clips: List<VideoClip>,
        playhead: Long
    ): List<VideoClip> {
        val targetClip = clips.find { playhead > it.startMs && playhead < (it.startMs + it.durationMs) }
            ?: return clips

        val offsetInClip = playhead - targetClip.startMs
        val firstDuration = offsetInClip
        val secondDuration = targetClip.durationMs - offsetInClip

        val clipPart1 = targetClip.copy(
            durationMs = firstDuration,
            title = "${targetClip.title} (Pt 1)"
        )
        val clipPart2 = targetClip.copy(
            id = UUID.randomUUID().toString(),
            startMs = playhead,
            durationMs = secondDuration,
            title = "${targetClip.title} (Pt 2)"
        )

        val updated = mutableListOf<VideoClip>()
        for (c in clips) {
            if (c.id == targetClip.id) {
                updated.add(clipPart1)
                updated.add(clipPart2)
            } else {
                updated.add(c)
            }
        }
        return updated
    }

    fun removeSilence(
        clips: List<VideoClip>,
        audioTracks: List<AudioTrackItem>
    ): Pair<List<VideoClip>, List<AudioTrackItem>> {
        // Trims 1.5s from gap regions and compacts clips
        var runningStart = 0L
        val trimmedClips = clips.mapIndexed { idx, clip ->
            val trimAmount = if (clip.durationMs > 6000L) 1500L else 0L
            val newDuration = clip.durationMs - trimAmount
            val updated = clip.copy(
                startMs = runningStart,
                durationMs = newDuration
            )
            runningStart += newDuration
            updated
        }

        val updatedAudio = audioTracks.map {
            it.copy(
                durationMs = runningStart,
                noiseReduction = true
            )
        }

        return Pair(trimmedClips, updatedAudio)
    }

    fun trimStartSeconds(
        clips: List<VideoClip>,
        secondsToTrim: Long
    ): List<VideoClip> {
        val trimMs = secondsToTrim * 1000L
        var accumulated = 0L
        val result = mutableListOf<VideoClip>()

        for (clip in clips) {
            val clipEnd = clip.startMs + clip.durationMs
            if (clipEnd <= trimMs) {
                // Entire clip falls within trimmed range -> omit
                continue
            } else if (clip.startMs < trimMs && clipEnd > trimMs) {
                // Clip spans over the cut
                val remainingDuration = clipEnd - trimMs
                result.add(
                    clip.copy(
                        startMs = accumulated,
                        durationMs = remainingDuration,
                        trimStartMs = trimMs - clip.startMs
                    )
                )
                accumulated += remainingDuration
            } else {
                // Clip is beyond cut
                result.add(
                    clip.copy(
                        startMs = accumulated
                    )
                )
                accumulated += clip.durationMs
            }
        }
        return result
    }

    fun generateAutoCaptions(
        projectId: String,
        clips: List<VideoClip>
    ): List<CaptionItem> {
        val captions = mutableListOf<CaptionItem>()
        var capId = 1
        for (clip in clips) {
            val p1 = clip.startMs + 1000L
            val d1 = (clip.durationMs / 2).coerceAtLeast(3000L)
            captions.add(
                CaptionItem(
                    id = "cap_${System.currentTimeMillis()}_${capId++}",
                    projectId = projectId,
                    text = "${clip.title.uppercase()} // ACTIVE SEQUENCE",
                    startMs = p1,
                    durationMs = d1,
                    style = CaptionStyle.NEON_3D,
                    highlightWord = "ACTIVE"
                )
            )
        }
        return captions
    }

    fun applyCinematicTransitions(clips: List<VideoClip>): List<VideoClip> {
        return clips.mapIndexed { index, clip ->
            if (index == 0) clip else clip.copy(
                transitionIn = if (index % 2 == 1) TransitionType.GLITCH_WARP else TransitionType.CROSSFADE
            )
        }
    }
}
