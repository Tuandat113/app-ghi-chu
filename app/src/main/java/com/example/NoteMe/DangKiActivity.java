package com.example.NoteMe;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class DangKiActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL = "Thong bao";
    private FirebaseDatabase database;
    DatabaseReference ref;
    Button btnDangKi, btnHuyBo;
    EditText etUserName, phone;
    TextView date;
    private ImageView imgImageView;
    private static final int REQUEST_IMAGE_OPEN = 1;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://lap02listvewfirebase.appspot.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dangky);
        getSupportActionBar().hide();
        mappingView();
        dangKi();

        imgImageView = (ImageView) findViewById(R.id.imgavatar);
        imgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_IMAGE_OPEN);
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chonngay();
            }
        });
        btnHuyBo.setOnClickListener(v -> {
            startActivity(new Intent(DangKiActivity.this, LottieActivyti.class));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK) {
            Uri full = data.getData();
            ImageView imgv = findViewById(R.id.imgavatar);
            imgv.setImageURI(full);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void dangKi() {

        btnDangKi.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            final StorageReference storageRef = storage.getReference();
            StorageReference mountainsRef = storageRef.child("image"+ calendar.getTimeInMillis() +".png");

                // Get the data from an ImageView as bytes
                imgImageView.setDrawingCacheEnabled(true);
                imgImageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(DangKiActivity.this, "Lỗi!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String userName = etUserName.getText().toString().trim();
                                String pass = phone.getText().toString().trim();
                                String email = date.getText().toString().trim();

                                Boolean checkError = true;
                                if(userName.isEmpty()){
                                    etUserName.setError("Tiêu đề không được bỏ trống!");
                                    checkError = false;
                                }
                                if(email.isEmpty()){
                                    date.setError("Ngày không được bỏ trống!");
                                    checkError = false;
                                }

                                if(pass.isEmpty()){
                                    phone.setError("Nội dung không được bỏ trống!");
                                    checkError = false;
                                }
                                if(checkError) {

                                    User user = new User();
                                    user.setName(userName);
                                    user.setNd(pass);
                                    user.setLinkhinhanh("");
                                    user.setNgaysinh(email);
                                    user.setId("" + calendar.getTimeInMillis());
                                    user.setLinkhinhanh(uri.toString().trim());
                                    database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
                                    ref = database.getReference("users");
                                    ref.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ref.child("" + calendar.getTimeInMillis()).setValue(user);
                                                        sendNotification();
                                                        startActivity(new Intent(DangKiActivity.this, LottieActivyti.class));
                                                        finishAffinity();
                                                    }
                                                }, 0);
                                            }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                });




        });
    }

    private void  sendNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(DangKiActivity.this);

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


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.note);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(DangKiActivity.this,
                NOTIFICATION_CHANNEL)
                .setContentTitle("Thêm ghi chú mới")
                .setContentText("Chào bạn!")
                .setSmallIcon(R.drawable.note)
                .setLargeIcon(bitmap)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }

    }

    private void chonngay() {
        Calendar calendar = Calendar.getInstance();
        int ngay = calendar.get(Calendar.DAY_OF_MONTH);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                date.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, nam,thang,ngay);
        datePickerDialog.show();
    }

    private void mappingView() {
        btnDangKi = findViewById(R.id.btnReg);
        btnHuyBo = findViewById(R.id.btnRelay);
        etUserName = findViewById(R.id.edtRegUser);
        date = findViewById(R.id.dangKi_etEmail);
        phone = findViewById(R.id.edtRegPassword);
    }

}
