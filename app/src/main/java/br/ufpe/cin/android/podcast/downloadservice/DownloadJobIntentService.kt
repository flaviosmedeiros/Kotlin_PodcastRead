package br.ufpe.cin.android.podcast.downloadservice

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.preference.PreferenceManager
import br.ufpe.cin.android.podcast.Adapter
import br.ufpe.cin.android.podcast.R
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.PodcastDataBase
import br.ufpe.cin.android.podcast.database.PodcastRepository
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.viewmodel.PodcastViewModel
import br.ufpe.cin.android.podcast.viewmodel.PodcastViewModelFactory
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//## Serviço responsável por baixar o Feed de Podcasts exibindo na tela inicial
class DownloadJobIntentService: JobIntentService() {

    private lateinit var binding : ActivityMainBinding

    companion object {
        private val JOB = 5432
        val DOWNLOAD_PODCAST = "br.ufpe.cin.android.podcast.downloadservice.DownloadJobIntentService"
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, DownloadJobIntentService::class.java, JOB, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val scope = CoroutineScope(Dispatchers.Main.immediate)
        val parser = Parser.Builder()
            .context(this)
            .cacheExpirationMillis(24L * 60L * 60L * 100L)
            .build()

        scope.launch {
            val channel = withContext(Dispatchers.IO) {
                val link = pref.getString(getString(R.string.rss_key),getString(R.string.link_inicial))
                parser.getChannel(link!!)}

            val repository = PodcastRepository(
                                             PodcastDataBase.getInstance(applicationContext)
                                                            .dao())

            //TODO Criar função pra fazer esse mapeamento/conversao e salvar no BD
            channel.articles.forEach {
                val linkEpisodio = it.link ?: ""
                val titulo = it.title ?: ""
                val descricao = it.description ?: ""
                val linkArquivo = it.audio ?: ""
                val dataPublicacao = it.pubDate ?: ""
                repository.insert(Episodio(linkEpisodio, titulo, descricao, linkArquivo, dataPublicacao,0))
            }

            //##Enviando mensagem brodcast informando que download foi finalizado
            sendBroadcast(Intent(DOWNLOAD_PODCAST))
        }
    }
}