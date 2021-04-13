package br.ufpe.cin.android.podcast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.PodcastRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PodcastViewModel(val repository: PodcastRepository): ViewModel() {

    val listaPodcast = repository.allEpisodios()

    fun searchByTitulo(titulo: String): Episodio? {
        var podcast: Episodio? = null
        viewModelScope.launch(Dispatchers.IO) {
            podcast = repository.searchByTitulo(titulo)
        }
        return podcast
    }

    fun insert(podcast: Episodio) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(podcast)
        }
    }

    fun update(podcast: Episodio) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(podcast)
        }
    }

    fun delete(podcast: Episodio) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(podcast)
        }
    }
}