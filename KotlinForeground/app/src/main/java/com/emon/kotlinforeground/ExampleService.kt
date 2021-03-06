package com.emon.kotlinforeground

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.emon.kotlinforeground.App.Companion.CHANNEL_ID


class ExampleService: Service() {

    lateinit var mediaPlayer: MediaPlayer

    var handler = Handler(Looper.getMainLooper())
    var runnable: Runnable = object : Runnable {
        override fun run() {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.bell_in_temple)
            mediaPlayer.start()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        handler.postDelayed(runnable, 500)


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)

            notification.setContentTitle("Example Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Handler(Looper.getMainLooper()).post {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationManager.createNotificationChannel(channel)
                notification.setChannelId(CHANNEL_ID)

                startForeground(1, notification.build())
            }

            startForeground(1, notification.build())
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        handler.removeCallbacks(runnable)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}