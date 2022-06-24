package com.example.NoteMe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

public class MyService extends Service {
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL = "Thong bao";

    public MyService() {
    }
    private FirebaseDatabase database;
    DatabaseReference ref;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
        ref = database.getReference("users");


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sendNotification("Thông báo","Có ghi chú mới");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendNotification("Thông báo","Khởi tạo service thành công!");
        Toast.makeText(this.getApplicationContext(), "Khởi tạo sevice", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this.getApplicationContext(), "Đóng service", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void  sendNotification(String tieude, String noidung) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MyService.this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Kênh 1";
            String description = "Mô tả thông báo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_notifications_active_24);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(MyService.this,
                NOTIFICATION_CHANNEL)
                .setContentTitle(""+ tieude +"")
                .setContentText(""+ noidung +"")
                .setSmallIcon(R.drawable.note)
                .setLargeIcon(bitmap)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }

    }
}