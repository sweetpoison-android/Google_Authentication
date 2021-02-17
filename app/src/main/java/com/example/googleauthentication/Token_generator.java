package com.example.googleauthentication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import androidx.core.app.NotificationCompat;

public class Token_generator extends FirebaseInstanceIdService {

    String token;

    @Override
    public void onTokenRefresh() {
        token = FirebaseInstanceId.getInstance().getToken();
        bigtextstyle_notification(token);
        super.onTokenRefresh();
    }

    public void bigtextstyle_notification(String token) {
        NotificationManager mngr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder bld = new NotificationCompat.Builder(this);
        bld.setSmallIcon(R.drawable.ic_launcher_background);
        bld.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background));
        bld.setContentTitle("Thank you for installing our app");
        bld.setStyle(new NotificationCompat.BigTextStyle().bigText(token));
        bld.setContentText("Expand for Token");
        bld.setAutoCancel(true);
        bld.setDefaults(NotificationCompat.DEFAULT_ALL);
        bld.setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "abc", NotificationManager.IMPORTANCE_HIGH);

            if (mngr != null) {
                mngr.createNotificationChannel(channel);
                bld.setChannelId("channel_1");
            }
        }
        mngr.notify(1, bld.build());
    }
}
