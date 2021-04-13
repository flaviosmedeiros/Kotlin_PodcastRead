package br.ufpe.cin.android.podcast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.ufpe.cin.android.podcast.database.PodcastRepository

class PodcastViewModelFactory(private val repository: PodcastRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PodcastViewModel::class.java)) {
            return PodcastViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel não encontrado!")
    }
}