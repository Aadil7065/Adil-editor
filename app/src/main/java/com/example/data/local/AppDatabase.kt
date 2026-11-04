package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.AspectRatio
import com.example.data.model.AudioTrackItem
import com.example.data.model.BrollItem
import com.example.data.model.CaptionItem
import com.example.data.model.CaptionStyle
import com.example.data.model.Project
import com.example.data.model.Resolution
import com.example.data.model.TransitionType
import com.example.data.model.VideoClip
import com.example.data.model.VideoFilter

class Converters {
    @TypeConverter
    fun fromAspectRatio(value: AspectRatio): String = value.name

    @TypeConverter
    fun toAspectRatio(value: String): AspectRatio = try {
        AspectRatio.valueOf(value)
    } catch (_: Exception) {
        AspectRatio.RATIO_16_9
    }

    @TypeConverter
    fun fromResolution(value: Resolution): String = value.name

    @TypeConverter
    fun toResolution(value: String): Resolution = try {
        Resolution.valueOf(value)
    } catch (_: Exception) {
        Resolution.RES_1080P
    }

    @TypeConverter
    fun fromVideoFilter(value: VideoFilter): String = value.name

    @TypeConverter
    fun toVideoFilter(value: String): VideoFilter = try {
        VideoFilter.valueOf(value)
    } catch (_: Exception) {
        VideoFilter.NONE
    }

    @TypeConverter
    fun fromTransition(value: TransitionType): String = value.name

    @TypeConverter
    fun toTransition(value: String): TransitionType = try {
        TransitionType.valueOf(value)
    } catch (_: Exception) {
        TransitionType.NONE
    }

    @TypeConverter
    fun fromCaptionStyle(value: CaptionStyle): String = value.name

    @TypeConverter
    fun toCaptionStyle(value: String): CaptionStyle = try {
        CaptionStyle.valueOf(value)
    } catch (_: Exception) {
        CaptionStyle.NEON_3D
    }
}

@Database(
    entities = [
        Project::class,
        VideoClip::class,
        AudioTrackItem::class,
        BrollItem::class,
        CaptionItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edit_with_adil_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
