package br.ufpe.cin.android.podcast.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.ufpe.cin.android.podcast.data.Episodio

@Database(entities = [Episodio::class], version = 1)
abstract class PodcastDataBase: RoomDatabase() {
    abstract fun dao(): PodcastDAO
    companion object {

        @Volatile
        private var INSTANCE: PodcastDataBase? = null
        fun getInstance(ctx: Context): PodcastDataBase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(ctx.applicationContext, PodcastDataBase::class.java, "episode_history_database")
                                   .fallbackToDestructiveMigration()
                                   .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}