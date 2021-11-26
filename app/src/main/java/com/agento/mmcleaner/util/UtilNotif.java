package com.agento.mmcleaner.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;


import com.agento.mmcleaner.R;
import com.agento.mmcleaner.ui.splash.SplashActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class UtilNotif {
    public static void showScheduleNotification(Context context) {

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        setListeners(contentView, context);
        int notificationId = 5609;
        String channelId = "5609";
        String channelName = "cleaner-guard";

        Intent intent = new Intent(context, SplashActivity.class);

        //notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, options);

        //channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel channel =
                    new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(channelName);
            channel.enableLights(true);
            channel.setLightColor(context.getResources().getColor(R.color.black));
            channel.enableVibration(false);
            channel.setSound(alarmSound, att);
            notificationManager.createNotificationChannel(channel);
        }

        //build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContent(contentView)
                .setColor(context.getResources().getColor(R.color.black))
                .setSound(alarmSound)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_DEFAULT);

        //show notification
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        notificationManager.notify(notificationId, notification);

    }
    private static void setListeners(RemoteViews view, Context ctx){

        /*Intent radio=new Intent(ctx, SplashActivity.class);
        radio.putExtra("notif", "scan");
        PendingIntent pRadio = PendingIntent.getActivity(ctx, 0, radio, 0);
        view.setOnClickPendingIntent(R.id.scan, pRadio);


        Intent volume=new Intent(ctx, SplashActivity.class);
        volume.putExtra("notif", "speed");
        PendingIntent pVolume = PendingIntent.getActivity(ctx, 1, volume, 0);
        view.setOnClickPendingIntent(R.id.speed, pVolume);


        Intent reboot=new Intent(ctx, SplashActivity.class);
        reboot.putExtra("notif", "battery");
        PendingIntent pReboot = PendingIntent.getActivity(ctx, 5, reboot, 0);
        view.setOnClickPendingIntent(R.id.battery, pReboot);


        Intent top=new Intent(ctx, SplashActivity.class);
        top.putExtra("notif", "cpu");
        PendingIntent pTop = PendingIntent.getActivity(ctx, 3, top, 0);
        view.setOnClickPendingIntent(R.id.cpu, pTop);


        Intent app=new Intent(ctx, SplashActivity.class);
        app.putExtra("notif", "clear");
        PendingIntent pApp = PendingIntent.getActivity(ctx, 4, app, 0);
        view.setOnClickPendingIntent(R.id.clear, pApp);*/
    }
}
