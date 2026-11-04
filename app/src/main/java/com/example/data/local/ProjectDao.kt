package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AudioTrackItem
import com.example.data.model.BrollItem
import com.example.data.model.CaptionItem
import com.example.data.model.Project
import com.example.data.model.VideoClip
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    // Clips
    @Query("SELECT * FROM video_clips WHERE projectId = :projectId ORDER BY startMs ASC")
    fun getClipsForProject(projectId: String): Flow<List<VideoClip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClips(clips: List<VideoClip>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClip(clip: VideoClip)

    @Update
    suspend fun updateClip(clip: VideoClip)

    @Query("DELETE FROM video_clips WHERE id = :clipId")
    suspend fun deleteClip(clipId: String)

    @Query("DELETE FROM video_clips WHERE projectId = :projectId")
    suspend fun deleteClipsForProject(projectId: String)

    // Audio
    @Query("SELECT * FROM audio_tracks WHERE projectId = :projectId ORDER BY startMs ASC")
    fun getAudioForProject(projectId: String): Flow<List<AudioTrackItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioTracks(tracks: List<AudioTrackItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioTrack(track: AudioTrackItem)

    @Query("DELETE FROM audio_tracks WHERE id = :trackId")
    suspend fun deleteAudioTrack(trackId: String)

    // B-roll
    @Query("SELECT * FROM broll_items WHERE projectId = :projectId ORDER BY startMs ASC")
    fun getBrollForProject(projectId: String): Flow<List<BrollItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrollItems(items: List<BrollItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrollItem(item: BrollItem)

    @Query("DELETE FROM broll_items WHERE id = :itemId")
    suspend fun deleteBrollItem(itemId: String)

    // Captions
    @Query("SELECT * FROM caption_items WHERE projectId = :projectId ORDER BY startMs ASC")
    fun getCaptionsForProject(projectId: String): Flow<List<CaptionItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaptions(items: List<CaptionItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaption(item: CaptionItem)

    @Query("DELETE FROM caption_items WHERE id = :captionId")
    suspend fun deleteCaption(captionId: String)
}
