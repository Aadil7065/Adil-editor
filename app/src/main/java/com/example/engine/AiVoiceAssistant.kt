package com.example.engine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.data.model.VoiceCommandResult
import java.util.Locale

class AiVoiceAssistant(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    fun parseEditingIntent(command: String, playheadMs: Long): VoiceCommandResult {
        val lower = command.trim().lowercase(Locale.ROOT)

        return when {
            // Hindi / Hinglish: "Is video ke starting ke 5 seconds hata do aur subtitles add kar do"
            lower.contains("starting ke") && (lower.contains("hata") || lower.contains("cut") || lower.contains("delete")) -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "TRIM_START_AND_CAPTIONS",
                    proposedChanges = listOf(
                        "Trim first 5.0 seconds from the beginning of timeline",
                        "Auto-generate synchronized 3D animated subtitles for remaining dialogue",
                        "Re-align audio and B-roll tracks to t=00:00:00"
                    ),
                    executableAction = "ACTION_TRIM_START_5S_ADD_CAPTIONS",
                    isDestructive = true
                )
            }

            // Remove silence
            lower.contains("remove silence") || lower.contains("silence hata") || lower.contains("cut silence") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "REMOVE_SILENCE",
                    proposedChanges = listOf(
                        "Scan multitrack audio waveform for segments below -38dB",
                        "Ripple-delete 3 silent pauses (total 4.2 seconds saved)",
                        "Smooth audio cut transitions with 80ms crossfades"
                    ),
                    executableAction = "ACTION_REMOVE_SILENCE",
                    isDestructive = true
                )
            }

            // Add cinematic transitions
            lower.contains("transition") || lower.contains("transitions") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "ADD_CINEMATIC_TRANSITIONS",
                    proposedChanges = listOf(
                        "Apply 'Glitch Warp' transition between Scene 1 and Scene 2",
                        "Apply 'Crossfade' transition between Scene 2 and Scene 3",
                        "Sync visual cut points to musical downbeats"
                    ),
                    executableAction = "ACTION_ADD_TRANSITIONS",
                    isDestructive = false
                )
            }

            // Add 3D captions / subtitles
            lower.contains("caption") || lower.contains("subtitles") || lower.contains("subtitle") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "GENERATE_3D_CAPTIONS",
                    proposedChanges = listOf(
                        "Transcribe active audio track with word-level accuracy",
                        "Generate animated 3D Neon Glow caption preset",
                        "Highlight key emphasis words with cyan extrusion effect"
                    ),
                    executableAction = "ACTION_GENERATE_3D_CAPTIONS",
                    isDestructive = false
                )
            }

            // Add B-roll / overlay footage
            lower.contains("b-roll") || lower.contains("broll") || lower.contains("footage") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "ADD_AI_BROLL",
                    proposedChanges = listOf(
                        "Analyze script narrative context: 'Spaceship Corridor'",
                        "Insert AI B-roll layer 1: 'Hyper-jump Engine Thrust' (12s)",
                        "Insert AI B-roll layer 2: 'Cyber HUD Telemetry' (10s)",
                        "Set blend mode to Screen with 85% opacity"
                    ),
                    executableAction = "ACTION_ADD_AI_BROLL",
                    isDestructive = false
                )
            }

            // Make video look professional / color grade
            lower.contains("professional") || lower.contains("look pro") || lower.contains("color grade") || lower.contains("cinematic") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "PRO_GRADE_AND_ENHANCE",
                    proposedChanges = listOf(
                        "Apply 'Teal & Orange Cinematic' 3D LUT filter across all clips",
                        "Enable AI Voice-Over noise reduction (-18dB ambient noise floor)",
                        "Boost dialogue clarity and add dynamic range compression",
                        "Add subtle vignette and anamorphic widescreen framing"
                    ),
                    executableAction = "ACTION_MAKE_PROFESSIONAL",
                    isDestructive = false
                )
            }

            // Background music
            lower.contains("music") || lower.contains("bgm") || lower.contains("background music") || lower.contains("soundtrack") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "ADD_BGM",
                    proposedChanges = listOf(
                        "Select royalty-free Cyberpunk Ambient Synthwave track",
                        "Auto-duck music volume to 25% whenever voice-over dialogue speaks",
                        "Add 2.0s smooth fade-in and 3.0s fade-out at project end"
                    ),
                    executableAction = "ACTION_ADD_BGM",
                    isDestructive = false
                )
            }

            // Speed controls
            lower.contains("speed") || lower.contains("fast") || lower.contains("slow") || lower.contains("tez") -> {
                val speed = if (lower.contains("2x")) 2.0f else if (lower.contains("slow")) 0.5f else 1.5f
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "ADJUST_SPEED",
                    proposedChanges = listOf(
                        "Adjust playback speed to ${speed}x for active clip",
                        "Enable pitch correction to prevent audio chipmunk distortion",
                        "Ripple-adjust subsequent timeline clips"
                    ),
                    executableAction = "ACTION_SET_SPEED_$speed",
                    isDestructive = false
                )
            }

            // Split / Cut
            lower.contains("split") || lower.contains("cut") || lower.contains("tukde") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "SPLIT_AT_PLAYHEAD",
                    proposedChanges = listOf(
                        "Split active video clip at playhead position (${playheadMs / 1000}s)",
                        "Preserve audio sync across both clip segments",
                        "Create new independent clip entity"
                    ),
                    executableAction = "ACTION_SPLIT_AT_PLAYHEAD",
                    isDestructive = false
                )
            }

            // Export
            lower.contains("export") || lower.contains("render") || lower.contains("save video") || lower.contains("download") -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "EXPORT_VIDEO",
                    proposedChanges = listOf(
                        "Prepare timeline for 1080p Full HD rendering (60 FPS)",
                        "Check daily export quota (Adil Pro: 40/day)",
                        "Launch background H.264 rendering pipeline"
                    ),
                    executableAction = "ACTION_EXPORT_HD",
                    isDestructive = false
                )
            }

            // Default generic AI editing instruction
            else -> {
                VoiceCommandResult(
                    transcript = command,
                    detectedIntent = "AI_TIMELINE_OPTIMIZATION",
                    proposedChanges = listOf(
                        "Analyze prompt instruction: '$command'",
                        "Apply smart color enhancement and contrast balancing",
                        "Align multitrack timing and optimize audio dynamic range"
                    ),
                    executableAction = "ACTION_GENERIC_AI_OPTIMIZE",
                    isDestructive = false
                )
            }
        }
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onListeningStateChanged: (Boolean) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device")
            onListeningStateChanged(false)
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        onListeningStateChanged(true)
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        onListeningStateChanged(false)
                    }

                    override fun onError(error: Int) {
                        onListeningStateChanged(false)
                        val errorMsg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Please speak clearly."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check microphone."
                            else -> "Voice recognition note: tap prompt suggestions or retry."
                        }
                        onError(errorMsg)
                    }

                    override fun onResults(results: Bundle?) {
                        onListeningStateChanged(false)
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onResult(matches[0])
                        } else {
                            onError("Could not understand voice command.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            onListeningStateChanged(false)
            onError(e.message ?: "Failed to start speech listener")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
    }
}
