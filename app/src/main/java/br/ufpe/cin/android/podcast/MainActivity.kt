package br.ufpe.cin.android.podcast

import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.PodcastDataBase
import br.ufpe.cin.android.podcast.database.PodcastRepository
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.downloadservice.DownloadJobIntentService
import br.ufpe.cin.android.podcast.downloadservice.PodcastJobIntentService
import br.ufpe.cin.android.podcast.mediaplay.ForegroundService
import br.ufpe.cin.android.podcast.viewmodel.PodcastViewModel
import br.ufpe.cin.android.podcast.viewmodel.PodcastViewModelFactory


class MainActivity : AppCompatActivity(), Adapter.OnClickTitleListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var myAdapter: Adapter

    private val myViewModel: PodcastViewModel by viewModels {
        PodcastViewModelFactory(PodcastRepository(PodcastDataBase.getInstance(this).dao()))
    }

    private var myMusicForegroundService: ForegroundService? = null
    private var isBound = false//## Verificar se o service foi vinculado a Activity

    companion object {
        val EXTRA_DESCRIPTION = "EPISODE_DESCRIPTION"
        val EXTRA_LINK = "EPISODE_LINK"
        val EXTRA_POSCAST_DOWNLOADED = "EXTRA_POSCAST_DOWNLOADED"
    }

    //## Ações realizadas apos o recebimento da mensagem brodcast informando a conclusão do
    //## download do feed de podcasts
    private val onFeedPodcastDownloaded = object: BroadcastReceiver() {
        override fun onReceive(contexto: Context?, intent: Intent?) {
            val listaPodcastMutableList = mutableListOf<Episodio>()

            //## Apos os podcasts serem baixados, vamos adicionar cada um em nossa MutableList
            myViewModel.listaPodcast.value?.forEach { listaPodcastMutableList.add(it) }

            binding.idRecycle.adapter = Adapter(listaPodcastMutableList, this@MainActivity)
            binding.idRecycle.layoutManager = LinearLayoutManager(this@MainActivity)

            //## Exibindo mensagem toast na tela
            Toast.makeText(contexto, "Lista de Podcasts baixada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    //## Exibe toast após conclusão do download do podcast clicado
    private val onAudioPodcastDownloaded = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context,"Podcast baixado com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //## Inicializando o serviço junto com o APP
        startService(Intent(this, ForegroundService::class.java))

        myAdapter = Adapter(mutableListOf(), this)

        //Chamada ao serviço encarregado de baixar o feed de podcasts
        DownloadJobIntentService.enqueueWork(this, Intent(this, DownloadJobIntentService::class.java))

        myViewModel.listaPodcast.observe(this, Observer {myAdapter.submitList(it.toList())})

        if(!isBound) {
            val intent = Intent(this, ForegroundService::class.java)
            bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE)
        }
    }

    //## Registrando os observer nos inicio do ciclo de vida
    override fun onResume() {
        super.onResume()
        registerReceiver(onFeedPodcastDownloaded, IntentFilter(DownloadJobIntentService.DOWNLOAD_PODCAST))
        registerReceiver(onAudioPodcastDownloaded, IntentFilter(PodcastJobIntentService.PODCAST_DOWNLOAD_COMPLETE))
    }

    override fun onPause() {
        unregisterReceiver(onFeedPodcastDownloaded)
        unregisterReceiver(onAudioPodcastDownloaded)
        super.onPause()
    }

    override fun onClick(podCast: Episodio, view: View) {

        when(view.id) {

            R.id.item_title -> {
                val intent = Intent(this, EpisodeDetailActivity::class.java)
                intent.putExtra(EXTRA_DESCRIPTION, podCast.descricao)
                intent.putExtra(EXTRA_LINK, podCast.linkArquivo)
                startActivity(intent)
            }

            R.id.item_action -> {
                val link = podCast.linkArquivo
                if (URLUtil.isHttpUrl(link) || URLUtil.isHttpsUrl(link)) {
                    val intent = Intent(this, PodcastJobIntentService::class.java)

                    intent.data = Uri.parse(link)

                    intent.putExtra(EXTRA_POSCAST_DOWNLOADED, podCast.titulo)

                    Toast.makeText(applicationContext, "Baixando Podcast", Toast.LENGTH_SHORT).show()

                    PodcastJobIntentService.enqueueWork(this, intent)

                } else if((link.isNotBlank()) && (link.isNotEmpty())) {

                    myMusicForegroundService?.playOrPauseMusic(link, podCast.posicaoPoscast)
                    if(myMusicForegroundService?.isPlaying() == false) {
                        val position = myMusicForegroundService?.currentAudioPositionWhenReproducing()

                        if((position != null) && (position > 0)) {
                            myViewModel.update(Episodio(
                                podCast.linkEpisodio,
                                podCast.titulo,
                                podCast.descricao,
                                podCast.linkArquivo,
                                podCast.dataPublicacao,
                                position
                            ))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menuapp, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_preferences -> {
                startActivity(Intent(this, PreferenciasActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isBound = true
            val audioBinder = service as ForegroundService.MusicBinder
            myMusicForegroundService = audioBinder.service
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            myMusicForegroundService = null
        }
    }

    override fun onStop() {
        if(isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onStop()
    }

}