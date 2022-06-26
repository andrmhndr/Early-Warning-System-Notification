package com.example.earlywarningnotificationsystem;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationService extends Service {
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    String status = null;
    boolean preparing = false;
    boolean normal = false;
    boolean waspada = false;
    boolean siaga = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = null;
            channel = new NotificationChannel("Notification Service", "Notification Service", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Log.e("service", "Notifications Service is running...!");
                        try {
                            //preparing
                            if (!preparing){
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "Notification Service");
                                builder.setContentTitle("Early Warning System")
                                        .setContentText("Preparing")
                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                        .setAutoCancel(true);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationService.this);
//                                                managerCompat.notify(1, builder.build());
//                                                manager.notify(1, builder.build());
                                    startForeground(1, builder.build());
                                }
                                preparing = true;
                            }
                            //after preparing
                            else{
                                ref.child("data").child("status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()){
                                            status = String.valueOf(task.getResult().getValue());
                                            // berbahaya
                                            if (status.equals("Awas")){
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "Notification Service");
                                                builder.setContentTitle("Early Warning System")
                                                        .setContentText("Awas")
                                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                                        .setAutoCancel(true);
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationService.this);
//                                                managerCompat.notify(1, builder.build());
//                                                manager.notify(1, builder.build())
                                                startForeground(1, builder.build());
                                                }
                                                normal = false;
                                                waspada = false;
                                                siaga = false;
                                            }else{
                                                Log.e("Firebase", status);
                                                // normal
                                                if (status.equals("Normal") && normal == false){
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "Notification Service");
                                                    builder.setContentTitle("Early Warning System")
                                                            .setContentText("Normal")
                                                            .setSmallIcon(R.drawable.ic_launcher_background)
                                                            .setAutoCancel(true);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        startForeground(1, builder.build());
                                                    }
                                                    normal = true;
                                                    waspada = false;
                                                    siaga = false;
                                                // waspada
                                                } else if (status.equals("Waspada") && waspada == false) {
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "Notification Service");
                                                    builder.setContentTitle("Early Warning System")
                                                            .setContentText("Waspada")
                                                            .setSmallIcon(R.drawable.ic_launcher_background)
                                                            .setAutoCancel(true);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        startForeground(1, builder.build());
                                                    }
                                                    normal = false;
                                                    waspada = true;
                                                    siaga = false;
                                                // siaga
                                                } else if (status.equals("Siaga") && siaga == false){
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "Notification Service");
                                                    builder.setContentTitle("Early Warning System")
                                                            .setContentText("Siaga")
                                                            .setSmallIcon(R.drawable.ic_launcher_background)
                                                            .setAutoCancel(true);
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        startForeground(1, builder.build());
                                                    }
                                                    normal = false;
                                                    waspada = false;
                                                    siaga = true;
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                            Thread.sleep(2000);
                        }catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        ).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}