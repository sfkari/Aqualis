package com.cmc.aqualis

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var surfaceView: SurfaceView
    private lateinit var logoImageView: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        surfaceView = findViewById(R.id.surfaceView)
        surfaceView.holder.addCallback(this)


    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer = MediaPlayer().apply {
            val videoPath = "android.resource://$packageName/${R.raw.splash_video}"
            setDataSource(this@SplashActivity, Uri.parse(videoPath))
            setDisplay(holder)
            isLooping = false // Pas de boucle
            setOnPreparedListener {
                start()
            }
            setOnCompletionListener {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
            prepareAsync()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mediaPlayer.release()
    }
}
