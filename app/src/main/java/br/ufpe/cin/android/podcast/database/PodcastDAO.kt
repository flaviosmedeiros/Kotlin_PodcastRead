package br.ufpe.cin.android.podcast.database

import androidx.lifecycle.LiveData
import androidx.room.*
import br.ufpe.cin.android.podcast.data.Episodio

@Dao
interface PodcastDAO {

    @Query("SELECT * FROM episodios")
    fun allEpisodios(): LiveData<List<Episodio>>

    @Query("SELECT * FROM episodios WHERE titulo LIKE :titulo")
    suspend fun searchByTitulo(titulo: String): Episodio?

    @Insert(onConflict = OnConflictStrategy.IGNORE)//##Ignora a operação caso aja conflito
    suspend fun insert(podcast: Episodio)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(podcast: Episodio)

    @Delete
    suspend fun delete(podcast: Episodio)
}