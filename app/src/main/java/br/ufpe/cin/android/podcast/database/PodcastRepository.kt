package br.ufpe.cin.android.podcast.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import br.ufpe.cin.android.podcast.data.Episodio

class PodcastRepository(private val dao: PodcastDAO) {

    @WorkerThread//##habilitar que a operação seja realizada em background
    suspend fun insert(podcast: Episodio) {
        dao.insert(podcast)
    }

    @WorkerThread
    suspend fun update(podcast: Episodio) {
        dao.update(podcast)
    }

    @WorkerThread
    suspend fun delete(podcast: Episodio) {
        dao.delete(podcast)
    }

    @WorkerThread
    fun allEpisodios(): LiveData<List<Episodio>> {
        return dao.allEpisodios()
    }

    @WorkerThread
    suspend fun searchByTitulo(titulo: String): Episodio? {
        return dao.searchByTitle(titulo)
    }

}