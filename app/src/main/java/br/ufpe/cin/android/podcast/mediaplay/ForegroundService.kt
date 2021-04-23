package br.ufpe.cin.android.podcast.mediaplay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import br.ufpe.cin.android.podcast.MainActivity
import br.ufpe.cin.android.podcast.R
import java.io.File


//## Serviço responsável por rodar o podcast
class ForegroundService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val audioBinder: IBinder = MusicBinder()
    private lateinit var ultimoPodcast: String
    private var posicaoAudio = 0

    companion object {
        const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
        const val NOTIFICATION_ID = 1
        const val VERBOSE_NOTIFICATION_DESCRIPTION = "Shows notifications for audio running"
        const val VERBOSE_NOTIFICATION_NAME = "Verbose notifications"
    }

    override fun onCreate() {
        super.onCreate()

        ultimoPodcast = ""
        mediaPlayer = MediaPlayer()

        mediaPlayer.setOnCompletionListener {
            val myFile = File(ultimoPodcast)
            myFile.delete()
        }

        //## Verificando a versao do Android
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val myNotification = NotificationChannel(
                CHANNEL_ID,
                VERBOSE_NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_HIGH)

            myNotification.description = VERBOSE_NOTIFICATION_DESCRIPTION

            val myNotificationManager = getSystemService(NotificationManager::class.java)

            myNotificationManager.createNotificationChannel(myNotification)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.iconfinder_youtube_youtuber_video_play_5340279)
            .setOngoing(true)
            .setContentTitle("Meu Podcast player")
            .setContentText("Confira os Podcast disponíveis!")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun playOrPauseMusic(filePath: String, audioPosition: Int) {
        //## verificando se o podcast está tocando
        if(!mediaPlayer.isPlaying) {

            if(filePath != ultimoPodcast) {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(filePath)
                mediaPlayer.prepare()

                ultimoPodcast = filePath
            }

            mediaPlayer.start()

            if(audioPosition > 0) {
                mediaPlayer.seekTo(audioPosition)
            }

        } else {
            posicaoAudio = mediaPlayer.currentPosition
            mediaPlayer.pause()
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun currentAudioPositionWhenReproducing(): Int {
        return posicaoAudio
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return audioBinder
    }

    inner class MusicBinder: Binder() {
        val service: ForegroundService
            get() = this@ForegroundService
    }
}