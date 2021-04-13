package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.ufpe.cin.android.podcast.databinding.ActivityEpisodeDetailBinding
class EpisodeDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEpisodeDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEpisodeDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //## Pegando as variaveis informadas no click
        val extras = intent.extras
        if(extras != null){
            binding.description.text = extras.getString(MainActivity.EXTRA_DESCRIPTION)
            binding.link.text = extras.getString(MainActivity.EXTRA_LINK)
        }




    }
}