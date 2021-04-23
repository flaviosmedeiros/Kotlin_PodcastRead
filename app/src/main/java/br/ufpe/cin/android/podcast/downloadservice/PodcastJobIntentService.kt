package br.ufpe.cin.android.podcast.downloadservice

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import androidx.core.app.JobIntentService
import br.ufpe.cin.android.podcast.MainActivity.Companion.EXTRA_POSCAST_DOWNLOADED
import br.ufpe.cin.android.podcast.data.Episodio
import br.ufpe.cin.android.podcast.database.PodcastDataBase
import br.ufpe.cin.android.podcast.database.PodcastRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

//## Serviço responsável por baixar o Podcast clicado pelo usuario
class PodcastJobIntentService: JobIntentService() {

    companion object {
        private const val JOB_ID = 9874
        const val PODCAST_DOWNLOAD_COMPLETE = "br.ufpe.cin.android.podcast.downloadservice.PodcastJobIntentService"

        fun enqueueWork(contexto: Context, intent: Intent) {
            enqueueWork(contexto, PodcastJobIntentService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        //##Implementação conforme apresentado em sala de aula.
        //## Copiado do repositorio disponibilizado pelo professor
        try {
            val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            root.mkdirs()

            //pegando o campo data do intent passado
            val intentData = intent.data

            val output = File(root, intentData!!.lastPathSegment)
            if (output.exists()) {
                output.delete()
            }
            val url = URL(intentData.toString())
            val c = url.openConnection() as HttpURLConnection
            val fos = FileOutputStream(output.path)
            val out = BufferedOutputStream(fos)

            try {
                val `in` = c.inputStream
                val buffer = ByteArray(8192)
                var len = 0
                while (`in`.read(buffer).also { len = it } >= 0) {
                    out.write(buffer, 0, len)
                }
                out.flush()
            } finally {
                fos.fd.sync()
                out.close()
                c.disconnect()
            }
            //## FIM DO CODIGO COPIADO DO REPOSITORO DO PROFESSOR


            val scope = CoroutineScope(Dispatchers.Main.immediate)

            scope.launch {
                val repository = PodcastRepository(PodcastDataBase.getInstance(applicationContext)
                                                                  .dao())

                println("## = > OUTPUT_PATH=>  " + output.path)
                //## Buscando o podcast pelo titulo
                val podcast = repository.searchByTitulo(intent.getStringExtra(EXTRA_POSCAST_DOWNLOADED)!!)
                if (podcast != null) {
                    println("## = > LINK_ARQUIVO=>  " + podcast.linkArquivo)
                }

                repository.update(Episodio(linkEpisodio = podcast!!.linkEpisodio,
                                           titulo = podcast.titulo,
                                           descricao = podcast.descricao,
                                           linkArquivo = output.path,
                                           dataPublicacao = podcast.dataPublicacao,
                                           posicaoPoscast = podcast.posicaoPoscast))

                val podcast2 = repository.searchByTitulo(intent.getStringExtra(EXTRA_POSCAST_DOWNLOADED)!!)
                if (podcast2 != null) {
                    println("## = > LINK_ARQUIVO_DEPOIS DO UPDATE=>  " + podcast2.linkArquivo)
                }
            }

            //## Enviando mensagem brodcast informando que o podcast foi baixado.
            sendBroadcast(Intent(PODCAST_DOWNLOAD_COMPLETE))

        } catch (e: IOException) {
            Log.e(javaClass.name, "Exception durante download", e)
        }
    }
}