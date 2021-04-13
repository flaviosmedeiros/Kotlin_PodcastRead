package br.ufpe.cin.android.podcast

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.PodcastDataBase
import br.ufpe.cin.android.podcast.database.PodcastRepository
import br.ufpe.cin.android.podcast.databinding.ActivityMainBinding
import br.ufpe.cin.android.podcast.viewmodel.PodcastViewModel
import br.ufpe.cin.android.podcast.viewmodel.PodcastViewModelFactory
import com.prof.rssparser.Article
import com.prof.rssparser.Parser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), Adapter.OnClickTitleListener {
    private lateinit var binding : ActivityMainBinding
    private lateinit var parser : Parser
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private lateinit var preferencias: SharedPreferences

    private val myViewModel: PodcastViewModel by viewModels {
        PodcastViewModelFactory(PodcastRepository(PodcastDataBase.getInstance(this)
                                                                 .dao()))
    }


    companion object {
        val PODCAST_FEED = "https://jovemnerd.com.br/feed-nerdcast/"
        val EXTRA_DESCRIPTION = "EPISODE_DESCRIPTION"
        val EXTRA_LINK = "EPISODE_LINK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parser = Parser.Builder()
            .context(this)
            .cacheExpirationMillis(24L * 60L * 60L * 100L)
            .build()
    }

    override fun onStart() {
        super.onStart()

        preferencias = PreferenceManager.getDefaultSharedPreferences(this)
        scope.launch {
            val channel = withContext(Dispatchers.IO) {
                val linkPref = preferencias.getString(getString(R.string.rss_key), getString(R.string.link_inicial))
                parser.getChannel(linkPref!!)
            }

            channel.articles.forEach{
                val linkEpisodio = it.link ?: ""
                val titulo = it.title ?: ""
                val descricao = it.description ?: ""
                val linkArquivo = it.audio ?: ""
                val dataPublicacao = it.pubDate ?: ""

                val podcast = Episodio(linkEpisodio, titulo, descricao, linkArquivo, dataPublicacao)
                myViewModel.insert(podcast)
            }

            binding.idRecycle.adapter = Adapter(channel.articles, this@MainActivity)

            //## organiza os itens em uma lista unidimensional
            binding.idRecycle.layoutManager = LinearLayoutManager(this@MainActivity)
        }

    }


    override fun onClick(podCast: Article) {
        val intent = Intent(this, EpisodeDetailActivity::class.java)

        intent.putExtra(EXTRA_DESCRIPTION, podCast.description)
        intent.putExtra(EXTRA_LINK, podCast.link)

        startActivity(intent)
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

}