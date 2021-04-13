package br.ufpe.cin.android.podcast.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.ufpe.cin.android.podcast.data.Episodio

class PodcastRepository(private val dao: PodcastDAO) {

    @WorkerThread//##habilitar que a operação seja realizada em background
    fun allEpisodios(): LiveData<List<Episodio>> {
        return this.dao.allEpisodios()
    }

    @WorkerThread
    suspend  fun searchByTitulo(titulo: String): Episodio? {
        return this.dao.searchByTitulo(titulo)
    }

    @WorkerThread
    suspend fun insert(podcast: Episodio) {
        this.dao.insert(podcast)
    }

    @WorkerThread
    suspend fun update(podcast: Episodio) {
        this.dao.update(podcast)
    }

    @WorkerThread
    suspend fun delete(podcast: Episodio) {
        this.dao.delete(podcast)
    }
}