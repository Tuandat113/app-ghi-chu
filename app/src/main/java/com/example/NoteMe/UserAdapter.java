package com.example.NoteMe;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
   private List<User> listUser;
   private CommentApdapter commentAdapter;
   private List<Comment> listCommnet;
   private ArrayList<String> tieude;
   private Context context;
   private updateclick mUpdateclick;
   public UserAdapter(){};
    private FirebaseDatabase database, data;
    DatabaseReference ref, reff;
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://lap02listvewfirebase.appspot.com");


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public interface updateclick {
        void onClickUpdate(User user);
        void onClickDelete(User user);
   }
   public void setData(List<User> list, updateclick mUpdateclick) {
       this.listUser = list;
       this.mUpdateclick = mUpdateclick;
       notifyDataSetChanged();
   }

   public UserAdapter(Context context, List<User> listUser){
       this.context = context;
       this.listUser = listUser;

   }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_chat, viewGroup, false);
       return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        User user = listUser.get(i);
        if (user == null) {
            return;
        }
        userViewHolder.ten.setText(user.getName());
        userViewHolder.sdt.setText("Nội dung: "+ user.getNd());
        userViewHolder.ngaysinh.setText("Date: "+ user.getNgaysinh());
        String linkHanhDB = user.getLinkhinhanh();
        Picasso.get().load(linkHanhDB).into(userViewHolder.imgavt);
        userViewHolder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateclick.onClickUpdate(user);
            }
        });
        userViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUpdateclick.onClickDelete(user);
            }
        });
        setAnimation(userViewHolder.itemView, i);

        userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton thoat, them;
                TextView tieude;
                ImageView anhtieude;
                RecyclerView recyclerView;
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.activity_chitiet);
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(false);

                them = dialog.findViewById(R.id.chitietthem);
                thoat = dialog.findViewById(R.id.chitietthoat);
                tieude = dialog.findViewById(R.id.chitieude);
                anhtieude = dialog.findViewById(R.id.anhtieude);
                recyclerView = dialog.findViewById(R.id.listtieude);

                commentAdapter = new CommentApdapter();
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
                ref = database.getReference("users").child(listUser.get(i).getId()).child("Comment");
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Comment comment = snapshot.getValue(Comment.class);
                        if (comment != null) {



                            listCommnet.add(comment);


                        }
                        commentAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        Comment comment = snapshot.getValue(Comment.class);
                        if (comment == null || comment == null || listCommnet.isEmpty()) {
                            return;
                        }
                        for (int i = 0; i < listCommnet.size(); i++) {
                            if (comment.getId() == listCommnet.get(i).getId()) {
                                listCommnet.remove(listCommnet.get(i));

                                break;
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                data = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
                reff = data.getReference("users").child(listUser.get(i).getId()).child("Comment");

                listCommnet = new ArrayList<>();
                commentAdapter.setData(listCommnet, new CommentApdapter.updateclick() {
                    @Override
                    public void onClickDelete(Comment comment) {
                        AlertDialog.Builder aBuilder = new AlertDialog.Builder(v.getContext());
                        aBuilder.setMessage("Bạn có chắc chắn xóa");
                        aBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {


                                reff.child(comment.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable  DatabaseError error, @NonNull  DatabaseReference ref) {
                                        TextView tieude;
                                        Button themltc;
                                        Dialog dialoga = new Dialog(v.getContext());
                                        dialoga.setContentView(R.layout.daketban);
                                        dialoga.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                                        tieude = dialoga.findViewById(R.id.txttieude);
                                        tieude.setText("Đã xóa dữ liệu thành công ");
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialoga.dismiss();
                                            }
                                        },0);
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
                });
                recyclerView.setAdapter(commentAdapter);




                String tentieude = listUser.get(i).getName();
                tieude.setText(tentieude);
                String hinhanh = listUser.get(i).getLinkhinhanh();
                Picasso.get().load(hinhanh).into(anhtieude);
                thoat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                them.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText tencm, ndcm;
                        Button themcm, thoatcm;


                        final Dialog dialog = new Dialog(v.getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.comment);
                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);


                        tencm = dialog.findViewById(R.id.cmedtRegUser);
                        ndcm = dialog.findViewById(R.id.cmedtRegPassword);
                        themcm = dialog.findViewById(R.id.cmbtnReg);



                        thoatcm = dialog.findViewById(R.id.cmbtnRelay);




                        thoatcm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        themcm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Calendar calendar = Calendar.getInstance();
                                final StorageReference storageRef = storage.getReference();
                                StorageReference mountainsRef = storageRef.child("image"+ calendar.getTimeInMillis() +".png");

                                // Get the data from an ImageView as bytes


                                                String userName = tencm.getText().toString().trim();
                                                String pass = ndcm.getText().toString().trim();


                                                Boolean checkError = true;
                                                if(userName.isEmpty()){
                                                    tencm.setError("Tiêu đề không được bỏ trống!");
                                                    checkError = false;
                                                }


                                                if(pass.isEmpty()){
                                                    ndcm.setError("Nội dung không được bỏ trống!");
                                                    checkError = false;
                                                }
                                                if(checkError) {

                                                    Toast.makeText(v.getContext(), "" +listUser.get(i).getId(), Toast.LENGTH_SHORT).show();
                                                    Comment comment = new Comment();
                                                    comment.setName(userName);
                                                    comment.setNd(pass);
                                                    comment.setId("" + calendar.getTimeInMillis());
                                                    database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
                                                    ref = database.getReference("users").child(listUser.get(i).getId()).child("Comment");
                                                    ref.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    ref.child(""+calendar.getTimeInMillis()).setValue(comment);

                                                                    dialog.dismiss();                                                                }
                                                            }, 0);
                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                                }


                            }
                        });





                        dialog.show();
                    }
                });







                dialog.show();
            }
        });



    }






    private void setAnimation(View itemView, int i) {
           Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.zoom);
           itemView.setAnimation(animation);

   }


//    private void getLisviewDatabasefirebase() {
//        for (int i = 0; i <= listUser.size(); i++) {
//            database = FirebaseDatabase.getInstance("https://lap02listvewfirebase-default-rtdb.firebaseio.com/");
//            ref = database.getReference("users").child(listUser.get(i).getId()).child("Comment");
//            ref.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                    Comment comment = snapshot.getValue(Comment.class);
//                    if (comment != null) {
//                        listCommnet.add(comment);
//                        commentAdapter.notifyDataSetChanged();
//                    }
//
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                    Comment comment = snapshot.getValue(Comment.class);
//                    if (comment == null || comment == null || listCommnet.isEmpty()) {
//                        return;
//                    }
//                    for (int i = 0; i < listCommnet.size(); i++) {
//                        if (comment.getId() == listCommnet.get(i).getId()) {
//                            listCommnet.remove(listCommnet.get(i));
//                            break;
//                        }
//                    }
//                    commentAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//
//        }
//    }

    @Override
    public int getItemCount() {
       if (listUser != null) {
           return listUser.size();
       }
        return 0;
    }

    public class  UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgavt, update, delete;
        private TextView ten, ngaysinh, sdt;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgavt = itemView.findViewById(R.id.avataruser);
            ten = itemView.findViewById(R.id.nameuser);
            ngaysinh = itemView.findViewById(R.id.tuoi);
            sdt = itemView.findViewById(R.id.sophone);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
