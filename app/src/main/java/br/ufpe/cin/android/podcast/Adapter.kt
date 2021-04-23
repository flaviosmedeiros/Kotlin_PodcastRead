package br.ufpe.cin.android.podcast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import br.ufpe.cin.android.podcast.data.Episodio


class Adapter(private val mutableList: MutableList<Episodio>, var onTitleListener:OnClickTitleListener): ListAdapter<Episodio, ViewHolder>(PodcastDiffer) {

    private lateinit var parentContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itemfeed,
                                                                          parent,
                                                              false)
        parentContext = parent.context
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = mutableList[position].titulo
        holder.date.text = mutableList[position].dataPublicacao

        holder.title.setOnClickListener {
            onTitleListener.onClick(mutableList[position], it)
        }

        val filePath = mutableList[position].linkArquivo

        if(!(URLUtil.isHttpUrl(filePath) || URLUtil.isHttpsUrl(filePath))) {
            val icone = ResourcesCompat.getDrawable(
                parentContext.resources,
                R.drawable.iconfinder_youtube_youtuber_video_play_5340279,
                null)

            holder.downloadButton.setCompoundDrawablesWithIntrinsicBounds(
                icone,
                null,
                null,
                null)

            //##Limpando o nome do botão
            holder.downloadButton.text = ""
        }

        holder.downloadButton.setOnClickListener {
            onTitleListener.onClick(mutableList[position], it)
        }

    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    //## passando para a MainActivy a responsabilidade
    interface OnClickTitleListener {
        fun onClick(podCast: Episodio, view: View)
    }


    private object PodcastDiffer: DiffUtil.ItemCallback<Episodio>() {

        override fun areItemsTheSame(oldItem: Episodio, newItem: Episodio): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Episodio, newItem: Episodio): Boolean {
            val checkTitulo = oldItem.titulo == newItem.titulo
            val checkLinkEpisodio = oldItem.linkEpisodio == newItem.linkEpisodio
            val checkDescricao = oldItem.descricao == newItem.descricao
            val checkLinkArquivo = oldItem.linkArquivo == newItem.linkArquivo
            val checkDataPublicacao = oldItem.dataPublicacao == newItem.dataPublicacao

            return checkTitulo
                    && checkLinkArquivo
                    && checkLinkEpisodio
                    && checkDescricao
                    && checkDataPublicacao
        }

    }

}