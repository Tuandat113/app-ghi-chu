package com.example.NoteMe;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class LottieActivyti extends AppCompatActivity {
    protected static final int RESULT_SPEECH = 1;
    private static final int NOTIFICATION_ID = 2;
    private static final int REQUEST_IMAGE_OPEN = 3;
    private static final String NOTIFICATION_CHANNEL = "Thong bao";
    private RecyclerView rcvUser;
    private UserAdapter userAdapter;
    private FirebaseDatabase database;
    DatabaseReference ref;
    ImageView anhavt;
    List<User> list;
    private Context context;
    private DatePickerDialog datePickerDialog;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    EditText timkiem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie_activyti);


        ImageButton mic;

        timkiem = (EditText) findViewById(R.id.timkiem);
        mic = (ImageButton) findViewById(R.id.mic);

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View  view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    timkiem.setText("");
                }catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        timkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               if (s.toString()!= null ) {
                   list.clear();
                   getLisviewDatabasefirebase(s.toString());
               }else {
                   list.clear();
                   getLisviewDatabasefirebase("");
               }
            }
        });








        rcvUser = findViewById(R.id.listuser);
        userAdapter = new UserAdapter();
        getSupportActionBar().setTitle("Danh sách ghi chú");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUser.setLayoutManager(linearLayoutManager);
        getLisviewDatabasefirebase("");
        list = new ArrayList<>();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        userAdapter.setData(list, new UserAdapter.updateclick() {
            @Override
            public void onClickUpdate(User user) {
                openDialogUpdate(user);
            }

            @Override
            public void onClickDelete(User user) {
                onClickDeleteData(user);
            }
        });
        rcvUser.setAdapter(userAdapter);
    }





    private void onClickDeleteData(User user) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(LottieActivyti.this);
        aBuilder.setMessage("Bạn có chắc chắn xóa");
        aBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
                ref = database.getReference("users");
                ref.child(user.getId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable  DatabaseError error, @NonNull  DatabaseReference ref) {
                        TextView tieude;
                        Button themltc;
                        Dialog dialoga = new Dialog(LottieActivyti.this);
                        dialoga.setContentView(R.layout.daketban);
                        dialoga.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                        tieude = dialoga.findViewById(R.id.txttieude);
                        tieude.setText("Đã xóa ghi chú");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialoga.dismiss();
                            }
                        },2000);
                        dialoga.show();
                    }
                });
            }
    });
        aBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        aBuilder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> text =data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    timkiem.setText(text.get(0));
                }
                break;


        }

        if(requestCode == REQUEST_IMAGE_OPEN && resultCode == RESULT_OK) {
            Uri full = data.getData();
            anhavt.setImageURI(full);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openDialogUpdate(User user) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_dialog);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnDangKi, btnHuyBo;
        EditText etUserName, phone, timkiem;
        TextView date;
        ImageButton mic;


        btnDangKi = dialog.findViewById(R.id.btnRegup);
        btnHuyBo = dialog.findViewById(R.id.btnRelayup);
        etUserName = dialog.findViewById(R.id.edtRegUserup);
        date = dialog.findViewById(R.id.dangKi_etEmailup);
        phone = dialog.findViewById(R.id.edtRegPasswordup);
        anhavt = dialog.findViewById(R.id.anhdaidien);







        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DAY_OF_MONTH);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(dialog.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        date.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, nam,thang,ngay);
                datePickerDialog.show();
            }
        });


        anhavt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_IMAGE_OPEN);
            }
        });


        etUserName.setText(user.getName());
        date.setText(user.getNgaysinh());
        phone.setText(user.getNd());
        String linkHanhDB = user.getLinkhinhanh();
        Picasso.get().load(linkHanhDB).into(anhavt);


        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://lap02listvewfirebase.appspot.com");
                Calendar calendar = Calendar.getInstance();
                final StorageReference storageRef = storage.getReference();
                StorageReference mountainsRef = storageRef.child("image"+ calendar.getTimeInMillis() +".png");

                // Get the data from an ImageView as bytes
                anhavt.setDrawingCacheEnabled(true);
                anhavt.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) anhavt.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(LottieActivyti.this, "Lỗi!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
                                ref = database.getReference("users");
                                String newName = etUserName.getText().toString().trim();
                                String newdate = date.getText().toString().trim();
                                String newphone = phone.getText().toString().trim();
                                Boolean checkError = true;
                                if (newName.isEmpty()) {
                                    etUserName.setError("Tiêu đề không được bỏ trống!");
                                    checkError = false;
                                }
                                if (newdate.isEmpty()) {
                                    date.setError("Ngày không được bỏ trống!");
                                    checkError = false;
                                }

                                if (newphone.isEmpty()) {
                                    phone.setError("Nội Dung không được bỏ trống!");
                                    checkError = false;
                                }

                                if(checkError){
                                    user.setName(newName);
                                    user.setNgaysinh(newdate);
                                    user.setNd(newphone);
                                    user.setLinkhinhanh(uri.toString().trim());
                                    ref.child(user.getId()).updateChildren(user.toMap(), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            TextView tieude;
                                            sendNotification();
                                            Dialog dialoga = new Dialog(v.getContext());
                                            dialoga.setContentView(R.layout.daketban);
                                            dialoga.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                                            tieude = dialoga.findViewById(R.id.txttieude);
                                            tieude.setText("Đã xửa ghi chú");
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialoga.dismiss();
                                                }
                                            }, 2500);
                                            dialog.dismiss();
                                            dialoga.show();

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



            }
        });


        btnHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.chiase:
                startActivity(new Intent(LottieActivyti.this, DangKiActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void getLisviewDatabasefirebase(String keyword) {
        database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
        ref = database.getReference("users");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull   DataSnapshot snapshot, @Nullable  String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getName().contains(keyword)) {
                        list.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull   DataSnapshot snapshot, @Nullable   String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user == null || list == null || list.isEmpty()) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (user.getId() == list.get(i).getId()) {
                        list.set(i, user);
                        break;
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull  DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user == null || list == null || list.isEmpty()) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (user.getId() == list.get(i).getId()) {
                        list.remove(list.get(i));
                        break;
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull   DataSnapshot snapshot, @Nullable   String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull   DatabaseError error) {

            }
        });


    }


    private void  sendNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(LottieActivyti.this);

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
        NotificationCompat.Builder notification = new NotificationCompat.Builder(LottieActivyti.this,
                NOTIFICATION_CHANNEL)
                .setContentTitle("Đăng ký thành công!")
                .setContentText("Chào mừng bạn mới, chúc bạn có một ngày vui vẻ!")
                .setSmallIcon(R.drawable.note)
                .setLargeIcon(bitmap)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification.build());
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
    }



}